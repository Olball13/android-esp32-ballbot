package com.example.ballbotbrain;

public class MotorMixer {
    public int[] output(float tiltHeading, float pdfOutput) {
        float pwm1 = (float) (pdfOutput * Math.cos(tiltHeading));
        float pwm2 = (float) (pdfOutput * Math.cos(tiltHeading - Math.toRadians(120)));
        float pwm3 = (float) (pdfOutput * Math.cos(tiltHeading - Math.toRadians(240)));

        float maxAbsPWM = Math.max(Math.abs(pwm1), Math.max(Math.abs(pwm2), Math.abs(pwm3)));
        if(maxAbsPWM > 255) {
            pwm1 = (pwm1 / maxAbsPWM) * 255;
            pwm2 = (pwm2 / maxAbsPWM) * 255;
            pwm3 = (pwm3 / maxAbsPWM) * 255;
        }

        return new int[] {Math.round(pwm1), Math.round(pwm2), Math.round(pwm3)};
    }
}
