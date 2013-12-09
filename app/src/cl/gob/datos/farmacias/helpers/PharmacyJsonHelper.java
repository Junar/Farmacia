package cl.gob.datos.farmacias.helpers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.junar.api.JunarAPI;
import com.junar.searchpharma.Pharmacy;

public class PharmacyJsonHelper {
    private static final String TAG = PharmacyJsonHelper.class.getSimpleName();
    private String DATA_GUID = "FARMA-SEMES-DIA-Y-MES";

    public void getDatastreamInfo() {
        JunarAPI junar = new JunarAPI();
        junar.info(DATA_GUID);
    }

    public String invokeDatastream(String[] arguments, String[] filters) {
        JunarAPI junar = new JunarAPI();
        return junar.invoke(DATA_GUID, arguments, filters);
    }

    public List<Pharmacy> parseJsonArrayResponse(String response) {
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
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray resultArray = jsonResponse.getJSONArray("result");

            for (int i = 1; i < resultArray.length(); i++) {
                JSONArray columnArray = resultArray.getJSONArray(i);
                Pharmacy pharma = this.getPharmacyFromJson(columnArray);
                pharmaList.add(pharma);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return pharmaList;
    }

    public Pharmacy getPharmacyFromJson(JSONArray json) {
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
            pharma.setName(json.getString(2));
            pharma.setAddress(json.getString(3));
            pharma.setDay(json.getInt(4));
            pharma.setMonth(json.getInt(5));
            pharma.setSchedule(json.getString(6));
            pharma.setPhone(json.getString(9));
            try {
                pharma.setLatitude((float) json.getDouble(7));
                pharma.setLongitude((float) json.getDouble(8));
            } catch (JSONException jee) {
                pharma.setLatitude(Float.valueOf(0));
                pharma.setLongitude(Float.valueOf(0));
            }
            Log.i(TAG, "Pharmacy: ".concat(pharma.toString()));
        } catch (JSONException e) {
            Log.i(TAG, e.getMessage());
        }
        return pharma;
    }

    public void getMarkersForPharmas(List<Pharmacy> pharmaList) {

    }
}