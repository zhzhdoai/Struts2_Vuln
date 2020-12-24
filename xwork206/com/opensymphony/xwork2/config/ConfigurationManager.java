/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.util.FileManager;
import com.opensymphony.xwork2.config.impl.DefaultConfiguration;
import com.opensymphony.xwork2.config.providers.XmlConfigurationProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * ConfigurationManager - central for XWork Configuration management, including
 * its ConfigurationProvider.
 *
 * @author Jason Carreira
 * @author tm_jee
 * @version $Date: 2006-11-18 07:59:15 +0100 (Sa, 18 Nov 2006) $ $Id: ConfigurationManager.java 1218 2006-11-18 06:59:15Z tm_jee $
 */
public class ConfigurationManager {

    protected static final Log LOG = LogFactory.getLog(ConfigurationManager.class);
    protected Configuration configuration;
    protected Lock providerLock = new ReentrantLock();
    private List<ConfigurationProvider> configurationProviders = new CopyOnWriteArrayList<ConfigurationProvider>();
    protected String defaultFrameworkBeanName;

    public ConfigurationManager() {
        this("xwork");
    }
    
    public ConfigurationManager(String name) {
        this.defaultFrameworkBeanName = name;
    }

    /**
     * Get the current XWork configuration object.  By default an instance of DefaultConfiguration will be returned
     *
     * @see com.opensymphony.xwork2.config.impl.DefaultConfiguration
     */
    public synchronized Configuration getConfiguration() {
        if (configuration == null) {
            setConfiguration(new DefaultConfiguration(defaultFrameworkBeanName));
            try {
                configuration.reload(getConfigurationProviders());
            } catch (ConfigurationException e) {
                setConfiguration(null);
                throw e;
            }
        } else {
            conditionalReload();
        }

        return configuration;
    }

    public synchronized void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * Get the current list of ConfigurationProviders. If no custom ConfigurationProviders have been added, this method
     * will return a list containing only the default ConfigurationProvider, XMLConfigurationProvider.  if a custom
     * ConfigurationProvider has been added, then the XmlConfigurationProvider must be added by hand.
     * </p>
     * <p/>
     * TODO: the lazy instantiation of XmlConfigurationProvider should be refactored to be elsewhere.  the behavior described above seems unintuitive.
     *
     * @return the list of registered ConfigurationProvider objects
     * @see ConfigurationProvider
     */
    public List<ConfigurationProvider> getConfigurationProviders() {
        providerLock.lock();
        try {
            if (configurationProviders.size() == 0) {
                configurationProviders.add(new XmlConfigurationProvider("xwork.xml", true));
            }

            return configurationProviders;
        } finally {
            providerLock.unlock();
        }
    }

    /**
     * Set the list of configuration providers
     *
     * @param configurationProviders
     */
    public void setConfigurationProviders(List<ConfigurationProvider> configurationProviders) {
        providerLock.lock();
        try {
            this.configurationProviders = new CopyOnWriteArrayList<ConfigurationProvider>(configurationProviders);
        } finally {
            providerLock.unlock();
        }
    }

    /**
     * adds a configuration provider to the List of ConfigurationProviders.  a given ConfigurationProvider may be added
     * more than once
     *
     * @param provider the ConfigurationProvider to register
     */
    public void addConfigurationProvider(ConfigurationProvider provider) {
        if (!configurationProviders.contains(provider)) {
            configurationProviders.add(provider);
        }
    }
    
    /**
     * clears the registered ConfigurationProviders.  this method will call destroy() on each of the registered
     * ConfigurationProviders
     *
     * @see com.opensymphony.xwork2.config.ConfigurationProvider#destroy
     */
    public void clearConfigurationProviders() {
        for (ConfigurationProvider configurationProvider : configurationProviders) {
        	try {
        		configurationProvider.destroy();
        	}
        	catch(Exception e) {
        		LOG.warn("error while destroying configuration provider ["+configurationProvider+"]", e);
        	}
        }
        configurationProviders.clear();
    }

    /**
     * Destroy its managing Configuration instance
     */
    public synchronized void destroyConfiguration() {
        clearConfigurationProviders(); // let's destroy the ConfigurationProvider first
        setConfigurationProviders(new CopyOnWriteArrayList<ConfigurationProvider>());
        if (configuration != null)
            configuration.destroy(); // let's destroy it first, before nulling it.
        configuration = null;
    }


    /**
     * Reloads the Configuration files if the configuration files indicate that they need to be reloaded.
     */
    public synchronized void conditionalReload() {
        if (FileManager.isReloadingConfigs()) {
            boolean reload;

            if (LOG.isDebugEnabled()) {
                LOG.debug("Checking ConfigurationProviders for reload.");
            }

            reload = false;

            List<ConfigurationProvider> providers = getConfigurationProviders();
            for (ConfigurationProvider provider : providers) {
                if (provider.needsReload()) {
                    if (LOG.isInfoEnabled()) {
                        LOG.info("Detected configuration provider "+provider+" needs to be reloaded.  Reloading all providers.");
                    }
                    reload = true;

                    break;
                }
            }

            if (reload) {
            	for (ConfigurationProvider configurationProvider : configurationProviders) {
                	try {
                		configurationProvider.destroy();
                	}
                	catch(Exception e) {
                		LOG.warn("error while destroying configuration provider ["+configurationProvider+"]", e);
                	}
                }
                configuration.reload(providers);
            }
        }
    }
    
    public synchronized void reload() {
        getConfiguration().reload(getConfigurationProviders());
    }
}
