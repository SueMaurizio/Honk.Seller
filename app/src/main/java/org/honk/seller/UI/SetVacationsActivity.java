package org.honk.seller.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.savvi.rangedatepicker.CalendarPickerView;

import org.honk.seller.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SetVacationsActivity extends AppCompatActivity {

    private CalendarPickerView calendar;

    public static final String PREFERENCE_VACATIONS = "PREFERENCE_VACATIONS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setvacations);

        // Get and deserialize the vacations data.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getBaseContext());
        String settingsString = sharedPreferences.getString(PREFERENCE_VACATIONS, "");
        List<Date> vacationDates = null;
        if (settingsString != "") {
            vacationDates = new Gson().fromJson(settingsString, TypeToken.getParameterized(List.class, Date.class).getType());
        } else {
            vacationDates = new ArrayList<>();
        }

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        calendar = findViewById(R.id.calendar_view);
        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MMMM yyyy", Locale.getDefault()))
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                .withSelectedDates(vacationDates);
    }

    public void save(View view) {
        List<Date> selectedDates = calendar.getSelectedDates();
        Context context = this.getApplicationContext();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(PREFERENCE_VACATIONS, new Gson().toJson(selectedDates)).apply();

        // Close the app and show a confirmation message.
        this.finishAffinity();
        Toast.makeText(this.getApplicationContext(), this.getString(R.string.vacationsSet), Toast.LENGTH_SHORT).show();
    }
}
