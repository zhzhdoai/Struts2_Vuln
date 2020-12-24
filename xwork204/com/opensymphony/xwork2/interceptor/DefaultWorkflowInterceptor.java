/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Validateable;
import com.opensymphony.xwork2.ValidationAware;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <!-- START SNIPPET: description -->
 *
 * An interceptor that does some basic validation workflow before allowing the interceptor chain to continue.
 *
 * <p/>This interceptor does nothing if the name of the method being invoked is specified in the <b>excludeMethods</b>
 * parameter. <b>excludeMethods</b> accepts a comma-delimited list of method names. For example, requests to
 * <b>foo!input.action</b> and <b>foo!back.action</b> will be skipped by this interceptor if you set the
 * <b>excludeMethods</b> parameter to "input, back".
 *
 * <p/>The order of execution in the workflow is:
 *
 * <ol>
 *
 * <li>If the action being executed implements {@link Validateable}, the action's {@link Validateable#validate()
 * validate} method is called.</li>
 *
 * <li>Next, if the action implements {@link ValidationAware}, the action's {@link ValidationAware#hasErrors()
 * hasErrors} method is called. If this method returns true, this interceptor stops the chain from continuing and
 * immediately returns {@link Action#INPUT}</li>
 *
 * </ol>
 *
 * <p/>
 * <b>Note:</b> if the action doesn't implement either interface, this interceptor effectively does nothing. This
 * interceptor is often used with the <b>validation</b> interceptor. However, it does not have to be, especially if you
 * wish to write all your validation rules by hand in the validate() method rather than in XML files.
 *
 * <p/>
 *
 * <b>Note:</b> As this method extends off MethodFilterInterceptor, it is capable of
 * deciding if it is applicable only to selective methods in the action class. This is done by adding param tags
 * for the interceptor element, naming either a list of excluded method names and/or a list of included method
 * names, whereby includeMethods overrides excludedMethods. A single * sign is interpreted as wildcard matching
 * all methods for both parameters.
 * See {@link MethodFilterInterceptor} for more info.
 * 
 * <p/><b>Update:</b> Added logic to execute a validate{MethodName} and then conditionally
 * followed than a general validate method, depending on the 'alwaysInvokeValidate' 
 * parameter/property which  is by default set to true.
 * This allows us to run some validation logic based on the method name we specify in the 
 * ActionProxy. For example, you can specify a validateInput() method 
 * that will be run before the invocation of the input method.
 * 
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>alwaysInvokeValidate - Default to true. If true validate() method will always
 * be invoked, otherwise it will not.</li>
 * <li>inputResultName - Default to "input". Determine the result name to be returned when
 * an action / field error is found.</li>
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
 * There are no known extension points for this interceptor.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * 
 * &lt;-- In this case myMethod as well as mySecondMethod of the action class
 *        will not pass through the workflow process --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"&gt;
 *         &lt;param name="excludeMethods"&gt;myMethod,mySecondMethod&lt;/param&gt;
 *     &lt;/interceptor-ref name="workflow"&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * 
 * &lt;-- In this case, the result named "error" will be used when 
 *        an action / field error is found --&gt;
 * &lt;-- The Interceptor will only be applied for myWorkflowMethod method of action
 *        classes, since this is the only included method while any others are excluded --&gt;
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="validation"/&gt;
 *     &lt;interceptor-ref name="workflow"&gt;
 *        &lt;param name="inputResultName"&gt;error&lt;/param&gt;
*         &lt;param name="excludeMethods"&gt;*&lt;/param&gt;
*         &lt;param name="includeMethods"&gt;myWorkflowMethod&lt;/param&gt;
 *     &lt;/interceptor-ref&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * 
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 * @author Philip Luppens
 * @author tm_jee
 *
 * @version $Date: 2007-03-30 10:56:25 +0200 (Fri, 30 Mar 2007) $ $Id: DefaultWorkflowInterceptor.java 1397 2007-03-30 08:56:25Z rgielen $
 */
public class DefaultWorkflowInterceptor extends MethodFilterInterceptor {
	
	private static final long serialVersionUID = 7563014655616490865L;

	private static final Log _log = LogFactory.getLog(DefaultWorkflowInterceptor.class);
	
	private final static String VALIDATE_PREFIX = "validate";
	private final static String ALT_VALIDATE_PREFIX = "validateDo";
	
	private boolean alwaysInvokeValidate = true;
	
	private String inputResultName = Action.INPUT;
	
	/**
	 * Determine if {@link Validateable}'s <code>validate()</code> should always 
	 * be invoked. Default to "true".
	 * 
	 * @param alwaysInvokeValidate
	 */
	public void setAlwaysInvokeValidate(String alwaysInvokeValidate) {
		this.alwaysInvokeValidate = Boolean.parseBoolean(alwaysInvokeValidate);
	}
	
	/**
	 * Set the <code>inputResultName</code> (result name to be returned when 
	 * a action / field error is found registered). Default to {@link Action#INPUT}
	 * 
	 * @param inputResultName
	 */
	public void setInputResultName(String inputResultName) {
		this.inputResultName = inputResultName;
	}
	
	/**
	 * Intercept {@link ActionInvocation} and returns a <code>inputResultName</code>
	 * when action / field errors is found registered.
	 * 
	 * @return String result name
	 */
    protected String doIntercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        
        
        if (action instanceof Validateable) {
        	// keep exception that might occured in validateXXX or validateDoXXX
        	Exception exception = null; 
        	
            Validateable validateable = (Validateable) action;
            if (_log.isDebugEnabled()) {
            	_log.debug("Invoking validate() on action "+validateable);
            }
            
            try {
            	PrefixMethodInvocationUtil.invokePrefixMethod(
            			invocation, 
            			new String[] { VALIDATE_PREFIX, ALT_VALIDATE_PREFIX });
            }
            catch(Exception e) {
            	// If any exception occurred while doing reflection, we want 
            	// validate() to be executed
            	_log.warn("an exception occured while executing the prefix method", e);
            	exception = e;
            }
            
            
            if (alwaysInvokeValidate) {
            	validateable.validate();
            }
            
            if (exception != null) { 
            	// rethrow if something is wrong while doing validateXXX / validateDoXXX 
            	throw exception;
            }
        }
        

        if (action instanceof ValidationAware) {
            ValidationAware validationAwareAction = (ValidationAware) action;

            if (validationAwareAction.hasErrors()) {
            	if (_log.isDebugEnabled()) {
            		_log.debug("Errors on action "+validationAwareAction+", returning result name 'input'");
            	}
            	return inputResultName;
            }
        }

        return invocation.invoke();
    }
}
