/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;

import java.io.Serializable;


/**
 * <!-- START SNIPPET: introduction -->
 * 
 * An interceptor is a stateless class that follows the interceptor pattern, as
 * found in {@link  javax.servlet.Filter} and in AOP languages.
 * 
 * <p/>
 * 
 * Interceptors are objects that dynamically intercept Action invocations. 
 * They provide the developer with the opportunity to define code that can be executed 
 * before and/or after the execution of an action. They also have the ability 
 * to prevent an action from executing. Interceptors provide developers a way to 
 * encapulate common functionality in a re-usable form that can be applied to 
 * one or more Actions.
 * 
 * <p/>
 * 
 * Interceptors <b>must</b> be stateless and not assume that a new instance will be created for each request or Action.
 * Interceptors may choose to either short-circuit the {@link ActionInvocation} execution and return a return code
 * (such as {@link com.opensymphony.xwork2.Action#SUCCESS}), or it may choose to do some processing before
 * and/or after delegating the rest of the procesing using {@link ActionInvocation#invoke()}.
 * 
 * <!-- END SNIPPET: introduction -->
 * 
 * <p/>
 * 
 * <!-- START SNIPPET: parameterOverriding -->
 * 
 * Interceptor's parameter could be overriden through the following ways :-
 * 
 * <p/>
 * 
 * <b>Method 1:</b>
 * <pre>
 * &lt;action name="myAction" class="myActionClass"&gt;
 *   &lt;interceptor-ref name="exception"/&gt;
 *     &lt;interceptor-ref name="alias"/&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="servlet-config"/&gt;
 *     &lt;interceptor-ref name="prepare"/&gt;
 *     &lt;interceptor-ref name="i18n"/&gt;
 *     &lt;interceptor-ref name="chain"/&gt;
 *     &lt;interceptor-ref name="model-driven"/&gt;
 *     &lt;interceptor-ref name="fileUpload"/&gt;
 *     &lt;interceptor-ref name="static-params"/&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="conversionError"/&gt;
 *     &lt;interceptor-ref name="validation"&gt;
 *       &lt;param name="excludeMethods"&gt;myValidationExcudeMethod&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;interceptor-ref name="workflow"&gt;
 *       &lt;param name="excludeMethods"&gt;myWorkflowExcludeMethod&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 * &lt;/action&gt;
 * </pre>
 * 
 * <b>Method 2:</b>
 * <pre>
 * &lt;action name="myAction" class="myActionClass"&gt;
 *   &lt;interceptor-ref name="defaultStack"&gt;
 *     &lt;param name="validation.excludeMethods"&gt;myValidationExcludeMethod&lt;/param&gt;
 *     &lt;param name="workflow.excludeMethods"&gt;myWorkflowExcludeMethod&lt;/param&gt;
 *   &lt;/interceptor-ref&gt;
 * &lt;/action&gt;
 * </pre>
 * 
 * <p/>
 * 
 * In the first method, the whole default stack is copied and the parameter then 
 * changed accordingly.
 * 
 * <p/>
 * 
 * In the second method, the <interceptor-ref .../> refer to an existing 
 * interceptor-stack, namely default-stack in this example, and override the validator
 * and workflow interceptor excludeMethods typically in this case. Note that in the
 * <param ... /> tag, the name attribute contains a dot (.) the word before the dot(.)
 * specifies the interceptor name whose parameter is to be overriden and the word after
 * the dot (.) specifies the parameter itself. Essetially it is as follows :-
 * 
 * <pre>
 *    &lt;interceptor-name&gt;.&lt;parameter-name&gt;
 * </pre>
 * 
 * <b>Note</b> also that in this case the <interceptor-ref ... > name attribute 
 * is used to indicate an interceptor stack which makes sense as if it is refering 
 * to the interceptor itself it would be just using Method 1 describe above.
 * 
 * <!-- END SNIPPET: parameterOverriding -->
 * 
 * 
 * @author Jason Carreira
 * @version $Date: 2006-07-10 02:30:29 +0200 (Mo, 10 Jul 2006) $ $Id: Interceptor.java 1063 2006-07-10 00:30:29Z mrdon $
 */
public interface Interceptor extends Serializable {

    /**
     * Called to let an interceptor clean up any resources it has allocated.
     */
    void destroy();

    /**
     * Called after an interceptor is created, but before any requests are processed using
     * {@link #intercept(com.opensymphony.xwork2.ActionInvocation) intercept} , giving
     * the Interceptor a chance to initialize any needed resources.
     */
    void init();

    /**
     * Allows the Interceptor to do some processing on the request before and/or after the rest of the processing of the
     * request by the {@link ActionInvocation} or to short-circuit the processing and just return a String return code.
     *
     * @return the return code, either returned from {@link ActionInvocation#invoke()}, or from the interceptor itself.
     * @throws Exception any system-level error, as defined in {@link com.opensymphony.xwork2.Action#execute()}.
     */
    String intercept(ActionInvocation invocation) throws Exception;
}
