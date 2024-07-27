package com.example.kcubewirelesscontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/*
 * This file is part of MyApplication.
 *
 * MyApplication is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyApplication is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyApplication.  If not, see <https://www.gnu.org/licenses/>.
 */

public class MainActivity extends AppCompatActivity {

    public static final String BLUETOOTH_MAC_ADRESS = "98:D3:71:FD:93:0D";

    Button startbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void SwitchActivityOnClick(View view){

        Intent switchAct = new Intent(this,com.example.kcubewirelesscontroller.ModeOfControlActivity.class);
        startActivity(switchAct);
    }
}