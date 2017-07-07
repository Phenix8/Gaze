package com.ican.anamorphoses_jsdn.activity.menu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ican.anamorphoses_jsdn.R;

import java.sql.Time;


public class LoadingActivity extends AppCompatActivity {

    Time timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


    }
}
