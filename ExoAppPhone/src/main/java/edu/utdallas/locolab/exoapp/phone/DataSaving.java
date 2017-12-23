package edu.utdallas.locolab.exoapp.phone;

/**
 * Created by jack on 12/23/17.
 */

public class DataSaving {
}

/*
package edu.utdallas.locolab.comex2.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.utdallas.locolab.comex2.Exoskeleton.DataPacket;
import edu.utdallas.locolab.comex2.R;

import static android.os.Environment.DIRECTORY_DOCUMENTS;


public class DataSavingSettingsFragment extends Fragment {

    private EditText fileNameBox;
    private Switch dataSavingSwitch;
    private Button changeFileButton;
    private File file;
    private FileOutputStream fileOutputStream;
    //private EventBus bus;
    private TextView utdAddr;
    private boolean dataSaving;

    public DataSavingSettingsFragment steal() {
        return DataSavingSettingsFragment.this;
    }

    @Override
    public void onStart() {
        super.onStart();
        dataSaving = false;
        try {
            fileNameBox = getView().findViewById(R.id.fileNameBox);
            utdAddr = getView().findViewById(R.id.utdAddr);
            dataSavingSwitch = getView().findViewById(R.id.dataSavingSwitch);
            dataSavingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    dataSaving = isChecked;
                }
            });
            changeFileButton = getView().findViewById(R.id.useFileButton);
            Button emailButton = getView().findViewById(R.id.emailFile);
            emailButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{utdAddr.getText() + "@utdallas.edu"});
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
            });
        } catch (NullPointerException e) {
            //something bad has happened, abort
            Log.e("DATASAVING_ABORT", e.getMessage());
            onDestroy();
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        Date today = Calendar.getInstance().getTime();

        String fileName = "Exo-Data-" + df.format(today) + ".csv";
        file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), fileName);
        fileNameBox.setText(fileName);
        openFile();


    }

    private void openFile() {
        boolean abort = false;
        try {
            if (!file.exists()) {
                abort = true;
                final boolean mkdirs = file.getParentFile().mkdirs();
                final boolean newFile = file.createNewFile();
                if (mkdirs && newFile) {
                    abort = false;
                }
            }
        } catch (IOException e) {
            abort = true;
            Log.e("DataSaving", e.getMessage());
        }

        if (!abort) {
            try {
                fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write("Joint Angle (Counts), Status Byte, Ball Sensor, Heel Sensor, Battery Voltage, TempL, TempR, Roll, Pitch, Yaw, Error Byte, statusWord, controlWord, Timestamp, Current (0.1%)\n".getBytes());
            } catch (FileNotFoundException e) {
                Log.e("DataSaving", e.getMessage());
            } catch (IOException e) {
                Log.e("DataSaving", e.getMessage());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void writeData(DataPacket data) {
        if(this.dataSaving) {
            try {
                //Log.d("FileWrite", data.toString());
                //openFile();
                fileOutputStream.write(data.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.data_saving_frag, container, false);

    }

}

 */