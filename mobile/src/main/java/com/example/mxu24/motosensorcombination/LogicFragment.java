package com.example.mxu24.motosensorcombination;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LogicFragment.OnFragmentInteractionListener3} interface
 * to handle interaction events.
 * Use the {@link LogicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LogicFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener3 mListener;

    public AlertView speedAlert;
    public AlertView acceleratorAlert;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LogicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LogicFragment newInstance(String param1, String param2) {
        LogicFragment fragment = new LogicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public LogicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //speedAlert = new AlertView(this);
        View v = inflater.inflate(R.layout.fragment_logic, container, false);
        speedAlert = (AlertView)v.findViewById(R.id.alertView);
        speedAlert.setButtonText("Speed");

        acceleratorAlert = (AlertView)v.findViewById(R.id.acceleratorView);
        acceleratorAlert.setButtonText("Wearable Acce");

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction3(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener3) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    public void checkAlert() throws JSONException {
        MainActivity activity = (MainActivity)getActivity();
        JSONArray vehicleData;// = activity.vehicleData;
        if(activity.vehicleData!=null) {
            JSONObject speedData;
            vehicleData = activity.vehicleData;
            for (int i = 0; i < vehicleData.length(); i++) {
                speedData = vehicleData.optJSONObject(i);
                String name = speedData.keys().next();
                if (name.equals("vehicle_speed")) {
                    Double speed = speedData.getDouble("vehicle_speed");
                    if (speed > speedAlert.value) {
                        speedAlert.alter();
                    } else {
                        speedAlert.normal();
                    }
                }
            }
        }

        JSONArray sensorData;// = activity.vehicleData;
        if(activity.sensorData!=null) {
            JSONObject acceleratorData;
            sensorData = activity.sensorData;
            for (int i = 0; i < sensorData.length(); i++) {
                acceleratorData = sensorData.optJSONObject(i);
                String name = acceleratorData.keys().next();
                if (name.equals("Accelerometer Sensor")) {
                    JSONArray accelerator = acceleratorData.getJSONArray("Accelerometer Sensor");

                    Float acc1 = (Float)accelerator.getJSONArray(0).get(0);
                    Float acc2 = (Float)accelerator.getJSONArray(0).get(1);
                    Float acc3 = (Float)accelerator.getJSONArray(0).get(2);
                    double acc = Math.sqrt(acc1*acc1+acc2*acc2+acc3*acc3);
                    if (acc > acceleratorAlert.value) {
                        acceleratorAlert.alter();
                    } else {
                        acceleratorAlert.normal();
                    }
                }
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener3 {
        // TODO: Update argument type and name
        public void onFragmentInteraction3(Uri uri);
    }

}
