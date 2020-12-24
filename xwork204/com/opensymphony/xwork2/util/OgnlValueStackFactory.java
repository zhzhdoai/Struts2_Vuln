/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

/**
 * Creates an Ognl value stack
 */
public class OgnlValueStackFactory extends ValueStackFactory {

    @Override
    public ValueStack createValueStack() {
        return new OgnlValueStack();
    }

    @Override
    public ValueStack createValueStack(ValueStack stack) {
        return new OgnlValueStack(stack);
    }

}
