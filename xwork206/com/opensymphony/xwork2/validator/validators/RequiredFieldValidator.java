/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;


/**
 * <!-- START SNIPPET: javadoc -->
 * RequiredFieldValidator checks if the specified field is not null.
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 * 
 * 
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 		<li>fieldName - field name if plain-validator syntax is used, not needed if field-validator syntax is used</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 * 	   &lt;validators&gt;
 * 
 *         &lt;!-- Plain Validator Syntax --&gt;
 *         &lt;validator type="required"&gt;
 *             &lt;param name="fieldName"&gt;username&lt;/param&gt;
 *             &lt;message&gt;username must not be null&lt;/message&gt;
 *         &lt;/validator&gt;
 * 
 * 
 *         &lt;!-- Field Validator Syntax --&gt;
 *         &lt;field name="username"&gt;
 *             &lt;field-validator type="required"&gt;
 *             	   &lt;message&gt;username must not be null&lt;/message&gt;
 *             &lt;/field-validator&gt;
 *         &lt;/field&gt;
 * 
 *     &lt;/validators&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * 
 *
 * @author rainerh
 * @version $Revision: 1063 $
 */
public class RequiredFieldValidator extends FieldValidatorSupport {

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        if (value == null) {
            addFieldError(fieldName, object);
        }
    }
}
