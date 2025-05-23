/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.geometry;

import codex.mage.VfxGlobals;
import codex.mage.mesh.MeshPrototype;
import codex.mage.particles.ParticleData;
import codex.mage.particles.ParticleGroup;
import codex.mage.resources.ResourceFactory;
import codex.mage.utils.MeshUtils;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author codex
 */
public class TriParticleGeometry extends ParticleGeometry<ParticleData> {
    
    private final MeshPrototype prototype;
    private int size = -1;
    
    /**
     * Creates a geometry displaying the particle group with the mesh prototype.
     * 
     * @param group
     * @param prototype 
     */
    public TriParticleGeometry(ParticleGroup group, MeshPrototype prototype) {
        super(group);
        this.prototype = prototype;
        setCullHint(Spatial.CullHint.Never);
        setMaterial(VfxGlobals.getInstance().createResource(ResourceFactory.TRI_PARTICLE_MAT, Material.class));
    }
        
    @Override
    protected void initBuffers() {
        
        // Position buffer, stores the origin of the particle for each vertex.
        FloatBuffer pb = BufferUtils.createVector3Buffer(capacity * prototype.getNumVerts());
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.Position, VertexBuffer.Usage.Stream, VertexBuffer.Format.Float, pb, 3);
        
        // Local position buffer
        FloatBuffer pb2 = BufferUtils.createVector3Buffer(capacity * prototype.getNumVerts());
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.TexCoord2, VertexBuffer.Usage.Stream, VertexBuffer.Format.Float, pb2, 2);
        
        // Main texture coordinate buffer, does not change.
        FloatBuffer tb = BufferUtils.createVector2Buffer(capacity * prototype.getNumVerts());
        for (int i = 0; i < capacity; i++) {
            for (Vector2f c : prototype.getTexCoords()) {
                MeshUtils.writeVector2(tb, c);
            }
        }
        tb.flip();
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.TexCoord, VertexBuffer.Usage.Static, VertexBuffer.Format.Float, tb, 2);
        
        // sprite factions buffer
        ShortBuffer sib = BufferUtils.createShortBuffer(capacity * prototype.getNumVerts());
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.TexCoord3, VertexBuffer.Usage.Stream, VertexBuffer.Format.UnsignedShort, sib, 1);
        
        // Color buffer, including alpha.
        FloatBuffer cb = BufferUtils.createFloatBuffer(capacity * prototype.getNumVerts() * 4);
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.Color, VertexBuffer.Usage.Stream, VertexBuffer.Format.Float, cb, 4);
        
        // Index buffer, changes as the number of particles changes -> dynamic usage
        IntBuffer ib = BufferUtils.createIntBuffer(capacity * prototype.getNumIndices());
        for (int i = 0, vi = 0; i < capacity; i++) {
            for (int j : prototype.getIndices()) {
                ib.put(vi+j);
            }
            vi += prototype.getNumVerts();
        }
        ib.position(0);
        ib.limit(group.size() * prototype.getNumIndices());
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.Index, VertexBuffer.Usage.Dynamic, VertexBuffer.Format.UnsignedInt, ib, 3);
        
        mesh.updateCounts();
        
    }
    @Override
    protected void updateMesh() {
        VertexBuffer pBuf = mesh.getBuffer(VertexBuffer.Type.Position);
        FloatBuffer positions = (FloatBuffer)pBuf.getData();        
        VertexBuffer lBuf = mesh.getBuffer(VertexBuffer.Type.TexCoord2);
        FloatBuffer localPos = (FloatBuffer)lBuf.getData();
        VertexBuffer siBuf = mesh.getBuffer(VertexBuffer.Type.TexCoord3);
        ShortBuffer spriteIndex = (ShortBuffer)siBuf.getData();
        VertexBuffer cBuf = mesh.getBuffer(VertexBuffer.Type.Color);
        FloatBuffer colors = (FloatBuffer)cBuf.getData();
        positions.clear();
        localPos.clear();
        colors.clear();
        if (useSpriteSheet.get()) {
            spriteIndex.clear();
        }
        Vector3f vec = new Vector3f();
        Quaternion q = new Quaternion();
        for (ParticleData p : group) {
            q.fromAngles(0f, 0f, p.angle.get());
            for (Vector3f v : prototype.getVerts()) {
                q.mult(v, vec).multLocal(p.size.get()).multLocal(p.getScale());
                MeshUtils.writeVector3(positions, p.getPosition());
                localPos.put(vec.x).put(vec.y);
                MeshUtils.writeColor(colors, p.color.get());
                if (useSpriteSheet.get()) {
                    spriteIndex.put(p.spriteIndex.get().shortValue());
                }
            }
        }
        positions.flip();
        localPos.flip();
        colors.flip();
        pBuf.updateData(positions);
        lBuf.updateData(localPos);
        cBuf.updateData(colors);
        if (useSpriteSheet.get()) {
            spriteIndex.flip();
            siBuf.updateData(spriteIndex);
        }
        if (size != group.size()) {
            VertexBuffer iBuf = mesh.getBuffer(VertexBuffer.Type.Index);
            Buffer index = iBuf.getData();
            index.position(0);
            index.limit(group.size() * prototype.getNumIndices());
            iBuf.updateData(index);
            size = group.size();
            mesh.updateCounts();
        }
    }
    
}
