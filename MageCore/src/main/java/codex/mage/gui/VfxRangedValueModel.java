/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.gui;

import codex.mage.tweens.Value;
import com.simsilica.lemur.RangedValueModel;
import com.simsilica.lemur.core.VersionedReference;

/**
 *
 * @author codex
 */
public class VfxRangedValueModel implements RangedValueModel {
    
    private static final double MIN = 0, MAX = 100;
    
    private final Value value;
    private double percent = 0;
    private long version = 0;
    
    public VfxRangedValueModel(Value value) {
        this.value = value;
    }
    
    @Override
    public void setValue(double val) {
        percent = (val-MIN)/(MAX-MIN);
        value.update((float)percent);
        version++;
    }
    @Override
    public double getValue() {
        return (MAX-MIN)*percent+MIN;
    }
    @Override
    public void setPercent(double val) {
        percent = val;
        value.update((float)percent);
        version++;
    }
    @Override
    public double getPercent() {
        return percent;
    }
    @Override
    public void setMaximum(double max) {}
    @Override
    public double getMaximum() {
        return MAX;
    }
    @Override
    public void setMinimum(double min) {}
    @Override
    public double getMinimum() {
        return MIN;
    }
    @Override
    public long getVersion() {
        return version;
    }
    @Override
    public Double getObject() {
        return percent;
    }
    @Override
    public VersionedReference<Double> createReference() {
        return new MultiVersionedReference<>(this, value.createReference());
    }
    
}
