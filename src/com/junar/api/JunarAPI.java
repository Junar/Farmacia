package com.junar.api;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class JunarAPI {
    private final String APP_KEY="a50116ebfb94789f2cad079d7c26caef0f85af14";    
    private final String BASE_URI="http://apisandbox.junar.com";
    private final String DS_URI = "/datastreams/";
    private final String INVOKE_URI = DS_URI.concat("invoke/");
    
    public String getAuthKey() {
        return this.APP_KEY;
    }
    
    private void callURI(String url, AsyncHttpResponseHandler responseHandler) {
    	AsyncHttpClient client = new AsyncHttpClient();
    	client.get(url, responseHandler);    	
    }
    
    private String getURI(String guid, String action) {
    	return this.BASE_URI.concat(action).concat(guid).concat("?auth_key=").concat(getAuthKey());
    }
    
    private String getURI(String guid, String action, String[] params) {
    	String local_url = getURI(guid, action);
    	
        for (int i=0; i < params.length; i++) {
        	local_url = local_url.concat("&pArgument" + i + "=" + params[i]);
        }
        
    	return local_url;
    }
    
    public void invoke(String guid, String[] params) {
    	String url = getURI(guid, INVOKE_URI);
    	
    	if (params != null && params.length > 0) {
        	url = getURI(guid, INVOKE_URI, params);
        }     
        
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        	public void onSuccess(String response) {
        		
        	}
        	
			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("invoke", "onFailure Content " + content + ", Error " + error + ", Stack " + error.getMessage());
				
				if (error instanceof java.net.SocketTimeoutException) {
					Log.v("invoke", "onFailure SocketTimeoutException, Retry to send");
				}
			}
        };
        
        
        callURI(url, responseHandler);    
        
    }
    
    public void info(String guid) {        
        String url = getURI(guid, this.DS_URI);
        
        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
        	public void onSuccess(String response) {
        		Log.v("info", response);
        	}
        	
			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("invoke", "onFailure Content " + content + ", Error " + error + ", Stack " + error.getMessage());
				
				if (error instanceof java.net.SocketTimeoutException) {
					Log.v("invoke", "onFailure SocketTimeoutException, Retry to send");
				}
			}
        };
        
        callURI(url, responseHandler);
    }
}