# Scheduler
The scheduler is the interface between the unity frontend and the individual workers. Workers can register and update data (populations) via a REST-API, see [OpenApi 3.0 Documentation](https://pschm.github.io/AVS-Scheduler-API/) for a detailed specification.

## Installation
The implementation of the scheduler is a Gradle project, so it's necessary that the IDE used for building (e.g. IntelliJ) supports Gradle. Should IntelliJ doesn't recognize the project automatically, the Gradle synchronization must be started manually. You can trigger this by opening the "build.gradle" file and pressing "Import all Gradle project". If the "Run" button (Shift + F10) isn't immediately green, open the Application.kt and press the "Run" button next to the main function.

The server then listens to **localhost:8080**.

## Documentation
The REST-API provided by the scheduler is documented in the [OpenApi 3.0](https://swagger.io/docs/specification/about/) Standard and can be found [here](https://pschm.github.io/AVS-Scheduler-API/);
