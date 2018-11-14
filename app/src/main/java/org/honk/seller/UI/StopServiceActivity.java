package org.honk.seller.UI;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import org.honk.seller.NotificationsHelper;
import org.honk.seller.R;
import org.honk.seller.UI.commons.DatePickerFragment;
import org.honk.seller.services.SchedulerJobService;

import java.util.Calendar;

public class StopServiceActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener, DialogInterface.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchedulerJobService.active = false;
        setContentView(R.layout.activity_stopservice);

        // Dismiss the message the user clicked on to open this activity.
        NotificationsHelper.dismissCurrentNotification((this.getBaseContext()));
    }

    public void pickDate(View view) {
        DialogFragment datePickerFragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putString(DatePickerFragment.ARGUMENT_TITLE, this.getString(R.string.whenWillYouBeBack));
        args.putBoolean(DatePickerFragment.ARGUMENT_ADD_DON_T_KNOW_BUTTON, true);

        // Compute the minimum date to be selectable in the date picker.
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);
        args.putLong(DatePickerFragment.ARGUMENT_MIN_DATE_MILLIS, tomorrow.getTimeInMillis());

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
        this.finishAffinity();
        Toast.makeText(this.getApplicationContext(), this.getString(R.string.comeBackToResume), Toast.LENGTH_LONG).show();
    }

    public void restartServiceAndClose(View view) {
        SchedulerJobService.active = true;
        this.finishAffinity();
    }

    public void stopService(View view) {
        // The service must be stopped but the user configuration should be maintained.
        SchedulerJobService.cancelAllJobs(this.getBaseContext());
        SchedulerJobService.active = false;
    }
}
