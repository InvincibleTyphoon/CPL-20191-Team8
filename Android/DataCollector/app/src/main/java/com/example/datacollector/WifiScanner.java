package com.example.datacollector;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;

public class WifiScanner {
    WifiManager wifiManager = null;

    WifiScanner(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
    }


    public JSONObject getWifiScanResultJson() {
        JSONObject jsonObject = new JSONObject();
        final List<ScanResult> scanResultList = wifiManager.getScanResults();
        int size = scanResultList.size();

        JSONArray jsonArr = new JSONArray();
        for (int i = 0; i < size; i++) {
            if (scanResultList.get(i) != null) {
                String ssid = scanResultList.get(i).SSID;
                String bssid = scanResultList.get(i).BSSID;
                int level = scanResultList.get(i).level;

                try {
                    JSONObject item = new JSONObject();
                    if(Settings.collectingWifiScanResultDataTypes.contains(Settings.WIFI_SSID))
                        item.put(Settings.WIFI_SSID, ssid);
                    if(Settings.collectingWifiScanResultDataTypes.contains(Settings.WIFI_BSSID))
                        item.put(Settings.WIFI_BSSID, bssid);
                    if(Settings.collectingWifiScanResultDataTypes.contains(Settings.WIFI_LEVEL))
                        item.put(Settings.WIFI_LEVEL_JSON, level);
            jsonArr.put(item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
        try {
                jsonObject.put(Settings.WIFI_JSON,jsonArr);
                } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
