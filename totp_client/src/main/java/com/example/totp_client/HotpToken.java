package com.example.totp_client;

import java.lang.reflect.UndeclaredThrowableException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HotpToken implements IToken {

    private String secret;
    private long mEventCount;
    private int mOtpLength;


    private static final int[] DIGITS_POWER
            // 0 1  2   3    4     5      6       7        8
            = {1,10,100,1000,10000,100000,1000000,10000000,100000000};


    public HotpToken(String secret, long eventCount, int otpLength){
        this.secret = secret;
        mEventCount = eventCount;
        mOtpLength = otpLength;
    }

    public int getTimeStep(){
        return 0;
    }

    public void setOtpLength(int otpLength) {
        this.mOtpLength = otpLength;
    }


    public String generateOtp() {

        byte[] counter = new byte[8];
        long movingFactor = mEventCount;

        for(int i = counter.length - 1; i >= 0; i--){
            counter[i] = (byte)(movingFactor & 0xff);
            movingFactor >>= 8;
        }

        byte[] hash = hmacSha(stringToHex(secret), counter);
        int offset = hash[hash.length - 1] & 0xf;

        int otpBinary = ((hash[offset] & 0x7f) << 24)
                |((hash[offset + 1] & 0xff) << 16)
                |((hash[offset + 2] & 0xff) << 8)
                |(hash[offset + 3] & 0xff);

        int otp = otpBinary % DIGITS_POWER[mOtpLength];
        String result = Integer.toString(otp);


        while(result.length() < mOtpLength){
            result = "0" + result;
        }

        return result;
    }

    public static byte[] stringToHex(String hexInputString){

        byte[] bts = new byte[hexInputString.length() / 2];

        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hexInputString.substring(2*i, 2*i+2), 16);
        }

        return bts;
    }

    private byte[] hmacSha(byte[] seed, byte[] counter) {

        try{
            Mac hmacSha1;

            try{
                hmacSha1 = Mac.getInstance("HmacSHA1");
            }catch(NoSuchAlgorithmException ex){
                hmacSha1 = Mac.getInstance("HMAC-SHA-1");
            }

            SecretKeySpec macKey = new SecretKeySpec(seed, "RAW");
            hmacSha1.init(macKey);

            return hmacSha1.doFinal(counter);

        }catch(GeneralSecurityException ex){
            throw new UndeclaredThrowableException(ex);
        }
    }

    /**
     * Generates a new seed value for a token
     * the returned string will contain a randomly generated
     * hex value
     * @param length - defines the length of the new seed this should be either 128 or 160
     * @return
     */
    public static String generateNewSeed(int length){

        String salt = "";
        long ticks = Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTimeInMillis();
        salt = salt + ticks;

        byte[] byteToHash = salt.getBytes();

        MessageDigest md;

        try{
            if(length == 128){
                //128 long
                md = MessageDigest.getInstance("MD5");
            }else{
                //160 long
                md = MessageDigest.getInstance("SHA1");
            }

            md.reset();
            md.update(byteToHash);

            byte[] digest = md.digest();

            //convert to hex string

            return byteArrayToHexString(digest);

        }catch(NoSuchAlgorithmException ex){
            return null;
        }
    }


    public static String byteArrayToHexString(byte[] digest) {

        StringBuffer buffer = new StringBuffer();

        for(int i =0; i < digest.length; i++){
            String hex = Integer.toHexString(0xff & digest[i]);

            if(hex.length() == 1)
                buffer.append("0");

            buffer.append(hex);

        }

        return buffer.toString();
    }


    protected void setEventCount(long eventCount) {
        this.mEventCount = eventCount;
    }
}
