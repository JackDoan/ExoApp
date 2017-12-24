package edu.utdallas.locolab.exoapp.packet;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Created by jack on 12/22/17.
 */

public class DataPacket {
    public static final int packetLen = 45;
    public static final int startOfPacket = 0x69;
    public static final int endOfPacket = 0x42;
    private final int voltageMax = 1530;//1680;
    private final int voltageMin = 1150;
    private final int JOINT_INDEX = 1;
    private final int TORQUE_INDEX = JOINT_INDEX + 4;
    private final int STATUS_INDEX = TORQUE_INDEX + 4;
    private final int BALL_INDEX = STATUS_INDEX + 1;
    private final int HEEL_INDEX = BALL_INDEX + 2;
    private final int VOLTAGE_INDEX = HEEL_INDEX + 2;
    private final int TEMPL_INDEX = VOLTAGE_INDEX + 2;
    private final int TEMPR_INDEX = TEMPL_INDEX + 2;
    private final int ROLL_INDEX = TEMPR_INDEX + 2;
    private final int PITCH_INDEX = ROLL_INDEX + 4;
    private final int YAW_INDEX = PITCH_INDEX + 4;
    private final int ERROR_INDEX = YAW_INDEX + 1;
    private final int STATUSWORD_INDEX = ERROR_INDEX + 2;
    private final int CONTROLWORD_INDEX = STATUSWORD_INDEX + 2;
    private final int TIMESTAMP_INDEX = CONTROLWORD_INDEX + 2;
    private final int DC_CURRENT_INDEX = TIMESTAMP_INDEX + 4;
    private final byte[] data;
    private final int jointCounts;
    private final float torqueCommand;
    private final int status;
    private final int ballSensor;
    private final int heelSensor;
    private final int voltage;
    private final int tempL;
    private final int tempR;
    private final float roll;
    private final float pitch;
    private final float yaw;
    private final int error;
    private final int statusWord;
    private final int controlWord;
    private final int timestamp;
    private final int current;


    public DataPacket() {
        data = new byte[1];
        jointCounts = 0;
        torqueCommand = 0;
        status = 0;
        ballSensor = 0;
        heelSensor = 0;
        voltage = 0;
        tempL = 0;
        tempR = 0;
        roll = 0;
        yaw = 0;
        pitch = 0;
        error = 0;
        statusWord = 0;
        controlWord = 0;
        timestamp = 0;
        current = 0;
    }

    public DataPacket(byte[] in) {
        data = in;
        jointCounts = bytesToInt(in, JOINT_INDEX, 4);
        torqueCommand = bytesToFloat(in, TORQUE_INDEX);
        status = (int) in[STATUS_INDEX];
        ballSensor = bytesToInt(in, BALL_INDEX, 2);
        heelSensor = bytesToInt(in, HEEL_INDEX, 2);
        voltage = bytesToInt(in, VOLTAGE_INDEX, 2);
        tempL = bytesToInt(in, TEMPL_INDEX, 2);
        tempR = bytesToInt(in, TEMPR_INDEX, 2);
        roll = bytesToFloat(in, ROLL_INDEX);
        yaw = bytesToFloat(in, YAW_INDEX);
        pitch = bytesToFloat(in, PITCH_INDEX);
        error = in[ERROR_INDEX];
        statusWord = bytesToInt(in, STATUSWORD_INDEX, 2);
        controlWord = bytesToInt(in, CONTROLWORD_INDEX, 2);
        timestamp = bytesToInt(in, TIMESTAMP_INDEX, 4);
        current = bytesToInt(in, DC_CURRENT_INDEX, 2);
    }

    public int getJointCounts() {
        return jointCounts;
    }

    public float getTorqueCommand() {
        return torqueCommand;
    }

    public int getStatus() {
        return status;
    }

    public int getBallSensor() {
        return ballSensor;
    }

    public int getHeelSensor() {
        return heelSensor;
    }

    public int getVoltage() {
        return voltage;
    }

    public double getVoltagePercent() {
        return 100 * (double)(this.getVoltage()-this.voltageMin) / (double)(this.voltageMax-this.voltageMin);
    }

    public int getTempL() {
        return tempL;
    }

    public int getTempR() {
        return tempR;
    }

    public float getRoll() {
        return roll;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public int getError() {
        return error;
    }

    public int getStatusWord() {
        return statusWord;
    }

    public int getControlWord() {
        return controlWord;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public int getCurrent() {
        return current;
    }

    private int bytesToInt(byte[] in, int index, int len) {
        int toReturn = 0;
        /*for(int i = 0; i < len; i++) {
            toReturn |= (  ((int)in[index+i]) << 8*i);
        }*/

        if(len == 2) {
            int msb = ((int)in[index] << 8 ) & 0xFF00;
            int lsb = (int)in[index+1] & 0xFF;
            toReturn = (msb) | (lsb);
        }
        else if(len == 4) {
            toReturn = (bytesToInt(in, index, 2) << 16) |
                    (bytesToInt(in, index+2, 2));
        }


        return toReturn;
    }

    private float bytesToFloat(byte[] in, int index) {
        return Float.intBitsToFloat(bytesToInt(in, index, 4));
    }

    @Override
    public String toString() {
        return String.valueOf(jointCounts) +
                ',' +
                torqueCommand +
                ',' +
                status +
                ',' +
                ballSensor +
                ',' +
                heelSensor +
                ',' +
                voltage +
                ',' +
                tempL +
                ',' +
                tempR +
                ',' +
                roll +
                ',' +
                pitch +
                ',' +
                yaw +
                ',' +
                error +
                ',' +
                statusWord +
                ',' +
                controlWord +
                ',' +
                timestamp +
                ',' +
                current +
                '\n';
    }

}
