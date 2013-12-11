package cl.gob.datos.farmacias.fragment;

import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import cl.gob.datos.farmacias.R;
import cl.gob.datos.farmacias.adapter.CustomPharmaAdapter;
import cl.gob.datos.farmacias.controller.AppController;
import cl.gob.datos.farmacias.helpers.LocalDao;
import cl.gob.datos.farmacias.helpers.Utils;

import com.google.android.gms.maps.model.LatLng;
import com.junar.searchpharma.Pharmacy;

public class PharmaListFragment extends Fragment {
    private int commune = 0;
    private int region = 0;
    private LatLng currentLocation;
    private TextView regionName;
    private TextView currentDate;
    private ListView pharmaListView;
    private CustomPharmaAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pharmacy_list,
                container, false);
        regionName = (TextView) rootView.findViewById(R.id.pharma_region_name);
        currentDate = (TextView) rootView
                .findViewById(R.id.pharma_list_schedule);
        pharmaListView = (ListView) rootView
                .findViewById(R.id.pharma_list_View);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        List<Pharmacy> pharmaList = null;
        LocalDao localDao = null;
        Bundle args = this.getArguments();
        if (args != null && !args.containsKey("radio")) {
            commune = args.getInt("commune");
            region = args.getInt("region");
            localDao = AppController.getInstace().getLocalDao();
            pharmaList = localDao.getPharmaListByRegionAndComune(region,
                    commune);
            Location location = AppController.getLastLocation();
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(),
                        location.getLongitude());
            }
            regionName.setText(args.getString("title"));
        } else {
            long radio = args.getLong("radio");
            if (args.containsKey("latitude") && args.containsKey("longitude")) {
                currentLocation = new LatLng(args.getDouble("latitude"),
                        args.getDouble("longitude"));
            }
            pharmaList = AppController.getInstace().filterNearestPharma(
                    currentLocation, radio);
            regionName.setText(getText(R.string.nearby_pharmacies_text) + " "
                    + radio + " mts");
        }

        currentDate.setText(getText(R.string.current_day) + " "
                + Utils.getDatePhone(false));

        adapter = new CustomPharmaAdapter(getActivity(),
                R.layout.fragment_pharmacy_item_row, pharmaList,
                currentLocation);
        setHasOptionsMenu(true);
        pharmaListView.setDivider(null);
        pharmaListView.setAdapter(adapter);
    }

    public void openMap() {
        Bundle args = new Bundle();
        args.putInt("commune", commune);
        args.putInt("region", region);
        Utils.openFragment(this, new PharmaClosestFragment(), args,
                R.id.frames_container, true, "mapadesdelistado");
    }

    public CustomPharmaAdapter getListAdapter() {
        return adapter;
    }
}
