/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.mage.tweens;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

/**
 * Performs linear interpolation between two objects.
 * 
 * @author codex
 * @param <T> type of object to interpolate
 */
public interface Interpolator <T> {
    
    /**
     * Performs linear interpolation between two objects and returns the result.
     * 
     * @param t time, scale, or blend value between 0 and 1 (inclusive)
     * @param a first object, fully returned at 0
     * @param b second object, fully returned at 1
     * @return interpolated object
     */
    T interpolate(float t, T a, T b);
    
    /**
     * Returns the percentage distance the argument val is between two objects.
     * <p>
     * If the object type exists in more than one demension, it is sometimes necessary
     * to clamp the value to the linear path using 
     * {@link #clampToLine(Object, Object, Object)}.
     * 
     * @param val
     * @param a
     * @param b
     * @return 
     */
    float uninterpolate(T val, T a, T b);
    
    /**
     * Interpolates only between the two objects without extrapolation.
     * 
     * @param t
     * @param a
     * @param b
     * @return 
     */
    default T interpolateClamped(float t, T a, T b) {
        return interpolate(FastMath.clamp(t, 0, 1), a, b);
    }
    
    /**
     * Clamps the value between two objects.
     * 
     * @param val
     * @param a
     * @param b
     * @return 
     */
    default T clamp(T val, T a, T b) {
        return interpolateClamped(uninterpolate(val, a, b), a, b);
    }
    
    /**
     * Clamps the value to the closest point on the linear path between two objects.
     * <p>
     * Returns the value unchanged as the default implementation, as it is assumed that
     * values will already be a member of the linear path.
     * 
     * @param val
     * @param a
     * @param b
     * @return 
     */
    default T clampToLine(T val, T a, T b) {
        return val;
    }
    
    
    /**
     * Interpolates between two integers.
     * <p>
     * Supports uninterpolation.
     */
    Interpolator<Integer> Integer = new Interpolator<Integer>() {
        @Override
        public Integer interpolate(float t, Integer a, Integer b) {
            return (int)((b-a)*t+a);
        }
        @Override
        public float uninterpolate(Integer val, Integer a, Integer b) {
            return (float)(val-a)/(b-a);
        }
    };
    
    /**
     * Interpolates between two floats.
     * <p>
     * Supports uninterpolation.
     */
    Interpolator<Float> Float = new Interpolator<Float>() {
        @Override
        public Float interpolate(float t, Float a, Float b) {
            return (b-a)*t+a;
        }
        @Override
        public float uninterpolate(Float val, Float a, Float b) {
            return (val-a)/(b-a);
        }
    };
    
    /**
     * Interpolates between two booleans.
     * <p>
     * If time is less than or equal to 0.5, a is returned, otherwise b is returned.
     * <p>
     * Supports uninterpolation.
     */
    Interpolator<Boolean> Boolean = new Interpolator<Boolean>() {
        @Override
        public Boolean interpolate(float t, Boolean a, Boolean b) {
            return t <= 0.5f ? a : b;
        }
        @Override
        public float uninterpolate(Boolean val, Boolean a, Boolean b) {
            return val.booleanValue() == a.booleanValue() ? 0f : 1f;
        }
    };
    
    /**
     * Interpolates between two {@link Vector3f} objects.
     */
    Interpolator<Vector3f> Vector3f = new Interpolator<Vector3f>() {
        @Override
        public Vector3f interpolate(float t, Vector3f a, Vector3f b) {
            return b.subtract(a).multLocal(t).addLocal(a);
        }
        @Override
        public float uninterpolate(Vector3f val, Vector3f a, Vector3f b) {
            return clampToLine(val, a, b).distance(a)/a.distance(b);
        }
        @Override
        public Vector3f clampToLine(Vector3f val, Vector3f a, Vector3f b) {
            Vector3f c = b.subtract(a).normalizeLocal();
            return c.multLocal(c.dot(val.subtract(a)));
        }
    };
    
    /**
     * Interpolates between two {@link Vector2f} objects.
     */
    Interpolator<Vector2f> Vector2f = new Interpolator<Vector2f>() {
        @Override
        public Vector2f interpolate(float t, Vector2f a, Vector2f b) {
            return b.subtract(a).multLocal(t).addLocal(a);
        }
        @Override
        public float uninterpolate(Vector2f val, Vector2f a, Vector2f b) {
            return clampToLine(val, a, b).distance(a)/a.distance(b);
        }
        @Override
        public Vector2f clampToLine(Vector2f val, Vector2f a, Vector2f b) {
            Vector2f c = b.subtract(a).normalizeLocal();
            return c.multLocal(c.dot(val.subtract(a)));
        }
    };
    
    /**
     * Interpolates between two {@link ColorRGBA} objects.
     */
    Interpolator<ColorRGBA> Color = new Interpolator<ColorRGBA>() {
        
        @Override
        public ColorRGBA interpolate(float t, ColorRGBA a, ColorRGBA b) {
            return new ColorRGBA().interpolateLocal(a, b, t);
        }
        @Override
        public float uninterpolate(ColorRGBA val, ColorRGBA a, ColorRGBA b) {
            Vector4f a4 = a.toVector4f();
            return clampToLineVector(val, a, b).distance(a4)/a4.distance(b.toVector4f());
        }
        @Override
        public ColorRGBA clampToLine(ColorRGBA val, ColorRGBA a, ColorRGBA b) {
            return new ColorRGBA(clampToLineVector(val, a, b));
        }
        
        private Vector4f clampToLineVector(ColorRGBA val, ColorRGBA a, ColorRGBA b) {
            Vector4f a4 = a.toVector4f();
            Vector4f c = b.toVector4f().subtractLocal(a4).normalizeLocal();
            return c.multLocal(c.dot(val.toVector4f().subtractLocal(a4)));
        }
        
    };
    
}
