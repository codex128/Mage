/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.drivers;

import codex.mage.particles.ParticleGroup;
import codex.mage.particles.PhysicalParticle;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;

/**
 *
 * @author codex
 */
public class PhysicsDriver implements ParticleDriver<PhysicalParticle> {
    
    private final PhysicsSpace space;
    private final CollisionShape shape;

    public PhysicsDriver(PhysicsSpace space, CollisionShape shape) {
        this.space = space;
        this.shape = shape;
    }
    
    @Override
    public void updateGroup(ParticleGroup<PhysicalParticle> group, float tpf) {}
    @Override
    public void updateParticle(PhysicalParticle p, float tpf) {
        p.rigidBody.getPhysicsLocation(p.getPosition());
        p.rigidBody.getPhysicsRotation(p.getRotation());
        p.rigidBody.getLinearVelocity(p.linearVelocity);
        p.rigidBody.getAngularVelocity(p.angularVelocity);
    }
    @Override
    public void particleAdded(ParticleGroup<PhysicalParticle> group, PhysicalParticle p) {
        CollisionShape s = shape.jmeClone();
        s.setScale(p.getScale().mult(p.size.get()));
        p.rigidBody = new PhysicsRigidBody(s, p.mass.get());
        p.rigidBody.setPhysicsLocation(p.getPosition());
        p.rigidBody.setPhysicsRotation(p.getRotation());
        space.add(p.rigidBody);
    }
    @Override
    public void groupReset(ParticleGroup<PhysicalParticle> group) {}
    @Override
    public void particleRemoved(ParticleGroup<PhysicalParticle> group, PhysicalParticle p) {
        space.remove(p.rigidBody);
        p.rigidBody = null;
    }
    @Override
    public void removedFromGroup(ParticleGroup<PhysicalParticle> group) {
        removeAllBodies(group);
    }
    
    private void removeAllBodies(ParticleGroup<PhysicalParticle> group) {
        for (PhysicalParticle p : group) {
            space.remove(p.rigidBody);
            p.rigidBody = null;
        }
    }
    
}
