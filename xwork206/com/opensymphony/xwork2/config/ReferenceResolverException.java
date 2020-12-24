/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.XWorkException;


/**
 * Exception when a reference can't be resolved.
 *
 * @author Mike
 */
public class ReferenceResolverException extends XWorkException {

    public ReferenceResolverException() {
        super();
    }

    public ReferenceResolverException(String s) {
        super(s);
    }

    public ReferenceResolverException(String s, Throwable cause) {
        super(s, cause);
    }

    public ReferenceResolverException(Throwable cause) {
        super(cause);
    }
}
