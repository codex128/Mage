/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.resources;

import codex.boost.GameAppState;
import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.texture.Texture;
import java.util.HashMap;

/**
 *
 * @author codex
 */
public class VfxResourceManager extends GameAppState {
    
    public static final String TRI_PARTICLE_MATDEF = "TriParticleMatdef";
    public static final String POINT_PARTICLE_MATDEF = "TriParticleMatdef";
    public static final String TRAIL_PARTICLE_MATDEF = "TrailParticleMatdef";
    
    private String assetRoot = "";
    private final HashMap<String, String> paths = new HashMap<>();
    private final HashMap<String, ResourceFactory> factories = new HashMap<>();
    
    public VfxResourceManager(boolean assignDefaults) {
        if (assignDefaults) {
            assignDefaultMappings();
        }
    }
    
    @Override
    protected void init(Application app) {}
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    
    private void assignDefaultMappings() {
        
        setAssetPath(TRI_PARTICLE_MATDEF, "MatDefs/particles.j3md");
        setAssetPath(POINT_PARTICLE_MATDEF, "MatDefs/particles.j3md");
        setAssetPath(TRAIL_PARTICLE_MATDEF, "Matdefs/trail.j3md");
        
        assignFactory(ResourceFactory.TRI_PARTICLE_MAT, (assets, resources) -> {
            return new Material(assets, resources.getAssetPath(TRI_PARTICLE_MATDEF));
        });
        assignFactory(ResourceFactory.POINT_PARTICLE_MAT, (assets, resources) -> {
            Material mat = new Material(assets, resources.getAssetPath(POINT_PARTICLE_MATDEF));
            mat.setBoolean("PointSprite", true);
            return mat;
        });
        assignFactory(ResourceFactory.TRAIL_PARTICLE_MAT, (assets, resources) -> {
            Material mat = new Material(assetManager, resources.getAssetPath(TRAIL_PARTICLE_MATDEF));
            mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            return mat;
        });
        
    }
    
    /**
     * Sets the root path all asset paths are relative to.
     * 
     * @param assetRoot 
     */
    public void setAssetRoot(String assetRoot) {
        this.assetRoot = assetRoot;
    }
    
    /**
     * Gets the root path all asset paths are relative to.
     * 
     * @return 
     */
    public String getAssetRoot() {
        return assetRoot;
    }
    
    /**
     * Registers the asset path under the key.
     * 
     * @param key
     * @param path
     * @return asset path previously registered under the key
     */
    public String setAssetPath(String key, String path) {
        return paths.put(key, path);
    }
    
    /**
     * Get the asset path registered under the key.
     * 
     * @param key
     * @return asset path combined with the root asset path
     */
    public String getAssetPath(String key) {
        String path = paths.get(key);
        if (path == null) {
            throw new NullPointerException("Path does not exist for \""+key+"\"");
        }
        return assetRoot+path;
    }
    
    /**
     * Assigns the factory under the given name.
     * 
     * @param name registry name
     * @param factory factory to register, or null to remove the currently registered factory
     * @return factory previously registered under the given name
     */
    public ResourceFactory assignFactory(String name, ResourceFactory factory) {
        if (factory != null) {
            return factories.put(name, factory);
        } else {
            return factories.remove(name);
        }
    }
    
    /**
     * Creates resource using the {@link ResourceFactory} assigned to
     * the given name.
     * 
     * @param <T> resource type
     * @param name factory registry name
     * @param type resource class type
     * @return created resource of the given type
     * @throws NullPointerException if state has not been initialized
     * @throws NullPointerException if no factory is registered under the name
     * @throws ClassCastException if resulting resource is not compatible with the given type
     */
    public <T> T createResource(String name, Class<T> type) {
        if (!isInitialized()) {
            throw new NullPointerException("Cannot create resources before initialization.");
        }
        ResourceFactory f = factories.get(name);
        if (f == null) {
            throw new NullPointerException("Resource factory for \""+name+"\" does not exist.");
        }
        Object res = f.createResource(assetManager, this);
        if (!type.isAssignableFrom(res.getClass())) {
            throw new ClassCastException("Created resource of type "+res.getClass().getName()
                    +" is not compatible with "+type.getName());
        }
        return (T)res;
    }
    
}
