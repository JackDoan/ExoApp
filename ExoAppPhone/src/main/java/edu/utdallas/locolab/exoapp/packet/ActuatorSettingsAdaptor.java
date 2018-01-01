package edu.utdallas.locolab.exoapp.packet;

/**
 * Created by jack on 12/22/17
 *
 * This provides an interface between the UI and the generation/sending of control packets
 *
 */


import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter;

import static edu.utdallas.locolab.exoapp.packet.ActuatorSettingsAdaptor.SettingID.*;

public class ActuatorSettingsAdaptor {

    public float getSwingGain() {
        lastUsed = SWING_GAIN;
        return swingGain;
    }

    public void setSwingGain(float swingGain) {
        mWriter.write(
                CommandPacket.buildChangeQSIPacket(
                        CommandPacket.CMD_SWING,
                        Float.floatToIntBits(swingGain)
                )
        );
        this.swingGain = swingGain;
    }

    public float getStanceGain() {
        lastUsed = STANCE_GAIN;
        return stanceGain;
    }

    public void setStanceGain(float stanceGain) {
        mWriter.write(
                CommandPacket.buildChangeQSIPacket(
                        CommandPacket.CMD_STANCE,
                        Float.floatToIntBits(stanceGain)
                )
        );
        this.stanceGain = stanceGain;
    }

    public int getThreshold() {
        lastUsed = THRESHOLD;
        return threshold;
    }

    public void setThreshold(int threshold) {
        mWriter.write(
                CommandPacket.buildChangeQSIPacket(
                        CommandPacket.CMD_THRESH,
                        Float.floatToIntBits(threshold)
                )
        );
        this.threshold = threshold;
    }

    public int getTorqueRamp() {
        lastUsed = TORQUE_RAMP;
        return torqueRamp;
    }

    public void setTorqueRamp(int torqueRamp) {
        mWriter.write(
                CommandPacket.buildTorqueRampPacket(
                        torqueRamp
                )
        );
        this.torqueRamp = torqueRamp;
    }

    public float getManualTorque() {
        lastUsed = MANUAL_TORQUE;
        return manualTorque;
    }

    public void setManualTorque(float manualTorque) {
        mWriter.write(
                CommandPacket.buildManualTorquePacket(
                        Float.floatToIntBits(manualTorque)
                )
        );
        this.manualTorque = manualTorque;
    }

    private float posDeg;

    public float getPosDeg() {
        lastUsed = POSDEG;
        return posDeg;
    }

    public void setPositionDeg(float deg) {
        //int countsPerRev = 8192;
        float degCounts = deg * 8192.0f/360.0f;
        mWriter.write(
                CommandPacket.buildManualTorquePacket( (int)degCounts )
        );
        //this.manualTorque = manualTorque;
    }

    public int getController() {
        lastUsed = CONTROLLER;
        return controller;
    }


    public final int CTRL_PASSIVE = 0;
    public final int CTRL_QUASI = 1;
    public final int CTRL_MANUAL = 3;
    public final int CTRL_POSITION = 4;

    public void setController(int controller) {
        mWriter.write(
                CommandPacket.buildChangeControllerPacket(controller)
        );
        this.controller = controller;
    }

    public int getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(int maxAngle) {
        this.maxAngle = maxAngle;
    }

    public int getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(int minAngle) {
        this.minAngle = minAngle;
    }

    public int getMaxTorque() {
        return maxTorque;
    }

    public void setMaxTorque(int maxTorque) {
        mWriter.write(
                CommandPacket.buildChangeSettingPacket(CommandPacket.CMD_TORQUEMAX, maxTorque)
        );
        this.maxTorque = maxTorque;
    }

    public int getMinTorque() {
        return minTorque;
    }

    public void setMinTorque(int minTorque) {
        mWriter.write(
                CommandPacket.buildChangeSettingPacket(CommandPacket.CMD_TORQUEMAX, minTorque)
        );
        this.minTorque = minTorque;
    }

    private final BluetoothWriter mWriter;
    private float swingGain;
    private float stanceGain;
    private float manualTorque;
    private int threshold, torqueRamp, maxAngle, minAngle, maxTorque, minTorque, controller;

    public enum SettingID {
        SWING_GAIN, STANCE_GAIN, THRESHOLD, TORQUE_RAMP, MAX_ANGLE,
        MIN_ANGLE, MAX_TORQUE, MIN_TORQUE, CONTROLLER, MANUAL_TORQUE, POSDEG, NONE
    }

    /*static public SettingID getLastUsed() {
        return lastUsed;
    }*/

    static private SettingID lastUsed;

    public ActuatorSettingsAdaptor(BluetoothWriter myExo) {
        mWriter = myExo;
        swingGain = 0.01f;
        stanceGain = -0.031f;
        threshold = 3850;
        torqueRamp = 8000;
        maxAngle = 75000;
        minAngle = -75000;
        maxTorque = 800;
        minTorque = -800;
        controller = 1; //qsi, 3=manual todo clean this up
        manualTorque = 0.0f;
        lastUsed = NONE;

    }

}
