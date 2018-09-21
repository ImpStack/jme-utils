package org.impstack.jme.state;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.input.controls.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * There is a memory-leak in the original {@link ScreenshotAppState}, it should only be attached once! The state
 * doesn't correctly cleans itself when detaching.
 * <p>
 * In the constructor the full path to the folder should be specified, and the prefix of the screenshot.
 * The screenshots are stored in the given folder, with filename as prefix and '-yyyyMMddTHHmmss' as suffix.
 * <p>
 * A screenshot can be taken by calling the {@link #takeScreenshot()} method or by pressing the key trigger.
 * By default this is the {@link com.jme3.input.KeyInput#KEY_SYSRQ}, but the trigger can be overriden by calling the
 * {@link #setTakeScreenshotTrigger(Trigger)} method.
 * <p>
 * the setFileName method is called each time a screenshot is taken, because we need to override the default filename
 * with the current timestamp.
 */
public class ScreenshotState extends ScreenshotAppState {

    private static final Logger LOG = LoggerFactory.getLogger(ScreenshotState.class);
    private static final DateFormat DF = new SimpleDateFormat("yyyyMMdd'T'HHmmss");

    private final String filename;
    private Application application;

    public ScreenshotState(Path path, String filename) {
        super();
        this.filename = filename;
        this.setIsNumbered(false);
        setFilePath(path.toString().endsWith(File.separator) ? path.toString() : path.toString() + File.separator);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.application = app;
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
        super.onAction(name, value, tpf);
        if (value) {
            setFileName(createFilename());
        }
    }

    @Override
    public void takeScreenshot() {
        super.takeScreenshot();
        setFileName(createFilename());
    }

    @Override
    public void setFilePath(String filePath) {
        super.setFilePath(filePath.endsWith(File.separator) ? filePath : filePath + File.separator);
    }

    public void takeScreenshot(String filename) {
        super.takeScreenshot();
        setFileName(filename);
    }

    public void setTakeScreenshotTrigger(Trigger trigger) {
        application.getInputManager().deleteMapping("ScreenShot");
        application.getInputManager().addMapping("ScreenShot", trigger);
        application.getInputManager().addListener(this, "ScreenShot");
    }

    private String createFilename() {
        return filename + "-" + DF.format(new Date());
    }
}
