/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import java.util.Map;

/**
 * ValueStack allows multiple beans to be pushed in and dynamic EL expressions to be evaluated against it. When
 * evaluating an expression, the stack will be searched down the stack, from the latest objects pushed in to the
 * earliest, looking for a bean with a getter or setter for the given property or a method of the given name (depending
 * on the expression being evaluated).
 */
public interface ValueStack {

    public static final String VALUE_STACK = "com.opensymphony.xwork2.util.ValueStack.ValueStack";

    public static final String REPORT_ERRORS_ON_NO_PROP = "com.opensymphony.xwork2.util.ValueStack.ReportErrorsOnNoProp";

    public abstract Map getContext();

    /**
     * Sets the default type to convert to if no type is provided when getting a value.
     *
     * @param defaultType
     */
    public abstract void setDefaultType(Class defaultType);

    public abstract void setExprOverrides(Map overrides);

    public abstract Map getExprOverrides();

    /**
     * Get the CompoundRoot which holds the objects pushed onto the stack
     */
    public abstract CompoundRoot getRoot();

    /**
     * Attempts to set a property on a bean in the stack with the given expression using the default search order.
     *
     * @param expr  the expression defining the path to the property to be set.
     * @param value the value to be set into the neamed property
     */
    public abstract void setValue(String expr, Object value);

    /**
     * Attempts to set a property on a bean in the stack with the given expression using the default search order.
     *
     * @param expr                    the expression defining the path to the property to be set.
     * @param value                   the value to be set into the neamed property
     * @param throwExceptionOnFailure a flag to tell whether an exception should be thrown if there is no property with
     *                                the given name.
     */
    public abstract void setValue(String expr, Object value,
            boolean throwExceptionOnFailure);

    public abstract String findString(String expr);

    /**
     * Find a value by evaluating the given expression against the stack in the default search order.
     *
     * @param expr the expression giving the path of properties to navigate to find the property value to return
     * @return the result of evaluating the expression
     */
    public abstract Object findValue(String expr);

    /**
     * Find a value by evaluating the given expression against the stack in the default search order.
     *
     * @param expr   the expression giving the path of properties to navigate to find the property value to return
     * @param asType the type to convert the return value to
     * @return the result of evaluating the expression
     */
    public abstract Object findValue(String expr, Class asType);

    /**
     * Get the object on the top of the stack without changing the stack.
     *
     * @see CompoundRoot#peek()
     */
    public abstract Object peek();

    /**
     * Get the object on the top of the stack and remove it from the stack.
     *
     * @return the object on the top of the stack
     * @see CompoundRoot#pop()
     */
    public abstract Object pop();

    /**
     * Put this object onto the top of the stack
     *
     * @param o the object to be pushed onto the stack
     * @see CompoundRoot#push(Object)
     */
    public abstract void push(Object o);

    /**
     * Sets an object on the stack with the given key
     * so it is retrievable by findValue(key,...)
     * @param key
     * @param o
     */
    public abstract void set(String key, Object o);

    /**
     * Get the number of objects in the stack
     * s
     *
     * @return the number of objects in the stack
     */
    public abstract int size();

}