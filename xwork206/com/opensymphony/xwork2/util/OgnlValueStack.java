/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import ognl.ObjectPropertyAccessor;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork2.DefaultTextProvider;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.inject.Inject;

/**
 * Ognl implementation of a value stack that allows for dynamic Ognl expressions to be evaluated against it. When
 * evaluating an expression, the stack will be searched down the stack, from the latest objects pushed in to the
 * earliest, looking for a bean with a getter or setter for the given property or a method of the given name (depending
 * on the expression being evaluated).
 *
 * @author Patrick Lightbody
 * @author tm_jee
 *
 * @version $Date: 2008-08-21 23:37:11 +0200 (Thu, 21 Aug 2008) $ $Id: OgnlValueStack.java 1857 2008-08-21 21:37:11Z musachy $
 */
public class OgnlValueStack implements Serializable, ValueStack, ClearableValueStack, MemberAccessValueStack {

	private static final long serialVersionUID = 370737852934925530L;

	private static CompoundRootAccessor accessor;
    private static Log LOG = LogFactory.getLog(OgnlValueStack.class);
    private static boolean devMode;
    private static boolean allowStaticMethodAccess = true;

    static {
        reset();
    }

    public static void reset() {
        accessor = new CompoundRootAccessor();
        OgnlRuntime.setPropertyAccessor(CompoundRoot.class, accessor);
        OgnlRuntime.setPropertyAccessor(Object.class, new ObjectAccessor());
        OgnlRuntime.setPropertyAccessor(Iterator.class, new XWorkIteratorPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Enumeration.class, new XWorkEnumerationAcccessor());
        OgnlRuntime.setPropertyAccessor(List.class, new XWorkListPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Map.class, new XWorkMapPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Collection.class, new XWorkCollectionPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(Set.class, new XWorkCollectionPropertyAccessor());
        OgnlRuntime.setPropertyAccessor(ObjectProxy.class, new ObjectProxyPropertyAccessor());
        OgnlRuntime.setMethodAccessor(Object.class, new XWorkMethodAccessor());
        OgnlRuntime.setMethodAccessor(CompoundRoot.class, accessor);
        OgnlRuntime.setNullHandler(Object.class, new InstantiatingNullHandler());
    }

    public static class ObjectAccessor extends ObjectPropertyAccessor {
        public Object getProperty(Map map, Object o, Object o1) throws OgnlException {
            Object obj = super.getProperty(map, o, o1);
            link(map, o.getClass(), (String) o1);

            map.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, o.getClass());
            map.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, o1.toString());
            OgnlContextState.updateCurrentPropertyPath(map, o1);
            return obj;
        }

        public void setProperty(Map map, Object o, Object o1, Object o2) throws OgnlException {
            super.setProperty(map, o, o1, o2);
        }
    }

    public static void link(Map context, Class clazz, String name) {
        context.put("__link", new Object[]{clazz, name});
    }


    CompoundRoot root;
    transient Map context;
    Class defaultType;
    Map overrides;

    transient SecurityMemberAccess securityMemberAccess;

    public OgnlValueStack() {
        setRoot(new CompoundRoot());
        push(DefaultTextProvider.INSTANCE);
    }

    public OgnlValueStack(ValueStack vs) {
        setRoot(new CompoundRoot(vs.getRoot()));
    }


    public static CompoundRootAccessor getAccessor() {
        return accessor;
    }

    @Inject(value = "devMode", required = false)
    public static void setDevMode(String mode) {
        devMode = "true".equals(mode);
    }

    @Inject(value="allowStaticMethodAccess", required=false)
    public static void setAllowStaticMethodAccess(boolean allowStaticMethodAccess) {
        OgnlValueStack.allowStaticMethodAccess = allowStaticMethodAccess;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#getContext()
     */
    public Map getContext() {
        return context;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#setDefaultType(java.lang.Class)
     */
    public void setDefaultType(Class defaultType) {
        this.defaultType = defaultType;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#setExprOverrides(java.util.Map)
     */
    public void setExprOverrides(Map overrides) {
    	if (this.overrides == null) {
    		this.overrides = overrides;
    	}
    	else {
    		this.overrides.putAll(overrides);
    	}
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#getExprOverrides()
     */
    public Map getExprOverrides() {
    	return this.overrides;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#getRoot()
     */
    public CompoundRoot getRoot() {
        return root;
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#setValue(java.lang.String, java.lang.Object)
     */
    public void setValue(String expr, Object value) {
        setValue(expr, value, devMode);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#setValue(java.lang.String, java.lang.Object, boolean)
     */
    public void setValue(String expr, Object value, boolean throwExceptionOnFailure) {
        Map context = getContext();

        try {
            context.put(XWorkConverter.CONVERSION_PROPERTY_FULLNAME, expr);
            context.put(REPORT_ERRORS_ON_NO_PROP, (throwExceptionOnFailure) ? Boolean.TRUE : Boolean.FALSE);
            OgnlUtil.setValue(expr, context, root, value);
        } catch (OgnlException e) {
            if (throwExceptionOnFailure) {
                String msg = "Error setting expression '" + expr + "' with value '" + value + "'";
                throw new XWorkException(msg, e);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error setting value", e);
                }
            }
        } catch (RuntimeException re) { //XW-281
            if (throwExceptionOnFailure) {
                String msg = "Error setting expression '" + expr + "' with value '" + value + "'";
                throw new XWorkException(msg, re);
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Error setting value", re);
                }
            }
        } finally {
            OgnlContextState.clear(context);
            context.remove(XWorkConverter.CONVERSION_PROPERTY_FULLNAME);
            context.remove(REPORT_ERRORS_ON_NO_PROP);
        }
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#findString(java.lang.String)
     */
    public String findString(String expr) {
        return (String) findValue(expr, String.class);
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#findValue(java.lang.String)
     */
    public Object findValue(String expr) {
        try {
            if (expr == null) {
                return null;
            }

            if ((overrides != null) && overrides.containsKey(expr)) {
                expr = (String) overrides.get(expr);
            }

            if (defaultType != null) {
                return findValue(expr, defaultType);
            }

            Object value = OgnlUtil.getValue(expr, context, root);
            if (value != null) {
                return value;
            } else {
                return findInContext(expr);
            }
        } catch (OgnlException e) {
            return findInContext(expr);
        } catch (Exception e) {
            logLookupFailure(expr, e);

            return findInContext(expr);
        } finally {
            OgnlContextState.clear(context);
        }
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#findValue(java.lang.String, java.lang.Class)
     */
    public Object findValue(String expr, Class asType) {
        try {
            if (expr == null) {
                return null;
            }

            if ((overrides != null) && overrides.containsKey(expr)) {
                expr = (String) overrides.get(expr);
            }

            Object value = OgnlUtil.getValue(expr, context, root, asType);
            if (value != null) {
                return value;
            } else {
                return findInContext(expr);
            }
        } catch (OgnlException e) {
            return findInContext(expr);
        } catch (Exception e) {
            logLookupFailure(expr, e);

            return findInContext(expr);
        } finally {
            OgnlContextState.clear(context);
        }
    }

    private Object findInContext(String name) {
        return getContext().get(name);
    }

    /**
     * Log a failed lookup, being more verbose when devMode=true.
     *
     * @param expr The failed expression
     * @param e    The thrown exception.
     */
    private void logLookupFailure(String expr, Exception e) {
        StringBuffer msg = new StringBuffer();
        msg.append("Caught an exception while evaluating expression '").append(expr).append("' against value stack");
        if (devMode && LOG.isWarnEnabled()) {
            LOG.warn(msg, e);
            LOG.warn("NOTE: Previous warning message was issued due to devMode set to true.");
        } else if (LOG.isDebugEnabled()) {
            LOG.debug(msg, e);
        }
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#peek()
     */
    public Object peek() {
        return root.peek();
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#pop()
     */
    public Object pop() {
        return root.pop();
    }

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#push(java.lang.Object)
     */
    public void push(Object o) {
        root.push(o);
    }
    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#set(java.lang.String, java.lang.Object)
     */
    public void set(String key, Object o) {
    	//set basically is backed by a Map
    	//pushed on the stack with a key
    	//being put on the map and the
    	//Object being the value

    	Map setMap=null;

    	//check if this is a Map
    	//put on the stack  for setting
    	//if so just use the old map (reduces waste)
    	Object topObj=peek();
    	if (topObj instanceof Map
    			&&((Map)topObj).get(MAP_IDENTIFIER_KEY)!=null) {

    		setMap=(Map)topObj;
    	}	else {
    		setMap=new HashMap();
    		//the map identifier key ensures
    		//that this map was put there
    		//for set purposes and not by a user
    		//whose data we don't want to touch
    		setMap.put(MAP_IDENTIFIER_KEY,"");
    		push(setMap);
    	}
    	setMap.put(key,o);

    }


    private static final String MAP_IDENTIFIER_KEY="com.opensymphony.xwork2.util.OgnlValueStack.MAP_IDENTIFIER_KEY";

    /* (non-Javadoc)
     * @see com.opensymphony.xwork2.util.ValueStack#size()
     */
    public int size() {
        return root.size();
    }

    private void setRoot(CompoundRoot compoundRoot) {
        this.root = compoundRoot;
        this.securityMemberAccess =  new SecurityMemberAccess(allowStaticMethodAccess);
        this.context = Ognl.createDefaultContext(this.root, accessor, XWorkConverter.getInstance(),
                this.securityMemberAccess);
        context.put(VALUE_STACK, this);
        Ognl.setClassResolver(context, accessor);
        ((OgnlContext) context).setTraceEvaluations(false);
        ((OgnlContext) context).setKeepLastEvaluation(false);
    }

    private Object readResolve() {
        OgnlValueStack aStack = new OgnlValueStack();
        aStack.setRoot(this.root);

        return aStack;
    }

    public void clearContextValues() {
        //this is an OGNL ValueStack so the context will be an OgnlContext
        //it would be better to make context of type OgnlContext
       ((OgnlContext)context).getValues().clear();
    }

    public void setAcceptProperties(Set<Pattern> acceptedProperties) {
        securityMemberAccess.setAcceptProperties(acceptedProperties);
    }

    public void setExcludeProperties(Set<Pattern> excludeProperties) {
       securityMemberAccess.setExcludeProperties(excludeProperties);
    }
}
