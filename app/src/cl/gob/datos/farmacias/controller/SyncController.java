package cl.gob.datos.farmacias.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import cl.gob.datos.farmacias.R;
import cl.gob.datos.farmacias.helpers.CommuneJsonHelper;
import cl.gob.datos.farmacias.helpers.LocalDao;
import cl.gob.datos.farmacias.helpers.PharmacyJsonHelper;
import cl.gob.datos.farmacias.helpers.RegionJsonHelper;
import cl.gob.datos.farmacias.helpers.Utils;

import com.junar.searchpharma.Commune;
import com.junar.searchpharma.Pharmacy;
import com.junar.searchpharma.Region;

public class SyncController {
    private static final String TAG = SyncController.class.getSimpleName();
    private final String DATE_FORMAT_MONTH = "M";
    private final String DATE_FORMAT_DAY = "d";
    private final String PREFS_NAME = "syncPharmaPreference";
    private final String KEY_DAY = "day";
    private final String KEY_MONTH = "month";
    private LocalDao localDao;
    private Context mContext;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SyncController(Context context) throws TimeoutException,
            JSONException, IOException {
        mContext = context;
        pref = context.getSharedPreferences(PREFS_NAME, 0);
        editor = pref.edit();
        localDao = AppController.getInstace().getLocalDao();
        initCache();
    }

    private void initCache() throws TimeoutException, JSONException,
            IOException {
        boolean firstTime = false;
        if (Utils.isOnline(mContext)
                && (localDao.isFirstPopulate() || isDiferentSyncDay())) {
            try {
                if (localDao.isFirstPopulate()) {
                    firstTime = true;
                    parseComuneFile(R.raw.codigo_ciudades);
                }
                cachePharmaList();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                if (firstTime || isDiferentSyncDay()) {
                    throw new JSONException("Error Parsing the data set.");
                }
            }
        } else {
            if (localDao.isFirstPopulate() || isDiferentSyncDay()) {
                throw new TimeoutException("There is not internet conection");
            }
        }
    }

    public void parseComuneFile(int resourceId) throws JSONException,
            IOException {
        InputStream is = mContext.getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;
        StringBuilder result = new StringBuilder();
        while ((readLine = br.readLine()) != null) {
            result.append(readLine);
        }
        cacheRegionList(result.toString());
        is.close();
        br.close();
    }

    private void cachePharmaList() throws JSONException, TimeoutException {
        final PharmacyJsonHelper pharmacyDao = new PharmacyJsonHelper();
        final String jsonArrayResponse = pharmacyDao.invokeDatastream(
                new String[] { getIntegerDate(DATE_FORMAT_DAY).toString(),
                        getIntegerDate(DATE_FORMAT_MONTH).toString() }, null);
        if (jsonArrayResponse == null) {
            throw new TimeoutException("There is not internet conection");
        }
        JSONArray jArray = new JSONObject(jsonArrayResponse)
                .getJSONArray("result");
        if (jArray.length() > 0) {
            localDao.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    localDao.cleanCachePharmaList();
                    List<Pharmacy> pharmaList = pharmacyDao
                            .parseJsonArrayResponse(jsonArrayResponse);
                    localDao.cachePharmaList(pharmaList);
                    localDao.addDatasetCacheControl();
                }
            });
            saveSyncDay();
        } else {
            Log.d(TAG, "There is not pharmacies for the current day.");
        }
    }

    private void cacheRegionList(String result) throws JSONException {
        JSONArray regionArray = new JSONObject(result).getJSONArray("regiones");
        if (regionArray.length() > 0) {
            RegionJsonHelper regionDao = new RegionJsonHelper();
            List<Region> regionsList = regionDao.parseJsonArrayResponse(result
                    .toString());
            localDao.cacheRegionList(regionsList);
            localDao.addDatasetCacheControl();
            Log.d(TAG, "cache count:" + localDao.getCachePharmaCount());
            cacheComuneList(result);
        } else {
            throw new JSONException("There is an error parsing regions list");
        }

    }

    private void cacheComuneList(String result) throws JSONException {
        JSONArray jArray = new JSONObject(result).getJSONArray("comunas");
        if (jArray.length() > 0) {
            CommuneJsonHelper communeDao = new CommuneJsonHelper();
            List<Commune> communeList = communeDao
                    .parseJsonArrayResponse(result);
            localDao.cacheCommuneList(communeList);
            localDao.addDatasetCacheControl();
            Log.d(TAG, "cache count:" + localDao.getCachePharmaCount());
        } else {
            throw new JSONException("There is an error parsing commune list");
        }
    }

    private boolean isDiferentSyncDay() {
        if (pref.getInt(KEY_DAY, 0) != getIntegerDate(DATE_FORMAT_DAY)
                || pref.getInt(KEY_MONTH, 0) != getIntegerDate(DATE_FORMAT_MONTH)) {
            return true;
        }
        return false;
    }

    private void saveSyncDay() {
        editor.putInt(KEY_DAY, getIntegerDate(DATE_FORMAT_DAY));
        editor.putInt(KEY_MONTH, getIntegerDate(DATE_FORMAT_MONTH));
        editor.commit();
    }

    @SuppressLint("SimpleDateFormat")
    private Integer getIntegerDate(String date_format) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(date_format);
        return Integer.valueOf(sdf.format(calendar.getTime()));
    }
}