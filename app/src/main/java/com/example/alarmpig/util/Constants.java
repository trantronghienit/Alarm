package com.example.alarmpig.util;

import java.util.Calendar;

import okhttp3.MediaType;

public final class Constants {
    public static final String ACTION_VOLUME_CHANGE = "android.media.VOLUME_CHANGED_ACTION";
    public static final String DATABASE_NAME = "alarm_db";
    public static final String PARCELABLE_DATA = "parcelable_data";
    public static final String API_HOST = "https://trantronghienit.github.io";
    public static final String INFO = "alarm_info";
    public static final String ERROR = "alarm_error";
    public static final String SERVICE = "alarm_service";
    public static final String RECEIVER = "alarm_receiver";
    public static final String FILE = "alarm_file";
    public static final String KEY = "alarm_key";
    public static final int TIME_DEFAULT = 7;

    public static final int MON = Calendar.MONDAY;
    public static final int TUES = Calendar.TUESDAY;
    public static final int WED = Calendar.WEDNESDAY;
    public static final int THURS = Calendar.THURSDAY;
    public static final int FRI = Calendar.FRIDAY;
    public static final int SAT = Calendar.SATURDAY;
    public static final int SUN = Calendar.SUNDAY;

    public static final String KEY_MON = "mon";
    public static final String KEY_TUES = "tue";
    public static final String KEY_WED = "wed";
    public static final String KEY_THURS = "thu";
    public static final String KEY_FRI = "fri";
    public static final String KEY_SAT = "sat";
    public static final String KEY_SUN = "sun";

    public static final String KEY_IS_CHANGE = "is_change";
    public static final String KEY_TYPE = "key_type";
    public static final String HOUR = "key_hour";
    public static final String MINUTE = "key_minute";
    public static final String DAY = "key_day";

    public static final int EDIT_RESULT_CODE = 102;
    public static final int EDIT_TYPE = 1;
    public static final int DEL_TYPE = 2;
    public static final String KEY_ALARM_ID = "key_alarm_id";
    public static final int ADD_REQUEST_CODE = 103;
    public static final int ADD_TYPE = 3;

    public static final int TYPE_ALARM = 1;
    public static final int TYPE_NOTIFICATION = 2;

    public static final String ALARM_FIREBASE_REF = "alarm";
    public static final String SECRET_KEY = "secret_key";
    public static final String TOTP_CODE_KEY = "totp_code_key";
    public static final String ALGORITHM = "AES";
    public static final String DIR_APP = "alarm_pig";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String HOST_TOKEN_FIREBASE = "https://alarmpig-db749.firebaseio.com/token.json";
    public static final String TOKEN = "token";
    public static final boolean ENABLE_MAX_VOLUME = false;
    public static final String TAG_ALARM_WORKER = "alarm_worker";
//    public static final String ALGORITHM = "HmacSHA1";
}
