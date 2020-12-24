/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * A utility class for invoking prefixed methods in action class.
 * 
 * Interceptors that made use of this class are 
 * <ul>
 * 	 <li>DefaultWorkflowInterceptor </li>
 *   <li>PrepareInterceptor </li>
 * </ul>
 * 
 * <p/>
 * 
 * <!-- START SNIPPET: javadocDefaultWorkflowInterceptor -->
 * 
 * <b>In DefaultWorkflowInterceptor</b>
 * <p>applies only when action implements {@link com.opensymphony.xwork2.Validateable}</p>
 * <ol>
 *    <li>if the action class have validate{MethodName}(), it will be invoked</li>
 *    <li>else if the action class have validateDo{MethodName}(), it will be invoked</li>
 *    <li>no matter if 1] or 2] is performed, if alwaysInvokeValidate property of the interceptor is "true" (which is by default "true"), validate() will be invoked.</li>
 * </ol>
 * 
 * <!-- END SNIPPET: javadocDefaultWorkflowInterceptor -->
 * 
 * 
 * <!-- START SNIPPET: javadocPrepareInterceptor -->
 * 
 * <b>In PrepareInterceptor</b>
 * <p>Applies only when action implements Preparable</p>
 * <ol>
 *    <li>if the action class have prepare{MethodName}(), it will be invoked</li>
 *    <li>else if the action class have prepareDo(MethodName()}(), it will be invoked</li>
 *    <li>no matter if 1] or 2] is performed, if alwaysinvokePrepare property of the interceptor is "true" (which is by default "true"), prepare() will be invoked.</li>
 * </ol>
 * 
 * <!-- END SNIPPET: javadocPrepareInterceptor -->
 * 
 * @author Philip Luppens
 * @author tm_jee
 * @version $Date: 2006-11-23 21:33:15 +0100 (Do, 23 Nov 2006) $ $Id: PrefixMethodInvocationUtil.java 1223 2006-11-23 20:33:15Z rainerh $
 */
public class PrefixMethodInvocationUtil {
	
	private static final Log _log = LogFactory.getLog(PrefixMethodInvocationUtil.class);

	/**
	 * This method will prefix <code>actionInvocation</code>'s <code>ActionProxy</code>'s
	 * <code>method</code> with <code>prefixes</code> before invoking the prefixed method.
	 * Order of the <code>prefixes</code> is important, as this method will return once 
	 * a prefixed method is found in the action class.
	 * 
	 * <p/>
	 * 
	 * For example, with
	 * <pre>
	 *   invokePrefixMethod(actionInvocation, new String[] { "prepare", "prepareDo" });
	 * </pre>
	 * 
	 * Assuming <code>actionInvocation.getProxy(),getMethod()</code> returns "submit", 
	 * the order of invocation would be as follows:-
	 * <ol>
	 *   <li>prepareSubmit()</li>
	 *   <li>prepareDoSubmit()</li>
	 * </ol>
	 * 
	 * If <code>prepareSubmit()</code> exists, it will be invoked and this method 
	 * will return, <code>prepareDoSubmit()</code> will NOT be invoked. 
	 * 
	 * <p/>
	 * 
	 * On the other hand, if <code>prepareDoSubmit()</code> does not exists, and 
	 * <code>prepareDoSubmit()</code> exists, it will be invoked.
	 * 
	 * <p/>
	 * 
	 * If none of those two methods exists, nothing will be invoked.
	 * 
	 * @param actionInvocation
	 * @param prefixes
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public static void invokePrefixMethod(ActionInvocation actionInvocation, String[] prefixes) throws InvocationTargetException, IllegalAccessException {
		Object action = actionInvocation.getAction();
		
		String methodName = actionInvocation.getProxy().getMethod();
		
		if (methodName == null) {
			 // TODO: clean me up
			 /* if null returns (possible according to the docs), 
			 * use the default execute - this should be a static somewhere ?
			 */
			methodName = "execute";
		}
		
		Method method = getPrefixedMethod(prefixes, methodName, action);
		if (method != null) {
			method.invoke(action, new Object[0]);
		}
	}
	
	
	/**
	 * This method returns a {@link Method} in <code>action</code>. The method 
	 * returned is found by searching for method in <code>action</code> whose method name
	 * is equals to the result of appending each <code>prefixes</code>
	 * to <code>methodName</code>. Only the first method found will be returned, hence
	 * the order of <code>prefixes</code> is important. If none is found this method
	 * will return null.
	 * 
	 * @param prefixes the prefixes to prefix the <code>methodName</code>
	 * @param methodName the method name to be prefixed with <code>prefixes</code>
	 * @param action the action class of which the prefixed method is to be search for.
	 * @return a {@link Method} if one is found, else null.
	 */
	public static Method getPrefixedMethod(String[] prefixes, String methodName, Object action) {
		assert(prefixes != null);
		String capitalizedMethodName = capitalizeMethodName(methodName);
		for (int a=0; a< prefixes.length; a++) {
			String prefixedMethodName = prefixes[a]+capitalizedMethodName;
			try {
				Method method = action.getClass().getMethod(prefixedMethodName, new Class[0]);
				return method;
			}
			catch(NoSuchMethodException e) {
				// hmm -- OK, try next prefix
				if (_log.isDebugEnabled()) {
					_log.debug("cannot find method ["+prefixedMethodName+"] in action ["+action+"]");
				}
			}
		}
		return null;
	}
	
	/**
	 * This method capitalized the first character of <code>methodName</code>.
	 * <p/>
	 * 
	 * eg.
	 * <pre>
	 *   capitalizeMethodName("someMethod");
	 * </pre>
	 * 
	 * will return "SomeMethod".
	 * 
	 * @param methodName
	 * @return String
	 */
	public static String capitalizeMethodName(String methodName) {
		assert(methodName != null);
		return methodName = methodName.substring(0, 1).toUpperCase()
							+ methodName.substring(1);
	}
}
