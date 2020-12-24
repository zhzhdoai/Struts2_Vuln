/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ObjectFactory;
import ognl.MapPropertyAccessor;
import ognl.OgnlException;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of PropertyAccessor that sets and gets properties by storing and looking
 * up values in Maps.
 *
 * @author Gabriel Zimmerman
 */
public class XWorkMapPropertyAccessor extends MapPropertyAccessor {

    private static final Log _log = LogFactory.getLog(XWorkMapPropertyAccessor.class);

    private static final String[] INDEX_ACCESS_PROPS = new String[]
            {"size", "isEmpty", "keys", "values"};

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {

        if (_log.isDebugEnabled()) {
            _log.debug("Entering getProperty ("+context+","+target+","+name+")");
        }

        OgnlContextState.updateCurrentPropertyPath(context, name);
        // if this is one of the regular index access
        // properties then just let the superclass deal with the
        // get.
        if (name instanceof String && contains(INDEX_ACCESS_PROPS, (String) name)) {
            return super.getProperty(context, target, name);
        }

        Object result = null;

        try{
            result = super.getProperty(context, target, name);
        } catch(ClassCastException ex){
        }

        if (result == null) {
            //find the key class and convert the name to that class
            Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);

            String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
            if (lastClass == null || lastProperty == null) {
                return super.getProperty(context, target, name);
            }
            Class keyClass = XWorkConverter.getInstance().getObjectTypeDeterminer()
                    .getKeyClass(lastClass, lastProperty);

            if (keyClass == null) {

                keyClass = java.lang.String.class;
            }
            Object key = getKey(context, name);
            Map map = (Map) target;
            result = map.get(key);

            if (result == null &&
                    context.get(InstantiatingNullHandler.CREATE_NULL_OBJECTS) != null
                    &&  XWorkConverter.getInstance()
                    .getObjectTypeDeterminer().shouldCreateIfNew(lastClass,lastProperty,target,null,false)) {
                Class valueClass = XWorkConverter.getInstance().getObjectTypeDeterminer().getElementClass(lastClass, lastProperty, key);

                try {
                    result = ObjectFactory.getObjectFactory().buildBean(valueClass, context);
                    map.put(key, result);
                } catch (Exception exc) {

                }

            }
        }
        return result;
    }

    /**
     * @param array
     * @param name
     */
    private boolean contains(String[] array, String name) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(name)) {
                return true;
            }
        }

        return false;
    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        if (_log.isDebugEnabled()) {
     		_log.debug("Entering setProperty("+context+","+target+","+name+","+value+")");
     	}
        
        Object key = getKey(context, name);
        Map map = (Map) target;
        map.put(key, getValue(context, value));
     }

     private Object getValue(Map context, Object value) {
         Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
         String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
         if (lastClass == null || lastProperty == null) {
             return value;
         }
         Class elementClass = XWorkConverter.getInstance().getObjectTypeDeterminer()
                 .getElementClass(lastClass, lastProperty, null);
         if (elementClass == null) {
             return value; // nothing is specified, we assume it will be the value passed in.
         }
         return XWorkConverter.getInstance().convertValue(context, value, elementClass);
}

    private Object getKey(Map context, Object name) {
        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        if (lastClass == null || lastProperty == null) {
            // return java.lang.String.class;
            // commented out the above -- it makes absolutely no sense for when setting basic maps!
            return name;
        }
        Class keyClass = XWorkConverter.getInstance().getObjectTypeDeterminer()
                .getKeyClass(lastClass, lastProperty);
        if (keyClass == null) {
            keyClass = java.lang.String.class;
        }

        return XWorkConverter.getInstance().convertValue(context, name, keyClass);

    }
}

