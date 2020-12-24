/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;


/**
 * Interface to be implemented by all forms of XWork configuration classes.
 *
 * @author $Author: mrdon $
 * @version $Revision: 1213 $
 */
public interface ConfigurationProvider {

    public void destroy();
    
    public void init(Configuration configuration) throws ConfigurationException;
    
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException;
    
    public void loadPackages() throws ConfigurationException;
    
    /**
     * Tells whether the ConfigurationProvider should reload its configuration
     *
     * @return <tt>true</tt>, whether the ConfigurationProvider should reload its configuration, <tt>false</tt>otherwise.
     */
    public boolean needsReload();
    
}
