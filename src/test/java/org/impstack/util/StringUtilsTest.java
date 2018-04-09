package org.impstack.util;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void camelCaseTest() {
        Assert.assertEquals("", StringUtils.toCamelCase(null));
        Assert.assertEquals("hello", StringUtils.toCamelCase("  hello"));
        Assert.assertEquals("myNameIsRemy", StringUtils.toCamelCase("my   name is Remy"));
        Assert.assertEquals("iAmFine", StringUtils.toCamelCase("I am FINE   "));
        Assert.assertEquals("", StringUtils.toCamelCase("      "));
    }

    @Test
    public void maxTest() {
        Assert.assertEquals(null, StringUtils.max(null, 5));
        Assert.assertEquals("abc", StringUtils.max("abc", 5));
        Assert.assertEquals("abc", StringUtils.max("abcdefghi", 3));
    }

}
