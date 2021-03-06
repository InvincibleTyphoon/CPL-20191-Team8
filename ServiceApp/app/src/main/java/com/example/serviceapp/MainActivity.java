package com.example.serviceapp;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class MainActivity extends AppCompatActivity  implements SensorEventListener, PositionReceivingEventObserver {
    //현재 좌표
    ArrayList<Float> coord;

    // 추측항법을 위한 방향값들
    float seta = 0.0f;
    float[] orientation = {0.0f,0.0f,0.0f};

    GraphView graph;
    Date accelUpdateTime;

    //그래프
    PointsGraphSeries<DataPoint> series;

    //서비스 진행중인지 여부
    boolean isServiceOn = false;

    //서버에 좌표 분석을 요청
    PositionRequester_ImmediateResponse requester = null;

    //센서 값 가져옴
    SensorDataGetter sensorDataGetter = null;

    //와이파이 스캐너
    private WifiScanner wifiScanner = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        coord = new ArrayList<Float>(3);
        coord.add(0.0f);
        coord.add(0.0f);
        coord.add(0.0f);

        SensorManager sensorManager = ((SensorManager)getSystemService(SENSOR_SERVICE));
        Sensor sensor = null;

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_NORMAL);

        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this,sensor,sensorManager.SENSOR_DELAY_NORMAL);

        graph = (GraphView) findViewById(R.id.graph);

        accelUpdateTime = new Date();

        //그래프 크기 조정
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setMaxXAxisSize(10);
        graph.getViewport().setMaxY(10);
        graph.getViewport().setMaxYAxisSize(10);
        graph.getViewport().setMinX(-10);
        graph.getViewport().setMinY(-10);

        PositionRecevingEventPublisher publisher = new PositionRecevingEventPublisher();
        publisher.addObserver(this);
        requester = new PositionRequester_ImmediateResponse(publisher);

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(!isServiceOn)
                    return;
                JSONObject sendObject = new JSONObject();
                JSONObject sensorJson = sensorDataGetter.getSensorDataJson();
                JSONObject wifiScanResultJson = wifiScanner.getWifiScanResultJson();
                try {
                    //sendObject.put("PDRPos",new String(stepDetectorCoord.get(0) + "," + stepDetectorCoord.get(1)));
                    sendObject.put("xCoord",coord.get(0).toString());
                    sendObject.put("yCoord",coord.get(1).toString());
                    for (Iterator<String> it = sensorJson.keys(); it.hasNext(); ) {
                        String key = it.next();
                        sendObject.put(key,sensorJson.get(key));
                    }

                    for (Iterator<String> it = wifiScanResultJson.keys(); it.hasNext(); ) {
                        String key = it.next();
                        sendObject.put(key,wifiScanResultJson.get(key));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("Thirty",sendObject.toString());
                requester.addToSendQueue(sendObject);
                requester.sendGetRequest();
            }
        },0, (int) (Settings.sensorDataUpdateInterval * 1000));
        sensorDataGetter = new SensorDataGetter((SensorManager)getSystemService(SENSOR_SERVICE));
        wifiScanner = new WifiScanner((WifiManager)getSystemService(Context.WIFI_SERVICE));


        /***********************테스팅용 초기 좌표 입력*****************************/
        int size = Settings.initialX.size();
        for(int i = 0;  i < size; i++)
        {
            PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(Settings.initialX.get(i),Settings.initialY.get(i))
            });
            graph.addSeries(series);
        }
        ////////////////////////////////////////////////////////////////////
        if(Settings.mode.equals(Settings.INITIALIZE_SCENARIO_MODE)) {
            int scenarioSize = Settings.ScenarioX.size();
            for (int i = 0; i < scenarioSize; i++) {
                PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(new DataPoint[]{
                        new DataPoint(Settings.ScenarioX.get(i), Settings.ScenarioY.get(i))
                });
                graph.addSeries(series);
            }
        }

    }

    void updateText()
    {
        ((TextView)findViewById(R.id.CoordText)).setText("Coord : (" + coord.get(0) + ", " + coord.get(1) + "," + coord.get(2) + ")");
        ((TextView)findViewById(R.id.OrientationText)).setText("Orientation : " + "(" + orientation[0] + "," + orientation[1] + "," + orientation[2] + ")");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR)
        {
            coord.set(0,(float)(coord.get(0) + Math.cos(seta) * Settings.stepSize));
            coord.set(1,(float)(coord.get(1) + Math.sin(seta) * Settings.stepSize));

            if(Settings.mode.equals(Settings.STEP_SCENARIO_MODE))
            {
                positionReceivingEventUpdate(Settings.ScenarioX.get(Settings.stepScenarioIdx),Settings.ScenarioY.get(Settings.stepScenarioIdx));
                Settings.stepScenarioIdx++;
            }
        }

        if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
        {
            for(int i = 0 ; i < 3; i++)
                orientation[i] = event.values[i];
            seta = (float) Math.toRadians(orientation[0]);
        }
        updateText();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void positionReceivingEventUpdate(float x, float y) {
        this.coord.set(0, x);
        this.coord.set(1, y);
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(x,y)
        });
        graph.addSeries(series);
    }

    public void setPositionButtonClicked(View v)
    {
        coord.set(0,
                Float.parseFloat(
                        ((EditText)findViewById(R.id.xCoord)).getText().toString()
                )
        );
        coord.set(1,
                Float.parseFloat(
                        ((EditText)findViewById(R.id.yCoord)).getText().toString()
                )
        );
    }

    public void serviceOnButtonClicked(View v)
    {
        if(isServiceOn)
        {
            isServiceOn = false;
            ((Button)findViewById(R.id.StartServiceButton)).setText("Set Service On");
        }
        else
        {
            isServiceOn = true;
            ((Button)findViewById(R.id.StartServiceButton)).setText("Set Service Off");
        }
    }

    public void setGraphSizeButtonClicked(View v)
    {
        float width = Float.parseFloat(((EditText)findViewById(R.id.GraphWidth)).getText().toString());
        float height = Float.parseFloat(((EditText)findViewById(R.id.GraphHeight)).getText().toString());
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxX(width/2);
        graph.getViewport().setMaxXAxisSize(width/2);
        graph.getViewport().setMaxY(height/2);
        graph.getViewport().setMaxYAxisSize(height/2);
        graph.getViewport().setMinX(-width/2);
        graph.getViewport().setMinY(-height/2);
    }

    public void testGraphPositionInputButtonClicked(View v)
    {
        float x =  Float.parseFloat(
                ((EditText)findViewById(R.id.xCoord2)).getText().toString()
        );
        float y =  Float.parseFloat(
                ((EditText)findViewById(R.id.yCoord2)).getText().toString()
        );
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(x,y)
        });
        graph.addSeries(series);
    }
}
