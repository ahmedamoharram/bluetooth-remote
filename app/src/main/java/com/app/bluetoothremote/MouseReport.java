package com.app.bluetoothremote;

public class MouseReport {

    public static final byte[] reportData = new byte[4];

    public static byte[] getReport(boolean left, boolean right, boolean middle, int x, int y, int wheel) {
        //No need to fill with zero now
//        Arrays.fill(keyboardData, (byte) 0);

        int buttons = ((left ? 1 : 0) | (right ? 2 : 0) | (middle ? 4 : 0));
        reportData[0] = (byte) buttons;
        reportData[1] = (byte) x;
        reportData[2] = (byte) y;
        reportData[3] = (byte) wheel;

        return reportData;
    }
}
