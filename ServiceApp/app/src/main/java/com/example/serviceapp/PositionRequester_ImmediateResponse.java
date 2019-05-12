package com.example.serviceapp;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

public class PositionRequester_ImmediateResponse {
    public PositionRecevingEventPublisher publisher;
    LinkedBlockingQueue<JSONObject> sendQueue = new LinkedBlockingQueue<JSONObject>();
    public static final String USER_AGENT = "Mozilla/5.0";

    public PositionRequester_ImmediateResponse(PositionRecevingEventPublisher publisher) {
        setPublisher(publisher);
    }

    public void setPublisher(PositionRecevingEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void addToSendQueue(JSONObject jsonObj) {
        sendQueue.add(jsonObj);
    }

    //queue 내에 쌓인 Json Object들을 서버로 post한다.
    public void sendGetRequest() {
        //네트워크 작업은 메인쓰레드에서 작업하면 안되므로 새로운 쓰레드를 생성해서 작업
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                URL serverUrl = null;
                JSONObject jsonObj = sendQueue.poll();
                try {
                    //Get Request에서는 body에 데이터를 넣는것을 지원하지 않음.
                    //그래서 HttpURLConnection의 OutputStream 객채를 이용하려고 하면 이 리퀘스트는 Post로 바뀜
                    //https://blog.outsider.ne.kr/312
                    //serverUrl = new URL(Settings.serverAddress + "?data=" + jsonObj.toString().replaceAll("\"",""));  //리퀘스트 패러미터로 Json 데이터를 전송
                    serverUrl = new URL(Settings.serverAddress + "?data=" + jsonObj.toString());  //리퀘스트 패러미터로 Json 데이터를 전송
                    //serverUrl = new URL(Settings.serverAddress);  //리퀘스트 패러미터로 넘기지 않음.
                    HttpURLConnection myConnection = null;
                    myConnection = (HttpURLConnection) serverUrl.openConnection();
                    myConnection.setRequestMethod("GET");
                    myConnection.setRequestProperty("User-Agent", USER_AGENT);


                    Log.i("Thirty", myConnection.getRequestMethod() + "response code : " + myConnection.getResponseCode());   //Response Code를 요청해야 전송 완료됨.
                    InputStream inputStream = new BufferedInputStream(myConnection.getInputStream());
                    //받은 데이터를 읽음

                    String answer = new String("");
                    byte[] buffer = new byte[1024];

                    while (inputStream.read(buffer) > 0) {
                        answer += new String(buffer);
                    }
                    Log.i("Thirty", "answer : " + answer);
                    JSONObject answerJson = new JSONObject(answer);
                    publisher.publish(answerJson);
                    myConnection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
