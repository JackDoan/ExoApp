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

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter;

import java.util.LinkedList;

import edu.utdallas.locolab.exoapp.packet.ActuatorSettingsAdaptor;
import edu.utdallas.locolab.exoapp.packet.DataPacket;
import edu.utdallas.locolab.exoapp.packet.DataPacketDAO;
import edu.utdallas.locolab.exoapp.packet.PacketFinder;
import io.objectbox.Box;
import io.objectbox.BoxStore;

import static edu.utdallas.locolab.exoapp.packet.CommandPacket.*;

/**
 * Created by jad140230 on 12/22/2017
 */

public class ExoControlActivity extends AppCompatActivity implements BluetoothService.OnBluetoothEventCallback{
    private static final String TAG = "ExoControlActivity";
    private final String tag = "ExoControlPanel";
    private int accel = 90000;
    //private TextView tv;
    //private EditText cmdArg;
    //private CMDSpinnerHandler cmdSpinnerHandler;
    private ActuatorSettingsAdaptor comex2;
    private BluetoothService mService;
    private BluetoothWriter mWriter;
    private PacketFinder packetFinder;
    private BoxStore boxStore;
    private Box<DataPacketDAO> dataBox;
    private LinkedList<DataPacketDAO> dataBuffer;
    private boolean saveData;
    private DrawerLayout mDrawerLayout;
    private ProgressBar batteryLevelBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBuffer = new LinkedList<>();
        packetFinder = new PacketFinder();
        mService = BluetoothService.getDefaultInstance();
        mWriter = new BluetoothWriter(mService);
        comex2 = new ActuatorSettingsAdaptor(mWriter);
        setContentView(R.layout.control_panel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Control Panel");
        setProgressBarVisibility(true);

        //toolbar.setTitle("Control Panel");

        ControllerSpinnerHandler ctrlSpinnerHandler = new ControllerSpinnerHandler(this, findViewById(R.id.controlSpinner));
        Switch stoSwitch = findViewById(R.id.stoID);
        Switch enableSwitch = findViewById(R.id.enableID);
        Switch saveDataSwitch = findViewById(R.id.saveDataSwitch);
        Button autoCal = findViewById(R.id.autoCalID);
        Button reset = findViewById(R.id.autoCalID2);
        //Button sendButton = findViewById(R.id.sendCmdBtn);
        ImageButton exportButton = findViewById(R.id.exportButton);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        //cmdArg = findViewById(R.id.cmdArg);
        batteryLevelBar = findViewById(R.id.batteryLevel);
        EditText thresholdValueBox = findViewById(R.id.thresholdVal);
        EditText torqueRampValueBox = findViewById(R.id.torqueRampVal);
        EditText swingGainValueBox = findViewById(R.id.swingGainVal);
        EditText stanceGainValueBox = findViewById(R.id.stanceGainVal);
        SeekBar maxTorqueSeek = findViewById(R.id.maxTorqueSeek);

        thresholdValueBox.setText(Integer.toString(comex2.getThreshold()));
        torqueRampValueBox.setText(Integer.toString(comex2.getTorqueRamp()));
        swingGainValueBox.setText(Float.toString(comex2.getSwingGain()));
        stanceGainValueBox.setText(Float.toString(comex2.getStanceGain()));

        thresholdValueBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String arg = s.toString();
                comex2.setThreshold(Integer.valueOf(arg));
            }
        });
        torqueRampValueBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String arg = s.toString();
                comex2.setTorqueRamp(Integer.valueOf(arg));
            }
        });
        swingGainValueBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String arg = s.toString();
                comex2.setSwingGain(Float.valueOf(arg));
            }
        });
        stanceGainValueBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String arg = s.toString();
                comex2.setStanceGain(Float.valueOf(arg));
            }
        });
        maxTorqueSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float max = seekBar.getMax();
                float toSet = 1000 * max * progress; //elmo is in per-thousand, not percent
                comex2.setMaxTorque((int) toSet);
                comex2.setMinTorque((int) -toSet);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        stoSwitch.setOnCheckedChangeListener((compoundButton, b) -> mWriter.write(buildSTOPacket(b)));
        enableSwitch.setOnCheckedChangeListener((compoundButton, b) -> mWriter.write(buildEnablePacket(b)));
        saveData = false;
        saveDataSwitch.setOnCheckedChangeListener((compoundButton, b) -> saveData = b);

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
        exportButton.setOnClickListener(v -> {
            Log.d(TAG, "Yay! We saved " + dataBox.count() + " things!");
            if (mDrawerLayout.isDrawerOpen(Gravity.END)) {
                mDrawerLayout.closeDrawer(Gravity.END, true);
            } else {
                mDrawerLayout.openDrawer(Gravity.END, true);
            }

            //todo export saved data
        });
        /*
        sendButton.setOnClickListener(v -> {
            cmdSpinnerHandler.sentClicked();
        });
        cmdSpinnerHandler = new CMDSpinnerHandler(this,
                findViewById(R.id.cmdSpinner),
                cmdArg,
                comex2
        );*/


        /*SensorListener mySensors = new SensorListener(
                (SensorManager) getSystemService(SENSOR_SERVICE),
                comex2,
                (TextView) findViewById(R.id.sensorText)
        );*/

        boxStore = ((ExoApplication) getApplication()).getBoxStore();
        dataBox = boxStore.boxFor(DataPacketDAO.class);
        
    }


    @Override
    protected void onResume() {
        super.onResume();
        mService.setOnEventCallback(this);
    }

    /*
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

            return true;
        }

        return super.onOptionsItemSelected(item);
    } */

    private void updateUI(DataPacket p) {
        if(p == null) {
            Log.e(tag, "updateUI was passed a null packet");
        }
        else {
            if(android.os.Build.VERSION.SDK_INT >= 24) {
                batteryLevelBar.setProgress((int) p.getVoltagePercent(), true);
            }
            else {
                batteryLevelBar.setProgress((int) p.getVoltagePercent());
            }

        }
    }


    @Override
    public void onDataRead(byte[] buffer, int length) {

        //Log.d(TAG, "onDataRead: " + BluetoothWriter.bytesToHex(buffer));

        packetFinder.push(buffer);
        while(packetFinder.getPacketsAvailable() >= 1) {
            dataBuffer.add(packetFinder.pop());
        }
        if (dataBuffer.size() >= 100) {
            updateUI(dataBuffer.getLast());
            if (saveData) {
                dataBox.put(dataBuffer);
                //boxStore.runInTxAsync(()->dataBox.put(dataBuffer.toArray(new DataPacketDAO[100])), null);
                dataBuffer.clear();
            }
        }
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
    }

    public class ControllerSpinnerHandler implements AdapterView.OnItemSelectedListener {

        ArrayAdapter<CtrlSpinnerItem> spinnerAdapter;
        Spinner uiHandle;

        ControllerSpinnerHandler(Context base, Spinner uiHandle) {
            this.uiHandle = uiHandle;
            CtrlSpinnerItem[] spinnerItems = {
                    new CtrlSpinnerItem("Passive", comex2.CTRL_PASSIVE),
                    new CtrlSpinnerItem("Quasi-Stiffness", comex2.CTRL_QUASI),
                    new CtrlSpinnerItem("Manual", comex2.CTRL_MANUAL),
                    new CtrlSpinnerItem("Position", comex2.CTRL_POSITION),
            };
            spinnerAdapter = new ArrayAdapter<>(base, android.R.layout.simple_spinner_dropdown_item, spinnerItems);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            this.uiHandle.setAdapter(spinnerAdapter);
            this.uiHandle.setOnItemSelectedListener(this);
        }

        public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
            CtrlSpinnerItem item = spinnerAdapter.getItem(position);
            if (item != null) {
                comex2.setController(item.controller);
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
        }

        private class CtrlSpinnerItem {
            public String name;
            int controller;

            CtrlSpinnerItem(String name, int controller) {
                this.name = name;
                this.controller = controller;
            }

            @Override
            public String toString() {
                return name;
            }
        }
    }


}
