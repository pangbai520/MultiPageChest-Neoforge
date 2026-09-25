# Multi Page Chest

A NeoForge 1.21.1 rewrite of the classic Forge 1.7.10 Multi Page Chest mod.

## Features

- One chest with five independent pages.
- 13 x 9 slots per page, for 585 storage slots in total.
- Page switching buttons in the chest GUI.
- Persistent inventory data saved with the block entity.
- Custom chest model, GUI, recipe, and loot table.

## Requirements

- Minecraft 1.21.1
- NeoForge 21.1.251 or a compatible 21.1.x version
- Java 21

## Installation

1. Install NeoForge for Minecraft 1.21.1.
2. Download the JAR from the repository's Releases page.
3. Put the JAR in the instance's `mods` directory.
4. Launch the game with NeoForge.

## Recipe

The recipe is available in the in-game recipe book after installing the mod.

## Building From Source

On Windows:

```powershell
.\gradlew.bat build
```

On Linux or macOS:

```bash
./gradlew build
```

The built JAR is written to:

```text
build/libs/multipagechest-1.3.4-neoforge-1.0.0.jar
```

To start a local client for testing:

```powershell
.\gradlew.bat runClient
```

## Development

The project uses the NeoForge ModDev Gradle plugin and Java 21. Generated resources are placed under `src/generated/resources` when the data run is used.

To regenerate data resources:

```powershell
.\gradlew.bat runData
```

## License

All rights reserved. See [LICENSE](LICENSE).

This project is a clean-room rewrite for NeoForge 1.21.1 based on the behavior of the original 1.7.10 Multi Page Chest mod.
