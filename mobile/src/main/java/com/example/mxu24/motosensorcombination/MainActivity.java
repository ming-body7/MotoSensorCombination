package com.example.mxu24.motosensorcombination;


import android.app.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;

import com.example.mxu24.shared.SensorData;
import com.example.mxu24.shared.SensorDataStream;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.openxc.VehicleManager;
import com.openxc.measurements.EngineSpeed;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.VehicleSpeed;
import com.openxc.messages.SimpleVehicleMessage;
import com.openxc.messages.VehicleMessage;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends ActionBarActivity implements DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        SensorDataStream.SensorDataStreamOperator,VehicleMessageBuffer.VehicleJSONData,
        VehicleDataFragment.OnFragmentInteractionListener, SensorDataFragment.OnFragmentInteractionListener2, LogicFragment.OnFragmentInteractionListener3{

    private GoogleApiClient mGoogleApiClient;
    private SlidingTabLayout slidingTabLayout;
    private ViewPager viewPager;
    //private List<Fragment> fragments;
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;

    private final static String TAG = "Main";
    private VehicleManager mVehicleManager;
    private VehicleMessageBuffer vehicleMessageBuffer;
    private TextView mTextView;
    private SensorDataStream sensorDataStream;

    public JSONArray vehicleData;
    public JSONArray sensorData;
    private List<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.tab);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        fragments = new ArrayList<Fragment>();
        fragments.add(new VehicleDataFragment());
        fragments.add(new SensorDataFragment());
        fragments.add(new LogicFragment());

        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                        getSupportFragmentManager());
        viewPager.setAdapter(mDemoCollectionPagerAdapter);
        //slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        //mTextView = (TextView)findViewById(R.id.hello);
        vehicleMessageBuffer = new VehicleMessageBuffer(this);
        sensorDataStream = new SensorDataStream(this);
        vehicleData = new JSONArray();
        sensorData = new JSONArray();
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        // When the activity starts up or returns from the background,
        // re-connect to the VehicleManager so we can receive updates.
        super.onStart();

        mGoogleApiClient.connect();
        Log.i(TAG,"onResume");
        if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public void onPause(){
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient,this);
        mGoogleApiClient.disconnect();
        if(mVehicleManager != null) {
            Log.i(TAG, "Unbinding from Vehicle Manager");
            // Remember to remove your listeners, in typical Android
            // fashion.
            mVehicleManager.removeListener(VehicleSpeed.class,
                    mSpeedListener);
            unbindService(mConnection);
            mVehicleManager = null;
        }
    }

    @Override
    protected void onStop(){
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    EngineSpeed.Listener mSpeedListener = new EngineSpeed.Listener() {
        public void receive(Measurement measurement) {
            // When we receive a new EngineSpeed value from the car, we want to
            // update the UI to display the new value. First we cast the generic
            // Measurement back to the type we know it to be, an EngineSpeed.
            final EngineSpeed speed = (EngineSpeed) measurement;
            // In order to modify the UI, we have to make sure the code is
            // running on the "UI thread" - Google around for this, it's an
            // important concept in Android.
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {

                    //mTextView.setText("Vehicle speed (km/h): "
                     //+ speed.getValue().doubleValue());
                }
            });
        }
    };

    private VehicleMessage.Listener mListener = new VehicleMessage.Listener() {
        @Override
        public void receive(final VehicleMessage message) {
            Activity activity = MainActivity.this;
            if(activity != null) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            vehicleMessageBuffer.add(message.asSimpleMessage());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i(TAG, "Vehicle Message:" + message.toString());
                    }
                });
            }
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the VehicleManager service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i("openxc", "Bound to VehicleManager");
            // When the VehicleManager starts up, we store a reference to it
            // here in "mVehicleManager" so we can call functions on it
            // elsewhere in our code.
            mVehicleManager = ((VehicleManager.VehicleBinder) service)
                    .getService();

            // We want to receive updates whenever the EngineSpeed changes. We
            // have an EngineSpeed.ListeVehicleMessageBufferner (see above, mSpeedListener) and here
            // we request that the VehicleManager call its receive() method
            // whenever the EngineSpeed changes
            mVehicleManager.addListener(EngineSpeed.class, mSpeedListener);
            mVehicleManager.addListener(SimpleVehicleMessage.class, mListener);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w("openxc", "VehicleManager Service  disconnected unexpectedly");
            mVehicleManager = null;
        }
    };


    @Override
    public void receiveVehicleJSONData(JSONArray jsonArray) {
        JSONObject allData = new JSONObject();
        try {
            allData.put("Vehicle Data", jsonArray);
            vehicleData = jsonArray;
            mDemoCollectionPagerAdapter.dataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /*if (sensorDataHolder != null){
            try {
                allData.put("Sensor Data", sensorDataHolder);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/

        //Log.i(TAG,allData.toString());
    }

    @Override
    public void receiveSensorDataStream(JSONArray newArray) throws JSONException {
        JSONObject finalObj = new JSONObject();
        sensorData = newArray;
        finalObj.put("Sensor", newArray);
        finalObj.put("Vehicle",vehicleData);
        Log.i("Data", finalObj.toString());


        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httppostreq = new HttpPost("https://dweet.io/dweet/for/wear");
        try {
            StringEntity se = new StringEntity(finalObj.toString());
            httppostreq.setEntity(se);
            httppostreq.setHeader("Accept", "application/json");
            httppostreq.setHeader("Content-type", "application/json");
            try {
                HttpResponse httpresponse = httpclient.execute(httppostreq);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onConnected(Bundle bundle) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Connected to Google Api Service");
        }
        Wearable.DataApi.addListener(mGoogleApiClient, this);

        //String message = "Hello wearable\n Via the data layer";
        //new SendToDataLayerThread("/message_path", message, mGoogleApiClient).start();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    @Override
    public void onFragmentInteraction2(String id) {

    }

    @Override
    public void onFragmentInteraction3(Uri uri) {

    }

    public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
        private Fragment fragment = new VehicleDataFragment();
        public DemoCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            //Fragment fragment = new VehicleDataFragment();
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "OBJECT " + (position + 1);
        }


        public void dataSetChanged() throws JSONException {
            //super.notifyDataSetChanged();
            VehicleDataFragment f = (VehicleDataFragment)getItem(0);
            if(f.isVisible())
            f.dataChanged();
            SensorDataFragment s = (SensorDataFragment)getItem(1);
            if(s.isVisible())
            s.dataChanged();
            LogicFragment l = (LogicFragment)getItem(2);
            if(l.isVisible())
            l.checkAlert();
        }
    }

}

