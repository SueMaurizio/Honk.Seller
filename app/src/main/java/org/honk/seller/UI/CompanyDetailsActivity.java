package org.honk.seller.UI;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.honk.seller.R;

public class CompanyDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companydetails);

        // TODO The company name should be mandatory
        // TODO It would be nice to show a preview of the Maps placeholder that will be shown to customers
    }

    public void openSetScheduleActivity(View view) {
        Intent openSetScheduleIntent = new Intent(this, SetScheduleActivity.class);
        this.startActivity(openSetScheduleIntent);
    }
}
