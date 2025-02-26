# Juhend kliendi ja serveri lokaalseks käivitamiseks

## Serveri ja cliendi jagamine eri akendesse
Ava server moodul uues IntelliJ aknas. Tee sama client mooduliga.

## Serveri runnimmine
Ava aken kus on avatud serveri moodul. Leia vasakust äärest Gradle tab. Otsi sealt server -> tasks -> build -> build. Siis ava serveri moodulist serve -> src -> main -> java -> ee.taltech.game.server -> GameServer ning runni seal public class GameServerit. Mitme instance korraga runnimiseks vajuta kolmele punktile üleval paremal -> edit -> application -> GameServer -> modify options -> allow multiple instances. Sulge see aken vajuta apply ok. Vajuta punast stop nuppu -> stop all. Ning runni Gameserverit uuesti.

## Clienti runnimine
Ava aken kus on avatud serveri moodul. Leia vasakust äärest Gradle tab. Otsi sealt client -> tasks -> build -> build. 
Otsi ülesse core -> tasks -> build -> build.
Otsi ülesse lwjgl3 -> tasks -> build -> build.
Et nüüd seda runnida otsi ülesse lwjgl3 -> tasks -> application -> run.
Kui sa tahad runnida mitut instancei vajuta üleval paremas nurgas kolmele täpile -> edit ->  Gradle ->  client:lwjgl3 [run] ->  modify options ->  allow multiple instances -> sulge aken -> apply -> ok. Sulge kõik jooksvad protsessid nagu tegid serveris ja runni uuesti. Nii mitu korda kui soovid.

## Mis client aknas toimub
Kliendi aknas saad saa liikuda klaviatuuri nooltega paremale ja vasakule. Korraga saab liikuda ainult ühes aknas ning kuni sa liigud on sinu liikumist näha ka teises aknas. Kliendid saadavad kordinaate serverile.
