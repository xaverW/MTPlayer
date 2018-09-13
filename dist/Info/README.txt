
MTPlayer
=========


Das Programm MTPlayer durchsucht die Online-Mediatheken verschiedener 
Sender und listet die gefundenen Sendungen auf. Die Liste kann mit 
verschiedenen Filtern nach Beiträgen durchsucht werden. Mit einem 
Programm eigener Wahl können die Filme angesehen und aufgezeichnet werden.
Es lassen sich Abos anlegen und neue Beiträge automatisch herunterladen.


Website: https://www.p2tools.de/mtplayer/
Anleitung: https://www.p2tools.de/mtplayer/manual/start.html



Systemvoraussetzungen
=====================

    * Unterstützt wird Windows (Vista, 7, 8, 10) und Linux. Das Programm benötigt unter Windows und Linux eine aktuelle Java-VM ab Version: 1.8 (= Java 8, Java 9, Java 10).

    * Zum Ansehen und Aufzeichnen werden geeignete Zusatzprogramme benötigt. MTPlayer ist vorbereitet für die Verwendung von VLC Media Player (zum Abspielen und Aufzeichnen) sowie flvstreamer und FFmpeg (zum Aufzeichnen).

    * Beim ersten Start von MTPlayer werden bereits zwei Programmsets mit den drei Hilfsprogrammen VLC Media Player, flvstreamer und FFmpeg angelegt. Damit können alle Filme angesehen und aufgezeichnet werden.

	
	Windows

    * Für Windows muss nur der VLC Media Player installiert sein (die anderen Programme werden mitgeliefert).

	
	Linux

    * Für Linux-Benutzer wird OpenJDK8 empfohlen. Außerdem benötigen Linux Benutzer die aktuelle Version von JavaFX (OpenJFX). OpenJFX ist aber nur für OpenJDK8 ohne Probleme zu installieren. Soll es Java 10 sein, wäre das Oracle Java SE 10 eine Alternative (und das bringt JavaFX schon mit).

    * Bei Linux muss der VLC Media Player, der flvstreamer (oder rtmpdump) und FFmpeg (oder avconv) durch die Paketverwaltung installiert werden. Bei OpenSuse müssen zusätzlich zum VLC Media Player auch die vlc-codecs installiert werden.

    * Wurden Alternativprogramme ausgewählt (rtmpdump, oder avconv) müssen beim ersten Start diese ausgewählt werden oder später in den Einstellungen zum Download evtl. angepasst werden (die Pfade dafür sind dann wahrscheinlich: “/usr/bin/rtmpdump” und “/usr/bin/avconv”).

    
    
Installation
============

MTPlayer muss nicht installiert werden, das Entpacken der heruntergeladenen ZIP-Datei ist quasi die Installation.

	Windows

    * die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner “MTPlayer” ins Benutzerverzeichnis verschieben
    * den eben entpackten MTPlayer-Ordner öffnen, die Datei “MTPlayer.exe” ansteuern und per Rechtsklick in “Senden an” eine Verknüpfung auf den Desktop legen. Von dort aus kann MTPlayer dann jeweils gestartet werden (oder auch mit Doppelklick auf die Datei “MTPlayer.exe” im Programmordner”.
    * die ZIP-Datei kann nach dem Entpacken gelöscht werden

	
	Linux

    * die heruntergeladene ZIP-Datei entpacken und den entpackten Ordner “MTPlayer” ins Benutzerverzeichnis verschieben
    * mit einem Rechtsklick auf den Desktop eine neue Verknüpfung zu einem Programm anlegen und dort die Startdatei im eben entpackten MTPlayer-Ordner: “MTPlayer.sh” auswählen. Mit dieser Verknüpfung (oder direkt mit Klick auf die Datei “MTPlayer.sh” kann dann das Programm MTPlayer gestartet werden (der Vorgang kann sich für unterschiedliche Distributionen etwas unterscheiden).
    * die ZIP-Datei kann nach dem Entpacken gelöscht werden



--------------------------------------------------------------------------------
Die Windows-Startdatei "MediathekView__Start.exe"
wurde mit: ++ Launch4j ++ erstellt.
(Cross-platform Java application wrapper, http://launch4j.sourceforge.net )
--------------------------------------------------------------------------------
