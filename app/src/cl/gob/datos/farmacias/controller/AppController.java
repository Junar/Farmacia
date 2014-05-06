package cl.gob.datos.farmacias.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Application;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import cl.gob.datos.farmacias.helpers.LocalDao;
import cl.gob.datos.farmacias.helpers.Utils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.junar.searchpharma.Pharmacy;

public class AppController extends Application implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    private static final String TAG = AppController.class.getSimpleName();
    public static float MAX_RADIO_IN_METERS = 10000;
    private LocalDao localDao;
    private static AppController mInstance;
    private static LocationClient client;
    private static Location location;

    public static void connectLocationClient() {
        if (Utils.isGooglePlayAvailable(AppController.getInstace()
                .getApplicationContext())) {

            if (client == null) {
                client = new LocationClient(AppController.getInstace(),
                        AppController.getInstace(), AppController.getInstace());
            }
            if (client != null && !client.isConnected()) {
                client.connect();
            }
        }
    }

    public static void disconnectLocationClient() {
        if (client != null && client.isConnected()) {
            client.disconnect();
            client = null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localDao = new LocalDao(getApplicationContext());
        mInstance = this;
        AppController.connectLocationClient();
    }

    public static AppController getInstace() {
        return mInstance;
    }

    public LocalDao getLocalDao() {
        return localDao;
    }

    public List<MarkerOptions> getMarkersList() {
        return getMarkersListForPharmaList(getPharmaList());
    }

    public List<MarkerOptions> getMarkersList(List<Pharmacy> pharmas) {
        return getMarkersListForPharmaList(pharmas);
    }

    public List<Pharmacy> getPharmaList() {
        List<Pharmacy> pharmaList = localDao.getPharmaList();
        return pharmaList;
    }

    public Pharmacy getPharmaById(long id) {
        return localDao.getPharmaById(id);
    }

    public List<MarkerOptions> getMarkersListForCommune(String commune) {
        List<Pharmacy> pharmaList = localDao.getPharmaByCommune(commune);
        Log.i(TAG, "cant de farmacias para comuna " + pharmaList.size());
        return getMarkersListForPharmaList(pharmaList);
    }

    public List<MarkerOptions> getMarkersListForPharmaList(
            List<Pharmacy> pharmaList) {
        List<MarkerOptions> markerList = new ArrayList<MarkerOptions>();
        Iterator<Pharmacy> it = pharmaList.iterator();

        while (it.hasNext()) {
            Pharmacy pharma = it.next();

            markerList.add(getMarkerForPharmacy(pharma));
        }

        return markerList;
    }

    public MarkerOptions getMarkerForPharmacy(Pharmacy pharma) {
        MarkerOptions marker = new MarkerOptions()
                .position(
                        new LatLng(pharma.getLatitude(), pharma.getLongitude()))
                .title(pharma.getName()).snippet(pharma.toString())
                .icon(pharma.getMarkerIcon());

        return marker;
    }

    public List<Pharmacy> filterNearestPharma(Location actualLocation,
            long curRadioInMeters) {
        LatLng location = new LatLng(actualLocation.getLatitude(),
                actualLocation.getLongitude());
        return filterNearestPharma(location, curRadioInMeters, null);
    }

    public List<Pharmacy> filterNearestPharma(Location actualLocation,
            long curRadioInMeters, String type) {
        LatLng location = new LatLng(actualLocation.getLatitude(),
                actualLocation.getLongitude());
        return filterNearestPharma(location, curRadioInMeters, type);
    }

    public List<Pharmacy> filterNearestPharma(LatLng actualLocation,
            long curRadioInMeters, String type) {
        if (actualLocation == null)
            return null;
        List<Pharmacy> pharmaMarkers = localDao.getPharmaListByRegionAndComune(
                0, 0, actualLocation, type);
        List<Pharmacy> pharmasInRadio = new ArrayList<Pharmacy>();

        for (Pharmacy marker : pharmaMarkers) {
            if (marker.getDistance() <= curRadioInMeters) {
                pharmasInRadio.add(marker);
            }
        }
        return pharmasInRadio;
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Log.i(TAG, "Connection failed: " + arg0.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        if (client != null) {
            Log.i(TAG, "Connection successfully");
            location = client.getLastLocation();
        }
    }

    @Override
    public void onDisconnected() {
        Log.i(TAG, "Disconnected");
    }

    public static Location getLastLocation() {
        connectLocationClient();
        if (client != null && client.isConnected()) {
            Location tmp = client.getLastLocation();
            if (tmp != null) {
                location = tmp;
            }
        }
        return location;
    }
}