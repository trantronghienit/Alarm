package com.example.alarmpig.model;

import android.support.annotation.NonNull;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AlarmModel {

    @PrimaryKey(autoGenerate = true)
    private String alarmId;
    public int hour;
    public int minute;
    public int second;
}
