package com.junar.searchpharma;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.junar.searchpharma.dao.JunarPharmacyDao;
import com.junar.searchpharma.dao.LocalDao;

public class SearchPharmaController {
	private final String DATE_FORMAT_MONTH = "M";
	private final String DATE_FORMAT_DAY = "d";
	private final float MAX_RADIO_IN_METERS = 1000; 
	protected JunarPharmacyDao junarDao;
	protected LocalDao localDao;
	private Context context;
	
	public SearchPharmaController(Context context) {
		this.context = context;
		junarDao = new JunarPharmacyDao(context);	
		localDao = new LocalDao(context);
		
		this.isGooglePlayAvailable();		
		this.initCache();
	}

	protected JunarPharmacyDao getJunarDao() {
		return this.junarDao;
	}
	
	protected LocalDao getLocalDao() {
		return this.localDao; 
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
	
    private void initCache() {
    	if (localDao.isFirstPopulate()) {
			String jsonArrayResponse = junarDao.invokeDatastream();
			
			List<Pharmacy> pharmaList = junarDao.parseJsonArrayResponse(jsonArrayResponse);
			localDao.cachePharmaList(pharmaList);
			localDao.addDatasetCacheControl();			
			Log.d("initCache", "cache count:" + localDao.getCachePharmaCount());
		} else {
			Log.d("populateLocalCache", "Isnt first running, cache already filled up");
		}
    }
    
    private Integer getIntegerDate(String date_format) {
    	Calendar calendar = Calendar.getInstance();
    	SimpleDateFormat sdf = new SimpleDateFormat(date_format);    	
    	return Integer.valueOf(sdf.format(calendar.getTime()));
    }
        
    public List<MarkerOptions> getMarkersListForToday() {    	
    	List<Pharmacy> pharmaList = localDao.getPharmaByDayMonth(this.getIntegerDate(DATE_FORMAT_MONTH), this.getIntegerDate(DATE_FORMAT_DAY));
    	return this.getMarkersListForPharmaList(pharmaList);
    }
    
    public List<MarkerOptions> getMarkersListForCommune(String commune) {
    	List<Pharmacy> pharmaList = localDao.getPharmaByCommune(commune);
    	
    	Log.i("pharma_commune", "cant de farmacias para comuna " + pharmaList.size());
    	return this.getMarkersListForPharmaList(pharmaList);
    }
    
    public List<MarkerOptions> getMarkersListForPharmaList(List<Pharmacy> pharmaList) {
    	List<MarkerOptions> markerList = new ArrayList<MarkerOptions>();
    	Iterator<Pharmacy> it = pharmaList.iterator();
    	while(it.hasNext()) {
    		Pharmacy pharma = it.next();
    		LatLng pharaLatLng = new LatLng(pharma.getLatitude(), pharma.getLongitude());
    		MarkerOptions marker = new MarkerOptions().position(pharaLatLng).title(pharma.getName());    		    		
    		markerList.add(marker);
    	}
    	
    	return markerList;
    }
    
    public void filterNearestPharma(Location actualLocation) {  
    	Log.i("nearest_pharma", "init");
    	//List<MarkerOptions> pharmaMarkers = this.getMarkersListForToday();
    	List<MarkerOptions> pharmaMarkers = this.getMarkersListForCommune("5109");
    	Iterator<MarkerOptions> it = pharmaMarkers.iterator();
    	List<MarkerOptions> markersInRadio = new ArrayList<MarkerOptions>();
    	
    	while (it.hasNext()) {
        	MarkerOptions marker = it.next();   
        	/**
        	 * The computed distance is stored in results[0]. 
        	 * If results has length 2 or greater, the initial bearing is stored in results[1]. 
        	 * If results has length 3 or greater, the final bearing is stored in results[2].
        	 */
        	float[] results = new float[1];        	
        	Location.distanceBetween(
        			actualLocation.getLatitude(), actualLocation.getLongitude(), 
        			marker.getPosition().latitude, marker.getPosition().longitude, 
        			results);
        	
        	if (results[0] < this.MAX_RADIO_IN_METERS) {        		
        		Log.i("nearest_pharma", "pharma in radio, distance of " + results[0] + " meters");
        		markersInRadio.add(marker);
        	} else {
        		Log.i("nearest_pharma", "distance of " + results[0] + " is out of radio " + this.MAX_RADIO_IN_METERS);
        	}
    	}    	
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