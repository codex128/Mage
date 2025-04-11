/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.tweens;

import codex.mage.utils.VfxUtils;

/**
 *
 * @author codex
 * @param <T>
 */
public class Random <T> extends AbstractValue<T> {

    private T a, b;
    private T value;
    private Interpolator<T> interpolator;
    
    public Random(T a, T b, Interpolator<T> interpolator) {
        this.a = a;
        this.b = b;
        this.interpolator = interpolator;
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
        value = interpolator.interpolate(VfxUtils.gen.nextFloat(), a, b);
        return value;
    }

    public void setA(T a) {
        this.a = a;
    }
    public void setB(T b) {
        this.b = b;
    }
    public void setInterpolator(Interpolator<T> interpolator) {
        this.interpolator = interpolator;
    }

    public T getA() {
        return a;
    }
    public T getB() {
        return b;
    }
    public Interpolator<T> getInterpolator() {
        return interpolator;
    }
    
}
