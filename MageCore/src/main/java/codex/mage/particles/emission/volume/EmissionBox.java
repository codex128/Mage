/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles.emission.volume;

import codex.mage.tweens.Value;
import codex.mage.utils.VfxUtils;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;

/**
 *
 * @author codex
 */
public class EmissionBox implements EmissionVolume {

    private Value<Vector3f> center;
    private Value<Vector3f> extent;

    public EmissionBox() {
        this(.5f, .5f, .5f);
    }
    public EmissionBox(float extent) {
        this(extent, extent, extent);
    }
    public EmissionBox(Vector3f center) {
        this(center, .5f, .5f, .5f);
    }
    public EmissionBox(float x, float y, float z) {
        this(Vector3f.ZERO, x, y, z);
    }
    public EmissionBox(Vector3f center, float extent) {
        this(center, extent, extent, extent);
    }
    public EmissionBox(Vector3f center, Vector3f extent) {
        this(Value.value(center), Value.value(extent));
    }
    public EmissionBox(Vector3f center, float x, float y, float z) {
        this(Value.value(center), Value.value(new Vector3f(x, y, z)));
    }
    public EmissionBox(Value<Vector3f> center, Value<Vector3f> extent) {
        this.center = center;
        this.extent = extent;
    }
    
    @Override
    public Vector3f getNextPosition(Transform transform) {
        return VfxUtils.random(center.get(), extent.get().x, extent.get().y, extent.get().z)
                .multLocal(transform.getScale())
                .addLocal(transform.getTranslation());
    }

    public void setCenter(Value<Vector3f> center) {
        this.center = center;
    }
    public void setExtent(Value<Vector3f> extent) {
        this.extent = extent;
    }

    public Value<Vector3f> getCenter() {
        return center;
    }
    public Value<Vector3f> getExtent() {
        return extent;
    }
    
}
