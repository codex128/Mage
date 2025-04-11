/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.force;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public class ForceSpace extends LinkedList<ForceField> implements ForceField {
    
    private final Transform tempTransform = new Transform();
    private final Vector3f tempLinearVelocity = new Vector3f();
    private final Vector3f tempAngularVelocity = new Vector3f();

    @Override
    public boolean applyInfluence(Transform transform, Vector3f linearVelocity, Vector3f angularVelocity, float tpf) {
        boolean influenced = false;
        for (ForceField f : this) {
            if (f.applyInfluence(transform, linearVelocity, angularVelocity, tpf)) {
                influenced = true;
            }
        }
        return influenced;
    }
    
    public void applyToPhysicsSpace(PhysicsSpace space, float tpf) {
        if (isEmpty()) return;
        for (PhysicsRigidBody r : space.getRigidBodyList()) {
            r.getPhysicsLocation(tempTransform.getTranslation());
            r.getPhysicsRotation(tempTransform.getRotation());
            r.getLinearVelocity(tempLinearVelocity);
            r.getAngularVelocity(tempAngularVelocity);
            if (applyInfluence(tempTransform, tempLinearVelocity, tempAngularVelocity, tpf)) {
                r.setPhysicsLocation(tempTransform.getTranslation());
                r.setPhysicsRotation(tempTransform.getRotation());
                r.setLinearVelocity(tempLinearVelocity);
                r.setAngularVelocity(tempAngularVelocity);
            }
        }
    }
    
}
