package com.junar.searchpharma;

import android.content.Context;

import com.junar.searchpharma.dao.JunarPharmacyDao;

public class SearchPharmaController {
	protected JunarPharmacyDao junarLocalDao;
	
	public SearchPharmaController(Context context) {
		junarLocalDao = new JunarPharmacyDao(context);
	}

	protected JunarPharmacyDao getLocalDao() {
		return this.junarLocalDao;
	}
	
	public void onSearchButtonClicked() {

	}
	
	public void onSearchByCommuneTabClicked() {
		
	}
	
	public void onSearchClosestTabClicked() {
				
	}
	
	public void onViewMapButtonClicked() {
		
	}
	
	public void onViewListButtonClicked() {
		
	}
	
	public void onComplaintButtonClicked() {
		
	}
	
	public void onTagPharmaButtonClicked() {
		
	}
}