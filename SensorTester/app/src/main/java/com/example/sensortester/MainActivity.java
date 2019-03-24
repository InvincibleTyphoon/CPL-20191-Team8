package com.example.sensortester;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.security.Permission;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;
    TextView mText;
    private int testCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener((SensorEventListener) this,mSensor,mSensorManager.SENSOR_DELAY_NORMAL);
        mText = (TextView)findViewById(R.id.SensorValue);


        Timer t = new Timer();

        t.scheduleAtFixedRate(
                new TimerTask()
                {
                    public void run()
                    {

                        Log.i("Thirty","timer task start. count : " + testCount);
                        String wifiScanResult = "";
                        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                        final List<ScanResult> scanResultList =  wm.getScanResults();

                        for (ScanResult sr: scanResultList) {
                            if(sr != null)
                                wifiScanResult = wifiScanResult + sr.SSID + " " + sr.level + "\n";
                        }

                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        final String finalText = wifiScanResult;
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                updateWifiText(finalText);
                            } // This is your code
                        };
                        mainHandler.post(myRunnable);

                        Log.i("Thirty","timer task end. count : " + testCount);
                        testCount++;
                    }
                },
                0,      // run first occurrence immediatetly
                1000); // run every two seconds

    }


    public void updateWifiText(String text)
    {
        TextView wifiText = (TextView)findViewById(R.id.WifiValue);
        wifiText.setText(text);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        mText.setText(String.valueOf(event.values[0])+ ", " + String.valueOf(event.values[1])+ ", " + String.valueOf(event.values[1]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
