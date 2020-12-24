/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;


/**
 * ModelDriven Actions provide a model object to be pushed onto the ValueStack
 * in addition to the Action itself, allowing a FormBean type approach like Struts.
 *
 * @author Jason Carreira
 *         Created Apr 8, 2003 6:22:42 PM
 */
public interface ModelDriven<T> {

    /**
     * @return the model to be pushed onto the ValueStack instead of the Action itself
     */
    T getModel();
}
