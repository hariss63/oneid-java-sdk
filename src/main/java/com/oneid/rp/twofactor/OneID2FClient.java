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
public class OneID2FClient 
{
    private String apiID;
    private String apiKey;
    
    private OneIDClient oneIDClient = null;
    
    private static final String ONEID_HOST = "https://account-stage.oneid.com/repo/send_2fa";
    
    public OneID2FClient(String apiKey, String apiID) {
    	this.apiID = apiID;
    	this.apiKey = apiKey;
    	
    	oneIDClient = new OneIDClient(apiKey, apiID);
    }
    
    public OneIDResponse send(String twoFactorToken, String uid, String title, String message) throws IOException {
	    	JSONObject obj = new JSONObject();
	    	obj.put("title", title);
	    	obj.put("message", message);
	    	obj.put("two_factor_token", twoFactorToken);
	    	String input = obj.toString();
	    	
	    	String basic = apiID + ":" + apiKey;
			String encoding = Base64.encodeBase64String(basic.getBytes());
			HttpPost httpPost = new HttpPost(ONEID_HOST);
			httpPost.setEntity(new StringEntity(input));
			httpPost.addHeader("Authorization", "Basic " + encoding);
			HttpClient client = new DefaultHttpClient();
			
			HttpResponse response = client.execute(httpPost);
			String payload = IOUtils.toString(response.getEntity().getContent());
			
			JSONObject payloadJSON = (JSONObject) JSONSerializer.toJSON(payload);
			
			if (!oneIDClient.isValidated(payloadJSON))
				return new OneIDResponse(false, payloadJSON.getString("error"), null);
			
			payloadJSON.put("uid", uid);
			
			return oneIDClient.validate(payloadJSON.toString());
    }
    
}
