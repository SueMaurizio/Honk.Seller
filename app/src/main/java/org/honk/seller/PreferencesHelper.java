package org.honk.seller;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.honk.seller.model.CompanyDetails;
import org.honk.seller.model.DailySchedulePreferences;
import org.honk.seller.model.TimeSpan;

import java.util.Calendar;
import java.util.Hashtable;

public class PreferencesHelper {

    private static final String PREFERENCE_SCHEDULE = "PREFERENCE_SCHEDULE";

    public static final int DEFAULT_WORK_START_HOUR = 8;
    public static final int DEFAULT_WORK_START_MINUTE = 0;
    public static final int DEFAULT_WORK_END_HOUR = 18;
    public static final int DEFAULT_WORK_END_MINUTE = 0;
    public static final int DEFAULT_BREAK_START_HOUR = 13;
    public static final int DEFAULT_BREAK_START_MINUTE = 0;
    public static final int DEFAULT_BREAK_END_HOUR = 14;
    public static final int DEFAULT_BREAK_END_MINUTE = 0;

    private static final String PREFERENCE_COMPANY_DETAILS = "PREFERENCE_COMPANY_DETAILS";

    public static Hashtable<Integer, DailySchedulePreferences> getScheduleSettings(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String settingsString = sharedPreferences.getString(PREFERENCE_SCHEDULE, "");
        if (!settingsString.equals("")) {
            try {
                return new Gson().fromJson(settingsString, TypeToken.getParameterized(Hashtable.class, Integer.class, DailySchedulePreferences.class).getType());
            } catch(Exception x) {
                return getDefaultScheduleSettings();
            }
        } else {
            return getDefaultScheduleSettings();
        }
    }

    private static Hashtable<Integer, DailySchedulePreferences> getDefaultScheduleSettings() {

        TimeSpan workStartTime = new TimeSpan(DEFAULT_WORK_START_HOUR, DEFAULT_WORK_START_MINUTE);
        TimeSpan workEndTime = new TimeSpan(DEFAULT_WORK_END_HOUR, DEFAULT_WORK_END_MINUTE);
        TimeSpan breakStartTime = new TimeSpan(DEFAULT_BREAK_START_HOUR, DEFAULT_BREAK_START_MINUTE);
        TimeSpan breakEndTime = new TimeSpan(DEFAULT_BREAK_END_HOUR, DEFAULT_BREAK_END_MINUTE);

        Hashtable<Integer, DailySchedulePreferences> scheduleSettings = new Hashtable<>();

        scheduleSettings.put(Calendar.MONDAY, new DailySchedulePreferences(workStartTime, workEndTime, breakStartTime, breakEndTime));
        scheduleSettings.put(Calendar.TUESDAY, new DailySchedulePreferences(workStartTime, workEndTime, breakStartTime, breakEndTime));
        scheduleSettings.put(Calendar.WEDNESDAY, new DailySchedulePreferences(workStartTime, workEndTime, breakStartTime, breakEndTime));
        scheduleSettings.put(Calendar.THURSDAY, new DailySchedulePreferences(workStartTime, workEndTime, breakStartTime, breakEndTime));
        scheduleSettings.put(Calendar.FRIDAY, new DailySchedulePreferences(workStartTime, workEndTime, breakStartTime, breakEndTime));
        scheduleSettings.put(Calendar.SATURDAY, new DailySchedulePreferences());
        scheduleSettings.put(Calendar.SUNDAY, new DailySchedulePreferences());

        return  scheduleSettings;
    }

    public static Boolean AreScheduleSettingsSet(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(PREFERENCE_SCHEDULE, "").equals("");
    }

    public static void setScheduleSettings(Hashtable<Integer, DailySchedulePreferences> settings, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(PREFERENCE_SCHEDULE, new Gson().toJson(settings)).apply();
    }

    public static CompanyDetails getCompanyDetails(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String settingsString = sharedPreferences.getString(PREFERENCE_COMPANY_DETAILS, "");
        if (!settingsString.equals("")) {
            return new Gson().fromJson(settingsString, CompanyDetails.class);
        } else {
            return new CompanyDetails();
        }
    }

    public static void setCompanyDetails(CompanyDetails companyDetails, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(PREFERENCE_COMPANY_DETAILS, new Gson().toJson(companyDetails)).apply();
    }
}
