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

                //센서 데이터 및 와이파이 데이터 Json화
                JSONObject sensorJson = sensorDataGetter.getSensorDataJson();
                JSONObject wifiScanResultJson = wifiScanner.getWifiScanResultJson();
                if(sensorJson == null)
                    return;

                //센서 데이터와 와이파이 데이터를 하나의 Json 안에 합치는 과정
                try {
                    final JSONObject jsonObject = new JSONObject();


                    for (Iterator<String> it = sensorJson.keys(); it.hasNext(); ) {
                        String key = it.next();
                        jsonObject.put(key,sensorJson.get(key));
                    }

                    for (Iterator<String> it = wifiScanResultJson.keys(); it.hasNext(); ) {
                        String key = it.next();
                        jsonObject.put(key,wifiScanResultJson.get(key));
                    }

                    final TextView jsonTextView = (TextView)findViewById(R.id.JsonData);

                    //긴 텍스트를 TextView에 삽입시 오류 발생
                    jsonTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            jsonTextView.setText(jsonObject.toString());
                        }
                    });


                    //전송 큐에 데이터 탑재시킴
                    //httpRequester.addToSendQueue(sensorDataGetter.getSensorDataJson());
                    httpRequester.addToSendQueue(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },0, (int) (Settings.sensorDataUpdateInterval * 1000));
    }


    //데이터 수집 버튼 클릭 이벤트 처리
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

    //전송 버튼 클릭
    public void onSendButtonClicked(View view)
    {
        httpRequester.postObjectsInQueue();
    }
}
