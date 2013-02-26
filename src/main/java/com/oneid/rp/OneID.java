package com.oneid.rp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 
 * @author acer
 */
public class OneID {

	public static String oneidServers = "-dev";
	public static String oneidServer = "https://keychain" + oneidServers + ".oneid.com";
	public String oneidSscript = "<script src=\"https://api" + oneidServers + "oneid.com/js/includeexternal.js type=\"text/javascript\"></script>";
	public String oneidFormScript = "<script src=\"https://api" + oneidServers + ".oneid.com/form/form.js\" type=\"text/javascript\"></script>";

	public String oneidReferralCode = "yyy";
	public String oneidApiId = "";
	public String oneidApiKey = "";

	public OneID(String apiID, String apiKey) {
		oneidApiId = apiID;
		oneidApiKey = apiKey;
	}

	public JSONObject callOneID(String method, String post) throws IOException {
		String scope = "";
		System.out.println(oneidApiId + ":" + oneidApiKey);
		String encoding = Base64.encodeBase64URLSafeString(String.valueOf(oneidApiId + ":" + oneidApiKey).getBytes());
		String url = oneidServer + scope + "/keychain/" + method;
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(post));
		httpPost.addHeader("Authorization", "Basic " + encoding);
		HttpClient client = new DefaultHttpClient();

		HttpResponse response = client.execute(httpPost);
		String resultString = IOUtils.toString(response.getEntity().getContent());
		System.out.println("XXXXX");
		System.out.println(url);
		System.out.println(encoding);
		System.out.println("XXXXX");
		System.out.println(resultString);
		System.out.println("XXXXX");
		JSONObject result = (JSONObject) JSONSerializer.toJSON(resultString);
		return result;
	}

	public void oneIdSetCredentials(String apiId, String apiKey) {
		oneidApiId = apiId;
		oneidApiKey = apiKey;
	}

	private String jsonEncode(Map<String, String> jsonMap) {
		JSONObject json = new JSONObject();
		json.accumulateAll(jsonMap);
		return json.toString();
	}

	public String createSignInButton(String callback) throws OneIDException {
		return "<img class=\"oneidlogin\" id=\"oneidlogin\" data-challenge='{\"nonce\":\"" + makeNonce() + "\",\"attr\":\"personal_info[email]\",\"callback\":\"" + callback + "\"}' src=\"https://api" + oneidServers + ".oneid.com/images/oneid_signin.png\" onclick=\"OneId.login()\">";
	}

	public String createFormFillButton() {
		return "<img class=\"oneidlogin\" id=\"oneidlogin\" src=\"https://api" + oneidServers + ".oneid.com/api/images/btn_id_signin.gif\" onclick=\"OneId.login()\">";
	}

	public String redirect(String page, JSONObject response) {
		return ("{\"error\":\"" + response.getString("error") + "\",\"errorcode\":\"" + response.getString("errorcode") + "\",\"url\":\"" + page + "\",\"response\":" + response.toString() + "}");
	}

	public String createProvisionButton(String emailAddress, Map<String, String> attrs) {

		return "<img id=\"getAOneIdButton\" class=\"oneidlogin\"ref=" + oneidReferralCode + "src=\"https://api" + oneidServers + "oneid.com/images/oneid_signin.png\" data-userattrs = '" + jsonEncode(attrs) + " 'onclick=\"OneId.createOneId()\" >";
	}

	private boolean checkSuccess(JSONObject response) {
		return "success".equals(response.getString("error")) && 0 == response.getInt("errorcode");
	}

	public String makeNonce() throws OneIDException {
		try {
			JSONObject json = callOneID("make_nonce", null);
			return json.getString("nonce");
		} catch (IOException e) {
			throw new OneIDException("HTTP call to make_nonce failed: " + e.toString(), -1);
		}
	}

	public Map<String, Object> validateResponse(String inputString) throws OneIDException {

		try {
			JSONObject validate = callOneID("validate", inputString);
			if (!checkSuccess(validate)) {
				throw new OneIDException(validate.getString("error"), validate.getInt("errorcode"));
			}
	
			JSONObject inputJSON = (JSONObject) JSONSerializer.toJSON(inputString);
			Map<String, Object> result = new HashMap<String, Object>();
			for (String key : result.keySet()) {
				result.put(key, inputJSON.get(key));
			}
			return result;
		} catch (IOException e) {
			throw new OneIDException("HTTP call to validate failed: " + e.toString(), -1);
		}
	}
	
	public static void main (String [] args) throws Exception {
		OneID oneid = new OneID("f7eea656-46c8-478d-8fb0-e2e9d005d045","nj/uZhJgrk61/deO7fzXXA==");
		oneid.validateResponse("{}");
	}
}
