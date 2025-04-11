/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.tweens;

import com.jme3.anim.tween.AbstractTween;

/**
 * Jme animation Tween that updates a {@link Value} over time.
 * 
 * @author codex
 * @param <T> value parameter type
 */
public class ValueTween <T> extends AbstractTween {

    private Value<T> value;

    public ValueTween(Value<T> value, double length) {
        super(length);
        this.value = value;
    }

    @Override
    protected void doInterpolate(double t) {
        value.update((float)t);
    }

    public void setValue(Value<T> value) {
        this.value = value;
    }
    
    public Value<T> getValue() {
        return value;
    }
    
}
