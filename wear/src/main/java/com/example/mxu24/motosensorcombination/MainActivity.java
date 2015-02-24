package com.example.mxu24.motosensorcombination;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.example.mxu24.shared.SensorDataParser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements
        SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    private GoogleApiClient mGoogleApiClient;

    private TextView mTextView;
    private SensorManager mSensorManager;
    private static final String TAG = "Moto360SensorMain";
    private Sensor mSensor;
    private List<Sensor> deviceSensors;
    private ArrayList<String> sensorsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mTextView.setText("Sensor Data Transferring");
            }
        });
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for(Sensor s : deviceSensors){
            Log.i(TAG, "" + s.getStringType());
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        Log.i(event.sensor.getStringType(),Float.toString(event.values[0]));
        //TODO:add thread queue to send data
        String path = "/SensorData/"+ System.currentTimeMillis();
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
        SensorDataParser parser = new SensorDataParser(event);
        Log.i(TAG,parser.getmSensorType());
        putDataMapRequest.getDataMap().putFloatArray(parser.getmSensorType(),parser.getValues());
        Log.i("Data size", String.valueOf(parser.getValues().length));
        PutDataRequest request = putDataMapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.i(TAG, "Sending sensor data successful: " + dataItemResult.getStatus()
                                .isSuccess());
                    }
                });
        Log.i(TAG, "send sensor data");
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        for(Sensor s : deviceSensors){
            Log.i(TAG, "" + s.getStringType());
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(s.getType()),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Has connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, " connected suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG,"connected failed" +connectionResult.toString());
    }




}
