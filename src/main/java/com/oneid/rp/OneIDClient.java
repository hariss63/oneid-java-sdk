/**
 * 
 */
package com.oneid.rp;

import java.io.IOException;
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

/**
 * @author jgoldberg
 * 
 */
public class OneIDClient {

	private String apiID;
	private String apiKey;

	private static final String ONEID_HOST = "https://keychain-stage.oneid.com/";

	public OneIDClient(String apiKey, String apiID) {
		this.apiID = apiID;
		this.apiKey = apiKey;
	}

	public JSONObject open(String method) throws IOException {
		return open(method, null);
	}

	public JSONObject open(String method, String post) throws IOException {
		String basic = apiID + ":" + apiKey;
		String encoding = Base64.encodeBase64String(basic.getBytes());
		HttpPost httpPost = new HttpPost(ONEID_HOST + method);
		httpPost.setEntity(new StringEntity(post));
		httpPost.addHeader("Authorization", "Basic " + encoding);
		HttpClient client = new DefaultHttpClient();

		HttpResponse response = client.execute(httpPost);
		String resultString = IOUtils.toString(response.getEntity().getContent());
		JSONObject result = (JSONObject) JSONSerializer.toJSON(resultString);
		return result;
	}

	public boolean isValidated(JSONObject response) {
		return "success".equals(response.getString("error")) && 0 == response.getInt("errorcode");
	}

	public String nonce() throws IOException {
		JSONObject json = open("make_nonce");
		return json.getString("nonce");
	}

	@SuppressWarnings("unchecked")
	public OneIDResponse validate(String payload) throws IOException {
		JSONObject validate = open("validate", payload);
		if (!isValidated(validate))
			return new OneIDResponse(false, validate.getString("error"), null);

		JSONObject inputJSON = (JSONObject) JSONSerializer.toJSON(payload);
		Map<String, Object> result = (Map<String, Object>) JSONObject.toBean(inputJSON, Map.class);

		return new OneIDResponse(true, null, result);
	}
}
