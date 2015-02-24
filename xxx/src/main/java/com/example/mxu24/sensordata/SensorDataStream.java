package com.example.mxu24.sensordata;

import android.hardware.SensorEvent;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MXU24 on 2/16/2015.
 */
public class SensorDataStream {

    private static final String TAG = "Sensor Data Stream";

    protected List<SensorData> mValues = new ArrayList<>();
    private Map<String, float[]> mSensorEventMap = new LinkedHashMap<>();

    private SensorDataStreamOperator operator;

    public interface SensorDataStreamOperator{
        public void receiveSensorDataStream(List<SensorData> values);
    }

    public SensorDataStream(SensorDataStreamOperator operator){
        this.operator = operator;
    }
    public void add(SensorData data){
        String stringType = data.getStringType();
        boolean dataSetChanged = false;

        if(mSensorEventMap.containsKey(stringType)){
            mSensorEventMap.put(stringType,data.getValues());
            for (int i = 0; i < mValues.size(); i++){
                SensorData oldData = mValues.get(i);
                if(oldData.getStringType().equals(stringType)){
                    mValues.set(i,data);
                    dataSetChanged = true;
                    break;
                }
            }
        }else{
            mSensorEventMap.put(stringType, data.getValues());
            mValues.add(data);
            dataSetChanged = true;
        }

        if(dataSetChanged){
            operator.receiveSensorDataStream(mValues);
        }
    }
}
