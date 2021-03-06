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

import android.app.Application;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;

import java.util.UUID;

import edu.utdallas.locolab.exoapp.MyObjectBox;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

/**
 * Created by jad140230 on 12/22/2017
 */
public class ExoApplication extends Application {

    private static final UUID UUID_DEVICE = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BoxStore boxStore;

    @Override
    public void onCreate() {
        super.onCreate();
        boxStore = MyObjectBox.builder().androidContext(ExoApplication.this).build();

        BluetoothConfiguration config = new BluetoothConfiguration();

        config.bluetoothServiceClass = BluetoothClassicService.class; //  BluetoothClassicService.class or BluetoothLeService.class

        config.context = getApplicationContext();
        config.bufferSize = 10240;
        config.characterDelimiter = 0x42;
        config.useDelimeter = true;
        config.deviceName = "ExoApp";
        config.callListenersInMainThread = true;
        config.uuid = UUID_DEVICE; // For Classic

        BluetoothService.init(config);
    }

    public BoxStore getBoxStore() {
        return boxStore;
    }
}
