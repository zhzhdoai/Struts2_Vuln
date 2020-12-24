/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.*;
import com.opensymphony.xwork2.util.ValueStack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * A default implementation of the {@link ValidatorContext} interface.
 *
 * @author Jason Carreira
 * @author Rainer Hermanns
 */
public class DelegatingValidatorContext implements ValidatorContext {

    private LocaleProvider localeProvider;
    private TextProvider textProvider;
    private ValidationAware validationAware;

    /**
     * Creates a new validation context given a ValidationAware object, and a text and locale provider. These objects
     * are used internally to set errors and get and set error text.
     */
    public DelegatingValidatorContext(ValidationAware validationAware, TextProvider textProvider,
            LocaleProvider localeProvider)
    {
        this.textProvider = textProvider;
        this.validationAware = validationAware;
        this.localeProvider = localeProvider;
    }

    /**
     * Creates a new validation context given an object - usually an Action. The internal objects
     * (validation aware instance and a locale and text provider) are created based on the given action.
     *
     * @param object the object to use for validation (usually an Action).
     */
    public DelegatingValidatorContext(Object object) {
        this.localeProvider = makeLocaleProvider(object);
        this.validationAware = makeValidationAware(object);
        this.textProvider = makeTextProvider(object, localeProvider);
    }

    /**
     * Create a new validation context given a Class definition. The locale provider, text provider and
     * the validation context are created based on the class.
     *
     * @param clazz the class to initialize the context with.
     */
    public DelegatingValidatorContext(Class clazz) {
        localeProvider = new ActionContextLocaleProvider();
        textProvider = new TextProviderFactory().createInstance(clazz, localeProvider);
        validationAware = new LoggingValidationAware(clazz);
    }

    public void setActionErrors(Collection errorMessages) {
        validationAware.setActionErrors(errorMessages);
    }

    public Collection getActionErrors() {
        return validationAware.getActionErrors();
    }

    public void setActionMessages(Collection messages) {
        validationAware.setActionMessages(messages);
    }

    public Collection getActionMessages() {
        return validationAware.getActionMessages();
    }

    public void setFieldErrors(Map errorMap) {
        validationAware.setFieldErrors(errorMap);
    }

    public Map getFieldErrors() {
        return validationAware.getFieldErrors();
    }

    public String getFullFieldName(String fieldName) {
        return fieldName;
    }

    public Locale getLocale() {
        return localeProvider.getLocale();
    }

    public String getText(String aTextName) {
        return textProvider.getText(aTextName);
    }

    public String getText(String aTextName, String defaultValue) {
        return textProvider.getText(aTextName, defaultValue);
    }

    public String getText(String aTextName, String defaultValue, String obj) {
        return textProvider.getText(aTextName, defaultValue, obj);
    }

    public String getText(String aTextName, List args) {
        return textProvider.getText(aTextName, args);
    }

    public String getText(String key, String[] args) {
        return textProvider.getText(key, args);
    }

    public String getText(String aTextName, String defaultValue, List args) {
        return textProvider.getText(aTextName, defaultValue, args);
    }

    public String getText(String key, String defaultValue, String[] args) {
        return textProvider.getText(key, defaultValue, args);
    }

    public ResourceBundle getTexts(String aBundleName) {
        return textProvider.getTexts(aBundleName);
    }

    public String getText(String key, String defaultValue, List args, ValueStack stack) {
        return textProvider.getText(key,defaultValue,args,stack);
    }

    public String getText(String key, String defaultValue, String[] args, ValueStack stack) {
        return textProvider.getText(key,defaultValue,args,stack);
    }

    public ResourceBundle getTexts() {
        return textProvider.getTexts();
    }

    public void addActionError(String anErrorMessage) {
        validationAware.addActionError(anErrorMessage);
    }

    public void addActionMessage(String aMessage) {
        validationAware.addActionMessage(aMessage);
    }

    public void addFieldError(String fieldName, String errorMessage) {
        validationAware.addFieldError(fieldName, errorMessage);
    }

    public boolean hasActionErrors() {
        return validationAware.hasActionErrors();
    }

    public boolean hasActionMessages() {
        return validationAware.hasActionMessages();
    }

    public boolean hasErrors() {
        return validationAware.hasErrors();
    }

    public boolean hasFieldErrors() {
        return validationAware.hasFieldErrors();
    }

    public static TextProvider makeTextProvider(Object object, LocaleProvider localeProvider) {
        if (object instanceof TextProvider) {
            return (TextProvider) object;
        } else {
            return new TextProviderFactory().createInstance(object.getClass(), localeProvider);
        }
    }

    protected static LocaleProvider makeLocaleProvider(Object object) {
        if (object instanceof LocaleProvider) {
            return (LocaleProvider) object;
        } else {
            return new ActionContextLocaleProvider();
        }
    }

    protected static ValidationAware makeValidationAware(Object object) {
        if (object instanceof ValidationAware) {
            return (ValidationAware) object;
        } else {
            return new LoggingValidationAware(object);
        }
    }

    protected void setTextProvider(TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    protected TextProvider getTextProvider() {
        return textProvider;
    }

    protected void setValidationAware(ValidationAware validationAware) {
        this.validationAware = validationAware;
    }

    protected ValidationAware getValidationAware() {
        return validationAware;
    }

    /**
     * An implementation of LocaleProvider which gets the locale from the action context.
     */
    private static class ActionContextLocaleProvider implements LocaleProvider {
        public Locale getLocale() {
            return ActionContext.getContext().getLocale();
        }
    }

    /**
     * An implementation of ValidationAware which logs errors and messages. 
     */
    private static class LoggingValidationAware implements ValidationAware {

        private Log log;

        public LoggingValidationAware(Class clazz) {
            log = LogFactory.getLog(clazz);
        }

        public LoggingValidationAware(Object obj) {
            log = LogFactory.getLog(obj.getClass());
        }

        public void setActionErrors(Collection errorMessages) {
            for (Iterator iterator = errorMessages.iterator(); iterator.hasNext();) {
                String s = (String) iterator.next();
                addActionError(s);
            }
        }

        public Collection getActionErrors() {
            return null;
        }

        public void setActionMessages(Collection messages) {
            for (Iterator iterator = messages.iterator(); iterator.hasNext();) {
                String s = (String) iterator.next();
                addActionMessage(s);
            }
        }

        public Collection getActionMessages() {
            return null;
        }

        public void setFieldErrors(Map errorMap) {
            for (Iterator iterator = errorMap.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry entry = (Map.Entry) iterator.next();
                addFieldError((String) entry.getKey(), (String) entry.getValue());
            }
        }

        public Map getFieldErrors() {
            return null;
        }

        public void addActionError(String anErrorMessage) {
            log.error("Validation error: " + anErrorMessage);
        }

        public void addActionMessage(String aMessage) {
            log.info("Validation Message: " + aMessage);
        }

        public void addFieldError(String fieldName, String errorMessage) {
            log.error("Validation error for " + fieldName + ":" + errorMessage);
        }

        public boolean hasActionErrors() {
            return false;
        }

        public boolean hasActionMessages() {
            return false;
        }

        public boolean hasErrors() {
            return false;
        }

        public boolean hasFieldErrors() {
            return false;
        }
    }
}
