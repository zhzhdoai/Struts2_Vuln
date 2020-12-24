/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;

import java.io.Serializable;


/**
 * An ActionInvocation represents the execution state of an Action. It holds the Interceptors and the Action instance.
 * By repeated re-entrant execution of the invoke() method, initially by the ActionProxy, then by the Interceptors, the
 * Interceptors are all executed, and then the Action and the Result.
 *
 * @author Jason Carreira
 * @see com.opensymphony.xwork2.ActionProxy
 */
public interface ActionInvocation extends Serializable {

    /**
     * Get the Action associated with this ActionInvocation
     */
    Object getAction();

    /**
     * @return whether this ActionInvocation has executed before. This will be set after the Action and the Result have
     *         executed.
     */
    boolean isExecuted();

    /**
     * Gets the ActionContext associated with this ActionInvocation. The ActionProxy is
     * responsible for setting this ActionContext onto the ThreadLocal before invoking
     * the ActionInvocation and resetting the old ActionContext afterwards.
     */
    ActionContext getInvocationContext();

    /**
     * Get the ActionProxy holding this ActionInvocation
     */
    ActionProxy getProxy();

    /**
     * If the ActionInvocation has been executed before and the Result is an instance of ActionChainResult, this method
     * will walk down the chain of ActionChainResults until it finds a non-chain result, which will be returned. If the
     * ActionInvocation's result has not been executed before, the Result instance will be created and populated with
     * the result params.
     *
     * @return a Result instance
     */
    Result getResult() throws Exception;

    /**
     * Gets the result code returned from this ActionInvocation
     */
    String getResultCode();

    /**
     * Sets the result code, possibly overriding the one returned by the
     * action.
     * <p/>
     * <p/>
     * The "intended" purpose of this method is to allow PreResultListeners to
     * override the result code returned by the Action.
     * </p>
     * <p/>
     * <p/>
     * If this method is used before the Action executes, the Action's returned
     * result code will override what was set.  However the Action could (if
     * specifically coded to do so) inspect the ActionInvocation to see that
     * someone "upstream" (e.g. an Interceptor) had suggested a value as the
     * result, and it could therefore return the same value itself.
     * </p>
     * <p/>
     * <p/>
     * If this method is called between the Action execution and the Result
     * execution, then the value set here will override the result code the
     * action had returned.   Creating an Interceptor that implements
     * PreResultListener will give you this oportunity.
     * </p>
     * <p/>
     * <p/>
     * If this method is called after the Result has been executed, it will
     * have the effect of raising an exception.
     * </p>
     *
     * @throws IllegalStateException if called after the Result has been
     *                               executed.
     * @see #isExecuted()
     */
    void setResultCode(String resultCode);

    /**
     * @return the ValueStack associated with this ActionInvocation
     */
    ValueStack getStack();

    /**
     * Register a com.opensymphony.xwork2.interceptor.PreResultListener to be notified after the Action is executed and
     * before the Result is executed. The ActionInvocation implementation must guarantee that listeners will be called in
     * the order in which they are registered. Listener registration and execution does not need to be thread-safe.
     */
    void addPreResultListener(PreResultListener listener);

    /**
     * Invokes the next step in processing this ActionInvocation. If there are more Interceptors, this will call the next
     * one. If Interceptors choose not to short-circuit ActionInvocation processing and return their own return code,
     * they will call invoke() to allow the next Interceptor to execute. If there are no more Interceptors to be applied,
     * the Action is executed. If the ActionProxy getExecuteResult() method returns true, the Result is also executed.
     */
    String invoke() throws Exception;

    /**
     * Invokes only the action (not interceptors or results). This is useful in rare situations where advanced usage
     * with the interceptor/action/result workflow is being manipulated for certain functionality. 
     */
    String invokeActionOnly() throws Exception;

    /**
     * Sets the action event listener to respond to key action events
     */
    void setActionEventListener(ActionEventListener listener);
}
