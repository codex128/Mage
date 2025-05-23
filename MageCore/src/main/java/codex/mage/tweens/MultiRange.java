/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.tweens;

import com.jme3.math.EaseFunction;
import com.jme3.math.Easing;
import com.jme3.math.FastMath;

/**
 *
 * @author codex
 * @param <T>
 */
public class MultiRange <T> extends AbstractValue<T> {

    private final Range<T>[] ranges;
    private final EaseFunction ease;
    private T value;

    public MultiRange(Range<T>... ranges) {
        this(Easing.linear, ranges);
    }
    public MultiRange(EaseFunction ease, Range<T>... ranges) {
        this.ease = ease;
        this.ranges = ranges;
        update(0);
    }
    
    @Override
    public T get() {
        return value;
    }
    @Override
    public void set(T val) {}
    @Override
    public final T update(float time) {
        version++;
        time = ease.apply(time);
        int i = Math.min((int)(time*ranges.length), ranges.length-1);
        float step = 1f/ranges.length;
        float min = i*step;
        float max = min+step;
        ranges[i].update(FastMath.unInterpolateLinear(FastMath.clamp(time, min, max), min, max));
        value = ranges[i].get();
        return value;
    }
    
}
