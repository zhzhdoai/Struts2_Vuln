/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.util.DomHelper;
import com.opensymphony.xwork2.XWorkException;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Parse the validation file. (eg. MyAction-validation.xml, MyAction-actionAlias-validation.xml)
 * to return a List of ValidatorConfig encapsulating the validator information.
 *
 * @author Jason Carreira
 * @author James House
 * @author tm_jee ( tm_jee (at) yahoo.co.uk )
 * @author Rob Harrop
 * @author Rene Gielen
 *
 * @see com.opensymphony.xwork2.validator.ValidatorConfig
 */
public class ValidatorFileParser {

    static final String MULTI_TEXTVALUE_SEPARATOR = " ";

    /**
     * Parse resource for a list of ValidatorConfig objects.
     *
     * @param is input stream to the resource
     * @param resourceName file name of the resource
     * @return List list of ValidatorConfig
     */
    public static List parseActionValidatorConfigs(InputStream is, final String resourceName) {
        List validatorCfgs = new ArrayList();
        Document doc = null;

        InputSource in = new InputSource(is);
        in.setSystemId(resourceName);
            
        Map dtdMappings = new HashMap();
        dtdMappings.put("-//OpenSymphony Group//XWork Validator 1.0//EN", "xwork-validator-1.0.dtd");
        dtdMappings.put("-//OpenSymphony Group//XWork Validator 1.0.2//EN", "xwork-validator-1.0.2.dtd");
        
        doc = DomHelper.parse(in, dtdMappings);

        if (doc != null) {
            NodeList fieldNodes = doc.getElementsByTagName("field");

            // BUG: xw-305: Let validator be parsed first and hence added to 
            // the beginning of list and therefore evaluated first, so short-circuting
            // it will not cause field-leve validator to be kicked off.
            {NodeList validatorNodes = doc.getElementsByTagName("validator");
            addValidatorConfigs(validatorNodes, new HashMap(), validatorCfgs);}

            for (int i = 0; i < fieldNodes.getLength(); i++) {
                Element fieldElement = (Element) fieldNodes.item(i);
                String fieldName = fieldElement.getAttribute("name");
                Map extraParams = new HashMap();
                extraParams.put("fieldName", fieldName);

                NodeList validatorNodes = fieldElement.getElementsByTagName("field-validator");
                addValidatorConfigs(validatorNodes, extraParams, validatorCfgs);
            }
        }

        return validatorCfgs;
    }

    
    /**
     * Parses validator definitions
     *
     * @deprecated Use parseValidatorDefinitions(InputStream, String)
     * @param is The input stream
     */
    public static void parseValidatorDefinitions(InputStream is) {
        parseValidatorDefinitions(is, null);
    }
    
    
    /**
     * Parses validator definitions
     *
     * @deprecated
     * @param is The input stream
     * @param resourceName The location of the input stream
     */
    public static void parseValidatorDefinitions(InputStream is, String resourceName) {
        parseValidatorDefinitions(is, resourceName, ObjectFactory.getObjectFactory());
    }


	private static void verifyObjectFactory(ObjectFactory objectFactory) {
		if (objectFactory == null) {
    		throw new IllegalStateException("Cannot find the ObjectFactory.  "+
    				"Please initialize it by calling 'ObjectFactory.setObjectFactory()'.");
    	}
	}
    
    /**
     * Parses validator definitions
     *
     * @since 1.2
     * @param is The input stream
     * @param resourceName The location of the input stream
     */
    public static void parseValidatorDefinitions(InputStream is, String resourceName, ObjectFactory objectFactory) {
        
        InputSource in = new InputSource(is);
        in.setSystemId(resourceName);

        Map dtdMappings = new HashMap();
        dtdMappings.put("-//OpenSymphony Group//XWork Validator Config 1.0//EN", "xwork-validator-config-1.0.dtd");

        Document doc = DomHelper.parse(in, dtdMappings);

        if (doc != null) {
            NodeList nodes = doc.getElementsByTagName("validator");
            
            verifyObjectFactory(objectFactory);

            for (int i = 0; i < nodes.getLength(); i++) {
                Element validatorElement = (Element) nodes.item(i);
                String name = validatorElement.getAttribute("name");
                String className = validatorElement.getAttribute("class");

                try {
                    // catch any problems here
                    objectFactory.buildValidator(className, new HashMap(), null);
                    ValidatorFactory.registerValidator(name, className);
                } catch (Exception e) {
                    throw new XWorkException("Unable to load validator class " + className, e, validatorElement);
                }
            }
        }
    }

    /**
     * Extract trimmed text value from the given DOM element, ignoring XML comments. Appends all CharacterData nodes
     * and EntityReference nodes into a single String value, excluding Comment nodes.
     * This method is based on a method originally found in DomUtils class of Springframework.
     *
     * @see org.w3c.dom.CharacterData
     * @see org.w3c.dom.EntityReference
     * @see org.w3c.dom.Comment
     */
    public static String getTextValue(Element valueEle) {
        StringBuffer value = new StringBuffer();
        NodeList nl = valueEle.getChildNodes();
        boolean firstCDataFound = false;
        for (int i = 0; i < nl.getLength(); i++) {
            Node item = nl.item(i);
            if ((item instanceof CharacterData && !(item instanceof Comment)) || item instanceof EntityReference) {
                final String nodeValue = item.getNodeValue();
                if (nodeValue != null) {
                    if (firstCDataFound) {
                        value.append(MULTI_TEXTVALUE_SEPARATOR);
                    } else {
                        firstCDataFound = true;
                    }
                    value.append(nodeValue.trim());
                }
            }
        }
        return value.toString().trim();
    }

    private static void addValidatorConfigs(NodeList validatorNodes, Map extraParams, List validatorCfgs) {
        for (int j = 0; j < validatorNodes.getLength(); j++) {
            Element validatorElement = (Element) validatorNodes.item(j);
            String validatorType = validatorElement.getAttribute("type");
            Map params = new HashMap(extraParams);
            NodeList paramNodes = validatorElement.getElementsByTagName("param");

            for (int k = 0; k < paramNodes.getLength(); k++) {
                Element paramElement = (Element) paramNodes.item(k);
                String paramName = paramElement.getAttribute("name");
                params.put(paramName, getTextValue(paramElement));
            }

            // ensure that the type is valid...
            ValidatorFactory.lookupRegisteredValidatorType(validatorType);

            ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
            vCfg.setLocation(DomHelper.getLocationObject(validatorElement));

            vCfg.setShortCircuit(Boolean.valueOf(validatorElement.getAttribute("short-circuit")).booleanValue());

            NodeList messageNodes = validatorElement.getElementsByTagName("message");
            Element messageElement = (Element) messageNodes.item(0);
            String key = messageElement.getAttribute("key");

            if ((key != null) && (key.trim().length() > 0)) {
                vCfg.setMessageKey(key);
            }

            final Node defaultMessageNode = messageElement.getFirstChild();
            String defaultMessage = (defaultMessageNode == null) ? "" : defaultMessageNode.getNodeValue();
            vCfg.setDefaultMessage(defaultMessage);
            validatorCfgs.add(vCfg);
        }
    }
}
