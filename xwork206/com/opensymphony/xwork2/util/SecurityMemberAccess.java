/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import ognl.DefaultMemberAccess;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Allows access decisions to be made on the basis of whether a member is static or not.
 * Also blocks or allows access to properties.
 */
public class SecurityMemberAccess extends DefaultMemberAccess {

    private boolean allowStaticMethodAccess;
    Set<Pattern> excludeProperties = Collections.emptySet();
    Set<Pattern> acceptProperties = Collections.emptySet();

    public SecurityMemberAccess(boolean method) {
        super(false);
        allowStaticMethodAccess = method;
    }

    public boolean getAllowStaticMethodAccess() {
        return allowStaticMethodAccess;
    }

    public void setAllowStaticMethodAccess(boolean allowStaticMethodAccess) {
        this.allowStaticMethodAccess = allowStaticMethodAccess;
    }

    @Override
    public boolean isAccessible(Map context, Object target, Member member,
                                String propertyName) {

        boolean allow = true;
        int modifiers = member.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            if (member instanceof Method && !getAllowStaticMethodAccess()) {
                allow = false;
                if (target instanceof Class) {
                    Class clazz = (Class) target;
                    Method method = (Method) member;
                    if (Enum.class.isAssignableFrom(clazz) && method.getName().equals("values"))
                        allow = true;
                }
            }
        }

        //failed static test
        if (!allow)
            return false;

        // Now check for standard scope rules
        if (!super.isAccessible(context, target, member, propertyName))
            return false;

        return isAcceptableProperty(propertyName);
    }

    protected boolean isAcceptableProperty(String name) {
        if (isAccepted(name) && !isExcluded(name)) {
            return true;
        }
        return false;
    }

    protected boolean isAccepted(String paramName) {
        if (!this.acceptProperties.isEmpty()) {
            for (Pattern pattern : acceptProperties) {
                Matcher matcher = pattern.matcher(paramName);
                if (matcher.matches()) {
                    return true;
                }
            }

            //no match, but acceptedParams is not empty
            return false;
        }

        //empty acceptedParams
        return true;
    }

    protected boolean isExcluded(String paramName) {
        if (!this.excludeProperties.isEmpty()) {
            for (Pattern pattern : excludeProperties) {
                Matcher matcher = pattern.matcher(paramName);
                if (matcher.matches()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setExcludeProperties(Set<Pattern> excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    public void setAcceptProperties(Set<Pattern> acceptedProperties) {
        this.acceptProperties = acceptedProperties;
    }
}
