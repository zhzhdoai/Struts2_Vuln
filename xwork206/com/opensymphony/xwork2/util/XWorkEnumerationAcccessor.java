/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import ognl.EnumerationPropertyAccessor;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import java.util.Map;


/**
 * @author plightbo
 */
public class XWorkEnumerationAcccessor extends EnumerationPropertyAccessor {

    ObjectPropertyAccessor opa = new ObjectPropertyAccessor();


    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        opa.setProperty(context, target, name, value);
    }
}
