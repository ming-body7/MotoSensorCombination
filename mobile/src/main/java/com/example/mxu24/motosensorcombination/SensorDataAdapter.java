package com.example.mxu24.motosensorcombination;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by MXU24 on 2/25/2015.
 */
public class SensorDataAdapter extends ArrayAdapter<JSONObject>{
    private LayoutInflater inflater;
    public JSONArray sensorData;

    private class ViewHolder{
        TextView name;
        TextView data;
    }

    public SensorDataAdapter(Context context, JSONArray jsonArray) {
        super(context, R.layout.sensor_data_line);
        inflater = LayoutInflater.from(context);
        sensorData = jsonArray;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.sensor_data_line, null);
            holder.name = (TextView) convertView.findViewById(R.id.sensorname);
            holder.data = (TextView) convertView.findViewById(R.id.sensordata);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject jsonObject = getItem(position);
        Iterator<String> iterator = jsonObject.keys();
        String name = iterator.next();

        holder.name.setText(name);

        try {
            JSONArray values = (JSONArray)jsonObject.get(name);
            holder.data.setText("value1:"+values.get(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //holder.data.setText("data");
        return convertView;
    }

    @Override public int getCount() {

        return sensorData.length();
    }

    @Override public JSONObject getItem(int position) {

        return sensorData.optJSONObject(position);
    }
}
