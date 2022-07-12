package com.app.bluetoothremote;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.Nullable;

public class BluetoothHidService extends Service implements BluetoothProfile.ServiceListener {
    private static final String TAG = "BluetoothHIDService";

    public @interface WHAT {
        int BLUETOOTH_DISCONNECTED = 3;
        int BLUETOOTH_CONNECTED = 2;
        int BLUETOOTH_CONNECTING = 1;
    }

    static BluetoothHidDevice bluetoothHidDevice;
    static BluetoothDevice bluetoothDevice;
    static boolean isRunning = false;
    static boolean isHidDeviceConnected = false;

    private BluetoothAdapter bluetoothAdapter;

    private BluetoothHidDevice.Callback callback;

    private Messenger messenger;

    private void debug(String msg) {
        Log.e(TAG, "------------------------- " + msg);
    }

    public BluetoothHidService() {
    }

    private void init() {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothAdapter.getProfileProxy(this, this, BluetoothProfile.HID_DEVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        debug("onBind");
        Binder binder = new Binder();
        init();
        return binder;
    }

    @Nullable
    @Override
    public ComponentName startForegroundService(Intent service) {
        debug("startForegroundService");
        return super.startForegroundService(service);
    }

    @Override
    public void onCreate() {
        init();
        super.onCreate();
        debug("onCreate");
    }

    @SuppressLint("MissingPermission")
    private void startAsForeground() {
        String CHANNEL_ID = "Bluetooth Remote Service";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Bluetooth Remote Service", NotificationManager.IMPORTANCE_MIN);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);
        Notification notification =
                new Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("Bluetooth Remote")
                        .setContentText("Bluetooth Remote is connected to " + bluetoothDevice.getName())
                        .setSmallIcon(R.drawable.remote_control)
                        .setContentIntent(pendingIntent)
                        .build();
        startForeground(1, notification);
        isRunning = true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        debug("onDestroy");
        releaseBluetooth();
    }

    @SuppressLint("MissingPermission")
    private void releaseBluetooth() {
        bluetoothHidDevice.unregisterApp();
        bluetoothAdapter.closeProfileProxy(BluetoothProfile.HID_DEVICE, bluetoothHidDevice);
        isRunning = false;

        //Send notification to activity
        sendMessage(WHAT.BLUETOOTH_DISCONNECTED);
    }

    private void sendMessage(@WHAT int what) {
        Message message = new Message();
        message.setTarget(MainActivity.handlerUi);
        message.what = what;
        message.sendToTarget();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        debug("onStartCommand");

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onServiceConnected(int profile, BluetoothProfile proxy) {
        if (profile == BluetoothProfile.HID_DEVICE) {
            bluetoothHidDevice = (BluetoothHidDevice) proxy;
            debug("onServiceConnected profile == BluetoothProfile.HID_DEVICE");

            callback = new BluetoothHidDevice.Callback() {
                @Override
                public void onAppStatusChanged(BluetoothDevice pluggedDevice, boolean registered) {
                    super.onAppStatusChanged(pluggedDevice, registered);
                    debug("onAppStatusChanged registered=" + registered);

                    boolean deviceConnected = bluetoothHidDevice.connect(bluetoothDevice);
                    if (deviceConnected) {
                        debug("Connected to " + bluetoothDevice.getName());
                    }
                }

                @Override
                public void onGetReport(BluetoothDevice device, byte type, byte id, int bufferSize) {
                    super.onGetReport(device, type, id, bufferSize);
                    debug("onGetReport");
                }

                @Override
                public void onSetReport(BluetoothDevice device, byte type, byte id, byte[] data) {
                    super.onSetReport(device, type, id, data);
                    debug("onSetReport");
                }

                @Override
                public void onConnectionStateChanged(BluetoothDevice device, int state) {
                    String stateStr = "";
                    switch (state) {
                        case BluetoothHidDevice.STATE_CONNECTED:
                            stateStr = "STATE_CONNECTED";
                            isHidDeviceConnected=true;
                            sendMessage(WHAT.BLUETOOTH_CONNECTED);
                            break;
                        case BluetoothHidDevice.STATE_DISCONNECTED:
                            stateStr = "STATE_DISCONNECTED";
                            isHidDeviceConnected=false;
                            sendMessage(WHAT.BLUETOOTH_DISCONNECTED);
                            BluetoothHidService.this.stopSelf();
                            break;
                        case BluetoothHidDevice.STATE_CONNECTING:
                            stateStr = "STATE_CONNECTING";
                            sendMessage(WHAT.BLUETOOTH_CONNECTING);
                            startAsForeground();
                            break;
                        case BluetoothHidDevice.STATE_DISCONNECTING:
                            stateStr = "STATE_DISCONNECTING";
                            break;
                    }
                    boolean isProfileSupported = HidUtils.isProfileSupported(device);
                    debug("isProfileSupported " + isProfileSupported);
                    debug("HID " + device.getName() + " " + device.getAddress() + " " + stateStr);
                }

                @Override
                public void onSetProtocol(BluetoothDevice device, byte protocol) {
                    super.onSetProtocol(device, protocol);
                    debug("onSetProtocol");
                }
            };

//            bluetoothHidDevice.registerApp(Constants.SDP_RECORD, null, Constants.QOS_OUT, Runnable::run, callback);
            bluetoothHidDevice.registerApp(Constants.SDP_RECORD, null, null, Runnable::run, callback);

        }
    }

    @Override
    public void onServiceDisconnected(int profile) {
        if (profile == BluetoothProfile.HID_DEVICE) {
//            bluetoothHidDevice = null;
            debug("HID onServiceDisconnected");
        }
    }

}