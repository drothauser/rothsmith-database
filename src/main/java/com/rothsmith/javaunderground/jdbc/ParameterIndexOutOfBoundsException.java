/**
 * Title:
 * <p>
 * Description:
 * <p>
 * Copyright: Copyright (c) Troy Thompson, Bob Byron
 * <p>
 * Company: JavaUnderground
 * <p>
 * 
 * @author Troy Thompson, Bob Byron
 * @version 1.1
 */
package com.rothsmith.javaunderground.jdbc;

import java.sql.SQLException;

/**
 * ParameterIndexOutOfBoundsException.
 * 
 * @author Troy Thompson, Bob Byron
 * 
 */
public class ParameterIndexOutOfBoundsException
        extends SQLException {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default ParameterIndexOutOfBoundsException.
	 */
	public ParameterIndexOutOfBoundsException() {
		super();
	}

	/**
	 * ParameterIndexOutOfBoundsException with an error message.
	 * 
	 * @param msg
	 *            error message
	 */
	public ParameterIndexOutOfBoundsException(final String msg) {
		super(msg);
	}
}