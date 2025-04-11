/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.drivers;

import codex.mage.particles.Particle;
import codex.mage.particles.ParticleData;
import codex.mage.particles.ParticleGroup;
import codex.mage.particles.emission.ParticleFactory;
import codex.mage.tweens.Value;
import codex.mage.utils.VfxUtils;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 * 
 * 
 * @author codex
 * @param <T> type of particle
 */
public interface ParticleDriver <T extends Particle> {
    
    /**
     * Performs an update on the particle group this is driving.
     * 
     * @param group
     * @param tpf 
     */
    public void updateGroup(ParticleGroup<T> group, float tpf);
    
    /**
     * Performs an update on a particle belonging to the particle group being driven.
     * 
     * @param particle
     * @param tpf 
     */
    public void updateParticle(T particle, float tpf);
    
    /**
     * Performs operations on a newly added particle.
     * 
     * @param group
     * @param particle 
     */
    public void particleAdded(ParticleGroup<T> group, T particle);
    
    /**
     * Called when the particle group's simulation is reset.
     * 
     * @param group 
     */
    public void groupReset(ParticleGroup<T> group);
    
    /**
     * Called when a particle is removed from the driven group.
     * 
     * @param group
     * @param particle 
     */
    public default void particleRemoved(ParticleGroup<T> group, T particle) {}
    
    /**
     * Called when this driver is assigned to a group.
     * 
     * @param group 
     */
    public default void assignedToGroup(ParticleGroup<T> group) {}
    
    /**
     * Called when this driver is removed from a group.
     * 
     * @param group 
     */
    public default void removedFromGroup(ParticleGroup<T> group) {}
    
    
    /**********************
     *   Common Drivers   *
     **********************/
    
    
    /**
     * Updates all {@link Value} objects belong to each particle according to
     * the percentage of life remaining on each particle.
     */
    public static final ParticleDriver ValueUpdateOverLifetime = new ParticleDriver<Particle>() {
        @Override
        public void updateGroup(ParticleGroup group, float tpf) {}
        @Override
        public void updateParticle(Particle particle, float tpf) {
            particle.updateValues(particle.getLifePercent());
        }
        @Override
        public void particleAdded(ParticleGroup group, Particle particle) {}
        @Override
        public void groupReset(ParticleGroup group) {}
    };
    
    /**
     * Updates position based on linearVelocity.
     */
    public static final ParticleDriver Position = new ParticleDriver<ParticleData>() {
        @Override
        public void updateGroup(ParticleGroup group, float tpf) {}
        @Override
        public void updateParticle(ParticleData particle, float tpf) {
            particle.getPosition().addLocal(particle.linearVelocity.mult(tpf));
        }
        @Override
        public void particleAdded(ParticleGroup group, ParticleData particle) {}
        @Override
        public void groupReset(ParticleGroup group) {}
    };
    
    /**
     * Updates rotation based on angularVelocity.
     */
    public static final ParticleDriver Rotation = new ParticleDriver<ParticleData>() {
        @Override
        public void updateGroup(ParticleGroup group, float tpf) {}
        @Override
        public void updateParticle(ParticleData particle, float tpf) {
            particle.getRotation().multLocal(new Quaternion().fromAngles(
                    particle.angularVelocity.x*tpf, particle.angularVelocity.y*tpf, particle.angularVelocity.z*tpf));
        }
        @Override
        public void particleAdded(ParticleGroup group, ParticleData particle) {}
        @Override
        public void groupReset(ParticleGroup group) {}
    };
    
    /**
     * Updates rotation to face velocity.
     */
    public static final ParticleDriver RotateToVelocity = new ParticleDriver<ParticleData>() {
        @Override
        public void updateGroup(ParticleGroup<ParticleData> group, float tpf) {}
        @Override
        public void updateParticle(ParticleData particle, float tpf) {
            particle.getRotation().lookAt(particle.linearVelocity, Vector3f.UNIT_Y);
        }
        @Override
        public void particleAdded(ParticleGroup<ParticleData> group, ParticleData particle) {}
        @Override
        public void groupReset(ParticleGroup<ParticleData> group) {}
    };
    
    /**
     * Updates angle based on rotation speed.
     */
    public static final ParticleDriver Angle = new ParticleDriver<ParticleData>() {
        @Override
        public void updateGroup(ParticleGroup group, float tpf) {}
        @Override
        public void updateParticle(ParticleData particle, float tpf) {
            particle.angle.set(particle.angle.get()+particle.angleSpeed.get()*tpf);
        }
        @Override
        public void particleAdded(ParticleGroup group, ParticleData particle) {}
        @Override
        public void groupReset(ParticleGroup group) {}
    };
    
    /**
     * Transforms new particles to the group's emission volume.
     */
    public static final ParticleDriver TransformToVolume = new ParticleDriver<ParticleData>() {
        @Override
        public void updateGroup(ParticleGroup group, float tpf) {}
        @Override
        public void updateParticle(ParticleData particle, float tpf) {}
        @Override
        public void particleAdded(ParticleGroup group, ParticleData particle) {
            particle.setPosition(group.getVolume().getNextPosition(group.getWorldTransform()));
        }
        @Override
        public void groupReset(ParticleGroup group) {}
    };
    
    /**
     * Returns a driver that sets the lifetime of new particles.
     * 
     * @param life
     * @return 
     */
    public static ParticleDriver life(float life) {
        return new LifeTime(life);
    }
    
    /**
     * Returns a driver that applies a constant force to each particle.
     * <p>
     * This is perfect for constant forces such as gravity.
     * 
     * @param force
     * @return 
     */
    public static ParticleDriver force(Vector3f force) {
        return new ConstantForce(force);
    }
    
    /**
     * Applies an impulse to new particles.
     * <p>
     * If the direction vector is null, then the group's world rotation will be
     * used to determine the direction of the impulse.
     * <p>
     * Warning: work in progress.
     * 
     * @param direction direction of impulse, or null for group rotation
     * @param magnitude magnitude of impulse
     * @param angle maximum offset angle
     * @return driver
     */
    public static ParticleDriver directionalImpulse(Vector3f direction, float magnitude, float angle) {
        return new DirectionalImpulse(direction, magnitude, angle);
    }
    
    public static class LifeTime extends ParticleFactory<ParticleData> {
        
        private final float life;
        
        public LifeTime(float life) {
            this.life = life;
        }
        
        @Override
        public void particleAdded(ParticleGroup<ParticleData> group, ParticleData particle) {
            particle.setLife(life);
        }
        
    }
    public static class ConstantForce implements ParticleDriver<ParticleData> {
        
        private final Vector3f force = new Vector3f();

        private ConstantForce(Vector3f force) {
            this.force.set(force);
        }

        @Override
        public void updateGroup(ParticleGroup group, float tpf) {}
        @Override
        public void updateParticle(ParticleData particle, float tpf) {
            particle.linearVelocity.addLocal(force.mult(tpf));
        }
        @Override
        public void particleAdded(ParticleGroup group, ParticleData particle) {}
        @Override
        public void groupReset(ParticleGroup group) {}
        
    }
    public static class DirectionalImpulse implements ParticleDriver<ParticleData> {
        
        private final Vector3f direction;
        private final float magnitude;
        private final float angle;
        private final Plane plane = new Plane();
        
        public DirectionalImpulse(Vector3f direction, float magnitude, float angle) {
            this.direction = direction;
            this.magnitude = magnitude;
            this.angle = angle;
        }
        
        @Override
        public void updateGroup(ParticleGroup group, float tpf) {}
        @Override
        public void updateParticle(ParticleData particle, float tpf) {}
        @Override
        public void particleAdded(ParticleGroup group, ParticleData p) {
            VfxUtils.offsetByAngle(getDirection(group).multLocal(magnitude),
                    VfxUtils.gen.nextFloat(-angle, angle), p.linearVelocity);
        }
        @Override
        public void groupReset(ParticleGroup group) {}
        
        private Vector3f getDirection(ParticleGroup group) {
            return (direction != null ? direction : group.getWorldRotation().mult(Vector3f.UNIT_Z));
        }
        
    }
    
}
