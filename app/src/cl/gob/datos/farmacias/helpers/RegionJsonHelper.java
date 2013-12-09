package cl.gob.datos.farmacias.helpers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.junar.searchpharma.Region;

public class RegionJsonHelper {
    private static final String TAG = RegionJsonHelper.class.getSimpleName();

    public List<Region> parseJsonArrayResponse(String response) {
        // {"regiones":[{"id":2,"name":"II Región"},
        // {"id":3,"name":"III Región"}]}
        List<Region> regionList = new ArrayList<Region>();
        try {
            JSONObject jsonResponse = new JSONObject(response);
            JSONArray resultArray = jsonResponse.getJSONArray("regiones");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject columnArray = resultArray.getJSONObject(i);
                Region region = getRegionFromJson(columnArray);
                regionList.add(region);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        return regionList;
    }

    private Region getRegionFromJson(JSONObject json) {
        Region region = new Region();
        try {
            // {"id":9,"name":"IX Región"}
            region.setCode(json.getInt("id"));
            region.setId(json.getLong("id"));
            region.setName(json.getString("name"));
            Log.d(TAG, "Region: ".concat(region.toString()));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return region;
    }
}