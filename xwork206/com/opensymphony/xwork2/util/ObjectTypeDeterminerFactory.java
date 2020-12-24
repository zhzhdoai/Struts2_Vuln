/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory for getting an instance of {@link ObjectTypeDeterminer}.
 * <p/>
 * Will use <code>com.opensymphony.xwork2.util.GenericsObjectTypeDeterminer</code> by default.
 *
 * @see com.opensymphony.xwork2.util.ObjectTypeDeterminer
 * @see com.opensymphony.xwork2.util.DefaultObjectTypeDeterminer
 *
 * @author plightbo
 * @author Rainer Hermanns
 * @author Rene Gielen
 */
public class ObjectTypeDeterminerFactory {
    private static final Log LOG = LogFactory.getLog(ObjectTypeDeterminerFactory.class);

    private static ObjectTypeDeterminer instance = new DefaultObjectTypeDeterminer();

    static {
        LOG.info("Setting DefaultObjectTypeDeterminer as default ...");
    }

    /**
     * Sets a new instance of ObjectTypeDeterminer to be used.
     *
     * @param instance  instance of ObjectTypeDeterminer
     */
    public static void setInstance(ObjectTypeDeterminer instance) {
        if (instance != null) {
            if (!instance.getClass().equals(ObjectTypeDeterminerFactory.instance.getClass())) {
                LOG.info("Switching to ObjectTypeDeterminer of type " + instance.getClass().getName());
            }
            ObjectTypeDeterminerFactory.instance = instance;
        }
    }

    /**
     * Gets the instance of ObjectTypeDeterminer to be used.
     *
     * @return instance of ObjectTypeDeterminer
     */
    public static ObjectTypeDeterminer getInstance() {
        return instance;
    }

}
