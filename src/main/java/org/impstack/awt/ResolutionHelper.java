package org.impstack.awt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public enum ResolutionHelper {
    INSTANCE;

    private static final Logger LOG = LoggerFactory.getLogger(ResolutionHelper.class);

    public List<Resolution> getResolutions() {
        List<Resolution> resolutions = new ArrayList<>();
        Arrays.stream(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayModes()).forEach(mode -> {
            resolutions.add(new Resolution(mode.getWidth(), mode.getHeight(), mode.getBitDepth()));
        });
        return resolutions;
    }

    /**
     * Picks the first supported HD resolution.
     * @return the resolution, or the current screen resolution
     */
    public Resolution getBestWindowSize() {
        DisplayMode displayMode = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
        return getResolutions().stream()
                .filter(r -> r.getBpp() == displayMode.getBitDepth() && r.getWidth() >= 1280)
                .min(Comparator.comparingInt(Resolution::getWidth))
                .orElse(new Resolution(displayMode.getWidth(), displayMode.getHeight(), displayMode.getBitDepth()));
    }

}