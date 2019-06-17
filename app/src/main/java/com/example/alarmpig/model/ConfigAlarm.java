package com.example.alarmpig.model;

import com.example.alarmpig.util.Constants;
import com.example.alarmpig.util.SharedPrefs;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigAlarm {

    @SerializedName("version_check")
    private VersionCheck versionCheck;
    @SerializedName("alarm_config")
    private AlarmConfig alarmConfig;
    @SerializedName("is_change")
    private int isChange;

    public VersionCheck getVersionCheck() {
        return versionCheck;
    }

    public void setVersionCheck(VersionCheck versionCheck) {
        this.versionCheck = versionCheck;
    }

    public AlarmConfig getAlramConfig() {
        return alarmConfig;
    }

    public void setAlramConfig(AlarmConfig alarmConfig) {
        this.alarmConfig = alarmConfig;
    }

    public boolean isChange() {
        return SharedPrefs.getInstance().get(Constants.KEY_IS_CHANGE , Integer.class) < isChange;
    }

    public int versionChange() {
        return isChange;
    }

    public static class VersionCheck {
        /**
         * Link : https://github.com/trantronghienit/Android/alarm.apk
         * Version : 1.0
         */

        private String Link;
        private String Version;

        public String getLink() {
            return Link;
        }

        public void setLink(String Link) {
            this.Link = Link;
        }

        public String getVersion() {
            return Version;
        }

        public void setVersion(String Version) {
            this.Version = Version;
        }
    }

    public static class AlarmConfig {
        /**
         * time_alarm : 5:30:am
         * day_of_week : ["mon","tue","wed","thu","fri","sat","sun"]
         */

        @SerializedName("time_alarm")
        private String timeAlarm;
        @SerializedName("day_of_week")
        private List<String> daysOfWeek;

        public String getTimeAlarm() {
            return timeAlarm;
        }

        public void setTimeAlarm(String timeAlarm) {
            this.timeAlarm = timeAlarm;
        }

        public List<String> getDaysOfWeek() {
            return daysOfWeek;
        }

        public void setDaysOfWeek(List<String> daysOfWeek) {
            this.daysOfWeek = daysOfWeek;
        }
    }
}
