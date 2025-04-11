/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles;

/**
 * Represents a single particle.
 * 
 * @author codex
 */
public abstract class Particle {
    
    protected float life = 1f, maxLife = life;
    protected boolean alive = true;
    
    public abstract void updateValues(float time);
    
    /**
     * Gets the amount of life remaining.
     * 
     * @return 
     */
    public float getLife() {
        return life;
    }
    
    /**
     * Gets the maximum life.
     * <p>
     * This is the life amount when the particle was newly created.
     * 
     * @return 
     */
    public float getMaxLife() {
        return maxLife;
    }
    
    /**
     * Gets the percentage life remaining.
     * <p>
     * Full life returns 1.0 and no life returns 0.0.
     * <p>
     * <em>Note: this measures the percentage of life remaining,
     * not percentage of life decayed!</em>
     * 
     * @return life percentage
     */
    public float getLifePercent() {
        return life/maxLife;
    }    
    
    /**
     * Sets the lifetime.
     * 
     * @param life 
     */
    public void setLife(float life) {
        maxLife = this.life = life;
    }
    
    /**
     * Kills the particle so it is destroyed on next update.
     */
    public void kill() {
        // Setting a boolean instead of setting the life value directly
        // because that may upset drivers performing "over lifetime" operations.
        alive = false;
    }
    
    /**
     * Decays life from the particle.
     * <p>
     * Further update operations should be performed in ParticleDrivers.
     * 
     * @param tpf
     * @return true if the particle is alive, false otherwise
     */
    public boolean decayLife(float tpf) {
        life = Math.max(life-tpf, 0);
        return isAlive();
    }
    
    /**
     * Returns true if the particle is alive.
     * <p>
     * i.e. life is greater than zero, and {@link #kill()} has not been called.
     * 
     * @return 
     */
    public boolean isAlive() {
        return life > 0 && alive;
    }
    
}
