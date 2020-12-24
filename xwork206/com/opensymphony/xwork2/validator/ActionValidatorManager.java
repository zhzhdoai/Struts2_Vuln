/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.validator;

import java.util.List;

/**
 * <code>ActionValidatorManager</code>
 *
 * @author Rainer Hermanns
 * @version $Id: ActionValidatorManager.java 1359 2007-02-16 12:00:09Z rainerh $
 */
public interface ActionValidatorManager {


    /**
     * Returns a list of validators for the given class, context, and method. This is the primary
     * lookup method for validators.
     *
     * @param clazz the class to lookup.
     * @param context the context of the action class - can be <tt>null</tt>.
     * @param method the name of the method being invoked on the action - can be <tt>null</tt>.
     * @return a list of all validators for the given class and context.
     */
    List<Validator> getValidators(Class clazz, String context, String method);

    /**
     * Returns a list of validators for the given class and context. This is the primary
     * lookup method for validators.
     *
     * @param clazz the class to lookup.
     * @param context the context of the action class - can be <tt>null</tt>.
     * @return a list of all validators for the given class and context.
     */
    List<Validator> getValidators(Class clazz, String context);

    /**
     * Validates the given object using action and its context.
     *
     * @param object the action to validate.
     * @param context the action's context.
     * @throws ValidationException if an error happens when validating the action.
     */
    void validate(Object object, String context) throws ValidationException;

    /**
     * Validates an action give its context and a validation context.
     *
     * @param object the action to validate.
     * @param context the action's context.
     * @param validatorContext
     * @throws ValidationException if an error happens when validating the action.
     */
    void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException;

    /**
     * Validates the given object using an action, its context, and the name of the method being invoked on the action.
     *
     * @param object the action to validate.
     * @param context the action's context.
     * @param method the name of the method being invoked on the action - can be <tt>null</tt>.
     * @throws ValidationException if an error happens when validating the action.
     */
    void validate(Object object, String context, String method) throws ValidationException;

    /**
     * Validates an action give its context and a validation context.
     *
     * @param object the action to validate.
     * @param context the action's context.
     * @param validatorContext
     * @param method the name of the method being invoked on the action - can be <tt>null</tt>.
     * @throws ValidationException if an error happens when validating the action.
     */
    void validate(Object object, String context, ValidatorContext validatorContext, String method) throws ValidationException;
}
