package com.example.edgedetectionapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.edgedetectionapp.databinding.ActivityMainBinding;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.Collections;
import java.util.List;




public class MainActivity extends CameraActivity {

    static {
        System.loadLibrary("edgedetectionapp");
    }



    private CameraBridgeViewBase cameraBridgeViewBase;
    private Button select, camera;
    private ImageView imageView;
    private Bitmap bitmap;
    private Mat mat;
    private final int SELECT_CODE = 100, CAMERA_CODE = 101;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cameraBridgeViewBase = findViewById(R.id.cameraView);
        cameraBridgeViewBase.setVisibility(CameraBridgeViewBase.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {}

            @Override
            public void onCameraViewStopped() {}

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                Mat rgba = inputFrame.rgba();
                Mat gray = new Mat();
                Mat edges = new Mat();

                Imgproc.cvtColor(rgba, gray, Imgproc.COLOR_RGBA2GRAY);


                Imgproc.Canny(gray, edges, 80, 100);
                Core.bitwise_not(gray,gray);

                Imgproc.cvtColor(edges, rgba, Imgproc.COLOR_GRAY2RGBA);

                return rgba;
            }
        });

        select = findViewById(R.id.select);
        camera = findViewById(R.id.camera);
        imageView = findViewById(R.id.imageView);

        select.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_CODE);
        });

        camera.setOnClickListener(view -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_CODE);
        });

        getPermission(); // Ensure permission before starting camera
    }

    private void startCameraView() {
        if (OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "OpenCV initialized");
            cameraBridgeViewBase.enableView();
        } else {
            Log.e("OpenCV", "OpenCV initialization failed");
        }
    }

    private void getPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 102);
        } else {
            startCameraView(); // Permission already granted
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 102 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCameraView();
        } else {
            Log.e("Permission", "Camera permission denied");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_CODE && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
                mat = new Mat();
                Utils.bitmapToMat(bitmap, mat);
                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
                Utils.matToBitmap(mat, bitmap);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CAMERA_CODE && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY);
            Utils.matToBitmap(mat, bitmap);
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraBridgeViewBase);
    }

    public native String stringFromJNI();
}



