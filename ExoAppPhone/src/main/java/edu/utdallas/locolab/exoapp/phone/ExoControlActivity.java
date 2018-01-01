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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedList;

import edu.utdallas.locolab.exoapp.packet.ActuatorSettingsAdaptor;
import edu.utdallas.locolab.exoapp.packet.DataPacket;
import edu.utdallas.locolab.exoapp.packet.DataSaver;
import edu.utdallas.locolab.exoapp.packet.PacketFinder;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.query.Query;
import io.objectbox.query.QueryBuilder;

import static edu.utdallas.locolab.exoapp.packet.CommandPacket.*;
import static edu.utdallas.locolab.exoapp.packet.DataPacket_.id;

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
    private Box<DataPacket> dataBox;
    private LinkedList<DataPacket> dataBuffer;
    private boolean saveData;
    private DrawerLayout mDrawerLayout;
    private ProgressBar batteryLevelBar;
    private EditText manualInput;
    private TextInputLayout manualInputLayout;

    private final int requestCode = 1;
    private int[] grantResults;
    private Switch saveDataSwitch;
    private ImageButton exportButton;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
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
        saveDataSwitch = findViewById(R.id.saveDataSwitch);
        Button autoCal = findViewById(R.id.autoCalID);
        Button reset = findViewById(R.id.autoCalID2);
        manualInput = findViewById(R.id.input_manual);
        manualInputLayout = findViewById(R.id.input_layout_manual);
        exportButton = findViewById(R.id.exportButton);
        mDrawerLayout = findViewById(R.id.drawer_layout);
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

        thresholdValueBox.addTextChangedListener(new ExoTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                comex2.setThreshold(Integer.valueOf(s.toString()));
            }
        });
        torqueRampValueBox.addTextChangedListener(new ExoTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                comex2.setTorqueRamp(Integer.valueOf(s.toString()));
            }
        });
        swingGainValueBox.addTextChangedListener(new ExoTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                comex2.setSwingGain(Float.valueOf(s.toString()));
            }
        });
        stanceGainValueBox.addTextChangedListener(new ExoTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                comex2.setStanceGain(Float.valueOf(s.toString()));
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
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        maxTorqueSeek.setOnTouchListener(new BoxBlocker());

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
            exportData();
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            //if you dont have required permissions ask for it (only required for API 23+)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
            onRequestPermissionsResult(requestCode, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, grantResults);
        }

        boxStore = ((ExoApplication) getApplication()).getBoxStore();
        dataBox = boxStore.boxFor(DataPacket.class);
        
    }

    private void exportData() {
        DataSaver ds = new DataSaver(this::sendEmail);

        if(ds.isFileOpen()) {
            QueryBuilder<DataPacket> builder = dataBox.query();
            Query<DataPacket> q = builder.notEqual(id,0).build(); //not ordering so we can use forEach
            ds.setQuery(q);
            //is this the best way to get all the data?
            //todo add a way to access data without connecting
            //also the id field can be used to order the data when it's plotting time
            ds.start();
        }
        else {
            Toast.makeText(this,"Data export failed.", Toast.LENGTH_LONG).show();
        }
    }

    private void sendEmail(File file) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure"); //todo fix this hack
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //todo don't hardcode this email address
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"jack.doan" + "@utdallas.edu"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Human Subject Experiment Data");
        intent.putExtra(Intent.EXTRA_TEXT, "Congratulations! You have received some data!");
        File root = Environment.getExternalStorageDirectory();
        if (!file.exists() || !file.canRead()) {
            //Toast.makeText(this, "Attachment Error", Toast.LENGTH_SHORT).show();
            //finish();
            return;
        }
        Uri uri = Uri.fromFile(file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "Send email..."));
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

    private void updateUI(double batteryLevelPercent) {
        if(android.os.Build.VERSION.SDK_INT >= 24) {
            batteryLevelBar.setProgress((int) batteryLevelPercent, true);
        }
        else {
            batteryLevelBar.setProgress((int) batteryLevelPercent);
        }
    }

    private void updateUI(DataPacket p) {
        if(p == null) {
            Log.e(tag, "updateUI was passed a null packet");
        }
        else {
            updateUI(p.getVoltagePercent());
        }
    }


    @Override
    public void onDataRead(byte[] buffer, int length) {
        packetFinder.push(buffer);
        while(packetFinder.getPacketsAvailable() >= 1) {
            dataBuffer.add(packetFinder.pop());
        }
        if (dataBuffer.size() >= 100) {
            double avgBatt = 0;
            for(DataPacket p : dataBuffer) {
                avgBatt += p.getVoltagePercent()/dataBuffer.size();
            }
            updateUI(avgBatt);
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("permission", "granted");
                } else {
                    // permission denied, boo! Disable the functionality that depends on this permission
                    Toast.makeText(this, R.string.no_extern_storage_perm_msg, Toast.LENGTH_LONG).show();
                    saveDataSwitch.setVisibility(View.GONE);
                    saveDataSwitch.setChecked(false);
                    saveData = false;
                    exportButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), R.string.no_extern_storage_perm_msg, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
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


    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null)
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /* UI element helper classes */

    private class ControllerSpinnerHandler implements AdapterView.OnItemSelectedListener {

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
                //hide all mode dependent views
                manualInputLayout.setVisibility(View.INVISIBLE);
                manualInput.setVisibility(View.INVISIBLE);
                if(item.controller == comex2.CTRL_MANUAL) {
                    manualInput.setHint("Manual Torque");
                    manualInputLayout.setVisibility(View.VISIBLE);
                    manualInput.setVisibility(View.VISIBLE);
                    manualInput.setText("0.0");
                    //manualInput.setHint("Torque");
                    showSoftKeyboard(manualInput);
                }
                else if(item.controller == comex2.CTRL_POSITION) {
                    manualInput.setHint("Manual Position");
                    manualInputLayout.setVisibility(View.VISIBLE);
                    manualInput.setVisibility(View.VISIBLE);
                    manualInput.setText("0.0");
                    //manualInput.setHint("Position");
                    //todo show keyboard
                    showSoftKeyboard(manualInput);
                }
                else {
                    hideSoftKeyboard();
                }
            }
        }

        public void onNothingSelected(AdapterView<?> parent) { }

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

    private abstract class ExoTextWatcher implements TextWatcher{
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
    }

    private class BoxBlocker implements View.OnTouchListener{
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //ripped from https://stackoverflow.com/questions/24731137/difference-between-androidid-and-androidlabelfor
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    // Disallow Drawer to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;

                case MotionEvent.ACTION_UP:
                    // Allow Drawer to intercept touch events.
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            v.performClick();
            // Handle seekbar touch events.
            v.onTouchEvent(event);
            return true;
        }



        BoxBlocker() { }
    }

    /* end UI element helper classes */

}
