package edu.utdallas.locolab.exoapp.packet;

import android.support.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import edu.utdallas.locolab.exoapp.experiment.ExperimentItem;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

/**
 * Created by jack on 12/22/17.
 */
@Entity
public class DataPacket {
    static final int packetLen = 45;
    static final int startOfPacket = 0x69;
    public static final int endOfPacket = 0x42;
    private static final int voltageMax = 1530;//1680;
    private static final int voltageMin = 1150;
    private static final int JOINT_INDEX = 1;
    private static final int TORQUE_INDEX = JOINT_INDEX + 4;
    private static final int STATUS_INDEX = TORQUE_INDEX + 4;
    private static final int BALL_INDEX = STATUS_INDEX + 1;
    private static final int HEEL_INDEX = BALL_INDEX + 2;
    private static final int VOLTAGE_INDEX = HEEL_INDEX + 2;
    private static final int TEMPL_INDEX = VOLTAGE_INDEX + 2;
    private static final int TEMPR_INDEX = TEMPL_INDEX + 2;
    private static final int ROLL_INDEX = TEMPR_INDEX + 2;
    private static final int PITCH_INDEX = ROLL_INDEX + 4;
    private static final int YAW_INDEX = PITCH_INDEX + 4;
    private static final int ERROR_INDEX = YAW_INDEX + 1;
    private static final int STATUSWORD_INDEX = ERROR_INDEX + 2;
    private static final int CONTROLWORD_INDEX = STATUSWORD_INDEX + 2;
    private static final int TIMESTAMP_INDEX = CONTROLWORD_INDEX + 2;
    private static final int DC_CURRENT_INDEX = TIMESTAMP_INDEX + 4;
    //private final byte[] data;
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
    private final long device;







    @Id private long id;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ToMany<ExperimentItem> experiments;

    public DataPacket() {
        //data = new byte[1];
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
        device = 0;
    }

    public DataPacket(long device, byte[] in) {
        //data = in;
        this.device = device;
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

    public long getDevice() { return device; }

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

    private static final double voltageRange = voltageMax-voltageMin;
    public double getVoltagePercent() {
        return 100 * (double)(voltage-voltageMin) / voltageRange;
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

    @NonNull
    @Contract(pure = true)
    public static String getHeader() {
        return "Record ID, Device MAC Address, Joint Angle (Counts), Torque Command (?), Status Byte, Ball Sensor, Heel Sensor, Battery Voltage, TempL, TempR, Roll, Pitch, Yaw, Error Byte, statusWord, controlWord, Timestamp, Current (0.1%)\n";
    }

    @Override
    public String toString() {
        return String.valueOf(id) + "," +
                device + "," +
                jointCounts + ',' +
                torqueCommand + ',' +//this is to get from %1000 to %
                status + ',' +
                ballSensor + ',' +
                heelSensor + ',' +
                voltage + ',' +
                tempL + ',' +
                tempR + ',' +
                roll + ',' +
                pitch + ',' +
                yaw + ',' +
                error + ',' +
                statusWord + ',' +
                controlWord + ',' +
                timestamp + ',' +
                current + '\n';
    }

}
