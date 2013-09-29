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
    	Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner_region);
    	
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), 
    			R.array.region_array, android.R.layout.simple_spinner_item);
    	
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(adapter);
    	        	
    	return rootView;        	
    }
}