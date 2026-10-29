# FF Extreme Optimizer

Системный оптимизатор Android для Free Fire (без модификации файлов игры и античита).

## Возможности

1. **Game Mode Service** — мониторинг запуска `com.dts.freefireth` через UsageStatsManager + агрессивная очистка фоновых процессов + WakeLock.
2. **VPN DNS Booster** — локальный VpnService с Cloudflare/Google DNS.
3. **Режим iPhone** — Zero Animations + попытка изменения DPI.
4. **Плавающий HUD** — пинг, температура батареи, статус.

## Требования

- Android 8.0+ (API 26)
- Разрешения:
  - Usage Access (вручную)
  - Display over other apps (Overlay)
  - VPN (при первом запуске)
  - Исключение из оптимизации батареи

### Для анимаций и DPI (рекомендуется)

```bash
adb shell pm grant com.example.ffoptimizer android.permission.WRITE_SECURE_SETTINGS
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
adb shell wm density 320   # или другое значение
```

## Сборка

1. Открой проект в Android Studio (Hedgehog / Ladybug или новее).
2. Sync Gradle.
3. Build → Build Bundle(s) / APK(s) → Build APK(s).

Или через командную строку:

```bash
./gradlew assembleDebug
```

APK появится в `app/build/outputs/apk/debug/`.

## Важно

- Без root/Shizuku `killBackgroundProcesses` работает ограниченно.
- VPN — упрощённый (только DNS).
- Изменение DPI надёжнее через `wm density` / Shizuku.
- Используй на свой страх и риск. Может влиять на стабильность системы и расход батареи.

## Структура

```
app/src/main/java/com/example/ffoptimizer/
├── MainActivity.kt
├── ui/          (Home, iPhone Mode, Network)
├── service/     (GameMode, VpnDns, Overlay)
├── util/        (ProcessKiller, AnimationKiller, DpiHelper, WifiHighPerf)
└── receiver/    (BootReceiver)
```

Лицензия: для личного использования.
