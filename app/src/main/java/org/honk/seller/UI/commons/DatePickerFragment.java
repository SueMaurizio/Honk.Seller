package org.honk.seller.UI.commons;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.honk.seller.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {

    public static final String ARGUMENT_TITLE = "ARGUMENT_TITLE";
    public static final String ARGUMENT_ADDDONTKNOWBUTTON = "ARGUMENT_ADDDONTKNOWBUTTON";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar now = Calendar.getInstance();

        Activity activity = this.getActivity();

        // Create a new instance of DatePickerDialog and return it
        FixedTitleDatePickerDialog dialog = new FixedTitleDatePickerDialog(
                activity,
                (DatePickerDialog.OnDateSetListener)activity,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH));

        // TODO: this should actually be the day after today, and it should also be configurable.
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());

        String title = this.getArguments().getString(ARGUMENT_TITLE);
        if (title != null) {
            dialog.setPermanentTitle(title);
        }

        boolean addDontKnowButton = this.getArguments().getBoolean(ARGUMENT_ADDDONTKNOWBUTTON);
        if (addDontKnowButton) {
            dialog.setButton(DialogInterface.BUTTON_NEUTRAL, this.getString(R.string.dontKnowYet), (DialogInterface.OnClickListener)activity);
        }

        return dialog;
    }

    private class FixedTitleDatePickerDialog extends DatePickerDialog {

        private String title;

        public FixedTitleDatePickerDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
            super(context, callBack, year, monthOfYear, dayOfMonth);
        }

        public void setPermanentTitle(String title) {
            this.title = title;
            setTitle(title);
        }

        @Override
        public void onDateChanged(DatePicker view, int year, int month, int day) {
            super.onDateChanged(view, year, month, day);
            setTitle(title);
        }
    }
}
