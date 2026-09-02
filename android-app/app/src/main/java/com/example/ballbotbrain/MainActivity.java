package com.example.ballbotbrain;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity implements BalanceDaemon.UiUpdateListener {

    BalanceDaemon balanceDaemon;
    TextView pitchInput, rollInput, yawInput, tiltHeadingInput, tiltMagnitudeInput, tiltRoRInput, kpInput, kdInput, kfInput, controlInput, pwm1Input, pwm2Input, pwm3Input;
    Slider kpSlider, kdSlider, kfSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        balanceDaemon = new BalanceDaemon(this);
        balanceDaemon.setUIUpdateListener(this); // Register MainActivity to receive the updates

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
        controlInput = findViewById(R.id.controlInput);
        pwm1Input = findViewById(R.id.pwm1Input);
        pwm2Input = findViewById(R.id.pwm2Input);
        pwm3Input = findViewById(R.id.pwm3Input);

        kpSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                balanceDaemon.pdfController.setKp(value);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kpInput.setText("Kp: " + value);
                    }
                });
            }
        });

        kdSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                balanceDaemon.pdfController.setKd(value);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kdInput.setText("Kd: " + value);
                    }
                });
            }
        });

        kfSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                balanceDaemon.pdfController.setKf(value);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        kfInput.setText("Kf: " + value);
                    }
                });
            }
        });
    }

    @Override
    public void balanceDaemonUpdate(float pitch, float roll, float yaw, float yawR, float tiltHeading, float tiltMagnitude, float tiltRadPerSec, float pdfOutput, int[] motorMixerOutput) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pitchInput.setText(String.format("Pitch: " + Math.round(Math.toDegrees(pitch)) + " deg"));
                rollInput.setText(String.format("Roll: " + Math.round(Math.toDegrees(roll)) + " deg"));
                yawInput.setText(String.format("Yaw: " + Math.round(Math.toDegrees(yaw)) + " deg"));
                tiltHeadingInput.setText(String.format("Tilt Heading: " + Math.round(Math.toDegrees(tiltHeading)) + " deg"));
                tiltMagnitudeInput.setText(String.format("Tilt Magnitude: " + Math.round(Math.toDegrees(tiltMagnitude)) + " deg"));
                tiltRoRInput.setText(String.format("Tilt Rate of Rotation: " + Math.round(Math.toDegrees(tiltRadPerSec)) + " deg/s"));
                controlInput.setText(String.format("Control Output: " + (int) pdfOutput));
                pwm1Input.setText(String.format("PWM 1: " + motorMixerOutput[0]));
                pwm2Input.setText(String.format("PWM 2: " + motorMixerOutput[1]));
                pwm3Input.setText(String.format("PWM 3: " + motorMixerOutput[2]));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        balanceDaemon.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        balanceDaemon.stop();
    }
}