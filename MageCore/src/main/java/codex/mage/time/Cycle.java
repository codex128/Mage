/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.time;

import codex.mage.tweens.Interpolator;
import codex.mage.tweens.Range;
import codex.mage.tweens.Value;
import com.jme3.math.Easing;

/**
 *
 * @author codex
 */
public class Cycle {
    
    private Value<Float> length = Value.value(1f);
    private Value<Float> warp = new Range(0f, 1f, Interpolator.Float, Easing.linear);
    private float time = 0;
    private boolean repeat = false;
    
}
