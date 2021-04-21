package com.fsdm.wisd.smartdoor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.CreatePersonResult;
import com.microsoft.projectoxford.face.contract.TrainingStatus;
import com.microsoft.projectoxford.face.rest.ClientException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class PersonAdder {

    Context context;

    PersonAdder(Context _context){
        context = _context;
        add = new AddPersonGroup();
    }

    private static final String API_ENDPOINT = "https://wisdsmartdoor.cognitiveservices.azure.com/face/v1.0/";
    private static final String API_KEY ="614038ff388d4b5c9fa42b57320b57c9";

    static int cameraIndex = 0;

    private static FaceServiceRestClient faceServiceClient = new FaceServiceRestClient(API_ENDPOINT, API_KEY);

    static AddPersonGroup add;

    class AddPersonGroup extends AsyncTask<PersonGroupData, String, Integer> {

        @Override
        protected Integer doInBackground(PersonGroupData... personGroupData) {
            PersonGroupData person = personGroupData[0];
            if(person == null) return -1;
            try {
                PersonAdder.CreatePersonGroup(person.context,person.PersonGroupId,person.PersonGroupName);
                PersonAdder.AddPersonToGroup(person.context,person.PersonGroupId,person.PersonName,person.bitmaps);
                PersonAdder.TrainingAI(person.context,person.PersonGroupId);
                return 0;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            } catch (ClientException e) {
                e.printStackTrace();
                return -1;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return -1;
            }
        }

    }


    private synchronized static void CreatePersonGroup(Context context, String personGroupID, String personGroupName) throws IOException, ClientException {
        try {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.INTERNET}, 111);
            }
            faceServiceClient.createPersonGroup(personGroupID,personGroupName, null);
            Log.d("FaceAPI","the person group has been created successfully");
        } catch (ClientException | IOException  e) {
            e.printStackTrace();
            Log.d("FaceAPI",e.getMessage());
        }

    }

    private synchronized static void AddPersonToGroup(Context context, String personGroupID, String personName, List<Bitmap> bitmap){

        try {
            faceServiceClient.getPersonGroup(personGroupID);
            CreatePersonResult personResult = faceServiceClient.createPerson(personGroupID,personName,null);
            DetectFaceAndRegister(personGroupID,personResult,bitmap);
            Log.d("FaceAPI","the person has been added to the group successfully");
        } catch (ClientException | IOException  e) {
            e.printStackTrace();
        }

    }

    private synchronized static void DetectFaceAndRegister(String personGroupID, CreatePersonResult personResult, List<Bitmap> bitmaps) throws IOException, ClientException {

        for (Bitmap b : bitmaps){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            faceServiceClient.addPersonFace(personGroupID,personResult.personId,inputStream,null,null);
        }

    }

    private synchronized static void TrainingAI(Context context,String personGroupID) throws IOException, ClientException, InterruptedException {
        faceServiceClient.trainPersonGroup(personGroupID);
        TrainingStatus trainingStatus = null;
        while(true){
            trainingStatus = faceServiceClient.getPersonGroupTrainingStatus(personGroupID);
            if(trainingStatus.status != TrainingStatus.Status.Running){
                Log.d("FaceAPIStatus",trainingStatus.status.name());
                break;
            }
            Thread.sleep(1000);
        }

    }





}
