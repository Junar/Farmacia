package com.junar.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class JunarAPI {
    private final String APP_KEY="a50116ebfb94789f2cad079d7c26caef0f85af14";    
    private final String BASE_URI="http://apisandbox.junar.com";
    private final String DS_URI = "/datastreams/";
    private final String INVOKE_URI = DS_URI.concat("invoke/");
    private String OUTPUT = "json_array";
    protected String lastResponse;
    
    public String getAuthKey() {
        return this.APP_KEY;
    }
    
    private String getURI(String guid, String action) {
    	// TODO: Remove limit
    	return this.BASE_URI.concat(action).concat(guid).concat("?auth_key=").concat(getAuthKey()).concat(getOutputForUrl()).concat("&limit=10");
    }
    
    private String getURI(String guid, String action, String[] params) {
    	String localUri = getURI(guid, action);
    	
        for (int i=0; i < params.length; i++) {
        	localUri = localUri.concat("&pArgument" + i + "=" + params[i]);
        }
        
    	return localUri;
    }
        
    public String invoke(String guid, String[] params) {
    	String url = getURI(guid, INVOKE_URI);
    	
    	if (params != null && params.length > 0) {
        	url = getURI(guid, INVOKE_URI, params);
        }     
        
        return callURI(url);
        
    }
    
    public String info(String guid) {        
        String url = getURI(guid, this.DS_URI);        
        
        return callURI(url);
    }
    
    public void setOutput(String type) {
    	this.OUTPUT = type;
    }
    
    public String getOutputForUrl() {
    	return "&output=".concat(this.OUTPUT);
    }

    private String callURI(String url) {
    	HttpClient httpClient = new DefaultHttpClient();
    	String response = null;
    	try {
    		HttpGet httpGet = new HttpGet(url);
    		ResponseHandler<String> responseHandler = new BasicResponseHandler();
    		response = httpClient.execute(httpGet, responseHandler);    		
    	} catch (Exception e) {
    		Log.d("callURI", "URL: " + url);
    		e.printStackTrace();
    	}    	
    	return response;    	
    }
}