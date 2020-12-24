/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.text.ParseException;

import com.opensymphony.xwork2.validator.annotations.*;

/**
 * <code>AnnotationValidationConfigurationBuilder</code>
 *
 * @author Rainer Hermanns
 * @author jepjep
 * @version $Id: AnnotationValidationConfigurationBuilder.java 1408 2007-03-31 16:14:52Z rainerh $
 */
public class AnnotationValidationConfigurationBuilder {

    private static final Pattern SETTER_PATTERN = Pattern.compile("set([A-Z][A-Za-z0-9]*)$");
    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is|has)([A-Z][A-Za-z0-9]*)$");



    private static List<ValidatorConfig> processAnnotations(Object o) {

        List<ValidatorConfig> result = new ArrayList<ValidatorConfig>();

        String fieldName = null;
        String methodName = null;

        Annotation[] annotations = null;

        if (o instanceof Class) {
            Class clazz = (Class) o;
            annotations = clazz.getAnnotations();
        }

        if (o instanceof Method) {
            Method method = (Method) o;
            fieldName = resolvePropertyName(method);
            methodName = method.getName();

            annotations = method.getAnnotations();
        }

        if (annotations != null) {
            for (Annotation a : annotations) {

                // Process collection of custom validations
                if (a instanceof Validations) {
                    processValidationAnnotation(a, fieldName, result);

                }

                // Process single custom validator
                if (a instanceof Validation) {
                    Validation v = (Validation) a;
                    if ( v.validations() != null ) {
                        for ( Validations val: v.validations()) {
                            processValidationAnnotation(val , fieldName, result);
                        }
                    }

                }
                // Process single custom validator
                else if (a instanceof ExpressionValidator) {
                    ExpressionValidator v = (ExpressionValidator) a;
                    ValidatorConfig temp = processExpressionValidatorAnnotation(v, fieldName);
                    if (temp != null) {
                        result.add(temp);
                    }

                }
                // Process single custom validator
                else if (a instanceof CustomValidator) {
                    CustomValidator v = (CustomValidator) a;
                    ValidatorConfig temp = processCustomValidatorAnnotation(v, fieldName);
                    if (temp != null) {
                        result.add(temp);
                    }

                }

                // Process ConversionErrorFieldValidator
                else if ( a instanceof ConversionErrorFieldValidator) {
                    ConversionErrorFieldValidator v = (ConversionErrorFieldValidator) a;
                    ValidatorConfig temp = processConversionErrorFieldValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
                // Process DateRangeFieldValidator
                else if ( a instanceof DateRangeFieldValidator) {
                    DateRangeFieldValidator v = (DateRangeFieldValidator) a;
                    ValidatorConfig temp = processDateRangeFieldValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
                // Process EmailValidator
                else if ( a instanceof EmailValidator) {
                    EmailValidator v = (EmailValidator) a;
                    ValidatorConfig temp = processEmailValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
                // Process FieldExpressionValidator
                else if ( a instanceof FieldExpressionValidator) {
                    FieldExpressionValidator v = (FieldExpressionValidator) a;
                    ValidatorConfig temp = processFieldExpressionValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
                // Process IntRangeFieldValidator
                else if ( a instanceof IntRangeFieldValidator) {
                    IntRangeFieldValidator v = (IntRangeFieldValidator) a;
                    ValidatorConfig temp = processIntRangeFieldValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
                // Process DoubleRangeFieldValidator
                else if ( a instanceof DoubleRangeFieldValidator) {
                    DoubleRangeFieldValidator v = (DoubleRangeFieldValidator) a;
                    ValidatorConfig temp = processDoubleRangeFieldValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
                // Process RequiredFieldValidator
                else if ( a instanceof RequiredFieldValidator) {
                    RequiredFieldValidator v = (RequiredFieldValidator) a;
                    ValidatorConfig temp = processRequiredFieldValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
                // Process RequiredStringValidator
                else if ( a instanceof RequiredStringValidator) {
                    RequiredStringValidator v = (RequiredStringValidator) a;
                    ValidatorConfig temp = processRequiredStringValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
                // Process StringLengthFieldValidator
                else if ( a instanceof StringLengthFieldValidator) {
                    StringLengthFieldValidator v = (StringLengthFieldValidator) a;
                    ValidatorConfig temp = processStringLengthFieldValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }
                }
                // Process UrlValidator
                else if ( a instanceof UrlValidator) {
                    UrlValidator v = (UrlValidator) a;
                    ValidatorConfig temp = processUrlValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
                // Process VisitorFieldValidator
                else if ( a instanceof VisitorFieldValidator) {
                    VisitorFieldValidator v = (VisitorFieldValidator) a;
                    ValidatorConfig temp = processVisitorFieldValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
                // Process RegexFieldValidator
                else if ( a instanceof RegexFieldValidator) {
                    RegexFieldValidator v = (RegexFieldValidator) a;
                    ValidatorConfig temp = processRegexFieldValidatorAnnotation(v, fieldName);
                    if ( temp != null) {
                        result.add(temp);
                    }

                }
            }
            
            if (methodName != null) {
                for (ValidatorConfig vc : result) {
                    vc.getParams().put("methodName", methodName);
                }
            }
        }
        return result;
    }

    private static void processValidationAnnotation(Annotation a, String fieldName, List<ValidatorConfig> result) {
        Validations validations = (Validations) a;
        CustomValidator[] cv = validations.customValidators();
        if ( cv != null ) {
            for (CustomValidator v : cv) {
                ValidatorConfig temp = processCustomValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        ExpressionValidator[] ev = validations.expressions();
        if ( ev != null ) {
            for (ExpressionValidator v : ev) {
                ValidatorConfig temp = processExpressionValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        ConversionErrorFieldValidator[] cef = validations.conversionErrorFields();
        if ( cef != null ) {
            for (ConversionErrorFieldValidator v : cef) {
                ValidatorConfig temp = processConversionErrorFieldValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        DateRangeFieldValidator[] drfv = validations.dateRangeFields();
        if ( drfv != null ) {
            for (DateRangeFieldValidator v : drfv) {
                ValidatorConfig temp = processDateRangeFieldValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        EmailValidator[] emv = validations.emails();
        if ( emv != null ) {
            for (EmailValidator v : emv) {
                ValidatorConfig temp = processEmailValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        FieldExpressionValidator[] fev = validations.fieldExpressions();
        if ( fev != null ) {
            for (FieldExpressionValidator v : fev) {
                ValidatorConfig temp = processFieldExpressionValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        IntRangeFieldValidator[] irfv = validations.intRangeFields();
        if ( irfv != null ) {
            for (IntRangeFieldValidator v : irfv) {
                ValidatorConfig temp = processIntRangeFieldValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        RegexFieldValidator[] rfv = validations.regexFields();
        if ( rfv != null ) {
            for (RegexFieldValidator v : rfv) {
                ValidatorConfig temp = processRegexFieldValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        RequiredFieldValidator[] rv = validations.requiredFields();
        if ( rv != null ) {
            for (RequiredFieldValidator v : rv) {
                ValidatorConfig temp = processRequiredFieldValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        RequiredStringValidator[] rsv = validations.requiredStrings();
        if ( rsv != null ) {
            for (RequiredStringValidator v : rsv) {
                ValidatorConfig temp = processRequiredStringValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        StringLengthFieldValidator[] slfv = validations.stringLengthFields();
        if ( slfv != null ) {
            for (StringLengthFieldValidator v : slfv) {
                ValidatorConfig temp = processStringLengthFieldValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        UrlValidator[] uv = validations.urls();
        if ( uv != null ) {
            for (UrlValidator v : uv) {
                ValidatorConfig temp = processUrlValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
        VisitorFieldValidator[] vfv = validations.visitorFields();
        if ( vfv != null ) {
            for (VisitorFieldValidator v : vfv) {
                ValidatorConfig temp = processVisitorFieldValidatorAnnotation(v, fieldName);
                if (temp != null) {
                    result.add(temp);
                }
            }
        }
    }

    private static ValidatorConfig processExpressionValidatorAnnotation(ExpressionValidator v, String fieldName) {
        String validatorType = "expression";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        }

        params.put("expression", v.expression());

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;

    }

    private static ValidatorConfig processCustomValidatorAnnotation(CustomValidator v, String fieldName) {

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }


        String validatorType = v.type();

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);

        Annotation[] recursedAnnotations = v.parameters();

        if ( recursedAnnotations != null ) {
            for (Annotation a2 : recursedAnnotations) {

                if (a2 instanceof ValidationParameter) {

                    ValidationParameter parameter = (ValidationParameter) a2;
                    String parameterName = parameter.name();
                    String parameterValue = parameter.value();
                    params.put(parameterName, parameterValue);
                }

            }
        }

        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processRegexFieldValidatorAnnotation(RegexFieldValidator v, String fieldName) {
        String validatorType = "regex";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        params.put("expression", v.expression());

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processVisitorFieldValidatorAnnotation(VisitorFieldValidator v, String fieldName) {

        String validatorType = "visitor";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        params.put("context", v.context());
        params.put("appendPrefix", v.appendPrefix());

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processUrlValidatorAnnotation(UrlValidator v, String fieldName) {
        String validatorType = "url";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processStringLengthFieldValidatorAnnotation(StringLengthFieldValidator v, String fieldName) {
        String validatorType = "stringlength";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        if ( v.maxLength() != null && v.maxLength().length() > 0) {
            params.put("maxLength", v.maxLength());
        }
        if ( v.minLength() != null && v.minLength().length() > 0) {
            params.put("minLength", v.minLength());
        }
        params.put("trim", v.trim());

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static Date parseDateString(String value) {

        SimpleDateFormat d1 = (SimpleDateFormat) DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, Locale.getDefault());
        SimpleDateFormat d2 = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, Locale.getDefault());
        SimpleDateFormat d3 = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.getDefault());
        SimpleDateFormat[] dfs = {d1, d2, d3};
        DateFormat df = null;
        for (int i = 0; i < dfs.length; i++) {
            try {
                Date check = dfs[i].parse(value);
                df = dfs[i];
                if (check != null) {
                    return check;
                }
            }
            catch (ParseException ignore) {
            }
        }
        return null;

    }

    private static ValidatorConfig processRequiredStringValidatorAnnotation(RequiredStringValidator v, String fieldName) {
        String validatorType = "requiredstring";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        params.put("trim", v.trim());

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processRequiredFieldValidatorAnnotation(RequiredFieldValidator v, String fieldName) {
        String validatorType = "required";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processIntRangeFieldValidatorAnnotation(IntRangeFieldValidator v, String fieldName) {
        String validatorType = "int";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        if ( v.min() != null && v.min().length() > 0) {
            params.put("min", v.min());
        }
        if ( v.max() != null && v.max().length() > 0) {
            params.put("max", v.max());
        }

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processDoubleRangeFieldValidatorAnnotation(DoubleRangeFieldValidator v, String fieldName) {
        String validatorType = "double";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        if ( v.minInclusive() != null && v.minInclusive().length() > 0) {
            params.put("minInclusive", v.minInclusive());
        }
        if ( v.maxInclusive() != null && v.maxInclusive().length() > 0) {
            params.put("maxInclusive", v.maxInclusive());
        }

        if ( v.minExclusive() != null && v.minExclusive().length() > 0) {
            params.put("minExclusive", v.minExclusive());
        }
        if ( v.maxExclusive() != null && v.maxExclusive().length() > 0) {
            params.put("maxExclusive", v.maxExclusive());
        }

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processFieldExpressionValidatorAnnotation(FieldExpressionValidator v, String fieldName) {
        String validatorType = "fieldexpression";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        params.put("expression", v.expression());

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processEmailValidatorAnnotation(EmailValidator v, String fieldName) {
        String validatorType = "email";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processDateRangeFieldValidatorAnnotation(DateRangeFieldValidator v, String fieldName) {
        String validatorType = "date";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }
        if ( v.min() != null && v.min().length() > 0) {
             final Date minDate = parseDateString(v.min());
             params.put("min", minDate == null ? v.min() : minDate);
        }
        if ( v.max() != null && v.max().length() > 0) {
             final Date maxDate = parseDateString(v.max());
             params.put("max", maxDate == null ? v.max() : maxDate);
        }

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    private static ValidatorConfig processConversionErrorFieldValidatorAnnotation(ConversionErrorFieldValidator v, String fieldName) {
        String validatorType = "conversion";

        Map<String, Object> params = new HashMap<String, Object>();

        if (fieldName != null) {
            params.put("fieldName", fieldName);
        } else if (v.fieldName() != null && v.fieldName().length() > 0 ) {
            params.put("fieldName", v.fieldName());
        }

        ValidatorFactory.lookupRegisteredValidatorType(validatorType);
        ValidatorConfig vCfg = new ValidatorConfig(validatorType, params);
        vCfg.setShortCircuit(v.shortCircuit());
        vCfg.setDefaultMessage(v.message());

        String key = v.key();

        if ((key != null) && (key.trim().length() > 0)) {
            vCfg.setMessageKey(key);
        }

        return vCfg;
    }

    public static List<ValidatorConfig> buildAnnotationClassValidatorConfigs(Class aClass) {

        List<ValidatorConfig> result = new ArrayList<ValidatorConfig>();

        List<ValidatorConfig> temp = processAnnotations(aClass);
        if (temp != null) {
            result.addAll(temp);
        }

        Method[] methods = aClass.getDeclaredMethods();

        if ( methods != null ) {
            for (Method method : methods) {
                temp = processAnnotations(method);
                if (temp != null) {
                    result.addAll(temp);
                }
            }
        }

        return result;

    }

    /**
     * Returns the property name for a method.
     * This method is independant from property fields.
     *
     * @param method The method to get the property name for.
     * @return the property name for given method; null if non could be resolved.
     */
    public static String resolvePropertyName(Method method) {

        Matcher matcher = SETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 1) {
            String raw = matcher.group(1);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        matcher = GETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 0) {
            String raw = matcher.group(2);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        return null;
    }

}
