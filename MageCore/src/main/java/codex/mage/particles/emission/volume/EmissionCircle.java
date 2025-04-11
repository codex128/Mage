/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.emission.volume;

import codex.mage.tweens.Value;
import codex.mage.utils.VfxUtils;
import com.jme3.math.Plane;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public class EmissionCircle implements EmissionVolume {
    
    private Value<Float> radius;
    private Value<Vector3f> normal;
    private final Plane plane = new Plane();
    
    public EmissionCircle(float radius) {
        this(Value.value(radius), null);
    }
    public EmissionCircle(float radius, Vector3f normal) {
        this(Value.value(radius), Value.value(normal));
    }
    public EmissionCircle(Value<Float> radius, Value<Vector3f> normal) {
        this.radius = radius;
        this.normal = normal;
    }
    
    @Override
    public Vector3f getNextPosition(Transform transform) {
        if (normal == null) {
            plane.setNormal(transform.getRotation().mult(Vector3f.UNIT_Z));
        } else {
            plane.setNormal(normal.get());
        }
        return plane.getClosestPoint(VfxUtils.gen.nextUnitVector3f()
                .multLocal(radius.get())).addLocal(transform.getTranslation());
    }

    public void setRadius(Value<Float> radius) {
        this.radius = radius;
    }
    public void setNormal(Value<Vector3f> normal) {
        this.normal = normal;
    }
    
    public Value<Float> getRadius() {
        return radius;
    }
    public Value<Vector3f> getNormal() {
        return normal;
    }
    
}
