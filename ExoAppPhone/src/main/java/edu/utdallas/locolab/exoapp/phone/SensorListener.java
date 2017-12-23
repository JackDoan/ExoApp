package edu.utdallas.locolab.exoapp.phone;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.TextView;

import edu.utdallas.locolab.exoapp.packet.ActuatorSettingsAdaptor;

/**
 * Created by jack on 12/22/17.
 */

public class SensorListener implements SensorEventListener {
    private float[] mGravity;
    private float[] mGeomagnetic;
    private boolean enabled;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private SensorManager mSensorManager;
    private ActuatorSettingsAdaptor comex2;
    private TextView sensorText;

    public SensorListener(SensorManager mSensorManager, ActuatorSettingsAdaptor comex2, TextView sensorText) {
        this.comex2 = comex2;
        this.sensorText = sensorText;
        this.mSensorManager = mSensorManager;
        enabled = false;
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorText.setVisibility(View.GONE);
        //mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, 6000);
        //mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME, 6000);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                float radToDeg = 180.0f / 3.14159f;
                SensorManager.getOrientation(R, orientation);
                sensorText.setText(
                        Float.toString(orientation[0] * radToDeg).split("\\.")[0]
                                + " " + Float.toString(orientation[1] * radToDeg).split("\\.")[0]
                                + " " + Float.toString(orientation[2] * radToDeg).split("\\.")[0]
                );
                if (enabled) {
                    comex2.setPositionDeg(24.0f * orientation[1] * radToDeg);
                }

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void enable() {
        enabled = true;
        sensorText.setVisibility(View.VISIBLE);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, 6000);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME, 6000);
    }

    public void disable() {
        enabled = false;
        sensorText.setVisibility(View.GONE);
        mSensorManager.unregisterListener(this);  //registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME, 6000);
        //mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME, 6000);
    }
}

