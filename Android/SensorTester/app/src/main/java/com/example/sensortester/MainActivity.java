package com.example.sensortester;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.icu.util.Output;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

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
    }


    public void updateWifiText(String text)
    {
        TextView wifiText = (TextView)findViewById(R.id.WifiValue);
        wifiText.setText(text);
    }

    private void addWifiScanResultToJsonObj(JSONObject jsonObj)
    {
        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        final List<ScanResult> scanResultList =  wm.getScanResults();
        int size = scanResultList.size();
        ArrayList<ArrayList<String>> selectedDatumList = new ArrayList<ArrayList<String>>();

        for(int i = 0 ; i < size; i ++)
        {
            if(scanResultList.get(i) != null)
            {
                selectedDatumList.add(new ArrayList<String>());
                selectedDatumList.get(selectedDatumList.size() - 1).add(scanResultList.get(i).SSID);
                selectedDatumList.get(selectedDatumList.size() - 1).add(Integer.toString(scanResultList.get(i).level));
            }
        }
        try {
            jsonObj.put("WifiScanResult",selectedDatumList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mText.setText(String.valueOf(event.values[0])+ ", " + String.valueOf(event.values[1])+ ", " + String.valueOf(event.values[1]));
        final JSONObject jsonObj = new JSONObject();
        float[] magnetic = {event.values[0],event.values[1],event.values[2]};
        try {
            jsonObj.put("Magnetic",magnetic.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addWifiScanResultToJsonObj(jsonObj);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                URL serverEndPoint = null;
                try {
                    serverEndPoint = new URL(Settings.serverAddress);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection myConnection = (HttpURLConnection) serverEndPoint.openConnection();
                    //myConnection.setRequestProperty("Content-Type", "application/json;");
                    //myConnection.setRequestProperty("Accept","application/json");
                    myConnection.setRequestMethod("POST");

                    myConnection.setDoOutput(true);
                    OutputStream outputStream = new BufferedOutputStream(myConnection.getOutputStream());
                    //myConnection.connect();
                    Log.i("Thirty","sending data : " + jsonObj.toString());

                    outputStream.write(jsonObj.toString().getBytes());
                    //outputStream.write(123);
                    outputStream.flush();
                    outputStream.close();

                    Log.i("Thirty","response code : " + myConnection.getResponseCode());

                    myConnection.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
