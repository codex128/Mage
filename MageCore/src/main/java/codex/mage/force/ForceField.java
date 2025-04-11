/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.mage.force;

import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public interface ForceField {
    
    /**
     * Applies this force field.
     * 
     * @param transform affected body transform (altered)
     * @param linearVelocity affected body linear velocity (altered)
     * @param angularVelocity affected body angular velocity (altered)
     * @param tpf
     * @return true if influence is applied
     */
    public boolean applyInfluence(Transform transform, Vector3f linearVelocity, Vector3f angularVelocity, float tpf);
    
}
