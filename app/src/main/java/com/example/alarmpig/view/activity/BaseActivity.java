package com.example.alarmpig.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.alarmpig.db.AppDatabase;

import static com.example.alarmpig.util.Constants.DATABASE_NAME;

public abstract class BaseActivity extends AppCompatActivity {

    protected AppDatabase appDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase .class, DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();

    }
}
