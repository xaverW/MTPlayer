
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

# MTPlayer
Das Programm MTPlayer ist eine Art Suchmaschine für Filme der Mediatheken der Öffentlich-Rechtlichen Sender. Das Programm stellt eine Liste mit Links zu den Filmen zur Verfügung. Es ist möglich, diese URLs an externe Programme weiterzugeben. Mit diesen Programmen können dann diese Filme angesehen oder aufgezeichnet werden.

Das Programm MTPlayer ist eine Alternative zu meinem früheren Programm MediathekView das jetzt von einer neuen Community weiter gepflegt wird. 

## Infos über das Programm
Das Programm nutzt den Ordner ".mtplayer" als Konfig-Ordner, es kann also parallel zu MediathekView benutzt werden. Man kann dem Programm auch einen Ordner für die Einstellungen mitgeben (und es z.B. auf einem USB-Stick verwenden):  
java -jar MTPlayer.jar ORDNER 

Ich habe einige Ideen die ich hatte oder bekam, damit umgesetzt. Die größten Änderungen haben die Filter und die interne Verarbeitung der Filme in der Filmliste und Blacklist erfahren.
weitere Infos über das Programm und was sich geändert hat, kann auf der Website nachgelsesen werden.

https://www.p2tools.de/mtplayer/

## Datenübernahme aus MediathekView
Wer Einstellungen von MediathekView übernehmen will, muss aus dessen Config-Ordner (.mediathek3) diese Dateien in den neuen Config-Ordner kopieren: history.txt downloadAbos.txt (diese Datei muss umbenannt werden in: downloads.txt) mediathek.xml (diese Datei muss umbenannt werden in: mtplayer.xml) Alle Einstellungen können nicht übernommen werden da sich einiges geändert hat, nach dem Start bitte die Einstellungen durchgehen und überprüfen.

## Systemvoraussetzungen
Unterstützt wird Windows (7, 8, 10) und Linux. Das Programm benötigt unter Windows und Linux eine aktuelle Java-VM ab Version: 1.8 (= Java 8). Für Linux-Benutzer wird OpenJDK8 empfohlen, außerdem benötigen Linux Benutzer die aktuelle Version von JavaFX (OpenJFX).
