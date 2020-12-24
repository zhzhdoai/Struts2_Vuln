/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.conversion.annotations;

/**
 * <code>ConversionType</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: ConversionType.java 1063 2006-07-10 00:30:29Z mrdon $
 */
public enum ConversionType {


    APPLICATION, CLASS;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }

}
