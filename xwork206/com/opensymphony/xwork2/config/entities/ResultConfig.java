/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Configuration for Result.
 * <p/>
 * In the xml configuration file this is defined as the <code>result</code> tag.
 *
 * @author Mike
 */
public class ResultConfig extends Located implements Parameterizable, Serializable {

    private Map params;
    private String className;
    private String name;


    public ResultConfig() {
        params = new LinkedHashMap();
    }

    public ResultConfig(String name, String clazz) {
        this(name, clazz, new LinkedHashMap());
    }

    public ResultConfig(String name, String className, Map params) {
        this.name = name;
        this.className = className;
        this.params = params;
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

        if (!(o instanceof ResultConfig)) {
            return false;
        }

        final ResultConfig resultConfig = (ResultConfig) o;

        if ((className != null) ? (!className.equals(resultConfig.className)) : (resultConfig.className != null)) {
            return false;
        }

        if ((name != null) ? (!name.equals(resultConfig.name)) : (resultConfig.name != null)) {
            return false;
        }

        if ((params != null) ? (!params.equals(resultConfig.params)) : (resultConfig.params != null)) {
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
