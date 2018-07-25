package org.honk.seller.UI;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;

import org.honk.seller.R;
import org.honk.seller.model.DailySchedulePreferences;
import org.honk.seller.model.TimeSpan;
import org.honk.seller.services.SchedulerJobService;

import java.util.Calendar;
import java.util.Hashtable;

public class SetScheduleActivity extends AppCompatActivity {

    public static final String PREFERENCE_SCHEDULE = "PREFERENCE_SCHEDULE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setschedule);
    }

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

    public void pickTime(View view) {

        // Parse value from TextView.
        TextView textView = (TextView)view;
        String text = textView.getText().toString();
        int hour = Integer.parseInt(text.substring(0, 2));
        int minute = Integer.parseInt(text.substring(3, 5));

        // TODO: set parameter is24HourView based on the current locale.
        TimePickerDialog timePickerDialog = new TimePickerDialog(SetScheduleActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                textView.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
            }
        }, hour, minute, true);
        timePickerDialog.setTitle(this.getString(R.string.selectTime));
        timePickerDialog.show();
    }

    public void proceed(View view) {
        save();
    }

    private void save() {

        // TODO: fetch preferences from page.
        Hashtable<Integer, DailySchedulePreferences> allPreferences = new Hashtable<Integer, DailySchedulePreferences>();
        allPreferences.put(Calendar.MONDAY, new DailySchedulePreferences(new TimeSpan(8, 0), new TimeSpan(17, 0)));
        allPreferences.put(Calendar.TUESDAY, new DailySchedulePreferences(new TimeSpan(8, 0), new TimeSpan(17, 0)));
        allPreferences.put(Calendar.WEDNESDAY, new DailySchedulePreferences(new TimeSpan(8, 0), new TimeSpan(17, 0)));
        allPreferences.put(Calendar.THURSDAY, new DailySchedulePreferences(new TimeSpan(8, 0), new TimeSpan(17, 0)));
        allPreferences.put(Calendar.FRIDAY, new DailySchedulePreferences(new TimeSpan(8, 0), new TimeSpan(17, 0)));
        allPreferences.put(Calendar.SATURDAY, new DailySchedulePreferences(new TimeSpan(8, 0), new TimeSpan(17, 0)));
        allPreferences.put(Calendar.SUNDAY, new DailySchedulePreferences(new TimeSpan(8, 0), new TimeSpan(17, 0)));

        Context context = this.getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(PREFERENCE_SCHEDULE, new Gson().toJson(allPreferences)).apply();

        SchedulerJobService.checkAndSchedule(context);
    }
}
