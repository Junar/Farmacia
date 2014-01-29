package cl.gob.datos.farmacias.helpers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.junar.api.JunarAPI;
import com.junar.searchpharma.Commune;

public class CommuneJsonHelper {
    private static final String TAG = CommuneJsonHelper.class.getSimpleName();
    private String DATA_GUID = "COMUN-Y-NOMBR-MINSA";

    public List<Commune> parseJsonArrayResponse(String response)
            throws JSONException {
        List<Commune> communeList = new ArrayList<Commune>();

        JSONObject jsonResponse = new JSONObject(response);
        JSONArray resultArray = jsonResponse.getJSONArray("result");
        for (int i = 1; i < resultArray.length(); i++) {
            JSONArray columnArray = resultArray.getJSONArray(i);
            Commune commune = getCommuneFromJson(columnArray);
            communeList.add(commune);
        }

        return communeList;
    }

    private Commune getCommuneFromJson(JSONArray json) {
        Commune commune = new Commune();
        try {
            commune.setCode(json.getInt(0));
            commune.setId(json.getLong(0));
            commune.setName(json.getString(1));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return commune;
    }

    public String invokeDatastream(String[] arguments, String[] filters) {
        JunarAPI junar = new JunarAPI();
        return junar.invoke(DATA_GUID, arguments, filters);
    }
}