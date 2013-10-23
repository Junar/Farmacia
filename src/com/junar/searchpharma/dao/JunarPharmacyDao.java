package com.junar.searchpharma.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.junar.api.JunarAPI;
import com.junar.searchpharma.Pharmacy;


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
	
	public List<Pharmacy> parseJsonArrayResponse(String response) {
		/**
		 * {
		 * 	"tags":[],
		 * 	"id":"LISTA-FARMA-DE-TURNO-2",
		 * 	"result":
		 * 		[
		 * 			["región","comuna","nombre farmacia","dirección","día","mes","horario atención","latitud","longitud","teléfono"],
		 * 			["2","2101","Cruz Verde 222","Arturo Pratt 640","1","7","DESDE 8:00 AM. HASTA LAS 8:00 DEL DÍA SIGUIENTE","-23.6491776","-70.3963394",""],
		 * 			["2","2101","Cruz Verde 725","Av. J.M. Carrera Nº1527","1","7","DESDE 10:00 AM. HASTA LAS 22:00 DEL MISMO DIA","-23.6600609","-70.4024124",""],
		 * 			["2","2101","Cruz Verde 223","M.A.Matta N° 2490","2","7","DESDE 8:00 AM. HASTA LAS 8:00 DEL DÍA SIGUIENTE","-23.697752","-70.4103928",""]
		 * 		]
		 * }
		 */
		List<Pharmacy> pharmaList = new ArrayList<Pharmacy>();
		try {			
			JSONObject jsonResponse = new JSONObject(response);			
			JSONArray resultArray = jsonResponse.getJSONArray("result");
			
			for (int i=1; i < resultArray.length(); i++) {
				JSONArray columnArray = resultArray.getJSONArray(i);
				Pharmacy pharma = this.getPharmacyFromJson(columnArray);
				pharmaList.add(pharma);
			}						
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return pharmaList;
	}
	
	public Pharmacy getPharmacyFromJson(JSONArray json) throws JSONException {
		Pharmacy pharma = new Pharmacy();
		pharma.setRegion(json.getString(0));
		pharma.setCommune(json.getString(1));
		pharma.setName(json.getString(2));
		pharma.setAddress(json.getString(3));
		pharma.setLatitude(json.getLong(7));
		pharma.setLongitude(json.getLong(8));
		pharma.setPhone(json.getString(9));
		
		return pharma;
	}
}