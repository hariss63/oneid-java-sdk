/**
 * 
 */
package com.oneid.rp;

/**
 * @author jgoldberg
 *
 */
public class OneIDException extends Exception {
	
	private static final long serialVersionUID = 5480231393822414418L;
	
	private int errorcode;
	private String error;
	
	public OneIDException(String error, int errorcode) {
		super(error);
		this.errorcode = errorcode;
		this.error = error;
	}

	@Override
	public String toString() {
		return "OneID Exception (" + errorcode + "): " + error;
	}
	
}
