/**
 * 
 */
package com.oneid.rp;

import java.util.Map;

/**
 * @author jgoldberg
 *
 */
public class OneIDResponse {
	
	private Map<String, Object> result;
	private boolean valid;
	private String response;
	
	public OneIDResponse(boolean valid, String response, Map<String, Object> result) {
		this.valid = valid;
		this.result = result;
		this.response = response;
	}
	
	public Map<String, Object> getResult() {
		return result;
	}
	public void setResult(Map<String, Object> result) {
		this.result = result;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
}
