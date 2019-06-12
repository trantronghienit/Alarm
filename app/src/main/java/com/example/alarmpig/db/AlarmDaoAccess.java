package com.example.alarmpig.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.alarmpig.model.AlarmModel;

import java.util.List;

@Dao
public interface AlarmDaoAccess {
    @Insert
    void insertOnlySingleAlarm (AlarmModel alarm);

    @Insert
    void insertMultipleAlarmModel(List<AlarmModel> alarmList);

    @Query("SELECT * FROM AlarmModel WHERE alarmId = :alarmId")
    AlarmModel fetchOneAlarmModelbyAlarmId (int alarmId);

    @Update
    void updateAlarm (AlarmModel alarm);

    @Delete
    void deleteAlarm (AlarmModel alarm);
}
