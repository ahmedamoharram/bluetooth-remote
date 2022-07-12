package com.app.bluetoothremote;

import android.annotation.SuppressLint;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

public class KeyboardHelper {

    public @interface Modifier {
        int NONE = 0;
        int KEY_MOD_LCTRL = 0x01;
        int KEY_MOD_LSHIFT = 0x02;
        int KEY_MOD_LALT = 0x04;
        int KEY_MOD_LMETA = 0x08;
        int KEY_MOD_RCTRL = 0x10;
        int KEY_MOD_RSHIFT = 0x20;
        int KEY_MOD_RALT = 0x40;
        int KEY_MOD_RMETA = 0x80;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            Key.ENTER,
            Key.ESCAPE,
            Key.BACKSPACE,
            Key.TAB,
            Key.SPACE,
            Key.RIGHT,
            Key.LEFT,
            Key.DOWN,
            Key.UP
    })
    public @interface Key {
        int ENTER = 0x28;
        int ESCAPE = 0x29;
        int BACKSPACE = 0x2a;
        int TAB = 43;
        int SPACE = 0x2c;
        int RIGHT = 79;
        int LEFT = 80;
        int DOWN = 81;
        int UP = 82;

        int KEY_KP1 = 0x59;// Keypad 1 and End
        int KEY_KP2 = 0x5a;// Keypad 2 and Down Arrow
        int KEY_KP3 = 0x5b;// Keypad 3 and PageDn
        int KEY_KP4 = 0x5c;// Keypad 4 and Left Arrow
        int KEY_KP5 = 0x5d;// Keypad 5
        int KEY_KP6 = 0x5e;// Keypad 6 and Right Arrow
        int KEY_KP7 = 0x5f;// Keypad 7 and Home
        int KEY_KP8 = 0x60;// Keypad 8 and Up Arrow
        int KEY_KP9 = 0x61;// Keypad 9 and Page Up
        int KEY_KP0 = 0x62;// Keypad 0 and Insert
        int KEY_KPDOT = 0x63; // Keypad . and Delete

        int KEY_PROPS = 0x76; // Keyboard Menu
        int KEY_POWER = 0x66;
        int KEY_APPLICATION = 0x65; // Keyboard Application
        int KEY_VOLUMEUP = 0x80; // Keyboard Volume Up
        int KEY_VOLUMEDOWN = 0x81; // Keyboard Volume Down
        int KEY_HOME = 0x4a;
        int KEY_MEDIA_PLAYPAUSE = 0xe8;
        int KEY_MEDIA_STOPCD = 0xe9;
        int KEY_MEDIA_PREVIOUSSONG = 0xea;
        int KEY_MEDIA_NEXTSONG = 0xeb;
        int KEY_MEDIA_EJECTCD = 0xec;
        int KEY_MEDIA_VOLUMEUP = 0xed;
        int KEY_MEDIA_VOLUMEDOWN = 0xee;
        int KEY_MEDIA_MUTE = 0xef;
        int KEY_MEDIA_WWW = 0xf0;
        int KEY_MEDIA_BACK = 0xf1;
        int KEY_MEDIA_FORWARD = 0xf2;
        int KEY_MEDIA_STOP = 0xf3;
        int KEY_MEDIA_FIND = 0xf4;
        int KEY_MEDIA_SCROLLUP = 0xf5;
        int KEY_MEDIA_SCROLLDOWN = 0xf6;
        int KEY_MEDIA_EDIT = 0xf7;
        int KEY_MEDIA_SLEEP = 0xf8;
        int KEY_MEDIA_COFFEE = 0xf9;
        int KEY_MEDIA_REFRESH = 0xfa;
        int KEY_MEDIA_CALC = 0xfb;
    }

    public static final Map<Character, Integer> keyMap = Map.ofEntries(
            Map.entry('a', 0x04),
            Map.entry('b', 0x05),
            Map.entry('c', 0x06),
            Map.entry('d', 0x07),
            Map.entry('e', 0x08),
            Map.entry('f', 0x09),
            Map.entry('g', 0x0A),
            Map.entry('h', 0x0B),
            Map.entry('i', 0x0C),
            Map.entry('j', 0x0D),
            Map.entry('k', 0x0E),
            Map.entry('l', 0x0F),
            Map.entry('m', 0x10),
            Map.entry('n', 0x11),
            Map.entry('o', 0x12),
            Map.entry('p', 0x13),
            Map.entry('q', 0x14),
            Map.entry('r', 0x15),
            Map.entry('s', 0x16),
            Map.entry('t', 0x17),
            Map.entry('u', 0x18),
            Map.entry('v', 0x19),
            Map.entry('w', 0x1A),
            Map.entry('x', 0x1B),
            Map.entry('y', 0x1C),
            Map.entry('z', 0x1D),
            Map.entry('1', 0x1E),
            Map.entry('2', 0x1F),
            Map.entry('3', 0x20),
            Map.entry('4', 0x21),
            Map.entry('5', 0x22),
            Map.entry('6', 0x23),
            Map.entry('7', 0x24),
            Map.entry('8', 0x25),
            Map.entry('9', 0x26),
            Map.entry('0', 0x27),
            Map.entry(' ', 0x2C),
            Map.entry('-', 0x2D),
            Map.entry('=', 0x2E),
            Map.entry('[', 0x2F),
            Map.entry(']', 0x30),
            Map.entry('\\', 0x31),
            Map.entry(';', 0x33),
            Map.entry('\'', 0x34),
            Map.entry('`', 0x35),
            Map.entry(',', 0x36),
            Map.entry('.', 0x37),
            Map.entry('/', 0x38));

    public static final Map<Character, Integer> shiftKeyMap = Map.ofEntries(
            Map.entry('A', 0x04),
            Map.entry('B', 0x05),
            Map.entry('C', 0x06),
            Map.entry('D', 0x07),
            Map.entry('E', 0x08),
            Map.entry('F', 0x09),
            Map.entry('G', 0x0A),
            Map.entry('H', 0x0B),
            Map.entry('I', 0x0C),
            Map.entry('J', 0x0D),
            Map.entry('K', 0x0E),
            Map.entry('L', 0x0F),
            Map.entry('M', 0x10),
            Map.entry('N', 0x11),
            Map.entry('O', 0x12),
            Map.entry('P', 0x13),
            Map.entry('Q', 0x14),
            Map.entry('R', 0x15),
            Map.entry('S', 0x16),
            Map.entry('T', 0x17),
            Map.entry('U', 0x18),
            Map.entry('V', 0x19),
            Map.entry('W', 0x1A),
            Map.entry('X', 0x1B),
            Map.entry('Y', 0x1C),
            Map.entry('Z', 0x1D),
            Map.entry('!', 0x1E),
            Map.entry('@', 0x1F),
            Map.entry('#', 0x20),
            Map.entry('$', 0x21),
            Map.entry('%', 0x22),
            Map.entry('^', 0x23),
            Map.entry('&', 0x24),
            Map.entry('*', 0x25),
            Map.entry('(', 0x26),
            Map.entry(')', 0x27),
            Map.entry('_', 0x2D),
            Map.entry('+', 0x2E),
            Map.entry('{', 0x2F),
            Map.entry('}', 0x30),
            Map.entry('|', 0x31),
            Map.entry(':', 0x33),
            Map.entry('"', 0x34),
            Map.entry('~', 0x35),
            Map.entry('<', 0x36),
            Map.entry('>', 0x37),
            Map.entry('?', 0x38));


    @SuppressLint("MissingPermission")
    public static boolean sendKeyDown(@Modifier int modifier, int code) {
        if (BluetoothHidService.bluetoothHidDevice != null && BluetoothHidService.isHidDeviceConnected) {
            return BluetoothHidService.bluetoothHidDevice.sendReport(BluetoothHidService.bluetoothDevice, Constants.ID_KEYBOARD, KeyboardReport.getReport(modifier, code));
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    public static boolean sendKeyUp() {
        if (BluetoothHidService.bluetoothHidDevice != null && BluetoothHidService.isHidDeviceConnected) {
            return BluetoothHidService.bluetoothHidDevice.sendReport(BluetoothHidService.bluetoothDevice, Constants.ID_KEYBOARD, KeyboardReport.getReport(0, 0));
        }
        return false;
    }

    static Integer getKey(Character c) {
        return keyMap.getOrDefault(c,0);
    }

    static Integer getShiftKey(Character c) {
        return shiftKeyMap.getOrDefault(c,0);
    }

}
