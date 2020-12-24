/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.validator.annotations;

import java.lang.annotation.*;

/**
 * <!-- START SNIPPET: description -->
 * If you want to use annotation based validation, you have to annotate the class or interface with Validation Annotation.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 *  <p/>The Validation annotation must be applied at Type level.
 * <!-- END SNIPPET: usage -->
 *
 * <p/> <u>Annotation parameters:</u>
 *
 * <!-- START SNIPPET: parameters -->
 * <table class='confluenceTable'>
 * <tr>
 * <th class='confluenceTh'> Parameter </th>
 * <th class='confluenceTh'> Required </th>
 * <th class='confluenceTh'> Default </th>
 * <th class='confluenceTh'> Notes </th>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>message</td>
 * <td class='confluenceTd'>yes</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>field error message</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>key</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>i18n key from language specific properties file.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>fieldName</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * <td class='confluenceTd'>&nbsp;</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>shortCircuit</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>false</td>
 * <td class='confluenceTd'>If this validator should be used as shortCircuit.</td>
 * </tr>
 * <tr>
 * <td class='confluenceTd'>type</td>
 * <td class='confluenceTd'>yes</td>
 * <td class='confluenceTd'>ValidatorType.FIELD</td>
 * <td class='confluenceTd'>Enum value from ValidatorType. Either FIELD or SIMPLE can be used here.</td>
 * </tr>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <u>An Annotated Interface</u>
 * <pre>
 * <!-- START SNIPPET: example -->
 * &#64;Validation()
 * public interface AnnotationDataAware {
 *
 *     void setBarObj(Bar b);
 *
 *     Bar getBarObj();
 *
 *     &#64;RequiredFieldValidator(message = "You must enter a value for data.")
 *     &#64;RequiredStringValidator(message = "You must enter a value for data.")
 *     void setData(String data);
 *
 *     String getData();
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * <p/> <u>Example code:</u>
 *
 * <u>An Annotated Class</u>
 * <pre>
 * <!-- START SNIPPET: example2 -->
 * &#64;Validation()
 * public class SimpleAnnotationAction extends ActionSupport {
 *
 *     &#64;RequiredFieldValidator(type = ValidatorType.FIELD, message = "You must enter a value for bar.")
 *     &#64;IntRangeFieldValidator(type = ValidatorType.FIELD, min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")
 *     public void setBar(int bar) {
 *         this.bar = bar;
 *     }
 *
 *     public int getBar() {
 *         return bar;
 *     }
 *
 *     &#64;Validations(
 *             requiredFields =
 *                     {&#64;RequiredFieldValidator(type = ValidatorType.SIMPLE, fieldName = "customfield", message = "You must enter a value for field.")},
 *             requiredStrings =
 *                     {&#64;RequiredStringValidator(type = ValidatorType.SIMPLE, fieldName = "stringisrequired", message = "You must enter a value for string.")},
 *             emails =
 *                     { &#64;EmailValidator(type = ValidatorType.SIMPLE, fieldName = "emailaddress", message = "You must enter a value for email.")},
 *             urls =
 *                     { &#64;UrlValidator(type = ValidatorType.SIMPLE, fieldName = "hreflocation", message = "You must enter a value for email.")},
 *             stringLengthFields =
 *                     {&#64;StringLengthFieldValidator(type = ValidatorType.SIMPLE, trim = true, minLength="10" , maxLength = "12", fieldName = "needstringlength", message = "You must enter a stringlength.")},
 *             intRangeFields =
 *                     { @IntRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "intfield", min = "6", max = "10", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
 *             dateRangeFields =
 *                     {&#64;DateRangeFieldValidator(type = ValidatorType.SIMPLE, fieldName = "datefield", min = "-1", max = "99", message = "bar must be between ${min} and ${max}, current value is ${bar}.")},
 *             expressions = {
 *                 &#64;ExpressionValidator(expression = "foo &gt; 1", message = "Foo must be greater than Bar 1. Foo = ${foo}, Bar = ${bar}."),
 *                 &#64;ExpressionValidator(expression = "foo &gt; 2", message = "Foo must be greater than Bar 2. Foo = ${foo}, Bar = ${bar}."),
 *                 &#64;ExpressionValidator(expression = "foo &gt; 3", message = "Foo must be greater than Bar 3. Foo = ${foo}, Bar = ${bar}."),
 *                 &#64;ExpressionValidator(expression = "foo &gt; 4", message = "Foo must be greater than Bar 4. Foo = ${foo}, Bar = ${bar}."),
 *                 &#64;ExpressionValidator(expression = "foo &gt; 5", message = "Foo must be greater than Bar 5. Foo = ${foo}, Bar = ${bar}.")
 *     }
 *     )
 *     public String execute() throws Exception {
 *         return SUCCESS;
 *     }
 * }
 *
 * <!-- END SNIPPET: example2 -->
 * </pre>
 *
 * @author Rainer Hermanns
 * @version $Id: Validation.java 1187 2006-11-13 08:05:32Z mrdon $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Validation {

    /**
     * Used for class or interface validation rules.
     */
    Validations[] validations() default {};
}
