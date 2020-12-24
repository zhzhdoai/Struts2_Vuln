/*
 * Copyright (c) 2002-2006 by OpenSymphony
 * All rights reserved.
 */
package com.opensymphony.xwork2;

/**
 * <code>InvalidMetadataException</code>
 *
 * @author Rainer Hermanns
 * @version $Id: InvalidMetadataException.java 1187 2006-11-13 08:05:32Z mrdon $
 */
public class InvalidMetadataException extends RuntimeException {

    /**
	 * Create a new <code>InvalidMetadataException</code> with the supplied error message.
     * 
	 * @param msg the error message
	 */
	public InvalidMetadataException(String msg) {
		super(msg);
	}
}
