/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage;

import codex.mage.resources.VfxResourceManager;
import com.jme3.app.Application;

/**
 *
 * @author codex
 */
public class VfxGlobals {
    
    private static VfxGlobals instance;
    
    public static void initialize(Application app) {
        instance = new VfxGlobals(app);
    }
    
    public static boolean isInitialized() {
        return instance != null;
    }
    
    public static VfxGlobals getInstance() {
        return instance;
    }
    
    private VfxResourceManager resources;
    
    private VfxGlobals(Application app) {
        resources = new VfxResourceManager(true);
        app.getStateManager().attach(resources);
    }
    
    /**
     * Gets the resource manager.
     * 
     * @return 
     */
    public VfxResourceManager getResources() {
        return resources;
    }
    
    /**
     * Creates a resource using the resource manager.
     * 
     * @param <T>
     * @param name
     * @param type
     * @return 
     * @see VfxResourceManager#createResource(String, Class)
     */
    public <T> T createResource(String name, Class<T> type) {
        return resources.createResource(name, type);
    }
    
}
