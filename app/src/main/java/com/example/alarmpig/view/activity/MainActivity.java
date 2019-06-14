package com.example.alarmpig.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.alarmpig.BuildConfig;
import com.example.alarmpig.R;
import com.example.alarmpig.callback.OnGetConfigInfoListener;
import com.example.alarmpig.file.FileManager;
import com.example.alarmpig.model.AlarmModel;
import com.example.alarmpig.model.ConfigAlarm;
import com.example.alarmpig.receiver.AlarmReceiver;
import com.example.alarmpig.repository.AlarmRepository;
import com.example.alarmpig.service.AlarmService;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity implements FileManager.OnDownloadFileListener {

    private Button btnStart;
    private Button btnStop;
    private Button btnDashboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 101);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnDashboard = findViewById(R.id.btnDashboard);
        final AlarmReceiver alarm = new AlarmReceiver();
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AlarmModel model = new AlarmModel();
//                Calendar rightNow = Calendar.getInstance();
//                model.hour = rightNow.get(Calendar.HOUR_OF_DAY);
//                model.minute = rightNow.get(Calendar.MINUTE);
//                model.second = rightNow.get(Calendar.SECOND) + 6;
//                alarm.setAlarm(MainActivity.this, model);

                AlarmRepository.getInstance().getConfig(new OnGetConfigInfoListener() {
                    @Override
                    public void onSuccess(ConfigAlarm config) {
                        if(!config.getVersionCheck().getVersion().equals(BuildConfig.VERSION_NAME)){
                            updateApp(config.getVersionCheck().getLink());
                        }
                        if(config.isChange()){
                            saveAlarmConfig(config.getAlramConfig());
                        }
                    }

                    @Override
                    public void onFailed(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToService = new Intent(MainActivity.this, AlarmService.class);
                stopService(intentToService);
            }
        });

        btnDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToService = new Intent(MainActivity.this, DashboardAlarmActivity.class);
                startActivity(intentToService);
            }
        });
    }

    private void updateApp(String link) {
        new FileManager(this , "alarm_pig").downloadFile("http://mediamap.fpt.vn/mobimap/Android/MobiMap_v3.16.2.apk" , this);
    }

    private void saveAlarmConfig(ConfigAlarm.AlarmConfig alramConfig) {
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        } else if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
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

    @Override
    public void onPrepare() {
        Toast.makeText(this, "Prepare download", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgress(int Progress) {
        Toast.makeText(this, "" + Progress, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCompleted(String fileName) {
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
