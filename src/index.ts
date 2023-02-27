import { registerPlugin } from '@capacitor/core';

import type { PrinterPlugin } from './definitions';

const Printer = registerPlugin<PrinterPlugin>('Printer');

export * from './definitions';
export { Printer };
