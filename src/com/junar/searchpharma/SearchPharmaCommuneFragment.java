package com.junar.searchpharma;

import com.junar.searchpharma.dao.LocalDao;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SearchPharmaCommuneFragment extends Fragment {
	Spinner spinnerCommune;
	FragmentManager fragmentManager;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
    	LocalDao localDao = ((SearchPharmaActivity) getActivity()).spController.getLocalDao();
    	View rootView = inflater.inflate(R.layout.fragment_commune, container, false);    	
    	
    	
    	// Spinners
    	Spinner spinnerRegion = (Spinner) rootView.findViewById(R.id.spinner_region);
    	spinnerCommune = (Spinner) rootView.findViewById(R.id.spinner_commune);
    	    	
    	ArrayAdapter<Region> adapterRegion = new ArrayAdapter<Region>(getActivity(), 
    			android.R.layout.simple_spinner_item, localDao.getRegionList());
    	
    	ArrayAdapter<String> adapterCommune = new ArrayAdapter<String>(getActivity(), 
    			android.R.layout.simple_spinner_item, localDao.getCommuneSpinnerLabel());
    	    	
    	adapterRegion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	adapterCommune.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	
    	spinnerRegion.setAdapter(adapterRegion);
    	spinnerCommune.setAdapter(adapterCommune);
    	
    	spinnerRegion.setOnItemSelectedListener(new OnItemSelectedListener() {    		
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
												
				Region region = (Region) parentView.getItemAtPosition(position);							
				try {
					LocalDao localDao = ((SearchPharmaActivity) getActivity()).spController.getLocalDao();
					ArrayAdapter<Commune> adapterCommune = new ArrayAdapter<Commune>(getActivity(), 
			    			android.R.layout.simple_spinner_item, localDao.getCommuneListByRegion(region));
					spinnerCommune.setAdapter(adapterCommune);
				} catch(Exception e) {
					Log.d("communeSpinner", "Region without communes");
				}						
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub				
			}	
    	});
    	
    	// Button
    	Button searchButton = (Button) rootView.findViewById(R.id.commune_search_button);
    	
    	searchButton.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			// it was the 1st button
    			fragmentManager = getFragmentManager();
    			Fragment srcFragment = fragmentManager.findFragmentByTag("android:switcher:" + R.id.pager + ":0");
    			Fragment dstFragment = new PharmaListFragment();
    			FragmentTransaction ft = fragmentManager.beginTransaction();
    			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    			//ft.replace(srcFragment.getId(), dstFragment, "listfragment:tag");
    			ft.hide(srcFragment);
    			ft.add(R.id.fragment_commune, dstFragment, "listfragment:tag");    			
    			ft.show(dstFragment);    			
    			ft.addToBackStack(null);
    			ft.commit();    			
    		}
    	});
    	
    	return rootView;        	
    }
}