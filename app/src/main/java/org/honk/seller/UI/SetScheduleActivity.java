package org.honk.seller.UI;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.honk.seller.R;
import org.honk.seller.model.DailySchedulePreferences;
import org.honk.seller.model.TimeSpan;
import org.honk.seller.services.SchedulerJobService;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public class SetScheduleActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    public static final String PREFERENCE_SCHEDULE = "PREFERENCE_SCHEDULE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setschedule);

        // Fill the activity with the values set by the user, if any.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        String settingsString = sharedPreferences.getString(SetScheduleActivity.PREFERENCE_SCHEDULE, "");

        Hashtable<Integer, DailySchedulePreferences> scheduleSettings;
        if (settingsString != "") {
            scheduleSettings = new Gson().fromJson(settingsString, TypeToken.getParameterized(Hashtable.class, Integer.class, DailySchedulePreferences.class).getType());
        } else {
            // Set default values.
            TimeSpan workStartTime = new TimeSpan(8, 0);
            TimeSpan workEndTime = new TimeSpan(18, 0);
            TimeSpan pauseStartTime = new TimeSpan(13, 0);
            TimeSpan pauseEndTime = new TimeSpan(14, 0);
            scheduleSettings = new Hashtable<Integer, DailySchedulePreferences>();
            scheduleSettings.put(Calendar.MONDAY, new DailySchedulePreferences(workStartTime, workEndTime, pauseStartTime, pauseEndTime));
            scheduleSettings.put(Calendar.TUESDAY, new DailySchedulePreferences(workStartTime, workEndTime, pauseStartTime, pauseEndTime));
            scheduleSettings.put(Calendar.WEDNESDAY, new DailySchedulePreferences(workStartTime, workEndTime, pauseStartTime, pauseEndTime));
            scheduleSettings.put(Calendar.THURSDAY, new DailySchedulePreferences(workStartTime, workEndTime, pauseStartTime, pauseEndTime));
            scheduleSettings.put(Calendar.FRIDAY, new DailySchedulePreferences(workStartTime, workEndTime, pauseStartTime, pauseEndTime));
            scheduleSettings.put(Calendar.SATURDAY, new DailySchedulePreferences());
            scheduleSettings.put(Calendar.SUNDAY, new DailySchedulePreferences());
        }

        displayScheduleForDay(
                scheduleSettings,
                Calendar.MONDAY,
                R.id.mondayWorkSwitch, R.id.mondayBreakSwitch,
                R.id.startMondayTextView, R.id.endMondayTextView,
                R.id.startMondayBreakTextView, R.id.endMondayBreakTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.TUESDAY,
                R.id.tuesdayWorkSwitch, R.id.tuesdayBreakSwitch,
                R.id.startTuesdayTextView, R.id.endTuesdayTextView,
                R.id.startTuesdayBreakTextView, R.id.endTuesdayBreakTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.WEDNESDAY,
                R.id.wednesdayWorkSwitch, R.id.wednesdayBreakSwitch,
                R.id.startWednesdayTextView, R.id.endWednesdayTextView,
                R.id.startWednesdayBreakTextView, R.id.endWednesdayBreakTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.THURSDAY,
                R.id.thursdayWorkSwitch, R.id.thursdayBreakSwitch,
                R.id.startThursdayTextView, R.id.endThursdayTextView,
                R.id.startThursdayBreakTextView, R.id.endThursdayBreakTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.FRIDAY,
                R.id.fridayWorkSwitch, R.id.fridayBreakSwitch,
                R.id.startFridayTextView, R.id.endFridayTextView,
                R.id.startFridayBreakTextView, R.id.endFridayBreakTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.SATURDAY,
                R.id.saturdayWorkSwitch, R.id.saturdayBreakSwitch,
                R.id.startSaturdayTextView, R.id.endSaturdayTextView,
                R.id.startSaturdayBreakTextView, R.id.endSaturdayBreakTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.SUNDAY,
                R.id.sundayWorkSwitch, R.id.sundayBreakSwitch,
                R.id.startSundayTextView, R.id.endSundayTextView,
                R.id.startSundayBreakTextView, R.id.endSundayBreakTextView);
    }

    private void displayScheduleForDay(
            Hashtable<Integer, DailySchedulePreferences> scheduleSettings,
            int day,
            int workSwitchId,
            int breakSwitchId,
            int startWorkTextViewId, int endWorkTextViewId,
            int startBreakTextViewId, int endBreakTextViewId) {
        DailySchedulePreferences dayPreferences = scheduleSettings.get(day);
        Switch workSwitch = this.findViewById(workSwitchId);
        Switch breakSwitch = this.findViewById(breakSwitchId);
        if (dayPreferences.workStartTime != null) {
            workSwitch.setChecked(true);
            TextView startWorkTextView = this.findViewById(startWorkTextViewId);
            this.displayTime(startWorkTextView, dayPreferences.workStartTime.hours, dayPreferences.workStartTime.minutes);
            TextView endWorkTextView = this.findViewById(endWorkTextViewId);
            this.displayTime(endWorkTextView, dayPreferences.workEndTime.hours, dayPreferences.workEndTime.minutes);
            if (dayPreferences.breakStartTime != null) {
                breakSwitch.setChecked(true);
                TextView startBreakTextView = this.findViewById(startBreakTextViewId);
                this.displayTime(startBreakTextView, dayPreferences.breakStartTime.hours, dayPreferences.breakStartTime.minutes);
                TextView endPauseTextView = this.findViewById(endBreakTextViewId);
                this.displayTime(endPauseTextView, dayPreferences.breakEndTime.hours, dayPreferences.breakEndTime.minutes);
            } else {
                breakSwitch.setChecked(false);
            }
        } else {
            workSwitch.setChecked(false);
            breakSwitch.setChecked(false);
        }
    }

    public void toggleSwitch(View view) {
        Switch selectedSwitch = (Switch)view;
        String workText, breakText;
        if (selectedSwitch.isChecked()) {
            workText = this.getString(R.string.workingFrom);
            breakText = this.getString(R.string.breakFrom);
        } else {
            workText = this.getString(R.string.notWorking);
            breakText = this.getString(R.string.noBreak);
        }

        if (view.getId() == R.id.mondayWorkSwitch) {
            if (selectedSwitch.isChecked()) {
                ((TextView)this.findViewById(R.id.fromMondayTextView)).setText(this.getString(R.string.workingFrom));
                this.findViewById(R.id.startMondayTextView).setVisibility(View.VISIBLE);
                this.findViewById(R.id.toMondayTextView).setVisibility(View.VISIBLE);
                this.findViewById(R.id.endMondayTextView).setVisibility(View.VISIBLE);
                ((Switch)this.findViewById(R.id.mondayBreakSwitch)).setEnabled(true);
            } else {
                ((TextView)this.findViewById(R.id.fromMondayTextView)).setText(R.string.notWorking);
                this.findViewById(R.id.startMondayTextView).setVisibility(View.INVISIBLE);
                this.findViewById(R.id.toMondayTextView).setVisibility(View.INVISIBLE);
                this.findViewById(R.id.endMondayTextView).setVisibility(View.INVISIBLE);
                ((Switch)this.findViewById(R.id.mondayBreakSwitch)).setChecked(false);
                ((Switch)this.findViewById(R.id.mondayBreakSwitch)).setEnabled(false);
                ((TextView)this.findViewById(R.id.mondayBreakFromTextView)).setText(this.getString(R.string.noBreak));
                this.findViewById(R.id.startMondayBreakTextView).setVisibility(View.INVISIBLE);
                this.findViewById(R.id.mondayBreakToTextView).setVisibility(View.INVISIBLE);
                this.findViewById(R.id.endMondayBreakTextView).setVisibility(View.INVISIBLE);
            }
        } else if (view.getId() == R.id.mondayBreakSwitch) {
            TextView textView = this.findViewById(R.id.mondayBreakToTextView);
            textView.setText(breakText);
        } else if (view.getId() == R.id.tuesdayWorkSwitch) {
            TextView textView = this.findViewById(R.id.fromTuesdayTextView);
            textView.setText(workText);
        } else if (view.getId() == R.id.tuesdayBreakSwitch) {
            TextView textView = this.findViewById(R.id.tuesdayBreakToTextView);
            textView.setText(breakText);
        } else if (view.getId() == R.id.wednesdayWorkSwitch) {
            TextView textView = this.findViewById(R.id.fromWednesdayTextView);
            textView.setText(workText);
        } else if (view.getId() == R.id.wednesdayBreakSwitch) {
            TextView textView = this.findViewById(R.id.wednesdayBreakToTextView);
            textView.setText(breakText);
        } else if (view.getId() == R.id.thursdayWorkSwitch) {
            TextView textView = this.findViewById(R.id.fromThursdayTextView);
            textView.setText(workText);
        } else if (view.getId() == R.id.thursdayBreakSwitch) {
            TextView textView = this.findViewById(R.id.thursdayBreakToTextView);
            textView.setText(breakText);
        } else if (view.getId() == R.id.fridayWorkSwitch) {
            TextView textView = this.findViewById(R.id.fromFridayTextView);
            textView.setText(workText);
        } else if (view.getId() == R.id.fridayBreakSwitch) {
            TextView textView = this.findViewById(R.id.fridayBreakToTextView);
            textView.setText(breakText);
        } else if (view.getId() == R.id.saturdayWorkSwitch) {
            TextView textView = this.findViewById(R.id.fromSaturdayTextView);
            textView.setText(workText);
        } else if (view.getId() == R.id.saturdayBreakSwitch) {
            TextView textView = this.findViewById(R.id.saturdayBreakToTextView);
            textView.setText(breakText);
        } else if (view.getId() == R.id.sundayWorkSwitch) {
            TextView textView = this.findViewById(R.id.fromSundayTextView);
            textView.setText(workText);
        } else if (view.getId() == R.id.sundayBreakSwitch) {
            TextView textView = this.findViewById(R.id.sundayBreakToTextView);
            textView.setText(breakText);
        }
    }

    private TextView resolveStartTimeTextView(int textViewId) {
        Integer linkedTextViewId = null;
        if (textViewId == R.id.endMondayTextView) {
            linkedTextViewId = R.id.startMondayTextView;
        } else if (textViewId == R.id.endMondayBreakTextView) {
            linkedTextViewId = R.id.startMondayBreakTextView;
        } else if (textViewId == R.id.endTuesdayTextView) {
            linkedTextViewId = R.id.startTuesdayTextView;
        } else if (textViewId == R.id.endTuesdayBreakTextView) {
            linkedTextViewId = R.id.startTuesdayBreakTextView;
        } else if (textViewId == R.id.endWednesdayTextView) {
            linkedTextViewId = R.id.startWednesdayTextView;
        } else if (textViewId == R.id.endWednesdayBreakTextView) {
            linkedTextViewId = R.id.startWednesdayBreakTextView;
        } else if (textViewId == R.id.endThursdayTextView) {
            linkedTextViewId = R.id.startThursdayTextView;
        } else if (textViewId == R.id.endThursdayBreakTextView) {
            linkedTextViewId = R.id.startThursdayBreakTextView;
        } else if (textViewId == R.id.endFridayTextView) {
            linkedTextViewId = R.id.startFridayTextView;
        } else if (textViewId == R.id.endFridayBreakTextView) {
            linkedTextViewId = R.id.startFridayBreakTextView;
        } else if (textViewId == R.id.endSaturdayTextView) {
            linkedTextViewId = R.id.startSaturdayTextView;
        } else if (textViewId == R.id.endSaturdayBreakTextView) {
            linkedTextViewId = R.id.startSaturdayBreakTextView;
        } else if (textViewId == R.id.endSundayTextView) {
            linkedTextViewId = R.id.startSundayTextView;
        } else if (textViewId == R.id.endSundayBreakTextView) {
            linkedTextViewId = R.id.startSundayBreakTextView;
        }

        if (linkedTextViewId != null) {
            return (TextView) findViewById(linkedTextViewId);
        }

        return null;
    }

    private TextView resolveEndTimeTextView(int textViewId) {
        Integer linkedTextViewId = null;
        if (textViewId == R.id.startMondayTextView) {
            linkedTextViewId = R.id.endMondayTextView;
        } else if (textViewId == R.id.startMondayBreakTextView) {
            linkedTextViewId = R.id.endMondayBreakTextView;
        } else if (textViewId == R.id.startTuesdayTextView) {
            linkedTextViewId = R.id.endTuesdayTextView;
        } else if (textViewId == R.id.startTuesdayBreakTextView) {
            linkedTextViewId = R.id.endTuesdayBreakTextView;
        } else if (textViewId == R.id.startWednesdayTextView) {
            linkedTextViewId = R.id.endWednesdayTextView;
        } else if (textViewId == R.id.startWednesdayBreakTextView) {
            linkedTextViewId = R.id.endWednesdayBreakTextView;
        } else if (textViewId == R.id.startThursdayTextView) {
            linkedTextViewId = R.id.endThursdayTextView;
        } else if (textViewId == R.id.startThursdayBreakTextView) {
            linkedTextViewId = R.id.endThursdayBreakTextView;
        } else if (textViewId == R.id.startFridayTextView) {
            linkedTextViewId = R.id.endFridayTextView;
        } else if (textViewId == R.id.startFridayBreakTextView) {
            linkedTextViewId = R.id.endFridayBreakTextView;
        } else if (textViewId == R.id.startSaturdayTextView) {
            linkedTextViewId = R.id.endSaturdayTextView;
        } else if (textViewId == R.id.startSaturdayBreakTextView) {
            linkedTextViewId = R.id.endSaturdayBreakTextView;
        } else if (textViewId == R.id.startSundayTextView) {
            linkedTextViewId = R.id.endSundayTextView;
        } else if (textViewId == R.id.startSundayBreakTextView) {
            linkedTextViewId = R.id.endSundayBreakTextView;
        }

        if (linkedTextViewId != null) {
            return (TextView) findViewById(linkedTextViewId);
        }

        return null;
    }

    public void pickTime(View view) {

        // Parse value from TextView.
        TextView textView = (TextView)view;
        TimeSpan timeSpan = getTimeSpanFromTextView(view.getId());

        /* When the user picks the end time of work or pause, it must be later than the starting time:
         * if the TextView the user tapped on is an "end time", I must get the matching starting time. */
        TextView startTimeTextView = this.resolveStartTimeTextView(view.getId());
        TimeSpan startingTimeSpan = null;
        TimeSpan endTimeSpan = null;
        if (startTimeTextView != null) {
            startingTimeSpan = getTimeSpanFromTextView(startTimeTextView.getId());
        } else {
            /* When the user picks the start time of work or pause, it must be earlier than the ending time:
             * if the TextView the user tapped on is a "start time", I must get the matching ending time. */
            TextView endTimeTextView = this.resolveEndTimeTextView(view.getId());
            if (endTimeTextView != null) {
                endTimeSpan = getTimeSpanFromTextView(endTimeTextView.getId());
            }
        }

        // TODO Picking dates for saturday and sunday fails.
        showTimePicker(textView, timeSpan, startingTimeSpan, endTimeSpan, this);
    }

    private void showTimePicker(TextView textView, TimeSpan timeSpan, TimeSpan startingTimeSpan, TimeSpan endTimeSpan, Context context) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(SetScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (endTimeSpan != null) {
                    // This is a "start time".
                    if (selectedHour < endTimeSpan.hours) {
                        // The user selected a valid time: write it to the schedule summary.
                        displayTime(textView, selectedHour, selectedMinute);
                    } else {
                        // The time selected is not valid: show a message and reset the time picker.
                        Toast.makeText(context, String.format(context.getString(R.string.setTimeBefore), getFormattedTime(endTimeSpan.hours, endTimeSpan.minutes)), Toast.LENGTH_SHORT).show();
                        showTimePicker(textView, timeSpan, startingTimeSpan, endTimeSpan, context);
                    }
                } else {
                    // This is an "end time".
                    if (selectedHour > startingTimeSpan.hours || (selectedHour == startingTimeSpan.hours && selectedMinute > startingTimeSpan.minutes)) {
                        // The user selected a valid time: write it to the schedule summary.
                        displayTime(textView, selectedHour, selectedMinute);
                    } else {
                        // The time selected is not valid: show a message and reset the time picker.
                        Toast.makeText(context, String.format(context.getString(R.string.setTimeAfter), getFormattedTime(startingTimeSpan.hours, startingTimeSpan.minutes)), Toast.LENGTH_SHORT).show();
                        showTimePicker(textView, timeSpan, startingTimeSpan, endTimeSpan, context);
                    }
                }
            }
        }, timeSpan.hours, timeSpan.minutes, DateFormat.is24HourFormat(this));
        timePickerDialog.setTitle(this.getString(R.string.selectTime));
        timePickerDialog.show();
    }

    private void displayTime(TextView textView, int hour, int minute) {
        textView.setText(getFormattedTime(hour, minute));
    }

    private String getFormattedTime(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
        return timeFormat.format(calendar.getTime());
    }

    public void proceed(View view) {
        save();
    }

    private void save() {

        Hashtable<Integer, DailySchedulePreferences> allPreferences = new Hashtable<Integer, DailySchedulePreferences>();

        // Get all the user preferences
        allPreferences.put(Calendar.MONDAY, this.getPreferences(
                R.id.mondayWorkSwitch, R.id.startMondayTextView, R.id.endMondayTextView, R.id.mondayBreakSwitch, R.id.startMondayBreakTextView, R.id.endMondayBreakTextView));
        allPreferences.put(Calendar.TUESDAY, this.getPreferences(
                R.id.tuesdayWorkSwitch, R.id.startTuesdayTextView, R.id.endTuesdayTextView, R.id.tuesdayBreakSwitch, R.id.startTuesdayBreakTextView, R.id.endTuesdayBreakTextView));
        allPreferences.put(Calendar.WEDNESDAY, this.getPreferences(
                R.id.wednesdayWorkSwitch, R.id.startWednesdayTextView, R.id.endWednesdayTextView, R.id.wednesdayBreakSwitch, R.id.startWednesdayBreakTextView, R.id.endWednesdayBreakTextView));
        allPreferences.put(Calendar.THURSDAY, this.getPreferences(
                R.id.thursdayWorkSwitch, R.id.startThursdayTextView, R.id.endThursdayTextView, R.id.thursdayBreakSwitch, R.id.startThursdayBreakTextView, R.id.endThursdayBreakTextView));
        allPreferences.put(Calendar.FRIDAY, this.getPreferences(
                R.id.fridayWorkSwitch, R.id.startFridayTextView, R.id.endFridayTextView, R.id.fridayBreakSwitch, R.id.startFridayBreakTextView, R.id.endFridayBreakTextView));
        allPreferences.put(Calendar.SATURDAY, this.getPreferences(
                R.id.saturdayWorkSwitch, R.id.startSaturdayTextView, R.id.endSaturdayTextView, R.id.saturdayBreakSwitch, R.id.startSaturdayBreakTextView, R.id.endSaturdayBreakTextView));
        allPreferences.put(Calendar.SUNDAY, this.getPreferences(
                R.id.sundayWorkSwitch, R.id.startSundayTextView, R.id.endSundayTextView, R.id.sundayBreakSwitch, R.id.startSundayBreakTextView, R.id.endSundayBreakTextView));

        // Save the new schedule.
        Context context = this.getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isFirstConfiguration = sharedPreferences.getString(SetScheduleActivity.PREFERENCE_SCHEDULE, "") == "";
        sharedPreferences.edit().putString(PREFERENCE_SCHEDULE, new Gson().toJson(allPreferences)).apply();

        // Cancel all pending jobs and restart with the new schedule.
        SchedulerJobService.cancelAllJobs(context);
        SchedulerJobService.scheduleJob(context);

        // Show a message that depends on whether this is the first configuration or just an edit.
        if (isFirstConfiguration) {
            // Show a dialog: the user should acknowledge that the service is starting.
            new AlertDialog.Builder(this)
                    .setMessage(this.getString(R.string.configurationComplete))
                    .setTitle(R.string.congratulations)
                    .setPositiveButton(R.string.ok, this)
                    .show();
        } else {
            // Show a toast message and keep the activity running.
            this.finishAffinity();
            Toast.makeText(this.getApplicationContext(), this.getString(R.string.scheduleSet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.finishAffinity();
    }

    private TimeSpan getTimeSpanFromTextView(int textViewId) {
        TextView textView = this.findViewById(textViewId);
        try {
            java.text.DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
            Date parsedDate = timeFormat.parse(textView.getText().toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            return new TimeSpan(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        } catch (ParseException x) { }

        return null;
    }

    private DailySchedulePreferences getPreferences(
            int workSwitchId, int startWorkTextViewId, int endWorkTextViewId, int breakSwitchId, int startBreakTextViewId, int endBreakTextViewId) {
        DailySchedulePreferences preferences = new DailySchedulePreferences();
        Switch workSwitch = this.findViewById(workSwitchId);
        if (workSwitch.isChecked()) {
            preferences.workStartTime = getTimeSpanFromTextView(startWorkTextViewId);
            preferences.workEndTime = getTimeSpanFromTextView(endWorkTextViewId);
            Switch mondayBreakSwitch = this.findViewById(breakSwitchId);
            if (mondayBreakSwitch.isChecked()) {
                preferences.breakStartTime = getTimeSpanFromTextView(startBreakTextViewId);
                preferences.breakEndTime = getTimeSpanFromTextView(endBreakTextViewId);
            }
        }

        return preferences;
    }
}
