/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Inject;

import java.util.ResourceBundle;

/**
 * This factory enables users to provide and correctly initialize a custom TextProvider.
 *
 * @author Oleg Gorobets
 * @author Rene Gielen
 */
public class TextProviderFactory {

    private TextProvider textProvider;

    @Inject
    public void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    protected TextProvider getTextProvider() {
        if (this.textProvider == null) {
            return new TextProviderSupport();
        } else {
            return textProvider;
        }
    }

    public TextProvider createInstance(Class clazz, LocaleProvider provider) {
        TextProvider instance = getTextProvider();
        if (instance instanceof ResourceBundleTextProvider) {
            ((ResourceBundleTextProvider) instance).setClazz(clazz);
            ((ResourceBundleTextProvider) instance).setLocaleProvider(provider);
        }
        return instance;
    }

    public TextProvider createInstance(ResourceBundle bundle, LocaleProvider provider) {
        TextProvider instance = getTextProvider();
        if (instance instanceof ResourceBundleTextProvider) {
            ((ResourceBundleTextProvider) instance).setBundle(bundle);
            ((ResourceBundleTextProvider) instance).setLocaleProvider(provider);
        }
        return instance;
    }
}
