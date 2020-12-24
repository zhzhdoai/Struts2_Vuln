/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple map that guarantees that retrieving objects will never return null and insertions will
 * perform type conversion if necessary.  Empty beans will be created for any key that would
 * normally returned null using ObjectFactory's
 * {@link ObjectFactory#buildBean(Class,java.util.Map) buildBean} method.
 *
 * @author Patrick Lightbody
 * @author Mark Woon
 * @deprecated Native support for expanding lists and maps is provided in XWork 1.1, so this is no longer needed.
 */
public class XWorkMap extends HashMap {
    private Class clazz;

    public XWorkMap(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * Returns the value to which the specified key is mapped in this identity hash map.  If there
     * is no mapping for this key, create an appropriate object for this key, put it in the map, and
     * return the new object.  Use {@link #containsKey(Object)} to check if there really is a
     * mapping for a key or not.
     *
     * @param key the key whose associated value is to be returned.
     * @return the value to which this map maps the specified key
     */
    public Object get(Object key) {
        Object o = super.get(key);

        if (o == null) {
            try {
                //todo - can this use the ThreadLocal?
                o = ObjectFactory.getObjectFactory().buildBean(clazz, null); // ActionContext.getContext().getContextMap());
                this.put(key, o);
            } catch (Exception e) {
                throw new XWorkException(e);
            }
        }

        return o;
    }

    /**
     * Associates the specified value with the specified key in this map. If the map previously
     * contained a mapping for this key, the old value is replaced.
     *
     * @param key   key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt> if there was no
     *         mapping for key.  A <tt>null</tt> return can also indicate that the HashMap
     *         previously associated <tt>null</tt> with the specified key.
     */
    public Object put(Object key, Object value) {
        if ((value != null) && !clazz.isAssignableFrom(value.getClass())) {
            // convert to correct type
            Map context = ActionContext.getContext().getContextMap();
            value = XWorkConverter.getInstance().convertValue(context, null, null, null, value, clazz);
        }

        return super.put(key, value);
    }

    /**
     * Copies all of the mappings from the specified map to this map These mappings will replace any
     * mappings that this map had for any of the keys currently in the specified map.
     *
     * @param m mappings to be stored in this map.
     * @throws NullPointerException if the specified map is null.
     */
    public void putAll(Map m) {
        for (Iterator i = m.entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            put(e.getKey(), e.getValue());
        }
    }
}
