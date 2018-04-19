package org.impstack.awt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ResolutionHelper {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(ResolutionHelper.class);

    private final double RATIO_16_9 = 16 / (double) 9;
    private final double RATIO_16_10 = 1.6;

    public List<Resolution> getResolutions() {
        List<Resolution> resolutions = new ArrayList<>();
        Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayModes()).forEach(mode -> {
            resolutions.add(new Resolution(mode.getWidth(), mode.getHeight(), mode.getBitDepth()));
        });
        return resolutions;
    }

    /**
     * Picks an appropriate resolution for the application window, considering the current aspect ratio.
     * For a ratio of 16/9, the HD standard (1280x720) will be picked.
     * For a ratio of 16/10, the WXGA standard (1280x800) will be picked.
     * If the ratio isn't 16/9 or 16/10, the resolution is calculated based on the ratio of the current screen.
     * @return the resolution
     */
    public Resolution getBestWindowedResolution() {
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        double ratio = displayMode.getWidth() / (double) displayMode.getHeight();
        if ( RATIO_16_9 == ratio ) {
            LOG.debug("Found screen ratio {}, setting resolution to {}", "16/9", "1280x720");
            return new Resolution(1280, 720, displayMode.getBitDepth() );
        } else if ( RATIO_16_10 == ratio ) {
            LOG.debug("Found screen ratio {}, setting resolution to {}", "16/10", "1280x800");
            return new Resolution(1280, 800, displayMode.getBitDepth() );
        } else {
            LOG.warn("Did not found a standard screen ration (16/9, 16/10), defaulting resolution to {}x{}", 1280, (int) (1280 / ratio));
            return new Resolution(1280, (int) (1280 / ratio), displayMode.getBitDepth());
        }
    }

}