package org.impstack.util;

/**
 * @author remy
 * @since 27/06/18
 */
public final class MathUtils {

    /**
     * Returns the value in the desired range.
     * @param min the minimum value
     * @param max the maximum value
     * @param value the value
     * @return min if the value < min, max if the value > max, or the value
     */
    public static float range(float min, float max, float value) {
        return Math.min(Math.max(min, value), max);
    }
}
