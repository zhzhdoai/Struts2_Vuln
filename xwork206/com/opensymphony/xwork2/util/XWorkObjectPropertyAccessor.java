/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import java.util.Map;

/**
 * @author Gabe
 */
public class XWorkObjectPropertyAccessor extends ObjectPropertyAccessor {
    public Object getProperty(Map context, Object target, Object oname)
            throws OgnlException {
        //set the last set objects in the context
        //so if the next objects accessed are
        //Maps or Collections they can use the information
        //to determine conversion types
        context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, target.getClass());
        context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, oname.toString());
        OgnlContextState.updateCurrentPropertyPath(context, oname);
        return super.getProperty(context, target, oname);
    }
}
