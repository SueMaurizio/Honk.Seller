package org.honk.seller.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TimePicker;

import org.honk.seller.R;

public class SetScheduleActivity extends AppCompatActivity {

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
        TimePicker timePicker = findViewById(R.id.timePicker);
        timePicker.setVisibility(View.VISIBLE);
        Button doneButton = findViewById(R.id.doneButton);
        doneButton.setVisibility(View.INVISIBLE);
    }
}
