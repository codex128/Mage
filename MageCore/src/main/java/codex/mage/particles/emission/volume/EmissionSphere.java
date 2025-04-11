/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.emission.volume;

import codex.mage.tweens.Interpolator;
import codex.mage.tweens.Range;
import codex.mage.tweens.Value;
import codex.mage.utils.VfxUtils;
import com.jme3.math.Easing;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public class EmissionSphere implements EmissionVolume {

    private Value<Vector3f> center;
    private Value<Float> radius;
    private Value<Float> distribution;

    public EmissionSphere(Value<Float> radius) {
        this(Value.value(Vector3f.ZERO), radius, new Range(0f, 1f, Interpolator.Float));
    }
    public EmissionSphere(Value<Float> radius, Value<Float> distribution) {
        this(Value.value(Vector3f.ZERO), radius, distribution);
    }
    public EmissionSphere(Value<Vector3f> center, Value<Float> radius, Value<Float> distribution) {
        this.center = center;
        this.radius = radius;
        this.distribution = distribution;
    }
    
    @Override
    public Vector3f getNextPosition(Transform transform) {
        return VfxUtils.gen.nextUnitVector3f()
                .multLocal(radius.get()*distribution.update(VfxUtils.gen.nextFloat()))
                .multLocal(transform.getScale())
                .addLocal(center.get())
                .addLocal(transform.getTranslation());
    }

    public void setCenter(Value<Vector3f> center) {
        this.center = center;
    }
    public void setRadius(Value<Float> radius) {
        this.radius = radius;
    }
    public void setDistribution(Value<Float> ditribution) {
        this.distribution = ditribution;
    }
    
    public Value<Vector3f> getCenter() {
        return center;
    }
    public Value<Float> getRadius() {
        return radius;
    }
    public Value<Float> getDistribution() {
        return distribution;
    }
    
}
