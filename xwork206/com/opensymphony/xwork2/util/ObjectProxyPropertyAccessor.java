/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.util;

import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import java.util.Map;

/**
 * Is able to access (set/get) properties on a given object.
 * <p/>
 * Uses Ognl internal.
 *
 * @author Gabe
 */
public class ObjectProxyPropertyAccessor implements PropertyAccessor {
    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        return OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass()).getProperty(context, target, name);

    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        ObjectProxy proxy = (ObjectProxy) target;
        setupContext(context, proxy);

        OgnlRuntime.getPropertyAccessor(proxy.getValue().getClass()).setProperty(context, target, name, value);
    }

    /**
     * Sets up the context with the last property and last class
     * accessed.
     *
     * @param context
     * @param proxy
     */
    private void setupContext(Map context, ObjectProxy proxy) {
        OgnlContextState.setLastBeanClassAccessed(context, proxy.getLastClassAccessed());
        OgnlContextState.setLastBeanPropertyAccessed(context, proxy.getLastPropertyAccessed());
    }
}
