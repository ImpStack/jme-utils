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
     * @return min if the value &lt; min, max if the value &gt; max, or the value
     */
    public static float range(float min, float max, float value) {
        return Math.min(Math.max(min, value), max);
    }

    /**
     * Checks if the given value is in the supplied range
     * @param min the minimum value
     * @param max the maximum value
     * @param value the value
     * @return true if the value &gte; min and &lte; max.
     */
    public static boolean inRange(float min, float max, float value) {
        return min <= value && value <= max;
    }
}
