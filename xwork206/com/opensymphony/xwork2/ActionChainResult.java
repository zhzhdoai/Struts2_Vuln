/*
* Copyright (c) 2002-2006 by OpenSymphony
* All rights reserved.
*/
package com.opensymphony.xwork2;

import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;


/**
* <!-- START SNIPPET: description -->
*
* This result invokes an entire other action, complete with it's own interceptor stack and result.
*
* <!-- END SNIPPET: description -->
*
* <b>This result type takes the following parameters:</b>
*
* <!-- START SNIPPET: params -->
*
* <ul>
*
* <li><b>actionName (default)</b> - the name of the action that will be chained to</li>
*
* <li><b>namespace</b> - used to determine which namespace the Action is in that we're chaining. If namespace is null,
* this defaults to the current namespace</li>
*
* <li><b>method</b> - used to specify another method on target action to be invoked.
* If null, this defaults to execute method</li>
*
* <li><b>skipActions</b> - (optional) the list of comma separated action names for the
* actions that could be chained to</li>
*
* </ul>
*
* <!-- END SNIPPET: params -->
*
* <b>Example:</b>
*
* <pre><!-- START SNIPPET: example -->
* &lt;package name="public" extends="webwork-default"&gt;
*     &lt;!-- Chain creatAccount to login, using the default parameter --&gt;
*     &lt;action name="createAccount" class="..."&gt;
*         &lt;result type="chain"&gt;login&lt;/result&gt;
*     &lt;/action&gt;
*
*     &lt;action name="login" class="..."&gt;
*         &lt;!-- Chain to another namespace --&gt;
*         &lt;result type="chain"&gt;
*             &lt;param name="actionName"&gt;dashboard&lt;/param&gt;
*             &lt;param name="namespace"&gt;/secure&lt;/param&gt;
*         &lt;/result&gt;
*     &lt;/action&gt;
* &lt;/package&gt;
*
* &lt;package name="secure" extends="webwork-default" namespace="/secure"&gt;
*     &lt;action name="dashboard" class="..."&gt;
*         &lt;result&gt;dashboard.jsp&lt;/result&gt;
*     &lt;/action&gt;
* &lt;/package&gt;
* <!-- END SNIPPET: example --></pre>
*
* @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
*/
public class ActionChainResult implements Result {

    private static final Log log = LogFactory.getLog(ActionChainResult.class);

    /**
     * The result parameter name to set the name of the action to chain to.
     */
    public static final String DEFAULT_PARAM = "actionName";

    /**
     * The action context key to save the chain history.
     */
    private static final String CHAIN_HISTORY = "CHAIN_HISTORY";

    /**
     * The result parameter name to set the name of the action to chain to.
     */
    public static final String SKIP_ACTIONS_PARAM = "skipActions";


    private ActionProxy proxy;
    private String actionName;
    
    private String namespace;

    private String methodName;

    /**
     * The list of actions to skip.
     */
    private String skipActions;

    private ActionProxyFactory actionProxyFactory;

    public ActionChainResult() {
        super();
    }

    public ActionChainResult(String namespace, String actionName, String methodName) {
        this.namespace = namespace;
        this.actionName = actionName;
        this.methodName = methodName;
    }

    public ActionChainResult(String namespace, String actionName, String methodName, String skipActions) {
        this.namespace = namespace;
        this.actionName = actionName;
        this.methodName = methodName;
        this.skipActions = skipActions;
    }


    /**
     * @param actionProxyFactory the actionProxyFactory to set
     */
    @Inject
    public void setActionProxyFactory(ActionProxyFactory actionProxyFactory) {
        this.actionProxyFactory = actionProxyFactory;
    }

    /**
     * Set the action name.
     *
     * @param actionName The action name.
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * sets the namespace of the Action that we're chaining to.  if namespace
     * is null, this defaults to the current namespace.
     *
     * @param namespace the name of the namespace we're chaining to
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Set the list of actions to skip.
     * To test if an action should not throe an infinite recursion,
     * only the action name is used, not the namespace.
     *
     * @param actions The list of action name separated by a white space.
     */
    public void setSkipActions(String actions) {
        this.skipActions = actions;
    }


    public void setMethod(String method) {
        this.methodName = method;
    }

    public ActionProxy getProxy() {
        return proxy;
    }

    /**
     * Get the XWork chain history.
     * The stack is a list of <code>namespace/action!method</code> keys.
     */
    public static LinkedList getChainHistory() {
        LinkedList chainHistory = (LinkedList) ActionContext.getContext().get(CHAIN_HISTORY);
        //  Add if not exists
        if (chainHistory == null) {
            chainHistory = new LinkedList();
            ActionContext.getContext().put(CHAIN_HISTORY, chainHistory);
        }

        return chainHistory;
    }

    /**
     * @param invocation the DefaultActionInvocation calling the action call stack
     */
    public void execute(ActionInvocation invocation) throws Exception {
        // if the finalNamespace wasn't explicitly defined, assume the current one
        if (this.namespace == null) {
            this.namespace = invocation.getProxy().getNamespace();
        }

        ValueStack stack = ActionContext.getContext().getValueStack();
        String finalNamespace = TextParseUtil.translateVariables(namespace, stack);
        String finalActionName = TextParseUtil.translateVariables(actionName, stack);
        String finalMethodName = this.methodName != null
                ? TextParseUtil.translateVariables(this.methodName, stack)
                : null;

        if (isInChainHistory(finalNamespace, finalActionName, finalMethodName)) {
            addToHistory(finalNamespace, finalActionName, finalMethodName);
            throw new XWorkException("Infinite recursion detected: "
                    + ActionChainResult.getChainHistory().toString());
        }

        if (ActionChainResult.getChainHistory().isEmpty() && invocation != null && invocation.getProxy() != null) {
            addToHistory(finalNamespace, invocation.getProxy().getActionName(), invocation.getProxy().getMethod());
        }
        addToHistory(finalNamespace, finalActionName, finalMethodName);

        HashMap extraContext = new HashMap();
        extraContext.put(ActionContext.VALUE_STACK, ActionContext.getContext().getValueStack());
        extraContext.put(ActionContext.PARAMETERS, ActionContext.getContext().getParameters());
        extraContext.put(CHAIN_HISTORY, ActionChainResult.getChainHistory());

        if (log.isDebugEnabled()) {
            log.debug("Chaining to action " + finalActionName);
        }

        proxy = actionProxyFactory.createActionProxy(finalNamespace, finalActionName, extraContext);
        if (null != finalMethodName) {
            proxy.setMethod(finalMethodName);
        }
        proxy.execute();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ActionChainResult that = (ActionChainResult) o;

        if (actionName != null ? !actionName.equals(that.actionName) : that.actionName != null) return false;
        if (methodName != null ? !methodName.equals(that.methodName) : that.methodName != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (actionName != null ? actionName.hashCode() : 0);
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0);
        return result;
    }

    private boolean isInChainHistory(String namespace, String actionName, String methodName) {
        LinkedList chainHistory = ActionChainResult.getChainHistory();

        if (chainHistory == null) {
            return false;
        } else {
            //  Actions to skip
            Set skipActionsList = new HashSet();
            if (skipActions != null && skipActions.length() > 0) {
                ValueStack stack = ActionContext.getContext().getValueStack();
                String finalSkipActions = TextParseUtil.translateVariables(this.skipActions, stack);
                skipActionsList.addAll(TextParseUtil.commaDelimitedStringToSet(finalSkipActions));
            }
            if (!skipActionsList.contains(actionName)) {
                //  Get if key is in the chain history
                return chainHistory.contains(makeKey(namespace, actionName, methodName));
            }

            return false;
        }
    }

    private void addToHistory(String namespace, String actionName, String methodName) {
        LinkedList chainHistory = ActionChainResult.getChainHistory();
        chainHistory.add(makeKey(namespace, actionName, methodName));
    }

    private String makeKey(String namespace, String actionName, String methodName) {
        if (null == methodName) {
            return namespace + "/" + actionName;
        }

        return namespace + "/" + actionName + "!" + methodName;
    }
}
