package com.fsdm.wisd.smartdoor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.projectoxford.face.FaceServiceRestClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText username;
    Button Add;
    PersonAdder adder = new PersonAdder(this);
    ArrayList<Bitmap> identifiedBitmaps = new ArrayList<Bitmap>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Add = findViewById(R.id.add_b);
        username = findViewById(R.id.PersonName);

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!identifiedBitmaps.isEmpty())
                identifiedBitmaps.clear();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 110);
                }
                getPickImageIntent();

            }
        });

    }


    public void getPickImageIntent(){
        if(username.getText().toString().isEmpty()) {
            Toast.makeText(this,"Please type the person name",Toast.LENGTH_LONG).show();
            return;
        }
        if(PersonAdder.cameraIndex < 5) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 101);
            PersonAdder.cameraIndex++;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == RESULT_OK){
            if(PersonAdder.cameraIndex==5)
            {
                PersonGroupData personGroupData = new PersonGroupData(MainActivity.this, "2021", "SmartDoorUsers", username.getText().toString() , identifiedBitmaps);
                PersonAdder.add.execute(personGroupData);
                PersonAdder.cameraIndex = 0;
                Toast.makeText(this,"The person is added successfully",Toast.LENGTH_LONG).show();
                return;
            }
            identifiedBitmaps.add((Bitmap) data.getExtras().get("data"));
            getPickImageIntent();

        }

    }
}