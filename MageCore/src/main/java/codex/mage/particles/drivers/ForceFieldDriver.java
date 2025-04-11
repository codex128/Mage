/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.drivers;

import codex.mage.force.ForceSpace;
import codex.mage.particles.ParticleData;
import codex.mage.particles.ParticleGroup;

/**
 *
 * @author codex
 */
public class ForceFieldDriver implements ParticleDriver<ParticleData> {
    
    private final ForceSpace space;
    
    public ForceFieldDriver() {
        this(new ForceSpace());
    }
    public ForceFieldDriver(ForceSpace space) {
        this.space = space;
    }
    
    @Override
    public void updateGroup(ParticleGroup<ParticleData> group, float tpf) {}
    @Override
    public void updateParticle(ParticleData p, float tpf) {
        space.applyInfluence(p.transform, p.linearVelocity, p.angularVelocity, tpf);
    }
    @Override
    public void particleAdded(ParticleGroup<ParticleData> group, ParticleData particle) {}
    @Override
    public void groupReset(ParticleGroup<ParticleData> group) {}
    
    public ForceSpace getSpace() {
        return space;
    }
    
}
