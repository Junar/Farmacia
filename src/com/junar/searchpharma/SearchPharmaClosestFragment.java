package com.junar.searchpharma;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchPharmaClosestFragment extends SupportMapFragment implements LocationListener {
	private GoogleMap googleMap;
	private SupportMapFragment fragment;
	private Context context;
	private LocationManager locationManager;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);		
		this.context = container.getContext();
		
		 locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		return inflater.inflate(R.layout.fragment_closest, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		FragmentManager fm = getChildFragmentManager();
		fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
		if (fragment == null) {
			Log.d("onActivityCreated", "fragmento nulo");
			fragment = SupportMapFragment.newInstance();
			fm.beginTransaction().replace(R.id.map, fragment).commit();
		} else {
			Log.d("onActivityCreated", "fragmento NO nulo");
		}	
	}
	
	public void onResume() {
		super.onResume();
		
		googleMap = fragment.getMap();
		
		LatLng hereLatLng = getActualLocation(this.context);
		
		if (hereLatLng != null) {
			Marker here = googleMap.addMarker(new MarkerOptions().position(hereLatLng).title("Ubicacion actual"));		    
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hereLatLng, 15));
		}
	}
	
	public LatLng getActualLocation(Context context) {			    	   
	    Criteria criteria = new Criteria();	    
	    String provider = locationManager.getBestProvider(criteria, false);	    
	    locationManager.requestLocationUpdates(provider, 400, 1, (android.location.LocationListener) this);
	    
	    Location location = locationManager.getLastKnownLocation(provider);
	    	    	    
	    LatLng hereLatLng = null;	    
	    if (location != null) {
	    	Log.d("getActualLocation", "Provider " + provider + " has been selected.");
	    	hereLatLng = new LatLng(location.getLatitude(), location.getLongitude());
	    } else {
	    	Log.d("getActualLocation", "location not available provider:" + provider);
	    }
	    	    	    
	    return hereLatLng;
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub		
		LatLng hereLatLng = new LatLng(location.getLatitude(), location.getLongitude());
		Marker here = googleMap.addMarker(new MarkerOptions().position(hereLatLng).title("Ubicacion actual"));	    
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hereLatLng, 15));		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		locationManager.requestLocationUpdates(provider, 400, 1, (android.location.LocationListener) this);
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
}