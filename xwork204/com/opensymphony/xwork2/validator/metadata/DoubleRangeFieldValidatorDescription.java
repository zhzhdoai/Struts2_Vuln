/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.validator.metadata;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * <code>DoubleRangeFieldValidatorDescription</code>
 *
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: DoubleRangeFieldValidatorDescription.java 1187 2006-11-13 08:05:32Z mrdon $
 */
public class DoubleRangeFieldValidatorDescription extends AbstractFieldValidatorDescription {

    public String min;
    public String max;

    public DoubleRangeFieldValidatorDescription() {
    }

    /**
     * Creates an DoubleRangeFieldValidatorDescription with the specified field name.
     *
     * @param fieldName
     */
    public DoubleRangeFieldValidatorDescription(String fieldName) {
        super(fieldName);
    }

    public void setMin(String min) {
        this.min = min;
    }

    public void setMax(String max) {
        this.max = max;
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
                writer.println("\t\t<field-validator type=\"double\">");
            } else {
                writer.println("\t\t<field-validator type=\"double\" short-circuit=\"true\">");
            }
            if ( min != null && min.length() > 0) {
                writer.println("\t\t\t<param name=\"min\">" + min + "</param>");
            }
            if ( max != null && max.length() > 0) {
                writer.println("\t\t\t<param name=\"max\">" + max + "</param>");
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
                writer.println("\t<validator type=\"double\">");
            } else {
                writer.println("\t<validator type=\"double\" short-circuit=\"true\">");
            }

            writer.println("\t\t<param name=\"fieldName\">" + fieldName+ "</param>");


            if ( min != null && min.length() > 0) {
                writer.println("\t\t<param name=\"min\">" + min + "</param>");
            }
            if ( max != null && max.length() > 0) {
                writer.println("\t\t<param name=\"max\">" + max + "</param>");
            }

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
