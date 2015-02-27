package com.example.mxu24.shared;

import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by MXU24 on 2/16/2015.
 */
public class SensorData {
    private int type;
    private String stringType;
    private String sensorName;
    private float[] values;
    public SensorData(DataMap dataMap){
        this.stringType = (String)dataMap.keySet().toArray()[0];
        //this.type = Integer.valueOf(stringType);

        this.values = dataMap.getFloatArray(stringType);
    }

    public int getType() {
        return type;
    }

    public String getStringType() {
        return stringType;
    }

    public String getSensorName() {
        return sensorName;
    }

    public float[] getValues() {
        return values;
    }
}
