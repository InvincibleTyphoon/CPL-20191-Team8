package com.example.serviceapp;

import android.hardware.Sensor;

import com.jjoe64.graphview.series.DataPoint;

import java.util.ArrayList;
import java.util.Arrays;

public class Settings {
    /*******************************서버 관련 설정****************************************/
    public static String serverAddress = "http://192.168.0.3:1337/testGet";
    public static ArrayList<ArrayList<String>> httpPostRequestProperty = new ArrayList<ArrayList<String>>();
    //////////////////////////////////////////////////////////////////////////////////////

    /*******************************센서 관련 설정***************************************/
    public static String SENSOR_JSON = "Sensor";
    public static float sensorDataUpdateInterval = 0.5f;
    public static float requestInterval = 0.5f;
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
    public static String WIFI_BSSID = "BSSID";
    public static String WIFI_LEVEL = "WIFI_LEVEL";
    public static String WIFI_LEVEL_JSON = "WifiLevel";

    public static ArrayList<String> collectingWifiScanResultDataTypes = new ArrayList<String>(
            Arrays.asList(WIFI_BSSID,WIFI_LEVEL)
    );
    /////////////////////////////////////////////////////////////////////////////////////////

    /*********************************추측항법 관련 설정*************************************/
    public static float stepSize = 1.0f;

    ///////////////////////////////////////////////////////////////////////


    /*****************************모드 관련******************************************************/
    public static String SERVICE_MODE = "SERVICE_MODE";
    public static String STEP_SCENARIO_MODE = "STEP_SCENARIO_MODE";
    public static int stepScenarioIdx = 0;
    public static String INITIALIZE_SCENARIO_MODE = "INITIALIZE_SCENARIO_MODE";
    public static String mode = STEP_SCENARIO_MODE;
    ///////////////////////////////////////////////////////////////////////////////////////////

    public static ArrayList<Float> ScenarioX = new ArrayList<Float>(
            Arrays.asList(1f,2f,3f,4f,5f,6f,7f,8f,9f,10f)
    );

    public static ArrayList<Float> ScenarioY = new ArrayList<Float>(
            Arrays.asList(1f,2f,3f,4f,5f,6f,7f,8f,9f,10f)
    );



    /***************************테스팅용 초기 데이터 삽입***********************************************/
    public static ArrayList<Integer> initialX = new ArrayList<Integer>(
           // Arrays.asList(1,2,3,4,5,6,7,8,9,10)
    );
    public static ArrayList<Integer> initialY = new ArrayList<Integer>(
            //Arrays.asList(1,1,1,1,1,1,1,1,1,1)
    );

    ////////////////////////////////////////////////////////////////////////////////////////////////////
}
