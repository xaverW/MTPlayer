
=====================================================================
MTPlayer
=====================================================================


Das Programm MTPlayer durchsucht die Online-Mediatheken verschiedener
Sender und listet die gefundenen Sendungen auf. Die Liste kann mit
verschiedenen Filtern nach Beiträgen durchsucht werden. Mit einem
Programm eigener Wahl können die Filme angesehen und aufgezeichnet
werden. Es lassen sich Abos anlegen und neue Beiträge automatisch
herunterladen.


Website: 	https://www.p2tools.de/mtplayer/
Anleitung: 	https://www.p2tools.de/mtplayer/manual/start.html



=====================================================================
Systemvoraussetzungen
=====================================================================

* Unterstützt wird Windows und Linux. Das Programm
benötigt unter Windows und Linux eine aktuelle Java-VM ab Version 11
(Java11 oder höher, die darüber hinaus benötigten JavaFX-Runtimes
sind im Programm bereits für alle Betriebssysteme enthalten).

* Für Windws und Linux gibt es auch ein Programmpaket das die benötigte
Java-Laufzeitumgebung bereits mitbringt. Damit muss also auch kein Java
mehr installiert sein.

* Zum Ansehen und Aufzeichnen werden geeignete Zusatzprogramme benötigt.
MTPlayer ist vorbereitet für die Verwendung von VLC Media Player (zum
Abspielen und Aufzeichnen) sowie flvstreamer und FFmpeg (zum Aufzeichnen).

* Beim ersten Start von MTPlayer werden bereits zwei Programmsets mit
den drei Hilfsprogrammen VLC Media Player, flvstreamer und FFmpeg
angelegt. Damit können alle Filme angesehen und aufgezeichnet werden.


Windows
--------
* Für Windows muss nur der VLC Media Player installiert sein (die
anderen Programme werden mitgeliefert).

Linux
------
* Für Linux-Benutzer wird OpenJDK empfohlen (oder das Programmpaket
das Java bereits enthält und so ohne Java-Installation auskommt).

* Bei Linux muss der VLC Media Player, der flvstreamer (oder rtmpdump)
und FFmpeg (oder avconv) durch die Paketverwaltung installiert werden.
Bei OpenSuse müssen zusätzlich zum VLC Media Player auch die vlc-codecs
installiert werden.

* Wurden Alternativprogramme ausgewählt (rtmpdump, oder avconv) müssen
beim ersten Start diese ausgewählt werden oder später in den
Einstellungen zum Download evtl. angepasst werden (die Pfade dafür sind
dann wahrscheinlich: “/usr/bin/rtmpdump” und “/usr/bin/avconv”).



=====================================================================
Installation
=====================================================================

MTPlayer muss nicht installiert werden, das Entpacken der
heruntergeladenen ZIP-Datei ist quasi die Installation.


Windows
--------
* die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner
“MTPlayer...” ins Benutzerverzeichnis verschieben

* den eben entpackten MTPlayer-Ordner öffnen, die Datei
“MTPlayer__Windows.exe” ansteuern und per Rechtsklick in “Senden an”
eine Verknüpfung auf den Desktop legen. Von dort aus kann MTPlayer dann
jeweils gestartet werden (oder auch mit Doppelklick auf die Datei
“MTPlayer.exe” im Programmordner”).

* die ZIP-Datei kann nach dem Entpacken gelöscht werden


Linux
------
* die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner
“MTPlayer...” ins Benutzerverzeichnis verschieben

* mit einem Rechtsklick auf den Desktop eine neue Verknüpfung zu einem
Programm anlegen und dort die Startdatei im eben entpackten
MTPlayer-Ordner: “MTPlayer__Linux.sh” auswählen. Mit dieser Verknüpfung
(oder direkt mit Klick auf die Datei “MTPlayer__Linux.sh” kann dann das
Programm MTPlayer gestartet werden (der Vorgang kann sich für
unterschiedliche Distributionen etwas unterscheiden).

* die ZIP-Datei kann nach dem Entpacken gelöscht werden



=====================================================================
MTPlayer startet nicht:
=====================================================================

* Java ist nicht oder nicht in der richtigen Version installiert
(Java11, Java11+). Zum Java-Download: https://jdk.java.net/

* ZIP-Datei nicht entpackt: Die Programmdatei wurde direkt im
ZIP-Archiv doppelgeklickt. Die ZIP-Datei muss erst entpackt werden, dazu
sind alle Dateien aus dem ZIP-Archiv in ein beliebiges Verzeichnis zu
kopieren. Dort kann dann die Programmdatei "MTPlayer__Windows.exe"
oder "MTPlayer__Linux.sh" doppelgeklickt werden.

* Benötigte Dateien wurden aus dem Programm-Ordner gelöscht oder
die Hilfsprogramme (im Ordner "bin") fehlen, da Dateien aus dem
MTPlayer-Programmordner gelöscht oder verschoben wurden,
->dann eine Neuinstallation.

* Für Windows gibt noch weitere Infos im Ordner "Windows"

* Auf der Website sind noch weitere Infos zu finden:
Website: 	https://www.p2tools.de/mtplayer/
Anleitung: 	https://www.p2tools.de/mtplayer/manual/start.html


=====================================================================
=====================================================================

