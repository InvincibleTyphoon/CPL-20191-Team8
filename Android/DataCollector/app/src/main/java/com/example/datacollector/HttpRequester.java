package com.example.datacollector;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class HttpRequester {
    LinkedBlockingQueue<JSONObject> sendQueue = new LinkedBlockingQueue<JSONObject>();

    public void addToSendQueue(JSONObject jsonObj) {
        sendQueue.add(jsonObj);
    }

    //queue 내에 쌓인 Json Object들을 서버로 post한다.
    public void postObjectsInQueue() {
        //네트워크 작업은 메인쓰레드에서 작업하면 안되므로 새로운 쓰레드를 생성해서 작업
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                URL serverUrl = null;
                JSONObject jsonObj = sendQueue.poll();
                try {
                    serverUrl = new URL(Settings.serverAddress + "?data="+jsonObj.toString());  //리퀘스트 패러미터로 Json 데이터를 전송
                    //serverUrl = new URL(Settings.serverAddress);  //리퀘스트 패러미터로 넘기지 않음.
                    HttpURLConnection myConnection = null;
                    myConnection =                    (HttpURLConnection) serverUrl.openConnection();
                    myConnection.setRequestMethod("POST");
                    myConnection.setDoOutput(true);

                    //리퀘스트 body로 Json 데이터를 전송
                    OutputStream outputStream = new BufferedOutputStream(myConnection.getOutputStream());
                    outputStream.write(jsonObj.toString().getBytes());
                    outputStream.flush();
                    outputStream.close();
                    Log.i("Thirty", "response code : " + myConnection.getResponseCode());   //Response Code를 요청해야 전송 완료됨.
                    myConnection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
}
}
