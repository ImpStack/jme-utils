package org.impstack.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author remy
 * @since 8/03/18
 */
public final class SystemUtils {

    /**
     * Returns the OS specific user data path.
     * Windows: %AppData%\
     * Linux: ~/
     * Mac: ~/Library/Application Support/
     * @return the user data path
     */
    public static Path getUserDataPath() {
        if (org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS) {
            return Paths.get(System.getenv("APPDATA"));
        } else if (org.apache.commons.lang3.SystemUtils.IS_OS_LINUX) {
            return Paths.get(System.getProperty("user.home"));
        } else if (org.apache.commons.lang3.SystemUtils.IS_OS_MAC) {
            return Paths.get(System.getProperty("user.home") + "/Library/Application Support");
        } else {
            throw new RuntimeException("Unable to determine Operating system!");
        }
    }
}
