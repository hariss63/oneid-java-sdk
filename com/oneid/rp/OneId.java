package com.oneid.rp;

import it.sauronsoftware.base64.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author acer
 */
public class OneId {

    public static String oneidServers = "";
    public static String oneidServer = "https://keychain" + oneidServers + ".oneid.com";
    public String oneidSscript = "<script src=\"https://api" + oneidServers + "oneid.com/js/includeexternal.js type=\"text/javascript\"></script>";
    public String oneidFormScript = "<script src=\"https://api" + oneidServers + ".oneid.com/form/form.js\" type=\"text/javascript\"></script>";
// Set your values here
    public String oneidReferralCode = "yyy";
    public String oneidApiId = "";
    public String oneidApiKey = "";

    public OneId() {
// Load key file
        JSONObject json = jsonDecode("api_key" + oneidServers + ".json");
        oneidApiId = json.getString("API_ID");
        oneidApiKey = json.getString("API_KEY");
    }

//    public static void main(String[] args) {
//        System.out.println(new OneId().oneIDFormFill());
//    }

    public JSONObject callOneID(String method, String post) {
        try {
            String scope = "";
            String encoding = Base64.encode(oneidApiId + ":" + oneidApiKey);
            HttpPost httpPost = new HttpPost(oneidServer + scope + "/" + method);
            httpPost.setHeader("Authorization", "Basic " + encoding);
            HttpClient client = new DefaultHttpClient();
            // client.getCredentialsProvider().setCredentials(new AuthScope("https://keychain.oneid.com",443), new UsernamePasswordCredentials(oneidApiId, oneidApiKey));

            HttpResponse response = client.execute(httpPost);
            return jsonDecode(response.getEntity().getContent());
        } catch (Exception ex) {
            Logger.getLogger(OneId.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public void oneIdSetCredentials(String apiId, String apiKey) {
        oneidApiId = apiId;
        oneidApiKey = apiKey;
    }

    public JSONObject jsonDecode(String jsonFile) {
        JSONObject json = null;
        try {
            InputStream is = OneId.class.getResourceAsStream(jsonFile);
            String jsonTxt = IOUtils.toString(is);
            json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
        } catch (IOException ex) {
            Logger.getLogger(OneId.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    public JSONObject jsonDecode(InputStream is) {
        JSONObject json = null;
        try {
            String jsonTxt = IOUtils.toString(is);
            json = (JSONObject) JSONSerializer.toJSON(jsonTxt);
        } catch (IOException ex) {
            Logger.getLogger(OneId.class.getName()).log(Level.SEVERE, null, ex);
        }
        return json;
    }

    public String jsonEncode(Map<String, String> jsonMap) {
        JSONObject json = new JSONObject();
        json.accumulateAll(jsonMap);
        return json.toString();
    }

    public String oneIDButton(String callback) {
        return "<img class=\"oneidlogin\" id=\"oneidlogin\" data-challenge='{\"nonce\":\"" + oneIDMakeNonce() + "\",\"attr\":\"personal_info[email]\",\"callback\":\"" + callback + "\"}' src=\"https://api" + oneidServers + ".oneid.com/images/oneid_signin.png\" onclick=\"OneId.login()\">";
    }

    public String oneIDFormFill() {
        return "<img class=\"oneidlogin\" id=\"oneidlogin\" src=\"https://api" + oneidServers + ".oneid.com/api/images/btn_id_signin.gif\" onclick=\"OneId.login()\">";
    }

    public String oneIDRedirect(String page, JSONObject response) {
        return ("{\"error\":\""
                + response.getString("error")
                + "\",\"errorcode\":\""
                + response.getString("errorcode")
                + "\",\"url\":\""
                + page
                + "\",\"response\":"
                + response.toString()
                + "}");
    }

    public String OneIDProvision(String emailAddress, Map<String, String> attrs) {

        return "<img id=\"getAOneIdButton\" class=\"oneidlogin\"ref=" + oneidReferralCode + "src=\"https://api" + oneidServers + "oneid.com/images/oneid_signin.png\" data-userattrs = '" + jsonEncode(attrs) + " 'onclick=\"OneId.createOneId()\" >";
    }

    public String oneIDIsSuccess(JSONObject response) {
        return response.getString("errorcode");
    }

    public String oneIDMakeNonce() {
        JSONObject json = callOneID("make_nonce", null);
        return json.getString("nonce");
    }

    public Map oneIDResponse(String inputString) {

        JSONObject validate = callOneID("validate", inputString);
        if (oneIDIsSuccess(validate) == null) 
        {
            validate.accumulate("failed", "failed");
            return validate;
        }

        JSONObject arr = jsonDecode(inputString);
        arr.accumulate("errorcode", validate.get("errorcode"));
        arr.accumulate("error", validate.get("error"));
        return arr;
    }
}
