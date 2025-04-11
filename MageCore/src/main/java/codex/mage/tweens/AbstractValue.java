/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.tweens;

import com.simsilica.lemur.core.VersionedReference;

/**
 *
 * @author codex
 * @param <T>
 */
public abstract class AbstractValue <T> implements Value<T> {
    
    protected T value;
    protected long version = 0;
    
    @Override
    public T get() {
        return value;
    }
    @Override
    public long getVersion() {
        return version;
    }
    @Override
    public T getObject() {
        return get();
    }
    @Override
    public VersionedReference<T> createReference() {
        return new VersionedReference<>(this);
    }
    
}
