package com.example.kcubewirelesscontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ModeOfControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_of_control);
    }

    public void GpsModeOnClick(View view){

        Intent switchAct = new Intent(this,GpsModeControlActivity.class);
        startActivity(switchAct);
    }

    public void remotedOnClick(View view){

        Intent switchAct = new Intent(this,remotedActivity.class);
        startActivity(switchAct);
    }

    public void lineFollowOnClick(View view){

        Intent switchAct = new Intent(this,GpsModeControlActivity.class);
        startActivity(switchAct);
    }


}