KESH — Android-проект

Структура собрана из предоставленных файлов:
- app/src/main/java/com/kesh/finance/MainActivity.kt
- settings.gradle.kts
- build.gradle.kts
- app/build.gradle.kts
- app/src/main/AndroidManifest.xml

Как собрать APK:
1. Установите Android Studio и Android SDK Platform 35.
2. Откройте папку KESH-Android-Project в Android Studio.
3. Дождитесь окончания Gradle Sync.
4. Выберите Build -> Build Bundle(s) / APK(s) -> Build APK(s).
5. Debug APK появится в app/build/outputs/apk/debug/app-debug.apk.

Важно:
Этот архив — подготовленный исходный Android-проект, а не готовый APK.
В текущей среде нет Android SDK и Gradle executable, поэтому APK здесь не собран
и приложение не прошло компиляцию/запуск на устройстве. После Gradle Sync
проверьте сообщения сборки в Android Studio; при ошибках их можно исправить.

Сборка APK в облаке через GitHub Actions (если нет Android Studio):
1. Создайте репозиторий на GitHub и загрузите в него содержимое этой папки.
2. Откройте вкладку Actions и разрешите запуск workflow, если GitHub попросит.
3. Выберите "Build KESH APK" -> "Run workflow" (или дождитесь сборки после push).
4. После успешной сборки откройте завершённый запуск и скачайте Artifact "KESH-debug-apk".
5. Распакуйте скачанный архив — внутри будет APK.

Для облачной сборки добавлен файл .github/workflows/build-apk.yml.
Сборка всё равно может выявить ошибки исходного кода или конфигурации; если это произойдёт,
нужно посмотреть лог GitHub Actions и исправить конкретную ошибку.
