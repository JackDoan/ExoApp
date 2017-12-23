package edu.utdallas.locolab.exoapp.packet;

import junit.framework.TestCase;

/**
 * Created by jack on 12/22/17.
 */
public class PacketFinderTest extends TestCase {

    String exactPacket = "426900000000000000000C0C8F0D5D05760D5D0C8F4166BA3CC0AE9DEB3BCCB792010233002700D8A3F60000";
    String halfPacketA = "426900000000000000000C0C8F0D5D05760D5D0C8F4166BA3CC0";
    String halfPacketB = "AE9DEB3BCCB792010233002700D8A3F60000";

    PacketFinder pf;

    public void setUp() throws Exception {
        super.setUp();
        pf = new PacketFinder();
    }

    public void tearDown() throws Exception {
    }

    public void testPush() throws Exception {

    }

}