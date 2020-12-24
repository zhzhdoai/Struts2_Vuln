/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;


/**
 * <!-- START SNIPPET: javadoc -->
 * A Non-Field Level validator that validates based on regular expression supplied.
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * 
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 	 <li>expression - the Ognl expression to be evaluated against the stack (Must evaluate to a Boolean)</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 *
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 *     &lt;validators&gt;
 *           &lt;validator type="expression"&gt;
 *              &lt;param name="expression"&gt; .... &lt;/param&gt;
 *              &lt;message&gt;Failed to meet Ognl Expression  .... &lt;/message&gt;
 *           &lt;/validator&gt;
 *     &lt;/validators&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Jason Carreira
 */
// START SNIPPET: global-level-validator
public class ExpressionValidator extends ValidatorSupport {

    private String expression;


    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void validate(Object object) throws ValidationException {
        Boolean answer = Boolean.FALSE;
        Object obj = null;

        try {
            obj = getFieldValue(expression, object);
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            // let this pass, but it will be logged right below
        }

        if ((obj != null) && (obj instanceof Boolean)) {
            answer = (Boolean) obj;
        } else {
            log.warn("Got result of " + obj + " when trying to get Boolean.");
        }

        if (!answer.booleanValue()) {
            if (log.isDebugEnabled()) log.debug("Validation failed on expression " + expression + " with validated object "+ object);
            addActionError(object);
        }
    }
}
// END SNIPPET: global-level-validator 

