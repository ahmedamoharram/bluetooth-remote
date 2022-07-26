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
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BluetoothHidService extends Service implements BluetoothProfile.ServiceListener {
    private static final String TAG = "BluetoothHIDService";

    static final String ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE";
    static final String ACTION_VOL_INC = "ACTION_VOL_INC";
    static final String ACTION_VOL_DEC = "ACTION_VOL_DEC";
    static final String ACTION_MUTE = "ACTION_MUTE";
    static final String ACTION_POWER = "ACTION_POWER";

    static final String ACTION_REWIND = "ACTION_REWIND";
    static final String ACTION_FORWARD = "ACTION_FORWARD";
    static final String ACTION_UP = "ACTION_UP";
    static final String ACTION_DOWN = "ACTION_DOWN";
    static final String ACTION_LEFT = "ACTION_LEFT";
    static final String ACTION_RIGHT = "ACTION_RIGHT";
    static final String ACTION_MIDDLE = "ACTION_MIDDLE";
    static final String ACTION_MENU = "ACTION_MENU";
    static final String ACTION_HOME = "ACTION_HOME";
    static final String ACTION_BACK = "ACTION_BACK";

    public @interface WHAT {
        int BLUETOOTH_CONNECTING = 1;
        int BLUETOOTH_CONNECTED = 2;
        int BLUETOOTH_DISCONNECTED = 3;
    }

    static BluetoothHidDevice bluetoothHidDevice;
    static BluetoothDevice bluetoothDevice;
    static boolean isRunning = false;
    static boolean isHidDeviceConnected = false;

    private BluetoothAdapter bluetoothAdapter;

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
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);
        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Bluetooth Remote")
                        .setContentText("Bluetooth Remote is connected to " + bluetoothDevice.getName())
                        .setSmallIcon(R.drawable.remote_control)
                        .setContentIntent(pendingIntent)
                        .setCustomBigContentView(getNotificationButtonsRemoteViews())
                        .build();
        startForeground(1, notification);
        isRunning = true;
    }

    private RemoteViews getNotificationButtonsRemoteViews() {
        Intent powerIntent = new Intent(this, NotificationBroadcastReceiver.class);
        powerIntent.setAction(ACTION_POWER);
        PendingIntent powerPI = PendingIntent.getBroadcast(this, 0, powerIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent muteIntent = new Intent(this, NotificationBroadcastReceiver.class);
        muteIntent.setAction(ACTION_MUTE);
        PendingIntent mutePI = PendingIntent.getBroadcast(this, 0, muteIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent upIntent = new Intent(this, NotificationBroadcastReceiver.class);
        upIntent.setAction(ACTION_UP);
        PendingIntent upPI = PendingIntent.getBroadcast(this, 0, upIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent menuIntent = new Intent(this, NotificationBroadcastReceiver.class);
        menuIntent.setAction(ACTION_MENU);
        PendingIntent menuPI = PendingIntent.getBroadcast(this, 0, menuIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent homeIntent = new Intent(this, NotificationBroadcastReceiver.class);
        homeIntent.setAction(ACTION_HOME);
        PendingIntent homePI = PendingIntent.getBroadcast(this, 0, homeIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent leftIntent = new Intent(this, NotificationBroadcastReceiver.class);
        leftIntent.setAction(ACTION_LEFT);
        PendingIntent leftPI = PendingIntent.getBroadcast(this, 0, leftIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent middleIntent = new Intent(this, NotificationBroadcastReceiver.class);
        middleIntent.setAction(ACTION_MIDDLE);
        PendingIntent middlePI = PendingIntent.getBroadcast(this, 0, middleIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent rightIntent = new Intent(this, NotificationBroadcastReceiver.class);
        rightIntent.setAction(ACTION_RIGHT);
        PendingIntent rightPI = PendingIntent.getBroadcast(this, 0, rightIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent backIntent = new Intent(this, NotificationBroadcastReceiver.class);
        backIntent.setAction(ACTION_BACK);
        PendingIntent backPI = PendingIntent.getBroadcast(this, 0, backIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent downIntent = new Intent(this, NotificationBroadcastReceiver.class);
        downIntent.setAction(ACTION_DOWN);
        PendingIntent downPI = PendingIntent.getBroadcast(this, 0, downIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent rewindIntent = new Intent(this, NotificationBroadcastReceiver.class);
        rewindIntent.setAction(ACTION_REWIND);
        PendingIntent rewindPI = PendingIntent.getBroadcast(this, 0, rewindIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent forwardIntent = new Intent(this, NotificationBroadcastReceiver.class);
        forwardIntent.setAction(ACTION_FORWARD);
        PendingIntent forwardPI = PendingIntent.getBroadcast(this, 0, forwardIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent playPauseIntent = new Intent(this, NotificationBroadcastReceiver.class);
        playPauseIntent.setAction(ACTION_PLAY_PAUSE);
        PendingIntent playPausePI = PendingIntent.getBroadcast(this, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent volIncIntent = new Intent(this, NotificationBroadcastReceiver.class);
        volIncIntent.setAction(ACTION_VOL_INC);
        PendingIntent volIncPI = PendingIntent.getBroadcast(this, 0, volIncIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent volDecIntent = new Intent(this, NotificationBroadcastReceiver.class);
        volDecIntent.setAction(ACTION_VOL_DEC);
        PendingIntent volDecPI = PendingIntent.getBroadcast(this, 0, volDecIntent, PendingIntent.FLAG_IMMUTABLE);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_buttons);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnPower, powerPI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnMute, mutePI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnUp, upPI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnMenu, menuPI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnHome, homePI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnLeft, leftPI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnMiddle, middlePI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnRight, rightPI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnBack, backPI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnDown, downPI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnVolInc, volIncPI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnRewind, rewindPI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnForward, forwardPI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnPlayPause, playPausePI);
        remoteViews.setOnClickPendingIntent(R.id.ntfBtnVolDec, volDecPI);

        return remoteViews;
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

            BluetoothHidDevice.Callback callback = new BluetoothHidDevice.Callback() {
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
                            isHidDeviceConnected = true;
                            sendMessage(WHAT.BLUETOOTH_CONNECTED);
                            break;
                        case BluetoothHidDevice.STATE_DISCONNECTED:
                            stateStr = "STATE_DISCONNECTED";
                            isHidDeviceConnected = false;
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