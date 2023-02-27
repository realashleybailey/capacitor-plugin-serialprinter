package com.realashleybailey.plugin.printer;

import android.content.SharedPreferences;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.BitmapFactory;

import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
@CapacitorPlugin(name = "Printer")

public class PrinterPlugin extends Plugin {

    private static final String TAG = "PrinterPlugin";
    private Printer printer;

    @PluginMethod
    public void connectPrinter(PluginCall call) {
        if (printer == null) {
            if (call.hasOption("port")) {
                String port = call.getString("port");
                printer = new Printer(port);
                Log.d(TAG, "Printer connected");
            } else {
                printer = new Printer(null);
                Log.d(TAG, "Printer connected");
            }
        }
        call.resolve();
    }

    @PluginMethod
    public void printText(PluginCall call) {
        String data = call.getString("data");
        if (printer != null) {
            printer.print(data);
            printer.print("\n");
            Log.d(TAG, "Printing text: " + data);
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void setAlignment(PluginCall call) {
        Integer data = call.getInt("align");
        if (printer != null) {
            byte[] centerCommand = new byte[3];
            centerCommand[0] = 0x1B;
            centerCommand[1] = 0x61;

            switch (data) {
                case 2:
                    centerCommand[2] = 0x01;
                    break;
                case 3:
                    centerCommand[2] = 0x02;
                    break;
                default:
                    centerCommand[2] = 0x00;
                    break;
            }

            printer.write(centerCommand);
            Log.d(TAG, "Setting the Alignment");
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void newLine(PluginCall call) {
        if (printer != null) {
          printer.print("\n");
          Log.d(TAG, "Printing New Line");
          call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void setFontSize(PluginCall call) {
        Integer fontInt = call.getInt("size");
        if (printer != null) {
            byte[] arrayOfByte = new byte[3]; // GS ! 11H 倍宽倍高
            arrayOfByte[0] = 0x1D;
            arrayOfByte[1] = 0x21;

            switch (fontInt) {
                case 2:
                    arrayOfByte[2] = 0x10;
                    break;
                case 3:
                    arrayOfByte[2] = 0x01;
                    break;
                case 4:
                    arrayOfByte[2] = 0x11;
                    break;
                default:
                    arrayOfByte[2] = 0x00;
                    break;
            }

            printer.write(arrayOfByte);
            Log.d(TAG, "Setting the Font Size");
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void setZoom(PluginCall call) {
        Integer levelInt = call.getInt("zoom");
        if (printer != null) {
            byte[] rv = new byte[3];
            rv[0] = 0x1D;
            rv[1] = 0x21;
            rv[2] = (byte)((levelInt & 0x07)<<4 | (levelInt & 0x07));

            printer.write(rv);
            Log.d(TAG, "Setting the Zoom");
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void printGBK(PluginCall call) {
        String dataString = call.getString("data");
        if (printer != null) {
            byte[] arrayOfByte = null;
            try {
                arrayOfByte = dataString.getBytes("GBK"); // 必须放在try内才可以
            } catch (Exception e) {
                Log.e(TAG, "Exception getting GBK");
                call.reject("Exception getting GBK", e);
            }

            printer.write(arrayOfByte);
            printer.print("\n");
            Log.d(TAG, "Print data in GBK format: " + dataString);
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void printUTF8(PluginCall call) {
        String dataString = call.getString("data");
        if (printer != null) {
            byte[] arrayOfByte = null;
            try {
                arrayOfByte = dataString.getBytes(StandardCharsets.UTF_8); // 必须放在try内才可以
            } catch (Exception e) {
                Log.e(TAG, "Exception getting GBK");
                call.reject("Exception getting GBK", e);
            }

            printer.write(arrayOfByte);
            printer.print("\n");
            Log.d(TAG, "Print data in UTF-8 format: " + dataString);
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void setCursorPosition(PluginCall call) {
        Integer positionInt = call.getInt("position");
        if (printer != null) {
            byte[] cursorPosition = new byte[4]; // 当前行，设置绝对打印位置 ESC $ bL bH
            cursorPosition[0] = 0x1B;
            cursorPosition[1] = 0x24;
            cursorPosition[2] = (byte) (positionInt % 256);
            cursorPosition[3] = (byte) (positionInt / 256);

            printer.write(cursorPosition);
            Log.d(TAG, "Setting the Cursor Position");
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void setBold(PluginCall call) {
        Boolean boldBoolean = call.getBoolean("bold");
        if (printer != null) {
            byte[] boldCommand = new byte[3];
            boldCommand[0] = 0x1B;
            boldCommand[1] = 0x45;
            if (boldBoolean) {
                boldCommand[2] = 0x01;
            } else {
                boldCommand[2] = 0x00;
            }

            printer.write(boldCommand);
            Log.d(TAG, "Setting the Bold");
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void cutPaper(PluginCall call) {
        if (printer != null) {
            byte[] cutCommand = new byte[]{0x1D, 0x56, 0x42, 0x00}; // cut paper command
            printer.write(cutCommand);
            Log.d(TAG, "Cutting paper");
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void openCashDrawer(PluginCall call) {
        if (printer != null) {
            byte[] arrayOfByte = new byte[] { 0x1B, 0x70, 0x00, (byte) 0xC0, (byte) 0xC0 };
            printer.write(arrayOfByte);
            Log.d(TAG, "Opening Cash Draw");
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void printBarcode(PluginCall call) {
        String data = call.getString("data");
        if (printer != null) {
            byte[] arrayOfByte = new byte[13 + data.length()];

            // Set Barcode Height
            arrayOfByte[0] = 0x1D;
            arrayOfByte[1] = 'h';
            arrayOfByte[2] = 0x60; // 1 to 255

            // Set Barcode Width
            arrayOfByte[3] = 0x1D;
            arrayOfByte[4] = 'w';
            arrayOfByte[5] = 2; // 2 to 6

            // Set Barcode Text Printing Position
            arrayOfByte[6] = 0x1D;
            arrayOfByte[7] = 'H';
            arrayOfByte[8] = 2; // 0 to 3

            // Print Code 39 Barcode
            arrayOfByte[9] = 0x1D;
            arrayOfByte[10] = 'k';
            arrayOfByte[11] = 0x45;
            arrayOfByte[12] = ((byte) data.length());
            System.arraycopy(data.getBytes(), 0, arrayOfByte, 13, data.getBytes().length);

            printer.write(arrayOfByte);
            Log.d(TAG, "Printing barcode: " + data);
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    private Bitmap createQRCode(String data, int width, int height) {
        try {
            // Set QR code encoding hints
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hints.put(EncodeHintType.MARGIN, 1);

            // Create QR code writer and encode the data
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            // Create bitmap from the bit matrix
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PluginMethod
    public void printQRCode(PluginCall call) {
        String data = call.getString("data");
        Integer size = call.getInt("size");

        Bitmap qrCodeBitmap = createQRCode(data, size, size);

        if (printer != null) {
            int width = qrCodeBitmap.getWidth();
            int height = qrCodeBitmap.getHeight();

            // Define pixel array
            int[] pixels = new int[width * height];

            // Get pixel values from QR code bitmap
            qrCodeBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            int widArray = ((width - 1) / 8) + 1; // Horizontal byte count
            int lenArray = widArray * height; // Vertical point count
            byte[] arrayOfByte = new byte[lenArray + 8]; // Define transformed data array

            arrayOfByte[0] = 0x1D;
            arrayOfByte[1] = 0x76;
            arrayOfByte[2] = 0x30;
            arrayOfByte[3] = 0x00;

            arrayOfByte[4] = (byte) widArray; // xL
            arrayOfByte[5] = (byte) (widArray / 256); // xH
            arrayOfByte[6] = (byte) height;
            arrayOfByte[7] = (byte) (height / 256);

            int indexByte = 8;
            arrayOfByte[indexByte] = 0;
            int indexBit = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int pixel = pixels[i * width + j];
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);
                    int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue + 0.5);
                    if (gray < 128) {
                        arrayOfByte[indexByte] |= (byte) (0x80 >> indexBit);
                    }
                    indexBit++;
                    if (indexBit == 8) {
                        indexBit = 0;
                        indexByte++;
                        if (indexByte >= arrayOfByte.length) {
                            break;
                        }
                        arrayOfByte[indexByte] = 0;
                    }
                }
            }

            printer.write(arrayOfByte);
            Log.d(TAG, "Printing QR code: " + data);
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    private Bitmap convertImage(String imageData) {
        byte[] decodedImage = Base64.decode(imageData, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
        return bitmap;
    }

    @PluginMethod
    public void printImage(PluginCall call) {
        String imageData = call.getString("data");

        Bitmap imageBitmap = convertImage(imageData);

        if (printer != null) {
            int width = imageBitmap.getWidth();
            int height = imageBitmap.getHeight();

            // Define pixel array
            int[] pixels = new int[width * height];

            // Get pixel values from QR code bitmap
            imageBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            int widArray = ((width - 1) / 8) + 1; // Horizontal byte count
            int lenArray = widArray * height; // Vertical point count
            byte[] arrayOfByte = new byte[lenArray + 8]; // Define transformed data array

            arrayOfByte[0] = 0x1D;
            arrayOfByte[1] = 0x76;
            arrayOfByte[2] = 0x30;
            arrayOfByte[3] = 0x00;

            arrayOfByte[4] = (byte) widArray; // xL
            arrayOfByte[5] = (byte) (widArray / 256); // xH
            arrayOfByte[6] = (byte) height;
            arrayOfByte[7] = (byte) (height / 256);

            int indexByte = 8;
            arrayOfByte[indexByte] = 0;
            int indexBit = 0;
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int pixel = pixels[i * width + j];
                    int red = Color.red(pixel);
                    int green = Color.green(pixel);
                    int blue = Color.blue(pixel);
                    int gray = (int) (0.299 * red + 0.587 * green + 0.114 * blue + 0.5);
                    if (gray < 128) {
                        arrayOfByte[indexByte] |= (byte) (0x80 >> indexBit);
                    }
                    indexBit++;
                    if (indexBit == 8) {
                        indexBit = 0;
                        indexByte++;
                        if (indexByte >= arrayOfByte.length) {
                            break;
                        }
                        arrayOfByte[indexByte] = 0;
                    }
                }
            }

            printer.write(arrayOfByte);
            Log.d(TAG, "Printing Image");
            call.resolve();
        } else {
            Log.e(TAG, "Printer is not connected");
            call.reject("Printer is not connected");
        }
    }

    @PluginMethod
    public void disconnectPrinter(PluginCall call) {
        if (printer != null) {
            printer.close();
            printer = null;
            Log.d(TAG, "Printer disconnected");
        }
        call.resolve();
    }
}
