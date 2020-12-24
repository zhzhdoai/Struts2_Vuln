/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import java.util.Map;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;


/**
 * DefaultActionProxyFactory
 *
 * @author Jason Carreira
 *         Created Jun 15, 2003 5:19:13 PM
 */
public class DefaultActionProxyFactory implements ActionProxyFactory {

    protected Container container;
    
    public DefaultActionProxyFactory() {
        super();
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }
    
    /**
     * Use this method to build an DefaultActionProxy instance.
     */
    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext) throws Exception {
        return createActionProxy(namespace, actionName, extraContext, true, true);
    }

    /**
     * Use this method to build an DefaultActionProxy instance.
     */
    public ActionProxy createActionProxy(String namespace, String actionName, Map extraContext, boolean executeResult, boolean cleanupContext) throws Exception {
        ActionProxy proxy = new DefaultActionProxy(namespace, actionName, extraContext, executeResult, cleanupContext);
        container.inject(proxy);
        proxy.prepare();
        return proxy;
    }
}
