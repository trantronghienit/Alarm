package com.example.totp_client;

public interface IToken {

    String generateOtp();

    int getTimeStep();
}