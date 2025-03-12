esimene player sprite 2h
player character movement 2h
objektide märkimine tiledis 2h
Teha teised mängijad nähtavaks, kui nad liituvad serveriga 6h 30m
Teha algne Hud 1h
Koosolek 1h



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

### Tehnoloogiad projektis:

- Java 21 - Programming language
- LibGDX 1.13.1 - Game development framework
- KryoNet 2.22 - Network communication
- Gradle (x.xx) - Build automation tool



### Authorid:
- Martin Sõna
- Janne-Lii Aun-Trepp
- Kristin Vares
