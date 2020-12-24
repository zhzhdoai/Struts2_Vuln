/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.interceptor;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * This interface is implemented by actions that want to declare acceptable parameters. Works in conjunction with {@link
 * ParametersInterceptor}. For example, actions may want to create a whitelist of parameters they will accept or a
 * blacklist of paramters they will reject to prevent clients from setting other unexpected (and possibly dangerous)
 * parameters.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * @author Bob Lee (crazybob@google.com)
 */
public interface ParameterNameAware {

    /**
     * Returns true if the action will accept the parameter with the given name.
     */
    boolean acceptableParameterName(String parameterName);
}
