package edu.gatech.cc.eatsafe;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.wonderkiln.camerakit.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CameraActivity extends AppCompatActivity {
    @BindView(R.id.camView) CameraView mCameraView;
    @BindView(R.id.camera_button) ImageButton mCameraButton;

    //Firebase Barcode Detector
    private FirebaseVisionBarcodeDetectorOptions barcodeDetectorOptions;
    private FirebaseVisionBarcodeDetector barcodeDetector;

    // Firebase Databases
    private DatabaseReference database;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("users");

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraView.captureImage();
            }
        });

        //Set up barcode detector
        barcodeDetectorOptions =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_UPC_A,
                                FirebaseVisionBarcode.FORMAT_UPC_E,
                                FirebaseVisionBarcode.FORMAT_EAN_13,
                                FirebaseVisionBarcode.FORMAT_EAN_8,
                                FirebaseVisionBarcode.FORMAT_CODE_128,
                                FirebaseVisionBarcode.FORMAT_ITF,
                                FirebaseVisionBarcode.FORMAT_CODE_39)
                        .build();
        barcodeDetector = FirebaseVision.getInstance().getVisionBarcodeDetector(barcodeDetectorOptions);

        mCameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                Bitmap bitmap = cameraKitImage.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, mCameraView.getWidth(), mCameraView.getHeight(), false);
                scanBarcode(FirebaseVisionImage.fromBitmap(bitmap));
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        mCameraView.start();
    }

    @Override
    public void onPause() {
        mCameraView.stop();
        super.onPause();
    }

    private void scanBarcode(FirebaseVisionImage image) {
        Task<List<FirebaseVisionBarcode>> result = barcodeDetector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                        try {
                            barcodeDetector.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        handleBarcode(firebaseVisionBarcodes);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CameraActivity.this,
                                "Failed to Read Barcode!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleBarcode(List<FirebaseVisionBarcode>  barcodes) {
        for (FirebaseVisionBarcode barcode : barcodes) {
            //Ensure this is a product barcode
            int valueType = barcode.getValueType();
            if (valueType != FirebaseVisionBarcode.TYPE_PRODUCT) {
                Toast.makeText(CameraActivity.this,
                        "Incorrect Barcode Type!: " + valueType,
                        Toast.LENGTH_LONG).show();
            } else {
                // Initialize Nutrition API library
                final GetNutrition api = GetNutrition.getInstance(this);
                String value = barcode.getRawValue();

                // Initial API call to get database reference number
                api.getFoodByKeyword(value, 1, new NutritionCallback() {
                    @Override
                    public void onSuccess(JSONObject result) throws JSONException {
                        // Request unsuccessful, alert user
                        if (result.has("errors")) {
                            Toast.makeText(CameraActivity.this,
                                    "Product not found!",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        String id = result.getJSONObject("list").getJSONArray("item")
                                .getJSONObject(0).getString("ndbno");

                        // Second API call to get detailed results
                        api.getFoodFacts(Integer.parseInt(id), new NutritionCallback() {
                            @Override
                            public void onSuccess(JSONObject result) throws JSONException {
                                // Get ingredients from refined search
                                String ingredients = result.getJSONArray("foods").getJSONObject(0)
                                        .getJSONObject("food").getJSONObject("ing").getString("desc");
                                handleIngredients(ingredients);
                            }

                        });
                    }
                });
            }
        }
    }

    private void handleIngredients(String ingredients) {
        // Strings to find allergies
        String[] dairyStrings = {"MILK", "CHEESE", "YOGURT", "CREAM"};
        String[] fishStrings = {"FISH", "SALMON", "TILAPIA", "HALIBUT", "COD"};
        String[] glutenStrings = {"GLUTEN", "WHEAT", "GRAIN", "FLOUR", "BREAD", "YEAST", "MEAT SUBSTITUTE"};
        String[] peanutStrings = {"PEANUT"};
        String[] shellfishStrings = {"SHELLFISH", "SHRIMP", "LOBSTER", "CRAB", "CRAWFISH", "CLAM", "OYSTER", "SCALLOP", "MUSSEL"};
        String[] soyStrings = {"SOY", "SOYBEAN"};
        String[] treenutStrings = {"ALMOND", "CASHEW", "HAZELNUT", "PECAN", "PISTACHIO", "MACADAMIA", "CHESTNUT", "PINE", "WALNUTS", "SHEA"};

        // Check if allergen is in ingredient string
        boolean dairy = Arrays.stream(dairyStrings).parallel().anyMatch(ingredients::contains);
        boolean fish = Arrays.stream(fishStrings).parallel().anyMatch(ingredients::contains);
        boolean gluten = Arrays.stream(glutenStrings).parallel().anyMatch(ingredients::contains);
        boolean peanuts = Arrays.stream(peanutStrings).parallel().anyMatch(ingredients::contains);
        boolean shellfish = Arrays.stream(shellfishStrings).parallel().anyMatch(ingredients::contains);
        boolean soy = Arrays.stream(soyStrings).parallel().anyMatch(ingredients::contains);
        boolean treenuts = Arrays.stream(treenutStrings).parallel().anyMatch(ingredients::contains);

        // Report existing allergies if user or friends have them listed
        database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserInformation queryUser = dataSnapshot.child(auth.getCurrentUser().getUid()).getValue(UserInformation.class);
                    Map<String, Boolean> allergens = queryUser.getAllergens();
                    Map<String, String> friendMap =  new HashMap<>();
                    for (DataSnapshot user : dataSnapshot.getChildren()) {
                        UserInformation friend = user.getValue(UserInformation.class);

                        // Skip yourself
                        if (friend.getEmail().equals(auth.getCurrentUser().getEmail())) {
                            continue;
                        }

                        // Add friends allergies
                        if (queryUser.getFriends().contains(friend.getEmail())) {
                            allergens.keySet().forEach(key -> {
                                boolean allergic = friend.getAllergens().get(key);
                                allergens.put(key, allergic);

                                // Record which friends are allergic to what
                                if (allergic) {
                                    friendMap.put(key, friend.getEmail());
                                }
                            });
                        }
                    }

                    ArrayList<String> alertAllergies = new ArrayList<>();
                    if (dairy && allergens.get("dairy")) {
                        alertAllergies.add("Dairy");
                    }
                    if (fish && allergens.get("fish")) {
                        alertAllergies.add("Fish");
                    }
                    if (gluten && allergens.get("gluten")) {
                        alertAllergies.add("Gluten");
                    }
                    if (peanuts && allergens.get("peanuts")) {
                        alertAllergies.add("Peanuts");
                    }
                    if (shellfish && allergens.get("shellfish")) {
                        alertAllergies.add("Shellfish");
                    }
                    if (soy && allergens.get("soy")) {
                        alertAllergies.add("Soy");
                    }
                    if (treenuts && allergens.get("treenuts")) {
                        alertAllergies.add("Treenuts");
                    }

                    // Alert relevant detected allergies
                    ArrayList<String> friends = new ArrayList<>(new HashSet<>(friendMap.values()));
                    String title;
                    if (alertAllergies.isEmpty() && friends.isEmpty()) {
                        title = "No Allergies Detected!";
                    } else {
                       title = "Allergies Detected";
                       friends.add(0, "Friends with these allergies: ");
                       alertAllergies.addAll(friends);
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                    builder.setTitle(title)
                            .setItems(alertAllergies.toArray(new CharSequence[alertAllergies.size()]),null)
                            .create().show();
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
