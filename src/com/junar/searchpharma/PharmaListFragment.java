package com.junar.searchpharma;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PharmaListFragment extends Fragment {	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {		
        View rootView = inflater.inflate(R.layout.pharma_list, container, false);
        container.bringChildToFront(rootView);
        Log.i("pharma_list", "loading pharma list fragment...");

        return rootView;
    }
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	public void onResume() {
		super.onResume();
	}
}

