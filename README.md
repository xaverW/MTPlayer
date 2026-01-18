[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](http://www.gnu.org/licenses/gpl-3.0)

# MTPlayer

Das Programm MTPlayer ist eine Art Suchmaschine für Filme der Mediatheken der Öffentlich-Rechtlichen Sender. Es ist der Nachfolger meines Programms MediathekView.  Das Programm stellt eine Liste mit Links zu den Filmen zur Verfügung. Es ist möglich, diese URLs an externe Programme weiterzugeben. Mit diesen Programmen können dann diese Filme angesehen oder aufgezeichnet werden.
<br />

Die Arbeit dafür (Programm, Filmliste aktuell halten, ..) war für mich alleine nicht mehr zu leisten. Deswegen habe ich das Projekt als Community-Projekt gestartet. Die Filmlilste wird jetzt dort weiter gepflegt. MTPlayer verwendet auch diese Filmlilste.
<br />

## Infos
MTPlayer wurde ganz neu mit einem modernem GUI-Toolkit erstellt. Viele Vorschläge die ich von Usern erhalten habe, konnten deswegen damit umgesetzt werden. Es wurde an vielen Stellen verbessert und erweitert.  

Ein besonderes Augenmerk waren die Filter zum Suche der Filmen. Es gibt jetzt mehr Filter die man bei der Suche verwenden kann und die Filtereinstellungen können auf verschiedene Weise, gespeichert und wieder zurückgeholt werden. So kann viel gezielter nach Filmen gesucht werden.  

Im MTPlayer ist jetzt auch die ARD-Audiothek enthalten. Mit einer Live-Suche in den Mediatheken von ARD und ZDF können fehlende Beiträge vielleicht doch gefunden werden.  

Es lassen sich Abos anlegen, die dann automatisch Filme/Audios suchen und speichern. Auch die Abos profitieren bei der Suche nach den Beiträgen, von den erweiterten Filtern. Das Ausblenden ungewollter Filme lässt sich mit zwei Blacklists sehr gut an die eigenen Bedürfnisse anpassen und mit einer Mediensammlung für bereits geladene Filme können doppelte Downloads vermieden werden.
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

Unterstützt wird Linux, Windows und MacOs. 

Das Programm benötigt eine aktuelle Java-VM ab Version: Java 17.
Diese muss installiert sein oder man verwendet die Version mit enthaltenem Java. FX-Runtime bringt das Programm bereits mit und muss nicht installiert werden.

<br />

## Download
Das Programm wird in verschiedenen Paketen angeboten. Diese unterscheiden sich nur im “Zubehör”, das Programm selbst ist in allen Paketen identisch: 

* **MTPlayer-XX__Windows==SETUP__DATUM.exe**  
Mit diesem Programmpaket kann das Programm auf Windows installiert werden: Doppelklick und alles wird eingerichtet, auch ein Startbutton auf dem Desktop. Es muss auch kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung ist enthalten).

* **MTPlayer-XX__DATUM.zip**  
Das Programmpaket bringt nur das Programm und die benötigten Hilfsprogramme aber kein Java mit. Auf dem Rechner muss eine Java-Laufzeitumgebung ab Java17 installiert sein. Dieses Programmpaket kann auf allen Betriebssystemen verwendet werden. Es bringt Startdateien für Linux und Windows mit. Zip entpacken und Programm Starten.

* **MTPlayer-XX__Linux+Java__DATUM.zip**  
**MTPlayer-XX__Win+Java__DATUM.zip**  
Diese Programmpakete bringen die Java-Laufzeitumgebung mit und sind nur für das angegebene Betriebssystem: Linux oder Windows. Es muss kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung liegt im Ordner: "Java" und kommt von jdk.java.net). Zip entpacken und Programm starten.

* **MTPlayer-XX__Mac=mit=Java__DATUM.dmg**  
Das ist ein Programmpaket für macOS (Apple Silicon). Es muss auch kein Java auf dem System installiert sein. (Die Java-Laufzeitumgebung ist enthalten).

* **MTPlayer-XX__Raspberry__DATUM.zip**  
Das ist ein Programmpaket, das auf einem Raspberry verwendet werden kann. Java muss installiert sein und muss ein aktueller Raspberry mit einer 64Bit CPU mit AArch64 Architektur sein. Zip entpacken und Programm Starten.



zum Download: [github.com/xaverW/MTPlayer/releases](https://github.com/xaverW/MTPlayer/releases)

<br />

Windows, MacOs:  
Der VLC-Player (oder ein anderer Player) muss installiert sein.  
Linux:  
Der VLC-Player (oder ein anderer Player) und ffmpeg müssen installiert sein.  

## Installation
MTPlayer-XX__Windows==SETUP__DATUM.exe wird durch einen Doppelklick darauf installiert.  

Die Mac-Version ist ein DMG-Image, das eingebunden wird (mit Doppelklick) Die enthaltene App wird dann ins Programmverzeichnis kopiert.  

Die anderen Versionen müssen nicht installiert werden, das Entpacken der heruntergeladenen ZIP-Datei ist quasi die Installation. Die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner MTPlayer...” ins Benutzerverzeichnis verschieben. Das Programm kann dann mit Doppelklick auf:  
Linux: “MTPlayer__Linux.sh” oder  
Windows: “MTPlayer__Windows.exe”  
gestartet werden.
<br />

## Infos
Weitere Infos zum Programm (Start und Benutzung) sind im Download-Paket enthalten oder können hier gefunden werden:
[www.p2tools.de/mtplayer/](https://www.p2tools.de/mtplayer/)  

## Website
[www.p2tools.de/mtplayer/]( https://www.p2tools.de/mtplayer/)

