/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.location.Locatable;
import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;


/**
 * A generic runtime exception that optionally contains Location information 
 *
 * @author Jason Carreira
 */
public class XWorkException extends RuntimeException implements Locatable {

    private Location location;


    /**
     * Constructs a <code>XWorkException</code> with no detail message.
     */
    public XWorkException() {
    }

    /**
     * Constructs a <code>XWorkException</code> with the specified
     * detail message.
     *
     * @param s the detail message.
     */
    public XWorkException(String s) {
        this(s, null, null);
    }
    
    /**
     * Constructs a <code>XWorkException</code> with the specified
     * detail message and target.
     *
     * @param s the detail message.
     * @param target the target of the exception.
     */
    public XWorkException(String s, Object target) {
        this(s, (Throwable) null, target);
    }

    /**
     * Constructs a <code>XWorkException</code> with the root cause
     *
     * @param cause The wrapped exception
     */
    public XWorkException(Throwable cause) {
        this(null, cause, null);
    }
    
    /**
     * Constructs a <code>XWorkException</code> with the root cause and target
     *
     * @param cause The wrapped exception
     * @param target The target of the exception
     */
    public XWorkException(Throwable cause, Object target) {
        this(null, cause, target);
    }

    /**
     * Constructs a <code>XWorkException</code> with the specified
     * detail message and exception cause.
     *
     * @param s the detail message.
     * @param cause the wrapped exception
     */
    public XWorkException(String s, Throwable cause) {
        this(s, cause, null);
    }
    
    
     /**
     * Constructs a <code>XWorkException</code> with the specified
     * detail message, cause, and target
     *
     * @param s the detail message.
     * @param cause The wrapped exception
     * @param target The target of the exception
     */
    public XWorkException(String s, Throwable cause, Object target) {
        super(s, cause);
        
        this.location = LocationUtils.getLocation(target);
        if (this.location == Location.UNKNOWN) {
            this.location = LocationUtils.getLocation(cause);
        }
    }


    /**
     * Gets the underlying cause
     * 
     * @deprecated Use getCause()
     */
    public Throwable getThrowable() {
        return getCause();
    }


    /**
     * Gets the location of the error, if available
     */
    public Location getLocation() {
        return this.location;
    }
    
    
    /**
     * Returns a short description of this throwable object, including the 
     * location. If no detailed message is available, it will use the message
     * of the underlying exception if available.
     *
     * @return a string representation of this <code>Throwable</code>.
     */
    public String toString() {
        String msg = getMessage();
        if (msg == null && getCause() != null) {
            msg = getCause().getMessage();
        }

        if (location != null) {
            if (msg != null) {
                return msg + " - " + location.toString();
            } else {
                return location.toString();
            }
        } else {
            return msg;
        }
    }
}