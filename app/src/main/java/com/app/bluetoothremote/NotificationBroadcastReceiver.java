package com.app.bluetoothremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(intent.getAction());

        boolean sent;

        switch (intent.getAction()) {
            case BluetoothHidService.ACTION_PLAY_PAUSE:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.PLAY_PAUSE[0], RemoteControlHelper.Key.PLAY_PAUSE[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
            case BluetoothHidService.ACTION_VOL_DEC:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.VOLUME_DEC[0], RemoteControlHelper.Key.VOLUME_DEC[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
            case BluetoothHidService.ACTION_VOL_INC:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.VOLUME_INC[0], RemoteControlHelper.Key.VOLUME_INC[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
            case BluetoothHidService.ACTION_POWER:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.POWER[0], RemoteControlHelper.Key.POWER[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;

            case BluetoothHidService.ACTION_MUTE:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.MUTE[0], RemoteControlHelper.Key.MUTE[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
            case BluetoothHidService.ACTION_LEFT:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.MENU_LEFT[0], RemoteControlHelper.Key.MENU_LEFT[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
            case BluetoothHidService.ACTION_RIGHT:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.MENU_RIGHT[0], RemoteControlHelper.Key.MENU_RIGHT[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
            case BluetoothHidService.ACTION_UP:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.MENU_UP[0], RemoteControlHelper.Key.MENU_UP[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
            case BluetoothHidService.ACTION_DOWN:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.MENU_DOWN[0], RemoteControlHelper.Key.MENU_DOWN[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
            case BluetoothHidService.ACTION_MIDDLE:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.MENU_PICK[0], RemoteControlHelper.Key.MENU_PICK[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
            case BluetoothHidService.ACTION_BACK:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.BACK[0], RemoteControlHelper.Key.BACK[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
            case BluetoothHidService.ACTION_HOME:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.HOME[0], RemoteControlHelper.Key.HOME[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;

            case BluetoothHidService.ACTION_MENU:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.MENU[0], RemoteControlHelper.Key.MENU[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;

            case BluetoothHidService.ACTION_REWIND:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.MEDIA_REWIND[0], RemoteControlHelper.Key.MEDIA_REWIND[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;

            case BluetoothHidService.ACTION_FORWARD:
                sent = RemoteControlHelper.sendKeyDown(RemoteControlHelper.Key.MEDIA_FAST_FORWARD[0], RemoteControlHelper.Key.MEDIA_FAST_FORWARD[1]);
                if (sent)
                    MainActivity.vibrate();
                RemoteControlHelper.sendKeyUp();
                break;
        }
    }
}
