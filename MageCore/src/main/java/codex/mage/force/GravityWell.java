/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.force;

import codex.mage.tweens.Interpolator;
import codex.mage.tweens.Range;
import codex.mage.tweens.Value;
import com.jme3.math.Easing;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author codex
 */
public class GravityWell extends Node implements ForceField {

    private Value<Float> radius;
    private Value<Float> pull = new Range<>(10f, 0f, Interpolator.Float, Easing.linear);
    
    public GravityWell(float radius) {
        this(Value.value(radius));
    }
    public GravityWell(Value<Float> radius) {
        this.radius = radius;
    }
    
    @Override
    public boolean applyInfluence(Transform transform, Vector3f linearVelocity, Vector3f angularVelocity, float tpf) {
        Vector3f down = worldTransform.getTranslation().subtract(transform.getTranslation());
        float dist = down.length();
        if (dist <= radius.get()) {
            pull.update(dist/radius.get());
            down.normalizeLocal().multLocal(pull.get()*tpf);
            linearVelocity.addLocal(down);
            return true;
        }
        return false;
    }
    
    public void setRadius(Value<Float> radius) {
        this.radius = radius;
    }
    public void setPull(Value<Float> pull) {
        this.pull = pull;
    }
    
    public Value<Float> getRadius() {
        return radius;
    }
    public Value<Float> getPull() {
        return pull;
    }
    
}
