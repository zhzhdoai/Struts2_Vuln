/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;


/**
 * PreResultListeners may be registered with an ActionInvocation to get a callback after the Action has been executed
 * but before the Result is executed.
 *
 * @author Jason Carreira
 *         Date: Nov 13, 2003 10:55:02 PM
 */
public interface PreResultListener {

    /**
     * This callback method will be called after the Action execution and before the Result execution.
     *
     * @param invocation
     * @param resultCode
     */
    void beforeResult(ActionInvocation invocation, String resultCode);
}
