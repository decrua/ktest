#!/usr/bin/env python3
import subprocess
import sys

def run_git_sequence():
    try:
        # Добавляем все изменения
        subprocess.run(["git", "add", "."], check=True)
        print("\033[92m✓ Все изменения добавлены (git add .)\033[0m")
        
        # Запрашиваем сообщение для коммита
        commit_message = input("Введите сообщение коммита: ").strip()
        if not commit_message:
            print("\033[91m✗ Сообщение коммита не может быть пустым!\033[0m")
            sys.exit(1)
        
        # Создаем коммит
        commit_result = subprocess.run(["git", "commit", "-m", commit_message], check=True)
        print(f"\033[92m✓ Коммит создан с сообщением: '{commit_message}'\033[0m")
        
        # Пытаемся выполнить push
        try:
            subprocess.run(["git", "push"], check=True)
            print("\033[92m✓ Изменения успешно отправлены (git push)\033[0m")
        except subprocess.CalledProcessError as push_error:
            print("\033[93m⚠ Ошибка при выполнении git push:\033[0m")
            print(f"\033[93m{push_error.stderr.decode() if push_error.stderr else push_error}\033[0m")
            print("\033[94mПопробуйте решить проблему с подключением и запустите:\033[0m")
            print("\033[95mgit push\033[0m")
            sys.exit(1)
            
    except subprocess.CalledProcessError as e:
        error_msg = e.stderr.decode() if e.stderr else str(e)
        print(f"\033[91m✗ Ошибка: {error_msg}\033[0m")
        sys.exit(1)

if __name__ == "__main__":
    run_git_sequence()
