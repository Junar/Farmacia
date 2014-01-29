package cl.gob.datos.farmacias.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import cl.gob.datos.farmacias.R;
import cl.gob.datos.farmacias.controller.AppController;
import cl.gob.datos.farmacias.helpers.LocalDao;
import cl.gob.datos.farmacias.helpers.Utils;

import com.junar.searchpharma.Commune;
import com.junar.searchpharma.Region;

public class SearchPharmaFragment extends Fragment {
    private int regionId;
    private int communeId;
    private Spinner spinnerRegion;
    private Spinner spinnerCommune;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        LocalDao localDao = AppController.getInstace().getLocalDao();
        View rootView = inflater.inflate(R.layout.fragment_commune, container,
                false);

        // Spinners
        spinnerRegion = (Spinner) rootView.findViewById(R.id.spinner_region);
        spinnerCommune = (Spinner) rootView.findViewById(R.id.spinner_commune);

        List<Region> tmpList = new ArrayList<Region>();
        tmpList.add(new Region((long) 0, 0,
                getString(R.string.region_spinner_label)));
        for (Region region : localDao.getRegionList()) {
            tmpList.add(new Region(region.getId(), region.getCode(),
                    new String(region.getName())));
        }

        spinnerCommune.setEnabled(false);
        final List<Commune> tmpCommuneList = new ArrayList<Commune>();
        final Commune comm = new Commune();
        comm.setName(getString(R.string.commune_spinner_label));
        tmpCommuneList.add(comm);

        ArrayAdapter<Region> adapterRegion = new ArrayAdapter<Region>(
                getActivity(), R.layout.spinner, tmpList);

        ArrayAdapter<Commune> adapterCommune = new ArrayAdapter<Commune>(
                getActivity(), R.layout.spinner, tmpCommuneList);

        spinnerRegion.setAdapter(adapterRegion);
        spinnerCommune.setAdapter(adapterCommune);

        spinnerRegion.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                    View selectedItemView, int position, long id) {

                Region region = (Region) parentView.getItemAtPosition(position);
                regionId = region.getId().intValue();
                try {
                    LocalDao localDao = AppController.getInstace()
                            .getLocalDao();

                    tmpCommuneList.clear();
                    tmpCommuneList.add(comm);
                    tmpCommuneList.addAll(localDao
                            .getCommuneListByRegion(region));

                    ArrayAdapter<Commune> adapterCommune = new ArrayAdapter<Commune>(
                            getActivity(), R.layout.spinner, tmpCommuneList);
                    if (regionId > 0) {
                        spinnerCommune.setEnabled(true);
                        spinnerCommune.setAdapter(adapterCommune);
                    } else {
                        spinnerCommune.setSelection(0);
                        spinnerCommune.setEnabled(false);
                    }
                } catch (Exception e) {
                    Log.d("communeSpinner", "Region without communes");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spinnerCommune.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView,
                    View selectedItemView, int position, long id) {
                if (position > 0) {
                    Commune commune = (Commune) parentView
                            .getItemAtPosition(position);
                    communeId = commune.getId().intValue();
                } else {
                    communeId = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        // Button
        ImageButton searchButton = (ImageButton) rootView
                .findViewById(R.id.commune_search_button);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (regionId > 0 && communeId > 0) {
                    Bundle args = new Bundle();
                    args.putString(
                            "title",
                            ((Region) spinnerRegion.getSelectedItem())
                                    .getName()
                                    + ", "
                                    + ((Commune) spinnerCommune
                                            .getSelectedItem()).getName());
                    args.putInt("commune", communeId);
                    args.putInt("region", regionId);
                    Utils.openFragment(SearchPharmaFragment.this,
                            new PharmaListFragment(), args,
                            R.id.frames_container, true, "listadofarmacias");
                } else {
                    Toast.makeText(getActivity(),
                            getString(R.string.region_or_commune_not_selected),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;
    }
}