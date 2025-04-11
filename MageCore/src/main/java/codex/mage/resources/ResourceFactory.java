/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.resources;

import com.jme3.asset.AssetManager;

/**
 *
 * @author codex
 * @param <T>
 */
public interface ResourceFactory <T> {
    
    public static final String TRI_PARTICLE_MAT = "TriParticleMat";
    public static final String POINT_PARTICLE_MAT = "PointParticleMat";
    public static final String TRAIL_PARTICLE_MAT = "TrailingParticleMat";
    
    public T createResource(AssetManager assetManager, VfxResourceManager resources);
    
}
