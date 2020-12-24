/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.entities;

import com.opensymphony.xwork2.util.location.Located;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Configuration for InterceptorStack.
 * <p/>
 * In the xml configuration file this is defined as the <code>interceptor-stack</code> tag.
 *
 * @author Mike
 * @author Rainer Hermanns
 */
public class InterceptorStackConfig extends Located implements InterceptorListHolder, Serializable {

    private List<InterceptorMapping> interceptors;
    private String name;


    public InterceptorStackConfig() {
        this.interceptors = new ArrayList<InterceptorMapping>();
    }

    public InterceptorStackConfig(String name) {
        this();
        this.name = name;
    }


    public Collection<InterceptorMapping> getInterceptors() {
        return interceptors;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addInterceptor(InterceptorMapping interceptor) {
        this.interceptors.add(interceptor);
    }

    public void addInterceptors(List<InterceptorMapping> interceptors) {
        this.interceptors.addAll(interceptors);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof InterceptorStackConfig)) {
            return false;
        }

        final InterceptorStackConfig interceptorStackConfig = (InterceptorStackConfig) o;

        if ((interceptors != null) ? (!interceptors.equals(interceptorStackConfig.interceptors)) : (interceptorStackConfig.interceptors != null))
        {
            return false;
        }

        if ((name != null) ? (!name.equals(interceptorStackConfig.name)) : (interceptorStackConfig.name != null)) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = ((name != null) ? name.hashCode() : 0);
        result = (29 * result) + ((interceptors != null) ? interceptors.hashCode() : 0);

        return result;
    }
}
