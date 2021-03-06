package edu.utdallas.locolab.exoapp.bluetooth;

import android.bluetooth.BluetoothDevice;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothDeviceDecorator;

/**
 * Created by jack on 12/22/17.
 */

public class BluetoothExoDecorator extends BluetoothDeviceDecorator {

    private final String exoFilter = "Comex";
    private boolean isExo = false;

    public BluetoothExoDecorator(BluetoothDevice device) {
        super(device);
        if (device.getName() != null)
            isExo = device.getName().contains(exoFilter);
    }

    public BluetoothExoDecorator(BluetoothDevice device, int RSSI) {
        this(device);
        super.setRSSI(RSSI);
    }

    public boolean isExo() {
        return isExo;
    }
}
