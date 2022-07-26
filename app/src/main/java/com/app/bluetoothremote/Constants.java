package com.app.bluetoothremote;

import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothHidDeviceAppQosSettings;
import android.bluetooth.BluetoothHidDeviceAppSdpSettings;
import android.os.ParcelUuid;

/**
 * Constants for the HID Report Descriptor and SDP configuration.
 * Useful links:
 * https://source.android.com/devices/input/keyboard-devices#hid-consumer-page-0x0c
 * hut1_12v2 (HID Codes).pdf
 * file:///C:/Users/ahmed/Downloads/hut1_12v2%20(HID%20Codes).pdf
 */
public class Constants {

    // HID related UUIDs
    public static final ParcelUuid HOGP_UUID = ParcelUuid.fromString("00001812-0000-1000-8000-00805f9b34fb");
    public static final ParcelUuid HID_UUID = ParcelUuid.fromString("00001124-0000-1000-8000-00805f9b34fb");

    public static final ParcelUuid DIS_UUID = ParcelUuid.fromString("0000180A-0000-1000-8000-00805F9B34FB");

    public static final ParcelUuid BAS_UUID = ParcelUuid.fromString("0000180F-0000-1000-8000-00805F9B34FB");

    public static final byte ID_KEYBOARD = 1;
    public static final byte ID_REMOTE_CONTROL = 2;

    private static final byte[] HID_REPORT_DESC = {
            // Keyboard
            (byte) 0x05, (byte) 0x01,               // Usage page (Generic Desktop)
            (byte) 0x09, (byte) 0x06,               // Usage (Keyboard)
            (byte) 0xA1, (byte) 0x01,               // Collection (Application)
            (byte) 0x85, ID_KEYBOARD,               //   Report ID
            (byte) 0x05, (byte) 0x07,               //   Usage page (Keyboard Key Codes)
            (byte) 0x19, (byte) 0xE0,               //   Usage minimum (224) Keyboard LeftControl
            (byte) 0x29, (byte) 0xE7,               //   Usage maximum (231) Keyboard Right GUI
            (byte) 0x15, (byte) 0x00,               //   Logical minimum (0)
            (byte) 0x25, (byte) 0x01,               //   Logical maximum (1)
            (byte) 0x75, (byte) 0x01,               //   Report size (1) bit
            (byte) 0x95, (byte) 0x08,               //   Report count (8)
            (byte) 0x81, (byte) 0x02,               //   Input (Data, Variable, Absolute)     ; Modifier byte
            // no need for a reserve byte i think...
//            (byte) 0x75, (byte) 0x08,               //       Report size (8)
//            (byte) 0x95, (byte) 0x01,               //       Report count (1)
//            (byte) 0x81, (byte) 0x01,               //       Input (Constant)                   ; Reserved byte
            // Keyboard Key
            (byte) 0x75, (byte) 0x08,               //    Report size (8) 8 bits
            (byte) 0x95, (byte) 0x01,               //    Report count (1)
            (byte) 0x15, (byte) 0x00,               //    Logical Minimum (0)
            (byte) 0x26, (byte) 0xFF, (byte) 0x00,  //    Logical Maximum (255)
            (byte) 0x05, (byte) 0x07,               //    Usage page (Keyboard Key Codes)
            (byte) 0x19, (byte) 0x00,               //    Usage Minimum (0)
            (byte) 0x29, (byte) 0xFF,               //    Usage Maximum (255)
            (byte) 0x81, (byte) 0x00,               //    Input (Data, Arr, Absolute)     ; Key array (1 key)
            (byte) 0xC0,                            // End Collection

            // Remote control
            (byte) 0x05, (byte) 0x0c,               //     USAGE_PAGE (Consumer Devices)
            (byte) 0x09, (byte) 0x01,               //     USAGE (Consumer Control)
            (byte) 0xa1, (byte) 0x01,               //     COLLECTION (Application)
            (byte) 0x85, ID_REMOTE_CONTROL,         //       REPORT_ID (2)
            (byte) 0x19, (byte) 0x00,               //       USAGE_MINIMUM (Unassigned)
            (byte) 0x2a, (byte) 0xff, (byte) 0x03,  //       USAGE_MAXIMUM (1023)
            (byte) 0x75, (byte) 0x0a,               //       REPORT_SIZE (10) bit
            (byte) 0x95, (byte) 0x01,               //       REPORT_COUNT (1)       10x1=1byte
            (byte) 0x15, (byte) 0x00,               //       LOGICAL_MINIMUM (0)
            (byte) 0x26, (byte) 0xff, (byte) 0x03,  //       LOGICAL_MAXIMUM (1023)
            (byte) 0x81, (byte) 0x00,               //       INPUT (Data,Ary,Abs)
            (byte) 0xc0                             //     END_COLLECTION

    };

    private static final String SDP_NAME = "BTRemote";
    private static final String SDP_DESCRIPTION = "BTRemote";
    private static final String SDP_PROVIDER = "AAM";
    private static final int QOS_TOKEN_RATE = 800; // 9 bytes * 1000000 us / 11250 us
    private static final int QOS_TOKEN_BUCKET_SIZE = 9;
    private static final int QOS_PEAK_BANDWIDTH = 0;
    private static final int QOS_LATENCY = 11250;

    public static final BluetoothHidDeviceAppSdpSettings SDP_RECORD =
            new BluetoothHidDeviceAppSdpSettings(
                    Constants.SDP_NAME,
                    Constants.SDP_DESCRIPTION,
                    Constants.SDP_PROVIDER,
//                    BluetoothHidDevice.SUBCLASS1_MOUSE,
//                    BluetoothHidDevice.SUBCLASS1_KEYBOARD,
//                    BluetoothHidDevice.SUBCLASS1_COMBO,
//                    BluetoothHidDevice.SUBCLASS2_REMOTE_CONTROL,
                    BluetoothHidDevice.SUBCLASS2_UNCATEGORIZED,
//                    BluetoothHidDevice.SUBCLASS1_NONE,
                    Constants.HID_REPORT_DESC);
//                    Constants.HID_REPORT_DESC_TEST);

    public static final BluetoothHidDeviceAppQosSettings QOS_OUT =
            new BluetoothHidDeviceAppQosSettings(
                    BluetoothHidDeviceAppQosSettings.SERVICE_BEST_EFFORT,
                    Constants.QOS_TOKEN_RATE,
                    Constants.QOS_TOKEN_BUCKET_SIZE,
                    Constants.QOS_PEAK_BANDWIDTH,
                    Constants.QOS_LATENCY,
                    BluetoothHidDeviceAppQosSettings.MAX);
}
