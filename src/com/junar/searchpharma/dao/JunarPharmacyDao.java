package com.junar.searchpharma.dao;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.entity.StringEntity;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class JunarPharmacyDao {
	private final String JUNAR_URL = "http://api.stage.junar.com";
	private final String JUNAR_API_KEY = "";	
	private Context context; // Android app context
	
	public void queryByCommune() {
		// prepareRequest() and send
		
	}
	
	private String getRequestURL() {
		return this.JUNAR_URL;
	}
	
	/**
	 * transform json to string entity, define async http response handler callback
	 * 
	 * @param json
	 * @throws UnsupportedEncodingException
	 */
	private void prepareRequest(String json) throws UnsupportedEncodingException {	
		StringEntity entity = new StringEntity(json, "UTF-8");
		AsyncHttpResponseHandler requestResponse = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {	
				
			}
			
			@Override
			public void onStart() {				
				Log.v("sendRequest", "onStart()");
			}

			@Override
			public void onFinish() {
				Log.v("sendRequest", "onFinish()");
			}

			@Override
			public void onFailure(Throwable error, String content) {
				Log.v("sendRequest", "onFailure() error: " + content);
				Log.v("sendRequest", "onFailure() stack: " + error.getMessage());						
			}			
		};
		
		this.sendRequest(getRequestURL(), entity, requestResponse);
		
	}
	
	/**
	 * send HTTP request to URL
	 * 
	 * @param url
	 * @param entity String Entity that contains JSON data
	 * @param responseHandler callback
	 */
	public void sendRequest(String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
		AsyncHttpClient client = new AsyncHttpClient();

		//client.addHeader("Authorization", getAuthHeader());
		client.post(this.context, url, entity, "application/json", responseHandler);
	}
}