/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.metadata;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <code>URLValidatorDescription</code>
 *
 * @author Rainer Hermanns
 * @version $Id: URLValidatorDescription.java 1187 2006-11-13 08:05:32Z mrdon $
 */
public class URLValidatorDescription extends AbstractFieldValidatorDescription {


    public URLValidatorDescription() {
    }

    /**
     * Creates an AbstractFieldValidatorDescription with the specified aliasNames.
     *
     * @param fieldName
     */
    public URLValidatorDescription(String fieldName) {
        super(fieldName);
    }


   /**
     * Returns the field validator XML definition.
     *
     * @return the field validator XML definition.
     */
    public String asFieldXml() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(sw);

            if ( shortCircuit) {
                writer.println("\t\t<field-validator type=\"url\">");
            } else {
                writer.println("\t\t<field-validator type=\"url\" short-circuit=\"true\">");
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
        StringWriter sw = new StringWriter();
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(sw);

            if ( shortCircuit) {
                writer.println("\t<validator type=\"url\">");
            } else {
                writer.println("\t<validator type=\"url\" short-circuit=\"true\">");
            }

            writer.println("\t\t<param name=\"fieldName\">" + fieldName+ "</param>");

            if ( !"".equals(key)) {
                writer.println("\t\t<message key=\"" + key + "\">" + message + "</message>");
            } else {
                writer.println("\t\t<message>" + message + "</message>");
            }

            writer.println("\t</validator>");

        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
        return sw.toString();
    }
}
