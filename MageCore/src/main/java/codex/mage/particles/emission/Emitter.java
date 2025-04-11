/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.emission;

import codex.mage.particles.Particle;
import codex.mage.particles.ParticleData;
import codex.mage.particles.ParticleGroup;
import codex.mage.particles.drivers.ParticleDriver;
import codex.mage.tweens.Value;

/**
 * Emits particles at certain time intervals.
 * <p>
 * Does not fill in particle data.
 * 
 * @author codex
 * @param <T>
 */
public abstract class Emitter <T extends Particle> implements ParticleDriver<T>, Spawner<T> {
    
    private Value<Integer> maxEmissions = Value.value(-1);
    private Value<Integer> particlesPerEmission = Value.value(1);
    private Value<Float> emissionRate = Value.value(.1f);
    private Value<Boolean> enabled = Value.value(true);
    
    private int emissions = 0;
    private float time = 0;
    
    @Override
    public void updateGroup(ParticleGroup group, float tpf) {
        if (enabled.get()) {
            int n = updateSpawn(group.getTime(), tpf);
            for (int i = 0; i < n; i++) {
                group.add(spawn());
            }
        }
    }
    @Override
    public void updateParticle(Particle particle, float tpf) {}
    @Override
    public void particleAdded(ParticleGroup group, Particle particle) {}    
    @Override
    public void groupReset(ParticleGroup group) {
        reset();
    }
    @Override
    public int updateSpawn(float time, float tpf) {
        this.time += tpf;
        if ((maxEmissions.get() < 0 || emissions < maxEmissions.get()) && this.time >= emissionRate.get()) {
            this.time = 0;
            emissions++;
            return particlesPerEmission.get();
        }
        return 0;
    }
    @Override
    public T spawn() {
        return createParticle();
    }
    @Override
    public void reset() {
        emissions = 0;
        time = 0;
    }
    
    /**
     * Creates a new particle instance.
     * 
     * @return 
     */
    protected abstract T createParticle();
    
    public void setMaxEmissions(Value<Integer> maxEmissions) {
        this.maxEmissions = maxEmissions;
    }
    public void setParticlesPerEmission(Value<Integer> particlesPerEmission) {
        this.particlesPerEmission = particlesPerEmission;
    }
    public void setEmissionRate(Value<Float> emissionRate) {
        this.emissionRate = emissionRate;
    }
    public void setEnabled(Value<Boolean> enabled) {
        this.enabled = enabled;
    }
    
    public Value<Integer> getMaxEmissions() {
        return maxEmissions;
    }
    public Value<Integer> getParticlesPerEmission() {
        return particlesPerEmission;
    }
    public Value<Float> getEmissionRate() {
        return emissionRate;
    }
    public Value<Boolean> isEnabled() {
        return enabled;
    }
    public int getNumEmissions() {
        return emissions;
    }
    
    /**
     * Creates a spawner instance using a non-abstract implementation
     * that creates basic particles.
     * 
     * @return 
     */
    public static Emitter<ParticleData> create() {
        return new SpawnerImpl();
    }
    
    private static class SpawnerImpl extends Emitter<ParticleData> {

        @Override
        protected ParticleData createParticle() {
            return new ParticleData();
        }
        
    }
    
}
