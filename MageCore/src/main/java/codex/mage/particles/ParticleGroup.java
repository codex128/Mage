/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.mage.particles;

import codex.mage.particles.emission.volume.EmissionVolume;
import codex.mage.particles.drivers.ParticleDriver;
import codex.mage.particles.emission.volume.EmissionPoint;
import codex.mage.particles.geometry.ParticleGeometry;
import codex.mage.tweens.Value;
import codex.mage.utils.VfxUtils;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.core.VersionedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;

/**
 * Contains a group of particles.
 * 
 * @author codex
 * @param <T> channel of particle data this group uses
 */
public class ParticleGroup <T extends Particle> extends Node implements Iterable<T> {
    
    private ParticleGroup parentGroup;
    private final LinkedList<ParticleGroup> childGroups = new LinkedList<>();
    private final ArrayList<T> particles = new ArrayList<>();
    private final VersionedList<ParticleDriver<T>> drivers = new VersionedList<>();
    private OverflowStrategy<T> overflow = OverflowStrategy.CullNew;
    private EmissionVolume volume = EmissionPoint.Instance;
    private Value<Integer> capacity;
    private Value<Integer> dynamicSizingStep = Value.value(-1);
    private Value<Float> updateSpeed = Value.value(1f);
    private Value<Float> decay = Value.value(1f);
    private Value<Float> delay = Value.value(0f);
    private Value<Boolean> inheritDecayRate = Value.value(false);
    private Value<Boolean> playing = Value.value(true);
    private float time = 0f;
    private boolean worldPlayState = true;
    
    /**
     * Creates a particle group with zero capacity.
     */
    public ParticleGroup() {
        this(ParticleGroup.class.getSimpleName(), Value.value(0));
    }
    
    /**
     * Creates a particle group with zero capacity.
     * 
     * @param name name of this spatial
     */
    public ParticleGroup(String name) {
        this(name, Value.value(0));
    }
    
    /**
     * Creates a particle group with the given capacity.
     * 
     * @param capacity 
     */
    public ParticleGroup(int capacity) {
        this(ParticleGroup.class.getSimpleName(), Value.value(capacity));
    }
    
    /**
     * Creates a particle group with the given capacity.
     * 
     * @param capacity 
     */
    public ParticleGroup(Value<Integer> capacity) {
        this(ParticleGroup.class.getSimpleName(), capacity);
    }
    
    /**
     * Creates a particle group with the given capacity.
     * 
     * @param name name of this spatial
     * @param capacity 
     */
    public ParticleGroup(String name, int capacity) {
        this(name, Value.value(capacity));
    }
    
    /**
     * Creates a particle group with the given capacity.
     * 
     * @param name name of this spatial
     * @param capacity 
     */
    public ParticleGroup(String name, Value<Integer> capacity) {
        super(name);
        this.capacity = capacity;
    }
    
    @Override
    public void updateLogicalState(float tpf) {
        if (parentGroup == null) {
            update(true, tpf, 1);
        }
        super.updateLogicalState(tpf);
    }
    
    @Override
    public int attachChildAt(Spatial spatial, int index) {
        if (spatial instanceof ParticleGroup) {
            ParticleGroup g = (ParticleGroup)spatial;
            g.parentGroup = this;
            childGroups.addLast(g);
        }
        return super.attachChildAt(spatial, index);
    }
    
    @Override
    public Spatial detachChildAt(int index) {
        Spatial spatial = super.detachChildAt(index);
        if (spatial != null && spatial instanceof ParticleGroup) {
            ParticleGroup g = (ParticleGroup)spatial;
            if (g.parentGroup == this) {
                g.parentGroup = null;
                childGroups.remove(g);
            }
        }
        return spatial;
    }
    
    /**
     * Updates this group and child groups.
     * 
     * @param update true if a regular update should be performed
     * @param tpf propogated manipulated time per frame
     * @param d propogated decay rate
     */
    @SuppressWarnings("null")
    protected void update(boolean update, float tpf, float d) {
        trimGroupSize();
        worldPlayState = update && playing.get();
        tpf *= updateSpeed.get();
        if (worldPlayState) {
            time += tpf;
        }
        if (inDelayZone()) {
            tpf = 0;
        }
        d *= decay.get();
        if (worldPlayState) {
            updateParticles(tpf, (inheritDecayRate.get() ? d : decay.get()));
        }
        // update child groups
        for (ParticleGroup g : childGroups) {
            g.update(worldPlayState, tpf, d);
        }
    }
    
    protected void updateParticles(float tpf, float decay) {
        for (ParticleDriver<T> d : drivers) {
            d.updateGroup(this, tpf);
        }
        if (!particles.isEmpty()) for (Iterator<T> it = particles.iterator(); it.hasNext();) {
            T p = it.next();
            for (ParticleDriver<T> d : drivers) {
                d.updateParticle(p, tpf);
            }
            if (!p.decayLife(tpf*decay)) {
                it.remove();
                for (ParticleDriver<T> d : drivers) {
                    d.particleRemoved(this, p);
                }
            }
        }
    }
    
    protected boolean addParticle(T particle) {
        int cap = capacity.get();
        boolean dynamic = dynamicSizingStep.get() >= 0;
        if ((cap > 0 || dynamic) && worldPlayState && particles.add(particle)) {
            int size = particles.size();
            if (size > cap && dynamic) {
                capacity.set(size+dynamicSizingStep.get());
                cap = capacity.get();
            }
            if (size > cap && overflow.removeParticle(this, particle)) {
                return false;
            }
            for (ParticleDriver<T> d : drivers) {
                d.particleAdded(this, particle);
            }
            return true;
        }
        return false;
    }
    
    protected void trimGroupSize() {
        int cap = Math.max(capacity.get(), 0);
        while (cap < particles.size()) {
            overflow.removeParticle(this, null);
        }
    }
    
    /**
     * Adds a particle to this group.
     * <p>
     * The group must be playing (true by default).
     * 
     * @param particle
     * @return true if the particle was successfully added to the group
     */
    public boolean add(T particle) {
        return addParticle(particle);
    }
    
    /**
     * Adds a number of particles generated from the Function.
     * 
     * @param num number of particles to generate and add
     * @param func function to generate particles
     * @return number of particles successfully added
     */
    public int add(int num, Function<Integer, T> func) {
        int added = 0;
        for (; num > 0; num--) {
            T p = func.apply(num);
            if (p != null && addParticle(p)) {
                added++;
            }
        }
        return added;
    }
    
    /**
     * Removes the particle from the group.
     * <p>
     * The group must be playing (true by default).
     * 
     * @param particle
     * @return true if the particle existed in the group
     */
    public boolean remove(T particle) {
        if (worldPlayState && particles.remove(particle)) {
            for (ParticleDriver<T> d : drivers) {
                d.particleRemoved(this, particle);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Removes the particle at the index from the group.
     * 
     * @param i index to remove from
     * @return removed particle
     */
    public T remove(int i) {
        T p = null;
        if (worldPlayState) {
            p = particles.remove(i);
            if (p != null) for (ParticleDriver<T> d : drivers) {
                d.particleRemoved(this, p);
            }
        }
        return p;
    }
    
    /**
     * Clears all particles from this group.
     * <p>
     * The group must be playing (true by default).
     */
    public void clearAllParticles() {
        if (worldPlayState) {
            particles.clear();
        }
    }
    
    /**
     * Add a particle driver.
     * <p>
     * The driver will be added to the front of the driver stack if indicated.
     * Otherwise, the driver will be added to the rear.
     * 
     * @param driver
     * @param appendToFront 
     */
    public void addDriver(ParticleDriver<T> driver, boolean appendToFront) {
        if (appendToFront) {
            drivers.add(0, driver);
        } else {
            drivers.add(driver);
        }
        driver.assignedToGroup(this);
    }
    
    /**
     * Adds a particle driver to the end of the driver stack.
     * 
     * @param driver 
     */
    public void addDriver(ParticleDriver<T> driver) {
        addDriver(driver, false);
    }
    
    /**
     * Removes the driver from the driver stack.
     * 
     * @param driver 
     */
    public void removeDriver(ParticleDriver<T> driver) {
        drivers.remove(driver);
        driver.removedFromGroup(this);
    }
    
    /**
     * Gets the first driver of the given class type.
     * 
     * @param <R>
     * @param type
     * @return first driver of the given class type or null
     */
    public <R extends ParticleDriver<T>> R getDriver(Class<R> type) {
        for (ParticleDriver<T> driver : drivers) {
            if (type.isAssignableFrom(driver.getClass())) {
                return (R)driver;
            }
        }
        return null;
    }
    
    /**
     * Removes and returns the first driver of the given class type.
     * 
     * @param <R>
     * @param type
     * @return first driver of the given class type or null
     */
    public <R extends ParticleDriver<T>> R removeDriver(Class<R> type) {
        for (Iterator<ParticleDriver<T>> it = drivers.iterator(); it.hasNext();) {
            ParticleDriver<T> driver = it.next();
            if (type.isAssignableFrom(driver.getClass())) {
                it.remove();
                driver.removedFromGroup(this);
                return (R)driver;
            }
        }
        return null;
    }
    
    /**
     * Removes all drivers from the driver stack.
     */
    public void clearAllDrivers() {
        for (ParticleDriver<T> d : drivers) {
            d.removedFromGroup(parentGroup);
        }
        drivers.clear();
    }
    
    /**
     * Set the maximum number of particles this group can handle.
     * <p>
     * Changing the capacity outside initialization can be expensive, since
     * particle geometries have to recalculate their buffers to cope with
     * the promise of more particles.
     * <p>
     * It is suggested to set the capacity using a constructor.
     * <p>
     * default=0 (no particle support)
     * 
     * @param capacity 
     * @see #ParticleGroup(int)
     * @see #ParticleGroup(String, int)
     */
    public void setCapacity(Value<Integer> capacity) {
        this.capacity = capacity;
    }
    
    /**
     * Sets the strategy for when an added particle would make
     * the group's size exceed its capacity.
     * <p>
     * default={@link OverflowStrategy#CullNew}
     * 
     * @param overflow 
     */
    public void setOverflowStrategy(OverflowStrategy<T> overflow) {
        this.overflow = overflow;
    }
    
    /**
     * Set the default emission volume used by this particle group and subsiquent drivers.
     * <p>
     * default={@link EmissionPoint}
     * 
     * @param volume emission volume (not null)
     */
    public void setVolume(EmissionVolume volume) {
        assert volume != null : "Emission volume cannot be null";
        this.volume = volume;
    }
    
    /**
     * Sets the rate at which particles update.
     * <p>
     * This is propogated to drivers through {@code tpf}.<br>
     * default=1.0
     * 
     * @param speed 
     */
    public void setUpdateSpeed(Value<Float> speed) {
        this.updateSpeed = speed;
    }
    
    /**
     * Sets the rate at which particles lose life.
     * <p>
     * Decay=1 means particles lose one "life point" each second.
     * Decay=2, particles lose two points each second.
     * A decay of zero allows particles to exist indefinitely.
     * <p>
     * default=1.0
     * 
     * @param decay 
     */
    public void setDecayRate(Value<Float> decay) {
        this.decay = decay;
    }
    
    /**
     * Sets the initial delay in seconds.
     * <p>
     * default=0.0 (no delay)
     * 
     * @param delay 
     */
    public void setInitialDelay(Value<Float> delay) {
        this.delay = delay;
    }
    
    /**
     * If true, world decay rate will be used to decay particles instead of local decay rate.
     * <p>
     * default=false
     * 
     * @param inheritDecayRate 
     */
    public void setInheritDecayRate(Value<Boolean> inheritDecayRate) {
        this.inheritDecayRate = inheritDecayRate;
    }
    
    /**
     * Sets this group to dynamically resize its capacity to accept more particles.
     * <p>
     * The integer value indicates how much above the necessary capacity
     * the group's capacity is set to. A dynamic sizing step of 3 would resize
     * the group to be 3 greater than the current number of particles. Resizing
     * only occurs when the number of particles would exceed the group's capacity.
     * <p>
     * This feature can potentially put more strain on the system, because particle
     * geometries have to create new buffers to match the changing capacity. It is
     * recommended to set the dynamic resizing step as high as reasonably possible
     * in order to cut down of the number of reallocations the geometries must do.
     * That is, if this feature must be used at all.
     * <p>
     * To disable this feature, set the dynamic sizing step to less than zero.
     * <p>
     * default={@code -1} (disabled)
     * 
     * @param step 
     */
    public void setDynamicSizingStep(Value<Integer> step) {
        this.dynamicSizingStep = step;
    }
    
    /**
     * Sets this group's play state to true.
     * <p>
     * default=play
     */
    public void play() {
        playing.set(true);
    }
    
    /**
     * Sets this group's play state to false.
     * <p>
     * Drivers will not be updated in this state. New particles will be denied.
     * Existing particles cannot be removed.
     * <p>
 Since this group's particles cannot, and should not, change while
 this group is paused, any particle geometries using this group do not need to update
 their meshes in this state.
 <p>
 This is an extreme method for making the simulation stop, since it stops everything.
 A more conservative approach is to set the update speed to zero. Then this group,
 child groups, and subsequent drivers will still get updated, but they will not progress.
 <p>
     * Call {@link #play()} to undo this.<br>
     * default=play
     * 
     * @see ParticleGeometry#setForceMeshUpdate(boolean)
     */
    public void pause() {
        playing.set(false);
    }
    
    /**
     * Reverses the play state.
     * <p>
     * If playing (true), the play state is set to false.<br>
     * If paused (false), the play state is set to true.
     * 
     * @return the new play state
     */
    public boolean flipPlayState() {
        playing.set(!playing.get());
        return playing.get();
    }
    
    /**
     * Directly sets the local play state.
     * 
     * @param playing 
     */
    public void setLocalPlayState(Value<Boolean> playing) {
        this.playing = playing;
    }
    
    /**
     * Resets the group by removing all particles, reseting the simulation time,
     * and reseting child particle groups.
     * <p>
     * Drivers are also notified, so they can reset themselves accordingly.
     * <br>Will not reset if paused.
     */
    public void reset() {
        reset(true);
    }
    
    /**
     * Resets the group by removing all particles, reseting the simulation time,
     * and (optionally) reseting child particle groups.
     * <p>
     * Drivers are also notified, so they can reset themselves accordingly.
     * <br>Will not reset if paused.
     * 
     * @param resetChildren if true, child particle groups will also be reset
     */
    public void reset(boolean resetChildren) {
        if (worldPlayState) {
            for (T p : particles) {
                for (ParticleDriver<T> d : drivers) {
                    d.particleRemoved(this, p);
                }
            }
            particles.clear();
            time = 0;
            for (ParticleDriver<T> d : drivers) {
                d.groupReset(this);
            }
            if (resetChildren) for (ParticleGroup g : childGroups) {
                g.reset(true);
            }
        }
    }
    
    /**
     * Gets the particle at the specified index.
     * 
     * @param i index between 0 (inclusive) and group size (exclusive)
     * @return particle at index
     */
    public T get(int i) {
        return particles.get(i);
    }
    
    /**
     * Gets a random particle from this group.
     * 
     * @return random particle, or null if none available
     */
    public T getRandom() {
        if (!particles.isEmpty()) {
            return particles.get(VfxUtils.gen.nextInt(particles.size()));
        } else {
            return null;
        }
    }
    
    /**
     * Gets the list of particles this group controls.
     * <p>
     * Note: {@code ParticleGroup} is iterable in this respect.
     * <p>
     * <em>Do not modify the returned list!</em>
     * 
     * @return 
     */
    public ArrayList<T> getParticleList() {
        return particles;
    }
    
    /**
     * 
     * @return 
     * @see #setOverflowStrategy(OverflowStrategy)
     */
    public OverflowStrategy<T> getOverflowStrategy() {
        return overflow;
    }
    
    /**
     * 
     * @return 
     * @see #setVolume(codex.mage.particles.drivers.emission.EmissionVolume)
     */
    public EmissionVolume getVolume() {
        return volume;
    }
    
    /**
     * Gets the list of child particle group's this group is a parent of.
     * <p>
     * <em>Do not modify the returned list!</em>
     * 
     * @return 
     */
    public LinkedList<ParticleGroup> getChildGroupList() {
        return childGroups;
    }
    
    /**
     * Gets the list of drivers controlling this particle group.
     * <p>
     * <em>Do not modify the returned list!</em>
     * 
     * @return 
     */
    public VersionedList<ParticleDriver<T>> getDriverList() {
        return drivers;
    }
    
    /**
     * Get the parent particle group of this group.
     * 
     * @return 
     */
    public ParticleGroup getParentGroup() {
        return parentGroup;
    }
    
    /**
     * Returns true if this group has no particles.
     * 
     * @return 
     */
    public boolean isEmpty() {
        return particles.isEmpty();
    }
    
    /**
     * Returns true if this group is empty and all child groups are
     * finished by this same standard.
     * 
     * @return 
     */
    public boolean isFinished() {
        if (!isEmpty()) return false;
        for (ParticleGroup child : childGroups) {
            if (!child.isFinished()) return false;
        }
        return true;
    }
    
    /**
     * Returns true if the number of particles in this group is
     * equal to its capacity.
     * 
     * @return 
     */
    public boolean isFull() {
        return particles.size() >= capacity.get();
    }
    
    /**
     * Returns this group's local play state.
     * <p>
     * true=playing<br>
     * false=paused
     * 
     * @return local play state
     */
    public Value<Boolean> getLocalPlayState() {
        return playing;
    }
    
    /**
     * Returns true if the current time is within this group's delay zone.
     * 
     * @return 
     */
    public boolean inDelayZone() {
        return time < delay.get();
    }
    
    /**
     * Returns the number of particles currently part of this group.
     * 
     * @return 
     */
    public int size() {
        return particles.size();
    }
    
    /**
     * Returns the maximum number of particles that can be part of this group.
     * 
     * @return 
     */
    public Value<Integer> capacity() {
        return capacity;
    }
    
    /**
     * 
     * @return 
     * @see #setUpdateSpeed(float)
     */
    public Value<Float> getUpdateSpeed() {
        return updateSpeed;
    }
    
    /**
     * 
     * @return 
     * @see #setDecayRate(float)
     */
    public Value<Float> getDecayRate() {
        return decay;
    }
    
    /**
     * 
     * @return 
     * @see #setInitialDelay(float)
     */
    public Value<Float> getInitialDelay() {
        return delay;
    }
    
    /**
     * 
     * @return 
     * @see #setInheritDecayRate(boolean)
     */
    public Value<Boolean> isInheritDecayRate() {
        return inheritDecayRate;
    }
    
    /**
     * 
     * @return 
     * @see #setDynamicSizingStep(int)
     */
    public Value<Integer> getDynamicSizingStep() {
        return dynamicSizingStep;
    }
    
    /**
     * Fetches the world update speed of this group.
     * 
     * @return 
     */
    public float getWorldUpdateSpeed() {
        if (parentGroup != null) {
            return parentGroup.getWorldUpdateSpeed() * updateSpeed.get();
        } else {
            return updateSpeed.get();
        }
    }
    
    /**
     * Fetches the world decay rate of this group.
     * 
     * @return 
     */
    public float getWorldDecayRate() {
        if (parentGroup != null) {
            return parentGroup.getWorldDecayRate() * decay.get();
        } else {
            return decay.get();
        }
    }
    
    /**
     * Fetches the total number of seconds which this group must
     * wait from particle system start to begin its simulation.
     * 
     * @return 
     */
    public float getWorldInitialDelay() {
        if (parentGroup != null) {
            return parentGroup.getWorldInitialDelay() + delay.get();
        } else {
            return delay.get();
        }
    }
    
    /**
     * Gets the world play state of this group.
     * <p>
     * If this or any parent group is <em>not</em> playing, the world
     * play state is false.
     * 
     * @return 
     */
    public boolean getWorldPlayState() {
        return worldPlayState;
    }
    
    /**
     * Gets the time since beginning the simulation (after world delay).
     * 
     * @return 
     */
    public float getTime() {
        return Math.max(time-delay.get(), 0f);
    }
    
    /**
     * Get the time since beginning update (before world delay).
     * 
     * @return 
     */
    public float getRawTime() {
        return time;
    }
    
    @Override
    public Iterator<T> iterator() {
        return particles.iterator();
    }
    
}
