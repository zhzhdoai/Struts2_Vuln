/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ValidationAware;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.XWorkConverter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * <!-- START SNIPPET: description -->
 *
 * This interceptor adds any error found in the {@link ActionContext}'s conversionErrors map as a field error (provided
 * that the action implements {@link ValidationAware}). In addition, any field that contains a validation error has its
 * original value saved such that any subsequent requests for that value return the original value rather than the value
 * in the action. This is important because if the value "abc" is submitted and can't be converted to an int, we want to
 * display the original string ("abc") again rather than the int value (likely 0, which would make very little sense to
 * the user).
 *
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
 * Because this interceptor is not web-specific, it abstracts the logic for whether an error should be added. This
 * allows for web-specific interceptors to use more complex logic in the {@link #shouldAddError} method for when a value
 * has a conversion error but is null or empty or otherwise indicates that the value was never actually entered by the
 * user.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="params"/&gt;
 *     &lt;interceptor-ref name="conversionError"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * ConversionErrorInterceptor adds conversion errors from the ActionContext to the Action's field errors.
 *
 * @author Jason Carreira
 */
public class ConversionErrorInterceptor extends AbstractInterceptor {
    public static final String ORIGINAL_PROPERTY_OVERRIDE = "original.property.override";

    protected Object getOverrideExpr(ActionInvocation invocation, Object value) {
        return "'" + value + "'";
    }

    public String intercept(ActionInvocation invocation) throws Exception {

        ActionContext invocationContext = invocation.getInvocationContext();
        Map conversionErrors = invocationContext.getConversionErrors();
        ValueStack stack = invocationContext.getValueStack();

        HashMap fakie = null;

        for (Iterator iterator = conversionErrors.entrySet().iterator();
             iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String propertyName = (String) entry.getKey();
            Object value = entry.getValue();

            if (shouldAddError(propertyName, value)) {
                String message = XWorkConverter.getConversionErrorMessage(propertyName, stack);

                Object action = invocation.getAction();
                if (action instanceof ValidationAware) {
                    ValidationAware va = (ValidationAware) action;
                    va.addFieldError(propertyName, message);
                }

                if (fakie == null) {
                    fakie = new HashMap();
                }

                fakie.put(propertyName, getOverrideExpr(invocation, value));
            }
        }

        if (fakie != null) {
            // if there were some errors, put the original (fake) values in place right before the result
            stack.getContext().put(ORIGINAL_PROPERTY_OVERRIDE, fakie);
            invocation.addPreResultListener(new PreResultListener() {
                public void beforeResult(ActionInvocation invocation, String resultCode) {
                    Map fakie = (Map) invocation.getInvocationContext().get(ORIGINAL_PROPERTY_OVERRIDE);

                    if (fakie != null) {
                        invocation.getStack().setExprOverrides(fakie);
                    }
                }
            });
        }
        return invocation.invoke();
    }

    protected boolean shouldAddError(String propertyName, Object value) {
        return true;
    }
}
