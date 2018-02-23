package org.impstack.awt;

public class Resolution {

    private final int width;
    private final int height;
    private final int bpp;

    public Resolution(int width, int height, int bpp) {
        this.width = width;
        this.height = height;
        this.bpp = bpp;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getBpp() {
        return bpp;
    }

    @Override
    public String toString() {
        return width + "x" + height + ":" + bpp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resolution that = (Resolution) o;

        if (width != that.width) return false;
        if (height != that.height) return false;
        return bpp == that.bpp;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        result = 31 * result + bpp;
        return result;
    }

    /**
     * Parses a string in the format: widthxheight:depth to a resolution object.
     * eg. 1280x800:24
     * @param resolution string representation of a resolution
     * @return resolution
     */
    public static Resolution fromString(String resolution) {
        if (!resolution.contains("x") || !resolution.contains(":")) {
            throw new RuntimeException("Unable to parse the given resolution: " + resolution);
        }
        String width = resolution.substring(0, resolution.indexOf("x"));
        String height = resolution.substring(resolution.indexOf("x") + 1, resolution.indexOf(":"));
        String bpp = resolution.substring(resolution.indexOf(":") + 1);
        return new Resolution(Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(bpp));
    }

}
