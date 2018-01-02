package edu.utdallas.locolab.exoapp.experiment;

import java.io.File;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;

/**
 * Created by jack on 12/22/17.
 */

@Entity
public class ExperimentItem {

    @Transient
    private File dataFile;

    @Id
    private long id;
    private boolean recording;



    private String name;

    public ExperimentItem() {
    }

    void setRecording(boolean recording) {
        this.recording = recording;
    }

    public boolean getRecording() {
        return recording;
    }

    public long getId() {
        return id;
    }

    public void setId(long in) {
        id = in;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }



    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BluetoothDeviceDecorator that = (BluetoothDeviceDecorator) o;

        return mDevice.equals(that.mDevice);
    }*/

    /*@Override
    public int hashCode() {
        return mDevice.hashCode();
    }
    */

}
