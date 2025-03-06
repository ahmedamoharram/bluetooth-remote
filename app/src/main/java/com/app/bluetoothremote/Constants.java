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
    public static final byte ID_MOUSE = 3;


    public static final byte[] HID_REPORT_DESC_TEST = {

            (byte) 0x05, (byte) 0x01,                    // Usage Page (Generic Desktop)        0
            (byte) 0x09, (byte) 0x06,                    // Usage (Keyboard)                    2
            (byte) 0xa1, (byte) 0x01,                    // Collection (Application)            4
            (byte) 0x85, (byte) 0x01,                    //  Report ID (1)                      6
            (byte) 0x05, (byte) 0x07,                    //  Usage Page (Keyboard)              8
            (byte) 0x19, (byte) 0xe0,                    //  Usage Minimum (224)                10
            (byte) 0x29, (byte) 0xe7,                    //  Usage Maximum (231)                12
            (byte) 0x15, (byte) 0x00,                    //  Logical Minimum (0)                14
            (byte) 0x25, (byte) 0x01,                    //  Logical Maximum (1)                16
            (byte) 0x75, (byte) 0x01,                    //  Report Size (1)                    18
            (byte) 0x95, (byte) 0x08,                    //  Report Count (8)                   20
            (byte) 0x81, (byte) 0x02,                    //  Input (Data,Var,Abs)               22
            (byte) 0x75, (byte) 0x08,                    //  Report Size (8)                    24
            (byte) 0x95, (byte) 0x01,                    //  Report Count (1)                   26
            (byte) 0x81, (byte) 0x01,                    //  Input (Cnst,Arr,Abs)               28
            (byte) 0x75, (byte) 0x08,                    //  Report Size (8)                    30
            (byte) 0x95, (byte) 0x05,                    //  Report Count (5)                   32
            (byte) 0x15, (byte) 0x00,                    //  Logical Minimum (0)                34
            (byte) 0x25, (byte) 0xff,                    //  Logical Maximum (255)              36
            (byte) 0x05, (byte) 0x07,                    //  Usage Page (Keyboard)              38
            (byte) 0x19, (byte) 0x00,                    //  Usage Minimum (0)                  40
            (byte) 0x29, (byte) 0xff,                    //  Usage Maximum (255)                42
            (byte) 0x81, (byte) 0x00,                    //  Input (Data,Arr,Abs)               44
            (byte) 0xc0,                                 // End Collection                      46
            (byte) 0x05, (byte) 0x0c,                    // Usage Page (Consumer Devices)       47
            (byte) 0x09, (byte) 0x01,                    // Usage (Consumer Control)            49
            (byte) 0xa1, (byte) 0x01,                    // Collection (Application)            51
            (byte) 0x85, (byte) 0x03,                    //  Report ID (3)                      53
            (byte) 0x19, (byte) 0x00,                    //  Usage Minimum (0)                  55
            (byte) 0x2a, (byte) 0xff, (byte) 0x03,       //  Usage Maximum (1023)               57
            (byte) 0x75, (byte) 0x0c,                    //  Report Size (12)                   60
            (byte) 0x95, (byte) 0x01,                    //  Report Count (1)                   62
            (byte) 0x15, (byte) 0x00,                    //  Logical Minimum (0)                64
            (byte) 0x26, (byte) 0xff, (byte) 0x03,       //  Logical Maximum (1023)             66
            (byte) 0x81, (byte) 0x00,                    //  Input (Data,Arr,Abs)               69
            (byte) 0x75, (byte) 0x04,                    //  Report Size (4)                    71
            (byte) 0x95, (byte) 0x01,                    //  Report Count (1)                   73
            (byte) 0x81, (byte) 0x01,                    //  Input (Cnst,Arr,Abs)               75
            (byte) 0xc0,                                 // End Collection                      77
            (byte) 0x05, (byte) 0x01,                    // Usage Page (Generic Desktop)        78
            (byte) 0x09, (byte) 0x02,                    // Usage (Mouse)                       80
            (byte) 0xa1, (byte) 0x01,                    // Collection (Application)            82
            (byte) 0x85, (byte) 0x02,                    //  Report ID (2)                      84
            (byte) 0x09, (byte) 0x01,                    //  Usage (Pointer)                    86
            (byte) 0xa1, (byte) 0x00,                    //  Collection (Physical)              88
            (byte) 0x05, (byte) 0x09,                    //   Usage Page (Button)               90
            (byte) 0x19, (byte) 0x01,                    //   Usage Minimum (1)                 92
            (byte) 0x29, (byte) 0x05,                    //   Usage Maximum (5)                 94
            (byte) 0x15, (byte) 0x00,                    //   Logical Minimum (0)               96
            (byte) 0x25, (byte) 0x01,                    //   Logical Maximum (1)               98
            (byte) 0x75, (byte) 0x01,                    //   Report Size (1)                   100
            (byte) 0x95, (byte) 0x05,                    //   Report Count (5)                  102
            (byte) 0x81, (byte) 0x02,                    //   Input (Data,Var,Abs)              104
            (byte) 0x75, (byte) 0x03,                    //   Report Size (3)                   106
            (byte) 0x95, (byte) 0x01,                    //   Report Count (1)                  108
            (byte) 0x81, (byte) 0x01,                    //   Input (Cnst,Arr,Abs)              110
            (byte) 0x05, (byte) 0x01,                    //   Usage Page (Generic Desktop)      112
            (byte) 0x09, (byte) 0x30,                    //   Usage (X)                         114
            (byte) 0x09, (byte) 0x31,                    //   Usage (Y)                         116
            (byte) 0x09, (byte) 0x38,                    //   Usage (Wheel)                     118
            (byte) 0x15, (byte) 0x81,                    //   Logical Minimum (-127)            120
            (byte) 0x25, (byte) 0x7f,                    //   Logical Maximum (127)             122
            (byte) 0x75, (byte) 0x08,                    //   Report Size (8)                   124
            (byte) 0x95, (byte) 0x03,                    //   Report Count (3)                  126
            (byte) 0x81, (byte) 0x06,                    //   Input (Data,Var,Rel)              128
            (byte) 0x05, (byte) 0x0c,                    //   Usage Page (Consumer Devices)     130
            (byte) 0x0a, (byte) 0x38, (byte) 0x02,       //   Usage (AC Pan)                    132
            (byte) 0x95, (byte) 0x01,                    //   Report Count (1)                  135
            (byte) 0x81, (byte) 0x06,                    //   Input (Data,Var,Rel)              137
            (byte) 0xc0,                                 //  End Collection                     139
            (byte) 0xc0,                                 // End Collection                      140

            // 142 bytes
    };



    public static final byte[] HID_REPORT_DESC = {
            // Keyboard
            (byte) 0x05, (byte) 0x01,               // Usage page (Generic Desktop)
            (byte) 0x09, (byte) 0x06,               // Usage (Keyboard)
            (byte) 0xA1, (byte) 0x01,               // Collection (Application)
            (byte) 0x85, ID_KEYBOARD,               //   Report ID (1)
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
            (byte) 0xc0,                            //     END_COLLECTION

            // Mouse
            (byte) 0x05, (byte) 0x01,               // Usage Page (Generic Desktop)
            (byte) 0x09, (byte) 0x02,               // Usage (Mouse)
            (byte) 0xA1, (byte) 0x01,               // Collection (Application)
            (byte) 0x85, ID_MOUSE,                  //    Report ID
            (byte) 0x09, (byte) 0x01,               //    Usage (Pointer)
            (byte) 0xA1, (byte) 0x00,               //    Collection (Physical)
            (byte) 0x05, (byte) 0x09,               //       Usage Page (Buttons)
            (byte) 0x19, (byte) 0x01,               //       Usage minimum (1)
            (byte) 0x29, (byte) 0x03,               //       Usage maximum (3)
            (byte) 0x15, (byte) 0x00,               //       Logical minimum (0)
            (byte) 0x25, (byte) 0x01,               //       Logical maximum (1)
            (byte) 0x75, (byte) 0x01,               //       Report size (1)
            (byte) 0x95, (byte) 0x03,               //       Report count (3)
            (byte) 0x81, (byte) 0x02,               //       Input (Data, Variable, Absolute)
            (byte) 0x75, (byte) 0x05,               //       Report size (5)
            (byte) 0x95, (byte) 0x01,               //       Report count (1)
            (byte) 0x81, (byte) 0x01,               //       Input (constant)                 ; 5 bit padding
            (byte) 0x05, (byte) 0x01,               //       Usage page (Generic Desktop)
            (byte) 0x09, (byte) 0x30,               //       Usage (X)
            (byte) 0x09, (byte) 0x31,               //       Usage (Y)
            (byte) 0x09, (byte) 0x38,               //       Usage (Wheel)
            (byte) 0x15, (byte) 0x81,               //       Logical minimum (-127)
            (byte) 0x25, (byte) 0x7F,               //       Logical maximum (127)
            (byte) 0x75, (byte) 0x08,               //       Report size (8)
            (byte) 0x95, (byte) 0x03,               //       Report count (3)
            (byte) 0x81, (byte) 0x06,               //       Input (Data, Variable, Relative)
            (byte) 0xC0,                            //    End Collection
            (byte) 0xC0,                            // End Collection


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
                    BluetoothHidDevice.SUBCLASS1_COMBO,
//                    BluetoothHidDevice.SUBCLASS2_REMOTE_CONTROL,
//                    BluetoothHidDevice.SUBCLASS2_UNCATEGORIZED,
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
