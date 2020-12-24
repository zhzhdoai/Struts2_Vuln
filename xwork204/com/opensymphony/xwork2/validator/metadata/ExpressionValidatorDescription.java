/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator.metadata;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * <code>ExpressionValidatorDescription</code>
 *
 * @author Rainer Hermanns
 * @version $Id: ExpressionValidatorDescription.java 1187 2006-11-13 08:05:32Z mrdon $
 */
public class ExpressionValidatorDescription implements ValidatorDescription {

    public String expression;
    public String key;
    public String message;
    public boolean shortCircuit;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setShortCircuit(boolean shortCircuit) {
        this.shortCircuit = shortCircuit;
    }

    /**
     * Returns the field name to create the validation rule for.
     *
     * @return The field name to create the validation rule for
     */
    public String getFieldName() {
        throw new UnsupportedOperationException("ExpressionValidator annotations cannot be applied to fields...");
    }

    public String asXml() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(sw);

            if ( shortCircuit) {
                writer.println("\t<validator type=\"expression\">");
            } else {
                writer.println("\t<validator type=\"expression\" short-circuit=\"true\">");
            }

            writer.println("\t\t<param name=\"expression\">" + expression+ "</param>");

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

    public boolean isSimpleValidator() {
        return false;
    }
}
