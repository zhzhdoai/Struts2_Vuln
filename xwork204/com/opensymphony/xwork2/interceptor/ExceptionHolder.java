/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */

package com.opensymphony.xwork2.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * <!-- START SNIPPET: javadoc -->
 *
 * A simple wrapper around an exception, providing an easy way to print out the stack trace of the exception as well as
 * a way to get a handle on the exception itself.
 *
 * <!-- END SNIPPET: javadoc -->
 *
 * @author Matthew E. Porter (matthew dot porter at metissian dot com) Date: Sep 21, 2005 Time: 3:09:12 PM
 */
public class ExceptionHolder {
    private Exception exception;

    public ExceptionHolder(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return this.exception;
    }

    public String getExceptionStack() throws IOException {
        String exceptionStack = null;

        if (getException() != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            try {
                getException().printStackTrace(pw);
                exceptionStack = sw.toString();
            }
            finally {
                sw.close();
                pw.close();
            }
        }

        return exceptionStack;
    }
}
