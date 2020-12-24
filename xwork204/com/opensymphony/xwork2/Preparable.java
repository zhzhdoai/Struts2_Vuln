/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;


/**
 * Preparable Actions will have their prepare() method called if the PrepareInterceptor is applied to the ActionConfig
 *
 * @author Jason Carreira
 * @see com.opensymphony.xwork2.interceptor.PrepareInterceptor
 *      Date: Nov 5, 2003 2:28:47 AM
 */
public interface Preparable {

    /**
     * This method is called to allow the action to prepare itself.
     *
     * @throws Exception thrown if a system level exception occurs.
     */
    void prepare() throws Exception;
}
