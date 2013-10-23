package com.junar.searchpharma.dao;

import android.content.Context;
import android.util.Log;

import com.junar.api.JunarAPI;


public class JunarPharmacyDao {
	private String DATA_GUID = "LISTA-FARMA-DE-TURNO-2";	
	private Context context;
	
	public JunarPharmacyDao(Context context) {		
		this.context = context;
	}
	
	public JunarPharmacyDao() {
		
	}
		
	public void getDatastreamInfo() {
		JunarAPI junar = new JunarAPI();		
		junar.info(DATA_GUID);
	}
	
	public String invokeDatastream() {
		JunarAPI junar = new JunarAPI();
		return junar.invoke(DATA_GUID, null);
	}
	
	public void populateLocalCache() {
		Log.i("populateLocalCache", this.invokeDatastream());
	}
}