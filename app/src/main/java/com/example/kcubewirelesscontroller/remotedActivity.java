package com.example.kcubewirelesscontroller;

import static com.example.kcubewirelesscontroller.MainActivity.BLUETOOTH_MAC_ADRESS;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.UUID;

public class remotedActivity extends AppCompatActivity {

    //Used to communicate with Bluetooth boards
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public Boolean isConnected = false;

    TextView connectionStatus;
    BluetoothDevice hc05 = null;
    BluetoothSocket btSocket = null;
    OutputStream outputStream = null;

    InputStream inputStream = null;


    private String m_Text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remoted);
        connectionStatus = findViewById(R.id.connectionStatus);

        setConnectionStatusValue(connectionStatus, isConnected);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        System.out.println(btAdapter.getBondedDevices());
        //set the mac adress of your hc05
        hc05 = btAdapter.getRemoteDevice(BLUETOOTH_MAC_ADRESS);
        System.out.println(hc05.getName());


       /*
        InputStream inputStream = null;

        try {
            inputStream = btSocket.getInputStream();
            inputStream.skip(inputStream.available());

            for (int i = 0; i < 26; i++) {
                byte b = (byte) inputStream.read();
                System.out.println((char) b);
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/


    }


    public void moveForward(View view) {
    }

    public void moveBackward(View view) {
    }

    public void moveRight(View view) {
    }

    public void moveLeft(View view) {
    }

    public void Stop(View view) {
    }

    public void openTheDoor(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(remotedActivity.this);
        builder.setTitle("Security PIN").setMessage("Please Enter your Professional Card Code ");

        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER |  InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    if (input.getText().toString().equals("0000")) {
                        Toast.makeText(remotedActivity.this, "Granted Access, Door Opened Successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(remotedActivity.this, "Incorrect PIN", Toast.LENGTH_SHORT).show();
                    }
                }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void connect(View view) {


        int counter = 0;
        // a loop to try couple times to connect
        // to not throw an error from the first time
        do {
            try {

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                System.out.println(btSocket);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                btSocket.connect();
                System.out.println(btSocket.isConnected());
                outputStream = btSocket.getOutputStream();
                inputStream = btSocket.getInputStream();
                inputStream.skip(inputStream.available());
                Toast.makeText(remotedActivity.this, "Connceted Successfully", Toast.LENGTH_SHORT).show();
                setConnectionStatusValue(connectionStatus,btSocket.isConnected());
            }catch (IOException e ){
                e.printStackTrace();
            }
            counter++;

        }while(!btSocket.isConnected() && counter < 3);



    }
    public void disconnect(View view){
        try {
            btSocket.close();
            System.out.println(btSocket.isConnected());
            Toast.makeText(remotedActivity.this, "disConnceted Successfully", Toast.LENGTH_SHORT).show();
            setConnectionStatusValue(connectionStatus,btSocket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void nineteenDegreeRight(View view){}
    public void nineteenDegreeLeft(View view){}


    public void setConnectionStatusValue(TextView connectionStatus , Boolean status){

        connectionStatus.setText((status == true)?"Connected":"Not Connected");
    }
}
