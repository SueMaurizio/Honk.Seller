package org.honk.seller.UI;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.honk.seller.BuildConfig;
import org.honk.seller.NotificationsHelper;
import org.honk.seller.PreferencesHelper;
import org.honk.seller.R;
import org.honk.seller.model.CompanyDetails;
import org.honk.seller.model.User;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

public class CompanyDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companydetails);

        CompanyDetails companyDetails = PreferencesHelper.getCompanyDetails(this.getApplicationContext());
        ((EditText)this.findViewById(R.id.companyNameEditText)).setText(companyDetails.name);
        ((EditText)this.findViewById(R.id.companyDescriptionEditText)).setText(companyDetails.description);

        // TODO low priority: It would be nice to show a preview of the Maps placeholder that will be shown to customers
    }

    public void SaveAndClose(View view) {

        Context context = this.getApplicationContext();

        // This is the first configuration if the schedule settings are not set.
        boolean isFirstConfiguration = !PreferencesHelper.areScheduleSettingsSet(context);

        // Save company details in case the user wishes to update them; send company details to the server.
        EditText companyNameEditText = this.findViewById(R.id.companyNameEditText);
        String companyName = companyNameEditText.getText().toString();
        String companyDescription = ((EditText)this.findViewById(R.id.companyDescriptionEditText)).getText().toString();
        CompanyDetails companyDetails = new CompanyDetails(companyName, companyDescription);
        PreferencesHelper.setCompanyDetails(companyDetails, context);
        PutSellerTask task = new PutSellerTask(companyDetails);
        try {
            task.execute(context);
        } catch (Exception x) {
            if(BuildConfig.DEBUG) {
                NotificationsHelper.showNotification(context, "Debug", "I caught an exception: " + x.toString());
            }
        }

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

    public static class PutSellerTask extends AsyncTask<Context, Void, Void> {

        private CompanyDetails companyDetails;

        PutSellerTask(CompanyDetails companyDetails) {
            this.companyDetails = companyDetails;
        }

        protected Void doInBackground(Context... params) {
            try {
                URL url = new URL("http://192.168.0.22/HonkServices/api/Seller");
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestMethod("PUT");
                httpCon.setRequestProperty("Content-Type", "application/json");
                httpCon.setRequestProperty("Accept", "application/json");

                // This will be a really simple JSON object: I'm building it by hand.
                MessageFormat messageFormat = new MessageFormat("'{'\"Id\":\"{0}\",\"AuthenticationSource\":{1},\"Name\":\"{2}\",\"Description\":\"{3}\"'}'");
                User currentUser = PreferencesHelper.getUser(params[0]);
                Object[] args = {currentUser.id, currentUser.authenticationType.getNumericValue(), this.companyDetails.name, this.companyDetails.description};

                OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream());
                out.write(messageFormat.format(args));
                out.flush();
                out.close();
                httpCon.getResponseCode();
            } catch (Exception e) {
                if(BuildConfig.DEBUG) {
                    NotificationsHelper.showNotification(params[0], "Debug", "I caught an exception: " + e.toString());
                }
            }

            return null;
        }
    }
}
