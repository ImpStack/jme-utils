package org.impstack.util;

import org.junit.Assert;
import org.junit.Test;

public class MathUtilsTest {

    @Test
    public void testRange() {
        Assert.assertEquals(1, MathUtils.range(1, 5, 0), 0);
        Assert.assertEquals(5, MathUtils.range(1, 5, 6), 0);
        Assert.assertEquals(2, MathUtils.range(1, 5, 2), 0);
        Assert.assertEquals(-5, MathUtils.range(-5, -1, -10), 0);
        Assert.assertEquals(-1, MathUtils.range(-5, -1, 1), 0);
        Assert.assertEquals(-2, MathUtils.range(-5, -1, -2), 0);
    }
}
