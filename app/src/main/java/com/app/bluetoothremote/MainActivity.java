package com.app.bluetoothremote;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.app.bluetoothremote.databinding.ActivityMainBinding;
import com.app.bluetoothremote.databinding.PopupBluetoothDiscoveryBinding;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "BluetoothTest";
    private static final int REQUEST_CODE_BLUETOOTH_CONNECT = 1;
    private static final int REQUEST_CODE_BLUETOOTH_SCAN = 2;
    private static final int REQUEST_CODE_FINE_LOCATION_ACCESS = 3;
    private static final int REQUEST_CODE_POST_NOTIFICATION = 4;
    static final int MESSAGE_FROM_SCAN_THREAD = 4;
    private BluetoothAdapter bluetoothAdapter;
    static Vibrator vibrator;
    private BluetoothLeScanner bluetoothLeScanner;
    private SwitchMaterial swtConnect;
    private SwitchMaterial swtConnectMouse;
    private TextView txtOut;
    private EditText txtInput;
    private Spinner cmbBondedDevices;
    protected static Handler handlerUi;
    private List<Button> buttons;
    private ActivityResultLauncher<Intent> launcherEnableBluetooth;
    private SensorManager sensorManager;
    private Sensor sensorGyroscope;
    private Button btnCurLeft;
    private Button btnCurClick;
    private Button btnCurRight;
    private SeekBar seekBar;
    private TextView seekBarLabel;
    private TableRow rowCursor;

    private static boolean isNotificationRefused = false;

    private LinearLayout layoutBluetoothDiscover;
    private PopupWindow popupWindow;

    private ActivityMainBinding activityMainBinding;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem itemConnect = menu.findItem(R.id.item_connect);
        swtConnect = Objects.requireNonNull(itemConnect.getActionView()).findViewById(R.id.swtConnect);
        swtConnect.setOnClickListener(this::connectSwitchAction);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        if (item.getItemId() == R.id.item_discover) {
            showBluetoothDiscoveryPopup();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressLint({"MissingPermission", "ClickableViewAccessibility"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        View decorView = getWindow().getDecorView();
//        decorView.setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Optional: For hiding navigation bar
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Important: Enables layout behind status bar
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Optional: Hides navigation bar (use with caution)
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN // Optional: Hides status bar (use with caution)
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Optional: For immersive mode (hides bars until user interacts)
//        );

        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);
        //Hide the Action bar
//        Objects.requireNonNull(this.getSupportActionBar()).hide();

        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
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

        btnCurLeft = activityMainBinding.curLeft;
        btnCurClick = activityMainBinding.curMiddle;
        btnCurRight = activityMainBinding.curRight;
        rowCursor = activityMainBinding.curRow;
        swtConnectMouse = activityMainBinding.swtConnectMouse;
        swtConnectMouse.setOnClickListener(this::connectMouseAction);

        txtOut = activityMainBinding.txtOut;
//        txtOut.setMaxLines(40);

        txtInput = activityMainBinding.txtInput;
        txtInput.setEnabled(false);

        Button btnDiscover = activityMainBinding.btnDiscover;
        btnDiscover.setOnClickListener(this::btnDiscoverAction);

        seekBar = activityMainBinding.seekBar;
        seekBar.setMin(1);
        seekBar.setMax(60);
        seekBar.setProgress(30);
        seekBarLabel = activityMainBinding.seekBarLabel;

        assignButtonActions();

//        testNotification();
    }

    private void createUIHandler() {
        if (handlerUi == null) {
            handlerUi = new Handler(getMainLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case BluetoothHidService.STATUS.BLUETOOTH_DISCONNECTED -> {
                            swtConnect.setChecked(false);
                            swtConnect.setEnabled(true);
                            setButtonsEnabled(false);
                            txtInput.setEnabled(false);
                            swtConnect.setText(R.string.connect);
                        }
                        case BluetoothHidService.STATUS.BLUETOOTH_CONNECTING -> {
                            swtConnect.setChecked(true);
                            swtConnect.setEnabled(true);
                            swtConnect.setText(R.string.connecting);
                        }
                        case BluetoothHidService.STATUS.BLUETOOTH_CONNECTED -> {
                            swtConnect.setChecked(true);
                            swtConnect.setEnabled(true);
                            setButtonsEnabled(true);
                            txtInput.setEnabled(true);
                            swtConnect.setText(R.string.connected);
                        }
                    }
                }
            };
        }
    }

    private void connectMouseAction(View v) {
        if (swtConnectMouse.isChecked()) {
            startGyroscope();
        } else {
            stopGyroscope();
        }
    }

    private void startGyroscope() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (sensorGyroscope != null) {
            sensorManager.registerListener(this, sensorGyroscope, 10000);
            rowCursor.setVisibility(View.VISIBLE);
//            btnCurLeft.setVisibility(View.VISIBLE);
//            btnCurRight.setVisibility(View.VISIBLE);
            seekBar.setVisibility(View.VISIBLE);
            seekBarLabel.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "No Gyroscope sensor!", Toast.LENGTH_LONG).show();
            swtConnectMouse.setChecked(false);
        }
    }

    private void stopGyroscope() {
        if (sensorManager != null) sensorManager.unregisterListener(this);
        rowCursor.setVisibility(View.GONE);
        seekBar.setVisibility(View.GONE);
        seekBarLabel.setVisibility(View.GONE);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (BluetoothHidService.isHidDeviceConnected && sensorGyroscope != null) {
            MouseHelper.sendData(false, false, false, Math.round(sensorEvent.values[2] * seekBar.getProgress()) * -1, Math.round(sensorEvent.values[0] * seekBar.getProgress()) * -1, 0);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private void setButtonsEnabled(boolean enabled) {
        for (Button button : buttons) {
            button.setEnabled(enabled);
        }
    }

    private void startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED &&
                !isNotificationRefused) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE_POST_NOTIFICATION);
        } else {
            Intent serviceIntent = new Intent(this, BluetoothHidService.class);
            createUIHandler();
            BluetoothHidService.bluetoothDevice = getSelectedBluetoothDevice();
            if (BluetoothHidService.bluetoothDevice != null) {
                startForegroundService(serviceIntent);
            } else {
                Toast.makeText(MainActivity.this, "Device not supported!", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void startService(BluetoothDevice bluetoothDevice) {
        Intent serviceIntent = new Intent(this, BluetoothHidService.class);
        createUIHandler();
        BluetoothHidService.bluetoothDevice = bluetoothDevice;
        try {
            startForegroundService(serviceIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Bluetooth did not start correctly, try again a few seconds.", Toast.LENGTH_LONG).show();
        }
    }

    private void stopService() {
        Intent serviceIntent = new Intent(this, BluetoothHidService.class);
        stopService(serviceIntent);
    }

    private void btnDiscoverAction(View v) {
        showBluetoothDiscoveryPopup();
    }

    private void connectSwitchAction(View v) {
//        debug("connectSwitchAction " + swtConnect.isChecked());
        if (swtConnect.isChecked()) {
            startService();
        } else {
            stopService();
            swtConnectMouse.setChecked(false);
            stopGyroscope();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (swtConnect != null) swtConnect.setChecked(BluetoothHidService.isRunning);

        if (!bluetoothAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CODE_BLUETOOTH_CONNECT);
            } else {
                launcherEnableBluetooth.launch(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
            }
        } else {
            populateBondedDevices();
        }

        if (swtConnectMouse.isChecked()) {
            startGyroscope();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cmbBondedDevices != null)
            getSharedPreferences().edit().putInt("selectedBluetoothDevice", cmbBondedDevices.getSelectedItemPosition()).apply();
        stopGyroscope();
    }

    private void debug(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "------------------------- " + msg);
            txtOut.setText(String.format("%s%n%s", msg, txtOut.getText()));
//        txtOut.append("\n", 0, 1);
//        txtOut.append(msg, 0, msg.length());

//        if (txtOut.getLineCount() >= txtOut.getMaxLines()) {
//            txtOut.setText("");
//        }
//        txtOut.append(msg + "\n");
        }
    }

    private void populateBondedDevices() {
        cmbBondedDevices = findViewById(R.id.cmbBondedDevices);
        List<BondedDevice> spinnerArray = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CODE_BLUETOOTH_CONNECT);
        } else {
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            if (!bondedDevices.isEmpty()) {
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


    }

    private BluetoothDevice getSelectedBluetoothDevice() {
        if (cmbBondedDevices.getSelectedItem() instanceof BondedDevice) {
            return ((BondedDevice) cmbBondedDevices.getSelectedItem()).bluetoothDevice;
        } else {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cmbBondedDevices != null)
            getSharedPreferences().edit().putInt("selectedBluetoothDevice", cmbBondedDevices.getSelectedItemPosition()).apply();
    }

    private SharedPreferences getSharedPreferences() {
        return this.getPreferences(Context.MODE_PRIVATE);
    }


    private void showBluetoothDiscoveryPopup() {

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_FINE_LOCATION_ACCESS);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_CODE_BLUETOOTH_SCAN);
            } else {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                PopupBluetoothDiscoveryBinding popupBinding = PopupBluetoothDiscoveryBinding.inflate(inflater);
                layoutBluetoothDiscover = popupBinding.getRoot();

                popupWindow = new PopupWindow(this);
                popupWindow.setContentView(popupBinding.getRoot());

                Button btnCancelDiscover = popupBinding.btnCancelDiscover;
                btnCancelDiscover.setOnClickListener(view -> {
                    popupWindow.dismiss();
                    stopBluetoothDiscovery();
                });

                // Show the popup window
                popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
                startBluetoothDiscovery();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_FINE_LOCATION_ACCESS -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showBluetoothDiscoveryPopup();
                } else {
                    Toast.makeText(MainActivity.this, "Fine location access not granted", Toast.LENGTH_LONG).show();
                }
            }
            case REQUEST_CODE_BLUETOOTH_SCAN -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showBluetoothDiscoveryPopup();
                } else {
                    Toast.makeText(MainActivity.this, "Bluetooth Scan not granted", Toast.LENGTH_LONG).show();
                }
            }

            case REQUEST_CODE_POST_NOTIFICATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startService();
                } else {
                    isNotificationRefused = true;
                    Toast.makeText(MainActivity.this, "No notification buttons will be displayed!", Toast.LENGTH_LONG).show();
                    startService();
                }
            }
        }
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

    private boolean stopBluetoothDiscovery() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return false;
        }
        return bluetoothAdapter.cancelDiscovery();
    }

    private BroadcastReceiver getBroadcastReceiver() {
        return new BroadcastReceiver() {
            @Override
            @SuppressLint("MissingPermission")
            public void onReceive(Context context, Intent intent) {
                debug("onReceive " + intent.getAction());
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    if (device != null && device.getName() != null && !device.getName().trim().isEmpty()) {
                    if (device != null) {
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        debug(deviceName + ", " + deviceHardwareAddress);
                        createBluetoothDiscoveredItem(deviceName, device);
                    }

                }
//                if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
//                    if (bondState == BluetoothDevice.BOND_BONDED && device != null) {
//                        String deviceName = device.getName();
//                        String deviceHardwareAddress = device.getAddress(); // MAC address
//                        debug("Bonding completed with " + deviceName + ", " + deviceHardwareAddress);
//                        //TODO: Connect device right away
////                        debug("Starting Service with device " + deviceName);
////                        populateBondedDevices();
////                        cmbBondedDevices.setSelection(0);
////                        startService(device);
////                        bluetoothAdapter.getProfileProxy(MainActivity.this, MainActivity.this, BluetoothProfile.HID_DEVICE);
//                    }
//                }
            }
        };
    }

    private void createBluetoothDiscoveredItem(String deviceName, BluetoothDevice bluetoothDevice) {
        final int padding = 40;
        TextView textView = new TextView(this);
        textView.setPadding(padding, padding, padding, padding);
        textView.setText(String.format("%s: %s", deviceName, bluetoothDevice.getAddress()));
//        textView.setOnClickListener(view -> this.pairDevice(bluetoothDevice));
        textView.setOnClickListener(view -> {
            popupWindow.dismiss();
            stopBluetoothDiscovery();
            startService(bluetoothDevice);
        });
        layoutBluetoothDiscover.addView(textView, 1);
    }

    private void pairDevice(BluetoothDevice device) {
        debug("pairing device " + device);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            Toast.makeText(MainActivity.this, "Bluetooth connect permission not granted, please grant it.", Toast.LENGTH_LONG).show();
            return;
        }
        if (stopBluetoothDiscovery()) {
            debug("bonding now...");
            popupWindow.dismiss();
            device.createBond();
        } else {
            debug("cannot stop discovery!");
        }

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
        if (getSelectedBluetoothDevice() != null) {
            getSelectedBluetoothDevice().connectGatt(this, false, bluetoothGattCallback);
        }
    }

    @SuppressLint({"WrongConstant", "ClickableViewAccessibility"})
    private void assignButtonActions() {

        Button btnPower = activityMainBinding.btnPower;
        Button btnMenu = activityMainBinding.btnMenu;

        Button btnDiscover = activityMainBinding.btnDiscover;

        btnDiscover.setOnClickListener(this::btnDiscoverAction);

        Button btnLeft = activityMainBinding.btnLeft;
        Button btnRight = activityMainBinding.btnRight;
        Button btnUp = activityMainBinding.btnUp;
        Button btnDown = activityMainBinding.btnDown;
        Button btnMiddle = activityMainBinding.btnMiddle;
        Button btnBack = activityMainBinding.btnBack;
        Button btnHome = activityMainBinding.btnHome;
        Button btnVolInc = activityMainBinding.btnVolInc;
        Button btnVolDec = activityMainBinding.btnVolDec;
        Button btnMute = activityMainBinding.btnMute;
        Button btnPlayPause = activityMainBinding.btnPlayPause;
        Button btnRewind = activityMainBinding.btnRewind;
        Button btnForward = activityMainBinding.btnForward;

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
        buttons.add(swtConnectMouse);

        //TESTING
        buttons.add(btnCurLeft);
        buttons.add(btnCurClick);
        buttons.add(btnCurRight);
        btnCurLeft.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                boolean sent = MouseHelper.sendData(true, false, false, 0, 0, 0);
                if (sent) vibrate();
            }
            return false;
        });
        btnCurRight.setOnTouchListener((view, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                boolean sent = MouseHelper.sendData(false, true, false, 0, 0, 0);
                if (sent) vibrate();
            }
            return false;
        });


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
            if (sent) vibrate();
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
                if (sent) vibrate();
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
            if (sent) vibrate();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
//            debug("onKey= BACKSPACE");
            boolean sent = KeyboardHelper.sendKeyDown(KeyboardHelper.Modifier.NONE, KeyboardHelper.Key.BACKSPACE);
            KeyboardHelper.sendKeyUp();
            if (sent) vibrate();
            return true;
        }
        return false;
    }


    @SuppressLint("ClickableViewAccessibility")
    private void addKeyboardListeners(Button button, int... keys) {

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
                if (sent) vibrate();
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
                if (sent) vibrate();
            }
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                boolean sent = RemoteControlHelper.sendKeyUp();
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
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


}