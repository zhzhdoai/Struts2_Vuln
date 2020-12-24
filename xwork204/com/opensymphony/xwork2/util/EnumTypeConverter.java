/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.util;

import ognl.DefaultTypeConverter;

import java.util.Map;

/**
 * <code>EnumTypeConverter</code>
 *
 * <!-- START SNIPPET: description -->
 * This class converts java 5 enums to String and from String[] to enum.
 * <p/>
 * One of Java 5's improvements is providing enumeration facility.
 * Up to now, there existed no enumerations. The only way to simulate was the so-called int Enum pattern:
 * {code}
 * public static final int SEASON_WINTER = 0;
 * public static final int SEASON_SPRING = 1;
 * public static final int SEASON_SUMMER = 2;
 * public static final int SEASON_FALL   = 3;
 * {code}
 * <p/>
 * Java 5.0 now provides the following construct:
 * {code}
 * public static enum Season { WINTER, SPRING, SUMMER, FALL };
 * {code}
 * <!-- END SNIPPET: description -->
 *
 * <!-- START SNIPPET: example -->
 * h3. Implementing Java 5 Enumeration Type Conversion
 * <p/>
 * 1. myAction-conversion.properties*
 * <p/>
 * Place a myAction-conversion.properties-file in the path of your Action.
 * Add the following entry to the properties-file:
 * {code}
 * nameOfYourField=fullyClassifiedNameOfYourConverter
 * {code}
 * &nbsp;
 * <p/>
 * 2. myAction.java*
 * Your action contains the _enumeration_:
 * {code}
 * public enum Criticality {DEBUG, INFO, WARNING, ERROR, FATAL}
 * {code}
 * &nbsp;
 * * Your action contains the _private field_:
 * {code}
 * private myEnum myFieldForEnum;
 * {code}
 * &nbsp;
 * Your action contains _getters and setters_ for your field:
 * {code}
 * public myEnum getCriticality() {
 *         return myFieldForEnum;
 *     }
 *
 *     public void setCriticality(myEnum myFieldForEnum) {
 *         this.myFieldForEnum= myFieldForEnum;
 *     }
 * {code}
 * <p/>
 * 3. JSP*
 * <p/>
 * &nbsp;&nbsp;&nbsp; In your jsp you can access an enumeration value just normal by using the known <ww:property>-Tag:
 * {code}
 * <ww:property value="myField"/>
 * {code}
 * <!-- END SNIPPET: example -->
 *
 * @author Tamara Cattivelli
 * @author <a href="mailto:hermanns@aixcept.de">Rainer Hermanns</a>
 * @version $Id: EnumTypeConverter.java 1050 2006-06-26 21:46:27 +0200 (Mon, 26 Jun 2006) rainerh $
 */
public class EnumTypeConverter extends DefaultTypeConverter {

    /**
     * Converts the given object to a given type. How this is to be done is implemented in toClass. The OGNL context, o
     * and toClass are given. This method should be able to handle conversion in general without any context or object
     * specified.
     *
     * @param context - OGNL context under which the conversion is being done
     * @param o       - the object to be converted
     * @param toClass - the class that contains the code to convert to enumeration
     * @return Converted value of type declared in toClass or TypeConverter.NoConversionPossible to indicate that the
     *         conversion was not possible.
     */
    public Object convertValue(Map context, Object o, Class toClass) {
        if (o instanceof String[]) {
            return convertFromString(((String[]) o)[0], toClass);
        } else if (o instanceof String) {
            return convertFromString((String) o, toClass);
        }

        return super.convertValue(context, o, toClass);
    }

    /**
     * Converts one or more String values to the specified class.
     * @param value - the String values to be converted, such as those submitted from an HTML form
     * @param toClass - the class to convert to
     * @return the converted object
     */
    public java.lang.Enum convertFromString(String value, Class toClass) {
        return Enum.valueOf(toClass, value);
    }

}