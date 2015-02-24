package com.example.mxu24.shared;

import android.hardware.SensorEvent;
import android.util.Log;

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
        this.values = event.values;
        Log.i("sensor data size", String.valueOf(event.values.length));
    }
    public Map<String, float[]> GetSensorData(){
        Map<String, float[]> data = new HashMap<>();
        data.put(mSensorType, values);
        return data;
    }

    public String getmSensorType() {
        return mSensorType;
    }

    public float[] getValues() {
        return values;
    }
}
