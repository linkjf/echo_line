# EchoLine

EchoLine is a small Jetpack Compose app that validates text input and echoes successful submissions.

## Features

- Lets the user enter text.
- Validates the input locally before submit.
- Pretends to validate the submitted text with an external server.
- Shows the submitted text when validation succeeds.
- Shows an error when validation fails.

## Architecture

The app uses a single-module Kotlin/Compose setup. The screen follows a minimal MVVM/MVI-style flow: `EchoViewModel` owns `EchoUiState`, the Compose UI renders that state, and UI events call back into the ViewModel.

`LocalTextValidator` handles immediate validation for empty input, minimum length, and unsupported control characters. `FakeEchoRepository` represents the external server boundary and uses a deterministic rule: valid text succeeds unless it contains `fail` or `error`. This keeps the behavior easy to test and easy to demonstrate during a walkthrough.

The UI uses Material3 and dynamic color where available. It stays scrollable and width-aware for phones, foldables, landscape, and keyboard-visible states.

## Testing

Run the JVM unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

The unit tests focus on ViewModel behavior: local validation, preventing invalid submissions, successful echo display, and failed server validation.

## Next Steps

The fake repository can be replaced with a real network-backed repository while keeping the ViewModel and UI mostly unchanged. If the feature grows, the next reasonable additions are a small data package and UI tests for critical interactions.
