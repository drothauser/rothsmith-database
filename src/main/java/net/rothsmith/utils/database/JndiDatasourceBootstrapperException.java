/*
 * (c) 2012 Rothsmith, LLC All Rights Reserved.
 */
package net.rothsmith.utils.database;

/**
 * Exception for JNDI datasource bootstrapping errors.
 * 
 * @author tgrewe
 * 
 */
public class JndiDatasourceBootstrapperException
        extends Exception {

	/**
	 * Serialized version uid
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Holds the exception error information
	 */
	private String mistake;

	/**
	 * Default constructor - initializes instance variable to unknown.
	 * 
	 */
	public JndiDatasourceBootstrapperException() {
		super(); // call superclass constructor
		mistake = "unknown- Typical UsageException is "
		    + "wrong number of arguments passed in";
	}

	/**
	 * Constructor receives some kind of message that is saved in an instance
	 * variable.
	 * 
	 * @param err
	 *            {@link java.lang.String}
	 * 
	 */
	public JndiDatasourceBootstrapperException(String err) {
		super(err); // call super class constructor
		mistake = err; // save message
	}

	/**
	 * public method, callable by exception catcher. It returns the error
	 * message.
	 * 
	 * @return String {@link java.lang.String}
	 * 
	 */
	public String getError() {
		return mistake;
	}

}
