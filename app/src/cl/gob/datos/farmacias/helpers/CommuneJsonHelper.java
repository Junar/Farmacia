package cl.gob.datos.farmacias.helpers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.junar.searchpharma.Commune;

public class CommuneJsonHelper {
    private static final String TAG = CommuneJsonHelper.class.getSimpleName();

    public List<Commune> parseJsonArrayResponse(String response) {
        // {"comunas": [{"id":1405,"region_id":2,"name":"Pica"}]}
        List<Commune> communeList = new ArrayList<Commune>();
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray resultArray = jsonResponse.getJSONArray("comunas");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject columnArray = resultArray.getJSONObject(i);
                Commune commune = getCommuneFromJson(columnArray);
                communeList.add(commune);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return communeList;
    }

    private Commune getCommuneFromJson(JSONObject json) {
        Commune commune = new Commune();
        try {
            // {"id":1401,"region_id":2,"name":"Pozo Almonte"},
            commune.setCode(json.getInt("id"));
            commune.setId(json.getLong("id"));
            commune.setRegionCode(json.getInt("region_id"));
            commune.setName(json.getString("name"));
            Log.d(TAG, "Commune: ".concat(commune.toString()));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return commune;
    }
}