/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2.config.entities;

import java.util.List;

/**
 * InterceptorListHolder
 *
 * @author Jason Carreira
 *         Created Jun 1, 2003 1:02:48 AM
 */
public interface InterceptorListHolder {

    void addInterceptor(InterceptorMapping interceptor);

    void addInterceptors(List<InterceptorMapping> interceptors);
}
