package com.app.bluetoothremote;

public class KeyboardReport {

    public static final byte[] keyboardData = new byte[2];

    public static byte[] getReport(int modifier, int key) {
        //No need to fill with zero now
//        Arrays.fill(keyboardData, (byte) 0);
        keyboardData[0] = (byte) modifier;
//        keyboardData[1] = 0;    //reserve byte
        keyboardData[1] = (byte) key;
//        keyboardData[3] = 0;    //End reserve byte
        return keyboardData;
    }

    public static String print(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ ");
        for (byte b : bytes) {
            sb.append(String.format("0x%02X, ", b));
        }
        sb.append("]");
        return sb.toString();
    }

}
