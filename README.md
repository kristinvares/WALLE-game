# ITI0301-2025
## WALLE
### Mängu kirjeldus:

Mäng on praegu oma algfaasis, kus on võetud baasiks Mario mängu tiledmap. Hetkel saab seal ainult liikuda
vasakule, paremale ja hüpata ning näeb teisi mängijaid ning nende liikumist, kui nad on sama serveriga ühendunud.

### Klahviseosed:

leftarrow, uparrow, rightarrow - liikumine

### Kuidas mäng tööle saada:

Algselt tuleb see repo endale kloonida ja siis avada too Intellijs. Seal jaguneb see peamiselt kaheks osaks: server ja client. Server läheb tööle, kui jooksutada GameServeri java faili, mis on servr -> src -> main -> java -> ee -> taltech -> game -> server -> GameServer.java. See on serveri launcher. Client läheb tööle, kui jooksutada Lwjg3Launcher java faili, mis on
client -> lwjgl3 -> src -> main -> java -> ee -> taltech -> WALLE -> lwjgl3 -> Lwjg3Launcher.java. See on kliendi launcher. Kuna tegemist on gradle projektiga, siis on mõistlik avada client kaust ja serveri kaust erinevatest akendes Intellijs. Seda saab teha, kui Intellijs avada File -> Open ning siis valida client kaust ja serveri kaust. Kui need on erinevates akendes avatud tuleb need buildida, selle jaoks peaks olema build.gradle, mida saab jooksutada. Kui builditud, siis tuleb jooksutada vastav launcher. Mängu toimimiseks tuleb algselt panna tööle server ning siis client. Kui sa tahad jooksutada mitut klienti, siis vajuta üleval paremas nurgas kolmele täpile -> edit ->  Gradle ->  client:lwjgl3 [run] ->  modify options ->  allow multiple instances -> sulge aken -> apply -> ok.

### Kui sa oled kasutaja kellel on authorized ssh key või server juba runnib on nüüd võimalik runnida mängu ka läbi taltech serveri:

Serveri runnimiseks kui sul on authorized ssh võti:

windows powershellis runnida neid commande:

- ssh ubuntu@193.40.255.32
- cd server
- java -jar server-1.0-SNAPSHOT.jar

Kui oled avanud serveri või server on juba avatud:

Client läheb tööle, kui jooksutada Lwjg3Launcher java faili, mis on
client -> lwjgl3 -> src -> main -> java -> ee -> taltech -> WALLE -> lwjgl3 -> Lwjg3Launcher.java. See on kliendi launcher. Kuna tegemist on gradle projektiga, siis on mõistlik avada client kaust erinevas aknas Intellijs. Seda saab teha, kui Intellijs avada File -> Open ning siis valida client kaust. Kui see on teises aknas avatud tuleb need buildida, selle jaoks peaks olema build.gradle, mida saab jooksutada. Kui builditud, siis tuleb jooksutada vastav launcher.



### Tehnoloogiad projektis:

- Java 21 - Programming language
- LibGDX 1.13.1 - Game development framework
- KryoNet 2.22 - Network communication
- Gradle (x.xx) - Build automation tool

### Features:

| Feature                 | Description                                |
|-------------------------|--------------------------------------------|
| Multiplayer support     | Multiple players can join                  |
| Object detection        | Map where u can't run through objects      |
| Parallel game instances | Multiple games can run simultaneously      |



### Authorid:
- Martin Sõna
- Janne-Lii Aun-Trepp
- Kristin Vares