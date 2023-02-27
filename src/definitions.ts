import type { PluginListenerHandle } from '@capacitor/core';

// export interface PrinterPlugin {
//   print(): Promise<void>;
// }

export interface PrinterPlugin {
  connectPrinter(options?: { port: string }): Promise<void>;
  printText(options: { data: string }): Promise<void>;
  setAlignment(options: { align: PrinterAlignment | number }): Promise<void>;
  newLine(): Promise<void>;
  setFontSize(options: { size: FontSize | number }): Promise<void>;
  setZoom(options: { zoom: number }): Promise<void>;
  printGBK(options: { data: string }): Promise<void>;
  printUTF8(options: { data: string }): Promise<void>;
  setCursorPosition(options: { position: number }): Promise<void>;
  setBold(options: { bold: boolean }): Promise<void>;
  cutPaper(): Promise<void>;
  openCashDrawer(): Promise<void>;
  printBarcode(options: { data: string }): Promise<void>;
  printQRCode(options: {
    data: string;
    size: number | QRCodeSize;
  }): Promise<void>;
  printImage(options: { data: string }): Promise<void>;
  disconnectPrinter(): Promise<void>;
  addListener(
    eventName: 'printerStatusChanged',
    listenerFunc: (status: PrinterStatus) => void,
  ): Promise<PluginListenerHandle> & PluginListenerHandle;
}

export enum FontSize {
  XSMALL = 1,
  SMALL = 2,
  MEDIUM = 3,
  LARGE = 4,
}

export enum QRCodeSize {
  XSMALL = 100,
  SMALL = 200,
  MEDIUM = 300,
  LARGE = 400,
  XLARGE = 500,
}

export enum PrinterAlignment {
  LEFT = 1,
  CENTER = 2,
  RIGHT = 3,
}

export interface PrinterStatus {
  connected: boolean;
}
