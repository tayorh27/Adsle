package com.ad.adsle.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ad.adsle.AppConfig;
import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.Plans;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.DataUtility;
import com.ad.adsle.Util.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class TopupActivity extends AppCompatActivity {

    AppCompatTextView below_text;

    AppData data;
    Utils utils;
    User user;

    Plans plan = null;
    String rechargeNumber, userChoice;

    DataUtility dataUtility;

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

    private void LoadUserWithData() {
        utils.displayDialog("Sending data...");
        final String token = data.getTokenForData()[0];
        final String sms = "You have successfully been recharged with " + utils.getExactDataValue(plan.getData() + " by Adsle.");
        final String msg = "Your recharge of " + utils.getExactDataValue(plan.getData()) + " to " + rechargeNumber + " is successful.";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String res = dataUtility.SendData(rechargeNumber, token, plan.getProduct_id(), plan.getPrice().substring(1), sms);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                //Log.e("res", res);
                                if (jsonObject.getInt("status") == 201) {
                                    SubtractData(msg);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void SubtractData(String msg) {
        String email = user.getNumber();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dr = db.collection("users").document(email).collection("user-data").document("signup");
        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User serverUser = task.getResult().toObject(User.class);
                    long debited_plan = Long.parseLong(plan.getData());
                    long server_plan = serverUser.getBonus_data();
                    long newData = server_plan - debited_plan;

                    Map<String, Object> params = new HashMap<>();
                    params.put("bonus_data", newData);
                    dr.update(params);
                    user.setBonus_data(newData);
                    data.StoreUsers(user);
                    Toast.makeText(TopupActivity.this, msg, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(TopupActivity.this, HomeActivity.class));
                    finish();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String username = AppConfig.SMS_USERNAME;
        String password = AppConfig.SMS_PASSWORD;
        if (TextUtils.isEmpty(data.getTokenForData()[0])) {
            GetTokenForData(username, password);
        } else {
            String date = data.getTokenForData()[1];
            String[] expiresDate = date.substring(0, date.indexOf("T")).split("-");
            String[] expiresTime = date.substring(date.indexOf("T") + 1, date.indexOf(".")).split(":");
            Date build = new Date(
                    Integer.parseInt(expiresDate[0]),
                    Integer.parseInt(expiresDate[1]),
                    Integer.parseInt(expiresDate[2]),
                    Integer.parseInt(expiresTime[0]),
                    Integer.parseInt(expiresTime[1]),
                    Integer.parseInt(expiresTime[2]));
            Date currentDate = new Date();
            long diff = build.getTime() - currentDate.getTime();
            if (diff < 0) {
                GetTokenForData(username, password);
            } else {
                LoadUserWithData();
            }
        }
    }

    private void GetTokenForData(String username, String password) {
        dataUtility = new DataUtility(username, password);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String res = dataUtility.GetToken();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(res);
                                data.SaveTokenForData(jsonObject.getString("token"), jsonObject.getString("expires"));
                                LoadUserWithData();
                                //String date = jsonObject.getString("expires");
                                //Log.e("date", date.substring(0, date.indexOf("T")));
                                //Log.e("time", date.substring(date.indexOf("T") + 1, date.indexOf(".")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
