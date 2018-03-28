
Das Programm MTPlayer durchsucht die Online-Mediatheken verschiedener 
Sender und listet die gefundenen Sendungen auf. Die Liste kann mit 
verschiedenen Filtern nach Beiträgen durchsucht werden. Mit einem 
Programm eigener Wahl können die Filme angesehen und aufgezeichnet werden.
Es lassen sich Abos anlegen und neue Beiträge automatisch herunterladen.



###########################################################################
###########################################################################
---------------------------------------------------------------------------
Installation
---------------------------------------------------------
*Windows:
MTPlayer wird nicht installiert; das Entpacken der heruntergeladenen 
ZIP-Datei ist quasi die Installation:
- die heruntergeladene ZIP-Datei in einen Ordner entpacken
- den entpackten Ordner ins Benutzerverzeichnis verschieben
- den eben verschobenen MTPlayer-Ordner öffnen, 
	die Datei "MTPlayer__Start.exe" ansteuern und per Rechtsklick in "Senden an"
	eine Verknüpfung auf den Desktop legen. Von dort aus kann MTPlayer 
	dann jeweils gestartet werden.
- die ZIP-Datei kann nach dem Entpacken gelöscht werden

*Linux
MTPlayer wird nicht installiert; das Entpacken der heruntergeladenen 
ZIP-Datei ist quasi die Installation.



###########################################################################
###########################################################################
---------------------------------------------------------------------------
Starten
---------------------------------------------------------
Für Windows (MTPlayer__Start.exe), Linux (MTPlayer__Linux.sh) sind eigene 

Startdateien enthalten, mit welchen MTPlayer direkt gestartet werden kann. 


Ansonsten kann man die Programmdatei auch so starten:
Windows: Doppelklick auf "MTPlayer.jar"
Linux (in der Konsole): java -jar MTPlayer.jar



===========================================================    
Starten mit zusätzlichen Parametern
---------------------------------------------------------

java -jar MTPlayer.jar [Pfad]
java -jar MTPlayer.jar c:\temp
java -jar MTPlayer.jar Einstellungen

Das Programm verwendet das Verzeichnis "Einstellungen" (relativ zur Programmdatei)
oder "c:\temp" für die Einstellungen.
Die Programmeinstellungen (Filmliste, Einstellungen, gesehene Filme) werden 
standardmäßig im Home-Verzeichnis (Benutzer-Verzeichnis) in einem Ordner ".mtplayer" 
gespeichert (beim Start ohne die Angabe eines Pfades).

java -jar MTPlayer.jar -v
Das Programm gibt nur die Versionsnummer aus.


--------------------------------------------------------------------------------
Die Windows-Startdatei "MediathekView__Start.exe"
wurde mit: ++ Launch4j ++ erstellt.
(Cross-platform Java application wrapper, http://launch4j.sourceforge.net )
--------------------------------------------------------------------------------
