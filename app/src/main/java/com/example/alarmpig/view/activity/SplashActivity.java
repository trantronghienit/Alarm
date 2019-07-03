package com.example.alarmpig.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.alarmpig.BuildConfig;
import com.example.alarmpig.R;
import com.example.alarmpig.callback.OnGetConfigInfoListener;
import com.example.alarmpig.callback.OnImportTokenListener;
import com.example.alarmpig.file.FileManager;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.model.ConfigAlarm;
import com.example.alarmpig.model.FirebaseInfoDevice;
import com.example.alarmpig.repository.AlarmRepository;
import com.example.alarmpig.util.AlarmUtils;
import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.LogUtils;
import com.example.alarmpig.util.SharedPrefs;
import com.example.alarmpig.util.UtilHelper;
import com.example.totp_client.TotpToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import trantronghien.io.com.TimeBasedOneTimePasswordGenerator;

public class SplashActivity extends BaseActivity implements FileManager.OnDownloadFileListener {

    private Button btnUnlock;
    private EditText edtInputCode;
    private ProgressDialog progress;
    private String mTOTPCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        inIt();
//        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);
        loadConfig();

        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidCode()) {
                    UtilHelper.switchActivity(SplashActivity.this, MainActivity.class);
                } else {
                    generateTOTPCode();
                    Toast.makeText(SplashActivity.this, "code not valid", Toast.LENGTH_SHORT).show();
                }

            }
        });
        checkPermission();

        pushTokenFMCToFirebase();
    }

    private void pushTokenFMCToFirebase() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            LogUtils.k("getInstanceId failed " + task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        final String token = task.getResult().getToken();
                        if (!TextUtils.isEmpty(token)){
                            AlarmRepository.getInstance().pushTokenNotification(token, new OnImportTokenListener() {
                                @Override
                                public void onImportTokenSuccess() {
                                    LogUtils.k("token push firebase Success: " + token);
                                }

                                @Override
                                public void onImportTokenFailed(String message) {
                                    LogUtils.k("token push firebase fail: " + message);
//                                    SharedPrefs.getInstance().put(Constants.TOKEN , token);
                                }
                            });
                        }else {
                            LogUtils.k("token not init");
                        }
                    }
                });

    }

    private void loadConfig() {
        AlarmRepository.getInstance().getConfig(new OnGetConfigInfoListener() {
            @Override
            public void onSuccess(ConfigAlarm config) {
                if (!config.getVersionCheck().getVersion().equals(BuildConfig.VERSION_NAME)) {
                    updateApp(config.getVersionCheck().getLink());
                }
                if (config.isChange()) {
                    // save change
                    Toast.makeText(SplashActivity.this, "Cập nhật config...", Toast.LENGTH_SHORT).show();
                    SharedPrefs.getInstance().put(Constants.KEY_IS_CHANGE, config.versionChange());
                    LogUtils.i("version change " + config.versionChange());
                    saveAlarmConfig(config.getAlramConfig());
                }
            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void inIt() {
        btnUnlock = findViewById(R.id.btnUnlock);
        edtInputCode = findViewById(R.id.edtInputCode);
        progress = new ProgressDialog(this);
        progress.setMessage("Đang cập nhật...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setMax(100);

        generateTOTPCode();
    }

    // https://totp.danhersam.com/
    private void generateTOTPCode() {
//        TimeBasedOneTimePasswordGenerator totp = null;
//        String secretKeyStringSharedPrefs = SharedPrefs.getInstance().get(Constants.SECRET_KEY, String.class);
//        try {
//            totp = new TimeBasedOneTimePasswordGenerator(60L, TimeUnit.SECONDS, 6, Constants.ALGORITHM);
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        SecretKey key = null;
//        if (TextUtils.isEmpty(secretKeyStringSharedPrefs)) {
//            // get base64 encoded version of the key
//            final KeyGenerator keyGenerator;
//            try {
////                keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
//                keyGenerator = KeyGenerator.getInstance(Constants.ALGORITHM);
//                keyGenerator.init(512);
//
//                key = keyGenerator.generateKey();
//
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
//            // todo:  thay đổi thuật toán mã hóa
//            final String stringKey = UtilHelper.removeSpecial(UtilHelper.encodeAESKeyToBase64(key));
//            // push key to firebase success then save key local
//            FirebaseInfoDevice infoDevice = new FirebaseInfoDevice();
//            infoDevice.deviceName = android.os.Build.MODEL;
//            infoDevice.keyTOTP = stringKey;
//            UtilHelper.saveInfoAppInFirebase(infoDevice, new DatabaseReference.CompletionListener() {
//                @Override
//                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                    SharedPrefs.getInstance().put(Constants.SECRET_KEY, stringKey);
//                    if (databaseError != null) {
//                        LogUtils.e("save firebase error " + databaseError.getMessage());
//                    } else {
//                        LogUtils.k("save akey local " + stringKey);
//                    }
//
//                }
//            });
//            LogUtils.k("push to firebase key " + stringKey);
//
//        }
//        final Date now = new Date();
//        LogUtils.k("save secretKeyString local " + secretKeyStringSharedPrefs);
//        try {
//            key = UtilHelper.decodeBase64ToAESKey(secretKeyStringSharedPrefs);
//            mTOTPCode = String.valueOf(totp.generateOneTimePassword(key, now));
//        } catch (InvalidKeyException | NullPointerException e) {
//            e.printStackTrace();
//        }
        String key = UtilHelper.generateStringAESKey(256);
        TotpToken totpToken = new TotpToken("8927589235723952830581209535534",30 , 6);
        LogUtils.k("key sectec: " + key);
        FirebaseInfoDevice infoDevice = new FirebaseInfoDevice();
        infoDevice.deviceName = android.os.Build.MODEL;
        infoDevice.keyTOTP =  totpToken.generateOtp();
        infoDevice.totp = mTOTPCode;
        UtilHelper.saveInfoAppInFirebase(infoDevice, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                SharedPrefs.getInstance().put(Constants.TOTP_CODE_KEY, mTOTPCode);
                if (databaseError != null) {
                    LogUtils.e("save mTOTPCode error " + databaseError.getMessage());
                } else {
                    LogUtils.k("save mTOTPCode local " + mTOTPCode);
                }

            }
        });
        LogUtils.k("push to mTOTPCode key " + mTOTPCode);

    }

    private void updateApp(String link) {
        new FileManager(this, "alarm_pig").downloadFile(link, this);
    }

    private void saveAlarmConfig(ConfigAlarm.AlarmConfig alramConfig) {
        String alarm = alramConfig.getTimeAlarm();
        AlarmModel model = new AlarmModel();
        model.hour = AlarmUtils.getTimeFormString(alarm, 0);
        model.minute = AlarmUtils.getTimeFormString(alarm, 1);
        model.second = 0;
        model.active = true;
        model.label = "online";
        model.message = "get alarm info for internet";

        final Calendar time = Calendar.getInstance();
        time.set(Calendar.MINUTE, model.minute);
        time.set(Calendar.HOUR_OF_DAY, model.hour);

        AlarmUtils.getDayOfWeek(model.days, alramConfig.getDaysOfWeek());
        model.convertDays();
        LogUtils.i("db insert " + model.toString());
        appDatabase.AlarmDAO().insertOnlySingleAlarm(model);
    }

    private boolean isValidCode() {
        if (TextUtils.isEmpty(mTOTPCode)) {
            return false;
        }
        String input = edtInputCode.getText().toString();
        LogUtils.k("check code TOTPCode " + mTOTPCode);
        return input.equalsIgnoreCase(mTOTPCode);
    }

    @Override
    public void onPrepare() {
        progress.setProgress(0);
        progress.show();
        Toast.makeText(this, "Prepare download", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgress(int Progress) {
        progress.setProgress(Progress);
    }

    @Override
    public void onCompleted(String fileName) {
        progress.dismiss();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() +
                        ".provider", new File(fileName));
                List<ResolveInfo> resInfoList = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    this.getApplicationContext().grantUriPermission(packageName, uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            } else {
                uri = Uri.fromFile(new File(fileName));
            }
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            this.startActivity(intent);

        } catch (Exception ex) {
            Toast.makeText(this, "Update version error!",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onError(String message) {
        progress.dismiss();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == 101)
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Go to settings")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            gotoSetting();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
            Dexter.withActivity(SplashActivity.this)
                    .withPermission(permissions[0])
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) { }
                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) { }
                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) { }
                    })
                    .check();
        }
    }

    private void gotoSetting(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void checkPermission(){
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (!report.areAllPermissionsGranted()) {
                            List<PermissionDeniedResponse> denieds = report.getDeniedPermissionResponses();
                            for (PermissionDeniedResponse permission : denieds) {
                                if (permission.isPermanentlyDenied()) {
                                    Dexter.withActivity(SplashActivity.this)
                                            .withPermission(permission.getPermissionName())
                                            .withListener(new PermissionListener() {
                                                @Override
                                                public void onPermissionGranted(PermissionGrantedResponse response) {

                                                }

                                                @Override
                                                public void onPermissionDenied(PermissionDeniedResponse response) {

                                                }

                                                @Override
                                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                                                }
                                            })
                                            .check();
                                }
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                })
                .check();
    }
}
