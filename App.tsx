import React, {useEffect, useState} from 'react';
import {
  SafeAreaView,
  Text,
  NativeModules,
  NativeEventEmitter,
  StyleSheet,
  StatusBar,
  ToastAndroid, // Добавлен импорт для Toast
} from 'react-native';

const {MediaKeyListener} = NativeModules;

function App(): React.JSX.Element {
  const [pressCount, setPressCount] = useState(0);

  useEffect(() => {
    // [10] Toast при запуске прослушивания в JS
    ToastAndroid.show('10: JS: Запуск прослушивания', ToastAndroid.SHORT);
    MediaKeyListener.start();

    const eventEmitter = new NativeEventEmitter(MediaKeyListener);
    const subscription = eventEmitter.addListener(
      'onMediaKey79Pressed',
      () => {
        // [11] Toast при получении события
        ToastAndroid.show('11: JS: Кнопка 79 получена', ToastAndroid.SHORT);
        console.log('Кнопка 79 была нажата!');
        setPressCount(prevCount => prevCount + 1);
      },
    );

    return () => {
      // [12] Toast при отписке
      ToastAndroid.show('12: JS: Отписка от событий', ToastAndroid.SHORT);
      subscription.remove();
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