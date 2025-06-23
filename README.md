[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

# MTPlayer

Das Programm MTPlayer ist eine Art Suchmaschine für Filme der Mediatheken der Öffentlich-Rechtlichen Sender. Das Programm stellt eine Liste mit Links zu den Filmen zur Verfügung. Es ist möglich, diese URLs an externe Programme weiterzugeben. Mit diesen Programmen können dann diese Filme angesehen oder aufgezeichnet werden.
<br />

## Infos
Die Arbeit für das Projekt (MediathekView), die über die Pflege des Programms hinaus, weiter nötig war (z.B. die Filmliste aktuell zu halten) war für mich alleine nicht mehr zu leisten. So habe ich mich entschlossen, das Projekt als Community-Projekt weiter laufen zu lassen, um die Filmliste wird sich seit dem super gekümmert. Ich habe dadurch wieder die Zeit gefunden, an anderen Programmen zu arbeiten.
<br />
<br />
MTPlayer ist nun dabei herausgekommen. Es ist ein Nachfolger von MediathekView (das von MediathekView.de weiter gepflegt wird). Damit wird ebenfalls die Filmliste des Projekts MediathekView.de durchsucht. Es sind Online-Mediatheken verschiedener Sender enthalten und die Filme können aufgezeichnet und abgespielt werden.
<br />
<br />
MTPlayer wurde ganz neu mit einem modernem GUI-Toolkit erstellt. Ein besonderes Augenmerk habe ich auf die Suche nach Filmen gelegt. Mit einem deutlich erweiterten Filter, auch mit der Möglichkeit einzelne Filtereinstellungen zu speichern und wieder abzurufen, wurden die Möglichkeiten des Suchens stark erweitert. Auch die angelegten Abos können die Filme nach vielen weiteren Kriterien auswählen. Das Ausblenden ungewollter Filme lässt sich mit zwei Blacklists sehr gut an die eigenen Bedürfnisse anpassen. Eine Mediensammlung für bereits geladene Filme kann mit dem Programm verwaltet werden. Damit ist es leicht möglich, doppelte Downloads zu vermeiden. Auch gibt es eine Live-Suche in den Mediatheken von ARD und ZDF. So können fehlende Beiträge vielleicht doch gefunden werden.
<br />
<br />

Weitere Infos über das Programm und was sich alles geändert hat, kann auf der Website nachgelesen werden.

https://www.p2tools.de/mtplayer/

Das Programm nutzt den Ordner ".p2Mtplayer" unter Linux und den versteckten Ordner "p2Mtplayer" unter Windows als Konfig-Ordner. Man kann dem Programm auch einen Ordner für die Einstellungen mitgeben (und es z.B. auf einem USB-Stick verwenden):  
```
java -jar MTPlayer.jar ORDNER 
```

<br />

## Systemvoraussetzungen

Unterstützt wird Windows und Linux. 

Das Programm benötigt unter Windows und Linux eine aktuelle Java-VM ab Version: Java 17.
Für Linux-Benutzer wird OpenJDK empfohlen. (FX-Runtime bringt das Programm bereits mit und muss nicht installiert werden).

<br />

## Download
Das Programm wird in fünf Paketen angeboten. Diese unterscheiden sich nur im “Zubehör”, das Programm selbst ist in allen Paketen identisch: 

* **MTPlayer-XX__Windows==SETUP__DATUM.exe**  
Mit diesem Programmpaket kann das Programm auf Windows installiert werden: Doppelklick und alles wird eingerichtet, auch ein Startbutton auf dem Desktop. Es muss auch kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung ist enthalten).

* **MTPlayer-XX__DATUM.zip**  
Das Programmpaket bringt nur das Programm und die benötigten Hilfsprogramme aber kein Java mit. Auf dem Rechner muss eine Java-Laufzeitumgebung ab Java17 installiert sein. Dieses Programmpaket kann auf allen Betriebssystemen verwendet werden. Es bringt Startdateien für Linux und Windows mit. Zip entpacken und Programm Starten.

* **MTPlayer-XX__Linux+Java__DATUM.zip**  
**MTPlayer-XX__Win+Java__DATUM.zip**  
Diese Programmpakete bringen die Java-Laufzeitumgebung mit und sind nur für das angegebene Betriebssystem: Linux oder Windows. Es muss kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung liegt im Ordner: "Java" und kommt von jdk.java.net). Zip entpacken und Programm starten.

* **MTPlayer-XX__Raspberry__DATUM.zip**  
Das ist ein Programmpaket, das auf einem Raspberry verwendet werden kann. Es muss ein aktueller Raspberry mit einer 64Bit CPU mit AArch64 Architektur sein. Zip entpacken und Programm Starten.

zum Download: [github.com/xaverW/MTPlayer/releases](https://github.com/xaverW/MTPlayer/releases)

<br />

Windows:  
Der VLC-Player muss installiert sein.  
Linux:  
Der VLC-Player und ffmpeg müssen installiert sein.  

## Installation
MTPlayer-XX__Windows==SETUP__DATUM.exe wird durch einen Doppelklick darauf installiert. Die anderen Versionen müssen nicht installiert werden, das Entpacken der heruntergeladenen ZIP-Datei ist quasi die Installation. Die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner MTPlayer...” ins Benutzerverzeichnis verschieben. Das Programm kann dann mit Doppelklick auf:  
Linux: “MTPlayer__Linux.sh” oder  
Windows: “MTPlayer__Windows.exe”  
gestartet werden.
<br />

## Infos
Weitere Infos zum Programm (Start und Benutzung) sind im Download-Paket enthalten oder können hier gefunden werden:
[www.p2tools.de/mtplayer/](https://www.p2tools.de/mtplayer/)  

## Website
[www.p2tools.de/mtplayer/]( https://www.p2tools.de/mtplayer/)

