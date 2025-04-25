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

export interface UserDetails {
  countryCode: string;
  phoneNumber: string;
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
  td_json_client_create(): Promise<string>;
  //td_json_client_execute(request: ExecuteRequest): Promise<string>;
  //td_json_client_send(request: SendRequest): Promise<string>;
  td_json_client_receive(): Promise<string>;

  startTdLib(parameters: TdLibParameters): Promise<string>;
  subscribeToEvents(eventTypes: string[]): void;
  unsubscribeFromEvents(eventTypes: string[] | null): void;
  login(userDetails: UserDetails): Promise<void>;
  verifyPhoneNumber(otp: string): Promise<void>;
  verifyPassword(password: string): Promise<string>;
  getAuthorizationState(): Promise<any>;
  getProfile(): Promise<any>;
  sendMessage(chatId: number, message: string, file: string | null): Promise<any>;
  createNewSupergroupChat(title: string, isForum: boolean | null, isChannel: boolean | null, description: string | null, location: Location | null, messageAutoDeleteTime: number | null, forImport: boolean | null): Promise<any>;
  getChats(limit: number): Promise<any>;
  getChat(chatId: number): Promise<any>;
  getChatHistory(chatId: number, fromMessageId: number | null, offset: number | null, limit: number | null, onlyLocal: boolean | null): Promise<any>;
  logout(): Promise<any>;
  destroy(): Promise<any>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Tdlib');
