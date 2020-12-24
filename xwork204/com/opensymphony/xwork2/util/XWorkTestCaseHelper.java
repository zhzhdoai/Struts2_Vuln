/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.config.ConfigurationManager;
import com.opensymphony.xwork2.config.ConfigurationProvider;
import com.opensymphony.xwork2.inject.Container;

/**
 * Generic test setup methods to be used with any unit testing framework. 
 */
public class XWorkTestCaseHelper {

    public static ConfigurationManager setUp() throws Exception {
        ConfigurationManager configurationManager = new ConfigurationManager();
        
        // Reset the value stack
        ValueStack stack = new OgnlValueStack();
        ActionContext.setContext(new ActionContext(stack.getContext()));
    
        // clear out localization
        LocalizedTextUtil.reset();
    
        // type conversion
        XWorkConverter.resetInstance();
    
        // reset ognl
        OgnlValueStack.reset();
        
        ObjectFactory.setObjectFactory(new ObjectFactory());
        return configurationManager;
    }

    public static ConfigurationManager loadConfigurationProviders(ConfigurationManager configurationManager,
            ConfigurationProvider... providers) {
        if (configurationManager != null) {
            configurationManager.clearConfigurationProviders();
        } else {
            configurationManager = new ConfigurationManager();
        }
        configurationManager.clearConfigurationProviders();
        for (ConfigurationProvider prov : providers) {
            configurationManager.addConfigurationProvider(prov);
        }
        configurationManager.getConfiguration().reload(
                configurationManager.getConfigurationProviders());
        Container container = configurationManager.getConfiguration().getContainer();
        ObjectFactory.setObjectFactory(container.getInstance(ObjectFactory.class));
        return configurationManager;
    }

    public static void tearDown(ConfigurationManager configurationManager) throws Exception {
        // reset the old object factory
        ObjectFactory.setObjectFactory(null);
    
        //  clear out configuration
        if (configurationManager != null) {
            configurationManager.destroyConfiguration();
            configurationManager = null;
        }
        ActionContext.setContext(null);
    }
}