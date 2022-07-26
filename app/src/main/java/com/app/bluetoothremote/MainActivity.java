package com.app.bluetoothremote;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.companion.AssociationRequest;
import android.companion.BluetoothDeviceFilter;
import android.companion.CompanionDeviceManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BluetoothTest";
    private static final int REQUEST_CODE_BT_DEVICE_SELECTED = 1;
    static final int MESSAGE_FROM_SCAN_THREAD = 4;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    static Vibrator vibrator;
    private BluetoothLeScanner bluetoothLeScanner;
    private SwitchMaterial swtConnect;
    private TextView txtOut;
    private EditText txtInput;
    private Spinner cmbBondedDevices;
    protected static Handler handlerUi;
    private List<Button> buttons;
    private ActivityResultLauncher<Intent> launcherEnableBluetooth;

    private void createUIHandler() {
        if (handlerUi == null) {
            handlerUi = new Handler(getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == BluetoothHidService.WHAT.BLUETOOTH_DISCONNECTED) {
                        swtConnect.setChecked(false);
                        swtConnect.setEnabled(true);
                        setButtonsEnabled(false);
                        txtInput.setEnabled(false);
                    } else if (msg.what == BluetoothHidService.WHAT.BLUETOOTH_CONNECTING) {
                        swtConnect.setChecked(true);
                        swtConnect.setEnabled(false);
                    } else if (msg.what == BluetoothHidService.WHAT.BLUETOOTH_CONNECTED) {
                        swtConnect.setChecked(true);
                        swtConnect.setEnabled(true);
                        setButtonsEnabled(true);
                        txtInput.setEnabled(true);
                    }
                }
            };
        }
    }

    private void setButtonsEnabled(boolean enabled) {
        for (Button button : buttons) {
            button.setEnabled(enabled);
        }
    }

    private void startService() {
        Intent serviceIntent = new Intent(this, BluetoothHidService.class);
        createUIHandler();
        BluetoothHidService.bluetoothDevice = getSelectedBluetoothDevice();
        startForegroundService(serviceIntent);
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, BluetoothHidService.class);
        stopService(serviceIntent);
    }

    private void pairBtnAction(View v) {
        //                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1);

//                startBluetoothDiscovery();
//                connectGATT();
//                boolean bonded = bluetoothAdapter.getRemoteDevice("AC:ED:5C:63:FF:41").createBond();
//                debug("bonded=" + bonded);

        startBluetoothLEAdvertise();


//                for (ParcelUuid parcelUuid : getSelectedBluetoothDevice().getUuids()) {
//                    debug(parcelUuid.toString());
//                }

//                startBluetoothLEScan();

//                openGattServer();

//        companionPair();
    }

    private void connectSwitchAction(View v) {
//        debug("connectSwitchAction " + swtConnect.isChecked());
        if (swtConnect.isChecked()) {
            startService();
        } else {
            stopService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!bluetoothAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
            }
            launcherEnableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
        } else {
            populateBondedDevices();
        }
    }

    @Override
    @SuppressLint({"MissingPermission", "ClickableViewAccessibility"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        vibrator = getSystemService(Vibrator.class);
//        companionDeviceManager = getSystemService(CompanionDeviceManager.class);

        launcherEnableBluetooth = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResult -> {
//            debug("bluetooth enabled?" + activityResult.getResultCode());
            if (activityResult.getResultCode() == -1) { // enabled
                populateBondedDevices();
            } else {
                Toast.makeText(MainActivity.this, "Bluetooth not enabled, exiting now.", Toast.LENGTH_LONG).show();
                finish();
            }
        });

//        if (!bluetoothAdapter.isEnabled()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
//                    ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
//            }
//            launcherEnableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
//        } else {
//            populateBondedDevices();
//        }

        swtConnect = findViewById(R.id.swtConnect);
        swtConnect.setChecked(BluetoothHidService.isRunning);
        swtConnect.setOnClickListener(this::connectSwitchAction);

        txtOut = findViewById(R.id.txtOut);
//        txtOut.setMaxLines(40);

        txtInput = findViewById(R.id.txtInput);
        txtInput.setEnabled(false);

//        Button btnPair = findViewById(R.id.btnPair);
//        btnPair.setOnClickListener(this::pairBtnAction);

        assignButtonActions();

//        testNotification();
    }

    private void debug(String msg) {
        Log.e(TAG, "------------------------- " + msg);
        txtOut.setText(msg + "\n" + txtOut.getText());
//        txtOut.append("\n", 0, 1);
//        txtOut.append(msg, 0, msg.length());

//        if (txtOut.getLineCount() >= txtOut.getMaxLines()) {
//            txtOut.setText("");
//        }
//        txtOut.append(msg + "\n");

    }

    //    @SuppressLint("MissingPermission")
    private void populateBondedDevices() {
        cmbBondedDevices = findViewById(R.id.cmbBondedDevices);
        List<BondedDevice> spinnerArray = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices) {
                spinnerArray.add(new BondedDevice(device));
//                debug(device.getName() + " MajorDeviceClass=" + device.getBluetoothClass().getMajorDeviceClass());
//                debug(device.getName() + " DeviceClass=" + device.getBluetoothClass().getDeviceClass());
            }
        }
        ArrayAdapter<BondedDevice> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cmbBondedDevices.setAdapter(adapter);

        if (cmbBondedDevices.getCount() > getSharedPreferences().getInt("selectedBluetoothDevice", 0)) {
            cmbBondedDevices.setSelection(getSharedPreferences().getInt("selectedBluetoothDevice", 0));
        }
    }

    private BluetoothDevice getSelectedBluetoothDevice() {
        return ((BondedDevice) cmbBondedDevices.getSelectedItem()).bluetoothDevice;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cmbBondedDevices != null)
            getSharedPreferences().edit().putInt("selectedBluetoothDevice", cmbBondedDevices.getSelectedItemPosition()).apply();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cmbBondedDevices != null)
            getSharedPreferences().edit().putInt("selectedBluetoothDevice", cmbBondedDevices.getSelectedItemPosition()).apply();
    }

    private SharedPreferences getSharedPreferences() {
        return this.getPreferences(Context.MODE_PRIVATE);
    }

    //    @SuppressLint("MissingPermission")
    private void startBluetoothLEAdvertise() {
        AdvertiseCallback advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                debug("AdvertiseCallback onStartSuccess");
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                debug("AdvertiseCallback onStartFailure");
            }
        };
        BluetoothLeAdvertiser bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        AdvertiseSettings advertiseSettings = new AdvertiseSettings.Builder()
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .setTimeout(0)
//                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .build();


//        byte[] mfData=hexStringToByteArray("0201");

        AdvertiseData advertiseData = new AdvertiseData.Builder()
//                .setIncludeTxPowerLevel(true)
                .setIncludeDeviceName(true)
//                .addServiceUuid(Constants.DIS_UUID)
//                .addServiceUuid(Constants.HID_UUID)
                .addServiceUuid(Constants.HOGP_UUID)
//                .addServiceUuid(Constants.BAS_UUID)
                .build();

        AdvertiseData scanResult = new AdvertiseData.Builder()
//                .setIncludeTxPowerLevel(true)
                .setIncludeDeviceName(true)
//                .addServiceUuid(Constants.DIS_UUID)
//                .addServiceUuid(Constants.HID_UUID)
//                .addServiceUuid(Constants.HOGP_UUID)
//                .addServiceUuid(Constants.BAS_UUID)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, 1);
        }
        bluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, scanResult, advertiseCallback);
//        bluetoothLeAdvertiser.startAdvertising(advertiseSettings, advertiseData, advertiseCallback);
    }

    @SuppressLint("MissingPermission")
    private void startBluetoothLEScan() {
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        ScanCallback leScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
//                if (result.getDevice().getAddress().equals("41:67:08:9E:29:6F")) {
//
//                    ScanCallback scanCallbackStopped = new ScanCallback() {
//                        @Override
//                        public void onScanResult(int callbackType, ScanResult result) {
//                            super.onScanResult(callbackType, result);
//                            debug("scanCallbackStopped onScanResult");
//                        }
//                    };
//                    bluetoothLeScanner.stopScan(scanCallbackStopped);
//                    result.getDevice().createBond();
//                }
                debug("onScanResult " + result.getDevice().getAddress() + " " + result.getDevice().getName());
                ScanCallback scanCallbackStopped = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        debug("scanCallbackStopped onScanResult");
                    }
                };
                bluetoothLeScanner.stopScan(scanCallbackStopped);
                result.getDevice().createBond();
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                debug("onScanFailed " + errorCode);
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                debug("onBatchScanResults " + results);
            }
        };

        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter scanFilter = new ScanFilter.Builder()
//                .setDeviceName("DESKTOP-4NAPAOI")
                .setDeviceAddress("41:67:08:9E:29:6F")
//                .setServiceUuid(ParcelUuid.fromString("00001812-0000-1000-8000-00805F9B34FB"))
                .build();
        scanFilters.add(scanFilter);

        ScanSettings scanSettings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();

//        bluetoothLeScanner.startScan(leScanCallback);
//        bluetoothLeScanner.startScan(scanFilters, scanSettings, leScanCallback);
        bluetoothLeScanner.startScan(null, scanSettings, leScanCallback);

    }

    @SuppressLint("MissingPermission")
    private void startBluetoothDiscovery() {
//        this.registerReceiver(bluetoothScanReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(getBroadcastReceiver(), intentFilter);
        boolean discoveryStarted = bluetoothAdapter.startDiscovery();
        debug("discoveryStarted=" + discoveryStarted);
    }

    @SuppressLint("MissingPermission")
    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            @SuppressLint("MissingPermission")
            public void onReceive(Context context, Intent intent) {
                debug("onReceive " + intent.getAction());
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    debug(deviceName + ", " + deviceHardwareAddress);

                    if (deviceName.equals("DESKTOP-4NAPAOI")) {
                        device.createBond();
                    }
                }
                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        debug("Bonding completed with " + deviceName + ", " + deviceHardwareAddress);
                        populateBondedDevices();
//                        bluetoothAdapter.getProfileProxy(MainActivity.this, MainActivity.this, BluetoothProfile.HID_DEVICE);
                    }
                }
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void connectGATT() {

        BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    debug("successfully connected to the GATT Server");
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    debug("disconnected from the GATT Server");
                }
            }
        };
        getSelectedBluetoothDevice().connectGatt(this, false, bluetoothGattCallback);

    }

    @SuppressLint("MissingPermission")
    private void openGattServer() {
        BluetoothGattServerCallback gattServerCallback = new BluetoothGattServerCallback() {
            @Override
            public void onServiceAdded(int status, BluetoothGattService service) {
                super.onServiceAdded(status, service);
                debug("onServiceAdded");
            }

            @Override
            public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
                super.onConnectionStateChange(device, status, newState);
                debug("onConnectionStateChange " + device.getName());
            }
        };

        bluetoothManager.openGattServer(this, gattServerCallback);
    }

    @SuppressLint("MissingPermission")
    private void companionPair() {
        CompanionDeviceManager companionDeviceManager = getSystemService(CompanionDeviceManager.class);

        BluetoothDeviceFilter bluetoothDeviceFilter = new BluetoothDeviceFilter.Builder()
//                .setNamePattern(Pattern.compile("My device"))
//                .addServiceUuid(new ParcelUuid(new UUID(0x123abcL, -1L)), null)
//                .addServiceUuid(ParcelUuid.fromString("0000110a-0000-1000-8000-00805f9b34fb"), null)
                .build();

        AssociationRequest pairingRequest = new AssociationRequest.Builder()
                .addDeviceFilter(bluetoothDeviceFilter)
//                .setSingleDevice(true)
                .build();

        CompanionDeviceManager.Callback callback = new CompanionDeviceManager.Callback() {
            @Override
            public void onDeviceFound(IntentSender chooserLauncher) {
                debug("onDeviceFound");
                try {
                    startIntentSenderForResult(chooserLauncher, REQUEST_CODE_BT_DEVICE_SELECTED, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    debug("onDeviceFound SendIntentException");
                }
            }

            @Override
            public void onFailure(CharSequence error) {
                debug("onFailure");
            }
        };
        companionDeviceManager.associate(pairingRequest, callback, null);


    }

//    @SuppressLint("MissingPermission")
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        debug("onActivityResult requestCode" + requestCode + " resultCode=" + resultCode);
//        if (requestCode == REQUEST_CODE_BT_DEVICE_SELECTED) {
//            if (resultCode == Activity.RESULT_OK && data != null) {
//                BluetoothDevice deviceToPair = data.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE);
//                if (deviceToPair != null) {
//
//                    IntentFilter intentFilter = new IntentFilter();
//                    intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//                    registerReceiver(getBroadcastReceiver(), intentFilter);
//
//                    debug("will now pair with " + deviceToPair.getName());
//                    deviceToPair.createBond();
//                }
//            }
//
//        }
//        if (requestCode == REQUEST_CODE_NOTIFICATION_TEST) {
//            debug("NOTIFICATION!");
//        }
//    }

    @SuppressLint("WrongConstant")
    private void assignButtonActions() {
        Button btnPower = findViewById(R.id.btnPower);
        Button btnMenu = findViewById(R.id.btnMenu);

        Button btnPair = findViewById(R.id.btnPair);

        btnPair.setOnClickListener(this::pairBtnAction);

        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        Button btnUp = findViewById(R.id.btnUp);
        Button btnDown = findViewById(R.id.btnDown);
        Button btnMiddle = findViewById(R.id.btnMiddle);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnHome = findViewById(R.id.btnHome);
        Button btnVolInc = findViewById(R.id.btnVolInc);
        Button btnVolDec = findViewById(R.id.btnVolDec);
        Button btnMute = findViewById(R.id.btnMute);
        Button btnPlayPause = findViewById(R.id.btnPlayPause);

        Button btnRewind = findViewById(R.id.btnRewind);
        Button btnForward = findViewById(R.id.btnForward);


        buttons = new ArrayList<>();
        buttons.add(btnLeft);
        buttons.add(btnRight);
        buttons.add(btnUp);
        buttons.add(btnDown);
        buttons.add(btnMiddle);
        buttons.add(btnHome);
        buttons.add(btnBack);
        buttons.add(btnVolDec);
        buttons.add(btnVolInc);
        buttons.add(btnPlayPause);
        buttons.add(btnPower);
        buttons.add(btnMenu);
        buttons.add(btnMute);
        buttons.add(btnRewind);
        buttons.add(btnForward);
//        buttons.add(btnSource);

        setButtonsEnabled(BluetoothHidService.isRunning);

        addRemoteKeyListeners(btnPower, RemoteControlHelper.Key.POWER);

//        addRemoteKeysListeners(btnSource, RemoteControlHelper.Key.ASSIGN_SELECTION, RemoteControlHelper.Key.MEDIA_SELECT_CD);
//        addRemoteKeyListeners(btnPair, RemoteControlHelper.Key.MEDIA_SELECT_CD);

//        addKeyBoardListeners(btnSource, 0x91);

        addRemoteKeyListeners(btnMenu, RemoteControlHelper.Key.MENU);

        addRemoteKeyListeners(btnLeft, RemoteControlHelper.Key.MENU_LEFT);
        addRemoteKeyListeners(btnRight, RemoteControlHelper.Key.MENU_RIGHT);
        addRemoteKeyListeners(btnUp, RemoteControlHelper.Key.MENU_UP);
        addRemoteKeyListeners(btnDown, RemoteControlHelper.Key.MENU_DOWN);
        addRemoteKeyListeners(btnMiddle, RemoteControlHelper.Key.MENU_PICK);

        addRemoteKeyListeners(btnBack, RemoteControlHelper.Key.BACK);
        addRemoteKeyListeners(btnHome, RemoteControlHelper.Key.HOME);

        addRemoteKeyListeners(btnVolInc, RemoteControlHelper.Key.VOLUME_INC);
        addRemoteKeyListeners(btnVolDec, RemoteControlHelper.Key.VOLUME_DEC);

        addRemoteKeyListeners(btnMute, RemoteControlHelper.Key.MUTE);

        addRemoteKeyListeners(btnPlayPause, RemoteControlHelper.Key.PLAY_PAUSE);
        addRemoteKeyListeners(btnRewind, RemoteControlHelper.Key.MEDIA_REWIND);
        addRemoteKeyListeners(btnForward, RemoteControlHelper.Key.MEDIA_FAST_FORWARD);


        txtInput.setOnKeyListener(this::handleInputText);
//        txtInput.setOnKeyListener(this::handleRealtimeInputText);
//        txtInput.addTextChangedListener(getKeyTextWatcher());

    }

    /*
    Used only to handle backspace and enter keys
     */
    private boolean handleInputText(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
//            debug("onKey=" + txtInput.getText().toString());

            txtInput.getText().chars().forEach(c -> {
//                debug((char) c + "");
                if (KeyboardHelper.keyMap.containsKey((char) c)) {// Small case letter
                    KeyboardHelper.sendKeyDown(KeyboardHelper.Modifier.NONE, KeyboardHelper.getKey((char) c));
                    KeyboardHelper.sendKeyUp();
                } else if (KeyboardHelper.shiftKeyMap.containsKey((char) c)) {// Upper case letter
                    KeyboardHelper.sendKeyDown(KeyboardHelper.Modifier.KEY_MOD_LSHIFT, KeyboardHelper.getShiftKey((char) c));
                    KeyboardHelper.sendKeyUp();
                }
            });
//            boolean sent = KeyboardHelper.sendKeyDown(KeyboardHelper.Modifier.NONE, KeyboardHelper.Key.ENTER);
//            if (sent)
            vibrate();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
//            debug("onKey= BACKSPACE");
            boolean sent = KeyboardHelper.sendKeyDown(KeyboardHelper.Modifier.NONE, KeyboardHelper.Key.BACKSPACE);
            KeyboardHelper.sendKeyUp();
            if (sent)
                vibrate();
            return true;
        }
        return false;
    }

    private TextWatcher getKeyTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                debug("beforeTextChanged " + s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                debug("onTextChanged start=" + start + " before=" + before + " count=" + count);
                //This is a backspace
                boolean sent = false;
                if (before > count) {
//                        debug("BACKSPACE");
                    sent = KeyboardHelper.sendKeyDown(KeyboardHelper.Modifier.NONE, KeyboardHelper.Key.BACKSPACE);
                    KeyboardHelper.sendKeyUp();
                } else if (start + count > 0) {
                    char c = s.charAt(s.length() - 1);
//                        debug("onTextChanged " + s);
                    if (KeyboardHelper.keyMap.containsKey(c)) {// Small case letter
                        sent = KeyboardHelper.sendKeyDown(KeyboardHelper.Modifier.NONE, KeyboardHelper.getKey(c));
                        KeyboardHelper.sendKeyUp();
                    } else if (KeyboardHelper.shiftKeyMap.containsKey(c)) {// Upper case letter
                        sent = KeyboardHelper.sendKeyDown(KeyboardHelper.Modifier.KEY_MOD_LSHIFT, KeyboardHelper.getShiftKey(c));
                        KeyboardHelper.sendKeyUp();
                    }
                }
                if (sent)
                    vibrate();
            }

            @Override
            public void afterTextChanged(Editable s) {
//                debug("afterTextChanged " + s.toString());
            }
        };
    }

    /*
    Used only to handle backspace and enter keys
     */
    private boolean handleRealtimeInputText(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
//            debug("onKey=" + txtInput.getText().toString());
            boolean sent = KeyboardHelper.sendKeyDown(KeyboardHelper.Modifier.NONE, KeyboardHelper.Key.ENTER);
            KeyboardHelper.sendKeyUp();
            if (sent)
                vibrate();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
//            debug("onKey= BACKSPACE");
            boolean sent = KeyboardHelper.sendKeyDown(KeyboardHelper.Modifier.NONE, KeyboardHelper.Key.BACKSPACE);
            KeyboardHelper.sendKeyUp();
            if (sent)
                vibrate();
            return true;
        }
        return false;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void addKeyBoardListeners(Button button, int... keys) {

        int modifier;
        int key;
        if (keys.length > 1) {
            modifier = keys[0];
            key = keys[1];
        } else {
            modifier = 0;
            key = keys[0];
        }

        button.setOnTouchListener((view, motionEvent) -> {
//            debug("onTouch " + motionEvent.getAction());
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                boolean sent = KeyboardHelper.sendKeyDown(modifier, key);
                if (sent)
                    vibrate();
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                boolean sent = KeyboardHelper.sendKeyUp();
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addRemoteKeyListeners(Button button, byte... keys) {
        button.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                boolean sent = RemoteControlHelper.sendKeyDown(keys[0], keys[1]);
                if (sent)
                    vibrate();
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                boolean sent = RemoteControlHelper.sendKeyUp();
            }
            return false;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addRemoteKeysListeners(Button button, byte[] key1, byte[] key2) {
        button.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                boolean sent = RemoteControlHelper.sendKeyDown(key1[0], key1[1]);
                sent = RemoteControlHelper.sendKeyUp();

                sent = RemoteControlHelper.sendKeyDown(key2[0], key2[1]);
                sent = RemoteControlHelper.sendKeyUp();
                if (sent)
                    vibrate();
            }
            return false;
        });
    }

    static void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK));
        } else {
            vibrator.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private void testNotification() {

        Intent intent = new Intent(this, NotificationBroadcastReceiver.class);
        intent.setAction("Play/Pause");

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        RemoteViews remoteViews=new RemoteViews(getPackageName(), R.layout.notification_buttons);
        remoteViews.setOnClickPendingIntent(R.id.btnPower,pi);

        String CHANNEL_ID = "Bluetooth Remote Service";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Bluetooth Remote Service", NotificationManager.IMPORTANCE_MIN);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);
        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Bluetooth Remote")
                        .setContentText("Test")
                        .setSmallIcon(R.drawable.remote_control)
                        .setContentIntent(pendingIntent)
                        .setOngoing(true)
//                        .setCustomContentView(new RemoteViews(getPackageName(), R.layout.test))
                        .setCustomBigContentView(remoteViews)
                        .build();

        getSystemService(NotificationManager.class).notify(1, notification);
    }
}