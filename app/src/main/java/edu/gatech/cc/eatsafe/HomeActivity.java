package edu.gatech.cc.eatsafe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private View mContentView;
    private View mControlsView;

    //Firebase Barcode Detector
    private FirebaseVisionBarcodeDetectorOptions barcodeDetectorOptions;
    private FirebaseVisionBarcodeDetector barcodeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setContentView(R.layout.activity_home);
        mControlsView = findViewById(R.id.content_controls);
        mContentView = findViewById(R.id.content_main);

        findViewById(R.id.camera).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dispatchTakePictureIntent();
                    }
                }
        );
        findViewById(R.id.friends).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        comingSoon();
                    }
                }
        );
        findViewById(R.id.allergies).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        comingSoon();
                    }
                }
        );
    }

    private static final int REQUEST_TAKE_PHOTO = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File imageFile;
            Uri uri = null;
            FirebaseVisionImage image = null;
            try {
                imageFile = createImageFile();
                Uri.fromFile(imageFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                image = FirebaseVisionImage.fromFilePath(HomeActivity.this, uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            scanBarcode(image);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "edu.gatech.cc.eatsafe",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
                       Toast toast = Toast.makeText(HomeActivity.this,
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
                Toast toast = Toast.makeText(HomeActivity.this,
                        "Incorrect Barcode Type!: " + valueType,
                        Toast.LENGTH_LONG);
                toast.show();
            } else {
                //Show barcode scan works by printing to screen
                Toast toast = Toast.makeText(HomeActivity.this,
                        "Barcode Scanned!: " + barcode.getRawValue(),
                        Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private void comingSoon() {
        Toast toast = Toast.makeText(HomeActivity.this,
                "Coming Soon!",
                Toast.LENGTH_SHORT);
        toast.show();
    }
}
