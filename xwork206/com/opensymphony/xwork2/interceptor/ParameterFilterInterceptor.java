/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.TextParseUtil;

import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <!-- START SNIPPET: description -->
 *
 * <p>The Parameter Filter Interceptor blocks parameters from getting
 * to the rest of the stack or your action. You can use multiple 
 * parameter filter interceptors for a given action, so, for example,
 * you could use one in your default stack that filtered parameters
 * you wanted blocked from every action and those you wanted blocked 
 * from an individual action you could add an additional interceptor
 * for each action.</p>
 * 
 * <!-- END SNIPPET: description -->
 * 
 * <!-- START SNIPPET: parameters -->
 *
 * <ul>
 *
 * <li>allowed - a comma delimited list of parameter prefixes
 *  that are allowed to pass to the action</li>
 * <li>blocked - a comma delimited list of parameter prefixes 
 * that are not allowed to pass to the action</li>
 * <li>defaultBlock - boolean (default to false) whether by
 * default a given parameter is blocked. If true, then a parameter
 * must have a prefix in the allowed list in order to be able 
 * to pass to the action
 * </ul>
 * 
 * <p>The way parameters are filtered for the least configuration is that
 * if a string is in the allowed or blocked lists, then any parameter
 * that is a member of the object represented by the parameter is allowed
 * or blocked respectively.</p>
 * 
 * <p>For example, if the parameters are:
 * <ul>
 * <li>blocked: person,person.address.createDate,personDao</li>
 * <li>allowed: person.address</li>
 * <li>defaultBlock: false</li>
 * </ul>
 * <br>
 * The parameters person.name, person.phoneNum etc would be blocked 
 * because 'person' is in the blocked list. However, person.address.street
 * and person.address.city would be allowed because person.address is
 * in the allowed list (the longer string determines permissions).</p> 
 * <!-- END SNIPPET: parameters -->
 *
 * <!-- START SNIPPET: extending -->
 * 
 * none
 * 
 * <!-- END SNIPPET: extending -->
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 * 
 * <interceptors>
 *   ...
 *   <interceptor name="parameterFilter" class="com.opensymphony.xwork2.interceptor.ParameterFilterInterceptor" />
 *   ...
 * </interceptors>
 * 
 * <action ....>
 *   ...
 *   <interceptor-ref name="parameterFilter">
 *     <param name="blocked">person,person.address.createDate,personDao</param>
 *   </interceptor-ref>
 *   ...
 * </action> 
 * 
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * @author Gabe
 * @version $Date: 2006-10-31 05:00:17 +0100 (Di, 31 Okt 2006) $ $Id: ParameterFilterInterceptor.java 1179 2006-10-31 04:00:17Z tm_jee $
 */
public class ParameterFilterInterceptor extends AbstractInterceptor {

    private static final Log LOG = LogFactory.getLog(ParameterFilterInterceptor.class);

    private Collection allowed;

    private Collection blocked;

    private Map includesExcludesMap;

    private boolean defaultBlock = false;

    public String intercept(ActionInvocation invocation) throws Exception {

        Map parameters = invocation.getInvocationContext().getParameters();
        HashSet paramsToRemove = new HashSet();

        Map includesExcludesMap = getIncludesExcludesMap();

        for (Iterator i = parameters.keySet().iterator(); i.hasNext();) {

            String param = (String) i.next();

            boolean currentAllowed = !isDefaultBlock();

            boolean foundApplicableRule = false;
            for (Iterator j = includesExcludesMap.keySet().iterator(); j.hasNext();) {
                String currRule = (String) j.next();

                if (param.startsWith(currRule)
                        && (param.length() == currRule.length()
                        || isPropSeperator(param.charAt(currRule.length())))) {
                    currentAllowed = ((Boolean) includesExcludesMap.get(currRule)).booleanValue();
                } else {
                    if (foundApplicableRule) {
                        foundApplicableRule = false;
                        break;
                    }
                }
            }
            if (!currentAllowed) {
                paramsToRemove.add(param);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Params to remove: " + paramsToRemove);
        }

        for (Iterator i = paramsToRemove.iterator(); i.hasNext();) {
            parameters.remove(i.next());
        }

        return invocation.invoke();
    }

    /**
     * @param c
     * @return <tt>true</tt>, if char is property separator, <tt>false</tt> otherwise.
     */
    private boolean isPropSeperator(char c) {
        return c == '.' || c == '(' || c == '[';
    }

    private Map getIncludesExcludesMap() {
        if (this.includesExcludesMap == null) {
            this.includesExcludesMap = new TreeMap();

            if (getAllowedCollection() != null) {
                for (Iterator i = getAllowedCollection().iterator(); i.hasNext();) {
                    this.includesExcludesMap.put(i.next(), Boolean.TRUE);
                }
            }
            if (getBlockedCollection() != null) {
                for (Iterator i = getBlockedCollection().iterator(); i.hasNext();) {
                    this.includesExcludesMap.put(i.next(), Boolean.FALSE);
                }
            }
        }

        return this.includesExcludesMap;
    }

    /**
     * @return Returns the defaultBlock.
     */
    public boolean isDefaultBlock() {
        return defaultBlock;
    }

    /**
     * @param defaultExclude The defaultExclude to set.
     */
    public void setDefaultBlock(boolean defaultExclude) {
        this.defaultBlock = defaultExclude;
    }

    /**
     * @return Returns the blocked.
     */
    public Collection getBlockedCollection() {
        return blocked;
    }

    /**
     * @param blocked The blocked to set.
     */
    public void setBlockedCollection(Collection blocked) {
        this.blocked = blocked;
    }

    /**
     * @param blocked The blocked paramters as comma separated String.
     */
    public void setBlocked(String blocked) {
        setBlockedCollection(asCollection(blocked));
    }

    /**
     * @return Returns the allowed.
     */
    public Collection getAllowedCollection() {
        return allowed;
    }

    /**
     * @param allowed The allowed to set.
     */
    public void setAllowedCollection(Collection allowed) {
        this.allowed = allowed;
    }

    /**
     * @param allowed The allowed paramters as comma separated String.
     */
    public void setAllowed(String allowed) {
        setAllowedCollection(asCollection(allowed));
    }

    /**
     * Return a collection from the comma delimited String.
     *
     * @param commaDelim
     * @return A collection from the comma delimited String.
     */
    private Collection asCollection(String commaDelim) {
        if (commaDelim == null || commaDelim.trim().length() == 0) {
            return null;
        }
        return TextParseUtil.commaDelimitedStringToSet(commaDelim);
    }
}
