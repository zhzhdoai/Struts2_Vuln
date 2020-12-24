/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.validators;

import java.util.Date;


/**
 * <!-- START SNIPPET: javadoc -->
 * 
 * Field Validator that checks if the date supplied is within a specific range.
 * 
 * <b>NOTE:</b> If no date converter is specified, XWorkBasicConverter will kick
 * in to do the date conversion, which by default using the <code>Date.SHORT</code> format using 
 * the a programmatically specified locale else falling back to the system 
 * default locale.
 * 
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * <p/>
 *
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 		<li>fieldName - The field name this validator is validating. Required if using Plain-Validator Syntax otherwise not required</li>
 *      <li>min - the min date range. If not specified will not be checked.</li>
 *      <li>max - the max date range. If not specified will not be checked.</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: examples -->
 *    &lt;validators>
 *    		&lt;!-- Plain Validator syntax --&gt;
 *    		&lt;validator type="date"&gt;
 *    	        &lt;param name="fieldName"&gt;birthday&lt;/param&gt;
 *              &lt;param name="min"&gt;01/01/1990&lt;/param&gt;
 *              &lt;param name="max"&gt;01/01/2000&lt;/param&gt;
 *              &lt;message&gt;Birthday must be within ${min} and ${max}&lt;/message&gt;
 *    		&lt;/validator&gt;
 *    
 *          &lt;!-- Field Validator Syntax --&gt;
 *          &lt;field name="birthday"&gt;
 *          	&lt;field-validator type="date"&gt;
 *           	    &lt;param name="min"&gt;01/01/1990&lt;/param&gt;
 *                  &lt;param name="max"&gt;01/01/2000&lt;/param&gt;
 *                  &lt;message&gt;Birthday must be within ${min} and ${max}&lt;/message&gt;
 *          	&lt;/field&gt;
 *          &lt;/field&gt;
 *    
 *    &lt;/validators&gt;
 * <!-- END SNIPPET: examples -->
 * </pre>
 * 
 *
 * @author Jason Carreira
 * @version $Date: 2006-10-27 07:48:38 +0200 (Fr, 27 Okt 2006) $ $Id: DateRangeFieldValidator.java 1177 2006-10-27 05:48:38Z mrdon $
 */
public class DateRangeFieldValidator extends AbstractRangeValidator {

    private Date max;
    private Date min;


    public void setMax(Date max) {
        this.max = max;
    }

    public Date getMax() {
        return max;
    }

    public void setMin(Date min) {
        this.min = min;
    }

    public Date getMin() {
        return min;
    }

    protected Comparable getMaxComparatorValue() {
        return max;
    }

    protected Comparable getMinComparatorValue() {
        return min;
    }
}
