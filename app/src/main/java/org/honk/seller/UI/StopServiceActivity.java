package org.honk.seller.UI;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;

import org.honk.seller.R;
import org.honk.seller.services.SchedulerJobService;

import java.util.Calendar;

public class StopServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SchedulerJobService.active = false;
        setContentView(R.layout.activity_stopservice);
    }

    public void pickDate(View view) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this.getBaseContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar pausedUntil = Calendar.getInstance();
                pausedUntil.set(year, month, dayOfMonth);
                SchedulerJobService.pausedUntil = pausedUntil;
            }
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle(this.getString(R.string.whenWillYouBeBack));
        datePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, this.getString(R.string.dontKnowYet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO
            }
        });
        datePickerDialog.show();
    }
}
