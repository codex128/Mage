/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.filter;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;

/**
 * Simulates rain effects. WIP.
 * 
 * @author codex
 */
public class RainFilter extends Filter {

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        
    }
    @Override
    protected Material getMaterial() {
        return material;
    }
    @Override
    protected void postQueue(RenderQueue queue) {
        // render portal depth
    }
    
}
