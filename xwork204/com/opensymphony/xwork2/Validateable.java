/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;


/**
 * Provides an interface in which a call for a validation check can be done.
 *
 * @author Jason Carreira
 * @see ActionSupport
 * @see com.opensymphony.xwork2.interceptor.DefaultWorkflowInterceptor
 */
public interface Validateable {

    /**
     * Performs validation.
     */
    void validate();

}
