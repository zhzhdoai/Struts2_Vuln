/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor.annotations;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Comparator;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.util.AnnotationUtils;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;

/**
 * <!-- START SNIPPET: javadoc -->
 * <p>Invokes any annotated methods on the action. Specifically, it supports the following
 * annotations:
 * <ul>
 * <li> &#64;{@link Before} - will be invoked before the action method. If the returned value is not null, it is
 * returned as the action result code</li>
 * <li> &#64;{@link BeforeResult} - will be invoked after the action method but before the result execution</li>
 * <li> &#64;{@link After} - will be invoked after the action method and result execution</li>
 * </ul>
 * </p>
 * <p/>
 * <p>There can be multiple methods marked with the same annotations, but the order of their execution
 * is not guaranteed. However, the annotated methods on the superclass chain are guaranteed to be invoked before the
 * annotated method in the current class in the case of a {@link Before} annotations and after, if the annotations is
 * {@link After}.</p>
 * <!-- END SNIPPET: javadoc -->
 *
 * <pre>
 * <!-- START SNIPPET: javacode -->
 *  public class BaseAnnotatedAction {
 *  	protected String log = "";
 *
 *  	&#64;Before
 *  	public String baseBefore() {
 *  		log = log + "baseBefore-";
 *  		return null;
 *  	}
 *  }
 *
 *  public class AnnotatedAction extends BaseAnnotatedAction {
 *  	&#64;Before
 *  	public String before() {
 *  		log = log + "before";
 *  		return null;
 *  	}
 *
 *  	public String execute() {
 *  		log = log + "-execute";
 *  		return Action.SUCCESS;
 *  	}
 *
 *  	&#64;BeforeResult
 *  	public void beforeResult() throws Exception {
 *  		log = log +"-beforeResult";
 *  	}
 *
 *  	&#64;After
 *  	public void after() {
 *  		log = log + "-after";
 *  	}
 *  }
 * <!-- END SNIPPET: javacode -->
 *  </pre>
 * <p/>
 * <!-- START SNIPPET: example -->
 * <p>With the interceptor applied and the action executed on <code>AnnotatedAction</code> the log
 * instance variable will contain <code>baseBefore-before-execute-beforeResult-after</code>.</p>
 * <!-- END SNIPPET: example -->
 *
 * <p/>Configure a stack in xwork.xml that replaces the PrepareInterceptor with the AnnotationWorkflowInterceptor:
 * <pre>
 * <!-- START SNIPPET: stack -->
 * &lt;interceptor-stack name="annotatedStack"&gt;
 *	&lt;interceptor-ref name="static-params"/&gt;
 *	&lt;interceptor-ref name="params"/&gt;
 *	&lt;interceptor-ref name="conversionError"/&gt;
 *	&lt;interceptor-ref name="annotationInterceptor"/&gt;
 * &lt;/interceptor-stack&gt;
 *  <!-- END SNIPPET: stack -->
 * </pre>

 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Rainer Hermanns
 */
public class AnnotationWorkflowInterceptor implements Interceptor, PreResultListener {
    /**
     * Discovers annotated methods on the action and calls them according to the workflow
     *
     * @see com.opensymphony.xwork2.interceptor.Interceptor#intercept(com.opensymphony.xwork2.ActionInvocation)
     */
    public String intercept(ActionInvocation invocation) throws Exception {
        final Object action = invocation.getAction();
        invocation.addPreResultListener(this);
        List<Method> methods = AnnotationUtils.findAnnotatedMethods(action.getClass(), Before.class);
        if (methods != null && methods.size() > 0) {
            Collections.sort(methods, new Comparator<Method>() {
                public int compare(Method method1, Method method2) {
                    return method2.getAnnotation(Before.class).priority()
                            - method1.getAnnotation(Before.class).priority();
                }
            });
            for (Method m : methods) {
                // action superclass methods first then action methods
                final String resultCode = (String) m
                        .invoke(action, (Object[]) null);
                if (resultCode != null) {
                    // shortcircuit execution
                    return resultCode;
                }
            }
        }

        String invocationResult = invocation.invoke();

        // invoke any @After methods
        methods = AnnotationUtils.findAnnotatedMethods(action.getClass(), After.class);

        if (methods != null && methods.size() > 0) {
            // action methods first then action superclass methods
            Collections.sort(methods, new Comparator<Method>() {
                public int compare(Method method1, Method method2) {
                    return method2.getAnnotation(After.class).priority()
                            - method1.getAnnotation(After.class).priority();
                }
            });
            for (Method m : methods) {
                m.invoke(action, (Object[]) null);
            }
        }

        return invocationResult;
    }

    public void destroy() {
    }

    public void init() {
    }

    /**
     * Invokes any &#64;BeforeResult annotated methods
     *
     * @see com.opensymphony.xwork2.interceptor.PreResultListener#beforeResult(com.opensymphony.xwork2.ActionInvocation,String)
     */
    public void beforeResult(ActionInvocation invocation, String resultCode) {
        Object action = invocation.getAction();
        List<Method> methods = AnnotationUtils.findAnnotatedMethods(action.getClass(), BeforeResult.class);

        if (methods != null && methods.size() > 0) {
            Collections.sort(methods, new Comparator<Method>() {
                public int compare(Method method1, Method method2) {
                    return method2.getAnnotation(BeforeResult.class).priority()
                            - method1.getAnnotation(BeforeResult.class).priority();
                }
            });
            for (Method m : methods) {
                try {
                    m.invoke(action, (Object[]) null);
                } catch (Exception e) {
                    throw new XWorkException(e);
                }
            }
        }
    }
}
