package edu.gatech.cc.eatsafe;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.wonderkiln.camerakit.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    @BindView(R.id.camView) CameraView mCameraView;
    @BindView(R.id.camera_button) ImageButton mCameraButton;

    //Firebase Barcode Detector
    private FirebaseVisionBarcodeDetectorOptions barcodeDetectorOptions;
    private FirebaseVisionBarcodeDetector barcodeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ButterKnife.bind(this);

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
                        Toast toast = Toast.makeText(CameraActivity.this,
                                "Failed to Read Barcode!",
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
    }

    private void handleBarcode(List<FirebaseVisionBarcode>  barcodes) {
        for (FirebaseVisionBarcode barcode : barcodes) {
            //Ensure this is a product barcode
            int valueType = barcode.getValueType();
            if (valueType != FirebaseVisionBarcode.TYPE_PRODUCT) {
                Toast toast = Toast.makeText(CameraActivity.this,
                        "Incorrect Barcode Type!: " + valueType,
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                //Show barcode scan works by printing to screen
                GetNutrition api = GetNutrition.getInstance(this);
                String value = barcode.getRawValue();
                api.getFoodByKeyword(value, 1, new NutritionCallback() {
                    @Override
                    public void onSuccess(JSONObject result) throws JSONException {
                        String name = result.getJSONObject("list").getJSONArray("item")
                                .getJSONObject(0).getString("name");
                        Toast toast = Toast.makeText(CameraActivity.this,
                                "Barcode Scanned!: " + name, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        }
    }

}
