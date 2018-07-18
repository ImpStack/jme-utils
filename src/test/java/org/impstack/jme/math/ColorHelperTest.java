package org.impstack.jme.math;

import com.jme3.math.ColorRGBA;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ColorHelperTest {

    @Test
    public void hexWithoutAlpha() {
        String hex = "#FFFFFF";
        assertEquals(ColorRGBA.White, ColorHelper.fromHex(hex));
    }

    @Test
    public void hexWithAlpha() {
        String hex = "#FFFFFFFF";
        assertEquals(ColorRGBA.White, ColorHelper.fromHex(hex));
    }

    @Test
    public void hexWithoutHashtag() {
        String hex = "FFFFFF";
        assertEquals(ColorRGBA.White, ColorHelper.fromHex(hex));
    }

    @Test
    public void invalidHex() {
        String hex = "azerty";
        assertNull(ColorHelper.fromHex(hex));
    }

    @Test
    public void hexWithWrongSize() {
        String hex = "#FFFF";
        assertNull(ColorHelper.fromHex(hex));
    }

    @Test
    public void hexAlreadyGammaCorrected() {
        String hex = "#191919";
        assertEquals(new ColorRGBA(0.09803922f, 0.09803922f, 0.09803922f, 1f), ColorHelper.fromHex(hex, true));
    }

    @Test
    public void hexNotGammaCorrected() {
        String hex = "#191919";
        // Gamma correction = rgba values with a power of 2.2
        // ColorHelper.fromHex(hex, true) ^ 2.2 == ColorHelper.fromHex(hex, false)
        assertEquals(new ColorRGBA(0.006040593f, 0.006040593f, 0.006040593f, 1f), ColorHelper.fromHex(hex, false));
    }

    @Test
    public void colorToHex() {
        assertEquals("#000000FF", ColorHelper.toHex(new ColorRGBA(0, 0, 0, 1)));
        assertEquals("#000000FF", ColorHelper.toHex(new ColorRGBA(0, 0, 0, 1), true));
        assertEquals("#000000", ColorHelper.toHex(new ColorRGBA(0, 0, 0, 1), false));
    }

    @Test
    public void colorFromRGBA() {
        assertEquals(new ColorRGBA(0, 0, 0, 1), ColorHelper.fromRGBA(0, 0, 0, 255));
        assertEquals(new ColorRGBA(1, 1, 1, 1), ColorHelper.fromRGBA(255, 255, 255, 255));
    }

    @Test
    public void rgbaWithWrongInput() {
        assertNull(ColorHelper.fromRGBA(256, -1, 3, 9));
    }

}
