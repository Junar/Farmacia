package com.junar.api;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class JunarAPI {
    private final String API_KEY = "";
    private final String BASE_URI = "http://api.recursos.datos.gob.cl";
    private final String DS_URI = "/datastreams/";
    private final String INVOKE_URI = DS_URI.concat("invoke/");
    private String OUTPUT = "json_array";
    // Set the timeout in milliseconds until a connection is established.
    private static final int timeoutConnection = 30000;
    // Set the default socket timeout (SO_TIMEOUT)
    // in milliseconds which is the timeout for waiting for data.
    private static final int timeoutSocket = 30000;

    public String getApiKey() {
        return this.API_KEY;
    }

    private String getURI(String guid, String action) {
        return this.BASE_URI.concat(action).concat(guid).concat("?auth_key=")
                .concat(getApiKey()).concat(getOutputForUrl());
    }

    private String getURI(String guid, String action, String[] arguments,
            String[] filters) {
        String localUri = getURI(guid, action);

        if (arguments != null) {
            for (int i = 0; i < arguments.length; i++) {
                localUri = localUri.concat("&pArgument" + i + "="
                        + arguments[i]);
            }
        }

        if (filters != null) {
            for (int i = 0; i < filters.length; i++) {
                if (filters[i].contains("where=")) {
                    localUri = localUri.concat("&" + filters[i]);
                } else {
                    localUri = localUri
                            .concat("&filter" + i + "=" + filters[i]);
                }
            }
        }

        return localUri;
    }

    public String invoke(String guid, String[] params, String[] filters) {
        String url = getURI(guid, INVOKE_URI, params, filters);
        return callURI(url);
    }

    public String invoke(String guid) {
        String url = getURI(guid, INVOKE_URI, null, null);
        return callURI(url);
    }

    public String invoke(String guid, String[] params, boolean hasFilter) {
        String url = null;
        if (hasFilter) {
            getURI(guid, INVOKE_URI, null, params);
        } else {
            getURI(guid, INVOKE_URI, params, null);
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
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters,
                timeoutConnection);
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        HttpClient httpClient = new DefaultHttpClient(httpParameters);
        String response = null;
        try {
            Log.i("junar", url);
            HttpGet httpGet = new HttpGet(url);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpClient.execute(httpGet, responseHandler);
        } catch (Exception e) {
            Log.e("callURI", e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}
