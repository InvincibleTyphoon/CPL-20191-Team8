package com.example.datacollector;

import android.hardware.Sensor;
import android.net.wifi.ScanResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Settings {
    /*******************************서버 관련 설정****************************************/
    public static String serverAddress = "http://192.168.0.2:1337/testPost";
    public static ArrayList<ArrayList<String>> httpPostRequestProperty = new ArrayList<ArrayList<String>>();
    //////////////////////////////////////////////////////////////////////////////////////

    /*******************************센서 관련 설정***************************************/
    public static float sensorDataUpdateInterval = 0.0f;

    public static ArrayList<Float> startCoordinate = new ArrayList<Float>(
            Arrays.asList(0f,0f,0f)
    );

    public static ArrayList<Integer> collectingSensorTypes = new ArrayList<Integer>(
            Arrays.asList(Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_GYROSCOPE)
    );
    ////////////////////////////////////////////////////////////////////////////////////

    /*****************************와이파이 관련 설정******************************************/
    public static String WIFI_SSID = "SSID";
    public static String WIFI_BSID = "BSID";
    public static String WIFI_LEVEL = "WIFI_LEVEL";

    public static ArrayList<String> collectingWifiScanResultDataTypes = new ArrayList<String>(
            Arrays.asList(WIFI_SSID,WIFI_LEVEL)
    );
    /////////////////////////////////////////////////////////////////////////////////////////
}
