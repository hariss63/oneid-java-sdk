package com.oneid.rp.twofactor;

import java.io.IOException;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.oneid.rp.OneIDClient;
import com.oneid.rp.OneIDResponse;

/**
 * Hello world!
 * 
 */
public class OneID2FClient extends OneIDClient {

	private static final String ONEID_HOST = "https://account.oneid.com/repo/send_2fa";

	/**
	 * Constructor - API Credentials are required
	 * 
	 * Can be retrieved from https://keychain.oneid.com/register
	 * 
	 * @param apiKey
	 * @param apiID
	 */
	public OneID2FClient(String apiKey, String apiID) {
		super(apiKey, apiID);
	}
	
	/**
	 * Trigger a second factor request using the OneID Remote App.
	 * 
	 * @param twoFactorToken
	 *            - the two_factor_token value from a user's authentication
	 *            payload during linking
	 * @param uid
	 *            - the uid value from a user's authentication payload during
	 *            linking
	 * @param title
	 *            - Title for the second factor request
	 * @param message
	 *            - Message for the second factor request
	 * @return
	 * @throws IOException
	 *             - throws an IOException if an HTTP connection can't be opened
	 */
	public OneIDResponse send(String twoFactorToken, String uid, String title, String message) throws IOException {
		return send(twoFactorToken, nonce(), uid, title, message);
	}

	/**
	 * Trigger a second factor request using the OneID Remote App.
	 * 
	 * @param twoFactorToken
	 *            - the two_factor_token value from a user's authentication
	 *            payload during linking
	 * @param nonce
	 *            - the nonce which gets signed during the authentication transaction 
	 * @param uid
	 *            - the uid value from a user's authentication payload during
	 *            linking
	 * @param title
	 *            - Title for the second factor request
	 * @param message
	 *            - Message for the second factor request
	 * @return
	 * @throws IOException
	 *             - throws an IOException if an HTTP connection can't be opened
	 */
	public OneIDResponse send(String twoFactorToken, String nonce, String uid, String title, String message) throws IOException {
		JSONObject obj = new JSONObject();
		obj.put("title", title);
		obj.put("message", message);
		obj.put("nonce", nonce);
		obj.put("two_factor_token", twoFactorToken);
		String input = obj.toString();

		String basic = super.apiID + ":" + super.apiKey;
		String encoding = Base64.encodeBase64String(basic.getBytes());
		HttpPost httpPost = new HttpPost(ONEID_HOST);
		httpPost.setEntity(new StringEntity(input));
		httpPost.addHeader("Authorization", "Basic " + encoding);
		HttpClient client = new DefaultHttpClient();

		HttpResponse response = client.execute(httpPost);
		String payload = IOUtils.toString(response.getEntity().getContent());

		JSONObject payloadJSON = (JSONObject) JSONSerializer.toJSON(payload);

		if (!super.isValidated(payloadJSON))
			return new OneIDResponse(false, payloadJSON.getString("error"), null);

		payloadJSON.put("uid", uid);

		return super.validate(payloadJSON.toString());
	}

	public static void main(String[] args) throws Exception {
		OneID2FClient client = new OneID2FClient("PDlKwQNiKLYfYOxyfD+RoQ==", "43d4b408-f1b8-4212-a303-52c86cbec8a2");
		OneIDResponse r = client.send("5anR6Zila+GWWYikXF4Uhw==", "KaTUyUUWoAn7ajRmWKjIFw==", "Test Tile", "Test Message");
		System.out.println(r.hasADSignature());
		System.out.println(r.hasCDSignature());
		System.out.println(r.hasRepoSignature());
	}

}
