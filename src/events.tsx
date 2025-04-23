import { NativeEventEmitter, NativeModules } from 'react-native';

const { Tdlib } = NativeModules;
const tdlibEmitter = new NativeEventEmitter(Tdlib);

let globalSubscription: any | null = null;

const listeners: Map<string, Set<(payload: any) => void>> = new Map();

function ensureGlobalSubscription() {
  if (globalSubscription) return;

  globalSubscription = tdlibEmitter.addListener('tdlibGlobalUpdate', (event: string) => {
    try {
      const parsed = JSON.parse(event);
      const { '@type': type } = parsed;

      const typeCallbacks = listeners.get(type);
      if (typeCallbacks) {
        for (const cb of typeCallbacks) {
          cb(parsed);
        }
      }

      const globalCallbacks = listeners.get('tdlibGlobalUpdate');
      if (globalCallbacks) {
        for (const cb of globalCallbacks) {
          cb(parsed);
        }
      }
    } catch (err) {
      console.warn('Failed to parse TDLib payload', err);
    }
  });
}

export function subscribeToTdlibEvents(
  types: string[],
  callback: (payload: any) => void
) {
  ensureGlobalSubscription();

  for (const type of types) {
    if (!listeners.has(type)) {
      listeners.set(type, new Set());
      Tdlib.subscribeToEvents([type]);
    }
    listeners.get(type)!.add(callback);
  }

  return () => unsubscribeFromTdlibEvents(types, callback);
}

export function unsubscribeFromTdlibEvents(
  types: string[] | null,
  callback?: (payload: any) => void
) {
  if (!types) {
    for (const [type, callbacks] of listeners.entries()) {
      if (callback) {
        callbacks.delete(callback);
        if (callbacks.size === 0) {
          listeners.delete(type);
          Tdlib.unsubscribeFromEvents([type]);
        }
      } else {
        listeners.delete(type);
        Tdlib.unsubscribeFromEvents([type]);
      }
    }
  } else {
    for (const type of types) {
      const set = listeners.get(type);
      if (set) {
        if (callback) {
          set.delete(callback);
        }
        if (!callback || set.size === 0) {
          listeners.delete(type);
          Tdlib.unsubscribeFromEvents([type]);
        }
      }
    }
  }

  if ([...listeners.values()].every(set => set.size === 0)) {
    globalSubscription?.remove();
    globalSubscription = null;
  }
}
