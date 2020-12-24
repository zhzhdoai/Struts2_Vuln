/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import java.util.Map;

import com.opensymphony.xwork2.config.Configuration;


/**
 * The ActionProxyFactory is used to create ActionProxies to be executed. It is the entry point to XWork that is used
 * by a dispatcher to create an ActionProxy to execute for a particular namespace and action name.
 *
 * @author Jason Carreira
 *         Created Jun 15, 2003 5:18:30 PM
 * @see DefaultActionProxyFactory
 */
public interface ActionProxyFactory {

    /**
     * Creates an ActionProxy for the given namespace and action name by looking up the configuration. The ActionProxy
     * should be fully initialized when it is returned, including having an ActionInvocation instance associated.
     *
     * @param namespace    the namespace of the action
     * @param actionName
     * @param extraContext a Map of extra parameters to be provided to the ActionProxy
     * @return ActionProxy
     * @throws Exception
     */
    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext) throws Exception;

    /**
     * Creates an ActionProxy for the given namespace and action name by looking up the configuration. The ActionProxy
     * should be fully initialized when it is returned, including having an ActionInvocation instance associated.
     *
     * @param namespace     the namespace of the action
     * @param actionName
     * @param extraContext  a Map of extra parameters to be provided to the ActionProxy
     * @param executeResult flag which tells whether the result should be executed after the action
     * @param cleanupContext
     * @return ActionProxy
     * @throws Exception
     */
    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult, boolean cleanupContext) throws Exception;
}
