# ITI0301-2025
## Project description:

This project was originally made on gitlab for a development course at TalTech. The original version of the game was runnable on a server remotely. However we cannot extend the same support to the github version. The game was made as a group project with the 3 authors being: Kristin Vares([kristinvares](https://github.com/kristinvares)), Janne-Lii Aun-Trepp([jaaunt](https://github.com/jaaunt)) and Martin Sõna.

## WALLE
### Game description:

Walle is a shooter/RPG game, playable on either single- or multiplayer. The game supports multiple instances that can run at the same time. Some features of the game are stable and easy to use movement, shooting mechanics, 2 diffrent types of AIs (guards and regular hostile npcs). The ai detects the nearest player and finds the shortest path to them. The guards ai is only triggered by proximity. The games end objective is randomly chosen from a set amout of options.

### Keybinds:

- leftarrow, uparrow, rightarrow, downarrow - movement
- space - shoot in the direction you are facing
- escape - pause menu

### How to run the game:

First clone the project repo and open it in an IDE of your choice(must support java). The repo has 2 main parts: server and client. Please open both folders in seperate windows to continue. After that make sure to build the project again with grade for safety. You can find the build tasks in the gradle window on the right side. All server actions must be made on the server side and client side actions on the client side. Run the server first and then the client.

To run the game server locally on your pc you must run the GameServer file. You can find it under servr -> src -> main -> java -> ee -> taltech -> game -> server -> GameServer.java.

To run the client side  you must run the Lwjg3Launcher file. You can find it under client -> lwjgl3 -> src -> main -> java -> ee -> taltech -> WALLE -> lwjgl3 -> Lwjg3Launcher.java. 
To play the game on multiplayer on your pc you must first allow multiple instances for the Lwjg3Launcher.

### How it connect to the TalTech server

Only applicable when the server is currently running:

- **Server IP aadress:** 193.40.255.32
- **Port:** TCP: `8080`, UDP: `8081`
- **Code on how the client connects:**

```java
client.connect(5000, "193.40.255.32", 8080, 8081);
```

### How to play

1. Select either the singleplayer or multiplayer option.
2. When the game starts you can move with arrow keys and shoot with space.
3. Upon joining the game, hostile entities spawn and immidiately take the shortest path to you. Avoid them or kill them
4. When a hostile entity touches you, you will take damage and eventually die
5. The game ends when you find the correct house(picked randomly), avoid or kill the guards and enter it.
6. You can pause and leave the game in the middle by pressting esc and bringig up the pause menu


### Technologies used in the project:

- Java 21 - Programming language
- LibGDX 1.13.1 - Game development framework
- KryoNet 2.22 - Network communication
- Gradle - Build automation tool

### Features:

| Feature                 | Description                                |
|-------------------------|--------------------------------------------|
| Multiplayer support     | Multiple players can join                  |
| Map with collision detection | Map with working collisions           |
| Parallel game instances | Multiple games can run simultaneously      |
| AI enemies              | Bots move to the nearest player on sight   |
| Menu                    | Single- and multiplayer options            |


### Project athors:
- Martin Sõna
- Janne-Lii Aun-Trepp
- Kristin Vares
