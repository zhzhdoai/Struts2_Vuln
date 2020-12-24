/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.util;

/**
 * An Object to use within OGNL to proxy other Objects
 * usually Collections that you set in a different place
 * on the ValueStack but want to retain the context information
 * about where they previously were.
 *
 * @author Gabe
 */
public class ObjectProxy {
    private Object value;
    private Class lastClassAccessed;
    private String lastPropertyAccessed;

    public Class getLastClassAccessed() {
        return lastClassAccessed;
    }

    public void setLastClassAccessed(Class lastClassAccessed) {
        this.lastClassAccessed = lastClassAccessed;
    }

    public String getLastPropertyAccessed() {
        return lastPropertyAccessed;
    }

    public void setLastPropertyAccessed(String lastPropertyAccessed) {
        this.lastPropertyAccessed = lastPropertyAccessed;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
