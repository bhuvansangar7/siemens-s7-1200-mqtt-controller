# Siemens S7-1200 PLC MQTT Controller

An Android Kotlin application for controlling Siemens S7-1200 PLC devices via MQTT protocol.

## Features

- **MQTT Connectivity**: Connect to MQTT brokers with configurable IP, port, and topic
- **Auto Reconnect**: Automatic reconnection with exponential backoff
- **Connection Status**: Real-time connection status indicator
- **Control Interface**: ON/OFF buttons for immediate control + 4 customizable buttons
- **Local Storage**: Broker IP and credentials saved locally for quick reconnection
- **Modern UI**: Industrial-themed Material Design interface
- **Date & Time**: Real-time display of current date and time
- **Responsive**: Optimized for various screen sizes

## Requirements

- Android SDK 26 (API 26) or higher
- Android Studio 2021.1 or later
- Kotlin 1.7+
- Gradle 7.0+

## Installation

### Prerequisites
- Android Studio installed
- JDK 11 or higher
- MQTT Broker running (e.g., Mosquitto)

### Build & Run

1. Clone the repository
2. Open in Android Studio
3. Wait for Gradle sync to complete
4. Connect your Android device or start emulator (API 26+)
5. Click "Run" or press Shift + F10

### Building APK

```bash
./gradlew assembleRelease
```

The APK will be located at: `app/build/outputs/apk/release/app-release.apk`

## Configuration

### MQTT Broker Settings
- **IP Address**: Enter your MQTT broker IP
- **Port**: Default is 1883 (standard MQTT port)
- **Topic**: Enter the MQTT topic to subscribe/publish to

### Control Buttons
- **ON Button**: Publishes "1" to the configured topic
- **OFF Button**: Publishes "0" to the configured topic
- **Custom Buttons**: Configure with custom payloads

## Dependencies

- Eclipse Paho MQTT Client: `org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5`
- AndroidX: Core, AppCompat, ConstraintLayout
- Kotlin Coroutines
- Material Design 3

## License

MIT License