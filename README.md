# FAF Moderator Client [![Build](https://github.com/FAForever/faf-moderator-client/actions/workflows/build.yml/badge.svg)](https://github.com/FAForever/faf-moderator-client/actions/workflows/build.yml)
This application enables faforever.com moderators to perform administrative actions. This involves:
- managing of users, user groups and permissions
- uploading and assigning avatars
- managing matchmaker map pools
- map & mod vault
- checking recent activities
- editing runtime translations

# How to use / run it
- Make sure you have Java 17 or higher installed (JRE or JDK does not matter). Adoptium offers free installation
  packages [here](https://adoptium.net/?variant=openjdk17) (other Java flavours like Oracle should also work)
- Download the right client version for your operating system from the release page
- Unzip it
- Go to the bin folder and run the .bat script (Windows) or the .sh script (Linux)
- Login with your FAF credentials

# Setting up your environment

## Recommended software
- We recommend [JetBrains IntelliJ](https://www.jetbrains.com/idea) as IDE. The community edition is free and open source.
- For editing the user interface, we strongly recommend [Scene Builder](https://gluonhq.com/products/scene-builder)
- For a simple setup and testing of the dependencies you should use [Docker](https://www.docker.org) and [Docker Compose](https://github.com/docker/compose/releases)

## Boot the dependencies
- Checkout the [FAF Stack](https://github.com/FAForever/faf-stack) and initialize the database (`scripts/init-db.sh`).
  Afterwards boot the api via `docker-compose up -d faf-java-api`.
- Get some [test data](https://github.com/FAForever/db/blob/develop/test-data.sql) and insert it into the MySQL db (
  user: root & password: banana). A tool like HeidiSQL can help you with this. This also adds a moderator account with
  username: test & password: test_password

## Run from source

1. Clone the project with git
1. Import the project into IntelliJ as "Gradle Project"
1. Make sure you have the IntelliJ [Lombok plugin](https://plugins.jetbrains.com/idea/plugin/6317-lombok-plugin) installed
1. Make sure you have `Enable annotation processing` enabled in the settings
1. Add the dev profile as command line options ("VM options" in IntelliJ) using `-Dspring.profiles.active=dev`
1. Add your platform as parameter for all Gradle processes e.g. `-PjavafxPlatform=win` for Windows.
