package com.example.ballbotbrain;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class IMU implements SensorEventListener {

    // Volatile to update to all systems quickly
    private volatile float pitch, roll, yaw, yawR, tiltHeading, tiltMagnitude, tiltRadPerSec;

    // Custom Listener
    public interface IMUListener {
        void onOrientationChanged( float pitch, float roll, float yaw, float yawR, float tiltHeading, float tiltMagnitude, float tiltRadPerSec);
    }

    float[] rotationMatrix = new float[9];
    float[] orientationAngles = new float[3];

    private final SensorManager sensorManager;
    private Sensor gameRotationVectorSensor;
    private Sensor gyroscopeSensor;
    private IMUListener listener;

    public IMU(Context context) {
        // Get an instance of the SensorManager
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
            // Sensor Available
            // Gyroscope to measure the rate of angular change for the derivative term of the control loop
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR) != null) {
            // Sensor Available
            // Game Rotation Vector doesn't use magnetometer so no Magnetic Distortion
            gameRotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        }

    }

    // Setter for listener
    public void setListener(IMUListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (listener == null) return; // No listener set! Nowhere to update to

        // Check if the sensor that is used is the one that changed
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            // Update rotation rate variables
            yawR = event.values[0];
            float pitchR = event.values[1];
            float rollR = event.values[2];

            if (tiltMagnitude == 0) {
                tiltRadPerSec = 0;
            } else {
                tiltRadPerSec = ((roll * rollR) + (pitch * pitchR))/tiltMagnitude;
            }

            // Send data to the Activity
            listener.onOrientationChanged(pitch, roll, yaw, yawR, tiltHeading, tiltMagnitude, tiltRadPerSec);

        } else if (event.sensor.getType() == Sensor.TYPE_GAME_ROTATION_VECTOR) {

            // Get rotation matrix and convert to orientation angels
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientationAngles);

            // Update rotation variables
            yaw = orientationAngles[0]; // Rotation around Z-axis
            pitch = orientationAngles[1]; // Rotation around X-axis
            roll = orientationAngles[2]; // Rotation around Y-axis

            tiltHeading = (float) Math.atan2(pitch, roll);
            tiltMagnitude = (float) Math.sqrt(Math.pow(pitch, 2) + Math.pow(roll, 2));

            // Send data to the Activity
            listener.onOrientationChanged(pitch, roll, yaw, yawR, tiltHeading, tiltMagnitude, tiltRadPerSec);
        }
    }

    public void startListening() {
        if (gameRotationVectorSensor != null || gyroscopeSensor != null) {
            sensorManager.registerListener(this, gameRotationVectorSensor, SensorManager.SENSOR_DELAY_FASTEST); // Needed for quick corrections
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    public void stopListening() {
        if (gameRotationVectorSensor != null || gyroscopeSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
