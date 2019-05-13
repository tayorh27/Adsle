package com.ad.adsle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ad.adsle.Activities.HomeActivity;
import com.ad.adsle.Activities.InterestActivity;
import com.ad.adsle.Activities.LoginActivity;
import com.ad.adsle.Db.AppData;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    AppData data;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        data = new AppData(MainActivity.this);
                        if(data.getLogged()){
                            if(data.getInterestSelected()) {
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            }else {
                                startActivity(new Intent(MainActivity.this, InterestActivity.class));
                            }
                        }else {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    }
                });
            }
        }, 4000);
//        try {
//            Thread.sleep(4000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }finally {
//
//        }
    }
}
