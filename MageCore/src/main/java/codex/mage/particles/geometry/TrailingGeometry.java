/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.geometry;

import codex.mage.VfxGlobals;
import codex.mage.particles.ParticleData;
import codex.mage.particles.ParticleGroup;
import codex.mage.resources.ResourceFactory;
import codex.mage.utils.MeshUtils;
import codex.mage.utils.VfxUtils;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Displays particles as a trail.
 * 
 * @author codex
 */
public class TrailingGeometry extends ParticleGeometry<ParticleData> {
     
    private int size = -1;
    private boolean faceCamera = true;

    public TrailingGeometry(ParticleGroup group) {
        super(group);
        super.setMaterial(VfxGlobals.getInstance().createResource(ResourceFactory.TRAIL_PARTICLE_MAT, Material.class));
        material.setBoolean("FaceCamera", faceCamera);
    }
    
    @Override
    public void setMaterial(Material mat) {
        super.setMaterial(mat);
        if (material != null) {
            material.setBoolean("FaceCamera", faceCamera);
        }
    }    
    @Override
    protected void initBuffers() {
        
        // position buffer
        FloatBuffer pb = BufferUtils.createVector3Buffer(capacity * 2);
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.Position, VertexBuffer.Usage.Stream, VertexBuffer.Format.Float, pb, 3);
        
        // color buffer
        ByteBuffer cb = BufferUtils.createByteBuffer(capacity * 2 * 4);
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.Color, VertexBuffer.Usage.Stream, VertexBuffer.Format.UnsignedByte, cb, 4);
        
        // buffer for storing rotational axis used for hardware normals
        FloatBuffer ab = BufferUtils.createFloatBuffer(capacity * 2 * 4);
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.TexCoord3, VertexBuffer.Usage.Stream, VertexBuffer.Format.Float, ab, 4);
        
        // buffer for general vertex info
        FloatBuffer tb2 = BufferUtils.createFloatBuffer(capacity * 2);
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.TexCoord2, VertexBuffer.Usage.Stream, VertexBuffer.Format.Float, tb2, 1);
        
        // main buffer for texture coordinates
        FloatBuffer tb = BufferUtils.createVector2Buffer(capacity * 2);
        float uvx = 0, incrUv = 1f/capacity;
        for (int i = 0; i < capacity; i++) {
            tb.put(uvx).put(0f)
              .put(uvx).put(1f);
            uvx += incrUv;
        }
        tb.flip();
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.TexCoord, VertexBuffer.Usage.Static, VertexBuffer.Format.Float, tb, 2);
        
        // index buffer
        IntBuffer ib = BufferUtils.createIntBuffer((capacity-1) * 6);
        for (int i = 0, j = 0; i < capacity-1; i++) {
            MeshUtils.writeTriangle(ib, j  , j+1, j+2);
            MeshUtils.writeTriangle(ib, j+3, j+2, j+1);
            j += 2;
        }
        ib.flip();
        MeshUtils.initializeVertexBuffer(mesh, VertexBuffer.Type.Index, VertexBuffer.Usage.Dynamic, VertexBuffer.Format.UnsignedInt, ib, 3);
        
    }
    @Override
    protected void updateMesh() {
        
        VertexBuffer pvb = mesh.getBuffer(VertexBuffer.Type.Position);
        FloatBuffer positions = (FloatBuffer)pvb.getData();        
        VertexBuffer cvb = mesh.getBuffer(VertexBuffer.Type.Color);
        ByteBuffer colors = (ByteBuffer)cvb.getData();        
        VertexBuffer tvb = mesh.getBuffer(VertexBuffer.Type.TexCoord2);
        FloatBuffer info = (FloatBuffer)tvb.getData();        
        VertexBuffer avb = mesh.getBuffer(VertexBuffer.Type.TexCoord3);
        FloatBuffer axis = (FloatBuffer)avb.getData();
        
        // initialize buffers to be written (does not actually clear the buffer)
        positions.clear();
        colors.clear();
        info.clear();
        axis.clear();
        
        float life = 0;
        float lifeIncr = 1f/group.size();
        for (int i = 0; group.size() >= 2 && i < group.size(); i++) {
            ParticleData data = group.get(i);
            // configure the rotation axis
            Vector3f rotAxis;
            if (i+1 >= group.size()) {
                // axis away from previous position
                rotAxis = group.get(i-1).getPosition().subtract(data.getPosition()).normalizeLocal().negateLocal();
            } else if (i == 0) {
                // axis towards next position
                rotAxis = group.get(i+1).getPosition().subtract(data.getPosition()).normalizeLocal();
            } else {
                // axis as average between direction to next and previous (negated)
                Vector3f toNext = group.get(i+1).getPosition().subtract(data.getPosition()).normalizeLocal();
                Vector3f toPrev = group.get(i-1).getPosition().subtract(data.getPosition()).normalizeLocal();
                rotAxis = toNext.add(toPrev.negateLocal()).normalizeLocal();
            }
            if (faceCamera) {
                // hardware normals
                MeshUtils.writeVector3(positions, data.getPosition());
                MeshUtils.writeVector3(positions, data.getPosition());
                MeshUtils.writeVector4(axis, buildVector4f(rotAxis, data.size.get()));
                MeshUtils.writeVector4(axis, buildVector4f(rotAxis, -data.size.get()));
            } else {
                // software normals
                Vector3f across = rotAxis.cross(data.getRotation().mult(Vector3f.UNIT_Z))
                        .normalizeLocal().multLocal(data.size.get()*VfxUtils.vectorAverage(data.getScale()));
                Vector3f neg = across.negate().addLocal(data.getPosition());
                across.addLocal(data.getPosition());
                MeshUtils.writeVector3(positions, across);
                MeshUtils.writeVector3(positions, neg);
            }
            // life gradient
            info.put(life).put(life);
            life += lifeIncr;
            // color
            int abgr = data.color.get().asIntABGR();
            colors.putInt(abgr);
            colors.putInt(abgr);
        }
        
        // reset position and limit
        positions.flip();
        colors.flip();
        info.flip();
        axis.flip();
        
        // update vertex buffers
        pvb.updateData(positions);
        cvb.updateData(colors);
        tvb.updateData(info);
        avb.updateData(axis);
        
        // update indices when size changes
        if (group.size() != size) {        
            VertexBuffer ivb = mesh.getBuffer(VertexBuffer.Type.Index);
            Buffer index = ivb.getData();
            index.position(0);
            index.limit(Math.max(group.size()-1, 0) * 6);
            ivb.updateData(index);
            size = group.size();
        }
        
        mesh.updateCounts();
        mesh.updateBound();
        
    }
    
    private Vector4f buildVector4f(Vector3f vec, float w) {
        return new Vector4f(vec.x, vec.y, vec.z, w);
    }
    
    /**
     * Sets the mesh to face the camera.
     * 
     * @param faceCamera 
     */
    public void setFaceCamera(boolean faceCamera) {
        this.faceCamera = faceCamera;
        if (material != null) {
            material.setBoolean("FaceCamera", faceCamera);
        }
    }    
    public boolean isFaceCamera() {
        return faceCamera;
    }
    
}
