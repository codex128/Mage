/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.emission.volume;

import codex.mage.tweens.Value;
import codex.mage.utils.VfxUtils;
import com.jme3.math.FastMath;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public class EmissionTorus implements EmissionVolume {

    private Value<Vector3f> center;
    private Value<Vector3f> axis;
    private Value<Float> majorRadius;
    private Value<Float> minorRadius;
    private Value<Boolean> applyScale = Value.value(false);
    
    public EmissionTorus(Vector3f axis, float majorRadius, float minorRadius) {
        this(Vector3f.ZERO, axis, majorRadius, minorRadius);
    }
    public EmissionTorus(Vector3f center, Vector3f axis, float majorRadius, float minorRadius) {
        this(Value.value(center), Value.value(axis), Value.value(majorRadius), Value.value(minorRadius));
    }
    public EmissionTorus(Value<Vector3f> center, Value<Vector3f> axis, Value<Float> majorRadius, Value<Float> minorRadius) {
        this.center = center;
        this.axis = axis;
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
    }
    
    @Override
    public Vector3f getNextPosition(Transform transform) {
        Vector3f y = transform.getRotation().mult(axis.get());
        Vector3f x = y.cross(Vector3f.UNIT_Y);
        Vector3f z = y.cross(x);
        float a = VfxUtils.gen.nextFloat(0f, FastMath.TWO_PI);
        float b = VfxUtils.gen.nextFloat(0f, FastMath.TWO_PI);
        Vector3f major = x.multLocal(FastMath.cos(a)).addLocal(z.multLocal(FastMath.sin(a))).normalizeLocal();
        Vector3f minor = major.mult(FastMath.cos(b)).addLocal(y.multLocal(FastMath.sin(b))).normalizeLocal();
        major.multLocal(majorRadius.get());
        minor.multLocal(VfxUtils.gen.nextFloat(0f, minorRadius.get()));
        Vector3f origin = center.get();
        if (applyScale.get()) {
            major.multLocal(transform.getScale());
            minor.multLocal(transform.getScale());
            origin = origin.mult(transform.getScale());
        }
        return major.addLocal(minor).addLocal(origin).addLocal(transform.getTranslation());
    }

    public void setCenter(Value<Vector3f> center) {
        this.center = center;
    }
    public void setAxis(Value<Vector3f> axis) {
        this.axis = axis;
    }
    public void setMajorRadius(Value<Float> majorRadius) {
        this.majorRadius = majorRadius;
    }
    public void setMinorRadius(Value<Float> minorRadius) {
        this.minorRadius = minorRadius;
    }
    public void setApplyScale(Value<Boolean> applyScale) {
        this.applyScale = applyScale;
    }
    
    public Value<Vector3f> getCenter() {
        return center;
    }
    public Value<Vector3f> getAxis() {
        return axis;
    }
    public Value<Float> getMajorRadius() {
        return majorRadius;
    }
    public Value<Float> getMinorRadius() {
        return minorRadius;
    }
    public Value<Boolean> getApplyScale() {
        return applyScale;
    }
    
}
