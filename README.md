
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

# MTPlayer
Das Programm MTPlayer ist eine Art Suchmaschine für Filme der Mediatheken der Öffentlich-Rechtlichen Sender. Das Programm stellt eine Liste mit Links zu den Filmen zur Verfügung. Es ist möglich, diese URLs an externe Programme weiterzugeben. Mit diesen Programmen können dann diese Filme angesehen oder aufgezeichnet werden.

Das Programm MTPlayer ist eine Alternative zu meinem früheren Programm MediathekView das jetzt von einer neuen Community weiter gepflegt wird. 

<br />

**Infos über das Programm**

Das Programm nutzt den Ordner ".p2Mtplayer" unter Linux und den versteckten Ordner "p2Mtplayer" unter Windows als Konfig-Ordner, es kann also parallel zu MediathekView benutzt werden. Man kann dem Programm auch einen Ordner für die Einstellungen mitgeben (und es z.B. auf einem USB-Stick verwenden):  
java -jar MTPlayer.jar ORDNER 

Ich habe einige Ideen die ich hatte oder bekam, damit umgesetzt. Die größten Änderungen haben die Filter und die interne Verarbeitung der Filme in der Filmliste und Blacklist erfahren.
weitere Infos über das Programm und was sich geändert hat, kann auf der Website nachgelsesen werden.

https://www.p2tools.de/mtplayer/

<br />

**Datenübernahme aus MediathekView**

Wer Einstellungen von MediathekView übernehmen will, muss aus dessen Config-Ordner (.mediathek3) diese Dateien in den neuen Config-Ordner kopieren:  
history.txt  
downloadAbos.txt (diese Datei muss umbenannt werden in: downloads.txt)  
mediathek.xml (diese Datei muss umbenannt werden in: mtplayer.xml)

Alle Einstellungen können nicht übernommen werden da sich einiges geändert hat, nach dem Start bitte die Einstellungen durchgehen und überprüfen.

<br />

**Systemvoraussetzungen**

Unterstützt wird Windows (Vista, 7, 8, 10) und Linux. Das Programm benötigt unter Windows und Linux eine aktuelle Java-VM ab Version: 1.8 (= Java 8, Java 9, Java 10).

Für Linux-Benutzer wird OpenJDK8 empfohlen. Außerdem benötigen Linux Benutzer die aktuelle Version von JavaFX (OpenJFX). OpenJFX ist aber nur für OpenJDK8 ohne Probleme zu installieren. Soll es Java 10 sein, wäre das Oracle Java SE 10 eine Alternative (und das bringt JavaFX schon mit).


**Java 11**

MTPlayer kann auch unter Java 11 laufen. Da Oracle ab der Java Version 11 FX ausgelagert hat, muss das zusätzlich installiert sein. Der Download der aktuellen FX-Pakete findet sich hier: <a href="https://gluonhq.com/products/javafx/" target="_blank">www.gluonhq.com/products/javafx</a>. Für weitere Infos wäre hier ein guter Startpunkt: <a href="https://openjfx.io/" target="_blank">www.openjfx.io</a>

FX für Windows: __JavaFX Windows SDK__   
FX für Linux: __JavaFX Linux SDK__

Das Zipfile muss nur in ein beliebiges Verzeichnis entpackt werden.


Aufruf von MTPlayer für Windows (in einer Zeile):   

```java --module-path "C:\PFAD_ZUM_ENTPACKTEN_FX\javafx-sdk-11.0.2\lib" --add-modules=javafx.controls -jar MTPlayer.jar ```

Aufruf von MTPlayer für Linux (in einer Zeile):   

```java --module-path /PFAD_ZUM_ENTPACKTEN_FX/javafx-sdk-11.0.2/lib/ --add-modules=javafx.controls -jar MTPlayer.jar ```

(Soll der Aufruf nicht aus dem Speicherordner von MTPlayer erfolgen, muss auch für MTPlayer.jar der Pfad mit angegeben werden.)

