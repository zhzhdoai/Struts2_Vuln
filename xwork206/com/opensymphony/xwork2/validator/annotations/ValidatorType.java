/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.validator.annotations;

/**
 * <code>ValidatorType</code>
 *
 * @author Rainer Hermanns
 * @version $Id: ValidatorType.java 1187 2006-11-13 08:05:32Z mrdon $
 */
public enum ValidatorType {

    FIELD, SIMPLE;

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
    
}
