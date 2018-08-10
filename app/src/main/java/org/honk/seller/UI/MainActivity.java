package org.honk.seller.UI;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.honk.seller.R;
import org.honk.seller.UI.commons.DatePickerFragment;
import org.honk.seller.services.SchedulerJobService;

import java.util.Calendar;

public class MainActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener, DialogInterface.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtStatus = this.findViewById(R.id.txtStatus);
        if (SchedulerJobService.isWorkTime(this.getBaseContext())) {
            txtStatus.setText(this.getString(R.string.ImWorking));
        } else {
            txtStatus.setText(this.getString(R.string.ImNotWorking));
        }
    }

    public void pickDate(View view) {
        DialogFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString(DatePickerFragment.ARGUMENT_TITLE, this.getString(R.string.whenWillYouBeBack));
        args.putBoolean(DatePickerFragment.ARGUMENT_ADDDONTKNOWBUTTON, true);
        datePickerFragment.setArguments(args);
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        SchedulerJobService.cancelAllJobs(this.getBaseContext());
        Calendar pauseEnd = Calendar.getInstance();
        pauseEnd.set(year, month, day);
        SchedulerJobService.pausedUntil = pauseEnd;
    }

    // Called when the user presses "I don't know".
    @Override
    public void onClick(DialogInterface dialog, int which) {
        // Do nothing, the user hasn't selected a date.
    }

    public void setSchedule(View view) {
        Intent intent = new Intent(this, SetScheduleActivity.class);
        startActivity(intent);
        finish();
    }

    public void close(View view) {
        this.finishAffinity();
    }
}
