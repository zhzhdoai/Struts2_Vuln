/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

/**
 * Factory that creates a value stack, defaulting to the OgnlValueStackFactory
 */
public abstract class ValueStackFactory {
    private static ValueStackFactory factory = new OgnlValueStackFactory();
    
    public static void setFactory(ValueStackFactory factory) {
        ValueStackFactory.factory = factory;
    }
    
    public static ValueStackFactory getFactory() {
        return factory;
    }

    public abstract ValueStack createValueStack();
    
    public abstract ValueStack createValueStack(ValueStack stack);
    
}
