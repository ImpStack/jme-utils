package org.impstack.jme.config;

import com.jme3.system.AppSettings;
import org.impstack.awt.Resolution;
import org.impstack.awt.ResolutionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.Map;

public final class ApplicationSettingsFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationSettingsFactory.class);

    public static final String NAME = "name";
    public static final String VERSION = "version";
    public static final String BUILD = "build";
    public static final String TITLE = "title";
    public static final String VSYNC = "vsync";
    public static final String GAMMA_CORRECTION = "gammaCorrection";
    public static final String FRAMERATE = "framerate";
    public static final String ANTI_ALIASING = "antiAliasing";
    public static final String RENDERER = "renderer";
    public static final String RESOLUTION = "resolution";
    public static final String FULLSCREEN = "fullscreen";

    private static final boolean VSYNC_DEFAULT = true;
    private static final boolean GAMMA_CORRECTION_DEFAULT = true;
    private static final int FRAMERATE_DEFAULT = 60;
    private static final int ANTI_ALIASING_DEFAULT = 2;
    private static final String RENDERER_DEFAULT = AppSettings.LWJGL_OPENGL2;
    private static final String RESOLUTION_DEFAULT = ResolutionHelper.INSTANCE.getBestWindowedResolution().toString();
    private static final boolean FULLSCREEN_DEFAULT = false;

    public static AppSettings getAppSettings() {
        return getAppSettings(null);
    }

    public static AppSettings getAppSettings(Map<String, String> overrides) {
        LOG.info("Loading application settings");
        if (overrides != null && !overrides.isEmpty()) {
            LOG.info("Using overrides: {}", overrides);
        }
        LOG.debug("Supported resolutions: {}", ResolutionHelper.INSTANCE.getResolutions());

        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        DisplayMode displayMode = graphicsDevice.getDisplayMode();

        AppSettings settings = new AppSettings(true);
        settings.setFrequency(displayMode.getRefreshRate());
        settings.setTitle(getTitle());
        settings.setGammaCorrection(ApplicationProperties.INSTANCE.get(GAMMA_CORRECTION, GAMMA_CORRECTION_DEFAULT));
        settings.setFrameRate(ApplicationProperties.INSTANCE.get(FRAMERATE, FRAMERATE_DEFAULT));
        settings.setVSync(ApplicationProperties.INSTANCE.get(VSYNC, VSYNC_DEFAULT));
        settings.setSamples(ApplicationProperties.INSTANCE.get(ANTI_ALIASING, ANTI_ALIASING_DEFAULT));
        settings.setRenderer(ApplicationProperties.INSTANCE.get(RENDERER, RENDERER_DEFAULT));
        if (overrides != null && overrides.containsKey(FULLSCREEN)) {
            settings.setFullscreen(Boolean.parseBoolean(overrides.get(FULLSCREEN)));
        } else {
            settings.setFullscreen(ApplicationProperties.INSTANCE.get(FULLSCREEN, FULLSCREEN_DEFAULT));
        }

        Resolution resolution = Resolution.fromString(ApplicationProperties.INSTANCE.get(RESOLUTION, RESOLUTION_DEFAULT));
        if (overrides != null && overrides.containsKey(RESOLUTION)) {
            resolution = Resolution.fromString(overrides.get(RESOLUTION));
        }
        settings.setWidth(resolution.getWidth());
        settings.setHeight(resolution.getHeight());
        settings.setBitsPerPixel(resolution.getBpp());

        LOG.debug("Settings: {}", settings);
        return settings;
    }

    /**
     * The title is either the 'title' field in the application.properties file, or a concatenation of the name,
     * version and build
     *
     * @return the title
     */
    private static String getTitle() {
        String title = ApplicationProperties.INSTANCE.get(TITLE, "");
        if (title.isEmpty()) {
            String name = ApplicationProperties.INSTANCE.get(NAME, "Main");
            String version = ApplicationProperties.INSTANCE.get(VERSION, "0.1");
            String build = ApplicationProperties.INSTANCE.get(BUILD, "");
            title = build.isEmpty() ? name + " - " + version : name + " - " + version + " build " + build;
        }
        return title;
    }

}
