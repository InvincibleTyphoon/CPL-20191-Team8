package com.example.serviceapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PositionRecevingEventPublisher {
    private List<PositionReceivingEventObserver> observers;

    public PositionRecevingEventPublisher()
    {
        observers = new ArrayList<PositionReceivingEventObserver>();
    }

    public void addObserver(PositionReceivingEventObserver observer)
    {
        observers.add(observer);
    }

    public void publish(float x, float y)
    {
        for (PositionReceivingEventObserver observer: observers) {
            observer.positionReceivingEventUpdate(x,y);
        }
    }

    public void publish(JSONObject obj) throws JSONException {
        publish(Float.parseFloat(obj.get("xCoord").toString()),Float.parseFloat(obj.get("yCoord").toString()));
    }
}
