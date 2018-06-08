package org.impstack.jme.lemur;

import com.simsilica.lemur.Panel;
import org.impstack.jme.ApplicationContext;

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

}
