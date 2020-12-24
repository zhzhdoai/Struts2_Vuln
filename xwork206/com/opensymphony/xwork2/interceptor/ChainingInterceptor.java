/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Unchainable;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.OgnlUtil;
import com.opensymphony.xwork2.util.ValueStack;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * <!-- START SNIPPET: description -->
 *
 * An interceptor that copies all the properties of every object in the value stack to the currently executing object,
 * except for any object that implements {@link Unchainable}. A collection of optional <i>includes</i> and
 * <i>excludes</i> may be provided to control how and which parameters are copied. Only includes or excludes may be
 * specified. Specifying both results in undefined behavior. See the javadocs for {@link OgnlUtil#copy(Object, Object,
 * java.util.Map, java.util.Collection, java.util.Collection)} for more information.
 *
 * <p/> It is important to remember that this interceptor does nothing if there are no objects already on the stack.
 * This means two things: One, you can safely apply it to all your actions without any worry of adverse affects. Two, it
 * is up to you to ensure an object exists in the stack prior to invoking this action. The most typical way this is done
 * is through the use of the <b>chain</b> result type, which combines with this interceptor to make up the action
 * chaining feature.
 *
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Interceptor parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>excludes (optional) - the list of parameter names to exclude from copying (all others will be included).</li>
 *
 * <li>includes (optional) - the list of parameter names to include when copying (all others will be excluded).</li>
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
 * 
 * &lt;action name="someAction" class="com.examples.SomeAction"&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success" type="chain"&gt;otherAction&lt;/result&gt;
 * &lt;/action&gt;
 *
 * &lt;action name="otherAction" class="com.examples.OtherAction"&gt;
 *     &lt;interceptor-ref name="chain"/&gt;
 *     &lt;interceptor-ref name="basicStack"/&gt;
 *     &lt;result name="success"&gt;good_result.ftl&lt;/result&gt;
 * &lt;/action&gt;
 * 
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author $Author: mrdon $
 * @author tm_jee ( tm_jee(at)yahoo.co.uk )
 * @version $Revision: 1142 $
 */
public class ChainingInterceptor extends AbstractInterceptor {
	
	private static final Log _log = LogFactory.getLog(ChainingInterceptor.class);
	
    Collection excludes;
    Collection includes;

    public String intercept(ActionInvocation invocation) throws Exception {
        ValueStack stack = invocation.getStack();
        CompoundRoot root = stack.getRoot();

        if (root.size() > 1) {
            List list = new ArrayList(root);
            list.remove(0);
            Collections.reverse(list);

            Map ctxMap = invocation.getInvocationContext().getContextMap();
            Iterator iterator = list.iterator();
            int index = 1; // starts with 1, 0 has been removed
            while (iterator.hasNext()) {
            	index = index + 1;
                Object o = iterator.next();
                if (o != null) {
                	if (!(o instanceof Unchainable)) {
                		OgnlUtil.copy(o, invocation.getAction(), ctxMap, excludes, includes);
                	}
                }
                else {
                	_log.warn("compound root element at index "+index+" is null");
                }
            }
        }
        return invocation.invoke();
    }
    
    public Collection getExcludes() {
        return excludes;
    }

    public void setExcludes(Collection excludes) {
        this.excludes = excludes;
    }

    public Collection getIncludes() {
        return includes;
    }

    public void setIncludes(Collection includes) {
        this.includes = includes;
    }
}
