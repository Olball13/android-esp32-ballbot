package com.example.ballbotbrain;

import android.content.Context;
import android.util.Log;

public class BalanceDaemon implements Runnable, IMU.IMUListener {

    IMU imu;
    private IMU.IMUListener uiUpdateIMUListener; // send IMU data back to MainActivity
    PDFController pdfController;

    private volatile boolean running = false; // Volatile so not to mistakenly run when said not to
    private Thread thread;

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

            float pdfOutput = pdfController.output(tiltMagnitude, tiltRadPerSec);

            // TODO: Add Inverse Kinematics Logic and USB-OTG data transmission

            // pass back to the UI Thread
            if (uiUpdateIMUListener != null) {
                uiUpdateIMUListener.onOrientationChanged(pitch, roll, yaw, yawR, tiltHeading, tiltMagnitude, tiltRadPerSec);
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

    public void setUIUpdateIMUListener(IMU.IMUListener listener) {this.uiUpdateIMUListener = listener;}
}