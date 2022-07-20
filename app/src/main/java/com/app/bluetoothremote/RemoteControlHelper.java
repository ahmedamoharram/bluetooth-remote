package com.app.bluetoothremote;

import android.annotation.SuppressLint;

public class RemoteControlHelper {

    public @interface Key {

        byte[] POWER = new byte[]{(byte) 0x00, (byte) 0x30};
        byte[] SLEEP = new byte[]{(byte) 0x00, (byte) 0x34};
        byte[] MENU = new byte[]{(byte) 0x00, (byte) 0x40};

        byte[] MENU_PICK = new byte[]{(byte) 0x00, (byte) 0x41};
        byte[] MENU_UP = new byte[]{(byte) 0x00, (byte) 0x42};
        byte[] MENU_DOWN = new byte[]{(byte) 0x00, (byte) 0x43};
        byte[] MENU_LEFT = new byte[]{(byte) 0x00, (byte) 0x44};
        byte[] MENU_RIGHT = new byte[]{(byte) 0x00, (byte) 0x45};



        byte[] CAPTIONS = new byte[]{(byte) 0x00, (byte) 0x61};

        byte[] VCR_TV = new byte[]{(byte) 0x00, (byte) 0x63};
        byte[] RED = new byte[]{(byte) 0x00, (byte) 0x69};
        byte[] GREEN = new byte[]{(byte) 0x00, (byte) 0x6a};
        byte[] BLUE = new byte[]{(byte) 0x00, (byte) 0x6b};
        byte[] YELLOW = new byte[]{(byte) 0x00, (byte) 0x6c};

        byte[] ASSIGN_SELECTION = new byte[]{(byte) 0x00, (byte) 0x81};
        byte[] MEDIA_SELECT_CD = new byte[]{(byte) 0x00, (byte) 0x91}; // SOURCE button in Tornado Skyworth Android TV?
        byte[] MEDIA_SELECT_HOME = new byte[]{(byte) 0x00, (byte) 0x9a};
        byte[] MEDIA_SELECT_SATELLITE = new byte[]{(byte) 0x00, (byte) 0x98};
        byte[] MEDIA_SELECT_TV = new byte[]{(byte) 0x00, (byte) 0x89};

        byte[] MEDIA_SELECT_SAP = new byte[]{(byte) 0x00, (byte) 0x9e};

        byte[] CHANNEL_INC = new byte[]{(byte) 0x00, (byte) 0x9c};
        byte[] CHANNEL_DEC = new byte[]{(byte) 0x00, (byte) 0x9d};

        byte[] MEDIA_FAST_FORWARD = new byte[]{(byte) 0x00, (byte) 0xb3};
        byte[] MEDIA_REWIND = new byte[]{(byte) 0x00, (byte) 0xb4};

        byte[] CHANNEL_UP = new byte[]{(byte) 0x00, (byte) 0x9c};
        byte[] CHANNEL_DOWN = new byte[]{(byte) 0x00, (byte) 0x9d};

        byte[] PLAY_PAUSE = new byte[]{(byte) 0x00, (byte) 0xcd};

        byte[] VOLUME_INC = new byte[]{(byte) 0x00, (byte) 0xe9};
        byte[] VOLUME_DEC = new byte[]{(byte) 0x00, (byte) 0xea};
        byte[] MUTE = new byte[]{(byte) 0x00, (byte) 0xe2};

        byte[] HOME = new byte[]{(byte) 0x02, (byte) 0x23};
        byte[] BACK = new byte[]{(byte) 0x02, (byte) 0x24};
        byte[] QUIT = new byte[]{(byte) 0x00, (byte) 0x94};

    }

    @SuppressLint("MissingPermission")
    public static boolean sendKeyDown(int byte1, int byte2) {
        if (BluetoothHidService.bluetoothHidDevice != null && BluetoothHidService.isHidDeviceConnected) {
            return BluetoothHidService.bluetoothHidDevice.sendReport(BluetoothHidService.bluetoothDevice, Constants.ID_REMOTE_CONTROL, RemoteControlReport.getReport(byte1, byte2));
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static boolean sendKeyUp() {
        if (BluetoothHidService.bluetoothHidDevice != null && BluetoothHidService.isHidDeviceConnected) {
            return BluetoothHidService.bluetoothHidDevice.sendReport(BluetoothHidService.bluetoothDevice, Constants.ID_REMOTE_CONTROL, RemoteControlReport.getReport(0, 0));
        }
        return false;
    }


}
