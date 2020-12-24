/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.io.InputStream;
import java.io.IOException;

import com.opensymphony.xwork2.util.FileManager;
import com.opensymphony.xwork2.ObjectFactory;

/**
 * <code>AnnotationActionValidatorManager</code>
 * <p/>
 * This is the entry point into XWork's annotations-based validator framework.
 * Validation rules are specified as annotations within the source files.
 *
 * @author Rainer Hermanns
 * @author jepjep
 * @version $Id: AnnotationActionValidatorManager.java 1506 2007-05-18 13:12:03Z rainerh $
 */
public class AnnotationActionValidatorManager implements ActionValidatorManager {


    /**
     * The file suffix for any validation file.
     */
    protected static final String VALIDATION_CONFIG_SUFFIX = "-validation.xml";

    private static final Map<String, List<ValidatorConfig>> validatorCache = Collections.synchronizedMap(new HashMap<String, List<ValidatorConfig>>());
    private static final Map<String, List<ValidatorConfig>> validatorFileCache = Collections.synchronizedMap(new HashMap<String, List<ValidatorConfig>>());
    private static final Log LOG = LogFactory.getLog(AnnotationActionValidatorManager.class);

    /**
     * Returns a list of validators for the given class and context. This is the primary
     * lookup method for validators.
     *
     * @param clazz   the class to lookup.
     * @param context the context of the action class - can be <tt>null</tt>.
     * @return a list of all validators for the given class and context.
     */
    public synchronized List<Validator> getValidators(Class clazz, String context) {
        return getValidators(clazz, context, null);
    }

    /**
     * Returns a list of validators for the given class, context, and method. This is the primary
     * lookup method for validators.
     *
     * @param clazz   the class to lookup.
     * @param context the context of the action class - can be <tt>null</tt>.
     * @param method  the name of the method being invoked on the action - can be <tt>null</tt>.
     * @return a list of all validators for the given class and context.
     */
    public synchronized List<Validator> getValidators(Class clazz, String context, String method) {
        final String validatorKey = buildValidatorKey(clazz, context);

        if (validatorCache.containsKey(validatorKey)) {
            if (FileManager.isReloadingConfigs()) {
                validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, true, null));
            }
        } else {
            validatorCache.put(validatorKey, buildValidatorConfigs(clazz, context, false, null));
        }

        // get the set of validator configs
        List<ValidatorConfig> cfgs = validatorCache.get(validatorKey);

        // create clean instances of the validators for the caller's use
        ArrayList<Validator> validators = new ArrayList<Validator>(cfgs.size());
        for (ValidatorConfig cfg : cfgs) {
            if (method == null || method.equals(cfg.getParams().get("methodName"))) {
                // Remove methodName temporary
                Object methodName = cfg.getParams().remove("methodName");
                Validator validator = ValidatorFactory.getValidator(cfg, ObjectFactory.getObjectFactory());
                // Readd methodName temporary
                cfg.getParams().put("methodName", methodName);
                validator.setValidatorType(cfg.getType());
                validators.add(validator);
            }
        }

        return validators;
    }

    /**
     * Validates the given object using action and its context.
     *
     * @param object  the action to validate.
     * @param context the action's context.
     * @throws ValidationException if an error happens when validating the action.
     */
    public void validate(Object object, String context) throws ValidationException {
        validate(object, context, (String) null);
    }

    /**
     * Validates the given object using action, its context, and the name of the method being invoked on the action.
     *
     * @param object  the action to validate.
     * @param context the action's context.
     * @param method  the name of the method being invoked on the action.
     * @throws ValidationException if an error happens when validating the action.
     */
    public void validate(Object object, String context, String method) throws ValidationException {
        ValidatorContext validatorContext = new DelegatingValidatorContext(object);
        validate(object, context, validatorContext, method);
    }

    /**
     * Validates an action give its context and a validation context.
     *
     * @param object           the action to validate.
     * @param context          the action's context.
     * @param validatorContext
     * @throws ValidationException if an error happens when validating the action.
     */
    public void validate(Object object, String context, ValidatorContext validatorContext) throws ValidationException {
        validate(object, context, validatorContext, null);
    }

    /**
     * Validates an action give its context, a validation context, and the name of the method being invoked on the action.
     *
     * @param object           the action to validate.
     * @param context          the action's context.
     * @param validatorContext
     * @param method           the name of the method being invoked on the action.
     * @throws ValidationException if an error happens when validating the action.
     */
    public void validate(Object object, String context, ValidatorContext validatorContext, String method) throws ValidationException {
        List<Validator> validators = getValidators(object.getClass(), context, method);
        Set<String> shortcircuitedFields = null;

        for (final Validator validator: validators) {
            try {
                validator.setValidatorContext(validatorContext);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Running validator: " + validator + " for object " + object + " and method " + method);
                }

                FieldValidator fValidator = null;
                String fullFieldName = null;

                if (validator instanceof FieldValidator) {
                    fValidator = (FieldValidator) validator;
                    fullFieldName = fValidator.getValidatorContext().getFullFieldName(fValidator.getFieldName());

                    if ((shortcircuitedFields != null) && shortcircuitedFields.contains(fullFieldName)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Short-circuited, skipping");
                        }

                        continue;
                    }
                }

                if (validator instanceof ShortCircuitableValidator && ((ShortCircuitableValidator) validator).isShortCircuit())
                {
                    // get number of existing errors
                    List<String> errs = null;

                    if (fValidator != null) {
                        if (validatorContext.hasFieldErrors()) {
                            Collection<String> fieldErrors = (Collection<String>) validatorContext.getFieldErrors().get(fullFieldName);

                            if (fieldErrors != null) {
                                errs = new ArrayList<String>(fieldErrors);
                            }
                        }
                    } else if (validatorContext.hasActionErrors()) {
                        Collection<String> actionErrors = validatorContext.getActionErrors();

                        if (actionErrors != null) {
                            errs = new ArrayList<String>(actionErrors);
                        }
                    }

                    validator.validate(object);

                    if (fValidator != null) {
                        if (validatorContext.hasFieldErrors()) {
                            Collection<String> errCol = (Collection<String>) validatorContext.getFieldErrors().get(fullFieldName);

                            if ((errCol != null) && !errCol.equals(errs)) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Short-circuiting on field validation");
                                }

                                if (shortcircuitedFields == null) {
                                    shortcircuitedFields = new TreeSet<String>();
                                }

                                shortcircuitedFields.add(fullFieldName);
                            }
                        }
                    } else if (validatorContext.hasActionErrors()) {
                        Collection<String> errCol = validatorContext.getActionErrors();

                        if ((errCol != null) && !errCol.equals(errs)) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Short-circuiting");
                            }

                            break;
                        }
                    }

                    continue;
                }

                validator.validate(object);
            } finally {
                validator.setValidatorContext( null );
            }

        }
    }

    /**
     * Builds a key for validators - used when caching validators.
     *
     * @param clazz   the action.
     * @param context the action's context.
     * @return a validator key which is the class name plus context.
     */
    protected static String buildValidatorKey(Class clazz, String context) {
        StringBuffer sb = new StringBuffer(clazz.getName());
        sb.append("/");
        sb.append(context);
        return sb.toString();
    }

    private  List<ValidatorConfig> buildAliasValidatorConfigs(Class aClass, String context, boolean checkFile) {
        String fileName = aClass.getName().replace('.', '/') + "-" + context + VALIDATION_CONFIG_SUFFIX;

        return loadFile(fileName, aClass, checkFile);
    }


    protected List<ValidatorConfig> buildClassValidatorConfigs(Class aClass, boolean checkFile) {

        String fileName = aClass.getName().replace('.', '/') + VALIDATION_CONFIG_SUFFIX;

        List<ValidatorConfig> result = new ArrayList<ValidatorConfig>(loadFile(fileName, aClass, checkFile));

        List<ValidatorConfig> annotationResult = new ArrayList<ValidatorConfig>(AnnotationValidationConfigurationBuilder.buildAnnotationClassValidatorConfigs(aClass));

        result.addAll(annotationResult);

        return result;

    }

    /**
     * <p>This method 'collects' all the validator configurations for a given
     * action invocation.</p>
     * <p/>
     * <p>It will traverse up the class hierarchy looking for validators for every super class
     * and directly implemented interface of the current action, as well as adding validators for
     * any alias of this invocation. Nifty!</p>
     * <p/>
     * <p>Given the following class structure:
     * <pre>
     *   interface Thing;
     *   interface Animal extends Thing;
     *   interface Quadraped extends Animal;
     *   class AnimalImpl implements Animal;
     *   class QuadrapedImpl extends AnimalImpl implements Quadraped;
     *   class Dog extends QuadrapedImpl;
     * </pre></p>
     * <p/>
     * <p>This method will look for the following config files for Dog:
     * <pre>
     *   Animal
     *   Animal-context
     *   AnimalImpl
     *   AnimalImpl-context
     *   Quadraped
     *   Quadraped-context
     *   QuadrapedImpl
     *   QuadrapedImpl-context
     *   Dog
     *   Dog-context
     * </pre></p>
     * <p/>
     * <p>Note that the validation rules for Thing is never looked for because no class in the
     * hierarchy directly implements Thing.</p>
     *
     * @param clazz     the Class to look up validators for.
     * @param context   the context to use when looking up validators.
     * @param checkFile true if the validation config file should be checked to see if it has been
     *                  updated.
     * @param checked   the set of previously checked class-contexts, null if none have been checked
     * @return a list of validator configs for the given class and context.
     */
    private List<ValidatorConfig> buildValidatorConfigs(Class clazz, String context, boolean checkFile, Set checked) {
        List<ValidatorConfig> validatorConfigs = new ArrayList<ValidatorConfig>();

        if (checked == null) {
            checked = new TreeSet<String>();
        } else if (checked.contains(clazz.getName())) {
            return validatorConfigs;
        }

        if (clazz.isInterface()) {
            Class[] interfaces = clazz.getInterfaces();

            for (Class anInterface : interfaces) {
                validatorConfigs.addAll(buildValidatorConfigs(anInterface, context, checkFile, checked));
            }
        } else {
            if (!clazz.equals(Object.class)) {
                validatorConfigs.addAll(buildValidatorConfigs(clazz.getSuperclass(), context, checkFile, checked));
            }
        }

        // look for validators for implemented interfaces
        for (Class anInterface1 : clazz.getInterfaces()) {
            if (checked.contains(anInterface1.getName())) {
                continue;
            }

            validatorConfigs.addAll(buildClassValidatorConfigs(anInterface1, checkFile));

            if (context != null) {
                validatorConfigs.addAll(buildAliasValidatorConfigs(anInterface1, context, checkFile));
            }

            checked.add(anInterface1.getName());
        }

        validatorConfigs.addAll(buildClassValidatorConfigs(clazz, checkFile));

        if (context != null) {
            validatorConfigs.addAll(buildAliasValidatorConfigs(clazz, context, checkFile));
        }

        checked.add(clazz.getName());

        return validatorConfigs;
    }

    private List<ValidatorConfig> loadFile(String fileName, Class clazz, boolean checkFile) {
        List<ValidatorConfig> retList = Collections.emptyList();

        if ((checkFile && FileManager.fileNeedsReloading(fileName)) || !validatorFileCache.containsKey(fileName)) {
            InputStream is = null;

            try {
                is = FileManager.loadFile(fileName, clazz);

                if (is != null) {
                    retList = new ArrayList<ValidatorConfig>(ValidatorFileParser.parseActionValidatorConfigs(is, fileName));
                }
            } catch (Exception e) {
                LOG.error("Caught exception while loading file " + fileName, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        LOG.error("Unable to close input stream for " + fileName, e);
                    }
                }
            }

            validatorFileCache.put(fileName, retList);
        } else {
            retList = validatorFileCache.get(fileName);
        }

        return retList;
    }


}
