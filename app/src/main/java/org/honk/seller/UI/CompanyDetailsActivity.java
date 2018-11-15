package org.honk.seller.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.honk.seller.PreferencesHelper;
import org.honk.seller.R;
import org.honk.seller.model.CompanyDetails;

public class CompanyDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companydetails);

        CompanyDetails companyDetails = PreferencesHelper.getCompanyDetails(this.getApplicationContext());
        ((EditText)this.findViewById(R.id.companyNameEditText)).setText(companyDetails.name);
        ((EditText)this.findViewById(R.id.companyDescriptionEditText)).setText(companyDetails.description);

        // TODO It would be nice to show a preview of the Maps placeholder that will be shown to customers
    }

    public void SaveAndClose(View view) {

        Context context = this.getApplicationContext();
        boolean isFirstConfiguration = PreferencesHelper.areScheduleSettingsSet(context);
        EditText companyNameEditText = this.findViewById(R.id.companyNameEditText);
        String companyName = companyNameEditText.getText().toString();
        String companyDescription = ((EditText)this.findViewById(R.id.companyDescriptionEditText)).getText().toString();
        CompanyDetails companyDetails = new CompanyDetails(companyName, companyDescription);
        PreferencesHelper.setCompanyDetails(companyDetails, context);
        if (isFirstConfiguration) {
            // This is the first configuration: show the "set schedule" activity.
            if (TextUtils.isEmpty(companyName)) {
                companyNameEditText.setError(this.getString(R.string.companyNameRequired));
            } else {
                Intent openSetScheduleIntent = new Intent(this, SetScheduleActivity.class);
                this.startActivity(openSetScheduleIntent);
            }
        } else {
            // Show a toast message and close this activity.
            this.finishAffinity();
            Toast.makeText(this.getApplicationContext(), this.getString(R.string.companyDetailsSet), Toast.LENGTH_SHORT).show();
        }
    }
}
