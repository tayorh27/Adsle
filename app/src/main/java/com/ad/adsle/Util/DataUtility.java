package com.ad.adsle.Util;

import android.content.Context;
import android.util.Log;

import com.ad.adsle.AppConfig;
import com.ad.adsle.network.TLSSocketFactory;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.TlsVersion;

public class DataUtility {

    String username, password;

    public DataUtility() {

    }

    public DataUtility(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String SendData(String number, String token, String product_id, String denomination, String sms_text) throws IOException {
        String national = "";
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse("+234" + number, "");
            national = "234" + String.valueOf(phoneNumber.getNationalNumber());
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        String url = AppConfig.DATA_API_URL + "datatopup/exec/" + national;
        //Log.e("url", url);
        JSONObject json = new JSONObject();
        try {
            json.put("product_id", product_id);
            json.put("denomination", denomination);
            json.put("send_sms", true);
            json.put("sms_text", sms_text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.e("json", json.toString());
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
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String GetToken() throws IOException {
        String url = AppConfig.DATA_API_URL + "auth";
        JSONObject json = new JSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
        okhttp3.Response response = client.newCall(request).execute();
        return response.body().string();
    }

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
}
