/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;


/**
 * <!-- START SNIPPET: javadoc -->
 * RequiredStringValidator checks that a String field is non-null and has a length > 0.
 * (i.e. it isn't "").  The "trim" parameter determines whether it will {@link String#trim() trim}
 * the String before performing the length check.  If unspecified, the String will be trimmed.
 * <!-- END SNIPPET: javadoc -->
 * <p/>
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 		<li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 *      <li>trim - trim the field name value before validating (default is true)</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: examples -->
 *     &lt;validators&gt;
 *         &lt;!-- Plain-Validator Syntax --&gt;
 *         &lt;validator type="requiredstring"&gt;
 *             &lt;param name="fieldName"&gt;username&lt;/param&gt;
 *             &lt;param name="trim"&gt;true&lt;/param&gt;
 *             &lt;message&gt;username is required&lt;/message&gt;
 *         &lt;/validator&gt;
 *         
 *         &lt;!-- Field-Validator Syntax --&gt;
 *         &lt;field name="username"&gt;
 *         	  &lt;field-validator type="requiredstring"&gt;
 *                 &lt;param name="trim"&gt;true&lt;/param&gt;
 *                 &lt;message&gt;username is required&lt;/message&gt;
 *            &lt;/field-validator&gt;
 *         &lt;/field&gt;
 *     &lt;/validators&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 * 
 * @author rainerh
 * @version $Date: 2006-07-10 02:30:29 +0200 (Mo, 10 Jul 2006) $ $Id: RequiredStringValidator.java 1063 2006-07-10 00:30:29Z mrdon $
 */
public class RequiredStringValidator extends FieldValidatorSupport {

    private boolean doTrim = true;


    public void setTrim(boolean trim) {
        doTrim = trim;
    }

    public boolean getTrim() {
        return doTrim;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = getFieldName();
        Object value = this.getFieldValue(fieldName, object);

        if (!(value instanceof String)) {
            addFieldError(fieldName, object);
        } else {
            String s = (String) value;

            if (doTrim) {
                s = s.trim();
            }

            if (s.length() == 0) {
                addFieldError(fieldName, object);
            }
        }
    }
}
