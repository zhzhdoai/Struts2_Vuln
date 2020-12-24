/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.validator;


/**
 * <!-- START SNIPPET: validatorFlavours -->
 * <p>The validators supplied by the Xwork distribution (and any validators you 
 * might write yourself) come in two different flavors:</p>
 *
 * <ol>
 *  <li> Plain Validators / Non-Field validators </li>
 *  <li> FieldValidators </li>
 * </ol>
 *
 * <p>Plain Validators (such as the ExpressionValidator) perform validation checks 
 * that are not inherently tied to a single specified field. When you declare a 
 * plain Validator in your -validation.xml file you do not associate a fieldname 
 * attribute with it. (You should avoid using plain Validators within the 
 * <field-validator> syntax described below.)</p>
 *
 * <p>FieldValidators (such as the EmailValidator) are designed to perform 
 * validation checks on a single field. They require that you specify a fieldname 
 * attribute in your -validation.xml file. There are two different (but equivalent) 
 * XML syntaxes you can use to declare FieldValidators (see "<validator> vs. 
 * <field-Validator> syntax" below).</p>
 *
 * <p>There are two places where the differences between the two validator flavors 
 * are important to keep in mind:</p>
 *
 * <ol>
 *   <li> when choosing the xml syntax used for declaring a validator 
 *       (either <validator> or <field-validator>)</li>
 *   <li> when using the short-circuit capability</li>
 * </ol>
 *
 * <p><b>NOTE:</b>Note that you do not declare what "flavor" of validator you are 
 * using in your -validation.xml file, you just declare the name of the validator 
 * to use and WebWork will know whether it's a "plain Validator" or a "FieldValidator" 
 * by looking at the validation class that the validator's programmer chose 
 * to implement.</p>
 * <!-- END SNIPPET: validatorFlavours -->
 * 
 * 
 * 
 * 
 * <!-- START SNIPPET: validationRules -->
 * <p>To define validation rules for an Action, create a file named ActionName-validation.xml 
 * in the same package as the Action. You may also create alias-specific validation rules which 
 * add to the default validation rules defined in ActionName-validation.xml by creating 
 * another file in the same directory named ActionName-aliasName-validation.xml. In both 
 * cases, ActionName is the name of the Action class, and aliasName is the name of the 
 * Action alias defined in the xwork.xml configuration for the Action.</p>
 * 
 * <p>The framework will also search up the inheritance tree of the Action to 
 * find validation rules for directly implemented interfaces and parent classes of the Action. 
 * This is particularly powerful when combined with ModelDriven Actions and the VisitorFieldValidator. 
 * Here's an example of how validation rules are discovered. Given the following class structure:</p>
 * 
 * <ul>
 *   <li>interface Animal;</li>
 *   <li>interface Quadraped extends Animal;</li>
 *   <li>class AnimalImpl implements Animal;</li>
 *   <li>class QuadrapedImpl extends AnimalImpl implements Quadraped;</li>
 *   <li>class Dog extends QuadrapedImpl;</li>
 * </ul>
 * 
 * <p>The framework method will look for the following config files if Dog is to be validated:</p>
 *
 * <ul>
 *   <li>Animal</li>
 *   <li>Animal-aliasname</li>
 *   <li>AnimalImpl</li>
 * 	 <li>AnimalImpl-aliasname</li>
 *   <li>Quadraped</li>
 *   <li>Quadraped-aliasname</li>
 *   <li>QuadrapedImpl</li>
 *   <li>QuadrapedImpl-aliasname</li>
 *   <li>Dog</li>
 *   <li>Dog-aliasname</li>
 * </ul>
 *
 * <p>While this process is similar to what the XW:Localization framework does 
 * when finding messages, there are some subtle differences. The most important 
 * difference is that validation rules are discovered from the parent downwards.
 * </p>
 * 
 * <p><b>NOTE:</b>Child's *-validation.xml will add on to parent's *-validation.xml 
 * according to the class hierarchi defined above. With this feature, one could have
 * more generic validation rule at the parent and more specific validation rule at
 * the child.</p>
 * 
 * <!-- END SNIPPET: validationRules -->
 * 
 * 
 * <!-- START SNIPPET: validatorVsFieldValidators1 -->
 * <p>There are two ways you can define validators in your -validation.xml file:</p>
 * <ol>
 *  <li> &lt;validator&gt; </li>
 *  <li> &lt;field-validator&gt; </li>
 * </ol>
 * <p>Keep the following in mind when using either syntax:</p>
 * 
 * <p><b>Non-Field-Validator</b>
 * The &lt;validator&gt; element allows you to declare both types of validators 
 * (either a plain Validator a field-specific FieldValidator).</p>
 * <!-- END SNIPPET: validatorVsFieldValidators1 -->
 *
 *<pre>
 * <!-- START SNIPPET: nonFieldValidatorUsingValidatorSyntax -->
 *    &lt;!-- Declaring a plain Validator using the &lt;validator&gt; syntax: --&gt;
 *
 *    &lt;validator type="expression&gt;
 *          &lt;param name="expression">foo gt bar&lt;/param&gt;
 *          &lt;message&gt;foo must be great than bar.&lt;/message&gt;
 *    &lt;/validator&gt;
 * <!-- END SNIPPET: nonFieldValidatorUsingValidatorSyntax -->
 * </pre>
 * 
 * <pre>
 * <!-- START SNIPPET: fieldValidatorUsingValidatorSyntax -->
 *    &lt;!-- Declaring a field validator using the &lt;validator&gt; syntax; --&gt;
 *
 *    &lt;validator type="required"&gt;
 *         &lt;param name="fieldName"&gt;bar&lt;/param&gt;
 *         &lt;message&gt;You must enter a value for bar.&lt;/message&gt;
 *    &lt/validator&gt;
 * <!-- END SNIPPET: fieldValidatorUsingValidatorSyntax -->
 * </pre>
 *
 *
 * <!-- START SNIPPET: validatorVsFieldValidators2 -->
 * <p><b>field-validator</b>
 * The &lt;field-validator&gt; elements are basically the same as the &lt;validator&gt; elements 
 * except that they inherit the fieldName attribute from the enclosing &lt;field&gt; element. 
 * FieldValidators defined within a &lt;field-validator&gt; element will have their fieldName 
 * automatically filled with the value of the parent &lt;field&gt; element's fieldName 
 * attribute. The reason for this structure is to conveniently group the validators 
 * for a particular field under one element, otherwise the fieldName attribute 
 * would have to be repeated, over and over, for each individual &lt;validator&gt;.</p>
 * 
 * <p><b>HINT:</b>
 * It is always better to defined field-validator inside a &lt;field&gt; tag instead of 
 * using a &lt;validator&gt; tag and supplying fieldName as its param as the xml code itself 
 * is clearer (grouping of field is clearer)</p>
 * 
 * <p><b>NOTE:</b>
 * Note that you should only use FieldValidators (not plain Validators) within a 
 * <field-validator> block. A plain Validator inside a &lt;field&gt; will not be 
 * allowed and would generate error when parsing the xml, as it is not allowed in 
 * the defined dtd (xwork-validator-1.0.2.dtd)</p>
 * <!-- END SNIPPET: validatorVsFieldValidators2 -->
 *
 * <pre>
 * <!-- START SNIPPET: fieldValidatorUsingFieldValidatorSyntax -->
 * Declaring a FieldValidator using the &lt;field-validator&gt; syntax:
 * 
 * &lt;field name="email_address"&gt;
 *   &lt;field-validator type="required"&gt;
 *       &lt;message&gt;You cannot leave the email address field empty.&lt;/message&gt;
 *   &lt;/field-validator&gt;
 *   &lt;field-validator type="email"&gt;
 *       &lt;message&gt;The email address you entered is not valid.&lt;/message&gt;
 *   &lt;/field-validator&gt;
 * &lt;/field&gt;
 * <!-- END SNIPPET: fieldValidatorUsingFieldValidatorSyntax -->
 * </pre>
 * 
 * 
 * <!-- START SNIPPET: validatorVsFieldValidators3 -->
 * <p>The choice is yours. It's perfectly legal to only use <validator> elements 
 * without the <field> elements and set the fieldName attribute for each of them. 
 * The following are effectively equal:</P>
 * <!-- END SNIPPET: validatorVsFieldValidators3 -->
 * 
 * <pre>
 * <!-- START-SNIPPET: similarVaidatorDeclaredInDiffSyntax -->
 * &lt;field name="email_address"&gt;
 *   &lt;field-validator type="required"&gt;
 *       &lt;message&gt;You cannot leave the email address field empty.&lt;/message&gt;
 *   &lt;/field-validator&gt;
 *   &lt;field-validator type="email"&gt;
 *       &lt;message&gt;The email address you entered is not valid.&lt;/message&gt;
 *   &lt;/field-validator&gt;
 * &lt;/field&gt;
 *
 *
 * &lt;validator type="required"&gt;
 *   &lt;param name="fieldName"&gt;email_address&lt;/param&gt;
 *   &lt;message&gt;You cannot leave the email address field empty.&lt;/message&gt;
 * &lt;/validator&gt;
 * &lt;validator type="email"&gt;
 *   &lt;param name="fieldName"&gt;email_address&lt;/param&gt;
 *   &lt;message&gt;The email address you entered is not valid.&lt;/message&gt;
 * &lt;/validator&gt;
 * <!-- END SNIPPET: similarVaidatorDeclaredInDiffSyntax -->
 * </pre>
 *
 *
 * <!-- START SNIPPET: shortCircuitingValidators1 -->
 * <p>Beginning with XWork 1.0.1 (bundled with WebWork 2.1), it is possible 
 * to short-circuit a stack of validators. Here is another sample config file 
 * containing validation rules from the Xwork test cases: Notice that some of the 
 * &lt;field-validator&gt; and &lt;validator&gt; elements have the short-circuit 
 * attribute set to true.</p>
 * <!-- END SNIPPET : shortCircuitingValidators1 -->
 *
 *<pre>
 * &lt;!-- START SNIPPET: exShortCircuitingValidators --&gt;
 * &lt;!DOCTYPE validators PUBLIC 
 *         "-//OpenSymphony Group//XWork Validator 1.0.2//EN" 
 *         "http://www.opensymphony.com/xwork/xwork-validator-1.0.2.dtd"&gt;
 * &lt;validators&gt;
 *   &lt;!-- Field Validators for email field --&gt;
 *   &lt;field name="email"&gt;
 *       &lt;field-validator type="required" short-circuit="true"&gt;
 *           &lt;message&gt;You must enter a value for email.&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *       &lt;field-validator type="email" short-circuit="true"&gt;
 *           &lt;message&gt;Not a valid e-mail.&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   &lt;!-- Field Validators for email2 field --&gt;
 *   &lt;field name="email2"&gt;
 *      &lt;field-validator type="required"&gt;
 *           &lt;message&gt;You must enter a value for email2.&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *      &lt;field-validator type="email"&gt;
 *           &lt;message&gt;Not a valid e-mail2.&lt;/message&gt;
 *       &lt;/field-validator&gt;
 *   &lt;/field&gt;
 *   &lt;!-- Plain Validator 1 --&gt;
 *   &lt;validator type="expression"&gt;
 *       &lt;param name="expression"&gt;email.equals(email2)&lt;/param&gt;
 *       &lt;message&gt;Email not the same as email2&lt;/message&gt;
 *   &lt;/validator&gt;
 *   &lt;!-- Plain Validator 2 --&gt;
 *   &lt;validator type="expression" short-circuit="true"&gt;
 *       &lt;param name="expression"&gt;email.startsWith('mark')&lt;/param&gt;
 *       &lt;message&gt;Email does not start with mark&lt;/message&gt;
 *   &lt;/validator&gt;
 * &lt;/validators&gt;
 * &lt;!-- END SNIPPET: exShortCircuitingValidators --&gt;
 *</pre>
 *
 * <!-- START SNIPPET:shortCircuitingValidators2  -->
 * <p><b>short-circuiting and Validator flavors</b></p>
 * <p>Plain validator takes precedence over field-validator. They get validated 
 * first in the order they are defined and then the field-validator in the order 
 * they are defined. Failure of a particular validator marked as short-circuit 
 * will prevent the evaluation of subsequent validators and an error (action 
 * error or field error depending on the type of validator) will be added to 
 * the ValidationContext of the object being validated.</p>
 *
 * <p>In the example above, the actual execution of validator would be as follows:</p>
 * 
 * <ol>
 *  <li> Plain Validator 1</li>
 *  <li> Plain Validator 2</li>
 *  <li> Field Validators for email field</li>
 *  <li> Field Validators for email2 field</li>
 * </ol>
 *
 * <p>Since Field Validator 2 is short-circuited, if its validation failed, 
 * it will causes Field validators for email field and Field validators for email2 
 * field to not be validated as well.</p>
 * 
 * <p><b>Usefull Information:</b>
 * More complecated validation should probably be done in the validate() 
 * method on the action itself (assuming the action implements Validatable 
 * interface which ActionSupport already does).</p>
 * 
 * <p>
 * A plain Validator (non FieldValidator) that gets short-circuited will
 * completely break out of the validation stack no other validators will be
 * evaluated and plain validator takes precedence over field validator meaning 
 * that they get evaluated in the order they are defined before field validator 
 * gets a chance to be evaludated again according to their order defined.
 * </p>
 * <!-- END SNIPPET: shortCircuitingValidators2 -->
 * 
 * 
 * <!-- START SNIPPET: scAndValidatorFlavours1 -->
 * <p><b>Short cuircuiting and validator flavours</b></p>
 * <p>A FieldValidator that gets short-circuited will only prevent other 
 * FieldValidators for the same field from being evaluated. Note that this 
 * "same field" behavior applies regardless of whether the <validator> or 
 * <field-validator> syntax was used to declare the validation rule. 
 * By way of example, given this -validation.xml file:</p>
 * <!-- END SNIPPET: scAndValidatorFlavours1 -->
 * 
 * <pre>
 * <!-- START SNIPPET: exScAndValidatorFlavours -->
 * &lt;validator type="required" short-circuit="true"&gt;
 *   &lt;param name="fieldName"&gt;bar&lt;/param&gt;
 *   &lt;message&gt;You must enter a value for bar.&lt;/message&gt;
 * &lt;/validator&gt;
 *
 * &lt;validator type="expression"&gt;
 *   &lt;param name="expression">foo gt bar&lt;/param&gt;
 *   &lt;message&gt;foo must be great than bar.&lt;/message&gt;
 * &lt;/validator&gt;
 * <!-- END SNIPPET: exScAndValidatorFlavours -->
 * </pre>
 * 
 * <!-- START SNIPPET: scAndValidatorFlavours2 -->
 * <p>both validators will be run, even if the "required" validator short-circuits.
 * "required" validators are FieldValidator's and will not short-circuit the plain 
 * ExpressionValidator because FieldValidators only short-circuit other checks on 
 * that same field. Since the plain Validator is not field specific, it is 
 * not short-circuited.</p>
 * <!-- END SNIPPET: scAndValidatorFlavours2 -->
 * 
 * 
 * <!-- START SNIPPET: howXworkFindsValidatorForAction -->
 * <p>As mentioned above, the framework will also search up the inheritance tree 
 * of the action to find default validations for interfaces and parent classes of 
 * the Action. If you are using the short-circuit attribute and relying on 
 * default validators higher up in the inheritance tree, make sure you don't 
 * accidentally short-circuit things higher in the tree that you really want!</p>
 * <!-- END SNIPPET: howXworkFindsValidatorForAction -->
 * 
 *
 * @author Jason Carreira
 */
public interface Validator {

    void setDefaultMessage(String message);

    String getDefaultMessage();

    String getMessage(Object object);

    void setMessageKey(String key);

    String getMessageKey();

    /**
     * This method will be called before validate with a non-null ValidatorContext.
     *
     * @param validatorContext  the validation context to use.
     */
    void setValidatorContext(ValidatorContext validatorContext);

    ValidatorContext getValidatorContext();

    /**
     * The validation implementation must guarantee that setValidatorContext will
     * be called with a non-null ValidatorContext before validate is called.
     *
     * @param object  the object to be validated.
     * @throws ValidationException is thrown if there is validation error(s).
     */
    void validate(Object object) throws ValidationException;

    /**
     * Sets the validator type to use (see class javadoc).
     *
     * @param type  the type to use.
     */
    void setValidatorType(String type);

    String getValidatorType();
    
}
