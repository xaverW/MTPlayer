[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

# MTPlayer

Das Programm MTPlayer ist eine Art Suchmaschine für Filme der Mediatheken der Öffentlich-Rechtlichen Sender. Das Programm stellt eine Liste mit Links zu den Filmen zur Verfügung. Es ist möglich, diese URLs an externe Programme weiterzugeben. Mit diesen Programmen können dann diese Filme angesehen oder aufgezeichnet werden.

Das Programm MTPlayer ist der Nachfolger meines früheren Programms MediathekView. 

<br />

## Infos

Das Programm nutzt den Ordner ".p2Mtplayer" unter Linux und den versteckten Ordner "p2Mtplayer" unter Windows als Konfig-Ordner. Man kann dem Programm auch einen Ordner für die Einstellungen mitgeben (und es z.B. auf einem USB-Stick verwenden):  
```
java -jar MTPlayer.jar ORDNER 
```

Ich habe einige Ideen die ich hatte oder bekam, damit umgesetzt. Die größten Änderungen haben die Filter und die interne Verarbeitung der Filme in der Filmliste und Blacklist erfahren. Die eigene Mediensammlung kann vor einem Download durchsucht werden, die Suche darin wurde ausgebaut. Für die Programmoberfläche wurde auf ein modernes GUI-ToolKit gewechselt. Weitere Infos über das Programm und was sich geändert hat, kann auf der Website nachgelesen werden.

https://www.p2tools.de/mtplayer/


<br />

## Systemvoraussetzungen

Unterstützt wird Windows und Linux. 

Das Programm benötigt unter Windows und Linux eine aktuelle Java-VM ab Version: Java 17.
Für Linux-Benutzer wird OpenJDK empfohlen. (FX-Runtime bringt das Programm bereits mit und muss nicht installiert werden).

<br />

## Download

Das Programm wird in drei Paketen angeboten. Diese unterscheiden sich nur im "Zubehör", das Programm selbst ist in allen Paketen identisch:

- **MTPlayer-XX.zip**  
Das Programmpaket bringt nur das Programm und die benötigten "Hilfsprogramme" aber kein Java mit. Auf dem Rechner muss eine Java-Laufzeitumgebung ab Java17 installiert sein. Dieses Programmpaket kann auf allen Betriebssystemen verwendet werden. Es bringt Startdateien für Linux und Windows mit.

- **MTPlayer-XX__Linux+Java.zip**  
**MTPlayer-XX__Win+Java.zip**  
Diese Programmpakete bringen die Java-Laufzeitumgebung mit und sind nur für das angegebene Betriebssystem: Linux oder Windows. Es muss kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung liegt im Ordner "Java" und kommt von jdk.java.net).

zum Download: [github.com/xaverW/MTPlayer/releases](https://github.com/xaverW/MTPlayer/releases)

<br />

## Website

[www.p2tools.de/mtplayer/]( https://www.p2tools.de/mtplayer/)

