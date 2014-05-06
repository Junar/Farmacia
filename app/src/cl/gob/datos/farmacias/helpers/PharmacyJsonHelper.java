package cl.gob.datos.farmacias.helpers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.junar.api.JunarAPI;
import com.junar.searchpharma.Pharmacy;

public class PharmacyJsonHelper {
    private static final String TAG = PharmacyJsonHelper.class.getSimpleName();
    public static final String DATA_GUID_TURN = "FARMA-DE-TURNO-EN-LINEA";
    public static final String DATA_GUID_NORMAL = "FARMA-EN-CHILE";
    private Date currentDate = null;

    public void getDatastreamInfo(String guid) {
        JunarAPI junar = new JunarAPI();
        junar.info(guid);
    }

    public String invokeDatastream(String guid) {
        JunarAPI junar = new JunarAPI();
        return junar.invoke(guid, null, null, -1, -1, -1);
    }

    public String invokeDatastream(String guid, String[] arguments,
            String[] filters, int limit, int page, long timestamp) {
        JunarAPI junar = new JunarAPI();
        return junar.invoke(guid, arguments, filters, limit, page, timestamp);
    }

    public List<Pharmacy> parseJsonArrayResponse(String response, String guid)
            throws JSONException {
        /**
         * { "tags":[], "id":"LISTA-FARMA-DE-TURNO-2", "result": [
         * ["región","comuna"
         * ,"nombre farmacia","dirección","día","mes","horario atención"
         * ,"latitud","longitud","teléfono"],
         * ["2","2101","Cruz Verde 222","Arturo Pratt 640"
         * ,"1","7","DESDE 8:00 AM. HASTA LAS 8:00 DEL DÍA SIGUIENTE"
         * ,"-23.6491776","-70.3963394",""]] }
         */
        List<Pharmacy> pharmaList = new ArrayList<Pharmacy>();
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray resultArray = jsonResponse.getJSONArray("result");
        currentDate = Calendar.getInstance(TimeZone.getDefault()).getTime();

        for (int i = 1; i < resultArray.length(); i++) {
            JSONArray columnArray = resultArray.getJSONArray(i);
            Pharmacy pharma = getPharmacyFromJson(columnArray, guid);
            pharmaList.add(pharma);
        }
        return pharmaList;
    }

    public Pharmacy getPharmacyFromJson(JSONArray json, String guid) {
        Pharmacy pharma = new Pharmacy();
        try {
            /**
             * ["región","comuna","nombre farmacia","dirección","día","mes",
             * "horario atención","latitud","longitud","teléfono"],
             * ["2","2101","Cruz Verde 222","Arturo Pratt 640","1","7",
             * "DESDE 8:00 AM. HASTA LAS 8:00 DEL DÍA SIGUIENTE"
             * ,"-23.6491776","-70.3963394",""],
             */

            pharma.setRegion(json.getString(0));
            pharma.setCommune(json.getString(1));
            pharma.setAddress(json.getString(2));
            pharma.setName(json.getString(3));
            pharma.setOpenFrom(json.getString(4));
            pharma.setOpenTo(json.getString(5));
            pharma.setPhone(json.getString(8));
            pharma.setSyncroDate(currentDate);
            if (guid.equals(PharmacyJsonHelper.DATA_GUID_TURN)) {
                pharma.setType("T");
            } else {
                pharma.setType("N");
            }

            try {
                pharma.setLatitude(json.getDouble(6));
                pharma.setLongitude(json.getDouble(7));
            } catch (JSONException jee) {
                pharma.setLatitude(0.0d);
                pharma.setLongitude(0.0d);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return pharma;
    }

    public void getMarkersForPharmas(List<Pharmacy> pharmaList) {

    }
}