package com.ad.adsle.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.ad.adsle.Db.AppData;
import com.ad.adsle.Information.User;
import com.ad.adsle.R;
import com.ad.adsle.Util.Utils;
import com.ad.adsle.network.TLSSocketFactory;
import com.braintreepayments.cardform.OnCardFormSubmitListener;
import com.braintreepayments.cardform.view.CardForm;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.exceptions.ExpiredAccessCodeException;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.TlsVersion;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PaymentActivity extends AppCompatActivity implements OnCardFormSubmitListener {

    Charge charge;
    Utils utils;
    AppData data;
    User user;
    int total_amount = 0;
    Card payStackCard;
    Transaction transaction;
    String paystack_verify_url = "https://api.paystack.co/transaction/", paystack_secret_key = "sk_test_4108c44ace23af3893b4f7959bf810a3f15cac39", paystack_public_key = "pk_test_fe5e7a92b2ac1e14defe1172eec62d743f974915";
    String card_reference = "";
    CardForm cardForm;

    private static List<ConnectionSpec> createConnectionSpecs() {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_RC4_128_SHA,
                        CipherSuite.TLS_ECDHE_RSA_WITH_RC4_128_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_DHE_DSS_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_256_CBC_SHA)
                .build();
        return Collections.singletonList(spec);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        cardForm = findViewById(R.id.card_form);
        utils = new Utils(PaymentActivity.this);
        data = new AppData(PaymentActivity.this);
        user = data.getUser();
        total_amount = (int) getIntent().getExtras().getLong("total_amount", 0);
        //Log.e("total_amount", "onCreate: " + total_amount);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("SAVE")
                .setup(PaymentActivity.this);
        cardForm.setOnCardFormSubmitListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        PaystackSdk.setPublicKey(paystack_public_key);
    }

    private void GetAccessCode() {
        String amt = total_amount + "00";
        charge = new Charge();
        charge.setCard(payStackCard); //sets the card to charge
        charge.setCurrency("NGN");
        charge.setEmail(user.getEmail());
        charge.setAmount(Integer.parseInt(amt));
        utils.displayDialog("Initializing transaction");
        final String url = paystack_verify_url + "initialize";
        final JSONObject object = new JSONObject();
        try {
            Random r = new Random();
            String ref = user.getName().substring(0, 2).toUpperCase() + r.nextInt(999) + user.getEmail().substring(0, 2).toUpperCase() + r.nextInt(999);
            object.put("reference", ref);
            object.put("amount", total_amount + "00");
            object.put("email", user.getEmail());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String response = ChargeTransaction(url, object.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            try {
                                JSONObject obj = new JSONObject(response);
                                boolean status = obj.getBoolean("status");
                                String message = obj.getString("message");
                                if (status) {
                                    String code = obj.getJSONObject("data").getString("access_code");
                                    charge.setAccessCode(code);
                                    Toast.makeText(getApplicationContext(), "Please wait...", Toast.LENGTH_LONG).show();
                                    ProcessPayment();
                                } else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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

    private void ProcessPayment() {
        transaction = null;
        PaystackSdk.chargeCard(PaymentActivity.this, charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(Transaction transaction) {
                utils.dismissDialog();
                utils.displayDialog("Verifying Transaction");
                String url = paystack_verify_url + "verify/" + transaction.getReference();
                VerifyTransaction(url);
            }

            @Override
            public void beforeValidate(Transaction transaction) {

            }

            @Override
            public void onError(Throwable error, Transaction transaction) {
                if (error instanceof ExpiredAccessCodeException) {
                    Toast.makeText(getApplicationContext(), "Operation time-out", Toast.LENGTH_SHORT).show();
                    GetAccessCode();
                    return;
                }
                if (transaction.getReference() != null) {
                    Toast.makeText(getApplicationContext(), transaction.getReference() + " concluded with error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void VerifyTransaction(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String res = TransactionVerification(url);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            utils.dismissDialog();
                            try {
                                JSONObject object = new JSONObject(res);
                                boolean status = object.getBoolean("status");
                                String message = object.getString("message");
                                String _message = object.getJSONObject("data").getString("status");
                                if (status && _message.contentEquals("success")) {
                                    card_reference = object.getJSONObject("data").getString("reference");
                                    String authCode = object.getJSONObject("data").getJSONObject("authorization").getString("authorization_code");
                                    Intent intent = new Intent();
                                    intent.putExtra("status", true);
                                    intent.putExtra("authCode", authCode);
                                    intent.putExtra("card_reference", card_reference);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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

    String TransactionVerification(String url) throws IOException {
        OkHttpClient client = null;
        try {
            TLSSocketFactory tls = new TLSSocketFactory();
            client = new OkHttpClient.Builder()
                    .connectionSpecs(createConnectionSpecs())
                    .sslSocketFactory(tls, tls.x509TrustManager)
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        //RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + paystack_secret_key)
                .get()
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String ChargeTransaction(String url, String json) throws IOException {
        OkHttpClient client = null;
        try {
            TLSSocketFactory tls = new TLSSocketFactory();
            client = new OkHttpClient.Builder()
                    .connectionSpecs(createConnectionSpecs())
                    .sslSocketFactory(tls, tls.x509TrustManager)
                    .build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + paystack_secret_key)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    public void onCardFormSubmit() {
        String card_number = cardForm.getCardNumber();
        String card_month = cardForm.getExpirationMonth();
        String card_year = cardForm.getExpirationYear();
        String card_cvv = cardForm.getCvv();

        if (TextUtils.isEmpty(card_number) || TextUtils.isEmpty(card_month) || TextUtils.isEmpty(card_year) || TextUtils.isEmpty(card_cvv)) {
            utils.error("All fields must be filled.");
            return;
        }

        int c_m = Integer.parseInt(card_month);
        int c_y = Integer.parseInt(card_year);
        payStackCard = new Card(card_number, c_m, c_y, card_cvv);
        if (!payStackCard.isValid()) {
            utils.error("Card is not valid.");
        } else {
            GetAccessCode();
        }
    }
}
