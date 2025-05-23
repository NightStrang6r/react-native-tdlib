/**
 * Example with authorization flow
 */

import React, { useCallback, useEffect } from 'react';
import {
  Button,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
  ToastAndroid
} from 'react-native';
import TdLib, { subscribeToTdlibEvents, unsubscribeFromTdlibEvents } from 'react-native-tdlib';
import Config from 'react-native-config';
import { CameraRoll } from '@react-native-camera-roll/camera-roll';
import type { TdLibParameters } from '../../../src/NativeTdlib';

const parameters = {
  api_id: Number(Config.APP_ID),
  api_hash: Config.APP_HASH,
} as TdLibParameters;

const AuthorizationExample = () => {
  const [phone, setPhone] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [otp, setOtp] = React.useState('');
  const [countryCode, setCountryCode] = React.useState('');
  const [profile, setProfile] = React.useState<any>(null);
  const [chatId, setChatId] = React.useState('');
  const [message, setMessage] = React.useState('');
  const [channelName, setChannelName] = React.useState('');
  const [channelDescription, setChannelDescription] = React.useState('');

  useEffect(() => {
    const subscription = subscribeToTdlibEvents(
      ['tdlibGlobalUpdate'],
      (payload) => {
        console.log(`${payload['@type']}:`, payload);
      }
    );

    return () => {
      subscription();
    };
  }, []);

  useEffect(() => {
    // Initializes TDLib with the provided parameters and checks the authorization state
    console.log('Initializing TDLib...');
    TdLib.startTdLib(parameters).then((r) => {
      console.log('StartTdLib:', r);
      TdLib.getAuthorizationState().then((r) => {
        console.log('InitialAuthState:', r);
        if (JSON.parse(r)['@type'] === 'authorizationStateReady') {
          getProfile(); // Fetches the user's profile if authorization is ready
        }
      });
    });
  }, []);

  // Sends a verification code to the provided phone number
  const sendCode = useCallback(async () => {
    try {
      console.log('Sending code...');
      const result = await TdLib.login({
        countryCode: countryCode,
        phoneNumber: phone,
      });
      console.log('SendCode:', result);
    } catch (error) {
      console.error('Ошибка login:', error);
    }
  }, [countryCode, phone]);

  // Verifies the phone number using the entered OTP code
  const verifyPhoneNumber = useCallback(() => {
    TdLib.verifyPhoneNumber(otp).then((r) =>
      console.log('VerifyPhoneNumber:', r)
    );
  }, [otp]);

  // Verifies the password if required for login
  const checkPassword = useCallback(() => {
    TdLib.verifyPassword(password).then((r) =>
      console.log('CheckPassword:', r)
    );
  }, [password]);

  // Fetches the profile of the logged-in user
  const getProfile = useCallback(async () => {
    console.log('Fetching profile...');
    const result = await TdLib.getProfile();
    const profile = Platform.select({
      ios: result,
      android: JSON.parse(result),
    });
    const json = JSON.parse(result);
    console.log('User Profile:', json);
    ToastAndroid.show(`Logged in as ${json.usernames.editableUsername}`, ToastAndroid.SHORT);
    setProfile(profile);
  }, []);

  const checkAuthState = useCallback(() => {
    TdLib.getAuthorizationState().then((r) => console.log('AuthState:', r));
  }, []);

  const sendMessage = useCallback(async () => {
    try {
      const gallery = await CameraRoll.getPhotos({
        first: 1,
      });
      
      let imageUri = null;
      if (gallery && gallery.edges && gallery.edges.length > 0) {
        const edge = gallery.edges[0];
        if (edge && edge.node && edge.node.image) {
          imageUri = edge.node.image.uri;
        }
      }

      console.log('Image URI:', imageUri);

      console.log('Chat ID:', chatId);
      console.log('Message:', message);
      const result = await TdLib.sendMessage(Number(chatId), message, imageUri);
      console.log('SendMessage:', result);
    } catch (error) {
      console.error('Error sending message:', error);
    }
  }, [chatId, message]);

  /*useEffect(() => {
    (async () => {
      while (true) {
        const result = await TdLib.td_json_client_receive();
        const parsedResult = JSON.parse(result);
        if (parsedResult.code != 400) {
          console.log('Received:', parsedResult);
        }

        if (parsedResult['@type'] === 'updateNewMessage') {
          console.log('New message received:', parsedResult);
        }
      }
    })();
  }, []);*/

  const createChannel = useCallback(async () => {
    const result = await TdLib.createNewSupergroupChat(
      channelName,
      false,
      true,
      channelDescription,
      null,
      null,
      null
    );
    console.log('CreateChannel:', result);
  }, [
    channelName,
    channelDescription,
  ]);

  const getChats = useCallback(async () => {
    try {
      console.log('Fetching chats...');
      const result = await TdLib.getChats(999);
      const parsedResult = JSON.parse(result);
      console.log(parsedResult);
    } catch (error) {
      console.error('Error fetching chats:', error);
    }
  }, []);

  const getChatHistory = useCallback(async () => {
    try {
      const getChat = await TdLib.getChat(Number(chatId));
      const parsedChat = JSON.parse(getChat);
      console.log('Chat:', parsedChat);
      const result = await TdLib.getChatHistory(
        Number(chatId),
        null,
        null,
        100,
        null
      );
      const parsedResult = JSON.parse(result);
      console.log('Chat History:', parsedResult);
    } catch (error) {
      console.error('Error fetching chat history:', error);
    }
  }, [chatId]);

  return (
    <ScrollView style={styles.container}>
      <View style={styles.contentContainer}>
        <Text style={styles.title}>TDLib Playground</Text>
        <Text>1. Login</Text>
        <TextInput
          value={countryCode}
          onChangeText={setCountryCode}
          placeholder={'+90'}
          placeholderTextColor={'gray'}
          style={[
            styles.input,
            {
              marginBottom: 14,
              marginTop: 14,
            },
          ]}
        />
        <TextInput
          value={phone}
          onChangeText={setPhone}
          placeholder={'1234567890'}
          placeholderTextColor={'gray'}
          style={[
            styles.input,
            {
              marginBottom: 14,
            },
          ]}
        />
        <Button title={'Send Code'} onPress={sendCode} />
        <View style={styles.divider} />
        <Text>2. OTP code</Text>
        <TextInput
          value={otp}
          onChangeText={setOtp}
          placeholder={'1234'}
          placeholderTextColor={'gray'}
          style={[
            styles.input,
            {
              marginVertical: 14,
            },
          ]}
        />
        <Button title={'Login'} onPress={verifyPhoneNumber} />
        <View style={styles.divider} />
        <Text>3. Password (optional)</Text>
        <TextInput
          value={password}
          onChangeText={setPassword}
          placeholder={'123456'}
          placeholderTextColor={'gray'}
          style={[
            styles.input,
            {
              marginVertical: 14,
            },
          ]}
        />
        <Button title={'Login'} onPress={checkPassword} />
        <View style={styles.divider} />
        {profile && (
          <>
            <Text>
              Name: {profile.first_name || profile.firstName}{' '}
              {profile.last_name || profile.lastName}
            </Text>
            <Text>
              Phone Number: {profile.phone_number || profile.phoneNumber}
            </Text>
          </>
        )}
        <Button title={'Get Profile'} onPress={getProfile} />
        <Button title={'Get Auth State'} onPress={checkAuthState} />
        <View style={styles.divider} />
        <Text>Send Message</Text>
        <TextInput
          value={chatId}
          onChangeText={setChatId}
          placeholder={'Chat ID'}
          placeholderTextColor={'gray'}
          style={[
            styles.input,
            {
              marginVertical: 14,
            },
          ]}
        />
        <TextInput
          value={message}
          onChangeText={setMessage}
          placeholder={'Message'}
          placeholderTextColor={'gray'}
          style={[
            styles.input,
            {
              marginBottom: 14,
            },
          ]}
        />
        <Button title={'Send Message'} onPress={sendMessage} />
        <Button title={'Get Chat History'} onPress={getChatHistory} />
        <Button title={'Get Chats'} onPress={getChats} />
        <View style={styles.divider} />
        <Text>Create Channel</Text>
        <TextInput
          value={channelName}
          onChangeText={setChannelName}
          placeholder={'Channel Name'}
          placeholderTextColor={'gray'}
          style={[
            styles.input,
            {
              marginVertical: 14,
            },
          ]}
        />
        <TextInput
          value={channelDescription}
          onChangeText={setChannelDescription}
          placeholder={'Channel Description'}
          placeholderTextColor={'gray'}
          style={[
            styles.input,
            {
              marginBottom: 14,
            },
          ]}
        />
        <Button title={'Create Channel'} onPress={createChannel} />
        <View style={styles.divider} />
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: 'white',
    flex: 1,
  },
  contentContainer: {
    paddingTop: 20,
    paddingHorizontal: 8,
  },
  title: { fontSize: 18, alignSelf: 'center', marginBottom: 10 },
  input: { padding: 6, borderRadius: 10, borderWidth: 1, height: 40 },
  divider: {
    height: 1,
    width: '100%',
    backgroundColor: 'black',
    marginVertical: 14,
  },
});

export default AuthorizationExample;
