package com.junar.searchpharma;

import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PharmaListFragment extends Fragment {
	public static final String ARG_SECTION_NUMBER = "section_number";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.pharma_list, container, false);        
        return rootView;
    }
}

