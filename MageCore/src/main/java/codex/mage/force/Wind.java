/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.force;

import codex.mage.tweens.Interpolator;
import codex.mage.tweens.Range;
import codex.mage.tweens.Value;
import com.jme3.math.Easing;
import com.jme3.math.Plane;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author codex
 */
public class Wind extends Node implements ForceField{
    
    private Value<Float> radius = Value.value(1f);
    private Value<Float> distance = Value.value(20f);
    private Value<Float> speed = new Range(100f, 0f, Interpolator.Float, Easing.linear);
    private Value<Float> power = new Range(40f, 0f, Interpolator.Float, Easing.linear);
    private Value<Float> pPowerRadius = Value.value(1f);
    private final Plane plane = new Plane();
    
    @Override
    public boolean applyInfluence(Transform transform, Vector3f linearVelocity, Vector3f angularVelocity, float tpf) {
        calculateEmissionPlane();
        float d = plane.pseudoDistance(transform.getTranslation());
        float r = plane.getClosestPoint(transform.getTranslation()).distance(worldTransform.getTranslation());
        if (d >= 0 && d <= distance.get() && r <= radius.get()) {
            float s = plane.getNormal().dot(linearVelocity);
            d /= distance.get();
            if (s < speed.update(d)*tpf) {
                r /= radius.get();
                linearVelocity.addLocal(plane.getNormal().mult(power.update(d)*pPowerRadius.update(r)*tpf));
            }
            return true;
        }
        return false;
    }
    
    private Plane calculateEmissionPlane() {
        Vector3f normal = worldTransform.getRotation().mult(Vector3f.UNIT_Z);
        plane.setOriginNormal(worldTransform.getTranslation(), normal);
        return plane;
    }

    public void setRadius(Value<Float> radius) {
        this.radius = radius;
    }
    public void setDistance(Value<Float> distance) {
        this.distance = distance;
    }
    public void setSpeed(Value<Float> speed) {
        this.speed = speed;
    }
    public void setPower(Value<Float> power) {
        this.power = power;
    }
    public void setPercentPowerPerRadius(Value<Float> pPowerRadius) {
        this.pPowerRadius = pPowerRadius;
    }

    public Value<Float> getRadius() {
        return radius;
    }
    public Value<Float> getDistance() {
        return distance;
    }
    public Value<Float> getSpeed() {
        return speed;
    }
    public Value<Float> getPower() {
        return power;
    }
    public Value<Float> getPercentForcePerRadius() {
        return pPowerRadius;
    }
    
}
