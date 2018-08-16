package org.honk.seller.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.honk.seller.NotificationsHelper;
import org.honk.seller.R;
import org.honk.seller.services.SchedulerJobService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Show the current status of the job scheduler.
        TextView txtStatus = this.findViewById(R.id.txtStatus);
        if (SchedulerJobService.active) {
            if (SchedulerJobService.isWorkTime(this.getBaseContext())) {
                txtStatus.setText(this.getString(R.string.ImWorking));
            } else {
                txtStatus.setText(this.getString(R.string.ImNotWorking));
            }
        } else {
            // The service is disabled: change the label of the "keep running" button to "start running".
            TextView btnRun = this.findViewById(R.id.btnRun);
            btnRun.setText(this.getString(R.string.startRunning));
        }
    }

    // Called when the user asks to stop the service indefinitely.
    public void stopService(View view) {
        SchedulerJobService.cancelAllJobs(this.getBaseContext());
        SchedulerJobService.active = false;

        // Show a message and close the app.
        NotificationsHelper.showNotification(
                this.getBaseContext(), this.getString(R.string.seeYouSoon), this.getString(R.string.comeBackToResume), null, null, null, true);
        this.finishAffinity();
    }

    public void setSchedule(View view) {
        Intent intent = new Intent(this, SetScheduleActivity.class);
        startActivity(intent);
        finish();
    }

    public void keepRunning(View view) {
        // If the job scheduler was disabled, I resume it, then I close this activity.
        if (!SchedulerJobService.active || SchedulerJobService.pausedUntil != null) {
            SchedulerJobService.active = true;
            SchedulerJobService.pausedUntil = null;
            SchedulerJobService.scheduleJob(this.getBaseContext());

            NotificationsHelper.showNotification(
                    this.getBaseContext(), this.getString(R.string.congratulations), this.getString(R.string.schedulerStarted), null, null, null, true);
        }

        this.finishAffinity();
    }

    public void setVacations(View view) {
        Intent intent = new Intent(this, SetVacationsActivity.class);
        startActivity(intent);
        finish();
    }
}
