package com.ad.adsle.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.os.Bundle;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.Plans;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TopupActivity extends AppCompatActivity {

    AppCompatTextView below_text;

    AppData data;
    Utils utils;
    User user;

    Plans plan = null;
    String rechargeNumber, userChoice;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        utils = new Utils(TopupActivity.this);
        data = new AppData(TopupActivity.this);
        user = data.getUser();

        below_text = findViewById(R.id.tvText);

        getIntentValues();
    }

    private void getIntentValues() {
        plan = getIntent().getParcelableExtra("plan");
        rechargeNumber = SetDataRechargeNumber(getIntent().getStringExtra("number"));
        userChoice = getIntent().getStringExtra("choice");

        below_text.setText("Crediting " + rechargeNumber + " with " + plan.getTitle() + " data.");
    }

    private String SetDataRechargeNumber(String number) {
        if (number.startsWith("+234") || number.startsWith("234")) {
            return number;
        }
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse("+234" + number, "");
            return "234" + phoneNumber.getNationalNumber();
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
