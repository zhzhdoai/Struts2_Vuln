/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.util.ValueStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Map;


/**
 * <!-- START SNIPPET: description -->
 *
 * The aim of this Interceptor is to alias a named parameter to a different named parameter. By acting as the glue
 * between actions sharing similiar parameters (but with different names), it can help greatly with action chaining.
 *
 * <p/>  Action's alias expressions should be in the form of  #{ "name1" : "alias1", "name2" : "alias2" }. This means
 * that assuming an action (or something else in the stack) has a value for the expression named <i>name1</i> and the
 * action this interceptor is applied to has a setter named <i>alias1</i>, <i>alias1</i> will be set with the value from
 * <i>name1</i>.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>aliasesKey (optional) - the name of the action parameter to look for the alias map (by default this is
 * <i>aliases</i>).</li>
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
 * This interceptor does not have any known extension points.
 *
 * <!-- END SNIPPET: extending -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;!-- The value for the foo parameter will be applied as if it were named bar --&gt;
 *     &lt;param name="aliases"&gt;#{ 'foo' : 'bar' }&lt;/param&gt;
 *
 *     &lt;interceptor-ref name="alias"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Matthew Payne
 */
public class AliasInterceptor extends AbstractInterceptor {
    private static final Log log = LogFactory.getLog(AliasInterceptor.class);
    private static final String DEFAULT_ALIAS_KEY = "aliases";

    protected String aliasesKey = DEFAULT_ALIAS_KEY;

    public void setAliasesKey(String aliasesKey) {
        this.aliasesKey = aliasesKey;
    }

    public String intercept(ActionInvocation invocation) throws Exception {

        ActionConfig config = invocation.getProxy().getConfig();
        ActionContext ac = invocation.getInvocationContext();

        // get the action's parameters
        final Map parameters = config.getParams();

        if (parameters.containsKey(aliasesKey)) {

            String aliasExpression = (String) parameters.get(aliasesKey);
            ValueStack stack = ac.getValueStack();
            Object obj = stack.findValue(aliasExpression);

            if (obj != null && obj instanceof Map) {
                // override
                Map aliases = (Map) obj;
                Iterator itr = aliases.entrySet().iterator();
                while (itr.hasNext()) {
                    Map.Entry entry = (Map.Entry) itr.next();
                    String name = entry.getKey().toString();
                    String alias = (String) entry.getValue();
                    Object value = stack.findValue(name);
                    if (null == value) {
                        // workaround
                        Map contextParameters = ac.getParameters();
                        
                        if (null != contextParameters) {
                            value = contextParameters.get(name);
                        }
                    }
                    if (null != value) {
                        stack.setValue(alias, value);
                    }
                }
            } else {
                log.debug("invalid alias expression:" + aliasesKey);
            }
        }
        
        return invocation.invoke();
    }
}
