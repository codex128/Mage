/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.geometry;

import codex.mage.particles.Particle;
import codex.mage.particles.ParticleGroup;
import codex.mage.tweens.Value;
import com.jme3.bounding.BoundingSphere;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.simsilica.lemur.core.VersionedReference;
import java.util.Objects;

/**
 *
 * @author codex
 * @param <T>
 */
public abstract class ParticleGeometry <T extends Particle> extends Geometry {
    
    public static final BoundingSphere INFINITE_BOUND = new BoundingSphere(Float.POSITIVE_INFINITY, Vector3f.ZERO);
    
    protected ParticleGroup<T> group;
    protected int capacity = -1;
    protected Value<Boolean> useSpriteSheet = Value.value(false);
    private Value<Boolean> forceMeshUpdate = Value.value(false);
    private Value<Boolean> worldSpace = Value.value(true);
    private VersionedReference<Boolean> worldSpaceRef;
    
    public ParticleGeometry(ParticleGroup<T> group) {
        super();
        this.group = group;
        setMesh(new Mesh());
        worldSpaceRef = worldSpace.createReference();
        worldSpaceRef.update();
        setIgnoreTransform(worldSpace.get());
    }
    
    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);
        if (worldSpaceRef.update()) {
            setIgnoreTransform(worldSpaceRef.get());
        }
        if (group.capacity().get() > 0) {
            if (checkBuffers()) {
                capacity = group.capacity().get();
                initBuffers();
            }
            if (forceMeshUpdate.get() || group.getWorldPlayState()) {
                updateMesh();
            }
        } else if (capacity > 0) {
            capacity = 0;
        }
    }
    @Override
    public void updateWorldBound() {
        super.updateWorldBound();
        worldBound = INFINITE_BOUND;
    }
    
    protected abstract void initBuffers();
    protected abstract void updateMesh();
    protected boolean checkBuffers() {
        return capacity < 0 || capacity != group.capacity().get();
    }
    
    /**
     * Sets the particle group used to generate the particle mesh.
     * 
     * @param group 
     */
    public void setParticleGroup(ParticleGroup<T> group) {
        Objects.requireNonNull(group, "Rendered particle group cannot be null");
        if (this.group != group) {
            this.group = group;
            // force buffers to reinitialize
            capacity = -1;
        }
    }
    
    /**
     * Forces this geometry to always update its mesh.
     * <p>
     * If the particle group is paused, the mesh would normally not be
     * updated, since all particles are assumed to be static. If, for some
     * reason, the particles are not static in this state, setting this to
     * true will force this geometry to always update the mesh.
     * <p>
     * default=false
     * 
     * @param forceMeshUpdate true to force this geometry to always update the mesh
     * @see ParticleGroup#play()
     * @see ParticleGroup#pause()
     */
    public void setForceMeshUpdate(Value<Boolean> forceMeshUpdate) {
        this.forceMeshUpdate = forceMeshUpdate;
    }
    
    /**
     * Enables writing to a sprite index buffer.
     * <p>
     * This must be enabled to support sprite sheets. Otherwise geometries
     * will not write particle sprite indices to their meshes.
     * <p>
     * default=false
     * 
     * @param enable 
     */
    public void enableSpriteSheet(Value<Boolean> enable) {
        useSpriteSheet = enable;
    }
    
    /**
     * Sets particles to be rendered in world space, as opposed to local space.
     * <p>
     * Particles rendered in local space are local to this geometry's transform.
     * <p>
     * default=true
     * 
     * @param use 
     */
    public void setUseWorldSpace(Value<Boolean> use) {
        worldSpace = use;
        worldSpaceRef = worldSpace.createReference();
        worldSpaceRef.update();
        setIgnoreTransform(worldSpace.get());
    }
    
    public ParticleGroup<T> getParticleGroup() {
        return group;
    }
    
    public Value<Boolean> isForceMeshUpdate() {
        return forceMeshUpdate;
    }
    
    public Value<Boolean> isSpriteSheetEnabled() {
        return useSpriteSheet;
    }
    
    public Value<Boolean> isWorldSpace() {
        return worldSpace;
    }
    
}
