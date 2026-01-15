# Noteturne

Android note-taking app with encryption.

## Features

- Encrypted note storage using Enigma-likes algorithm
- 4-digit PIN protection
- QR code sharing between devices
- File export/import (.noteturne format)
- Toggle visibility for each note
- Light/dark theme support

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- ZXing (QR code)

## Build

```bash
./gradlew assembleDebug
```

## Security

- Notes encrypted with unique seed per note
- PIN hashed with SHA-256
- No cloud storage, all local
