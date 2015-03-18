package com.example.mxu24.motosensorcombination;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;

/**
 * Created by MXU24 on 2/24/2015.
 */
public class VehicleDataAdapter extends ArrayAdapter<JSONObject> {

    private LayoutInflater inflater;
    public JSONArray vehicleData;
    private class ViewHolder{
        TextView name;
        TextView data;
    }
    public VehicleDataAdapter(Context context, JSONArray jsonArray) {
        super(context, R.layout.vehicle_data_line);
        inflater = LayoutInflater.from(context);
        vehicleData = jsonArray;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.vehicle_data_line, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.data = (TextView) convertView.findViewById(R.id.data);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        JSONObject jsonObject = getItem(position);
        Iterator<String> iterator = jsonObject.keys();
        String name = iterator.next();

        holder.name.setText(name);
        try {
            //Object o = jsonObject.get(name);
            if(jsonObject.get(name) instanceof Double){
                holder.data.setText(Double.toString((Double)jsonObject.get(name)));
            }
            if(jsonObject.get(name) instanceof String){
                holder.data.setText((String)jsonObject.get(name));
            }
            if(jsonObject.get(name) instanceof Boolean){
                holder.data.setText(Boolean.toString((Boolean)jsonObject.get(name)));
            }
            //holder.data.setText((String)jsonObject.get(name));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //holder.data.setText("data");
        return convertView;
    }

    @Override public int getCount() {

        return vehicleData.length();
    }

    @Override public JSONObject getItem(int position) {

        return vehicleData.optJSONObject(position);
    }
}
