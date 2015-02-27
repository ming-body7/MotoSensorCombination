package com.example.mxu24.motosensorcombination;

import android.content.Context;
import android.util.Log;

import com.openxc.messages.ExactKeyMatcher;
import com.openxc.messages.KeyMatcher;
import com.openxc.messages.KeyedMessage;
import com.openxc.messages.MessageKey;
import com.openxc.messages.SimpleVehicleMessage;
import com.openxc.messages.VehicleMessage;
import com.openxc.units.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.Boolean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MXU24 on 2/12/2015.
 */
public class VehicleMessageBuffer {

    private static final String TAG = "Vehicle Message Buffer";
    private Context mContext;

    private VehicleJSONData vjd;
    protected List<VehicleMessage> mValues = new ArrayList<>();
    private Map<MessageKey, KeyedMessage> mMessages;

    public interface VehicleJSONData{
        public void receiveVehicleJSONData(JSONArray jsonArray);
    }
    public VehicleMessageBuffer(Context context) {
        mContext = context;
        mMessages = new LinkedHashMap<>();
        vjd = (VehicleJSONData)context;
    }

    public void add(KeyedMessage message) throws JSONException {
        //Log.i(TAG, "get new vehicle message");
        MessageKey key = message.getKey();
        boolean dataSetChanged = false;
        if(mMessages.containsKey(key)) {
            mMessages.put(key, message);
            // Already exists in values, just need to update it
            KeyMatcher exactMatcher = ExactKeyMatcher.buildExactMatcher(key);
            for(int i = 0; i < mValues.size(); i++) {
                KeyedMessage existingMessage = ((KeyedMessage)mValues.get(i));
                if(exactMatcher.matches(existingMessage.getKey())) {
                    mValues.set(i, message);
                    dataSetChanged = true;
                    break;
                }
            }
        } else {
            // Need to recreate values list because positions will be shifted
            mMessages.put(key, message);
            mValues = new ArrayList<VehicleMessage>(mMessages.values());
            Collections.sort(mValues);
            dataSetChanged = true;
        }

        if(dataSetChanged) {
            //notifyDataSetChanged();
            //TODO: active out put, change to JSON and combine with
            mValues.get(0).asSimpleMessage().getName();
            mValues.get(0).asSimpleMessage().getValueAsNumber();
            JSONArray jsonArray = new JSONArray();
            for (VehicleMessage updatedMessage:mValues){
                JSONObject jsonObj = new JSONObject();
                SimpleVehicleMessage m = updatedMessage.asSimpleMessage();
                if (m.getValue() instanceof Boolean){
                    jsonObj.put(m.getName(),m.getValueAsBoolean());
                }else if (m.getValue() instanceof String){
                    jsonObj.put(m.getName(), m.getValueAsString());
                }else{
                    jsonObj.put(m.getName(), m.getValueAsNumber());
                }
                jsonArray.put(jsonObj);
            }
            //TODO:send jsonArray to handler
            vjd.receiveVehicleJSONData(jsonArray);
        }
    }
}
