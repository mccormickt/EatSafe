package edu.gatech.cc.eatsafe;

import org.json.JSONException;
import org.json.JSONObject;

//Interface for retrieving data from Nutrition API
public interface NutritionCallback {
    void onSuccess(JSONObject result) throws JSONException;
}
