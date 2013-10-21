package com.junar.searchpharma;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.junar.searchpharma.dao.JunarPharmacyDao;

public class SearchPharmaController {
	protected JunarPharmacyDao junarLocalDao;
	private Context context;
	
	public SearchPharmaController(Context context) {
		this.context = context;
		junarLocalDao = new JunarPharmacyDao(context);	
		
		this.isGooglePlayAvailable();
	}

	protected JunarPharmacyDao getLocalDao() {
		return this.junarLocalDao;
	}
	
	protected void isGooglePlayAvailable() {
		try {
			int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
			
			if (ConnectionResult.SUCCESS == resultCode) {
	            Log.i("controller", "Google Play services is available.");
	            // Continue
	            return; 
	        } else {
	            Log.d("controller", "Google Play services is unavailable.");
	            return;
	        }
		} catch (Exception e) {
			Log.d("controller", "Google Play services exception");
			Log.d("controller", e.getMessage());
		}
		
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