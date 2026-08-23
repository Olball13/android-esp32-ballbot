package com.example.ballbotbrain;

public class PDFController {
    private float
            kp, // Proportional
            kd, // Derivative
            kf; // FeedForward (Gravity)

    public void setKp(float kp) {
        this.kp = kp;
    }

    public void setKd(float kd) {
        this.kd = kd;
    }

    public void setKf(float kf) {
        this.kp = kf;
    }
    public float output(float tiltMagnitude, float tiltRadPerSec) {
        float setpoint = 0;
        float error = setpoint - tiltMagnitude;
        return (float) (kp * error + kd * -tiltRadPerSec + kf * Math.sin(tiltMagnitude)); // -tiltRadPerSec because its Rad/s of error
    }

}
