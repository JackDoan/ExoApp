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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;


import java.util.Arrays;

import edu.utdallas.locolab.exoapp.bluetooth.BluetoothExoDecorator;

public class MainActivity extends AppCompatActivity implements BluetoothService.OnBluetoothScanCallback, BluetoothService.OnBluetoothEventCallback, DeviceItemAdapter.OnAdapterItemClickListener {

    public static final String TAG = "ExoApp";
    private static final int REQUEST_ENABLE_BT = 1;

    private ProgressBar pgBar;
    private Menu mMenu;
    private RecyclerView mRecyclerView;
    private DeviceItemAdapter mAdapter;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothService mService;
    private boolean mScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Select an Actuator:");
        pgBar = findViewById(R.id.pg_bar);
        pgBar.setVisibility(View.GONE);
        //todo request bt permissions
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.no_bt_msg, Toast.LENGTH_LONG).show();
            finish(); //abandon ship
        } else if(!mBluetoothAdapter.isEnabled()) {
            //prompt to turn bt on
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }


        mRecyclerView = findViewById(R.id.rv);
        LinearLayoutManager lm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(lm);

        //mAdapter = new DeviceItemAdapter(this, mBluetoothAdapter.getBondedDevices());
        mAdapter = new DeviceItemAdapter(this); //use the above to display paired devices
        mAdapter.setOnAdapterItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        mService = BluetoothService.getDefaultInstance();

        mService.setOnScanCallback(this);
        mService.setOnEventCallback(this);
        //startStopScan();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mService.setOnEventCallback(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_scan) {
            startStopScan();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startStopScan() {
        if (!mScanning) {
            mService.startScan();
        } else {
            mService.stopScan();
        }
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice device, int rssi) {
        /*todo: this has a bug that makes it so if there is something at index 0, and we find an exoskeleton, the existing item gets duplicated */
        Log.d(TAG, "onDeviceDiscovered: " + device.getName() + " - " + device.getAddress() + " - " + Arrays.toString(device.getUuids()));
        BluetoothExoDecorator dv = new BluetoothExoDecorator(device, rssi);
        int index = mAdapter.getDevices().indexOf(dv);
        if (index < 0) {
            mAdapter.getDevices().add(dv);
            if(dv.isExo()) {
                //put it on top
                mAdapter.notifyItemInserted(0);
            }
            else {
                //put it at the bottom
                mAdapter.notifyItemInserted(mAdapter.getDevices().size() - 1);
            }

        } else {
            mAdapter.getDevices().get(index).setDevice(device);
            mAdapter.getDevices().get(index).setRSSI(rssi);
            mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void onStartScan() {
        Log.d(TAG, "onStartScan");
        mScanning = true;
        pgBar.setVisibility(View.VISIBLE);
        mMenu.findItem(R.id.action_scan).setTitle(R.string.action_stop);
    }

    @Override
    public void onStopScan() {
        Log.d(TAG, "onStopScan");
        mScanning = false;
        pgBar.setVisibility(View.GONE);
        mMenu.findItem(R.id.action_scan).setTitle(R.string.action_scan);
    }

    @Override
    public void onDataRead(byte[] buffer, int length) {
        onDataRead(buffer, length, 0);
    }

    @Override
    public void onDataRead(byte[] buffer, int length, long deviceAddr) {
        Log.d(TAG, "onDataRead");
    }

    @Override
    public void onStatusChange(BluetoothStatus status) {
        Log.d(TAG, "onStatusChange: " + status);
        Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show();

        if (status == BluetoothStatus.CONNECTED) {
            startActivity(new Intent(MainActivity.this, ExoControlActivity.class));
            /*CharSequence colors[] = new CharSequence[]{"Console", "Control Panel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select");
            builder.setItems(colors, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        startActivity(new Intent(MainActivity.this, DeviceActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, ExoControlActivity.class));
                    }
                }
            });
            builder.setCancelable(false);
            builder.show();*/
        }

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

    @Override
    public void onItemClick(BluetoothExoDecorator device, int position) {
        mService.connect(device.getDevice());
    }
}
