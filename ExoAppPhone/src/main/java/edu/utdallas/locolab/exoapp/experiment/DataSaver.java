package edu.utdallas.locolab.exoapp.experiment;

/**
 * Created by jack on 12/23/17.
 *
 */


import java.io.FileNotFoundException;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import edu.utdallas.locolab.exoapp.packet.DataPacket;
import io.objectbox.query.Query;

import static android.os.Environment.DIRECTORY_DOCUMENTS;


public class DataSaver extends Thread {

    public interface WriteComplete {
        void onComplete(File finishedFile); // would be in any signature
    }

    private File file;
    private FileOutputStream fileOutputStream;
    private WriteComplete writeComplete;
    private boolean fileOpen;

    private Query<DataPacket> query;

    public DataSaver(WriteComplete writeComplete) {
        this.writeComplete = writeComplete;
        fileOpen = false;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        Date today = Calendar.getInstance().getTime();
        String fileName = "Exo-Data-" + df.format(today) + ".csv";
        file = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), fileName);
        if(openFile()) {
            fileOpen = true;
        }
    }

    public void run() {
        if(query != null) {
            query.forEach(this::write); //apparently only works on unordered data? Lame.
            query.close();
        }
        this.writeComplete.onComplete(this.finish());
    }

    public void setQuery(Query<DataPacket> query) {
        this.query = query;
    }

    public boolean isFileOpen() {
        return fileOpen;
    }

    private boolean openFile() {
        boolean abort = false;
        try {
            if (!file.exists()) {
                abort = true;
                boolean mkdirs = true;
                if(!file.getParentFile().exists()) {
                    mkdirs = file.getParentFile().mkdirs();
                }
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
                //todo: DataPacket should provide this string
                fileOutputStream.write(DataPacket.getHeader().getBytes());
            } catch (FileNotFoundException e) {
                Log.e("DataSaving", e.getMessage());
                abort = true;
            } catch (IOException e) {
                Log.e("DataSaving", e.getMessage());
                abort = true;
            }
        }
        return !abort; //return true on success
    }

    public void write(DataPacket data) {
        try {
            //Log.d("FileWrite", data.toString());
            //openFile();
            fileOutputStream.write(data.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File finish() {
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}