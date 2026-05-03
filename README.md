# EchoLine

EchoLine is a small Jetpack Compose text echo app built for the Joist Android take-home project.

## What It Does

- Lets the user enter text.
- Validates the input locally before submit.
- Pretends to validate the submitted text with an external server.
- Shows the submitted text when validation succeeds.
- Shows an error when validation fails.

## Implementation Choices

The app uses a single-module Kotlin/Compose setup because the assignment is intentionally small. The screen follows a minimal MVVM/MVI-style flow: `EchoViewModel` owns `EchoUiState`, the Compose UI renders that state, and UI events call back into the ViewModel.

`LocalTextValidator` handles immediate validation for empty input, minimum length, and unsupported control characters. `FakeEchoRepository` represents the external server boundary and uses a deterministic rule: valid text succeeds unless it contains `fail` or `error`. This keeps the behavior easy to test and easy to demonstrate during a walkthrough.

The UI uses Material3 and the existing app theme, including Android dynamic color support where available. No third-party UI, DI, networking, database, or navigation libraries were added.

## Testing

Run the JVM unit tests:

```bash
./gradlew :app:testDebugUnitTest
```

The unit tests focus on ViewModel behavior: local validation, preventing invalid submissions, successful echo display, and failed server validation.

## If This Became A Real App

The fake repository could be replaced with a real network-backed repository while keeping the ViewModel and UI mostly unchanged. If the feature grew, the next reasonable steps would be adding a small data package, explicit result types, and UI tests for critical interactions.
