package cl.gob.datos.farmacias.fragment;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cl.gob.datos.farmacias.R;
import cl.gob.datos.farmacias.controller.AppController;
import cl.gob.datos.farmacias.helpers.LocalDao;
import cl.gob.datos.farmacias.helpers.Utils;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.junar.searchpharma.Pharmacy;

public class PharmaClosestFragment extends SupportMapFragment {
    private GoogleMap googleMap;
    private Context context;
    private HashMap<String, Pharmacy> pharmaList = new HashMap<String, Pharmacy>();
    private int commune = 0;
    private int region = 0;
    private int currentRadioInMeters = (int) AppController.MAX_RADIO_IN_METERS;
    private LatLngBounds.Builder builder;
    private AlertDialog settingDialog;
    private LatLng currentLocation;

    public void onResume() {
        super.onResume();
        currentLocation = null;

        context = getActivity().getApplicationContext();

        setHasOptionsMenu(true);

        Bundle args = this.getArguments();
        if (args != null) {
            commune = args.getInt("commune");
            region = args.getInt("region");
        }

        googleMap = getMap();
        googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                Pharmacy phar = pharmaList.get(marker.getTitle());
                if (phar != null) {
                    Long pharId = phar.getId();
                    Intent pharmaDetail = new Intent(context,
                            PharmaDetailActivity.class);
                    pharmaDetail.putExtra("id", pharId);
                    startActivity(pharmaDetail);
                }
            }
        });

        googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View localView = ((LayoutInflater) context
                        .getSystemService("layout_inflater")).inflate(
                        R.layout.info_marker_layout, null);
                TextView name = (TextView) localView
                        .findViewById(R.id.pharma_marker);
                TextView address = (TextView) localView
                        .findViewById(R.id.address_marker);
                TextView horario = (TextView) localView
                        .findViewById(R.id.horario_marker);
                Pharmacy phar = pharmaList.get(marker.getTitle());
                if (phar != null) {
                    name.setText(phar.getName());
                    address.setText(phar.getAddress());
                    horario.setText(getText(R.string.current_day) + " "
                            + Utils.getDatePhone(false) + " - "
                            + phar.getSchedule());
                } else {
                    return null;
                }
                return localView;
            }
        });

        addMarkers();
        makeSettingsDialog();
    }

    private void makeSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String msg = getString(R.string.map_config_message).replaceAll("#",
                String.valueOf(currentRadioInMeters));
        builder.setMessage(msg).setTitle(getString(R.string.map_config_title));

        final SeekBar seekBar = new SeekBar(getActivity()
                .getApplicationContext());
        seekBar.setMax((int) AppController.MAX_RADIO_IN_METERS);
        seekBar.incrementProgressBy(1000);
        seekBar.setProgress(currentRadioInMeters);
        builder.setView(seekBar);
        builder.setPositiveButton(getString(R.string.btn_accept),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        currentRadioInMeters = seekBar.getProgress();
                        addMarkers();
                        centerMap();
                    }
                });
        builder.setNegativeButton(getString(R.string.btn_cancel),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        settingDialog = builder.create();
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progressChanged = 0;

            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                progressChanged = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                String message = getString(R.string.map_config_message)
                        .replaceAll("#", String.valueOf(progressChanged));
                settingDialog.setMessage(message);

            }
        });

    }

    public void showSettingDialog() {
        settingDialog.show();
    }

    protected void addMarkers() {
        googleMap.clear();
        builder = new LatLngBounds.Builder();
        if (commune == 0 && region == 0) {
            addActualLocationMarker();
        }
        addPharmaMarkers();
    }

    protected void addActualLocationMarker() {
        LatLng hereLatLng = getActualLatLng();
        if (hereLatLng != null) {
            currentLocation = hereLatLng;
            googleMap.addMarker(new MarkerOptions().position(hereLatLng).title(
                    getString(R.string.actual_location)));
            builder.include(hereLatLng);
            Circle circle = googleMap.addCircle(new CircleOptions()
                    .center(hereLatLng).radius(currentRadioInMeters)
                    .strokeColor(Color.BLACK).strokeWidth(5)
                    .fillColor(0x4000ff00));
        }
    }

    protected void addPharmaMarkers() {
        List<Pharmacy> markersList;

        if (region > 0 && commune > 0) {
            LocalDao localDao = AppController.getInstace().getLocalDao();
            markersList = localDao.getPharmaListByRegionAndComune(region,
                    commune);
        } else {
            markersList = getNearestPharma();
        }

        if (markersList != null && markersList.size() > 0) {
            for (Pharmacy pharma : markersList) {
                googleMap.addMarker(AppController.getInstace()
                        .getMarkerForPharmacy(pharma));
                builder.include(new LatLng(pharma.getLatitude(), pharma
                        .getLongitude()));
                pharmaList.put(pharma.getName(), pharma);
            }
        } else if (((MainActivity) getActivity()).getSelectedPage() == 1) {
            Toast.makeText(getActivity(),
                    getString(R.string.no_pharmas_in_range), Toast.LENGTH_LONG)
                    .show();
        }

        if (getView().getViewTreeObserver().isAlive()) {
            getView().getViewTreeObserver().addOnGlobalLayoutListener(
                    new OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if (getView() != null) {
                                ViewTreeObserver treeObs = getView()
                                        .getViewTreeObserver();
                                if (treeObs != null) {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                                        treeObs.removeGlobalOnLayoutListener(this);
                                    } else {
                                        treeObs.removeOnGlobalLayoutListener(this);
                                    }
                                }
                                centerMap();
                            }
                        }
                    });
        }
    }

    private void centerMap() {
        if (builder != null) {
            if (pharmaList.size() > 1) {
                LatLngBounds bounds = builder.build();
                int padding = 100;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                        padding);
                googleMap.animateCamera(cu);
            } else if (pharmaList.size() == 1) {
                Pharmacy f = (Pharmacy) pharmaList.values().toArray()[0];
                LatLng loc = new LatLng(f.getLatitude(), f.getLongitude());
                googleMap
                        .moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
            } else if (pharmaList.size() == 0 && region == 0 && commune == 0
                    && currentLocation != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        currentLocation, 11));
            }
        }
    }

    protected List<Pharmacy> getNearestPharma() {
        if (currentLocation != null) {
            return AppController.getInstace().filterNearestPharma(
                    currentLocation, currentRadioInMeters);
        } else
            return null;
    }

    protected List<Pharmacy> getTodayPharmas() {
        return AppController.getInstace().getPharmaList();
    }

    public LatLng getActualLatLng() {
        LatLng hereLatLng = null;
        Location location = AppController.getLastLocation();
        if (location != null) {
            hereLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
        }

        return hereLatLng;
    }

    public void openList() {
        if (pharmaList.size() > 0) {
            Bundle args = new Bundle();
            args.putLong("radio", currentRadioInMeters);
            if (currentLocation != null) {
                args.putDouble("longitude", currentLocation.longitude);
                args.putDouble("latitude", currentLocation.latitude);
            }
            Utils.openFragment(this, new PharmaListFragment(), args,
                    R.id.frames_map_container, true, "listadodesdemap");
        } else {
            Toast.makeText(getActivity(),
                    getString(R.string.no_pharmas_in_range), Toast.LENGTH_LONG)
                    .show();
        }
    }
}