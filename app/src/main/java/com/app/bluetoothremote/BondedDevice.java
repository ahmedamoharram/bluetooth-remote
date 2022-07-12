package com.app.bluetoothremote;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public class BondedDevice {
    public BluetoothDevice bluetoothDevice;
    public BondedDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    @NonNull
    @SuppressLint("MissingPermission")
    @Override
    public String toString(){
        return bluetoothDevice.getName();
    }
}
