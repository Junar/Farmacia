package com.junar.searchpharma.dao;

import com.junar.api.JunarAPI;


public class JunarPharmacyDao {
	private String DATA_GUID = "OPERA-DEL-EN-MILLO-DE";

	public void queryByCommune() {
		// prepareRequest() and send
		
	}
	
	public void getDatastreamInfo() {
		JunarAPI junar = new JunarAPI();
		
		junar.info(DATA_GUID);
	}
}