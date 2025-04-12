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
  countrycode: string; // Country code, e.g., "+1"
  phoneNumber: string; // Phone number, e.g., "1234567890"
}

export interface SendRequest {}

export interface ExecuteRequest {}

export interface Spec extends TurboModule {
  td_json_client_create(): Promise<string>;
  //td_json_client_execute(request: ExecuteRequest): Promise<string>;
  //td_json_client_send(request: SendRequest): Promise<string>;
  td_json_client_receive(): Promise<string>;

  startTdLib(parameters: TdLibParameters): Promise<string>;
  login(userDetails: UserDetails): Promise<void>;
  verifyPhoneNumber(otp: string): Promise<void>;
  verifyPassword(password: string): Promise<string>;
  getAuthorizationState(): Promise<any>;
  getProfile(): Promise<any>;
  sendMessage(chatId: string, message: string): Promise<any>;
  logout(): Promise<any>;
  destroy(): Promise<any>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('Tdlib');
