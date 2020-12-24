package com.opensymphony.xwork2.interceptor;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.TextParseUtil;

/**
 * <!-- START SNIPPET: description -->
 * This is a simple XWork interceptor that allows parameters (matching
 * one of the paramNames attribute csv value) to be 
 * removed from the parameter map if they match a certain value
 * (matching one of the paramValues attribute csv value), before they 
 * are set on the action. A typical usage would be to want a dropdown/select 
 * to map onto a boolean value on an action. The select had the options 
 * none, yes and no with values -1, true and false. The true and false would 
 * map across correctly. However the -1 would be set to false. 
 * This was not desired as one might needed the value on the action to stay null. 
 * This interceptor fixes this by preventing the parameter from ever reaching 
 * the action.
 * <!-- END SNIPPET: description -->
 * 
 * 
 * <!-- START SNIPPET: parameters -->
 * <ul>
 * 	<li>paramNames - A comma separated value (csv) indicating the parameter name 
 * 								    whose param value should be considered that if they match any of the
 *                                     comma separated value (csv) from paramValues attribute, shall be 
 *                                     removed from the parameter map such that they will not be applied
 *                                     to the action</li>
 * 	<li>paramValues - A comma separated value (csv) indicating the parameter value that if 
 * 							      matched shall have its parameter be removed from the parameter map 
 * 							      such that they will not be applied to the action</li>
 * </ul>
 * <!-- END SNIPPET: parameters -->
 * 
 * 
 * <!-- START SNIPPET: extending -->
 * No intended extension point
 * <!-- END SNIPPET: extending -->
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 *	
 * &lt;action name="sample" class="org.martingilday.Sample"&gt;
 * 	&lt;interceptor-ref name="paramRemover"&gt;
 *   		&lt;param name="paramNames"&gt;aParam,anotherParam&lt;/param&gt;
 *   		&lt;param name="paramValues"&gt;--,-1&lt;/param&gt;
 * 	&lt;/interceptor-ref&gt;
 * 	&lt;interceptor-ref name="default-stack" /&gt;
 * 	...
 * &lt;/action&gt;
 *  
 * <!-- END SNIPPET: example -->
 * </pre>
 *  
 *  
 * @author martin.gilday
 * @version $Date$ $Id$
 */
public class ParameterRemoverInterceptor extends AbstractInterceptor {

	private static final Log LOG = LogFactory.getLog(ParameterRemoverInterceptor.class);

	private static final long serialVersionUID = 1;

	private Set paramNames = Collections.EMPTY_SET;

	private Set paramValues = Collections.EMPTY_SET;

	
	/**
	 * Decide if the parameter should be removed from the parameter map based on
	 * <code>paramNames</code> and <code>paramValues</code>.
	 * 
	 * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor
	 */
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		if (!(invocation.getAction() instanceof NoParameters)
				&& (null != this.paramNames)) {
			ActionContext ac = invocation.getInvocationContext();
			final Map parameters = ac.getParameters();

			if (parameters != null) {
				for (Iterator i = paramNames.iterator(); i.hasNext(); ) {
					Object removeName = i.next();
					
					// see if the field is in the parameter map
					if (parameters.containsKey(removeName)) {

						try {
							String[] values = (String[]) parameters
									.get(removeName);
							String value = values[0];
							if (null != value && this.paramValues.contains(value)) {
								parameters.remove(removeName);
							}
						} catch (Exception e) {
							LOG.error("Failed to convert parameter to string", e);
						}
					}
				}
			}
		}
		return invocation.invoke();
	}

	/**
	 * Allows <code>paramNames</code> attribute to be set as comma-separated-values (csv).
	 * 
	 * @param paramNames the paramNames to set
	 */
	public void setParamNames(String paramNames) {
		this.paramNames = TextParseUtil.commaDelimitedStringToSet(paramNames);
	}


	/**
	 * Allows <code>paramValues</code> attribute to be set as a comma-separated-values (csv).
	 * 
	 * @param paramValues the paramValues to set
	 */
	public void setParamValues(String paramValues) {
		this.paramValues = TextParseUtil.commaDelimitedStringToSet(paramValues);
	}
}

