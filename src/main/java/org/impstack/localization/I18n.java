package org.impstack.localization;

import org.impstack.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * A singleton class that handles a 'translations/gui' resource bundle.
 */
public enum I18n {
    INSTANCE;

    private final Logger LOG = LoggerFactory.getLogger(I18n.class.getName());
    private final ResourceBundle guiResourceBundle;

    I18n() {
        LOG.info("Loading {} translations with locale {}", "translations/gui", Locale.getDefault());
        guiResourceBundle = ResourceBundle.getBundle("translations/gui");
    }

    /**
     * Retrieves the value of the given key from the resource bundle.
     * @param key : key to look for
     * @return the string for the given key
     */
    public String t(String key) {
        return guiResourceBundle.getString(key);
    }

    /**
     * Retrieves the value of the given key from the resource bundle. Parameters can be supplied, these parameters will
     * be camelcased and inserted in the given key.
     * eg. t("button.{0}.{1}.title", "Hello World", "foo") will look for key: "button.helloWorld.foo.title"
     * in the given key String
     * @param key the key to look for
     * @param params the params to insert in the key
     * @return the string for the given key
     */
    public String t(String key, Object... params) {
        return t(new MessageFormat(key)
                .format(Arrays.stream(params).map(param -> StringUtils.toCamelCase(String.valueOf(param)))
                        .collect(Collectors.toList())
                        .toArray()));
    }
}
