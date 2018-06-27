package org.impstack.jme.lemur;

import com.simsilica.lemur.Panel;
import org.impstack.jme.ApplicationContext;
import org.impstack.util.MathUtils;

/**
 * A GUI method helper class
 * @author remy
 * @since 8/06/18.
 */
public final class GuiHelper {

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

}
