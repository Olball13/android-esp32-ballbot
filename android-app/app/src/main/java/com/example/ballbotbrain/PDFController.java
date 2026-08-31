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
        this.kf = kf;
    }

    public float output(float tiltMagnitude, float tiltRadPerSec) {
        float setpoint = 0;

        float error = setpoint + tiltMagnitude; // tilt Magnitude is error
        float pOutput = kp * error;

        // -tiltRadPerSec for Rad/s of error
        float dOutput = kd * -tiltRadPerSec;

        // Using tiltMagnitude radian value to display gravity through a sine input
        float fOutput = (float) (kf * Math.abs(Math.sin(tiltMagnitude)));

        return pOutput + dOutput + fOutput;
    }

}
