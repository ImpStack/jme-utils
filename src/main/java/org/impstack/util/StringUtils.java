package org.impstack.util;

import org.apache.commons.text.WordUtils;

import java.util.Collection;
import java.util.stream.Collectors;

public final class StringUtils {
    /**
     * Converts a string to camelcase.
     * toCamelCase(null) : ""
     * toCamelCase("hello") : "hello"
     * toCamelCase("my name is Remy") : "myNameIsRemy"
     * toCamelCase("I am FINE") : "iAmFine"
     * toCamelCase("     "): ""
     * @param string the input string
     * @return camelcase representation of the given string
     */
    public static String toCamelCase(String string) {
        String s = WordUtils.capitalizeFully(string);
        if (s != null) {
            s = s.replaceAll("\\s", "");
            return s.length() > 1 ? s.substring(0, 1).toLowerCase() + s.substring(1) : s.toLowerCase();
        }
        return "";
    }

    /**
     * Returns the given string, with a maximum of {@code max} characters.
     * Characters after the {@code max} will be stripped
     * @param string the input string
     * @param max the maximum allowed length of the output string
     * @return stripped string
     */
    public static String max(String string, int max) {
        return string != null && string.length() > max ? string.substring(0, max) : string;
    }

    /**
     * Returns a comma separated concatenated string of all the objects in the collection
     * @param collection collection to concatenate
     * @param <T> the type of objects in the collection
     * @return concatenated string
     */
    public static <T> String toString(Collection<T> collection) {
        return toString(collection, ",");
    }

    /**
     * Returns a concatenated string of all the objects in the collection with the given delimiter
     * @param collection collection to concatenate
     * @param <T> the type of objects in the collection
     * @param delimiter the delimiter
     * @return concatenated string
     */
    public static <T> String toString(Collection<T> collection, CharSequence delimiter) {
        return collection != null ? collection.stream().map(Object::toString).collect(Collectors.joining(delimiter)) : null;
    }

}
