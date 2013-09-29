package com.junar.searchpharma;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SearchPharmaCommuneFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.fragment_commune, container, false);    	
    	Spinner spinnerRegion = (Spinner) rootView.findViewById(R.id.spinner_region);
    	Spinner spinnerCommune = (Spinner) rootView.findViewById(R.id.spinner_commune);
    	
    	ArrayAdapter<CharSequence> adapterRegion = ArrayAdapter.createFromResource(getActivity(), 
    			R.array.region_array, android.R.layout.simple_spinner_item);
    	ArrayAdapter<CharSequence> adapterCommune = ArrayAdapter.createFromResource(getActivity(), 
    			R.array.commune_array, android.R.layout.simple_spinner_item);
    	
    	adapterRegion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	adapterCommune.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinnerRegion.setAdapter(adapterRegion);
    	spinnerCommune.setAdapter(adapterCommune);
    	        	
    	return rootView;        	
    }
}