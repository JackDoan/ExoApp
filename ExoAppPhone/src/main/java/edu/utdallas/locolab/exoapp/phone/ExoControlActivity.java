/*
 * MIT License
 *
 * Copyright (c) 2015 Douglas Nassif Roma Junior
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.utdallas.locolab.exoapp.phone;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter;

import deprecated.CMDSpinnerHandler;
import deprecated.ControllerSpinnerHandler;
import edu.utdallas.locolab.exoapp.packet.ActuatorSettingsAdaptor;
import edu.utdallas.locolab.exoapp.packet.CommandPacket;
import edu.utdallas.locolab.exoapp.packet.DataPacket;
import edu.utdallas.locolab.exoapp.packet.PacketFinder;

import static edu.utdallas.locolab.exoapp.packet.CommandPacket.*;

/**
 * Created by jad140230 on 12/22/2017
 */

public class ExoControlActivity extends AppCompatActivity implements BluetoothService.OnBluetoothEventCallback{
    private int accel = 90000;
    //private TextView tv;
    private EditText cmdArg;
    private ProgressBar battBar;
    private DataPacket data;
    private Menu mMenu;
    private CMDSpinnerHandler cmdSpinnerHandler;
    private ActuatorSettingsAdaptor comex2;

    private static final String TAG = "ExoControlActivity";

    private FloatingActionButton mFab;
    private EditText mEdRead;
    private EditText mEdWrite;

    private BluetoothService mService;
    private BluetoothWriter mWriter;
    private PacketFinder packetFinder;
    private final String tag = "ExoControlPanel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new DataPacket();
        packetFinder = new PacketFinder();
        mService = BluetoothService.getDefaultInstance();
        mWriter = new BluetoothWriter(mService);
        comex2 = new ActuatorSettingsAdaptor(mWriter);
        setContentView(R.layout.control_panel);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Control Panel");
        Switch stoSwitch = findViewById(R.id.stoID);
        Switch enableSwitch = findViewById(R.id.enableID);
        Button autoCal = findViewById(R.id.autoCalID);
        Button reset = findViewById(R.id.autoCalID2);
        Button sendButton = findViewById(R.id.sendCmdBtn);
        cmdArg = findViewById(R.id.cmdArg);
        battBar = findViewById(R.id.battBar);

        stoSwitch.setOnCheckedChangeListener((compoundButton, b) -> mWriter.write(buildSTOPacket(b)));
        enableSwitch.setOnCheckedChangeListener((compoundButton, b) -> mWriter.write(buildEnablePacket(b)));

        autoCal.setOnClickListener(v -> {
            mWriter.write(buildResetEncoderPacket());
            mWriter.write(buildSDOPacket(0x6083, accel)); //accel
            mWriter.write(buildSDOPacket(0x6084, accel)); //decel
            comex2.setTorqueRamp(8000);
            comex2.setThreshold(3050);
            Toast.makeText(this, "Calibrated!", Toast.LENGTH_LONG).show();
        });
        reset.setOnClickListener(v -> {
            mWriter.write(buildResetEncoderPacket());
            Toast.makeText(this, "Gait Reset!", Toast.LENGTH_LONG).show();
        });
        sendButton.setOnClickListener(v -> {
            cmdSpinnerHandler.sentClicked(); /*todo check for nulls*/
        });
        cmdSpinnerHandler = new CMDSpinnerHandler(this,
                (Spinner) findViewById(R.id.cmdSpinner),
                cmdArg,
                comex2
        );
        ControllerSpinnerHandler controllerSpinnerHandler = new ControllerSpinnerHandler(this,
                (Spinner) findViewById(R.id.controlSpinner),
                comex2
        );
        /*SensorListener mySensors = new SensorListener(
                (SensorManager) getSystemService(SENSOR_SERVICE),
                comex2,
                (TextView) findViewById(R.id.sensorText)
        );*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        mService.setOnEventCallback(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_control_panel, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            //todo export saved data
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI(DataPacket p) {
        if(p == null) {
            Log.e(tag, "updateUI was passed a null packet");
        }
        else {
            if(android.os.Build.VERSION.SDK_INT >= 24) {
                battBar.setProgress((int) p.getVoltagePercent(), true);
            }
            else {
                battBar.setProgress((int) p.getVoltagePercent());
            }

        }
    }

    @Override
    public void onDataRead(byte[] buffer, int length) {
        packetFinder.push(buffer);
        while(packetFinder.getPacketsAvailable() >= 1) {
            updateUI(packetFinder.pop());
        }
        //Log.d(TAG, "onDataRead: " + BluetoothWriter.bytesToHex(buffer));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mService.disconnect();
    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        Log.d(TAG, "onStatusChange: " + status);
    }

    @Override
    public void onDeviceName(String deviceName) {
        Log.d(TAG, "onDeviceName: " + deviceName);
    }

    @Override
    public void onToast(String message) {
        Log.d(TAG, "onToast");
    }

    @Override
    public void onDataWrite(byte[] buffer) {
        Log.d(TAG, "onDataWrite");
//        mEdRead.append("> " + new String(buffer));
    }
}
