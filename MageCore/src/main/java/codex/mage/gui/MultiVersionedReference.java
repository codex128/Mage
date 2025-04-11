/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.gui;

import com.simsilica.lemur.core.VersionedObject;
import com.simsilica.lemur.core.VersionedReference;

/**
 *
 * @author codex
 */
public class MultiVersionedReference <T> extends VersionedReference {
    
    private final VersionedReference[] refs;
    
    public MultiVersionedReference(VersionedObject<T> object, VersionedReference... refs) {
        super(object);
        this.refs = refs;
    }
    
    @Override
    public boolean needsUpdate() {
        for (VersionedReference r : refs) {
            if (r.needsUpdate()) {
                return true;
            }
        }
        return super.needsUpdate();
    }
    
    @Override
    public boolean update() {
        boolean u = super.update();
        for (VersionedReference r : refs) {
            if (r.update()) u = true;
        }
        return u;
    }
    
    public VersionedReference[] getChildReferences() {
        return refs;
    }
    
}
