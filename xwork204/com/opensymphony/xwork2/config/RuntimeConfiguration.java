/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config;

import com.opensymphony.xwork2.config.entities.ActionConfig;

import java.util.Map;
import java.io.Serializable;


/**
 * RuntimeConfiguration
 *
 * @author Jason Carreira
 *         Created Feb 25, 2003 10:56:02 PM
 */
public interface RuntimeConfiguration extends Serializable {

    /**
     * get the fully expanded ActionConfig for a specified namespace and (action) name
     *
     * @param namespace the namespace of the Action.  if this is null, then the empty namespace, "", will be used
     * @param name      the name of the Action.  may not be null.
     * @return the requested ActionConfig or null if there was no ActionConfig associated with the specified namespace
     *         and name
     */
    ActionConfig getActionConfig(String namespace, String name);

    /**
     * returns a Map of all the registered ActionConfigs.  Again, these ActionConfigs are fully expanded so that any
     * inherited interceptors, results, etc. will be included
     *
     * @return a Map of Map keyed by namespace and name respectively such that
     *         <pre>
     *                 ActionConfig config = (ActionConfig)((Map)getActionConfigs.get(namespace)).get(name);
     *                 </pre>
     *         should return a valid config for valid namespace/name pairs
     */
    Map getActionConfigs();
}
