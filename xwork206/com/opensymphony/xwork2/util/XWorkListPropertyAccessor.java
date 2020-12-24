/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.XWorkException;

import ognl.ListPropertyAccessor;
import ognl.OgnlException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Overrides the list property accessor so in the case of trying
 * to add properties of a given bean and the JavaBean is not present,
 * this class will create the necessary blank JavaBeans.
 *
 * @author Gabriel Zimmerman
 */
public class XWorkListPropertyAccessor extends ListPropertyAccessor {

    private XWorkCollectionPropertyAccessor _sAcc = new XWorkCollectionPropertyAccessor();

    public Object getProperty(Map context, Object target, Object name)
            throws OgnlException {

        if (OgnlContextState.isGettingByKeyProperty(context)
                || name.equals(XWorkCollectionPropertyAccessor.KEY_PROPERTY_FOR_CREATION)) {
            return _sAcc.getProperty(context, target, name);
        }	else if (name instanceof String) {
            return super.getProperty(context, target, name);
        }
        OgnlContextState.updateCurrentPropertyPath(context, name);
        //System.out.println("Entering XWorkListPropertyAccessor. Name: " + name);
        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        
        if (name instanceof Number
                && OgnlContextState.isCreatingNullObjects(context)
                && XWorkConverter.getInstance()
                .getObjectTypeDeterminer().shouldCreateIfNew(lastClass,lastProperty,target,null,true)) {

            //System.out.println("Getting index from List");
            List list = (List) target;
            int index = ((Number) name).intValue();
            int listSize = list.size();

            if (lastClass == null || lastProperty == null) {
                return super.getProperty(context, target, name);
            }
            Class beanClass = XWorkConverter.getInstance()
                    .getObjectTypeDeterminer().getElementClass(lastClass, lastProperty, name);
            if (listSize <= index) {
                Object result = null;

                for (int i = listSize; i < index; i++) {

                    list.add(null);

                }
                try {
                    list.add(index, result = ObjectFactory.getObjectFactory().buildBean(beanClass, context));
                } catch (Exception exc) {
                    throw new XWorkException(exc);
                }
                return result;
            } else if (list.get(index) == null) {
                Object result = null;
                try {
                    list.set(index, result = ObjectFactory.getObjectFactory().buildBean(beanClass, context));
                } catch (Exception exc) {
                    throw new XWorkException(exc);
                }
                return result;
            }
        }
        return super.getProperty(context, target, name);
    }

    public void setProperty(Map context, Object target, Object name, Object value)
            throws OgnlException {

        Class lastClass = (Class) context.get(XWorkConverter.LAST_BEAN_CLASS_ACCESSED);
        String lastProperty = (String) context.get(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED);
        Class convertToClass = XWorkConverter.getInstance()
                .getObjectTypeDeterminer().getElementClass(lastClass, lastProperty, name);

        if (name instanceof String && value.getClass().isArray()) {
            // looks like the input game in the form of "someList.foo" and
            // we are expected to define the index values ourselves.
            // So let's do it:

            Collection c = (Collection) target;
            Object[] values = (Object[]) value;
            for (int i = 0; i < values.length; i++) {
                Object v = values[i];
                try {
                    Object o = ObjectFactory.getObjectFactory().buildBean(convertToClass, context);
                    OgnlUtil.setValue((String) name, context, o, v);
                    c.add(o);
                } catch (Exception e) {
                    throw new OgnlException("Error converting given String values for Collection.", e);
                }
            }

            // we don't want to do the normal list property setting now, since we've already done the work
            // just return instead
            return;
        }

        Object realValue = getRealValue(context, value, convertToClass);

        if (target instanceof List && name instanceof Number) {
            //make sure there are enough spaces in the List to set
            List list = (List) target;
            int listSize = list.size();
            int count = ((Number) name).intValue();
            if (count >= listSize) {
                for (int i = listSize; i <= count; i++) {
                    list.add(null);
                }
            }
        }

        super.setProperty(context, target, name, realValue);
    }

    private Object getRealValue(Map context, Object value, Class convertToClass) {
        if (value == null || convertToClass == null) {
            return value;
        }
        return XWorkConverter.getInstance().convertValue(context, value, convertToClass);
    }
}
