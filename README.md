# FAF Moderator Client [![Build status](https://travis-ci.org/FAForever/faf-moderator-client.svg?branch=master)](https://travis-ci.org/FAForever/faf-moderator-client)
This application enables faforever.com moderators to perform administrative actions. This involves:
- managing of users
- avatars
- map & mod vault
- checking recent activities


# Setting up your environment

## Recommended software
- We recommend [JetBrains IntelliJ](https://www.jetbrains.com/idea) as IDE. The community edition is free and open source.
- For editing the user interface, we strongly recommend [Scene Builder](https://gluonhq.com/products/scene-builder)
- For a simple setup and testing of the dependencies you should use [Docker](https://www.docker.org) and [Docker Compose](https://github.com/docker/compose/releases)

## Boot the dependencies
- Checkout the [FAF Stack](https://github.com/FAForever/faf-stack) and boot the api via `docker-compose up -d faf-java-api`. This will also boot the FAF database.
- Get some [test data](https://github.com/FAForever/db/blob/develop/test-data.sql) and insert it into the MySQL db (user: root & password: banana). A tool like HeidiSQL can help you with this. This also adds a moderator account with username: test & password: test_password

## Run from source

1. Clone the project with git
1. Import the project into IntelliJ as "Maven Project"
1. Make sure you have the IntelliJ [Lombok plugin](https://plugins.jetbrains.com/idea/plugin/6317-lombok-plugin) installed
1. Make sure you have `Enable annotation processing` enabled in the settings
1. Add the dev profile as command line options ("VM options" in IntelliJ) using `-Dspring.profiles.active=dev`
