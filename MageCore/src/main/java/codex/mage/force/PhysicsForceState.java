/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.force;

import codex.boost.GameAppState;
import com.jme3.app.Application;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;

/**
 *
 * @author codex
 */
public class PhysicsForceState extends GameAppState {
    
    private PhysicsSpace physicsSpace;
    private ForceSpace forceSpace;
    
    public PhysicsForceState() {}
    public PhysicsForceState(PhysicsSpace space) {
        this.physicsSpace = space;
    }
    
    @Override
    protected void init(Application app) {
        if (physicsSpace == null) {
            var bullet = getState(BulletAppState.class);
            if (bullet == null) {
                throw new NullPointerException("Unable to locate BulletAppState. Please manually specify the PhysicsSpace.");
            }
            physicsSpace = bullet.getPhysicsSpace();
        }
    }
    @Override
    protected void cleanup(Application app) {}
    @Override
    protected void onEnable() {}
    @Override
    protected void onDisable() {}
    @Override
    public void update(float tpf) {
        forceSpace.applyToPhysicsSpace(physicsSpace, tpf);
    }
    
    public ForceSpace getForceSpace() {
        return forceSpace;
    }
    
}
