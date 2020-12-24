/*
 * Copyright (c) 2002-2007 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ognl.DefaultTypeConverter;
import ognl.Ognl;
import ognl.TypeConverter;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.XWorkException;


/**
 * <!-- START SNIPPET: javadoc -->
 * XWork will automatically handle the most common type conversion for you.
 * <p/>
 * This includes support for converting to and from Strings for each of the following:
 * <ul>
 * <li>String</li>
 * <li>boolean / Boolean</li>
 * <li>char / Character</li>
 * <li>int / Integer, float / Float, long / Long, double / Double</li>
 * <li>dates - uses the SHORT or RFC3339 format (<code>yyyy-MM-dd'T'HH:mm:ss</code>) for the Locale associated with the current request</li>
 * <li>arrays - assuming the individual strings can be coverted to the individual items</li>
 * <li>collections - if not object type can be determined, it is assumed to be a String and a new ArrayList is
 * created</li>
 * </ul>
 * <p/>
 * <b>Note:</b> that with arrays the type conversion will defer to the type of the array elements and try to convert each
 * item individually. As with any other type conversion, if the conversion can't be performed the standard type
 * conversion error reporting is used to indicate a problem occured while processing the type conversion.
 * <!-- END SNIPPET: javadoc -->
 *
 * @author <a href="mailto:plightbo@gmail.com">Pat Lightbody</a>
 * @author Mike Mosiewicz
 * @author Rainer Hermanns
 * @author <a href='mailto:the_mindstorm[at]evolva[dot]ro'>Alexandru Popescu</a>
 */
public class XWorkBasicConverter extends DefaultTypeConverter {

    private static final String MILLISECOND_FORMAT = ".SSS";

    public Object convertValue(Map context, Object o, Member member, String s, Object value, Class toType) {
        Object result = null;

        if (value == null || toType.isAssignableFrom(value.getClass())) {
            // no need to convert at all, right?
            return value;
        }

        if (toType == String.class) {
            /* the code below has been disabled as it causes sideffects in Strtus2 (XW-512)
            // if input (value) is a number then use special conversion method (XW-490)
            Class inputType = value.getClass();
            if (Number.class.isAssignableFrom(inputType)) {
                result = doConvertFromNumberToString(context, value, inputType);
                if (result != null) {
                    return result;
                }
            }*/
            // okay use default string conversion
            result = doConvertToString(context, value);
        } else if (toType == boolean.class) {
            result = doConvertToBoolean(value);
        } else if (toType == Boolean.class) {
            result = doConvertToBoolean(value);
        } else if (toType.isArray()) {
            result = doConvertToArray(context, o, member, s, value, toType);
        } else if (Date.class.isAssignableFrom(toType)) {
            result = doConvertToDate(context, value, toType);
        } else if (Collection.class.isAssignableFrom(toType)) {
            result = doConvertToCollection(context, o, member, s, value, toType);
        } else if (toType == Character.class) {
            result = doConvertToCharacter(value);
        } else if (toType == char.class) {
            result = doConvertToCharacter(value);
        } else if (Number.class.isAssignableFrom(toType)) {
            result = doConvertToNumber(context, value, toType);
        } else if (toType == Class.class) {
            result = doConvertToClass(value);
        }

        if (result == null) {
            if (value instanceof Object[]) {
                Object[] array = (Object[]) value;

                if (array.length >= 1) {
                    value = array[0];
                }

                // let's try to convert the first element only
                result = convertValue(context, o, member, s, value, toType);
            } else if (!"".equals(value)) { // we've already tried the types we know
                result = super.convertValue(context, value, toType);
            }

            if (result == null && value != null && !"".equals(value)) {
                throw new XWorkException("Cannot create type " + toType + " from value " + value);
            }
        }

        return result;
    }

    private Locale getLocale(Map context) {
        if (context == null) {
            return Locale.getDefault();
        }

        Locale locale = (Locale) context.get(ActionContext.LOCALE);

        if (locale == null) {
            locale = Locale.getDefault();
        }

        return locale;
    }

    /**
     * Creates a Collection of the specified type.
     *
     * @param fromObject
     * @param propertyName
     * @param toType       the type of Collection to create
     * @param memberType   the type of object elements in this collection must be
     * @param size         the initial size of the collection (ignored if 0 or less)
     * @return a Collection of the specified type
     */
    private Collection createCollection(Object fromObject, String propertyName, Class toType, Class memberType, int size) {
//        try {
//            Object original = Ognl.getValue(OgnlUtil.compile(propertyName),fromObject);
//            if (original instanceof Collection) {
//                Collection coll = (Collection) original;
//                coll.clear();
//                return coll;
//            }
//        } catch (Exception e) {
//            // fail back to creating a new one
//        }

        Collection result;

        if (toType == Set.class) {
            if (size > 0) {
                result = new HashSet(size);
            } else {
                result = new HashSet();
            }
        } else if (toType == SortedSet.class) {
            result = new TreeSet();
        } else {
            if (size > 0) {
                result = new XWorkList(memberType, size);
            } else {
                result = new XWorkList(memberType);
            }
        }

        return result;
    }

    private Object doConvertToArray(Map context, Object o, Member member, String s, Object value, Class toType) {
        Object result = null;
        Class componentType = toType.getComponentType();

        if (componentType != null) {
            TypeConverter converter = Ognl.getTypeConverter(context);

            if (value.getClass().isArray()) {
                int length = Array.getLength(value);
                result = Array.newInstance(componentType, length);

                for (int i = 0; i < length; i++) {
                    Object valueItem = Array.get(value, i);
                    Array.set(result, i, converter.convertValue(context, o, member, s, valueItem, componentType));
                }
            } else {
                result = Array.newInstance(componentType, 1);
                Array.set(result, 0, converter.convertValue(context, o, member, s, value, componentType));
            }
        }

        return result;
    }

    private Object doConvertToCharacter(Object value) {
        if (value instanceof String) {
            String cStr = (String) value;

            return (cStr.length() > 0) ? new Character(cStr.charAt(0)) : null;
        }

        return null;
    }

    private Object doConvertToBoolean(Object value) {
        if (value instanceof String) {
            String bStr = (String) value;

            return Boolean.valueOf(bStr);
        }

        return null;
    }

    private Class doConvertToClass(Object value) {
        Class clazz = null;

        if (value instanceof String && value != null && ((String) value).length() > 0) {
            try {
                clazz = Class.forName((String) value);
            } catch (ClassNotFoundException e) {
                throw new XWorkException(e.getLocalizedMessage(), e);
            }
        }

        return clazz;
    }

    private Collection doConvertToCollection(Map context, Object o, Member member, String prop, Object value, Class toType) {
        Collection result;
        Class memberType = String.class;

        if (o != null) {
            //memberType = (Class) XWorkConverter.getInstance().getConverter(o.getClass(), XWorkConverter.CONVERSION_COLLECTION_PREFIX + prop);
            memberType = XWorkConverter.getInstance().getObjectTypeDeterminer().getElementClass(o.getClass(), prop, null);

            if (memberType == null) {
                memberType = String.class;
            }
        }

        if (toType.isAssignableFrom(value.getClass())) {
            // no need to do anything
            result = (Collection) value;
        } else if (value.getClass().isArray()) {
            Object[] objArray = (Object[]) value;
            TypeConverter converter = Ognl.getTypeConverter(context);
            result = createCollection(o, prop, toType, memberType, objArray.length);

            for (int i = 0; i < objArray.length; i++) {
                result.add(converter.convertValue(context, o, member, prop, objArray[i], memberType));
            }
        } else if (Collection.class.isAssignableFrom(value.getClass())) {
            Collection col = (Collection) value;
            TypeConverter converter = Ognl.getTypeConverter(context);
            result = createCollection(o, prop, toType, memberType, col.size());

            for (Iterator it = col.iterator(); it.hasNext();) {
                result.add(converter.convertValue(context, o, member, prop, it.next(), memberType));
            }
        } else {
            result = createCollection(o, prop, toType, memberType, -1);
            result.add(value);
        }

        return result;
    }

    private Object doConvertToDate(Map context, Object value, Class toType) {
        Date result = null;

        if (value instanceof String && value != null && ((String) value).length() > 0) {
            String sa = (String) value;
            Locale locale = getLocale(context);

            DateFormat df = null;
            if (java.sql.Time.class == toType) {
                df = DateFormat.getTimeInstance(DateFormat.MEDIUM, locale);
            } else if (java.sql.Timestamp.class == toType) {
                Date check = null;
                SimpleDateFormat dtfmt = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT,
                        DateFormat.MEDIUM,
                        locale);
                SimpleDateFormat fullfmt = new SimpleDateFormat(dtfmt.toPattern() + MILLISECOND_FORMAT,
                        locale);

                SimpleDateFormat dfmt = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT,
                        locale);

                SimpleDateFormat[] fmts = {fullfmt, dtfmt, dfmt};
                for (int i = 0; i < fmts.length; i++) {
                    try {
                        check = fmts[i].parse(sa);
                        df = fmts[i];
                        if (check != null) {
                            break;
                        }
                    } catch (ParseException ignore) {
                    }
                }
            } else if (java.util.Date.class == toType) {
                Date check = null;
                SimpleDateFormat d1 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, locale);
                SimpleDateFormat d2 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
                SimpleDateFormat d3 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
                SimpleDateFormat rfc3399 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                SimpleDateFormat[] dfs = {d1, d2, d3, rfc3399}; //added RFC 3339 date format (XW-473)
                for (int i = 0; i < dfs.length; i++) {
                    try {
                        check = dfs[i].parse(sa);
                        df = dfs[i];
                        if (check != null) {
                            break;
                        }
                    }
                    catch (ParseException ignore) {
                    }
                }
            }
            //final fallback for dates without time
            if (df == null) {
                df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
            }
            try {
                df.setLenient(false); // let's use strict parsing (XW-341)
                result = df.parse(sa);
                if (!(Date.class == toType)) {
                    try {
                        Constructor constructor = toType.getConstructor(new Class[]{long.class});
                        return constructor.newInstance(new Object[]{new Long(result.getTime())});
                    } catch (Exception e) {
                        throw new XWorkException("Couldn't create class " + toType + " using default (long) constructor", e);
                    }
                }
            } catch (ParseException e) {
                throw new XWorkException("Could not parse date", e);
            }
        } else if (Date.class.isAssignableFrom(value.getClass())) {
            result = (Date) value;
        }
        return result;
    }

    private Object doConvertToNumber(Map context, Object value, Class toType) {
        if (value instanceof String) {
            if (toType == BigDecimal.class) {
                return new BigDecimal((String) value);
            } else if (toType == BigInteger.class) {
                return new BigInteger((String) value);
            } else {
                String stringValue = (String) value;
                if (!toType.isPrimitive() && (stringValue == null || stringValue.length() == 0)) {
                    return null;
                }
                NumberFormat numFormat = NumberFormat.getInstance(getLocale(context));
                ParsePosition parsePos = new ParsePosition(0);
                if (isIntegerType(toType)) {
                    numFormat.setParseIntegerOnly(true);
                }
                numFormat.setGroupingUsed(true);
                Number number = numFormat.parse(stringValue, parsePos);

                if (parsePos.getIndex() != stringValue.length()) {
                    throw new XWorkException("Unparseable number: \"" + stringValue + "\" at position "
                            + parsePos.getIndex());
                } else {
                    value = super.convertValue(context, number, toType);
                }
            }
        } else if (value instanceof Object[]) {
            Object[] objArray = (Object[]) value;

            if (objArray.length == 1) {
                return doConvertToNumber(context, objArray[0], toType);
            }
        }

        // pass it through DefaultTypeConverter
        return super.convertValue(context, value, toType);
    }

    protected boolean isIntegerType(Class type) {
        if (double.class == type || float.class == type || Double.class == type || Float.class == type
                || char.class == type || Character.class == type) {
            return false;
        }

        return true;
    }

    /**
     * Converts the input as a number using java's number formatter to a string output.
     */
    private String doConvertFromNumberToString(Map context, Object value, Class toType) {
        // XW-409: If the input is a Number we should format it to a string using the choosen locale and use java's numberformatter
        if (Number.class.isAssignableFrom(toType)) {
            NumberFormat numFormat = NumberFormat.getInstance(getLocale(context));
            if (isIntegerType(toType)) {
                numFormat.setParseIntegerOnly(true);
            }
            numFormat.setGroupingUsed(true);
            numFormat.setMaximumFractionDigits(99); // to be sure we include all digits after decimal seperator, otherwise some of the fractions can be chopped

            String number = numFormat.format(value);
            if (number != null) {
                return number;
            }
        }

        return null; // no number
    }


    private String doConvertToString(Map context, Object value) {
        String result = null;

        if (value instanceof int[]) {
            int[] x = (int[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Integer(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof long[]) {
            long[] x = (long[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Long(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof double[]) {
            double[] x = (double[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Double(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof boolean[]) {
            boolean[] x = (boolean[]) value;
            List intArray = new ArrayList(x.length);

            for (int i = 0; i < x.length; i++) {
                intArray.add(new Boolean(x[i]));
            }

            result = TextUtils.join(", ", intArray);
        } else if (value instanceof Date) {
            DateFormat df = null;
            if (value instanceof java.sql.Time) {
                df = DateFormat.getTimeInstance(DateFormat.MEDIUM, getLocale(context));
            } else if (value instanceof java.sql.Timestamp) {
                SimpleDateFormat dfmt = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT,
                        DateFormat.MEDIUM,
                        getLocale(context));
                df = new SimpleDateFormat(dfmt.toPattern() + MILLISECOND_FORMAT);
            } else {
                df = DateFormat.getDateInstance(DateFormat.SHORT, getLocale(context));
            }
            result = df.format(value);
        } else if (value instanceof String[]) {
            result = TextUtils.join(", ", (String[]) value);
        }

        return result;
    }
}
