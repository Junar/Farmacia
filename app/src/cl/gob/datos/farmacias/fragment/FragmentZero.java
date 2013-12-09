package cl.gob.datos.farmacias.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cl.gob.datos.farmacias.R;
import cl.gob.datos.farmacias.helpers.Utils;

public class FragmentZero extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_zero, container,
                false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utils.openFragment(this, new SearchPharmaFragment(), null,
                R.id.frames_container, false, "zero");
    }

}