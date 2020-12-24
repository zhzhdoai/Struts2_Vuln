/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.interceptor;


/**
* This marker interface should be implemented by actions that do not want any
* parameters set on them automatically (by the ParametersActionFactoryProxy)
* This may be useful if one is using the action tag and want to supply
* the parameters to the action manually using the param tag.
* It may also be useful if one for security reasons wants to make sure that
* parameters cannot be set by malicious users.
*
* @author        Dick Zetterberg (dick@transitor.se)
* @version        $Revision: 1063 $, $Date: 2006-07-10 02:30:29 +0200 (Mo, 10 Jul 2006) $
*/
public interface NoParameters {
}
