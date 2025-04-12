/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.force;

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
    
}
