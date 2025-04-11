/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.emission.volume;

import codex.mage.utils.VfxUtils;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public class LegacyEmissionSphere implements EmissionVolume {
    
    private final Vector3f center = new Vector3f();
    private float radius;
    
    public LegacyEmissionSphere() {
        this(.5f);
    }
    public LegacyEmissionSphere(float radius) {
        this.radius = radius;
    }
    public LegacyEmissionSphere(Vector3f center) {
        this(center, .5f);
    }
    public LegacyEmissionSphere(Vector3f center, float radius) {
        this.center.set(center);
        this.radius = radius;
    }
    
    @Override
    public Vector3f getNextPosition(Transform transform) {
        return VfxUtils.getGenerator().nextUnitVector3f()
                .multLocal(VfxUtils.getGenerator().nextFloat(radius))
                .multLocal(transform.getScale())
                .addLocal(transform.getTranslation());
    }

    public void setCenter(Vector3f center) {
        this.center.set(center);
    }
    public void setRadius(float radius) {
        this.radius = radius;
    }
    
    public Vector3f getCenter() {
        return center;
    }
    public float getRadius() {
        return radius;
    }
    
}
