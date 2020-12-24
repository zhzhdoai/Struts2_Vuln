/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor.annotations;

import java.lang.annotation.*;

/**
 * <!-- START SNIPPET: description -->
 * Marks a action method that needs to be executed before the main action method.
 * <!-- END SNIPPET: description -->
 *
 * <p/> <u>Annotation usage:</u>
 *
 * <!-- START SNIPPET: usage -->
 * The Before annotation can be applied at method level.
 *
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
 * <td class='confluenceTd'>priority</td>
 * <td class='confluenceTd'>no</td>
 * <td class='confluenceTd'>10</td>
 * <td class='confluenceTd'>Priority order of method execution</td>
 * </tr>
 * </table>
 * <!-- END SNIPPET: parameters -->
 *
 * <p/> <u>Example code:</u>
 *
 * <pre>
 * <!-- START SNIPPET: example -->
 * public class SampleAction extends ActionSupport {
 *
 *  &#64;Before
 *  public void isAuthorized() throws AuthenticationException {
 *    // authorize request, throw exception if failed
 *  }
 *
 *  public String execute() {
 *     // perform secure action
 *     return SUCCESS;
 *  }
 * }
 * <!-- END SNIPPET: example -->
 * </pre>
 *
 * @author Zsolt Szasz, zsolt at lorecraft dot com
 * @author Rainer Hermanns
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Before {
    int priority() default 10;
}
