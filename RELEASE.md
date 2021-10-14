# MTPlayer


<br />

**Release 11**


* Download-Chart und Download-Infos wurden überarbeitet
* Der Dialog: "Programm nach Downloads beenden" hat jetzt auch die Möglichkeit den Rechner anschließend herunterzufahren
* Tray: Das Programm kann ins Tray gelegt werden -> In den Einstellungen kann das Tray eingeschaltet werden
* Downloads: Es gibt einen weiteren Parameter: "%w" für die Website des Films, z.B. für den youtube-dl
* In den Tabellen kann mit der Leertaste seitenweise gescrollt werden
* Dateigröße bei Streams wird ermittelt
* Download über HTTPS kann bei Problemen abgeschaltet werden
* Programmupdate-Suche wurde überarbeitet
* Downloads können mit einer Startzeit gestartet werden
* In den Abos kann eine Startzeit für die Downloads des Abos vorgegeben werden
* Im Filmfilter (bei den Textfiltern) kann eine Zeitverzögerung zum Start der Suche, eingestellt werden
* Im Filmfilter (bei den Textfiltern) kann eingestellt werden, dass die Suche erst nach Eingabe von "Return" startet
* Es gibt einen "Tip des Tages"
* Es gibt einen neuen Filmfilter: Sendedatum
* Die gespeicherten Filterprofile können sortiert werden
* ...


<br />

**Release 10**

* Downloadchart: Daten bleiben auch beim Ändern der Anzeige erhalten
* Filmfilter erhält eine “zurück” Funktion
* Die interne Verarbeitung der Mediensammlung wurde verbessert, ist jetzt deutlich schneller bei sehr großen
  Mediensammlungen
* Beim Anlegen der Mediensammlung kann jetzt eine Mindestgröße vorgegeben werden, Dateien die kleiner sind werden nicht
  mehr aufgenommen
* Die Mediensammlung kann in eine Datei exportiert werden
* Beim Suchen in der History/erledigten Abos kann zwischen Thema oder Titel unterschieden werden
* Häufige Programmfunktionen können über Tastenkürzel aufgerufen werden und in den Einstellungen können die Tastenkürzel
  angepasst werden
* Wenn sich bei Downloads aus Abos doppelte Dateinamen ergeben, werden diese jetzt nummeriert
* flvstreamer wurde entfernt, gibt keine Downloads mehr dafür
* Downloadchart wurde überarbeitet
* Schriftgröße lässt sich anpassen
* Die Sortierreihenfolge der Downloads (Thema, Titel, Dateiname) wurde überarbeitet
* Der Add-Abo Dialog wurde überarbeitet
* Filmfilter Blacklist: “nur Blacklist” anzeigen, zur Kontrolle der Blacklist
* Download Continue Dialog wurde überarbeitet
* Einstellungen: Farben wurde überarbeitet
* Doppel-Klick auf den Tabellen-Header wird abgefangen
* Beschriftung Slider wurde überarbeitet
* Download Dialog wurde überarbeitet
* GUI Design (Filter, Tabellenspalten Ausrichtung, Filter Beschriftung, Shortcut, ..) wurde überarbeitet
* Start eines externen Programms über ein Shortcut möglich
* ...

**Release 9**

* Anordnung der Buttons (OK, Abbrechen, ..) passend zum Betriebssystem
* Beim Schließen des Config-Dialogs Filmliste nur neu laden, wenn Blacklist geändert wurde
* Die Möglichkeiten zum Anlegen von Verzeichnissen bei Abos wurde deutlich erweitert
* Möglichkeit, auch bei neuer Beta-Version einen Hinweis angezeigt zu bekommen
* Anpassungen bei der Suche nach einem Update / einer neuen BETA-Version
* Speicherverbrauch in den Dialogen verbessert
* Suchmöglichkeit in der Mediensammlung, der History und den erledigten Abos im Dialog "Einstellungen der
  Mediensammlung"
* Im Dialog "In der Mediensammlung suchen" bleibt die eingestellte Suche: "Mediensammlung" oder "History" erhalten
* Während das Ladens der Filmliste wird die Downloadbandbreite reduziert um die Filmliste schneller zu laden
* STRG-A in der Filmliste wird abgefangen wenn zu viele Filme in der Liste sind
* Anpassungen für den Sender RBTV
* Dialog "Über das Programm" wurde überarbeitet
* RegEx: Textfelder rot einfärben bei Fehlern
* Tabellenmenü jetzt auch bei leerer Tabelle verfügbar (für z.B. "neues Abo anlegen", ..)
* ToolTip des Downloads in der Downloadtabelle enthält die Fehlermeldung bei Downloadfehlern
* Downloadchart wurde überarbeitet
* ...

**Release 8**

* Dark Theme, die Programmoberfläche kann jetzt auch in einem "dark theme" angezeigt werden: Einstellungen->Farben
* Neuer Filter im Tab Abos: Suche eines Abos nach Abonamen
* Bei Filmabos kann jetzt der Suchzeitraum eingeschränkt werden: Filter "Zeitraum", es werden nur die Filme der letzten
  xx Tage gefunden
* Automode, wird das Programm mit dem Parameter -a oder --auto gestartet, wird die Filmliste aktualisiert, Abos gesucht,
  Downloads gespeichert und das Programm danach wieder beendet (für *nix user: Das Gui wird dabei geladen, es muss also
  auch X installiert sein!)
* Das Gui und die Programm-Menüs wurden an vielen Stellen überarbeitet und intuitiver angeordnet
* Der Filter "Zeitraum" wurde überarbeitet
* Filmfilter "Sender exakt" wurde komplett überarbeitet, jetzt kann man direkt aus dem Gui heraus einen Sender/oder
  mehrere Sender auswählen, dadurch wird die Bedienung deutlich vereinfacht, nicht mehr möglich ist die Suche mit RegEx
  im Senderfilter (ist hier aber ja auch nicht wirklich hilfreich?)
* Filter (Filme, Downloads, Abos) werden beim Programmstart wieder hergestellt
* Der gespeicherte Filter wird unterstrichen, wenn die Filtereinstellungen noch unverändert sind
* fx bug beim glyphcache management wird abgefangen
* Durch einen Fehler konnten keine Downloads (bei *nix) mit Leerzeichen im Pfad vorgenommen werden, geht wieder
* Verbessertes Importscript (für MV), findet jetzt z.B. auch die Abo-Min. und Abo-Max Dauer

**Release 7**

* Erkennung neuer Filme (wenn Filme nicht in jeder neuen Filmliste enthalten sind), wurde verbessert
* Ändern von Abonamen (auch mehrere gleichzeitig) und Umbenennen von Sets jetzt möglich
* Filmdownload löschen: Jetzt kann auch die Info- und Untertiteldatei mit gelöscht werden
* Tab Abo: Button zum Anlegen eines neuen Abos
* Tab Abo: Der Filter im Tab Filme kann nach einem Abo gesetzt werden und ein Abo kann aus dem aktuellen Filter im Tab
  Filme aktualisiert werden (damit lassen sich Abos leichter optimieren)
* Tab Filme, Tab Downloads: Zeilenhöhe der Tabelle lässt sich verkleinern -> Einstellungen
* Bookmarks für Filme lassen sich anlegen, alle Filme mit Bookmark können angezeigt werden
* Check beim Programmstart auf zu wenig Speicher
* Blacklist wird nach „Trefferzahl“ sortiert (ist etwas performanter)
* Anzeige der Filmlänge wurde auf Minuten geändert
* Beim Laden der Filmliste können bereits Filme mit Mindestlänge kleiner als X (z.B. <5 min.) gefiltert werden (dadurch
  kann die Filmliste deutlich verkleinert werden) -> Einstellungen
* Neuer Menüpunkt: In der Mediensammlung suchen (die Suche lässt sich so auch direkt starten)
* An vielen weiteren Stellen gab es kleine Verbesserungen und Fehler und Ungereimtheiten wurden entfernt

<br />


**Release 6**

* Tabelle Filme/Downloads/Abos: Die angezeigten Farben wurden überarbeitet
* Tabelle Download: Neuer Button zum Öffnen des Speicherordners
* Tabelle Download: Fehlerhafte Downloads können jetzt direkt aus der Tabelle neu gestartet werden
* Tabelle Abo: Es gibt eine weitere Suche über den Status: "eingeschaltet/ausgeschaltet"
* Abo: Es gibt jetzt ein neues Feld: "Beschreibung", auch eine Suche damit ist möglich, gedacht um Abos zu strukturieren
  oder mit Tags zu versehen
* In der Mediensammlung kann man versteckte Dateien ausschließen
* Einstellungen: Möglichkeit beim Laden der Filmliste Sender auszuschließen
* Einstellungen: Ein UserAgent kann angegeben werden
* Einstellungen: Im Tab Sets kann man jetzt die Standardsets direkt hier hinzufügen
* Hilfemenü: Möglichkeit zum Öffnen der Anleitung auf der Website
* Hilfemenü: Möglichkeit zum Öffnen der Logdatei
* Logdatei wurde übersichtlicher gestaltet und mit weiteren Informationen angereichert
* Einige Fehler konnten beseitigt werden
* Viele kleinere Änderungen die die Übersichtlichkeit und die Bedienung verbessern sollen (Danke für die vielen
  Vorschläge dazu)

<br />


**Release 5**

* In bestimmten Kon­s­tel­la­ti­onen kam es zu hoher Prozessorlast

<br />


**Release 4**

* Das Programm prüft und zeigt an, wenn es eine neue Filmliste gibt (alle 1/2h, Button "Filmliste" wird dann blau
  umrandet)
* Filminfo (unter dem Filmtab) ist beschreibbar (und damit kann die Infodatei die parallel zu einem Film gespeichert
  wird, angepasst werden). Die Änderungen gehen mit dem Neuladen einer Filmliste wieder verloren.
* Der Startdialog (beim ersten Programmstart) wurde überarbeitet
* Im Tab Abo (Spalte: Treffer) wird die Anzahl Filme die auf dieses Abo passen, angezeigt (damit kann man seine Abos
  optimieren)
* Die Verarbeitung für die Dateien History und erledigte Abos wurde überarbeitet
* Das Programm bekam einige optische Veränderungen und einige interne Verbesserungen und auch ein paar Fehler wurden
  bereinigt
* Habs geschafft auch eine Anleitung zu schreiben: https://www.p2tools.de/mtplayer/manual/

<br />


**Release 3**

* Das Programm ist nun unter Java >=8 (also Java8, Java9, Java10) lauffähig. Dazu musste einiges umgebaut werden und
  einige Gui-Elemente ausgetauscht werden. Die Optik hat sich desswegen etwas geändert, die Funktion ist aber erhalten
  geblieben.
* Bei den Downloads gibt es einen neuen Filter: "Download nicht gestartet", "Download wartet", "Download läuft"
* In der Blacklist können jetzt die Anzahl der Filme auf die ein Eintrag zutrifft, gezählt werden und so die Blacklist
  optimiert werden.
* Beim manuellem Anlegen eines Downloads, kann man sich jetzt einen Namen für den Downloadordner (Thema, Datum)
  vorschlagen lassen
* Viele kleinere Verbesserungen: Anzeige der Dialoge, Zeilenumbruch im Logfile unter Windows, Hilfetexte, Filterprofile,
  Mediensammlung (Anzeige der Filme pro Pfad jetzt auch bei den Internen Medien), ...
* Auch ein paar Probleme beim Anlegen des Downloadpfads und beim Reset der Programmsets wurden behoben

<br />


**Release 2**

* Mit dem Schalter "-h" lassen sich die möglichen Startparameter abfragen
* Das Laden der Filmliste und die anschließenden Aktivitäten z.B. beim Suchen der Downloads wurde optimiert
* Die History Liste speichert jetzt den ganzen Titel was das Suchen nach bereits gesehenen Beiträgen verbessert
* Beim Filter "URL" gabs Probleme wenn die URL bestimmte Zeichen (,:) enthielt
* Die Medienliste kann jetzt auch externe Medien verwalten. Es können Medien z.B. von USB-Laufwerken importiert werden.
  Es ist auch möglich diese Medien zu aktualisieren oder wieder zu löschen.
* Der Pfad der Konfigurationsdatei hat sich geändert (wer eine bereits vorhandene Konfigurationsdatei behalten will,
  braucht nur den bisherigen Ordner ".mtplayer" umbenennen, bei Windows wurde der führende Punkt der Probleme machen
  kann, entfernt und der Ordner wird als versteckt angelegt):  
  Windows: p2Mtplayer  
  Linux: .p2Mtplayer

<br />


**Release 1-81**

* Zur Programmausgabe in der Konsole wird jetzt auch ein Logfile geschrieben (in den Einstellungen kann angegeben
  werden, ob und wo es geschrieben werden soll)
* Das Format des Log (Konsole und Logfile) wurde übersichtlicher gestaltet
* Die Suche nach einem Programmupdate wurde geändert und auf die neue Website umgestellt

<br />


**Release 1-55**

* Es gibt einen neuen Filter (in "nicht anzeigen"): "Zukunft", damit werden Filme in der Zukunft nicht angezeigt.

<br />


**Release 1-50**

* Die Fehlermeldung "Windows: Fehlermeldung Softwareaktualisierung" beim Start durch fehlende Crypto-lib im System wird
  unterdrückt.  
  http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html  
  https://de.wikipedia.org/wiki/Java_Cryptography_Extension

<br />


**Release 1**

* Im Filter ist jetzt neben "," für ODER auch ":" für UND möglich (z.B. "Auto,Schiff" für Filme die entweder "Auto"
  oder "Schiff" im entsprechenden Feld enthalten, "Auto:Schiff" findet nur Filme die sowohl "Auto" als auch "Schiff" im
  gesuchten Feld enthalten).

* In den Filtern für "Sender" und "Thema" kann man jetzt auch mit freien Begriffen suchen.

* Es ist jetzt auch möglich nach Uhrzeit zu filtern und auch die Suche nach einer URL ist möglich. Auch können doppelte
  Filme (z.B. in ARD und BR) ausgeblendet werden. Beim Suchen nach Filmlänge kann ein min. und ein max. Wert vorgegeben
  werden.

* Filter kann man aus- und einblenden. Zur Suche von Filmen werden nur eingeblendete und benutzte Filter verwendet (auch
  Filter die eingeblendet aber leer sind, werden nicht ausgewertet). Die Suche wird damit beschleunigt.

* Es können beliebig viele eigene Filtereinstellungen gespeichert und wieder abgerufen werden.

* In der Blacklist kann man die Filmliste auf xx Tage beschränken (die Einstellung ist jetzt getrennt vom Filter
  möglich), was die Suche in der Filmliste deutlich beschleunigen kann.

* Das ganze Zusammenspiel zwischen "kompletter Filmliste - Blacklist - Filter - angezeigte Filme" wurde komplett
  umgebaut und ist so trotz der erweiterten Filtermöglichkeiten performanter.

* Ein Klick auf den Tab (z.B. Film) bringt den entsprechenden Tab in den Vordergrund, ein weiterer Klick blendet den
  Filter ein oder aus.

* Im Dialog "In der Mediensammlung suchen" kann man jetzt auch die Liste der bereits geladenen Abos durchsuchen. In
  diesem Dialog ist es möglich, durch einen Doppelklick auf ein Wort im Suchbegriff dieses frei zu stellen (z.B. wird
  nach dem Titel "Barcelona gegen Madrid ..." gesucht und man doppelklickt "Madrid", dann steht anschließend nur noch "
  Madrid" im Suchfeld).

* Die Filtermöglichkeiten zum Suchen von Abos wurden erweitert.

* Die Filmauflösung (HD, Hoch, Klein) für Abos kann jetzt direkt im Abo ausgewählt werden. Die Vorgabe im Set ist nur
  noch fürs Abspielen von Filmen oder für manuell gestartete Downloads zuständig.

* An allen wichtigen Einstellungen oder Schaltern ist ein "Hilfebutton" mit Erklärungen zu der Funktion.

<br />
<br />

**Links**

[www.p2tools.de/mtplayer/]( https://www.p2tools.de/mtplayer/)
