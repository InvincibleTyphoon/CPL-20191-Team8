package com.example.serviceapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SensorDataGetter implements SensorEventListener {
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
    }

    //최근 센서 측정값을 Json 포멧으로 리턴
    public JSONObject getSensorDataJson()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            if (Settings.collectingSensorTypes.contains(Sensor.TYPE_GYROSCOPE)) {
                JSONArray jsonArr = new JSONArray();
                jsonArr.put(gyroVector[0]);
                jsonArr.put(gyroVector[1]);
                jsonArr.put(gyroVector[2]);
                jsonObject.put(Settings.GYRO_SENSOR_JSON,jsonArr);
                //jsonObject.put(Settings.GYRO_SENSOR_JSON, "[" + gyroVector[0] + "," + gyroVector[1] + "," + gyroVector[2] + "]");
                //jsonObject.put(Settings.GYRO_SENSOR_JSON, (Object)gyroVector);
            }
            if (Settings.collectingSensorTypes.contains(Sensor.TYPE_MAGNETIC_FIELD)) {
                JSONArray jsonArr = new JSONArray();
                jsonArr.put(magneticVector[0]);
                jsonArr.put(magneticVector[1]);
                jsonArr.put(magneticVector[2]);
                jsonObject.put(Settings.MAGNETIC_SENSOR_JSON,jsonArr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
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
