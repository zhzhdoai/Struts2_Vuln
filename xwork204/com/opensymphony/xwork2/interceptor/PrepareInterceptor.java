/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Preparable;


/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor calls prepare() on actions which implement
 * {@link Preparable}. This interceptor is very useful for any situation where
 * you need to ensure some logic runs before the actual execute method runs.
 * 
 * <p/> A typical use of this is to run some logic to load an object from the
 * database so that when parameters are set they can be set on this object. For
 * example, suppose you have a User object with two properties: <i>id</i> and
 * <i>name</i>. Provided that the params interceptor is called twice (once
 * before and once after this interceptor), you can load the User object using
 * the id property, and then when the second params interceptor is called the
 * parameter <i>user.name</i> will be set, as desired, on the actual object
 * loaded from the database. See the example for more info.
 *
 * <p/>
 * <b>Note:</b> Since XWork 2.0.2, this interceptor extends {@link MethodFilterInterceptor}, therefore being
 * able to deal with excludeMethods / includeMethods parameters. See [Workflow Interceptor]
 * (class {@link DefaultWorkflowInterceptor}) for documentation and examples on how to use this feature.
 *
 * <p/><b>Update</b>: Added logic to execute a prepare{MethodName} and conditionally
 * the a general prepare() Method, depending on the 'alwaysInvokePrepare' parameter/property
 * which is by default true. This allows us to run some logic based on the method 
 * name we specify in the {@link com.opensymphony.xwork2.ActionProxy}. For example, you can specify a
 * prepareInput() method that will be run before the invocation of the input method.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>alwaysInvokePrepare - Default to true. If true, prepare will always be invoked, 
 * otherwise it will not.</li>
 *
 * </ul>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Extending the interceptor:</u>
 *
 * <p/>
 *
 * <!-- START SNIPPET: extending -->
 *
 * There are no known extension points to this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;!-- Calls the params interceptor twice, allowing you to
 *       pre-load data for the second time parameters are set --&gt;
 *  &lt;action name=&quot;someAction&quot; class=&quot;com.examples.SomeAction&quot;&gt;
 *      &lt;interceptor-ref name=&quot;params&quot;/&gt;
 *      &lt;interceptor-ref name=&quot;prepare&quot;/&gt;
 *      &lt;interceptor-ref name=&quot;basicStack&quot;/&gt;
 *      &lt;result name=&quot;success&quot;&gt;good_result.ftl&lt;/result&gt;
 *  &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * Date: Nov 5, 2003 2:33:11 AM
 *
 * @author Jason Carreira
 * @author Philip Luppens
 * @author tm_jee
 * @author Rene Gielen
 * @see com.opensymphony.xwork2.Preparable
 */
public class PrepareInterceptor extends MethodFilterInterceptor {
	
	private static final long serialVersionUID = -5216969014510719786L;

	private static final Log _log = LogFactory.getLog(PrepareInterceptor.class);
	
	private final static String PREPARE_PREFIX = "prepare";
	private final static String ALT_PREPARE_PREFIX = "prepareDo";

	private boolean alwaysInvokePrepare = true;
	
	public void setAlwaysInvokePrepare(String alwaysInvokePrepare) {
		this.alwaysInvokePrepare = Boolean.parseBoolean(alwaysInvokePrepare);
	}
	
    public String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof Preparable) {
            	try {
            		PrefixMethodInvocationUtil.invokePrefixMethod(invocation, 
            			new String[] { PREPARE_PREFIX, ALT_PREPARE_PREFIX });
            	}
            	catch(Exception e) {
            		// just in case there's an exception while doing reflection, 
            		// we still want prepare() to be able to get called.
            		_log.warn("an exception occured while trying to execute prefixed method", e);
            	}
            	if (alwaysInvokePrepare) {
            		((Preparable) action).prepare();
            	}
        }
        return invocation.invoke();
    }
}
