package com.example.alarmpig.view.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import trantronghien.io.com.TimeBasedOneTimePasswordGenerator;

import com.example.alarmpig.BuildConfig;
import com.example.alarmpig.R;
import com.example.alarmpig.callback.OnGetConfigInfoListener;
import com.example.alarmpig.file.FileManager;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.model.ConfigAlarm;
import com.example.alarmpig.repository.AlarmRepository;
import com.example.alarmpig.util.AlarmUtils;
import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.LogUtils;
import com.example.alarmpig.util.SharedPrefs;
import com.example.alarmpig.util.UtilHelper;
import com.example.alarmpig.util.ViewUtils;

import java.io.File;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SplashActivity extends BaseActivity implements FileManager.OnDownloadFileListener {

    private Button btnUnlock;
    private EditText edtInputCode;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        inIt();

        AlarmRepository.getInstance().getConfig(new OnGetConfigInfoListener() {
            @Override
            public void onSuccess(ConfigAlarm config) {
                if (!config.getVersionCheck().getVersion().equals(BuildConfig.VERSION_NAME)) {
                    updateApp(config.getVersionCheck().getLink());
                }
                if (config.isChange()) {
                    // save change
                    Toast.makeText(SplashActivity.this, "Cập nhật config...", Toast.LENGTH_SHORT).show();
                    SharedPrefs.getInstance().put(Constants.KEY_IS_CHANGE , config.versionChange());
                    LogUtils.i("version change " + config.versionChange());
                    saveAlarmConfig(config.getAlramConfig());
                }
            }

            @Override
            public void onFailed(String message) {
                Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);

        btnUnlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String code = edtInputCode.getText().toString();
//                checkCode(code);
//                AlarmRepository.getInstance().getConfig(new OnGetConfigInfoListener() {
//                    @Override
//                    public void onSuccess(ConfigAlarm config) {
//                        if (!config.getVersionCheck().getVersion().equals(BuildConfig.VERSION_NAME)) {
//                            updateApp(config.getVersionCheck().getLink());
//                        }
//                        if (config.isChange()) {
//                            Toast.makeText(SplashActivity.this, "Cập nhật config...", Toast.LENGTH_SHORT).show();
//                            SharedPrefs.getInstance().put(Constants.KEY_IS_CHANGE , config.versionChange());
//                            LogUtils.i("version change " + config.versionChange());
//                            saveAlarmConfig(config.getAlramConfig());
//                        }
//                    }
//
//                    @Override
//                    public void onFailed(String message) {
//                        Toast.makeText(SplashActivity.this, message, Toast.LENGTH_SHORT).show();
//                    }
//                });
                UtilHelper.switchActivity(SplashActivity.this , MainActivity.class);
            }
        });

        TimeBasedOneTimePasswordGenerator totp = null;
        try {
            totp = new TimeBasedOneTimePasswordGenerator();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKey key = null;
        {
            final KeyGenerator keyGenerator;
            try {
                keyGenerator = KeyGenerator.getInstance(totp.getAlgorithm());
                // HMAC-SHA1 and HMAC-SHA256 prefer 64-byte (512-bit) keys; HMAC-SHA512 prefers 128-byte (1024-bit) keys
                keyGenerator.init(512);

                key = keyGenerator.generateKey();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


        }
        final Date now = new Date();
        try {
            System.out.format("Current password: %06d\n", totp.generateOneTimePassword(key, now));
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private void inIt() {
        btnUnlock = findViewById(R.id.btnUnlock);
        edtInputCode = findViewById(R.id.edtInputCode);
        progress = new ProgressDialog(this);
        progress.setMessage("Đang cập nhật...");
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(true);
        progress.setMax(100);
    }

    private void updateApp(String link) {
        new FileManager(this, "alarm_pig").downloadFile("http://mediamap.fpt.vn/mobimap/Android/MobiMap_v3.16.2.apk", this);
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
        model.time = time.getTimeInMillis();

        AlarmUtils.getDayOfWeek(model.days, alramConfig.getDaysOfWeek());
        model.convertDays();
        LogUtils.i("db insert " + model.toString());
        appDatabase.AlarmDAO().insertOnlySingleAlarm(model);
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

    private void checkCode(String code) {

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
}
