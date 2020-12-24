/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.entities.PackageConfig;
import com.opensymphony.xwork2.inject.Container;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;


/**
 * XWork configuration.
 *
 * @author Mike
 */
public interface Configuration extends Serializable {
    
    void rebuildRuntimeConfiguration();

    PackageConfig getPackageConfig(String name);

    Set getPackageConfigNames();

    Map getPackageConfigs();

    /**
     * The current runtime configuration. Currently, if changes have been made to the Configuration since the last
     * time buildRuntimeConfiguration() was called, you'll need to make sure to.
     *
     * @return the current runtime configuration
     */
    RuntimeConfiguration getRuntimeConfiguration();

    void addPackageConfig(String name, PackageConfig packageConfig);

    /**
     * Allow the Configuration to clean up any resources that have been used.
     */
    void destroy();

    void reload(List<ConfigurationProvider> providers) throws ConfigurationException;

    void removePackageConfig(String name);

    /**
     * @return the container
     */
    Container getContainer();

    Set<String> getLoadedFileNames();
}
