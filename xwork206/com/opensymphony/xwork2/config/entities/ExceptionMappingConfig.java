/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.config.entities;

import java.util.Map;
import java.util.LinkedHashMap;
import java.io.Serializable;

import com.opensymphony.xwork2.util.location.Located;

/**
 * Configuration for exception mapping.
 *
 * @author Rainer Hermanns
 * @author Matthew E. Porter (matthew dot porter at metissian dot com)
 */
public class ExceptionMappingConfig extends Located implements Serializable {

    private String name;
    private String exceptionClassName;
    private String result;
    private Map params;


    public ExceptionMappingConfig() {
    }

    public ExceptionMappingConfig(String name, String exceptionClassName, String result) {
        this(name, exceptionClassName, result, new LinkedHashMap());
    }

    public ExceptionMappingConfig(String name, String exceptionClassName, String result, Map params) {
        this.name = name;
        this.exceptionClassName = exceptionClassName;
        this.result = result;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExceptionClassName() {
        return exceptionClassName;
    }

    public void setExceptionClassName(String exceptionClassName) {
        this.exceptionClassName = exceptionClassName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

        if (!(o instanceof ExceptionMappingConfig)) {
            return false;
        }

        final ExceptionMappingConfig exceptionMappingConfig = (ExceptionMappingConfig) o;

        if ((name != null) ? (!name.equals(exceptionMappingConfig.name)) : (exceptionMappingConfig.name != null)) {
            return false;
        }

        if ((exceptionClassName != null) ? (!exceptionClassName.equals(exceptionMappingConfig.exceptionClassName)) : (exceptionMappingConfig.exceptionClassName != null))
        {
            return false;
        }

        if ((result != null) ? (!result.equals(exceptionMappingConfig.result)) : (exceptionMappingConfig.result != null))
        {
            return false;
        }

        if ((params != null) ? (!params.equals(exceptionMappingConfig.params)) : (exceptionMappingConfig.params != null))
        {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int hashCode;
        hashCode = ((name != null) ? name.hashCode() : 0);
        hashCode = (29 * hashCode) + ((exceptionClassName != null) ? exceptionClassName.hashCode() : 0);
        hashCode = (29 * hashCode) + ((result != null) ? result.hashCode() : 0);
        hashCode = (29 * hashCode) + ((params != null) ? params.hashCode() : 0);

        return hashCode;
    }

}
