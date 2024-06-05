package com.example.kcubewirelesscontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    Button startButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void SwitchActivityOnClick(View view){

        Intent switchAct = new Intent(this,ModeOfControlActivity.class);
        startActivity(switchAct);
    }
}