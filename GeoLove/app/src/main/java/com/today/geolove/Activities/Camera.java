package com.today.geolove.Activities;

import android.app.AlertDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.camerakit.CameraKitView;
import com.camerakit.type.CameraSize;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import com.today.geolove.Helper.GraphicOverlay;
import com.today.geolove.Helper.ReactOverlay;
import com.today.geolove.R;


import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class Camera extends AppCompatActivity {
    private Button btn;
    private GraphicOverlay graphicOverlay;
    private CameraKitView cameraKitView;


    private boolean validate;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        btn = (Button) findViewById(R.id.btn);
        graphicOverlay = (GraphicOverlay) findViewById(R.id.overlay);
        cameraKitView = (CameraKitView) findViewById(R.id.camera);


        alertDialog = new SpotsDialog.Builder().setContext(Camera.this).setMessage("espere...").setCancelable(false).build();

        graphicOverlay.clear();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //cameraKitView.onStart();


                    cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                        @Override
                        public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                            alertDialog.show();
                            processFaceDetetion(capturedImage,cameraKitView);
                            cameraKitView.onStop();

                        }
                    });



            }
        });






    }

    private void processFaceDetetion(byte[] bitmap, CameraKitView photoResolution) {
        Log.d("process"," entra");
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setWidth(480)   // 480x360 is typically sufficient for
                .setHeight(360)  // image recognition
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setRotation((int)photoResolution.getRotation())
                .build();
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromByteArray(bitmap,metadata);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions = new FirebaseVisionFaceDetectorOptions.Builder().build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector = FirebaseVision.getInstance().getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                getFacesResuts(firebaseVisionFaces);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Camera.this, "error",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getFacesResuts(List<FirebaseVisionFace> firebaseVisionFaces) {
        int count =0;
        for(FirebaseVisionFace face : firebaseVisionFaces){
            Rect rect = face.getBoundingBox();
            ReactOverlay reactOverlay = new ReactOverlay(graphicOverlay,rect);
            graphicOverlay.add(reactOverlay );
            count= count+1;
        }
        alertDialog.dismiss();
        validate=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraKitView.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}