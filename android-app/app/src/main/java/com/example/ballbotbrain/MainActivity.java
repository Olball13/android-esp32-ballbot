package com.example.ballbotbrain;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity implements IMU.IMUListener {

    IMU imu;
    PDFController pdfController;
    TextView pitchInput, rollInput, yawInput, tiltHeadingInput, tiltMagnitudeInput, tiltRoRInput, kpInput, kdInput, kfInput;
    Slider kpSlider, kdSlider, kfSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        imu = new IMU(this);
        imu.setListener(this); // Register MainActivity to receive the updates
        pdfController = new PDFController();

        pitchInput = findViewById(R.id.pitchInput);
        rollInput = findViewById(R.id.rollInput);
        yawInput = findViewById(R.id.yawInput);
        tiltHeadingInput = findViewById(R.id.tiltHeadingInput);
        tiltMagnitudeInput = findViewById(R.id.tiltMagnitudeInput);
        tiltRoRInput = findViewById(R.id.tiltRoRInput);
        kpInput = findViewById(R.id.kpInput);
        kpSlider = findViewById(R.id.kpSlider);
        kdInput = findViewById(R.id.kdInput);
        kdSlider = findViewById(R.id.kdSlider);
        kfInput = findViewById(R.id.kfInput);
        kfSlider = findViewById(R.id.kfSlider);

        kpSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                pdfController.setKp(value);
                kpInput.setText("Kp: " + value);
            }
        });

        kdSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                pdfController.setKd(value);
                kdInput.setText("Kd: " + value);
            }
        });

        kfSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                pdfController.setKf(value);
                kfInput.setText("Kf: " + value);
            }
        });
    }

    @Override
    public void onOrientationChanged(float pitch, float roll, float yaw, float yawR, float tiltHeading, float tiltMagnitude, float tiltRadPerSec) {
        pitchInput.setText(String.format("Pitch: " + Math.round(Math.toDegrees(pitch)) + " deg"));
        rollInput.setText(String.format("Roll: " + Math.round(Math.toDegrees(roll)) + " deg"));
        yawInput.setText(String.format("Yaw: " + Math.round(Math.toDegrees(yaw)) + " deg"));
        tiltHeadingInput.setText(String.format("Tilt Heading: " + Math.round(Math.toDegrees(tiltHeading)) + " deg"));
        tiltMagnitudeInput.setText(String.format("Tilt Magnitude: " + Math.round(Math.toDegrees(tiltMagnitude)) + " deg"));
        tiltRoRInput.setText(String.format("Tilt Rate of Rotation: " + Math.round(Math.toDegrees(tiltRadPerSec)) + " deg/s"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        imu.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        imu.stopListening();
    }
}