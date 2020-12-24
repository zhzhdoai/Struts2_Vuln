/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.validators;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.validator.ValidationException;

/**
 * 
 * 
 * An abstract base class that adds in the capability to populate the stack with
 * a fake parameter map when a conversion error has occurred and the 'repopulateField'
 * property is set to "true".
 * 
 * <p/>
 * 
 *
 * <!-- START SNIPPET: javadoc -->
 *
 * The capability of auto-repopulating the stack with a fake parameter map when 
 * a conversion error has occurred can be done with 'repopulateField' property 
 * set to "true". 
 *
 * <p/>
 *
 * This is typically usefull when one wants to repopulate the field with the original value 
 * when a conversion error occurred. Eg. with a textfield that only allows an Integer 
 * (the action class have an Integer field declared), upon conversion error, the incorrectly
 * entered integer (maybe a text 'one') will not appear when dispatched back. With 'repopulateField'
 * porperty set to true, it will, meaning the textfield will have 'one' as its value 
 * upon conversion error.
 * 
 * <!-- END SNIPPET: javadoc -->
 * 
 * <p/>
 * 
 * <pre>
 * <!-- START SNIPPET: exampleJspPage -->
 * 
 * &lt;!-- myJspPage.jsp --&gt;
 * &lt;ww:form action="someAction" method="POST"&gt;
 *   ....
 *   &lt;ww:textfield 
 *       label="My Integer Field"
 *       name="myIntegerField" /&gt;
 *   ....
 *   &lt;ww:submit /&gt;       
 * &lt;/ww:form&gt;
 * 
 * <!-- END SNIPPET: exampleJspPage -->
 * </pre>
 * 
 * <pre>
 * <!-- START SNIPPET: exampleXwork -->
 * 
 * &lt;!-- xwork.xml --&gt;
 * &lt;xwork&gt;
 * &lt;include file="xwork-default.xml" /&gt;
 * ....
 * &lt;package name="myPackage" extends="xwork-default"&gt;
 *   ....
 *   &lt;action name="someAction" class="example.MyActionSupport.java"&gt;
 *      &lt;result name="input"&gt;myJspPage.jsp&lt;/result&gt;
 *      &lt;result&gt;success.jsp&lt;/result&gt;
 *   &lt;/action&gt;
 *   ....
 * &lt;/package&gt;
 * ....
 * &lt;/xwork&gt;
 * 
 * <!-- END SNIPPET:exampleXwork -->
 * </pre>
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: exampleJava -->
 * 
 * &lt;!-- MyActionSupport.java --&gt;
 * public class MyActionSupport extends ActionSupport {
 *    private Integer myIntegerField;
 *    
 *    public Integer getMyIntegerField() { return this.myIntegerField; }
 *    public void setMyIntegerField(Integer myIntegerField) { 
 *       this.myIntegerField = myIntegerField; 
 *    }
 * }
 * 
 * <!-- END SNIPPET: exampleJava -->
 * </pre>
 * 
 * 
 * <pre>
 * <!-- START SNIPPET: exampleValidation -->
 * 
 * &lt;!-- MyActionSupport-someAction-validation.xml --&gt;
 * &lt;validators&gt;
 *   ...
 *   &lt;field name="myIntegerField"&gt;
 *      &lt;field-validator type="conversion"&gt;
 *         &lt;param name="repopulateField"&gt;true&lt;/param&gt;
 *         &lt;message&gt;Conversion Error (Integer Wanted)&lt;/message&gt;
 *      &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   ...
 * &lt;/validators&gt;
 * 
 * <!-- END SNIPPET: exampleValidation -->
 * </pre>
 * 
 * @author tm_jee
 * @version $Date: 2006-10-27 07:48:38 +0200 (Fr, 27 Okt 2006) $ $Id: RepopulateConversionErrorFieldValidatorSupport.java 1177 2006-10-27 05:48:38Z mrdon $
 */
public abstract class RepopulateConversionErrorFieldValidatorSupport extends FieldValidatorSupport {
	
	private static final Log _log = LogFactory.getLog(RepopulateConversionErrorFieldValidatorSupport.class);
	
	private String repopulateFieldAsString = "false";
	private boolean repopulateFieldAsBoolean = false;
	
	public String getRepopulateField() { 
		return repopulateFieldAsString;
	}
	
	public void setRepopulateField(String repopulateField) {
		this.repopulateFieldAsString = repopulateField == null ? repopulateField : repopulateField.trim();
		this.repopulateFieldAsBoolean = "true".equalsIgnoreCase(this.repopulateFieldAsString) ? (true) : (false);
	}

	public void validate(Object object) throws ValidationException {
		doValidate(object);
		if (repopulateFieldAsBoolean) {
			repopulateField(object);
		}
	}
	
	public void repopulateField(Object object) throws ValidationException {
		
		ActionInvocation invocation = ActionContext.getContext().getActionInvocation();
		Map conversionErrors = ActionContext.getContext().getConversionErrors();
		
		String fieldName = getFieldName();
		String fullFieldName = getValidatorContext().getFullFieldName(fieldName);
		Object value = conversionErrors.get(fullFieldName);
		
		final Map fakeParams = new LinkedHashMap();
		boolean doExprOverride = false;
		
		if (value instanceof String[]) {
			// take the first element, if possible
			String[] tmpValue = (String[]) value;
			if (tmpValue != null && (tmpValue.length > 0) ) {
				doExprOverride = true;
				fakeParams.put(fullFieldName, "'"+tmpValue[0]+"'");
			}
			else {
				_log.warn("value is an empty array of String or with first element in it as null ["+value+"], will not repopulate conversion error ");
			}
		}
		else if (value instanceof String) {
			String tmpValue = (String) value;
			doExprOverride = true;
			fakeParams.put(fullFieldName, "'"+tmpValue+"'");
		}
		else {
			// opps... it should be 
			_log.warn("conversion error value is not a String or array of String but instead is ["+value+"], will not repopulate conversion error");
		}
		
		if (doExprOverride) {
			invocation.addPreResultListener(new PreResultListener() {
				public void beforeResult(ActionInvocation invocation, String resultCode) {
					ValueStack stack = ActionContext.getContext().getValueStack();
					stack.setExprOverrides(fakeParams);
				}
			});
		}
	}
	
	protected abstract void doValidate(Object object) throws ValidationException;
}
