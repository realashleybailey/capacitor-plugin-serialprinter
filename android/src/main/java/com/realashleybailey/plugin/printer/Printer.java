package com.realashleybailey.plugin.printer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import android.util.Log;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class Printer {

    private static final String TAG = "Printer";

    private File serialPortFile;
    private FileInputStream inputStream;
    private FileOutputStream outputStream;

    public Printer(String data) {
        if (data != null) {
            serialPortFile = new File(data);
            initSerialPort();
        } else {
            serialPortFile = new File("/dev/ttyS1");
            initSerialPort();
        }
    }

    private void initSerialPort() {
        try {
            inputStream = new FileInputStream(serialPortFile);
            outputStream = new FileOutputStream(serialPortFile);
        } catch (IOException e) {
            Log.e(TAG, "Error initializing serial port: " + e.getMessage());
        }
    }

    public void print(String data) {
        try {
            outputStream.write(data.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to serial port: " + e.getMessage());
        }
    }

    public void write(byte[] data) {
        try {
            outputStream.write(data);
            outputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "Error writing to serial port: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing serial port: " + e.getMessage());
        }
    }
}
