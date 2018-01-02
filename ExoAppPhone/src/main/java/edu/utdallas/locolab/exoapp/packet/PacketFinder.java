package edu.utdallas.locolab.exoapp.packet;

import android.util.Log;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothWriter;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by jack on 12/22/17.
 *
 */

public class PacketFinder {
    private final boolean D = false;
    private byte[] byteHolder;
    private LinkedList<DataPacket> stack;

    public PacketFinder() {
        //byteHolder = new byte[DataPacket.packetLen];
        stack = new LinkedList<>();
    }


    private byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    private void log(String l, byte[] b) {
        if (D) {
            Log.d("PacketFinder", l + " " + BluetoothWriter.bytesToHex(b));
        }
    }

    public void push(long device, byte[] buffer) {
        log("Input: ", buffer);
        if(byteHolder == null) {
            if (buffer.length < DataPacket.packetLen - 1) {
                byteHolder = buffer;
                log("Stord: ", buffer);
                //the buffer is empty and we didn't get enough: hang onto it and return
            }
            else { //no buffer to splice, find and /maybe/ add a packet?

                int head = findHeader(buffer);
                if(head < 0) {
                    Log.e("PacketFinder", "Rejected: " + BluetoothWriter.bytesToHex(buffer));
                    return; //the packet was all garbage
                }

                //we need to dump the garbage before the header:
                buffer = Arrays.copyOfRange(buffer, head, buffer.length);

                if (buffer.length >= DataPacket.packetLen - 1) {
                    //there should be an entire packet here!
                    byte[] pkt = Arrays.copyOfRange(buffer, 0, DataPacket.packetLen - 1); //changed 1 to 0 bc delimiter
                    log("Found: ", pkt);
                    stack.add(new DataPacket(device, pkt)); //from 1 for framing reasons
                    buffer = Arrays.copyOfRange(buffer, DataPacket.packetLen - 1, buffer.length); //keep the 0x42 in there
                    if (buffer.length >= DataPacket.packetLen - 1) {
                        push(device, buffer);
                    } else if (buffer.length == 0) {
                        byteHolder = null;
                    } else {
                        log("Store: ", buffer);
                        byteHolder = buffer;
                        //done!
                    }
                }
                else {
                    //there was /not/ enough data to fill a packet, but we have a header and a partial packet!
                    log("Too small: ", buffer);
                    byteHolder = buffer;
                    //done!
                }
            }
        }
        else {
            //there's something in the byteHolder
            buffer = concat(byteHolder, buffer);
            byteHolder = null;
            push(device, buffer); //recursive call baby!
        }
    }

    @Contract(pure = true)
    private int findHeader(byte[] buffer) {
        int i = 0;
        int toReturn = -1;
        while(i < buffer.length-1) {
            /*if(
                    buffer[i] == DataPacket.endOfPacket &&
                    buffer[i+1] == DataPacket.startOfPacket) {
                toReturn = i;
                break;
            }*/
            if (
                    buffer[i] == DataPacket.startOfPacket) {
                toReturn = i;
                break;
            }
            i++;
        }
        return toReturn;
    }

    public int getPacketsAvailable() {
        return stack.size();
    }

    public DataPacket pop() {
        return stack.removeFirst();
    }


    /*private boolean findHeader() {
        int x = 0;
        int y = 0;
        boolean success = false;
        try {
            do {
                x = mmInStream.read();
                if (x == DataPacket.startOfPacket) {
                    y = mmInStream.read();
                    if (y == 13) {
                        success = true;
                    }
                }
            } while (!success && mmInStream.available() >= 2);
        } catch(IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return (x == DataPacket.startOfPacket);
    } */

    /*private boolean producePacket() {
        boolean success = false;
        totalPackets++;
        try { //use DataPacket.packetLen-1 because we already grabbed the header
            if (findHeader() && mmInStream.available() >= DataPacket.packetLen - 2) {
                byte[] buf = new byte[DataPacket.packetLen];
                buf[0] = DataPacket.startOfPacket;
                mmInStream.read(buf, 1, DataPacket.packetLen - 2); //changed because framing as 0x6969

                // Send the obtained bytes to the UI activity.
                if (buf[43] == 0x42) {
                    Message readMsg = mHandler.obtainMessage(BTService.MessageConstants.MESSAGE_READ, 45, -1, buf);
                    //Log.d(TAG, "Packet found:  " + bytesToHex(buf));
                    readMsg.sendToTarget();
                    success = true;
                } else {
                    failedPackets++;
                    pktRatio = failedPackets / totalPackets;
                    Log.e(TAG, "Packet failed: " + bytesToHex(buf) + " " + (pktRatio * 100) + "%");
                    mmInStream.read(); //todo: does this help or hurt?
                }

            } else {
                //todo get more bytes
            }
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        return success;
    }*/

}
