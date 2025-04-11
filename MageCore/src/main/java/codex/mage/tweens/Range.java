/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.tweens;

import com.jme3.math.EaseFunction;
import com.jme3.math.Easing;
import com.jme3.math.FastMath;

/**
 * Represents a range of values between two objects.
 * 
 * @author codex
 * @param <T>
 */
public class Range <T> extends AbstractRange<T> {
    
    private EaseFunction ease;

    public Range(T a, T b, Interpolator<T> interpolator) {
        this(a, b, interpolator, Easing.linear);
    }
    public Range(T a, T b, Interpolator<T> interpolator, EaseFunction ease) {
        super(a, b, interpolator);
        this.ease = ease;
        this.value = this.a;
    }

    @Override
    protected T interpolate(float t) {
        return interpolator.interpolate(ease.apply(FastMath.clamp(t, 0, 1)), a, b);
    }
    
    public void setEase(EaseFunction ease) {
        this.ease = ease;
        version++;
    }
    public EaseFunction getEase() {
        return ease;
    }
    
}
