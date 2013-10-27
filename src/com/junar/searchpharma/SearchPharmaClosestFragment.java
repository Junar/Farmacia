package com.junar.searchpharma;

import java.util.Iterator;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.junar.searchpharma.dao.LocalDao;

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
		
		LatLng hereLatLng = getActualLatLng(this.context);		
		if (hereLatLng != null) {
			googleMap.addMarker(new MarkerOptions().position(hereLatLng).title("Ubicacion actual"));		    
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hereLatLng, 15));
		}
		
		List<MarkerOptions> markersList = this.getNearestPharma();
		
		if (markersList != null) {
			Iterator<MarkerOptions> it = markersList.iterator();
			while (it.hasNext()) {
				googleMap.addMarker(it.next());
			}
		}
	}
	
	public List<MarkerOptions> getNearestPharma() {
		SearchPharmaController spController = ((SearchPharmaActivity) getActivity()).spController;
		
		return spController.filterNearestPharma(this.getActualLocation());		
	}
	
	public Location getActualLocation() {
		Criteria criteria = new Criteria();	    
	    String provider = locationManager.getBestProvider(criteria, false);	    
	    locationManager.requestLocationUpdates(provider, 400, 1, (android.location.LocationListener) this);
	    
	    return locationManager.getLastKnownLocation(provider);
	}
	
	public LatLng getActualLatLng(Context context) {			    	   	    
	    Location location = this.getActualLocation();	    	    	    
	    LatLng hereLatLng = null;	    
	    if (location != null) {
	    	hereLatLng = new LatLng(location.getLatitude(), location.getLongitude());
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