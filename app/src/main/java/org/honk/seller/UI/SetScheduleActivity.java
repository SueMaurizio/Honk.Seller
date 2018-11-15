package org.honk.seller.UI;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.honk.seller.PreferencesHelper;
import org.honk.seller.R;
import org.honk.seller.model.DailySchedulePreferences;
import org.honk.seller.model.TimeSpan;
import org.honk.seller.services.SchedulerJobService;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

public class SetScheduleActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setschedule);

        // Fill the activity with the values set by the user, if any.
        Hashtable<Integer, DailySchedulePreferences> scheduleSettings = PreferencesHelper.getScheduleSettings(this.getApplicationContext());

        displayScheduleForDay(
                scheduleSettings,
                Calendar.MONDAY,
                R.id.mondayWorkSwitch, R.id.mondayBreakSwitch,
                R.id.mondayWorkFromTextView, R.id.mondayWorkToTextView,
                R.id.mondayBreakFromTextView, R.id.mondayBreakToTextView,
                R.id.mondayWorkStartTextView, R.id.mondayWorkEndTextView,
                R.id.mondayBreakStartTextView, R.id.mondayBreakEndTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.TUESDAY,
                R.id.tuesdayWorkSwitch, R.id.tuesdayBreakSwitch,
                R.id.tuesdayWorkFromTextView, R.id.tuesdayWorkToTextView,
                R.id.tuesdayBreakFromTextView, R.id.tuesdayBreakToTextView,
                R.id.tuesdayWorkStartTextView, R.id.tuesdayWorkEndTextView,
                R.id.tuesdayBreakStartTextView, R.id.tuesdayBreakEndTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.WEDNESDAY,
                R.id.wednesdayWorkSwitch, R.id.wednesdayBreakSwitch,
                R.id.wednesdayWorkFromTextView, R.id.wednesdayWorkToTextView,
                R.id.wednesdayBreakFromTextView, R.id.wednesdayBreakToTextView,
                R.id.wednesdayWorkStartTextView, R.id.wednesdayWorkEndTextView,
                R.id.wednesdayBreakStartTextView, R.id.wednesdayBreakEndTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.THURSDAY,
                R.id.thursdayWorkSwitch, R.id.thursdayBreakSwitch,
                R.id.thursdayWorkFromTextView, R.id.thursdayWorkToTextView,
                R.id.thursdayBreakFromTextView, R.id.thursdayBreakToTextView,
                R.id.thursdayWorkStartTextView, R.id.thursdayWorkEndTextView,
                R.id.thursdayBreakStartTextView, R.id.thursdayBreakEndTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.FRIDAY,
                R.id.fridayWorkSwitch, R.id.fridayBreakSwitch,
                R.id.fridayWorkFromTextView, R.id.fridayWorkToTextView,
                R.id.fridayBreakFromTextView, R.id.fridayBreakToTextView,
                R.id.fridayWorkStartTextView, R.id.fridayWorkEndTextView,
                R.id.fridayBreakStartTextView, R.id.fridayBreakEndTextView);

        displayScheduleForDay(
                scheduleSettings,
                Calendar.SATURDAY,
                R.id.saturdayWorkSwitch, R.id.saturdayBreakSwitch,
                R.id.saturdayWorkFromTextView, R.id.saturdayWorkToTextView,
                R.id.saturdayBreakFromTextView, R.id.saturdayBreakToTextView,
                R.id.saturdayWorkStartTextView, R.id.saturdayWorkEndTextView,
                R.id.saturdayBreakStartTextView, R.id.saturdayBreakEndTextView);
        displayScheduleForDay(
                scheduleSettings,
                Calendar.SUNDAY,
                R.id.sundayWorkSwitch, R.id.sundayBreakSwitch,
                R.id.sundayWorkFromTextView, R.id.sundayWorkToTextView,
                R.id.sundayBreakFromTextView, R.id.sundayBreakToTextView,
                R.id.sundayWorkStartTextView, R.id.sundayWorkEndTextView,
                R.id.sundayBreakStartTextView, R.id.sundayBreakEndTextView);
    }

    private void displayScheduleForDay(
            Hashtable<Integer, DailySchedulePreferences> scheduleSettings,
            int day,
            int workSwitchId, int breakSwitchId,
            int workFromTextViewId, int workToTextViewId,
            int breakFromTextViewId, int breakToTextViewId,
            int startWorkTextViewId, int endWorkTextViewId,
            int startBreakTextViewId, int endBreakTextViewId) {
        DailySchedulePreferences dayPreferences = scheduleSettings.get(day);

        Switch workSwitch = this.findViewById(workSwitchId);
        Switch breakSwitch = this.findViewById(breakSwitchId);

        TextView workFromTextView = this.findViewById(workFromTextViewId);
        TextView workToTextView = this.findViewById(workToTextViewId);

        TextView breakFromTextView = this.findViewById(breakFromTextViewId);
        TextView breakToTextView = this.findViewById(breakToTextViewId);

        TextView startWorkTextView = this.findViewById(startWorkTextViewId);
        TextView endWorkTextView = this.findViewById(endWorkTextViewId);

        TextView startBreakTextView = this.findViewById(startBreakTextViewId);
        TextView endBreakTextView = this.findViewById(endBreakTextViewId);

        if (dayPreferences.workStartTime != null) {
            // This is a working day: show the working schedule.
            {
                workSwitch.setChecked(true);
                workFromTextView.setText(this.getString(R.string.workingFrom));

                startWorkTextView.setVisibility(View.VISIBLE);
                this.setTextViewTextAsTime(startWorkTextView, dayPreferences.workStartTime.hours, dayPreferences.workStartTime.minutes);

                workToTextView.setVisibility(View.VISIBLE);

                endWorkTextView.setVisibility(View.VISIBLE);
                this.setTextViewTextAsTime(endWorkTextView, dayPreferences.workEndTime.hours, dayPreferences.workEndTime.minutes);

                if (dayPreferences.breakStartTime != null) {
                    // There is a break set for this day: show the break schedule.
                    {
                        breakSwitch.setChecked(true);
                        breakFromTextView.setText(this.getString(R.string.breakFrom));

                        startBreakTextView.setVisibility(View.VISIBLE);
                        this.setTextViewTextAsTime(startBreakTextView, dayPreferences.breakStartTime.hours, dayPreferences.breakStartTime.minutes);

                        breakToTextView.setVisibility(View.VISIBLE);

                        endBreakTextView.setVisibility(View.VISIBLE);
                        this.setTextViewTextAsTime(endBreakTextView, dayPreferences.breakEndTime.hours, dayPreferences.breakEndTime.minutes);
                    }
                } else {
                    // There is no break set for this day: hide the break schedule.
                    hideBreakSchedule(breakSwitch, breakFromTextView, startBreakTextView, breakToTextView, endBreakTextView);
                }

                breakSwitch.setEnabled(true);
            }
        } else {
            /* This is not a working day: hide both the working schedule and the break schedule.
             * Also, set default values for the hidden text views representing time. Otherwise, if the user
             * enables work, the default value 00:00 will appear for all text views. */
            {
                workSwitch.setChecked(false);

                workFromTextView.setText(this.getString(R.string.notWorking));

                setTextViewTextAsTime(startWorkTextView, PreferencesHelper.DEFAULT_WORK_START_HOUR, PreferencesHelper.DEFAULT_WORK_START_MINUTE);
                startWorkTextView.setVisibility(View.INVISIBLE);

                workToTextView.setVisibility(View.INVISIBLE);

                endWorkTextView.setVisibility(View.INVISIBLE);
                setTextViewTextAsTime(endWorkTextView, PreferencesHelper.DEFAULT_WORK_END_HOUR, PreferencesHelper.DEFAULT_WORK_END_MINUTE);

                hideBreakSchedule(breakSwitch, breakFromTextView, startBreakTextView, breakToTextView, endBreakTextView);
                breakSwitch.setEnabled(false);
            }
        }
    }

    private void hideBreakSchedule(Switch breakSwitch, TextView breakFromTextView, TextView startBreakTextView, TextView breakToTextView, TextView endBreakTextView) {
        breakSwitch.setChecked(false);

        breakFromTextView.setText(this.getString(R.string.noBreak));

        startBreakTextView.setVisibility(View.INVISIBLE);
        setTextViewTextAsTime(startBreakTextView, PreferencesHelper.DEFAULT_BREAK_START_HOUR, PreferencesHelper.DEFAULT_BREAK_START_MINUTE);

        breakToTextView.setVisibility(View.INVISIBLE);

        endBreakTextView.setVisibility(View.INVISIBLE);
        setTextViewTextAsTime(endBreakTextView, PreferencesHelper.DEFAULT_BREAK_END_HOUR, PreferencesHelper.DEFAULT_BREAK_END_MINUTE);
    }

    public void toggleSwitch(View view) {
        Switch selectedSwitch = (Switch)view;
        if (view.getId() == R.id.mondayWorkSwitch) {
            toggleWorkSwitch(
                    selectedSwitch.isChecked(), R.id.mondayWorkStartTextView, R.id.mondayWorkFromTextView, R.id.mondayWorkToTextView, R.id.mondayWorkEndTextView,
                    R.id.mondayBreakSwitch, R.id.mondayBreakStartTextView, R.id.mondayBreakFromTextView, R.id.mondayBreakToTextView, R.id.mondayBreakEndTextView);
        } else if (view.getId() == R.id.mondayBreakSwitch) {
            toggleBreakSwitch(selectedSwitch.isChecked(), R.id.mondayBreakStartTextView, R.id.mondayBreakFromTextView, R.id.mondayBreakToTextView, R.id.mondayBreakEndTextView);
        } else if (view.getId() == R.id.tuesdayWorkSwitch) {
            toggleWorkSwitch(
                    selectedSwitch.isChecked(), R.id.tuesdayWorkStartTextView, R.id.tuesdayWorkFromTextView, R.id.tuesdayWorkToTextView, R.id.tuesdayWorkEndTextView,
                    R.id.tuesdayBreakSwitch, R.id.tuesdayBreakStartTextView, R.id.tuesdayBreakFromTextView, R.id.tuesdayBreakToTextView, R.id.tuesdayBreakEndTextView);
        } else if (view.getId() == R.id.tuesdayBreakSwitch) {
            toggleBreakSwitch(selectedSwitch.isChecked(), R.id.tuesdayBreakStartTextView, R.id.tuesdayBreakFromTextView, R.id.tuesdayBreakToTextView, R.id.tuesdayBreakEndTextView);
        } else if (view.getId() == R.id.wednesdayWorkSwitch) {
            toggleWorkSwitch(
                    selectedSwitch.isChecked(), R.id.wednesdayWorkStartTextView, R.id.wednesdayWorkFromTextView, R.id.wednesdayWorkToTextView, R.id.wednesdayWorkEndTextView,
                    R.id.wednesdayBreakSwitch, R.id.wednesdayBreakStartTextView, R.id.wednesdayBreakFromTextView, R.id.wednesdayBreakToTextView, R.id.wednesdayBreakEndTextView);
        } else if (view.getId() == R.id.wednesdayBreakSwitch) {
            toggleBreakSwitch(selectedSwitch.isChecked(), R.id.wednesdayBreakStartTextView, R.id.wednesdayBreakFromTextView, R.id.wednesdayBreakToTextView, R.id.wednesdayBreakEndTextView);
        } else if (view.getId() == R.id.thursdayWorkSwitch) {
            toggleWorkSwitch(
                    selectedSwitch.isChecked(), R.id.thursdayWorkStartTextView, R.id.thursdayWorkFromTextView, R.id.thursdayWorkToTextView, R.id.thursdayWorkEndTextView,
                    R.id.thursdayBreakSwitch, R.id.thursdayBreakStartTextView, R.id.thursdayBreakFromTextView, R.id.thursdayBreakToTextView, R.id.thursdayBreakEndTextView);
        } else if (view.getId() == R.id.thursdayBreakSwitch) {
            toggleBreakSwitch(selectedSwitch.isChecked(), R.id.thursdayBreakStartTextView, R.id.thursdayBreakFromTextView, R.id.thursdayBreakToTextView, R.id.thursdayBreakEndTextView);
        } else if (view.getId() == R.id.fridayWorkSwitch) {
            toggleWorkSwitch(
                    selectedSwitch.isChecked(), R.id.fridayWorkStartTextView, R.id.fridayWorkFromTextView, R.id.fridayWorkToTextView, R.id.fridayWorkEndTextView,
                    R.id.fridayBreakSwitch, R.id.fridayBreakStartTextView, R.id.fridayBreakFromTextView, R.id.fridayBreakToTextView, R.id.fridayBreakEndTextView);
        } else if (view.getId() == R.id.fridayBreakSwitch) {
            toggleBreakSwitch(selectedSwitch.isChecked(), R.id.fridayBreakStartTextView, R.id.fridayBreakFromTextView, R.id.fridayBreakToTextView, R.id.fridayBreakEndTextView);
        } else if (view.getId() == R.id.saturdayWorkSwitch) {
            toggleWorkSwitch(
                    selectedSwitch.isChecked(), R.id.saturdayWorkStartTextView, R.id.saturdayWorkFromTextView, R.id.saturdayWorkToTextView, R.id.saturdayWorkEndTextView,
                    R.id.saturdayBreakSwitch, R.id.saturdayBreakStartTextView, R.id.saturdayBreakFromTextView, R.id.saturdayBreakToTextView, R.id.saturdayBreakEndTextView);
        } else if (view.getId() == R.id.saturdayBreakSwitch) {
            toggleBreakSwitch(selectedSwitch.isChecked(), R.id.saturdayBreakStartTextView, R.id.saturdayBreakFromTextView, R.id.saturdayBreakToTextView, R.id.saturdayBreakEndTextView);
        } else if (view.getId() == R.id.sundayWorkSwitch) {
            toggleWorkSwitch(
                    selectedSwitch.isChecked(), R.id.sundayWorkStartTextView, R.id.sundayWorkFromTextView, R.id.sundayWorkToTextView, R.id.sundayWorkEndTextView,
                    R.id.sundayBreakSwitch, R.id.sundayBreakStartTextView, R.id.sundayBreakFromTextView, R.id.sundayBreakToTextView, R.id.sundayBreakEndTextView);
        } else if (view.getId() == R.id.sundayBreakSwitch) {
            toggleBreakSwitch(selectedSwitch.isChecked(), R.id.sundayBreakStartTextView, R.id.sundayBreakFromTextView, R.id.sundayBreakToTextView, R.id.sundayBreakEndTextView);
        }
    }

    private void toggleWorkSwitch(
            Boolean selectedSwitchIsChecked, int workStartTextViewId, int workFromTextViewId, int workToTextViewId, int workEndTextViewId,
            int breakSwitchId, int breakStartTextViewId, int breakFromTextViewId, int breakToTextViewId, int breakEndTextViewId) {
        int workVisibility;
        if (selectedSwitchIsChecked) {
            ((TextView)this.findViewById(workFromTextViewId)).setText(this.getString(R.string.workingFrom));
            workVisibility = View.VISIBLE;
            this.findViewById(breakSwitchId).setEnabled(true);
        } else {
            ((TextView)this.findViewById(workFromTextViewId)).setText(R.string.notWorking);
            workVisibility = View.INVISIBLE;
            Switch mondayBreakSwitch = this.findViewById(breakSwitchId);
            mondayBreakSwitch.setChecked(false);
            mondayBreakSwitch.setEnabled(false);
            ((TextView)this.findViewById(breakFromTextViewId)).setText(this.getString(R.string.noBreak));
            this.findViewById(breakStartTextViewId).setVisibility(View.INVISIBLE);
            this.findViewById(breakToTextViewId).setVisibility(View.INVISIBLE);
            this.findViewById(breakEndTextViewId).setVisibility(View.INVISIBLE);
        }

        this.findViewById(workStartTextViewId).setVisibility(workVisibility);
        this.findViewById(workToTextViewId).setVisibility(workVisibility);
        this.findViewById(workEndTextViewId).setVisibility(workVisibility);
    }

    private void toggleBreakSwitch(Boolean selectedSwitchIsChecked, int breakStartTextViewId, int breakFromTextViewId, int breakToTextViewId, int breakEndTextViewId) {
        int visibility;
        if (selectedSwitchIsChecked) {
            ((TextView)this.findViewById(breakFromTextViewId)).setText(this.getString(R.string.breakFrom));
            visibility = View.VISIBLE;
        } else {
            ((TextView)this.findViewById(breakFromTextViewId)).setText(R.string.noBreak);
            visibility = View.INVISIBLE;
        }

        this.findViewById(breakStartTextViewId).setVisibility(visibility);
        this.findViewById(breakToTextViewId).setVisibility(visibility);
        this.findViewById(breakEndTextViewId).setVisibility(visibility);
    }

    private TextView resolveStartTimeTextView(int textViewId) {
        Integer linkedTextViewId = null;
        if (textViewId == R.id.mondayWorkEndTextView) {
            linkedTextViewId = R.id.mondayWorkStartTextView;
        } else if (textViewId == R.id.mondayBreakEndTextView) {
            linkedTextViewId = R.id.mondayBreakStartTextView;
        } else if (textViewId == R.id.tuesdayWorkEndTextView) {
            linkedTextViewId = R.id.tuesdayWorkStartTextView;
        } else if (textViewId == R.id.tuesdayBreakEndTextView) {
            linkedTextViewId = R.id.tuesdayBreakStartTextView;
        } else if (textViewId == R.id.wednesdayWorkEndTextView) {
            linkedTextViewId = R.id.wednesdayWorkStartTextView;
        } else if (textViewId == R.id.wednesdayBreakEndTextView) {
            linkedTextViewId = R.id.wednesdayBreakStartTextView;
        } else if (textViewId == R.id.thursdayWorkEndTextView) {
            linkedTextViewId = R.id.thursdayWorkStartTextView;
        } else if (textViewId == R.id.thursdayBreakEndTextView) {
            linkedTextViewId = R.id.thursdayBreakStartTextView;
        } else if (textViewId == R.id.fridayWorkEndTextView) {
            linkedTextViewId = R.id.fridayWorkStartTextView;
        } else if (textViewId == R.id.fridayBreakEndTextView) {
            linkedTextViewId = R.id.fridayBreakStartTextView;
        } else if (textViewId == R.id.saturdayWorkEndTextView) {
            linkedTextViewId = R.id.saturdayWorkStartTextView;
        } else if (textViewId == R.id.saturdayBreakEndTextView) {
            linkedTextViewId = R.id.saturdayBreakStartTextView;
        } else if (textViewId == R.id.sundayWorkEndTextView) {
            linkedTextViewId = R.id.sundayWorkStartTextView;
        } else if (textViewId == R.id.sundayBreakEndTextView) {
            linkedTextViewId = R.id.sundayBreakStartTextView;
        }

        if (linkedTextViewId != null) {
            return (TextView) findViewById(linkedTextViewId);
        }

        return null;
    }

    private TextView resolveEndTimeTextView(int textViewId) {
        Integer linkedTextViewId = null;
        if (textViewId == R.id.mondayWorkStartTextView) {
            linkedTextViewId = R.id.mondayWorkEndTextView;
        } else if (textViewId == R.id.mondayBreakStartTextView) {
            linkedTextViewId = R.id.mondayBreakEndTextView;
        } else if (textViewId == R.id.tuesdayWorkStartTextView) {
            linkedTextViewId = R.id.tuesdayWorkEndTextView;
        } else if (textViewId == R.id.tuesdayBreakStartTextView) {
            linkedTextViewId = R.id.tuesdayBreakEndTextView;
        } else if (textViewId == R.id.wednesdayWorkStartTextView) {
            linkedTextViewId = R.id.wednesdayWorkEndTextView;
        } else if (textViewId == R.id.wednesdayBreakStartTextView) {
            linkedTextViewId = R.id.wednesdayBreakEndTextView;
        } else if (textViewId == R.id.thursdayWorkStartTextView) {
            linkedTextViewId = R.id.thursdayWorkEndTextView;
        } else if (textViewId == R.id.thursdayBreakStartTextView) {
            linkedTextViewId = R.id.thursdayBreakEndTextView;
        } else if (textViewId == R.id.fridayWorkStartTextView) {
            linkedTextViewId = R.id.fridayWorkEndTextView;
        } else if (textViewId == R.id.fridayBreakStartTextView) {
            linkedTextViewId = R.id.fridayBreakEndTextView;
        } else if (textViewId == R.id.saturdayWorkStartTextView) {
            linkedTextViewId = R.id.saturdayWorkEndTextView;
        } else if (textViewId == R.id.saturdayBreakStartTextView) {
            linkedTextViewId = R.id.saturdayBreakEndTextView;
        } else if (textViewId == R.id.sundayWorkStartTextView) {
            linkedTextViewId = R.id.sundayWorkEndTextView;
        } else if (textViewId == R.id.sundayBreakStartTextView) {
            linkedTextViewId = R.id.sundayBreakEndTextView;
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
                        setTextViewTextAsTime(textView, selectedHour, selectedMinute);
                    } else {
                        // The time selected is not valid: show a message and reset the time picker.
                        Toast.makeText(context, String.format(context.getString(R.string.setTimeBefore), getFormattedTime(endTimeSpan.hours, endTimeSpan.minutes)), Toast.LENGTH_SHORT).show();
                        showTimePicker(textView, timeSpan, null, endTimeSpan, context);
                    }
                } else {
                    // This is an "end time".
                    if (selectedHour > startingTimeSpan.hours || (selectedHour == startingTimeSpan.hours && selectedMinute > startingTimeSpan.minutes)) {
                        // The user selected a valid time: write it to the schedule summary.
                        setTextViewTextAsTime(textView, selectedHour, selectedMinute);
                    } else {
                        // The time selected is not valid: show a message and reset the time picker.
                        Toast.makeText(context, String.format(context.getString(R.string.setTimeAfter), getFormattedTime(startingTimeSpan.hours, startingTimeSpan.minutes)), Toast.LENGTH_SHORT).show();
                        showTimePicker(textView, timeSpan, startingTimeSpan, null, context);
                    }
                }
            }
        }, timeSpan.hours, timeSpan.minutes, DateFormat.is24HourFormat(this));
        timePickerDialog.setTitle(this.getString(R.string.selectTime));
        timePickerDialog.show();
    }

    private void setTextViewTextAsTime(TextView textView, int hour, int minute) {
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
                R.id.mondayWorkSwitch, R.id.mondayWorkStartTextView, R.id.mondayWorkEndTextView, R.id.mondayBreakSwitch, R.id.mondayBreakStartTextView, R.id.mondayBreakEndTextView));
        allPreferences.put(Calendar.TUESDAY, this.getPreferences(
                R.id.tuesdayWorkSwitch, R.id.tuesdayWorkStartTextView, R.id.tuesdayWorkEndTextView, R.id.tuesdayBreakSwitch, R.id.tuesdayBreakStartTextView, R.id.tuesdayBreakEndTextView));
        allPreferences.put(Calendar.WEDNESDAY, this.getPreferences(
                R.id.wednesdayWorkSwitch, R.id.wednesdayWorkStartTextView, R.id.wednesdayWorkEndTextView, R.id.wednesdayBreakSwitch, R.id.wednesdayBreakStartTextView, R.id.wednesdayBreakEndTextView));
        allPreferences.put(Calendar.THURSDAY, this.getPreferences(
                R.id.thursdayWorkSwitch, R.id.thursdayWorkStartTextView, R.id.thursdayWorkEndTextView, R.id.thursdayBreakSwitch, R.id.thursdayBreakStartTextView, R.id.thursdayBreakEndTextView));
        allPreferences.put(Calendar.FRIDAY, this.getPreferences(
                R.id.fridayWorkSwitch, R.id.fridayWorkStartTextView, R.id.fridayWorkEndTextView, R.id.fridayBreakSwitch, R.id.fridayBreakStartTextView, R.id.fridayBreakEndTextView));
        allPreferences.put(Calendar.SATURDAY, this.getPreferences(
                R.id.saturdayWorkSwitch, R.id.saturdayWorkStartTextView, R.id.saturdayWorkEndTextView, R.id.saturdayBreakSwitch, R.id.saturdayBreakStartTextView, R.id.saturdayBreakEndTextView));
        allPreferences.put(Calendar.SUNDAY, this.getPreferences(
                R.id.sundayWorkSwitch, R.id.sundayWorkStartTextView, R.id.sundayWorkEndTextView, R.id.sundayBreakSwitch, R.id.sundayBreakStartTextView, R.id.sundayBreakEndTextView));

        // Save the new schedule.
        Context context = this.getApplicationContext();
        boolean isFirstConfiguration = PreferencesHelper.areScheduleSettingsSet(context);
        PreferencesHelper.setScheduleSettings(allPreferences, context);

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
            // Show a toast message and close this activity.
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
