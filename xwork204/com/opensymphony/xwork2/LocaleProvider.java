/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import java.util.Locale;


/**
 * Indicates that the implementing class can provide its own {@link Locale}. This
 * is useful for when an action may wish override the default locale. All that is
 * needed is to implement this interface and return your own custom locale.
 * The {@link TextProvider} interface uses this interface heavily for retrieving
 * internationalized messages from resource bundles.
 *
 * @author Jason Carreira
 *         Date: Oct 29, 2003 9:11:22 PM
 */
public interface LocaleProvider {

    Locale getLocale();
}
