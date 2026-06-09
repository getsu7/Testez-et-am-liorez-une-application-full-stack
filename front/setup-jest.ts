import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';

setupZoneTestEnv();

interface MockStorage {
  getItem: (key: string) => string | null;
  setItem: (key: string, value: string) => void;
  removeItem: (key: string) => void;
  clear: () => void;
}

/* mock for jsdom */
const mock = (): MockStorage => {
   let storage: { [key: string]: string } = {};
   return {
     getItem: (key: string): string | null => (key in storage ? storage[key] : null),
     setItem: (key: string, value: string): void => { storage[key] = value || ''; },
     removeItem: (key: string): void => { delete storage[key]; },
     clear: (): void => { storage = {}; },
   };
 };

Object.defineProperty(globalThis, 'localStorage', { value: mock() });
Object.defineProperty(globalThis, 'sessionStorage', { value: mock() });
Object.defineProperty(globalThis, 'getComputedStyle', {
  value: () => ['-webkit-appearance'],
});

Object.defineProperty(document.body.style, 'transform', {
  value: () => {
    return {
      enumerable: true,
      configurable: true,
    };
  },
});

/* output shorter and more meaningful Zone error stack traces */
// Error.stackTraceLimit = 2;
