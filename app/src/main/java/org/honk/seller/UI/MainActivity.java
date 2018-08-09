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

import org.honk.seller.R;
import org.honk.seller.UI.commons.DatePickerFragment;
import org.honk.seller.services.SchedulerJobService;

import java.util.Calendar;

public class MainActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener, DialogInterface.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void pickDate(View view) {
        /*Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getBaseContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // TODO
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle(this.getString(R.string.whenWillYouBeBack));
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, this.getString(R.string.dontKnowYet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO
            }
        });
        datePickerDialog.show();*/

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

    @Override
    public void onClick(DialogInterface dialog, int which) {
        // TODO
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
