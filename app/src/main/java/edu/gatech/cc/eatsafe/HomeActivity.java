package edu.gatech.cc.eatsafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity {
    private View mContentView;
    private View mControlsView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        mControlsView = findViewById(R.id.content_controls);
        mContentView = findViewById(R.id.content_main);

        findViewById(R.id.camera).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(HomeActivity.this,
                                CameraActivity.class));
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
                        startActivity(new Intent(HomeActivity.this,
                                ProfileActivity.class));
                    }
                }
        );

    }

    private void comingSoon() {
        Toast toast = Toast.makeText(HomeActivity.this,
                "Coming Soon!",
                Toast.LENGTH_SHORT);
        toast.show();
    }
}
