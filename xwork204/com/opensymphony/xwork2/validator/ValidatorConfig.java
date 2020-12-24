/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import java.util.Map;
import com.opensymphony.xwork2.util.location.Located;

/**
 * Holds the necessary information for configuring an instance of a Validator.
 * 
 * 
 * @author James House
 * @author Rainer Hermanns
 */
public class ValidatorConfig extends Located {

    private String type;
    private Map params;
    private String defaultMessage;
    private String messageKey;
    private boolean shortCircuit;
    
    public ValidatorConfig() {
    }
    
    /**
     * @param validatorType
     * @param params
     */
    public ValidatorConfig(String validatorType, Map params) {
        this.type = validatorType;
        this.params = params;
    }
    
    /**
     * @return Returns the defaultMessage for the validator.
     */
    public String getDefaultMessage() {
        return defaultMessage;
    }
    
    /**
     * @param defaultMessage The defaultMessage to set on the validator.
     */
    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
    
    /**
     * @return Returns the messageKey for the validator.
     */
    public String getMessageKey() {
        return messageKey;
    }
    
    /**
     * @param messageKey The messageKey to set on the validator.
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
    
    /**
     * @return Returns wether the shortCircuit flag should be set on the 
     * validator.
     */
    public boolean isShortCircuit() {
        return shortCircuit;
    }
    
    /**
     * @param shortCircuit Whether the validator's shortCircuit flag should 
     * be set.
     */
    public void setShortCircuit(boolean shortCircuit) {
        this.shortCircuit = shortCircuit;
    }

    /**
     * @return Returns the configured params to set on the validator. 
     */
    public Map getParams() {
        return params;
    }
    
    /**
     * @param params The configured params to set on the validator.
     */
    public void setParams(Map params) {
        this.params = params;
    }
    
    /**
     * @return Returns the type of validator to configure.
     */
    public String getType() {
        return type;
    }
    
    /**
     * @param validatorType The type of validator to configure.
     */
    public void setType(String validatorType) {
        this.type = validatorType;
    }
}
