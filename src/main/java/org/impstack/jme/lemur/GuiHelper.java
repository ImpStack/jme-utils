package org.impstack.jme.lemur;

import com.jme3.cursors.plugins.JmeCursor;
import com.simsilica.lemur.Panel;
import org.impstack.jme.ApplicationContext;
import org.impstack.jme.scene.MouseCursor;
import org.impstack.util.MathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A GUI method helper class
 * @author remy
 * @since 8/06/18.
 */
public final class GuiHelper {

    private static final Logger LOG = LoggerFactory.getLogger(GuiHelper.class);

    /**
     * Set the location of the panel to the center of the screen
     * @param panel the panel to center
     */
    public static void center(Panel panel) {
        if (panel == null) {
            return;
        }

        int width = ApplicationContext.INSTANCE.getApplication().getCamera().getWidth();
        int height = ApplicationContext.INSTANCE.getApplication().getCamera().getHeight();
        panel.setLocalTranslation((width / 2) - (panel.getPreferredSize().getX() / 2), (height / 2) + (panel.getPreferredSize().getY() / 2), panel.getLocalTranslation().getZ());
    }

    /**
     * Returns the pixels for the given percentage of the screen width.
     * @param percentage a value between 0 and 1
     * @return the width in pixels
     */
    public static float percentWidth(float percentage) {
        int width = ApplicationContext.INSTANCE.getApplication().getCamera().getWidth();
        return width * MathUtils.range(0, 1, percentage);
    }

    /**
     * Returns the pixels for the given percentage of the screen height.
     * @param percentage a value between 0 and 1
     * @return the height in pixels
     */
    public static float percentHeight(float percentage) {
        int height = ApplicationContext.INSTANCE.getApplication().getCamera().getHeight();
        return height * MathUtils.range(0, 1, percentage);
    }

    /**
     * @return The height/resolution of the display
     */
    public static int getHeight() {
        return ApplicationContext.INSTANCE.getApplication().getCamera().getHeight();
    }

    /**
     * @return The width/resolution of the display
     */
    public static int getWidth() {
        return ApplicationContext.INSTANCE.getApplication().getCamera().getWidth();
    }

    /**
     * Sets the visibility of the mouse cursor
     * @param visible true or false
     */
    public static void setMouseCursorVisible(boolean visible) {
        ApplicationContext.INSTANCE.getApplication().getInputManager().setCursorVisible(visible);
        LOG.trace("Setting mouse cursor visible: {}", visible);
    }

    /**
     * Sets a mouse cursor image. Set the {@link MouseCursor} to null to show the default system cursor.
     * @param cursor the cursor to set, or null for the default system cursor
     */
    public static void setMouseCursor(MouseCursor cursor) {
        if (cursor != null) {
            ApplicationContext.INSTANCE.getApplication().getInputManager().setMouseCursor((JmeCursor) ApplicationContext.INSTANCE.getApplication().getAssetManager().loadAsset(cursor.getPath()));
            LOG.trace("Setting mouse cursor: {}", cursor);
        } else {
            ApplicationContext.INSTANCE.getApplication().getInputManager().setMouseCursor(null);
            LOG.trace("Setting default system cursor");
        }
    }

}
