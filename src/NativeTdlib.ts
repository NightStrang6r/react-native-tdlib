import type { TurboModule } from 'react-native';
import { TurboModuleRegistry } from 'react-native';

export interface TdLibParameters {
  api_id: number;
  api_hash: string;
  system_language_code?: string;
  device_model?: string;
  system_version?: string;
  application_version?: string;
}

export interface SendRequest {}

export interface ExecuteRequest {}

export interface Location {
  latitude: number;
  longitude: number;
  horizontalAccuracy: number;
}

export interface ChatLocation {
  location: Location;
  address: string;
}

export interface ChatList {

}

export interface Spec extends TurboModule {
  /*
    For TDLib built with JSON or JSONJava interface only
    https://github.com/tdlib/td/tree/master/example/android
  */
  td_json_client_create(): Promise<string>;
  td_json_client_execute(request: string): Promise<string>;
  td_json_client_send(request: string): Promise<string>;
  td_json_client_receive(timeout: number | null): Promise<string>;

  /*
    For TDLib built with Java or JSONJava interface only
    https://github.com/tdlib/td/tree/master/example/android
  */
  startTdLib(parameters: TdLibParameters): Promise<string>;
  subscribeToEvents(eventTypes: string[]): void;
  unsubscribeFromEvents(eventTypes: string[] | null): void;
  login(phoneNumber: string): Promise<string>;
  verifyPhoneNumber(otp: string): Promise<void>;
  verifyPassword(password: string): Promise<string>;
  getAuthorizationState(): Promise<any>;
  getProfile(): Promise<any>;
  createNewSupergroupChat(title: string, isForum: boolean | null, isChannel: boolean | null, description: string | null, location: Location | null, messageAutoDeleteTime: number | null, forImport: boolean | null): Promise<any>;
  getChats(limit: number): Promise<any>;
  getChat(chatId: number): Promise<any>;
  getChatHistory(chatId: number, fromMessageId: number | null, offset: number | null, limit: number | null, onlyLocal: boolean | null): Promise<any>;
  sendMessage(chatId: number, message: string, file: string | null): Promise<any>;
  downloadFile(fileId: number, priority: number | null, offset: number | null, limit: number | null, synchronous: boolean | null): Promise<any>;
  logout(): Promise<any>;
  destroy(): Promise<any>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Tdlib');