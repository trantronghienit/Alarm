package com.example.alarmpig.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.alarmpig.BuildConfig;
import com.example.alarmpig.R;
import com.example.alarmpig.callback.OnGetConfigInfoListener;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

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

        inIt();
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);
        loadConfig();

        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidCode()){
                    UtilHelper.switchActivity(SplashActivity.this, MainActivity.class);
                }else {
                    generateTOTPCode();
                    Toast.makeText(SplashActivity.this, "code not valid", Toast.LENGTH_SHORT).show();
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
        TimeBasedOneTimePasswordGenerator totp = null;
        String secretKeyStringSharedPrefs = SharedPrefs.getInstance().get(Constants.SECRET_KEY, String.class);
        try {
            totp = new TimeBasedOneTimePasswordGenerator(60L, TimeUnit.SECONDS , 6 , Constants.ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey key = null;
        if (TextUtils.isEmpty(secretKeyStringSharedPrefs)) {
            // get base64 encoded version of the key
            if (key != null) {
                final KeyGenerator keyGenerator;
                try {
//                keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
                    keyGenerator = KeyGenerator.getInstance(Constants.ALGORITHM);
                    keyGenerator.init(512);

                    key = keyGenerator.generateKey();

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                // todo:  loại bỏ ký tự đặc biệt và khoảng trắng
                final String stringKey = UtilHelper.encodeAESKeyToBase64(key);
                // push key to firebase success then save key local
                FirebaseInfoDevice infoDevice = new FirebaseInfoDevice();
                infoDevice.deviceName = "";
                infoDevice.keyTOTP = stringKey;
                UtilHelper.saveInfoAppInFirebase(infoDevice, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        SharedPrefs.getInstance().put(Constants.SECRET_KEY, stringKey);
                        LogUtils.k("save akey local " + stringKey);
                    }
                });
                LogUtils.k("push to firebase key " + stringKey);
            }
        }
        final Date now = new Date();
        LogUtils.k("save secretKeyString local " + secretKeyStringSharedPrefs);
        try {
            key = UtilHelper.decodeBase64ToAESKey(secretKeyStringSharedPrefs);
            mTOTPCode = String.valueOf(totp.generateOneTimePassword(key, now));
        } catch (InvalidKeyException | NullPointerException e) {
            e.printStackTrace();
        }
        LogUtils.k("save mTOTPCode " + mTOTPCode);
    }

    private void updateApp(String link) {
        new FileManager(this, "alarm_pig").downloadFile(link, this);
    }

    private void saveAlarmConfig(ConfigAlarm.AlarmConfig alramConfig) {
        String alarm = alramConfig.getTimeAlarm();
        AlarmModel model = new AlarmModel();
        model.hour = AlarmUtils.getTimeFormString(alramConfig.getTimeAlarm(), 0);
        model.minute = AlarmUtils.getTimeFormString(alramConfig.getTimeAlarm(), 1);
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
        return input.trim().equalsIgnoreCase(mTOTPCode.trim());

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

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(SplashActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(SplashActivity.this, permission)) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{permission}, requestCode);
            }
        } else if (ContextCompat.checkSelfPermission(SplashActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Permission was denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == 101)
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
