package edu.gatech.cc.eatsafe;
import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class GetNutrition {

    private static GetNutrition mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    private JSONObject mResponse;

    private GetNutrition(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized GetNutrition getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new GetNutrition(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public void makeRequest(String url, final NutritionCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    callback.onSuccess(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        addToRequestQueue(request);
    }

    //String ndbno is the number for the food item you are looking to access
    //Pass in a valid number and returned will be the food item FDA nutrition facts
    public void getFoodFacts(int ndbno, final NutritionCallback callback) {
        String url = "https://api.nal.usda.gov/ndb/V2/reports?ndbno=" + ndbno + "&type=b&format=json&api_key=JuFZ2SrrbwMvXCsnW9phBtXLZm59Fq3WVMW6pnnU";
        makeRequest(url, callback);
    }


    //Free Search, just input a keyword and the number of relevent results you want.
    // Can be used to get an nbdno, product names, etc
    public void getFoodByKeyword(String term, int maxNum, final NutritionCallback callback) {
        String url = "https://api.nal.usda.gov/ndb/search/?format=json&q=" + term + "&sort=n&max=" + maxNum + "&offset=0&api_key=JuFZ2SrrbwMvXCsnW9phBtXLZm59Fq3WVMW6pnnU";
        makeRequest(url, callback);
    }

    //Search by food group, for example dairy is 0100, and poultry is 0500
    //This may turn out to be the most useful for food restrictions
    public void getFoodByFoodGroup(int foodGroup, NutritionCallback callback) {
        String url = "https://api.nal.usda.gov/ndb/nutrients/?format=json&api_key=JuFZ2SrrbwMvXCsnW9phBtXLZm59Fq3WVMW6pnnU&nutrients=205&nutrients=204&nutrients=208&nutrients=269&fg=" + foodGroup;
        makeRequest(url, callback);
    }
}
