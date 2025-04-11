/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.tweens;

import com.simsilica.lemur.core.VersionedObject;
import com.simsilica.lemur.core.VersionedReference;

/**
 * Represents a value.
 * 
 * @author codex
 * @param <T> type of value being held
 */
public interface Value <T> extends VersionedObject<T> {
    
    /**
     * Returns the value held.
     * 
     * @return 
     */
    public T get();
    
    /**
     * Sets the value held.
     * 
     * @param val 
     */
    public void set(T val);
    
    /**
     * Updates the held value based on a time component between 0 and 1.
     * 
     * @param t time between 1 and 0 (inclusive)
     * @return 
     */
    public T update(float t);
    
    /**
     * Returns a Value object that represents a single value
     * that can be changed manually but not by update.
     * 
     * @param <T>
     * @param value
     * @return 
     */
    public static <T> Value<T> value(T value) {
        return new ValueImpl<>(value);
    }
    
    /**
     * Returns a Value object that represents a single unchangable value.
     * 
     * @param <T>
     * @param value
     * @return 
     */
    public static <T> Value<T> constant(T value) {
        return new Constant<>(value);
    }
    
    /**
     * Creates an animatable tween for animating a Value.
     * 
     * @param <T>
     * @param value Value object
     * @param length duration of the tween in seconds
     * @return tween
     */
    public static <T> ValueTween<T> tween(Value<T> value, double length) {
        return new ValueTween<>(value, length);
    }
    
    public static class ValueImpl <T> implements Value<T> {

        private T value;
        private long version = 0;

        private ValueImpl(T value) {
            this.value = value;
        }
        
        @Override
        public T get() {
            return value;
        }
        @Override
        public void set(T val) {
            version++;
            this.value = val;
        }
        @Override
        public T update(float time) {
            return value;
        }
        @Override
        public long getVersion() {
            return version;
        }
        @Override
        public T getObject() {
            return value;
        }
        @Override
        public VersionedReference<T> createReference() {
            return new VersionedReference<>(this);
        }
        
    }
    
    public static class Constant <T> implements Value<T> {
        
        private final T value;

        private Constant(T value) {
            this.value = value;
        }
        
        @Override
        public T get() {
            return value;
        }
        @Override
        public void set(T val) {}
        @Override
        public T update(float time) {
            return value;
        }
        @Override
        public long getVersion() {
            return 0;
        }
        @Override
        public T getObject() {
            return value;
        }
        @Override
        public VersionedReference<T> createReference() {
            return new VersionedReference<>(this);
        }
        
    }
    
}
