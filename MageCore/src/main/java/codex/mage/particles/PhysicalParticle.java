/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles;

import codex.mage.tweens.Value;
import com.jme3.bullet.objects.PhysicsRigidBody;

/**
 *
 * @author codex
 */
public class PhysicalParticle extends ParticleData {
    
    public Value<Float> mass = Value.value(1f);
    public PhysicsRigidBody rigidBody;
    
}
