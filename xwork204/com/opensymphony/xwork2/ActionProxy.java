/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;


/**
 * ActionProxy is an extra layer between XWork and the action so that different proxies are possible.
 * <p/>
 * An example of this would be a remote proxy, where the layer between XWork and the action might be RMI or SOAP.
 *
 * @author Jason Carreira
 *         Created Jun 9, 2003 11:27:55 AM
 */
public interface ActionProxy {

    /**
     * Called after all dependencies are set
     */
    void prepare() throws Exception;
    
    /**
     * @return the Action instance for this Proxy
     */
    Object getAction();

    /**
     * @return the alias name this ActionProxy is mapped to
     */
    String getActionName();

    /**
     * @return the ActionConfig this ActionProxy is built from
     */
    ActionConfig getConfig();

    /**
     * Sets whether this ActionProxy should also execute the Result after executing the Action
     *
     * @param executeResult
     */
    void setExecuteResult(boolean executeResult);

    /**
     * @return the status of whether the ActionProxy is set to execute the Result after the Action is executed
     */
    boolean getExecuteResult();

    /**
     * @return the ActionInvocation associated with this ActionProxy
     */
    ActionInvocation getInvocation();

    /**
     * @return the namespace the ActionConfig for this ActionProxy is mapped to
     */
    String getNamespace();

    /**
     * Execute this ActionProxy. This will set the ActionContext from the ActionInvocation into the ActionContext
     * ThreadLocal before invoking the ActionInvocation, then set the old ActionContext back into the ThreadLocal.
     *
     * @return the result code returned from executing the ActionInvocation
     * @throws Exception
     * @see ActionInvocation
     */
    String execute() throws Exception;

    /**
     * Sets the method to execute for the action invocation. If no method is specified, the method provided by
     * in the action's configuration will be used.
     *
     * @param method the string name of the method to invoke
     */
    void setMethod(String method);

    /**
     * Returns the method to execute, or null if no method has been specified (meaning "execute" will be invoked)
     */
    String getMethod();
    
}
