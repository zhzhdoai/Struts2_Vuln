/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

import java.io.Serializable;


/**
 * All results (except for NONE) of an Action are mapped to a View implementation.
 * Examples of Views might be:
 * <ul>
 * <li>SwingPanelView - pops up a new Swing panel</li>
 * <li>ActionChainView - executes another action</li>
 * <li>SerlvetRedirectView - redirects the HTTP response to a URL</li>
 * <li>ServletDispatcherView - dispatches the HTTP response to a URL</li>
 * </ul>
 *
 * @author plightbo
 * @version $Revision: 1063 $
 */
public interface Result extends Serializable {

    /**
     * Represents a generic interface for all action execution results, whether that be displaying a webpage, generating
     * an email, sending a JMS message, etc.
     */
    public void execute(ActionInvocation invocation) throws Exception;
}
