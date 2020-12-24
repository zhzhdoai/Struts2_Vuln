/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.io.Serializable;

import com.opensymphony.xwork2.util.location.Located;

/**
 * Configuration for Interceptors.
 * <p/>
 * In the xml configuration file this is defined as the <code>interceptors</code> tag.
 *
 * @author Mike
 */
public class InterceptorConfig extends Located implements Parameterizable, Serializable {

    Map params;
    String className;
    String name;


    public InterceptorConfig() {
    }

    public InterceptorConfig(String name, Class clazz, Map params) {
        this.name = name;
        this.className = clazz.getName();
        this.params = params;
    }

    public InterceptorConfig(String name, String className, Map params) {
        this.name = name;
        this.className = className;
        this.params = params;
    }
    
    public InterceptorConfig(InterceptorConfig parent, Map params) {
        this.name = parent.getName();
        this.className = parent.getClassName();
        this.params = new HashMap(parent.getParams());
        this.params.putAll(params);
    }


    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setParams(Map params) {
        this.params = params;
    }

    public Map getParams() {
        if (params == null) {
            params = new LinkedHashMap();
        }

        return params;
    }

    public void addParam(String name, Object value) {
        getParams().put(name, value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof InterceptorConfig)) {
            return false;
        }

        final InterceptorConfig interceptorConfig = (InterceptorConfig) o;

        if ((className != null) ? (!className.equals(interceptorConfig.className)) : (interceptorConfig.className != null))
        {
            return false;
        }

        if ((name != null) ? (!name.equals(interceptorConfig.name)) : (interceptorConfig.name != null)) {
            return false;
        }

        if ((params != null) ? (!params.equals(interceptorConfig.params)) : (interceptorConfig.params != null)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((className != null) ? className.hashCode() : 0);
        result = (29 * result) + ((params != null) ? params.hashCode() : 0);

        return result;
    }
}
