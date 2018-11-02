package org.impstack.jme.control;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import org.impstack.jme.scene.SpatialUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * A control for playing animations on a spatial.
 * Use {@link #setAnimation(String, LoopMode, float, float)} to specify the animation to play.
 */
public class AnimationControl extends AbstractControl implements AnimEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(AnimationControl.class);

    private AnimControl animControl;
    private AnimChannel mainChannel;

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            animControl = SpatialUtils.findControl(spatial, AnimControl.class);
            if (animControl == null) {
                throw new IllegalArgumentException("AnimControl not found on spatial " + spatial);
            }
            mainChannel = animControl.createChannel();
            animControl.addListener(this);
        }
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }

    @Override
    protected void controlUpdate(float tpf) {
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        animControl.setEnabled(enabled);
    }

    public void setAnimation(String animation, LoopMode loopMode, float blendTime, float speed) {
        // no animation is playing or a different animation is playing
        if (mainChannel.getAnimationName() == null || !mainChannel.getAnimationName().equals(animation)) {
            try {
                mainChannel.setAnim(animation, blendTime);
                mainChannel.setLoopMode(loopMode);
                mainChannel.setSpeed(speed);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        } else {
            // we assume the same animation is playing, just update the speed and loopmode
            try {
                mainChannel.setSpeed(speed);
                mainChannel.setLoopMode(loopMode);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    /**
     * @return The names of all animations that this <code>AnimControl</code> can play.
     */
    public Collection<String> getAvailableAnimations() {
        return animControl.getAnimationNames();
    }

    public AnimControl getAnimControl() {
        return animControl;
    }

    @Override
    public String toString() {
        return "AnimationControl{" +
                "animControl=" + animControl +
                ", mainChannel=" + mainChannel +
                '}';
    }
}
