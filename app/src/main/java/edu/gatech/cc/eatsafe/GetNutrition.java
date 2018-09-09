package edu.gatech.cc.eatsafe;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GetNutrition {
    OkHttpClient client = new OkHttpClient();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //String ndbno is the number for the food item you are looking to access
    //Pass in a valid number and returned will be the food item FDA nutrition facts
    public String getFoodFacts(int ndbno) throws IOException {
        String url = "https://api.nal.usda.gov/ndb/V2/reports?ndbno=" + ndbno + "&type=b&format=json&api_key=JuFZ2SrrbwMvXCsnW9phBtXLZm59Fq3WVMW6pnnU";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //Free Search, just input a keyword and the number of relevent results you want.
    // Can be used to get an nbdno, product names, etc
    public String getFoodByKeyword(String term, int maxNum) throws IOException {
        String url = "https://api.nal.usda.gov/ndb/search/?format=json&q=" + term + "&sort=n&max=" + maxNum + "&offset=0&api_key=JuFZ2SrrbwMvXCsnW9phBtXLZm59Fq3WVMW6pnnU";
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    //Search by food group, for example dairy is 0100, and poultry is 0500
    //This may turn out to be the most useful for food restrictions
    public String getFoodByFoodGroup(int foodGroup) throws IOException {
        String url = "https://api.nal.usda.gov/ndb/nutrients/?format=json&api_key=JuFZ2SrrbwMvXCsnW9phBtXLZm59Fq3WVMW6pnnU&nutrients=205&nutrients=204&nutrients=208&nutrients=269&fg=" + foodGroup;
        Request request = new Request.Builder()
                .url(url)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    };
}
