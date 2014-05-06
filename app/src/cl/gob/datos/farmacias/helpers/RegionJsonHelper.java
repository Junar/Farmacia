package cl.gob.datos.farmacias.helpers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.junar.api.JunarAPI;
import com.junar.searchpharma.Region;

public class RegionJsonHelper {
    private static final String TAG = RegionJsonHelper.class.getSimpleName();
    private String DATA_GUID = "CODIG-Y-NOMBR-REGIO-MINSA";

    public List<Region> parseJsonArrayResponse(String response)
            throws JSONException {
        /**
         * {"tags":[],"id":"CODIG-REGIO-Y-NOMBR","result":[["Código de Región",
         * "Nombre de la Región"
         * ],["15","Arica y Parinacota"],["01","Tarapacá"],[
         * "02","Antofagasta"],[
         * "03","Atacama"],["04","Coquimbo"],["05","Valparaíso" ]]]
         */

        List<Region> regionList = new ArrayList<Region>();

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray resultArray = jsonResponse.getJSONArray("result");
        for (int i = 1; i < resultArray.length(); i++) {
            JSONArray columnArray = resultArray.getJSONArray(i);
            Region region = getRegionFromJson(columnArray);
            region.setName(region.getName().replace("REGIÓN DEL ", "")
                    .replace("REGIÓN DE ", "").replace("REGIÓN ", ""));

            if (region.getName().equalsIgnoreCase("ARICA Y PARINACOTA")) {
                region.setCode(1);
            } else if (region.getName().equalsIgnoreCase("TARAPACA")) {
                region.setCode(2);
            } else if (region.getName().equalsIgnoreCase("ANTOFAGASTA")) {
                region.setCode(3);
            } else if (region.getName().equalsIgnoreCase("ATACAMA")) {
                region.setCode(4);
            } else if (region.getName().equalsIgnoreCase("COQUIMBO")) {
                region.setCode(5);
            } else if (region.getName().equalsIgnoreCase("VALPARAISO")) {
                region.setCode(6);
            } else if (region.getName().equalsIgnoreCase("METROPOLITANA")) {
                region.setCode(7);
            } else if (region.getName().equalsIgnoreCase(
                    "LIBERTADOR GENERAL BERNARDO OHIGGINS")) {
                region.setCode(8);
            } else if (region.getName().equalsIgnoreCase("MAULE")) {
                region.setCode(9);
            } else if (region.getName().equalsIgnoreCase("BIO-BIO")) {
                region.setCode(10);
            } else if (region.getName().equalsIgnoreCase("LA ARAUCANIA")) {
                region.setCode(11);
            } else if (region.getName().equalsIgnoreCase("LOS RIOS")) {
                region.setCode(12);
            } else if (region.getName().equalsIgnoreCase("LOS LAGOS")) {
                region.setCode(13);
            } else if (region.getName().equalsIgnoreCase(
                    "AYSEN DEL GENERAL CARLOS IBAÑEZ DEL CAMPO")) {
                region.setCode(14);
            } else if (region.getName().equalsIgnoreCase(
                    "MAGALLANES Y LA ANTARTIDA CHILENA")) {
                region.setCode(15);
            }

            regionList.add(region);
        }

        return regionList;
    }

    private Region getRegionFromJson(JSONArray columnArray) {
        Region region = new Region();
        try {
            // {"id":9,"name":"IX Región"}
            region.setCode(columnArray.getInt(0));
            region.setId(columnArray.getLong(0));
            region.setName(columnArray.getString(1).toUpperCase());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return region;
    }

    public String invokeDatastream(String[] arguments, String[] filters,
            int limit, int page, long timestamp) {
        JunarAPI junar = new JunarAPI();
        return junar.invoke(DATA_GUID, arguments, filters, limit, page,
                timestamp);
    }

    public String invokeDatastream() {
        JunarAPI junar = new JunarAPI();
        return junar.invoke(DATA_GUID, null, null, -1, -1, -1);
    }
}