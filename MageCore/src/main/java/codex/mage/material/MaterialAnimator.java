/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.material;

import codex.boost.control.GeometryControl;
import codex.mage.tweens.Interpolator;
import codex.mage.tweens.Range;
import codex.mage.tweens.Value;
import com.jme3.math.Easing;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 *
 * @author codex
 */
public class MaterialAnimator extends GeometryControl {
    
    private Value<Float> length = Value.value(1f);
    private Value<Float> interpolator = new Range(0f, 1f, Interpolator.Float, Easing.linear);
    private float time = 0;
    
    @Override
    protected void controlUpdate(float tpf) {}
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
    
}
