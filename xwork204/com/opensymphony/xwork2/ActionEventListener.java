/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.util.ValueStack;

/**
 * Provides hooks for handling key action events
 */
public interface ActionEventListener {
    /**
     * Called after an action has been created. 
     * 
     * @param action The action
     * @param stack The current value stack
     * @return The action to use
     */
    public Object prepare(Object action, ValueStack stack);
    
    /**
     * Called when an exception is thrown by the action
     * 
     * @param t The exception/error that was thrown
     * @param stack The current value stack
     * @return A result code to execute, can be null
     */
    public String handleException(Throwable t, ValueStack stack);
}
