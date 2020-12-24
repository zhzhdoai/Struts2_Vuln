/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.conversion.annotations;

/**
 * <code>ConversionRule</code>
 *
 * @author Rainer Hermanns
 * @version $Id: ConversionRule.java 1063 2006-07-10 00:30:29Z mrdon $
 */
public enum ConversionRule {

    PROPERTY, COLLECTION, MAP, KEY, KEY_PROPERTY, ELEMENT, CREATE_IF_NULL;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}

