import os
import subprocess

# Получаем название папки, где находится скрипт
current_folder = os.path.basename(os.getcwd())

# Словарь файлов: {идентификатор: путь}
file_mapping = {
    'q': "App.tsx",
    'e': "android/app/src/main/AndroidManifest.xml",
    'r': "android/app/src/main/java/com/ktest/MainActivity.kt",    
    't': "android/app/src/main/java/com/ktest/MainApplication.kt",    
    'y': "android/app/src/main/java/com/ktest/MediaKeyListenerModule.kt",    
    'w': "android/app/src/main/java/com/ktest/MediaKeyListenerPackage.kt",    
    'u': "android/app/src/main/java/com/ktest/MediaKeyService.kt",    
#    'u': "android/app/src/main/res/xml/service_config.xml",    
#    'i': "android/app/src/main/res/values/strings.xml",    
#    'o': "android/app/src/main/java/com/rtest/SettingsActivity.kt",    
}

def show_help():
    """Функция для отображения справки"""
    print("=== Основные команды ===")
    print("  h   — показать эту справку")
    print("  cc  — скопировать все доступные файлы в буфер")
    print("  mq  — редактирование файлa")
    print("  q   — выйти")
    print()
    
    print("=== Команды записи ===")
    # Только команда pq
    if 'q' in file_mapping:
        file_name = os.path.basename(file_mapping['q'])
        print(f"  pq  — перезаписать {file_name} из буфера")
    print()
    
    print("=== Команды копирования ===")
    for file_id, file_path in file_mapping.items():
        file_name = os.path.basename(file_path)
        print(f"  {file_id}   {file_name}")
    print()

# Показать справку при запуске
show_help()

while True:
    # Добавляем название папки перед приглашением ввода
    command = input(f"{current_folder} >>> ").strip().lower()

    if command in ("e", "q", "quit"):
        print("Выход из скрипта.")
        break
        
    # Команда помощи
    if command in ("h", "help", "?"):
        show_help()
        continue

    # === КОПИРОВАНИЕ ВСЕХ ===
    if command == "cc":
        files = [path for path in file_mapping.values() if os.path.isfile(path)]
        if not files:
            print("Нет доступных файлов для копирования.")
            continue

        combined_text = ""
        for filepath in files:
            try:
                with open(filepath, "r", encoding="utf-8") as file:
                    content = file.read().strip()
                    combined_text += f"#===== {os.path.basename(filepath)} =====\n{content}\n\n"
            except Exception as e:
                print(f"Ошибка при чтении файла {filepath}: {e}")

        subprocess.run(["termux-clipboard-set"], input=combined_text.encode("utf-8"))
        print("Все доступные файлы скопированы в буфер обмена.\n")

    # === КОПИРОВАНИЕ ОДНОГО ===
    # Обрабатываем команды вида "c<идентификатор>"
    elif len(command) == 2 and command.startswith("c") and command[1] in file_mapping:
        file_id = command[1]
        filepath = file_mapping[file_id]
        
        if not os.path.isfile(filepath):
            print(f"Файл {filepath} не существует.")
            continue

        try:
            with open(filepath, "r", encoding="utf-8") as file:
                content = file.read().strip()
            labeled = f"#===== {os.path.basename(filepath)} =====\n{content}\n\n"
            subprocess.run(["termux-clipboard-set"], input=labeled.encode("utf-8"))
            print(f"Файл {filepath} скопирован в буфер.\n")
        except Exception as e:
            print(f"Ошибка при чтении файла {filepath}: {e}")

    # === ПЕРЕЗАПИСЬ ИЗ БУФЕРА ===
    # Обрабатываем команды вида "p<идентификатор>"
    elif len(command) == 2 and command.startswith("p") and command[1] in file_mapping:
        file_id = command[1]
        filepath = file_mapping[file_id]

        try:
            result = subprocess.run(["termux-clipboard-get"], capture_output=True, check=True)
            clipboard_text = result.stdout.decode("utf-8")
        except Exception as e:
            print(f"Ошибка при получении из буфера: {e}")
            continue

        try:
            with open(filepath, "w", encoding="utf-8") as file:
                file.write(clipboard_text)
            print(f"Файл {filepath} успешно перезаписан содержимым из буфера.\n")
        except Exception as e:
            print(f"Ошибка при записи файла {filepath}: {e}")
            
    # === РЕДАКТИРОВАНИЕ В MICRO ===
    # Обрабатываем команды вида "m<идентификатор>"
    elif len(command) == 2 and command.startswith("m") and command[1] in file_mapping:
        file_id = command[1]
        filepath = file_mapping[file_id]
        
        try:
            # Запускаем редактор micro для указанного файла
            subprocess.run(["micro", filepath])
            print(f"Файл {filepath} закрыт в редакторе.\n")
        except Exception as e:
            print(f"Ошибка при открытии редактора: {e}")
            print("Убедитесь, что редактор micro установлен в Termux:")
            print("pkg install micro")

    else:
        print("Неизвестная команда. Введите 'h' для справки.\n")
