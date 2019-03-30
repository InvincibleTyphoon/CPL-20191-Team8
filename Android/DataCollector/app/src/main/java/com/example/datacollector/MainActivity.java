package com.example.datacollector;

import android.content.Context;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private HttpRequester httpRequester = null;
    private SensorDataGetter sensorDataGetter = null;
    private WifiScanner wifiScanner = null;
    private boolean isCollectingMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        httpRequester = new HttpRequester();
        sensorDataGetter = new SensorDataGetter((SensorManager)getSystemService(SENSOR_SERVICE));
        wifiScanner = new WifiScanner((WifiManager)getSystemService(Context.WIFI_SERVICE));

        //설정된 시간마다 스캔
        final Timer sensorDataUpdateTimer = new Timer();
        sensorDataUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //콜렉팅 모드가 아니면 종료
                if(!isCollectingMode)
                    return;

                JSONObject sensorJson = sensorDataGetter.getSensorDataJson();
                JSONObject wifiScanResultJson = wifiScanner.getWifiScanResultJson();
                if(sensorJson == null)
                    return;

                try {
                    JSONObject jsonObject = new JSONObject();


                    for (Iterator<String> it = sensorJson.keys(); it.hasNext(); ) {
                        String key = it.next();
                        jsonObject.put(key,sensorJson.get(key));
                    }

                    for (Iterator<String> it = wifiScanResultJson.keys(); it.hasNext(); ) {
                        String key = it.next();
                        jsonObject.put(key,wifiScanResultJson.get(key));
                    }

                    //((TextView)findViewById(R.id.JsonData)).setText(jsonObject.toString());
                    //((TextView)findViewById(R.id.JsonData)).setText("asdaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\naaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                    Log.i("Thirty","Seding : " + jsonObject.toString());

                    httpRequester.addToSendQueue(sensorDataGetter.getSensorDataJson());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },0, (int) (Settings.sensorDataUpdateInterval * 1000));
    }

    public void onDataCollectButtonClicked(View view)
    {
        if(isCollectingMode)
        {
            isCollectingMode = false;
            ((Button) view).setText("Start");
        }else{
            isCollectingMode = true;
            ((Button) view).setText("Stop");
        }

    }

    public void onSendButtonClicked(View view)
    {
        httpRequester.postObjectsInQueue();
    }
}
