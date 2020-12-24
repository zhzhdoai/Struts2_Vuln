/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;


/**
 * <!-- START SNIPPET: javadoc -->
 * Validates a field using an OGNL expression.
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * 
 * <!-- START SNIPPET: parameters -->
 * <ul>
 *    <li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 *    <li>expression - The Ognl expression (must evaluate to a boolean) which is to be evalidated the stack</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 *    &lt;!-- Plain Validator Syntax --&gt;
 *    &lt;validators&gt;
 *        &lt;!-- Plain Validator Syntax --&gt;
 *        &lt;validator type="fieldexpression"&gt;
 *           &lt;param name="fieldName"&gt;myField&lt;/param&gt;
 *           &lt;param name="expression"&gt;&lt;![CDATA[#myCreditLimit &gt; #myGirfriendCreditLimit]]&gt;&lt;/param&gt;
 *           &lt;message&gt;My credit limit should be MORE than my girlfriend&lt;/message&gt;
 *        &lt;validator&gt;
 *        
 *        &lt;!-- Field Validator Syntax --&gt;
 *        &lt;field name="myField"&gt;
 *            &lt;field-validator type="fieldexpression"&gt;
 *                &lt;param name="expression"&gt;&lt;![CDATA[#myCreditLimit &gt; #myGirfriendCreditLimit]]&gt;&lt;/param&gt;
 *                &lt;message&gt;My credit limit should be MORE than my girlfriend&lt;/message&gt;
 *            &lt;/field-validator&gt;
 *        &lt;/field&gt;
 *        
 *    &lt;/vaidators&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 *
 * @author $Author: mrdon $
 * @version $Revision: 1063 $
 */
public class FieldExpressionValidator extends FieldValidatorSupport {

    private String expression;


    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();

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
            addFieldError(fieldName, object);
        }
    }
}
