package org.honk.seller.UI;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.savvi.rangedatepicker.CalendarPickerView;

import org.honk.seller.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SetVacationsActivity extends AppCompatActivity {

    private CalendarPickerView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setvacations);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Calendar lastYear = Calendar.getInstance();
        lastYear.add(Calendar.YEAR, -1);

        calendar = (CalendarPickerView) findViewById(R.id.calendar_view);

        Date first = new Date(118, 7, 22);
        Date second = new Date(118, 7, 23);
        Date third = new Date(118, 7, 5);
        Collection<Date> dates = new ArrayList<Date>();
        dates.add(first);
        dates.add(second);
        /*dates.add(third);*/
        calendar.init(lastYear.getTime(), nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                .withSelectedDates(dates);
// deactivates given dates, non selectable
                //.withDeactivateDates(list)
// highlight dates in red color, mean they are aleady used.
                //.withHighlightedDates(arrayList);

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ArrayList<Date> selectedDates = (ArrayList<Date>)calendar
                .getSelectedDates();
        Toast.makeText(SetVacationsActivity.this, selectedDates.toString(),
                Toast.LENGTH_LONG).show();
        return true;
    }

    private ArrayList<Date> getHolidays(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
        String dateInString = "21-04-2015";
        Date date = null;
        try {
            date = sdf.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ArrayList<Date> holidays = new ArrayList<>();
        holidays.add(date);
        return holidays;
    }
}
