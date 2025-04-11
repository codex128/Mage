/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.tweens;

/**
 *
 * @author codex
 * @param <T>
 */
public abstract class AbstractRange <T> extends AbstractValue<T> {

    protected T a, b;
    protected final Interpolator<T> interpolator;
    protected float percent = 0;
    
    public AbstractRange(T a, T b, Interpolator<T> interpolator) {
        this.a = a;
        this.b = b;
        this.interpolator = interpolator;
    }
    
    protected abstract T interpolate(float t);
    
    @Override
    public void set(T val) {}
    @Override
    public T update(float t) {
        percent = t;
        version++;
        value = interpolate(percent);
        return value;
    }
    
    public void setA(T a) {
        this.a = a;
        version++;
        value = interpolate(percent);
    }
    public void setB(T b) {
        this.b = b;
        version++;
        value = interpolate(percent);
    }
    
}
