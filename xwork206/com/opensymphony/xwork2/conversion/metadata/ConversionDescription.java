/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.conversion.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.opensymphony.xwork2.conversion.annotations.ConversionRule;
import com.opensymphony.xwork2.util.DefaultObjectTypeDeterminer;

import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * <code>ConversionDescription</code>
 *
 * @author Rainer Hermanns
 * @version $Id: ConversionDescription.java 1063 2006-07-10 00:30:29Z mrdon $
 */
public class ConversionDescription {

    /**
     * Jakarta commons-logging reference.
     */
    protected static Log log = null;


    public static final String KEY_PREFIX = "Key_";
    public static final String ELEMENT_PREFIX = "Element_";
    public static final String KEY_PROPERTY_PREFIX = "KeyProperty_";
    public static final String DEPRECATED_ELEMENT_PREFIX = "Collection_";

    /**
     * Key used for type conversion of maps.
     */
    String MAP_PREFIX = "Map_";

    public String property;
    public String typeConverter = "";
    public String rule = "";
    public String value = "";
    public String fullQualifiedClassName;
    public String type = null;

    public ConversionDescription() {
        log = LogFactory.getLog(this.getClass());
    }

    /**
     * Creates an ConversionDescription with the specified property name.
     *
     * @param property
     */
    public ConversionDescription(String property) {
        this.property = property;
        log = LogFactory.getLog(this.getClass());
    }

    /**
     * <p>
     * Sets the property name to be inserted into the related conversion.properties file.<br/>
     * Note: Do not add COLLECTION_PREFIX or MAP_PREFIX keys to property names.
     * </p>
     *
     * @param property The property to be converted.
     */
    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * Sets the class name of the type converter to be used.
     *
     * @param typeConverter The class name of the type converter.
     */
    public void setTypeConverter(String typeConverter) {
        this.typeConverter = typeConverter;
    }

    /**
     * Sets the rule prefix for COLLECTION_PREFIX or MAP_PREFIX key.
     * Defaults to en emtpy String.
     *
     * @param rule
     */
    public void setRule(String rule) {
        if (rule != null && rule.length() > 0) {
            if (rule.equals(ConversionRule.COLLECTION.toString())) {
                this.rule = DefaultObjectTypeDeterminer.DEPRECATED_ELEMENT_PREFIX;
            } else if (rule.equals(ConversionRule.ELEMENT.toString())) {
                this.rule = DefaultObjectTypeDeterminer.ELEMENT_PREFIX;
            } else if (rule.equals(ConversionRule.KEY.toString())) {
                this.rule = DefaultObjectTypeDeterminer.KEY_PREFIX;
            } else if (rule.equals(ConversionRule.KEY_PROPERTY.toString())) {
                this.rule = DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX;
            } else if (rule.equals(ConversionRule.MAP.toString())) {
                this.rule = MAP_PREFIX;
            }
        }
    }


    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Returns the conversion description as property entry.
     * <p>
     * Example:<br/>
     * property.name = converter.className<br/>
     * Collection_property.name = converter.className<br/>
     * Map_property.name = converter.className
     * KeyProperty_name = id
     * </p>
     *
     * @return the conversion description as property entry.
     */
    public String asProperty() {
        StringWriter sw = new StringWriter();
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(sw);
            writer.print(rule);
            writer.print(property);
            writer.print("=");
            if ( rule.startsWith(DefaultObjectTypeDeterminer.KEY_PROPERTY_PREFIX) && value != null && value.length() > 0 ) {
                writer.print(value);
            } else {
                writer.print(typeConverter);
            }
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }

        return sw.toString();

    }

    /**
     * Returns the fullQualifiedClassName attribute is used to create the special <code>conversion.properties</code> file name.
     *
     * @return fullQualifiedClassName
     */
    public String getFullQualifiedClassName() {
        return fullQualifiedClassName;
    }

    /**
     * The fullQualifiedClassName attribute is used to create the special <code>conversion.properties</code> file name.
     *
     * @param fullQualifiedClassName
     */
    public void setFullQualifiedClassName(String fullQualifiedClassName) {
        this.fullQualifiedClassName = fullQualifiedClassName;
    }
}
