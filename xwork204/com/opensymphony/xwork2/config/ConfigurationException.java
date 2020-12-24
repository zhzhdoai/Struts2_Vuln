/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.XWorkException;


/**
 * ConfigurationException
 *
 * @author Jason Carreira
 */
public class ConfigurationException extends XWorkException {

    /**
     * Constructs a <code>ConfigurationException</code> with no detail message.
     */
    public ConfigurationException() {
    }

    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public ConfigurationException(String s) {
        super(s);
    }
    
    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public ConfigurationException(String s, Object target) {
        super(s, target);
    }

    /**
     * Constructs a <code>ConfigurationException</code> with no detail message.
     */
    public ConfigurationException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Constructs a <code>ConfigurationException</code> with no detail message.
     */
    public ConfigurationException(Throwable cause, Object target) {
        super(cause, target);
    }

    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public ConfigurationException(String s, Throwable cause) {
        super(s, cause);
    }
    
    /**
     * Constructs a <code>ConfigurationException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public ConfigurationException(String s, Throwable cause, Object target) {
        super(s, cause, target);
    }
}
