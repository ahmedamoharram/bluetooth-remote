package com.app.bluetoothremote;

public class RemoteControlReport {

    public static final byte[] reportData = new byte[2];

    public static byte[] getReport(int byte1, int byte2) {
        //No need to fill with zero now
//        Arrays.fill(keyboardData, (byte) 0);

        // We have to swap the bytes here, as the data are sent with MSB on right,
        // for example 0x00ea should be sent as ea 00
        reportData[0] = (byte) byte2;
        reportData[1] = (byte) byte1;

//        reportData[0] = (byte) byte1;
//        reportData[1] = (byte) byte2;

        return reportData;
    }

}
