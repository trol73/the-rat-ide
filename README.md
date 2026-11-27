# the-rat-avr-ide

The RAT IDE — IDE for AVR/8085 assembly development.

## Build

### Prerequisites

- **JDK 21** (or newer)
- **Gradle 7.3.3+** (wrapper included)

### Quick Start

```bash
./build.sh
```

Or manually:

```bash
gradle clean jar
```

### Output

The built JAR is placed in `build/libs/the-rat.jar`.

This is a **fat JAR** — all dependencies and native libraries are embedded:

| Content | Included |
|---------|----------|
| Java sources (`src/main/java`) | ✅ |
| Resources (`src/main/dist`, `src/main/resources`) | ✅ |
| All JARs from `lib/` | ✅ (extracted) |
| Native libraries (`native/*.dll`) | ✅ |
| `the-rat.jar` (compiler library) | ✅ (extracted) |

### Run

```bash
java -jar build/libs/the-rat.jar
```

## License

GPL v3
