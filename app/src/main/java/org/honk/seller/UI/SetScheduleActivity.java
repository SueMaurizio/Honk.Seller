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
import android.widget.RadioButton;
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
                R.id.mondayWorkRadio, R.id.mondayNoWorkRadio,
                R.id.mondayPauseRadio, R.id.mondayNoPauseRadio,
                R.id.startMondayTextView, R.id.endMondayTextView,
                R.id.startMondayPauseTextView, R.id.endMondayPauseTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.TUESDAY,
                R.id.tuesdayWorkRadio, R.id.tuesdayNoWorkRadio,
                R.id.tuesdayPauseRadio, R.id.tuesdayNoPauseRadio,
                R.id.startTuesdayTextView, R.id.endTuesdayTextView,
                R.id.startTuesdayPauseTextView, R.id.endTuesdayPauseTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.WEDNESDAY,
                R.id.wednesdayWorkRadio, R.id.wednesdayNoWorkRadio,
                R.id.wednesdayPauseRadio, R.id.wednesdayNoPauseRadio,
                R.id.startWednesdayTextView, R.id.endWednesdayTextView,
                R.id.startWednesdayPauseTextView, R.id.endWednesdayPauseTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.THURSDAY,
                R.id.thursdayWorkRadio, R.id.thursdayNoWorkRadio,
                R.id.thursdayPauseRadio, R.id.thursdayNoPauseRadio,
                R.id.startThursdayTextView, R.id.endThursdayTextView,
                R.id.startThursdayPauseTextView, R.id.endThursdayPauseTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.FRIDAY,
                R.id.fridayWorkRadio, R.id.fridayNoWorkRadio,
                R.id.fridayPauseRadio, R.id.fridayNoPauseRadio,
                R.id.startFridayTextView, R.id.endFridayTextView,
                R.id.startFridayPauseTextView, R.id.endFridayPauseTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.SATURDAY,
                R.id.saturdayWorkRadio, R.id.saturdayNoWorkRadio,
                R.id.saturdayPauseRadio, R.id.saturdayNoPauseRadio,
                R.id.startSaturdayTextView, R.id.endSaturdayTextView,
                R.id.startSaturdayPauseTextView, R.id.endSaturdayPauseTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.SUNDAY,
                R.id.sundayWorkRadio, R.id.sundayNoWorkRadio,
                R.id.sundayPauseRadio, R.id.sundayNoPauseRadio,
                R.id.startSundayTextView, R.id.endSundayTextView,
                R.id.startSundayPauseTextView, R.id.endSundayPauseTextView);
    }

    private void displayScheduleForDay(
            Hashtable<Integer, DailySchedulePreferences> scheduleSettings,
            int day,
            int workRadioId, int noWorkRadioId,
            int pauseRadioId, int noPauseRadioId,
            int startWorkTextViewId, int endWorkTextViewId,
            int startPauseTextViewId, int endPauseTextViewId) {
        DailySchedulePreferences dayPreferences = scheduleSettings.get(day);
        RadioButton workRadio = this.findViewById(workRadioId);
        RadioButton noWorkRadio = this.findViewById(noWorkRadioId);
        RadioButton pauseRadio = this.findViewById(pauseRadioId);
        RadioButton noPauseRadio = this.findViewById(noPauseRadioId);
        if (dayPreferences.workStartTime != null) {
            workRadio.setChecked(true);
            noWorkRadio.setChecked(false);
            TextView startWorkTextView = this.findViewById(startWorkTextViewId);
            this.displayTime(startWorkTextView, dayPreferences.workStartTime.hours, dayPreferences.workStartTime.minutes);
            TextView endWorkTextView = this.findViewById(endWorkTextViewId);
            this.displayTime(endWorkTextView, dayPreferences.workEndTime.hours, dayPreferences.workEndTime.minutes);
            if (dayPreferences.pauseStartTime != null) {
                pauseRadio.setChecked(true);
                noPauseRadio.setChecked(false);
                TextView startPauseTextView = this.findViewById(startPauseTextViewId);
                this.displayTime(startPauseTextView, dayPreferences.pauseStartTime.hours, dayPreferences.pauseStartTime.minutes);
                TextView endPauseTextView = this.findViewById(endPauseTextViewId);
                this.displayTime(endPauseTextView, dayPreferences.pauseEndTime.hours, dayPreferences.pauseEndTime.minutes);
            } else {
                pauseRadio.setChecked(false);
                noPauseRadio.setChecked(true);
            }
        } else {
            workRadio.setChecked(false);
            noWorkRadio.setChecked(true);
            pauseRadio.setChecked(false);
            noPauseRadio.setChecked(true);
        }
    }

    // Implements a radio button group without graphical limitations.
    public void toggleRadioButtons(View view) {
        RadioButton button = (RadioButton)view;
        RadioButton linkedButton = resolveLinkedButton(view.getId());
        if (linkedButton.isChecked()) {
            linkedButton.setChecked(false);
            button.setChecked(true);
        }
    }

    private RadioButton resolveLinkedButton(int radioButtonId)
    {
        int linkedButtonId = -1;
        if (radioButtonId == R.id.mondayWorkRadio) {
            linkedButtonId = R.id.mondayNoWorkRadio;
        } else if (radioButtonId == R.id.mondayNoWorkRadio) {
            linkedButtonId = R.id.mondayWorkRadio;
        } else if (radioButtonId == R.id.tuesdayWorkRadio) {
            linkedButtonId = R.id.tuesdayNoWorkRadio;
        } else if (radioButtonId == R.id.tuesdayNoWorkRadio) {
            linkedButtonId = R.id.tuesdayWorkRadio;
        } else if (radioButtonId == R.id.wednesdayWorkRadio) {
            linkedButtonId = R.id.wednesdayNoWorkRadio;
        } else if (radioButtonId == R.id.wednesdayNoWorkRadio) {
            linkedButtonId = R.id.wednesdayWorkRadio;
        } else if (radioButtonId == R.id.thursdayWorkRadio) {
            linkedButtonId = R.id.thursdayNoWorkRadio;
        } else if (radioButtonId == R.id.thursdayNoWorkRadio) {
            linkedButtonId = R.id.thursdayWorkRadio;
        } else if (radioButtonId == R.id.fridayWorkRadio) {
            linkedButtonId = R.id.fridayNoWorkRadio;
        } else if (radioButtonId == R.id.fridayNoWorkRadio) {
            linkedButtonId = R.id.fridayWorkRadio;
        } else if (radioButtonId == R.id.saturdayWorkRadio) {
            linkedButtonId = R.id.saturdayNoWorkRadio;
        } else if (radioButtonId == R.id.saturdayNoWorkRadio) {
            linkedButtonId = R.id.saturdayWorkRadio;
        } else if (radioButtonId == R.id.sundayWorkRadio) {
            linkedButtonId = R.id.sundayNoWorkRadio;
        } else if (radioButtonId == R.id.sundayNoWorkRadio) {
            linkedButtonId = R.id.sundayWorkRadio;
        } else if (radioButtonId == R.id.mondayPauseRadio) {
            linkedButtonId = R.id.mondayNoPauseRadio;
        } else if (radioButtonId == R.id.mondayNoPauseRadio) {
            linkedButtonId = R.id.mondayPauseRadio;
        } else if (radioButtonId == R.id.tuesdayPauseRadio) {
            linkedButtonId = R.id.tuesdayNoPauseRadio;
        } else if (radioButtonId == R.id.tuesdayNoPauseRadio) {
            linkedButtonId = R.id.tuesdayPauseRadio;
        } else if (radioButtonId == R.id.wednesdayPauseRadio) {
            linkedButtonId = R.id.wednesdayNoPauseRadio;
        } else if (radioButtonId == R.id.wednesdayNoPauseRadio) {
            linkedButtonId = R.id.wednesdayPauseRadio;
        } else if (radioButtonId == R.id.thursdayPauseRadio) {
            linkedButtonId = R.id.thursdayNoPauseRadio;
        } else if (radioButtonId == R.id.thursdayNoPauseRadio) {
            linkedButtonId = R.id.thursdayPauseRadio;
        } else if (radioButtonId == R.id.fridayPauseRadio) {
            linkedButtonId = R.id.fridayNoPauseRadio;
        } else if (radioButtonId == R.id.fridayNoPauseRadio) {
            linkedButtonId = R.id.fridayPauseRadio;
        } else if (radioButtonId == R.id.saturdayPauseRadio) {
            linkedButtonId = R.id.saturdayNoPauseRadio;
        } else if (radioButtonId == R.id.saturdayNoPauseRadio) {
            linkedButtonId = R.id.saturdayPauseRadio;
        } else if (radioButtonId == R.id.sundayPauseRadio) {
            linkedButtonId = R.id.sundayNoPauseRadio;
        } else if (radioButtonId == R.id.sundayNoPauseRadio) {
            linkedButtonId = R.id.sundayPauseRadio;
        }

        return (RadioButton)findViewById(linkedButtonId);
    }

    private TextView resolveStartTimeTextView(int textViewId) {
        Integer linkedTextViewId = null;
        if (textViewId == R.id.endMondayTextView) {
            linkedTextViewId = R.id.startMondayTextView;
        } else if (textViewId == R.id.endMondayPauseTextView) {
            linkedTextViewId = R.id.startMondayPauseTextView;
        } else if (textViewId == R.id.endTuesdayTextView) {
            linkedTextViewId = R.id.startTuesdayTextView;
        } else if (textViewId == R.id.endTuesdayPauseTextView) {
            linkedTextViewId = R.id.startTuesdayPauseTextView;
        } else if (textViewId == R.id.endWednesdayTextView) {
            linkedTextViewId = R.id.startWednesdayTextView;
        } else if (textViewId == R.id.endWednesdayPauseTextView) {
            linkedTextViewId = R.id.startWednesdayPauseTextView;
        } else if (textViewId == R.id.endThursdayTextView) {
            linkedTextViewId = R.id.startThursdayTextView;
        } else if (textViewId == R.id.endThursdayPauseTextView) {
            linkedTextViewId = R.id.startThursdayPauseTextView;
        } else if (textViewId == R.id.endFridayTextView) {
            linkedTextViewId = R.id.startFridayTextView;
        } else if (textViewId == R.id.endFridayPauseTextView) {
            linkedTextViewId = R.id.startFridayPauseTextView;
        } else if (textViewId == R.id.endSaturdayTextView) {
            linkedTextViewId = R.id.startSaturdayTextView;
        } else if (textViewId == R.id.endSaturdayPauseTextView) {
            linkedTextViewId = R.id.startSaturdayPauseTextView;
        } else if (textViewId == R.id.endSundayTextView) {
            linkedTextViewId = R.id.startSundayTextView;
        } else if (textViewId == R.id.endSundayPauseTextView) {
            linkedTextViewId = R.id.startSundayPauseTextView;
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
        } else if (textViewId == R.id.startMondayPauseTextView) {
            linkedTextViewId = R.id.endMondayPauseTextView;
        } else if (textViewId == R.id.startTuesdayTextView) {
            linkedTextViewId = R.id.endTuesdayTextView;
        } else if (textViewId == R.id.startTuesdayPauseTextView) {
            linkedTextViewId = R.id.endTuesdayPauseTextView;
        } else if (textViewId == R.id.startWednesdayTextView) {
            linkedTextViewId = R.id.endWednesdayTextView;
        } else if (textViewId == R.id.startWednesdayPauseTextView) {
            linkedTextViewId = R.id.endWednesdayPauseTextView;
        } else if (textViewId == R.id.startThursdayTextView) {
            linkedTextViewId = R.id.endThursdayTextView;
        } else if (textViewId == R.id.startThursdayPauseTextView) {
            linkedTextViewId = R.id.endThursdayPauseTextView;
        } else if (textViewId == R.id.startFridayTextView) {
            linkedTextViewId = R.id.endFridayTextView;
        } else if (textViewId == R.id.startFridayPauseTextView) {
            linkedTextViewId = R.id.endFridayPauseTextView;
        } else if (textViewId == R.id.startSaturdayTextView) {
            linkedTextViewId = R.id.endSaturdayTextView;
        } else if (textViewId == R.id.startSaturdayPauseTextView) {
            linkedTextViewId = R.id.endSaturdayPauseTextView;
        } else if (textViewId == R.id.startSundayTextView) {
            linkedTextViewId = R.id.endSundayTextView;
        } else if (textViewId == R.id.startSundayPauseTextView) {
            linkedTextViewId = R.id.endSundayPauseTextView;
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
                R.id.mondayWorkRadio, R.id.startMondayTextView, R.id.endMondayTextView, R.id.mondayPauseRadio, R.id.startMondayPauseTextView, R.id.endMondayPauseTextView));
        allPreferences.put(Calendar.TUESDAY, this.getPreferences(
                R.id.tuesdayWorkRadio, R.id.startTuesdayTextView, R.id.endTuesdayTextView, R.id.tuesdayPauseRadio, R.id.startTuesdayPauseTextView, R.id.endTuesdayPauseTextView));
        allPreferences.put(Calendar.WEDNESDAY, this.getPreferences(
                R.id.wednesdayWorkRadio, R.id.startWednesdayTextView, R.id.endWednesdayTextView, R.id.wednesdayPauseRadio, R.id.startWednesdayPauseTextView, R.id.endWednesdayPauseTextView));
        allPreferences.put(Calendar.THURSDAY, this.getPreferences(
                R.id.thursdayWorkRadio, R.id.startThursdayTextView, R.id.endThursdayTextView, R.id.thursdayPauseRadio, R.id.startThursdayPauseTextView, R.id.endThursdayPauseTextView));
        allPreferences.put(Calendar.FRIDAY, this.getPreferences(
                R.id.fridayWorkRadio, R.id.startFridayTextView, R.id.endFridayTextView, R.id.fridayPauseRadio, R.id.startFridayPauseTextView, R.id.endFridayPauseTextView));
        allPreferences.put(Calendar.SATURDAY, this.getPreferences(
                R.id.saturdayWorkRadio, R.id.startSaturdayTextView, R.id.endSaturdayTextView, R.id.saturdayPauseRadio, R.id.startSaturdayPauseTextView, R.id.endSaturdayPauseTextView));
        allPreferences.put(Calendar.SUNDAY, this.getPreferences(
                R.id.sundayWorkRadio, R.id.startSundayTextView, R.id.endSundayTextView, R.id.sundayPauseRadio, R.id.startSundayPauseTextView, R.id.endSundayPauseTextView));

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
            int workRadioId, int startWorkTextViewId, int endWorkTextViewId, int pauseRadioId, int startPauseTextViewId, int endPauseTextViewId) {
        DailySchedulePreferences preferences = new DailySchedulePreferences();
        RadioButton workRadio = this.findViewById(workRadioId);
        if (workRadio.isChecked()) {
            preferences.workStartTime = getTimeSpanFromTextView(startWorkTextViewId);
            preferences.workEndTime = getTimeSpanFromTextView(endWorkTextViewId);
            RadioButton mondayPauseRadio = this.findViewById(pauseRadioId);
            if (mondayPauseRadio.isChecked()) {
                preferences.pauseStartTime = getTimeSpanFromTextView(startPauseTextViewId);
                preferences.pauseEndTime = getTimeSpanFromTextView(endPauseTextViewId);
            }
        }

        return preferences;
    }
}
