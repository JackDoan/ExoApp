package edu.utdallas.locolab.exoapp.packet;

import org.jetbrains.annotations.Contract;

/**
 * Created by jack on 12/22/17.
 */

public class CommandPacket {
    //private final byte[] CMD_STO_ON          = {0x69,0x01,0x0};
    // final byte[] CMD_STO_OFF         = {0x69,-127,0x0};//{0x69,0x81,0x0};
    // final byte[] CMD_ENABLE_ON       = {0x69,0x02,0x0};
    //private final byte[] CMD_ENABLE_OFF      = {0x69,-126,0x0};//{0x69,0x82,0x0};
    // final byte[] CMD_RESET_ENCODER   = {0x69,0x09,0x0};
    //private final byte[] CMD_SET_TORQUE_FADE   = {0x69,(byte)145,0x4,0x40,0x1F,0x00,0x00};

    public static final int CMD_SWING = 0;
    public static final int CMD_STANCE = 2;
    public static final int CMD_THRESH = 3;
    public static final int CMD_ANGLEMIN = 0;
    public static final int CMD_ANGLEMAX = 1;
    public static final int CMD_TORQUEMIN = 2;
    public static final int CMD_TORQUEMAX = 3;
    //private final byte HUMAN_MODE = 0xFD;
    //private final byte ERROR = 0xFE;
    //private final byte LATCH_FRAME = 0x03;
    //private final byte FRAME_LATCHED = 0x43;
    private static final byte GET_MAX_COUNTS = 0x05;

    private static final byte START_BYTE = 0x69;
    public CommandPacket() {
    }

    @org.jetbrains.annotations.Contract(pure = true)
    private static byte[]buildPacket(byte type) {
        byte[] data = new byte[3];
        data[0] = START_BYTE;
        data[1] = type;
        data[2] = 0;
        return data;
    }

    private static byte[]buildPacket(byte type, byte[] args) {
        byte[] data = new byte[args.length + 3];
        data[0] = START_BYTE;
        data[1] = type;
        data[2] = (byte) args.length;
        System.arraycopy(args, 0, data, 3, args.length);
        return data;
    }

    private static void intToBytes(byte[] arr, int index, int value) {
        arr[index] = (byte)(value);
        arr[index+1] = (byte)( (value >>  8));
        arr[index+2] = (byte)( (value >> 16));
        arr[index+3] = (byte)( (value >> 24));
    }


    public static byte[] buildChangeSettingPacket(int setting, int value) {
        byte[] args = new byte[5];
        args[0] = (byte)setting;
        intToBytes(args, 1, value);
        byte SET_SAFETY_SETTING = (byte) 0x90;
        return buildPacket(SET_SAFETY_SETTING, args);
    }

    public static byte[] buildChangeQSIPacket(int setting, int value) {
        byte[] args = new byte[5];
        args[0] = (byte)setting;
        intToBytes(args, 1, value);
        byte SET_QUASI_GAINS = -121;
        return buildPacket(SET_QUASI_GAINS, args);
    }

    public static byte[] buildChangeControllerPacket(int value) {
        byte[] args = new byte[4];
        intToBytes(args, 0, value);
        byte SET_CONTROLLER = (byte) 0x88;
        return buildPacket(SET_CONTROLLER, args);
    }

    public static byte[] buildManualTorquePacket(int value) {
        byte[] args = new byte[4];
        intToBytes(args, 0, value);
        byte SET_MOTION_PLAN = (byte) 0xAA;
        return buildPacket(SET_MOTION_PLAN, args);
    }

    public static byte[] buildSDOPacket(int reg, int value) {
        byte[] args = new byte[6];
        intToBytes(args, 0, reg);
        intToBytes(args, 2, value);
        byte SEND_SDO = (byte) 145;
        return buildPacket(SEND_SDO, args);
    }

    public static byte[] buildTorqueRampPacket(int value) {
        return buildSDOPacket(0x6087, value);
    }

    @Contract(pure = true)
    public static byte[] buildSTOPacket(boolean on) {
        byte STO_HIGH = 0x01;
        byte STO_LOW = -127;
        byte type = STO_LOW;
        if(on) {
            type = STO_HIGH;
        }
        else {
            type = STO_LOW;
        }
        return buildPacket(type);
    }

    @Contract(pure = true)
    public static byte[] buildEnablePacket(boolean on) {
        byte ENABLE_HIGH = 0x02;
        byte ENABLE_LOW = -126;
        byte type = ENABLE_LOW;
        if(on) {
            type = ENABLE_HIGH;
        }
        else {
            type = ENABLE_LOW;
        }
        return buildPacket(type);
    }

    @Contract(pure = true)
    public static byte[] buildResetEncoderPacket() {
        byte RESET_ENCODER = 0x09;
        return buildPacket(RESET_ENCODER);
    }
}
