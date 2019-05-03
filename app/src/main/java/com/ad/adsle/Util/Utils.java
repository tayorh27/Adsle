package com.ad.adsle.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Utils {

    private Context mContext = null;
    private MaterialDialog materialDialog;

    /**
     * Public constructor that takes mContext for later use
     */
    public Utils(Context con) {
        mContext = con;
    }

    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    //This is a method to Check if the device internet connection is currently on
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager

                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public MaterialDialog getMaterialDialog() {
        return materialDialog;
    }

    public void displayDialog(String text) {
        materialDialog = new MaterialDialog.Builder(mContext)
                .content(text)
                .canceledOnTouchOutside(true)
                .cancelable(false)
                .progress(true, 0)
                .show();
    }

    public void dismissDialog() {
        materialDialog.dismiss();
    }

    public void error(String text) {
        new MaterialDialog.Builder(mContext)
                .title("Error")
                .content(text)
                .positiveText("OK")
                .show();
    }


    public static String CopyTo(Bitmap bitmap, String username) {
        String new_file_path = "";
        FileOutputStream fileOutputStream = null;
        try {
            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File myDir1 = new File(root + "/KIT/");
            myDir1.mkdirs();
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            int milli = calendar.get(Calendar.MILLISECOND);
            File new_image_file = new File(myDir1.toString() + "/logo_" + username + "_" + (day + milli) + "" + (month + 1) + "" + year + "" + milli + "_" + new Random().nextInt(93564) + ".png");
            Log.e("dir", myDir1.toString() + "\n" + new_image_file.toString());

            String dest = new_image_file.getPath();
            new_file_path = dest;

            fileOutputStream = new FileOutputStream(dest);
            //Bitmap bitmap = BitmapFactory.decodeFile(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

        } catch (FileNotFoundException e) {
            Log.e("file exception", e.toString());
        } catch (Exception ex) {
            Log.e("normal exception", ex.toString());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    Log.e("io exception", e.toString());
                }
            }
        }
        return new_file_path;
    }

    public static int AddNumber() {
        Calendar calendar = Calendar.getInstance();
        int milli = calendar.get(Calendar.MILLISECOND);
        int sec = calendar.get(Calendar.SECOND);
        int min = calendar.get(Calendar.MINUTE);
        int hr = calendar.get(Calendar.HOUR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int mt = calendar.get(Calendar.MONTH);
        int yr = calendar.get(Calendar.YEAR);

        return (yr + mt - day * hr + min - sec + milli);
    }


    public SSLContext getSslContext() {
        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};

        SSLContext sslContext = null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }

    public boolean getSupportedCountry(String address) {
        if (address.contains("Nigeria")) {
            return true;
        } else if (address.contains("USA")) {
            return true;
        } else return address.contains("UK");
    }

    public JSONArray NetworkProviders() {
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray.put(0, new JSONObject().put("Airtel", "0701"));
            //jsonArray.put(0, new JSONObject().put("Smile","0702"));
            jsonArray.put(1, new JSONObject().put("MTN", "0703"));
            jsonArray.put(2, new JSONObject().put("Glo", "0705"));
            jsonArray.put(3, new JSONObject().put("MTN", "0706"));
            jsonArray.put(4, new JSONObject().put("Airtel", "0708"));
            jsonArray.put(5, new JSONObject().put("Airtel", "0802"));
            jsonArray.put(6, new JSONObject().put("MTN", "0803"));
            jsonArray.put(7, new JSONObject().put("Glo", "0805"));
            jsonArray.put(8, new JSONObject().put("MTN", "0806"));
            jsonArray.put(9, new JSONObject().put("Glo", "0807"));
            jsonArray.put(10, new JSONObject().put("Airtel", "0808"));
            jsonArray.put(11, new JSONObject().put("9mobile", "0809"));
            jsonArray.put(12, new JSONObject().put("MTN", "0810"));
            jsonArray.put(13, new JSONObject().put("Glo", "0811"));
            jsonArray.put(14, new JSONObject().put("Airtel", "0812"));
            jsonArray.put(15, new JSONObject().put("MTN", "0813"));
            jsonArray.put(16, new JSONObject().put("MTN", "0814"));
            jsonArray.put(17, new JSONObject().put("Glo", "0815"));
            jsonArray.put(18, new JSONObject().put("MTN", "0816"));
            jsonArray.put(19, new JSONObject().put("9mobile", "0817"));
            jsonArray.put(20, new JSONObject().put("9mobile", "0818"));
            jsonArray.put(21, new JSONObject().put("9mobile", "0819"));
            jsonArray.put(22, new JSONObject().put("9mobile", "0909"));
            jsonArray.put(23, new JSONObject().put("9mobile", "0908"));
            jsonArray.put(24, new JSONObject().put("Airtel", "0902"));
            jsonArray.put(25, new JSONObject().put("MTN", "0903"));
            jsonArray.put(26, new JSONObject().put("Glo", "0905"));
            jsonArray.put(27, new JSONObject().put("MTN", "0906"));
            jsonArray.put(28, new JSONObject().put("Airtel", "0907"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public String CheckBalanceCode(String number) {
        JSONArray jsonArray = NetworkProviders();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                String a = object.toString().replace("{", "").replace("}", "").replace(":", ",");
                String[] b = a.split(",");
                String name = b[0].substring(1, b[0].length() - 1);
                String value = b[1].substring(1, b[1].length() - 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String GetNetworkProviderType(String number) {
        String network = "";
        JSONArray jsonArray = NetworkProviders();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject object = jsonArray.getJSONObject(i);
                String a = object.toString().replace("{", "").replace("}", "").replace(":", ",");
                String[] b = a.split(",");
                String name = b[0].substring(1, b[0].length() - 1);
                String value = b[1].substring(1, b[1].length() - 1);
                String num = number.replace(" ", "").replace("-", "").replace("+234", "0");
                //Log.e("num", num);
                if (num.startsWith(value)) {
                    network = name.toLowerCase();
                    break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return network;
    }

    public int NetworkIcon(String number) {
        String network = GetNetworkProviderType(number);
        if (network.contentEquals("mtn")) {
            return 1;
        }
        return -1;
    }

    public String getExactDataValue(String byteValue) {
        long _byte = Long.parseLong(byteValue);
        double _kilobyte = _byte / 1024;
        double _megabyte = _kilobyte / 1024;
        double _gigabyte = _megabyte / 1024;

        int g = (int) _gigabyte;
        int m = (int) _megabyte;
        int k = (int) _kilobyte;

        if (g > 1) {
            return g + "GB";
        } else if (m > 1) {
            return m + "MB";
        } else if (k > 1) {
            return k + "KB";
        } else {
            return "0B";
        }
    }

//    public String getWifiName() {
//        WifiManager manager = (WifiManager) MyApplication.getAppContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        if (manager.isWifiEnabled()) {
//            WifiInfo wifiInfo = manager.getConnectionInfo();
//            if (wifiInfo != null) {
//                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
//                Log.e("NetworkInfo", state.name());
//                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR ||
//                        state == NetworkInfo.DetailedState.AUTHENTICATING || state == NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK) {
//                    return wifiInfo.getSSID().substring(1, wifiInfo.getSSID().length() - 1);
//                }
//            }
//        }
//        return null;
//    }


}
