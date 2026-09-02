package com.example.ballbotbrain;

import android.content.Context;
import android.util.Log;

public class BalanceDaemon implements Runnable, IMU.IMUListener {

    IMU imu;
    PDFController pdfController;
    MotorMixer motorMixer;

    // send data back to MainActivity
    public interface UiUpdateListener {
        void balanceDaemonUpdate(float pitch, float roll, float yaw, float yawR, float tiltHeading, float tiltMagnitude, float tiltRadPerSec, float pdfOutput, int[] motorMixerOutput);
    }
    UiUpdateListener uiUpdateListener;

    private volatile boolean running = false; // Volatile so not to mistakenly run when said not to
    private Thread thread;
    private long lastUiUpdateTime = 0;

    private volatile float pitch, roll, yaw, yawR, tiltHeading, tiltMagnitude, tiltRadPerSec;

    @Override
    public void onOrientationChanged(float pitch, float roll, float yaw, float yawR, float tiltHeading, float tiltMagnitude, float tiltRadPerSec) {
        this.pitch = pitch;
        this.roll = roll;
        this.yaw = yaw;
        this.yawR = yawR;
        this.tiltHeading = tiltHeading;
        this.tiltMagnitude = tiltMagnitude;
        this.tiltRadPerSec = tiltRadPerSec;
    }

    public BalanceDaemon(Context context) {
        this.imu = new IMU(context);
        this.imu.setListener(this);
        this.pdfController = new PDFController();
        this.motorMixer = new MotorMixer();
    }

    public void start() {
        running = true;

        thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY); // High priority for fast loops
        thread.setDaemon(true); // daemon thread because is running in background continuously
        thread.start();
        Log.d(getClass().getSimpleName(),"Thread Started" );

        imu.startListening();
    }

    @Override
    public void run() {
        while (running) {
            // Use snapshot of volatile values in this loop
            float magnitude = tiltMagnitude;
            float rateOfRotation = tiltRadPerSec;
            float heading = tiltHeading;

            float pdfOutput = pdfController.output(magnitude, rateOfRotation);
            int[] motorMixerOutput = motorMixer.output(heading, pdfOutput);

            // pass data back to the UI Thread every 200ms (5 times a second)
            long currentTime = System.currentTimeMillis();
            if (uiUpdateListener != null & currentTime - lastUiUpdateTime >= 200) {
                uiUpdateListener.balanceDaemonUpdate(pitch, roll, yaw, yawR, heading, magnitude, rateOfRotation, pdfOutput, motorMixerOutput);
                lastUiUpdateTime = currentTime;
            }

            // Enforce timing delay
            try {
                Thread.sleep(4); // 4ms break speed (tweak later)
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stop() {
        running = false;
        imu.stopListening();
        thread.interrupt();
        Log.d(getClass().getSimpleName(),"Thread Ended" );
    }

    public void setUIUpdateListener(UiUpdateListener listener) {this.uiUpdateListener = listener;}
}