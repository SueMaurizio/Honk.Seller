package org.honk.seller.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
                txtStatus.setText(this.getString(R.string.imWorking));
            } else {
                txtStatus.setText(this.getString(R.string.imNotWorking));
            }
        } else {
            // The service is disabled: change the label of the "keep running" button to "start running" and hide the "stop sending my location" button.
            TextView btnRun = this.findViewById(R.id.btnRun);
            btnRun.setText(this.getString(R.string.startRunning));
            TextView btnStop = this.findViewById(R.id.btnStop);
            btnStop.setVisibility(View.GONE);
        }
    }

    // Called when the user asks to stop the service indefinitely.
    public void stopService(View view) {
        SchedulerJobService.cancelAllJobs(this.getBaseContext());
        SchedulerJobService.active = false;

        // Close the app and show a confirmation message.
        this.finishAffinity();
        Toast.makeText(this.getApplicationContext(), this.getString(R.string.comeBackToResume), Toast.LENGTH_LONG).show();
    }

    // Starts the "set schedule" activity.
    public void setSchedule(View view) {
        Intent intent = new Intent(this, SetScheduleActivity.class);
        this.startActivity(intent);
        finish();
    }

    // If the service is running, simply closes the app; otherwise it starts the service first.
    public void keepRunning(View view) {
        // If the job scheduler was disabled, I resume it, then I close this activity.
        if (!SchedulerJobService.active || SchedulerJobService.pausedUntil != null) {
            SchedulerJobService.active = true;
            SchedulerJobService.pausedUntil = null;
            SchedulerJobService.scheduleJob(this.getBaseContext());

            // Close the app and show a confirmation message.
            this.finishAffinity();
            Toast.makeText(this.getApplicationContext(), this.getString(R.string.schedulerStarted), Toast.LENGTH_LONG).show();
        } else {
            this.finishAffinity();
        }
    }

    // Opens the "set vacations" activity.
    public void setVacations(View view) {
        Intent intent = new Intent(this, SetVacationsActivity.class);
        this.startActivity(intent);
        finish();
    }

    // Opens the "set company details" activity.
    public void setCompanyDetails(View view) {
        Intent intent = new Intent(this, CompanyDetailsActivity.class);
        this.startActivity(intent);
        finish();
    }
}
