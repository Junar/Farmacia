package com.junar.searchpharma;

import com.junar.searchpharma.dao.JunarPharmacyDao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SearchPharmaCommuneFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	JunarPharmacyDao localDao = ((SearchPharmaActivity) getActivity()).spController.getLocalDao();
    	View rootView = inflater.inflate(R.layout.fragment_commune, container, false);    	
    	
    	Spinner spinnerRegion = (Spinner) rootView.findViewById(R.id.spinner_region);
    	Spinner spinnerCommune = (Spinner) rootView.findViewById(R.id.spinner_commune);
    	    	
    	ArrayAdapter<String> adapterRegion = new ArrayAdapter<String>(getActivity(), 
    			android.R.layout.simple_spinner_item, localDao.getRegionForSpinner());
    	
    	ArrayAdapter<String> adapterCommune = new ArrayAdapter<String>(getActivity(), 
    			android.R.layout.simple_spinner_item, localDao.getCommuneSpinnerLabel());
    	    	
    	adapterRegion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	adapterCommune.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	spinnerRegion.setAdapter(adapterRegion);
    	spinnerCommune.setAdapter(adapterCommune);
    	
    	spinnerRegion.setOnItemSelectedListener(new OnItemSelectedListener() {    		
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub				
			}	
    	});
    	
    	return rootView;        	
    }
}