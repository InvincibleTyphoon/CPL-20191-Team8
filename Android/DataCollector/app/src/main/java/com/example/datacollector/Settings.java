package com.example.datacollector;

import android.hardware.Sensor;
import android.net.wifi.ScanResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Settings {
    /*******************************서버 관련 설정****************************************/
    public static String serverAddress = "http://175.113.48.102:5000/test";
    public static ArrayList<ArrayList<String>> httpPostRequestProperty = new ArrayList<ArrayList<String>>();
    //////////////////////////////////////////////////////////////////////////////////////

    /*******************************센서 관련 설정***************************************/
    public static String SENSOR_JSON = "Sensor";
    public static float sensorDataUpdateInterval = 0.5f;
    public static String MAGNETIC_SENSOR_JSON = "Magnetic";
    public static String GYRO_SENSOR_JSON = "Gyro";

    public static ArrayList<Float> startCoordinate = new ArrayList<Float>(
            Arrays.asList(0f,0f,0f)
    );

    public static ArrayList<Integer> collectingSensorTypes = new ArrayList<Integer>(
            //Arrays.asList(Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_GYROSCOPE)
            Arrays.asList(Sensor.TYPE_MAGNETIC_FIELD)
    );
    ////////////////////////////////////////////////////////////////////////////////////

    /*****************************와이파이 스캔 관련 설정******************************************/
    public static String WIFI_JSON = "WifiInfo";
    public static String WIFI_SSID = "SSID";
    public static String WIFI_BSSID = "BSID";
    public static String WIFI_LEVEL = "WIFI_LEVEL";
    public static String WIFI_LEVEL_JSON = "WifiLevel";

    public static ArrayList<String> collectingWifiScanResultDataTypes = new ArrayList<String>(
            Arrays.asList(WIFI_SSID,WIFI_LEVEL)
    );
    /////////////////////////////////////////////////////////////////////////////////////////
}
