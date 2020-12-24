/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.conversion.annotations;

import java.lang.annotation.*;

/**
 * <!-- START SNIPPET: description -->
 * <p/>This annotation is used for class and application wide conversion rules.
 * <p>
 * Class wide conversion:<br/>
 * The conversion rules will be assembled in a file called <code>XXXAction-conversion.properties</code>
 * within the same package as the related action class.
 * Set type to: <code>type = ConversionType.CLASS</code>
 * </p>
 * <p>
 * Allication wide conversion:<br/>
 * The conversion rules will be assembled within the <code>xwork-conversion.properties</code> file within the classpath root.
 * Set type to: <code>type = ConversionType.APPLICATION</code>
 * <p/>
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * The TypeConversion annotation can be applied at property and method level.
 * <!-- END SNIPPET: usage -->
 *
 * <p/> <u>Annotation parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 * <table>
 * <thead>
 * <tr>
 * <th>Parameter</th>
 * <th>Required</th>
 * <th>Default</th>
 * <th>Description</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>key</td>
 * <td>no</td>
 * <td>The annotated property/key name</td>
 * <td>The optional property name mostly used within TYPE level annotations.</td>
 * </tr>
 * <tr>
 * <td>type</td>
 * <td>no</td>
 * <td>ConversionType.CLASS</td>
 * <td>Enum value of ConversionType.  Determines whether the conversion should be applied at application or class level.</td>
 * </tr>
 * <tr>
 * <td>rule</td>
 * <td>no</td>
 * <td>ConversionRule.PROPERTY</td>
 * <td>Enum value of ConversionRule. The ConversionRule can be a property, a Collection or a Map.</td>
 * </tr>
 * <tr>
 * <td>converter</td>
 * <td>either this or value</td>
 * <td>&nbsp;</td>
 * <td>The class name of the TypeConverter to be used as converter.</td>
 * </tr>
 * <tr>
 * <td>value</td>
 * <td>either converter or this</td>
 * <td>&nbsp;</td>
 * <td>The value to set for ConversionRule.KEY_PROPERTY.</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;Conversion()
 * public class ConversionAction implements Action {
 *
 *   private String convertInt;
 *
 *   private String convertDouble;
 *   private List users = null;
 *
 *   private HashMap keyValues = null;
 *
 *   &#64;TypeConversion(type = ConversionType.APPLICATION, converter = "com.opensymphony.xwork2.util.XWorkBasicConverter")
 *   public void setConvertInt( String convertInt ) {
 *       this.convertInt = convertInt;
 *   }
 *
 *   &#64;TypeConversion(converter = "com.opensymphony.xwork2.util.XWorkBasicConverter")
 *   public void setConvertDouble( String convertDouble ) {
 *       this.convertDouble = convertDouble;
 *   }
 *
 *   &#64;TypeConversion(rule = ConversionRule.COLLECTION, converter = "java.util.String")
 *   public void setUsers( List users ) {
 *       this.users = users;
 *   }
 *
 *   &#64;TypeConversion(rule = ConversionRule.MAP, converter = "java.math.BigInteger")
 *   public void setKeyValues( HashMap keyValues ) {
 *       this.keyValues = keyValues;
 *   }
 *
 *   &#64;TypeConversion(type = ConversionType.APPLICATION, property = "java.util.Date", converter = "com.opensymphony.xwork2.util.XWorkBasicConverter")
 *   public String execute() throws Exception {
 *       return SUCCESS;
 *   }
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id: TypeConversion.java 1548 2007-07-16 15:32:09Z rainerh $
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TypeConversion {

    /**
     * The optional key name used within TYPE level annotations.
     * Defaults to the property name.
     */
    String key() default "";

    /**
     * The ConversionType can be either APPLICATION or CLASS.
     * Defaults to CLASS.
     *
     * Note: If you use ConversionType.APPLICATION, you can not set a value!
     */
    ConversionType type() default ConversionType.CLASS;

    /**
     * The ConversionRule can be a PROPERTY, KEY, KEY_PROPERTY, ELEMENT, COLLECTION (deprecated) or a MAP.
     * Note: Collection and Map vonversion rules can be determined via com.opensymphony.xwork2.util.DefaultObjectTypeDeterminer.
     *
     * @see com.opensymphony.xwork2.util.DefaultObjectTypeDeterminer
     */
    ConversionRule rule() default ConversionRule.PROPERTY;

    /**
     * The class of the TypeConverter to be used as converter.
     *
     * Note: This can not be used with ConversionRule.KEY_PROPERTY! 
     */
    String converter() default "";

    /**
     * If used with ConversionRule.KEY_PROPERTY specify a value here!
     *
     * Note: If you use ConversionType.APPLICATION, you can not set a value!
     */
    String value() default "";

}
