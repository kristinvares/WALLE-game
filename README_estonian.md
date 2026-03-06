# ITI0301-2025
## WALLE
### Mängu kirjeldus:

Tegemist on shooter mänguga, mida saab mängida nii single- kui ka multiplayerina. Mängus saab liikuda, tulistada ning olemas on ka AI, mis hetkel tuvastab talle lähimal oleva mängija ning liigub tema poole.

### Klahviseosed:

- leftarrow, uparrow, rightarrow, downarrow - liikumine
- space - tulistamine

### Kuidas mäng tööle saada:

Algselt tuleb see repo endale kloonida ja siis avada too Intellijs. Seal jaguneb see peamiselt kaheks osaks: server ja client. Server läheb tööle, kui jooksutada GameServeri java faili, mis on servr -> src -> main -> java -> ee -> taltech -> game -> server -> GameServer.java. See on serveri launcher. Client läheb tööle, kui jooksutada Lwjg3Launcher java faili, mis on
client -> lwjgl3 -> src -> main -> java -> ee -> taltech -> WALLE -> lwjgl3 -> Lwjg3Launcher.java. See on kliendi launcher. Kuna tegemist on gradle projektiga, siis on mõistlik avada client kaust ja serveri kaust erinevatest akendes Intellijs. Seda saab teha, kui Intellijs avada File -> Open ning siis valida client kaust ja serveri kaust. Kui need on erinevates akendes avatud tuleb need buildida, selle jaoks peaks olema build.gradle, mida saab jooksutada. Kui builditud, siis tuleb jooksutada vastav launcher. Mängu toimimiseks tuleb algselt panna tööle server ning siis client. Kui sa tahad jooksutada mitut klienti, siis vajuta üleval paremas nurgas kolmele täpile -> edit ->  Gradle ->  client:lwjgl3 [run] ->  modify options ->  allow multiple instances -> sulge aken -> apply -> ok.

### Kuidas ühenduda taltechi serveriga

Eeldusel, et server on üleval, siis:

- **Serveri IP aadress:** 193.40.255.32
- **Port:** TCP: `8080`, UDP: `8081`
- **Koodijupp, kus saab ühenduda, on walleGame klassis clientis:**

```java
client.connect(5000, "193.40.255.32", 8080, 8081);
```

### Kuidas mängida

1. Kui tahad üksinda mängida vajuta play, kui tahad mängida mitmekesi, siis vajuta multiplayer
2. Mängus saab liikuda kasutades keyarroweid ning tulistada vajutades space
3. Hetkel pole Ai collisioneid tehtud, seega ta liigub mängija poole ning midagi ei juhtu
4. Mängust saab välja, kui vajutada esc, see peaks tooma menüü ette, kus saab vajutada quiti


### Tehnoloogiad projektis:

- Java 21 - Programming language
- LibGDX 1.13.1 - Game development framework
- KryoNet 2.22 - Network communication
- Gradle (x.xx) - Build automation tool

### Features:

| Feature                 | Description                                |
|-------------------------|--------------------------------------------|
| Multiplayer support     | Multiple players can join                  |
| Map with collision detection | Map with working collisions           |
| Parallel game instances | Multiple games can run simultaneously      |
| AI enemies              | Bots move to the nearest player on sight   |
| Menu                    | Single- and multiplayer options            |




### Authorid:
- Martin Sõna
- Janne-Lii Aun-Trepp
- Kristin Vares