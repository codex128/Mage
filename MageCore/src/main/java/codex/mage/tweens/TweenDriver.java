/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.tweens;

import codex.mage.particles.ParticleData;
import codex.mage.particles.ParticleGroup;
import codex.mage.particles.drivers.ParticleDriver;
import com.jme3.anim.tween.Tween;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public class TweenDriver implements ParticleDriver<ParticleData> {
    
    private Value<Float> lifetime = Value.value(1f);
    private Value<Vector3f> linearVelocity = Value.value(Vector3f.ZERO);
    private Value<Vector3f> angularVelocity = Value.value(Vector3f.ZERO);
    private Value<ColorRGBA> color = Value.value(ColorRGBA.White);
    private Value<Float> size = Value.value(1f);
    private Value<Integer> particlesPerEmission = Value.value(1);
    
    private float delay = 0;
    private Tween tween;
    
    @Override
    public void updateGroup(ParticleGroup group, float tpf) {
        
    }
    @Override
    public void updateParticle(ParticleData particle, float tpf) {}
    @Override
    public void particleAdded(ParticleGroup group, ParticleData particle) {}
    @Override
    public void groupReset(ParticleGroup group) {}
    
}
