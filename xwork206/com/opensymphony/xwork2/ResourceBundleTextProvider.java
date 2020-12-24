package com.opensymphony.xwork2;

import java.util.ResourceBundle;

/**
 * Extension Interface for TextProvider to help supporting ResourceBundles.
 *
 * @author Rene Gielen
 */

public interface ResourceBundleTextProvider extends TextProvider {

    /**
     * Set the resource bundle to use.
     *
     * @param bundle the bundle to use.
     */
    void setBundle(ResourceBundle bundle);

    /**
     * Set the class to use for reading the resource bundle.
     *
     * @param clazz the class to use for loading.
     */
    void setClazz(Class clazz);

    /**
     * Set the LocaleProvider to use.
     *
     * @param localeProvider the LocaleProvider to use.
     */
    void setLocaleProvider(LocaleProvider localeProvider);

}
