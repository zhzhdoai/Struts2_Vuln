/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import java.util.ArrayList;
import java.util.List;


/**
 * A Stack that is implemented using a List.
 * 
 * @author plightbo
 * @version $Revision: 1063 $
 */
public class CompoundRoot extends ArrayList {

    public CompoundRoot() {
    }

    public CompoundRoot(List list) {
        super(list);
    }


    public CompoundRoot cutStack(int index) {
        return new CompoundRoot(subList(index, size()));
    }

    public Object peek() {
        return get(0);
    }

    public Object pop() {
        return remove(0);
    }

    public void push(Object o) {
        add(0, o);
    }
}
