package com.example.datacollector;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import static android.support.v4.content.ContextCompat.getSystemService;

public class SensorDataGetter implements SensorEventListener {
    //센서 데이터 JsonObject 타입 큐
    private LinkedBlockingQueue<JSONObject> gatheredDatum = new LinkedBlockingQueue<JSONObject>();

    //측청된 최근 값들
    private float[] gyroVector = {0,0,0};
    private float[] magneticVector = {0,0,0};

    public SensorDataGetter(SensorManager sensorManager)
    {
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        for (int sensorType:Settings.collectingSensorTypes) {
            sensorManager.registerListener((SensorEventListener) this,
                    sensorManager.getDefaultSensor(sensorType),
                    sensorManager.SENSOR_DELAY_NORMAL);
        }

        //정해진 시간마다 센서 데이터를 Json화하여 리스트에 추가
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    if (Settings.collectingSensorTypes.contains(Sensor.TYPE_GYROSCOPE)) {
                        jsonObject.put(Settings.GYRO_SENSOR_JSON,gyroVector.toString());
                    }
                    if (Settings.collectingSensorTypes.contains(Sensor.TYPE_MAGNETIC_FIELD)) {
                        jsonObject.put(Settings.MAGNETIC_SENSOR_JSON,magneticVector);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },(int)(Settings.sensorDataUpdateInterval * 1000));
    }

    //아직 처리되지 않은 가장 오래된 센서 데이터를 Json 포맷으로 리턴
    public JSONObject getSensorDataJson()
    {
        if(gatheredDatum.size() <= 0)
            return null;

        return gatheredDatum.poll();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE)
        {
            gyroVector[0] = event.values[0];
            gyroVector[1] = event.values[1];
            gyroVector[2] = event.values[2];
        }
        else if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
        {
            magneticVector[0] = event.values[0];
            magneticVector[1] = event.values[1];
            magneticVector[2] = event.values[2];
        }
        else
        {
            Log.i("Thirty", "Unhandled SensorType" + event.sensor.getType());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
