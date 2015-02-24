package com.example.mxu24.motosensorcombination;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.example.mxu24.shared.SensorData;
import com.example.mxu24.shared.SensorDataStream;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import com.openxc.VehicleManager;
import com.openxc.measurements.EngineSpeed;
import com.openxc.messages.SimpleVehicleMessage;
import com.openxc.messages.VehicleMessage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by MXU24 on 2/17/2015.
 */
public class SensorDataListenerService extends WearableListenerService{
    private static final String TAG = "DataLayerSample";
    private static final String PATH = "/start-activity";
    private SensorDataStream sensorDataStream;

    @Override
    public void onCreate(){
        Log.i(TAG,"Sensor Data Listener Service Create");
        super.onCreate();
        //sensorDataStream = new SensorDataStream((SensorDataStream.SensorDataStreamOperator)MainActivity.this);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents);
        }
        final List events = FreezableUtils
                .freezeIterable(dataEvents);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        for (DataEvent event : dataEvents) {
            Uri uri = event.getDataItem().getUri();
            Log.i(TAG, "On Data Changed");
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.i(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.i(TAG, "DataItem changed: " + event.getDataItem().getUri());
                Uri dataItemUri = event.getDataItem().getUri();
                DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem())
                        .getDataMap();
                SensorData data = new SensorData(dataMap);
                try {
                    sensorDataStream.add(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }


}
