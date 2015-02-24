package com.example.mxu24.sensordata;

import android.hardware.SensorEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MXU24 on 2/16/2015.
 */
public class SensorDataParser {
    private String mSensorType;
    private float[] values;

    public SensorDataParser(SensorEvent event){
        this.mSensorType = Integer.toString(event.sensor.getType());
        values = event.values;
    }
    public Map<String, float[]> GetSensorData(){
        Map<String, float[]> data = new HashMap<>();
        data.put(mSensorType, values);
        return data;
    }

}
