/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.util.ValueStack;


/**
 * <!-- START SNIPPET: description -->
 *
 * Watches for {@link ModelDriven} actions and adds the action's model on to the value stack.
 *
 * <p/> <b>Note:</b>  The ModelDrivenInterceptor must come before the both {@link StaticParametersInterceptor} and
 * {@link ParametersInterceptor} if you want the parameters to be applied to the model.
 * 
 * <p/> <b>Note:</b>  The ModelDrivenInterceptor will only push the model into the stack when the
 * model is not null, else it will be ignored.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>None</li>
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
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="model-driven"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * @author tm_jee
 * @version $Date: 2006-09-30 07:34:02 +0200 (Sa, 30 Sep 2006) $ $Id: ModelDrivenInterceptor.java 1142 2006-09-30 05:34:02Z mrdon $
 */
public class ModelDrivenInterceptor extends AbstractInterceptor {

    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ModelDriven) {
            ModelDriven modelDriven = (ModelDriven) action;
            ValueStack stack = invocation.getStack();
            if (modelDriven.getModel() !=  null) {
            	stack.push(modelDriven.getModel());
            }
        }
        return invocation.invoke();
    }
}
