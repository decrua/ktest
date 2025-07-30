import React, {useEffect, useState} from 'react';
import {
  SafeAreaView,
  Text,
  NativeModules,
  NativeEventEmitter,
  StyleSheet,
  StatusBar,
  ToastAndroid,
  Platform,
  PermissionsAndroid,
} from 'react-native';

const {MediaKeyListener} = NativeModules;

function App(): React.JSX.Element {
  const [pressCount, setPressCount] = useState(0);

  useEffect(() => {
    const requestPermissionAndStartService = async () => {
      if (Platform.OS === 'android') {
        try {
          const granted = await PermissionsAndroid.request(
            PermissionsAndroid.PERMISSIONS.POST_NOTIFICATIONS,
            {
              title: 'Разрешение для ktest',
              message:
                'Приложению нужно разрешение на показ уведомлений, ' +
                'чтобы служба отслеживания кнопок могла работать в фоне.',
              buttonNeutral: 'Спросить позже',
              buttonNegative: 'Отклонить',
              buttonPositive: 'Разрешить',
            },
          );
          if (granted === PermissionsAndroid.RESULTS.GRANTED) {
            ToastAndroid.show('3: Разрешение получено, запускаем службу...', ToastAndroid.SHORT);
            MediaKeyListener.start();
          } else {
            ToastAndroid.show('14: Разрешение отклонено!', ToastAndroid.SHORT);
          }
        } catch (err) {
          console.warn(err);
        }
      }
    };

    ToastAndroid.show('10: JS: Запуск прослушивания', ToastAndroid.SHORT);
    requestPermissionAndStartService();

    const eventEmitter = new NativeEventEmitter(MediaKeyListener);
    const subscription = eventEmitter.addListener(
      'onMediaKey79Pressed',
      () => {
        ToastAndroid.show('11: JS: Кнопка 79 получена', ToastAndroid.SHORT);
        setPressCount(prevCount => prevCount + 1);
      },
    );

    return () => {
      ToastAndroid.show('12: JS: Отписка и остановка службы', ToastAndroid.SHORT);
      subscription.remove();
      if (Platform.OS === 'android') {
          MediaKeyListener.stop();
      }
    };
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle={'light-content'} />
      <Text style={styles.title}>ktest</Text>
      <Text style={styles.text}>
        Приложение отслеживает нажатие медиа-кнопки (код 79).
      </Text>
      <Text style={styles.counter}>Нажато: {pressCount} раз</Text>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#2c3e50',
  },
  title: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#ecf0f1',
    marginBottom: 20,
  },
  text: {
    fontSize: 18,
    color: '#bdc3c7',
    textAlign: 'center',
    paddingHorizontal: 20,
  },
  counter: {
    fontSize: 24,
    color: '#1abc9c',
    marginTop: 30,
    fontWeight: '500',
  },
});

export default App;