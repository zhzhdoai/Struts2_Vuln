/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import ognl.MethodFailedException;
import ognl.ObjectMethodAccessor;
import ognl.OgnlContext;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Allows methods to be executed under normal cirumstances, except when {@link #DENY_METHOD_EXECUTION}
 * is in the action context with a value of true.
 *
 * @author Patrick Lightbody
 * @author tmjee
 */
public class XWorkMethodAccessor extends ObjectMethodAccessor {
	
	private static final Log _log = LogFactory.getLog(XWorkMethodAccessor.class);

    public static final String DENY_METHOD_EXECUTION = "xwork.MethodAccessor.denyMethodExecution";
    public static final String DENY_INDEXED_ACCESS_EXECUTION = "xwork.IndexedPropertyAccessor.denyMethodExecution";


    public Object callMethod(Map context, Object object, String string, Object[] objects) throws MethodFailedException {

        //Collection property accessing
        //this if statement ensures that ognl
        //statements of the form someBean.mySet('keyPropVal')
        //return the set element with value of the keyProp given
        
        if (objects.length==1 
                && context instanceof OgnlContext) {
            try {
              OgnlContext ogContext=(OgnlContext)context;
              if (OgnlRuntime.hasSetProperty(ogContext, object, string))  {
                  	PropertyDescriptor descriptor=OgnlRuntime.getPropertyDescriptor(object.getClass(), string);
                  	Class propertyType=descriptor.getPropertyType();
                  	if ((Collection.class).isAssignableFrom(propertyType)) {
                  	    //go directly through OgnlRuntime here
                  	    //so that property strings are not cleared
                  	    //i.e. OgnlUtil should be used initially, OgnlRuntime
                  	    //thereafter
                  	    
                  	    Object propVal=OgnlRuntime.getProperty(ogContext, object, string);
                  	    //use the Collection property accessor instead of the individual property accessor, because 
                  	    //in the case of Lists otherwise the index property could be used
                  	    PropertyAccessor accessor=OgnlRuntime.getPropertyAccessor(Collection.class);
                  	    OgnlContextState.setGettingByKeyProperty(ogContext,true);
                  	    return accessor.getProperty(ogContext,propVal,objects[0]);
                  	}
              }
            }	catch (Exception oe) {
                //this exception should theoretically never happen
                //log it
            	_log.error("An unexpected exception occurred", oe);
            }

        }

        //HACK - we pass indexed method access i.e. setXXX(A,B) pattern
        if (
                (objects.length == 2 && string.startsWith("set"))
                        ||
                        (objects.length == 1 && string.startsWith("get"))
                ) {
            Boolean exec = (Boolean) context.get(DENY_INDEXED_ACCESS_EXECUTION);
            boolean e = ((exec == null) ? false : exec.booleanValue());
            if (!e) {
                return super.callMethod(context, object, string, objects);
            }
        }
        Boolean exec = (Boolean) context.get(DENY_METHOD_EXECUTION);
        boolean e = ((exec == null) ? false : exec.booleanValue());

        if (!e) {
            return super.callMethod(context, object, string, objects);
        } else {
            return null;
        }
    }

    public Object callStaticMethod(Map context, Class aClass, String string, Object[] objects) throws MethodFailedException {
        Boolean exec = (Boolean) context.get(DENY_METHOD_EXECUTION);
        boolean e = ((exec == null) ? false : exec.booleanValue());

        if (!e) {
            return super.callStaticMethod(context, aClass, string, objects);
        } else {
            return null;
        }
    }
}
