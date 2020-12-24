/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.mock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.opensymphony.xwork2.ActionEventListener;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Mock for an {@link ActionInvocation}.
 *
 * @author plightbo
 * @author Rainer Hermanns
 * @author tm_jee
 * @version $Id: MockActionInvocation.java 1269 2006-12-13 06:17:15Z mrdon $
 */
public class MockActionInvocation implements ActionInvocation {

    private Object action;
    private ActionContext invocationContext;
    private ActionEventListener actionEventListener;
    private ActionProxy proxy;
    private Result result;
    private String resultCode;
    private ValueStack stack;
    
    private List preResultListeners = new ArrayList();

    public Object getAction() {
        return action;
    }

    public void setAction(Object action) {
        this.action = action;
    }

    public ActionContext getInvocationContext() {
        return invocationContext;
    }

    public void setInvocationContext(ActionContext invocationContext) {
        this.invocationContext = invocationContext;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    public void setProxy(ActionProxy proxy) {
        this.proxy = proxy;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public ValueStack getStack() {
        return stack;
    }

    public void setStack(ValueStack stack) {
        this.stack = stack;
    }

    public boolean isExecuted() {
        return false;
    }

    public void addPreResultListener(PreResultListener listener) {
    	preResultListeners.add(listener);
    }

    public String invoke() throws Exception {
    	for (Iterator i = preResultListeners.iterator(); i.hasNext(); ) {
    		PreResultListener listener = (PreResultListener) i.next();
    		listener.beforeResult(this, resultCode);
    	}
        return resultCode;
    }

    public String invokeActionOnly() throws Exception {
        return resultCode;
    }

    public void setActionEventListener(ActionEventListener listener) {
        this.actionEventListener = listener;
    }
    
    public ActionEventListener getActionEventListener() {
        return this.actionEventListener;
    }

}
