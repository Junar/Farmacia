package cl.gob.datos.farmacias.controller;

import java.io.IOException;
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
    private final String DATE_FORMAT_MONTH = "MM";
    private final String DATE_FORMAT_DAY = "dd";
    private final String DATE_FORMAT_YEAR = "yyyy";
    public static final String PREFS_NAME = "syncPharmaPreference";
    public static final String KEY_DAY = "day";
    public static final String KEY_MONTH = "month";
    public static final String KEY_YEAR = "year";
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
                }
                cachePharmaList();
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
                if (firstTime || isDiferentSyncDay()) {
                    throw new JSONException(e.getMessage());
                }
            }
        } else {
            if (localDao.isFirstPopulate() || isDiferentSyncDay()) {
                throw new TimeoutException("There is not internet conection");
            }
        }
    }

    private void cachePharmaList() throws JSONException, TimeoutException {
        final PharmacyJsonHelper pharmacyDao = new PharmacyJsonHelper();
        final String jsonArrayResponse = pharmacyDao.invokeDatastream(
                new String[] { getIntegerDate(DATE_FORMAT_DAY).toString(),
                        getIntegerDate(DATE_FORMAT_MONTH).toString() }, null);
        if (jsonArrayResponse == null) {
            throw new TimeoutException("There is not internet conection");
        }
        final JSONArray jArray = new JSONObject(jsonArrayResponse)
                .getJSONArray("result");

        Runnable runner = new Runnable() {
            private String hasFail = "false";

            public String toString() {
                return hasFail;
            }

            @Override
            public void run() {
                try {
                    if (jArray.length() > 0) {
                        localDao.cleanCacheRegionList();
                        localDao.cleanCacheCommuneList();
                        localDao.cleanCachePharmaList();
                        List<Pharmacy> pharmaList = pharmacyDao
                                .parseJsonArrayResponse(jsonArrayResponse);
                        localDao.cacheRegionList(cacheRegionList());
                        localDao.cacheCommuneList(cacheComuneList());
                        localDao.cachePharmaList(pharmaList);
                        localDao.addDatasetCacheControl();
                        saveSyncDay();
                    } else {
                        throw new JSONException(
                                "There is an error parsing pharma list");
                    }
                } catch (Exception e) {
                    if (localDao.isFirstPopulate()) {
                        hasFail = "true";
                    } else {
                        hasFail = "toast";
                    }
                }
            }
        };

        localDao.getDaoSession().runInTx(runner);
        if (runner.toString().equals("true")) {
            throw new JSONException("There is an error parsing pharma list");
        } else if (runner.toString().equals("toast")) {
            throw new JSONException("Toast");
        }
    }

    private List<Region> cacheRegionList() throws JSONException,
            TimeoutException {
        final RegionJsonHelper regionDao = new RegionJsonHelper();
        final String jsonArrayResponse = regionDao.invokeDatastream(null, null);
        if (jsonArrayResponse == null) {
            throw new TimeoutException("There is not internet conection");
        }
        JSONArray jArray = new JSONObject(jsonArrayResponse)
                .getJSONArray("result");

        if (jArray.length() > 0) {
            return regionDao.parseJsonArrayResponse(jsonArrayResponse
                    .toString());
        } else {
            throw new JSONException("There is an error parsing regions list");
        }
    }

    private List<Commune> cacheComuneList() throws JSONException,
            TimeoutException {
        CommuneJsonHelper communeDao = new CommuneJsonHelper();
        final String jsonArrayResponse = communeDao
                .invokeDatastream(null, null);
        if (jsonArrayResponse == null) {
            throw new TimeoutException("There is not internet conection");
        }
        JSONArray jArray = new JSONObject(jsonArrayResponse)
                .getJSONArray("result");

        if (jArray.length() > 0) {
            return communeDao.parseJsonArrayResponse(jsonArrayResponse
                    .toString());
        } else {
            throw new JSONException("There is an error parsing commune list");
        }
    }

    private boolean isDiferentSyncDay() {
        if (!pref.getString(KEY_DAY, "")
                .equals(getIntegerDate(DATE_FORMAT_DAY))
                || !pref.getString(KEY_MONTH, "").equals(
                        getIntegerDate(DATE_FORMAT_MONTH))) {
            return true;
        }
        return false;
    }

    private void saveSyncDay() {
        editor.putString(KEY_DAY, getIntegerDate(DATE_FORMAT_DAY));
        editor.putString(KEY_MONTH, getIntegerDate(DATE_FORMAT_MONTH));
        editor.putString(KEY_YEAR, getIntegerDate(DATE_FORMAT_YEAR));
        editor.commit();
    }

    @SuppressLint("SimpleDateFormat")
    private String getIntegerDate(String date_format) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(date_format);
        return sdf.format(calendar.getTime());
    }
}