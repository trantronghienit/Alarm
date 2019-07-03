package com.example.totp_client;

import java.util.Calendar;
import java.util.TimeZone;

public class TotpToken extends HotpToken {

    private int mTimeStep;

    public TotpToken(String secret, int timeStep, int otpLength) {
        super(secret, 0, otpLength);
        mTimeStep = timeStep;
    }

    @Override
    public int getTimeStep() {
        return mTimeStep;
    }


    @Override
    public String generateOtp() {

        //calculate the moving counter using the time
        return generateOtp(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
    }

    public String generateOtp(Calendar currentTime) {
        long time = currentTime.getTimeInMillis() / 1000;
        super.setEventCount(time / mTimeStep);

        return super.generateOtp();
    }


}