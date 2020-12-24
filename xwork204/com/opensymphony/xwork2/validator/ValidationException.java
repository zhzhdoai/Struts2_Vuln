/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;


/**
 * ValidationException.
 *
 * @author Jason Carreira
 */
public class ValidationException extends Exception {

    /**
     * Constructs an <code>Exception</code> with no specified detail message.
     */
    public ValidationException() {
    }

    /**
     * Constructs an <code>Exception</code> with the specified detail message.
     *
     * @param s the detail message.
     */
    public ValidationException(String s) {
        super(s);
    }
}
