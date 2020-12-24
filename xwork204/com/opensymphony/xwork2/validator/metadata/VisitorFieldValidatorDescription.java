/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.metadata;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <code>VisitorFieldValidatorDescription</code>
 *
 * @author Rainer Hermanns
 * @version $Id: VisitorFieldValidatorDescription.java 1187 2006-11-13 08:05:32Z mrdon $
 */
public class VisitorFieldValidatorDescription extends AbstractFieldValidatorDescription {

    public String context;
    public boolean appendPrefix = true;

    public VisitorFieldValidatorDescription() {
    }

    /**
     * Creates an AbstractFieldValidatorDescription with the specified field name.
     *
     * @param fieldName
     */
    public VisitorFieldValidatorDescription(String fieldName) {
        super(fieldName);
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setAppendPrefix(boolean appendPrefix) {
        this.appendPrefix = appendPrefix;
    }

    /**
     * Returns the validator XML definition.
     *
     * @return the validator XML definition.
     */
    public String asFieldXml() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(sw);

            if ( shortCircuit) {
                writer.println("\t\t<field-validator type=\"visitor\">");
            } else {
                writer.println("\t\t<field-validator type=\"visitor\" short-circuit=\"true\">");
            }

            if ( context != null && context.length() > 0) {
                writer.println("\t\t\t<param name=\"context\">" + context + "</param>");
            }

            if ( !appendPrefix) {
                writer.println("\t\t\t<param name=\"appendPrefix\">" + appendPrefix + "</param>");
            }

            if ( !"".equals(key)) {
                writer.println("\t\t\t<message key=\"" + key + "\">" + message + "</message>");
            } else {
                writer.println("\t\t\t<message>" + message + "</message>");
            }

            writer.println("\t\t</field-validator>");

        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
        return sw.toString();
    }

    /**
     * Returns the validator XML definition.
     *
     * @return the validator XML definition.
     */
    public String asSimpleXml() {
        throw new UnsupportedOperationException(getClass().getName() + " cannot be used for simple validators...");
    }
}
