package com.tomer.anibadi;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.tomer.anibadi.util.AadhaarAnalyser;
import com.tomer.anibadi.util.OverLayView;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;

import kotlin.Unit;


public class CameraHandler extends AppCompatActivity {


    //region GLOBALS---?>>>


    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    private PreviewView previewView;


    private ImageView btCap;
    private ImageView imgPrev;
    private OverLayView overLayView;
    private boolean isCap = false;
    private boolean doFinish = true;
    private Bitmap currentBitmap;

    private String aaNO = "";
    private String dob = "";
    private String[] names;

    private AadhaarAnalyser aadhaarAnalyser;

    //endregion GLOBALS---?>>>


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.trans));
        setContentView(R.layout.cam_handler);


        imgPrev = findViewById(R.id.ch_imgPrev);
        previewView = findViewById(R.id.camXPrev);
        btCap = findViewById(R.id.ch_fab_cap);
        overLayView = findViewById(R.id.overLAy);

        btCap.setOnClickListener((_v) -> takePicture());
        findViewById(R.id.btBackCam).setOnClickListener((_v) -> finish());

        aadhaarAnalyser = new AadhaarAnalyser(((rect, strFromAna, found) -> {
            overLayView.setRect(rect);
            if (found) {
                String[] sts = strFromAna.split(",,,");
                dob = sts[1];
                aaNO = sts[0].replaceAll(" ", "");
                names = new String[sts.length - 2];
                System.arraycopy(sts, 2, names, 0, sts.length - 2);

            }
            return Unit.INSTANCE;
        }), previewView);

//        overLayView.setRect(new Rect(200,200,400,400));

        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);

        overLayView.postDelayed(() -> aadhaarAnalyser.setValidRect(overLayView.getMainRect()), 200);

    }

    private void showImg() {
        runOnUiThread(() -> {
            imgPrev.setImageBitmap(currentBitmap);
            imgPrev.setVisibility(View.VISIBLE);
            previewView.setVisibility(View.INVISIBLE);

            btCap.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.round_done_black_24dp));
            isCap = true;
        });
    }


    //region ACTIVITY LIFECYCLES--->>>

    @Override
    public void onResume() {
        super.onResume();
        previewView.post(this::startCAm);
    }

    @Override

    public void onPause() {
        super.onPause();

        if (doFinish) {
            finish();
        } else {
            if (!isCap) {
                stopCAm();
            }
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.topfadein, R.anim.fadetobottom);
    }

    @Override
    public void onBackPressed() {
        doFinish = false;
        finish();
    }

    //endregion ACTIVITY LIFECYCLES--->>>

    //region HANDLING CAMERA EVENTS START STOP FLIP>>

    private void stopCAm() {
        try {
            cameraProviderListenableFuture.get().unbindAll();
        } catch (ExecutionException | InterruptedException ignored) {
        }
    }

    private void startCAm() {

        //Preview UseCase
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());


        //Analysis UseCase
        ImageAnalysis analysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(previewView.getWidth(), previewView.getHeight()))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        analysis.setAnalyzer(getMainExecutor(), aadhaarAnalyser);

        ProcessCameraProvider processCameraProvider;
        try {
            processCameraProvider = cameraProviderListenableFuture.get();
            processCameraProvider.unbindAll();
            processCameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis);
        } catch (ExecutionException | InterruptedException ignored) {

        }
    }

    //endregion HANDLING CAMERA EVENTS START STOP FLIP>>

    //region TAKING PICTURE AND CORRECT ROTATIONS


    public void takePicture() {

        if (isCap) {

            if (aaNO.isEmpty()) {
                setResult(RESULT_CANCELED);
            } else {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                currentBitmap.compress(Bitmap.CompressFormat.WEBP, 80, outputStream);

                Intent i = new Intent();
                i.putExtra("aano", aaNO);
                i.putExtra("dob", dob);
                i.putExtra("names", names);
                i.putExtra("imageData", outputStream.toByteArray());
                setResult(RESULT_OK, i);
            }
            finish();
            return;
        }

        overLayView.clickPhoto();
        Rect re = overLayView.getMainRect();
        currentBitmap = Bitmap.createBitmap(previewView.getBitmap(), 0, re.top, overLayView.getWidth(), re.height());
        showImg();
        overLayView.postDelayed(this::stopCAm, 200);
        doFinish = false;
    }


    //endregion TAKING PICTURE AND CORRECT ROTATIONS

    //region FULLSCREEN LOGIC

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

            );
        }
    }

    //endregion FULLSCREEN LOGIC

}