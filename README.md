# sd-Chat

![Build Status](https://github.com/Arishkamu/sd-control/actions/workflows/ci-chat.yaml/badge.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

Simple Chat with cli and gui.

***Supported operations:***

1. [Message] - send message to current chanel
2. !swith <channel name> - switch to channel with <channel name>

## Installation and Run

### Prerequisites

- Java Development Kit (JDK) 11 or higher installed
- Gradle installed (8.10+)
- **RabbitMQ server installed and running** (see below)

---

### 0. Start RabbitMQ Server

Before building or running the chat application, make sure the RabbitMQ server is running.

On macOS (with Homebrew), run:

```
  rabbitmq-server
```
---

### 1. Clone the repository:

```
git clone git@github.com:Arishkamu/sd-control.git
cd sd-control
```

---
### Run CLI
Run the project using Gradle only:

```
./gradlew runCli --args="Host Channel"
```
Or
```
./gradlew run --args="cli Host Channel"
```

---

### Run GUI
Run the project using Gradle only:

```
./gradlew runGui --args="Host Channel"
```
Or
```
./gradlew run --args="gui Host Channel"
```

---

### Tests

To run the unit tests, use:

```
./gradlew test
```

---

## License

This project is licensed under the [MIT license](LICENSE)

## Contributors

* [Zalilova Diana](https://www.github.com/mediana105)
* [Ivanova Arina](https://www.github.com/Arishkamu)
* [Isaeva Ekaterina](https://www.github.com/karambo3a)

HSE SPB, AMIS-3
