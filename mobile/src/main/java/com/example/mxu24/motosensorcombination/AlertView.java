package com.example.mxu24.motosensorcombination;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Created by MXU24 on 2/26/2015.
 */
public class AlertView extends RelativeLayout{
    public Double value;
    private EditText setValue;
    private Button alterButton;
    private String thing;

    public AlertView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.alert_button, this);
        initView();

    }
    public AlertView(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.alert_button, this);
        initView();
    }

    public void initView(){
        this.value = 0.0;
        setValue = (EditText)this.findViewById(R.id.alert_value);
        alterButton = (Button) this.findViewById(R.id.alert_button);
        alterButton.setBackgroundColor(Color.GREEN);
        alterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setValue();
            }
        });
        this.thing = new String("Speed");
        alterButton.setText(thing);
    }
    public void setValue(){
        this.value = Double.valueOf(setValue.getText().toString());
    }

    public void alter(){
        alterButton.setBackgroundColor(Color.RED);
    }
    public void normal(){
        alterButton.setBackgroundColor(Color.GREEN);
    }
    public void setButtonText(String s){
        thing = s;
        alterButton.setText(thing);
    }
}
