/**
 * 
 */
package com.oneid.rp;

import java.util.Map;

import javax.management.monitor.StringMonitor;

import net.sf.json.JSONObject;

/**
 * OneID Response result
 * 
 * valid - true: the request validated, false: the request did NOT validate
 * response - the server response if the request did not validate
 * result - any other keys resulting from the validation (used for linking accounts) 
 * 
 * @author jgoldberg
 *
 */
public class OneIDResponse {
	
	private JSONObject result;
	private boolean valid;
	private String response;
	
	public OneIDResponse(boolean valid, String response, JSONObject result) {
		this.valid = valid;
		this.result = result;
		this.response = response;
	}
	
	public JSONObject getResult() {
		return result;
	}
	public void setResult(JSONObject result) {
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
	
	/**
	 * Checks to see if the OneID auth payload has a device signature 
	 * 
	 * @param response - the OneID auth payload JSON
	 * @return
	 */
	public boolean hasADSignature() {
		if (!result.containsKey("nonces")) return false;
		return result.getJSONObject("nonces").containsKey("ad");
	}
	
	/**
	 * Checks to see if the OneID auth payload has a mobile device signature 
	 * 
	 * @param response - the OneID auth payload JSON
	 * @return
	 */
	public boolean hasCDSignature() {
		if (!result.containsKey("nonces")) return false;
		return result.getJSONObject("nonces").containsKey("cd");
	}
	
	/**
	 * Checks to see if the OneID auth payload has a OneID server signature 
	 * 
	 * @param response - the OneID auth payload JSON
	 * @return
	 */
	public boolean hasRepoSignature() {
		if (!result.containsKey("nonces")) return false;
		return result.getJSONObject("nonces").containsKey("repo");
	}
}
