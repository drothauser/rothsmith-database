/*
 * (c) 2011 Rothsmith, LLC All Rights Reserved.
 */
package com.rothsmith.utils.database;

/**
 * 
 * Exception for {@link DatasourceParser} errors. Errors occurring in the
 * DatasourceParser are unrecoverable and hence will be treated as
 * RuntimeExceptions.
 * 
 * @author drothauser
 * 
 */
public class DatasourceParserException
        extends RuntimeException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            error message
	 */
	public DatasourceParserException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            error message
	 * @param cause
	 *            {@link Throwable} that caused this exception
	 */
	public DatasourceParserException(String message, Throwable cause) {
		super(message, cause);
	}

}
