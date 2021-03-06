package com.example.datacollector;

import android.content.Context;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private float xCoord = 0.0f;
    private float yCoord = 0.0f;
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
                    addCoordInformationIntoJson(jsonObject,xCoord,yCoord);
                    //UI 작업은 메인 쓰레드에서 하지 않으면 오류가 발생 할 수 있음.
                    //이 코드는 다른 쓰레드에서 UI 작업을 할 수 있게 해줌.
                    jsonTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            jsonTextView.setText(jsonObject.toString());
                        }
                    });

                    httpRequester.addToSendQueue(jsonObject);
                    turnOffDataClickMode();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        },0, (int) (Settings.sensorDataUpdateInterval * 1000));
    }

    void addCoordInformationIntoJson(JSONObject jsonObject, float x, float y) {
        try {
            jsonObject.put("x",x);
            jsonObject.put("y",y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //현재 좌표 설정
    void setCurrentCoord()
    {
        this.xCoord = Float.parseFloat(((EditText)findViewById(R.id.XCoordEditText)).getText().toString());
        this.yCoord = Float.parseFloat(((EditText)findViewById(R.id.YCoordEditText)).getText().toString());
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
            setCurrentCoord();
            ((Button) view).setText("Stop");
        }

    }

    public void turnOffDataClickMode()
    {
        isCollectingMode = false;
        ((Button) findViewById(R.id.GatherButton)).setText("Start");
    }

    //전송 버튼 클릭
    public void onSendButtonClicked(View view)
    {
        httpRequester.postObjectsInQueue();
    }
}
