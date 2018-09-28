## MTPlayer

**Release 5-0**

* In bestimmten Kon­s­tel­la­ti­onen kam es zu hoher Prozessorlast


<br />

**Release 4-0**

* Das Programm prüft und zeigt an, wenn es eine neue Filmliste gibt (alle 1/2h, Button "Filmliste" wird dann blau umrandet)
* Filminfo (unter dem Filmtab) ist beschreibbar (und damit kann die Infodatei die parallel zu einem Film gespeichert wird, angepasst werden). Die Änderungen gehen mit dem Neuladen einer Filmliste wieder verloren.
* Der Startdialog (beim ersten Programmstart) wurde überarbeitet
* Im Tab Abo (Spalte: Treffer) wird die Anzahl Filme die auf dieses Abo passen, angezeigt (damit kann man seine Abos optimieren)
* Die Verarbeitung für die Dateien History und erledigte Abos wurde überarbeitet
* Das Programm bekam einige optische Veränderungen und einige interne Verbesserungen und auch ein paar Fehler wurden bereinigt
* Habs geschafft auch eine Anleitung zu schreiben: https://www.p2tools.de/mtplayer/manual/


<br />

**Release 3-0**

* Das Programm ist nun unter Java >=8 (also Java8, Java9, Java10) lauffähig. Dazu musste einiges umgebaut werden und einige Gui-Elemente ausgetauscht werden. Die Optik hat sich desswegen etwas geändert, die Funktion ist aber erhalten geblieben.
* Bei den Downloads gibt es einen neuen Filter: "Download nicht gestartet", "Download wartet", "Download läuft"
* In der Blacklist können jetzt die Anzahl der Filme auf die ein Eintrag zutrifft, gezählt werden und so die Blacklist optimiert werden.
* Beim manuellem Anlegen eines Downloads, kann man sich jetzt einen Namen für den Downloadordner (Thema, Datum) vorschlagen lassen
* Viele kleinere Verbesserungen: Anzeige der Dialoge, Zeilenumbruch im Logfile unter Windows, Hilfetexte, Filterprofile, Mediensammlung (Anzeige der Filme pro Pfad jetzt auch bei den Internen Medien), ...
* Auch ein paar Probleme beim Anlegen des Downloadpfads und beim Reset der Programmsets wurden behoben


<br />

**Release 2-0**

* Mit dem Schalter "-h" lassen sich die möglichen Startparameter abfragen
* Das Laden der Filmliste und die anschließenden Aktivitäten z.B. beim Suchen der Downloads wurde optimiert
* Die History Liste speichert jetzt den ganzen Titel was das Suchen nach bereits gesehenen Beiträgen verbessert
* Beim Filter "URL" gabs Probleme wenn die URL bestimmte Zeichen (,:) enthielt
* Die Medienliste kann jetzt auch externe Medien verwalten. Es können Medien z.B. von USB-Laufwerken importiert werden. Es ist auch möglich diese Medien zu aktualisieren oder wieder zu löschen.
* Der Pfad der Konfigurationsdatei hat sich geändert (wer eine bereits vorhandene Konfigurationsdatei behalten will, braucht nur den bisherigen Ordner ".mtplayer" umbenennen, bei Windows wurde der führende Punkt der Probleme machen kann, entfernt und der Ordner wird als versteckt angelegt):  
Windows: p2Mtplayer  
Linux: .p2Mtplayer


<br />

**Release 1-81**

* Zur Programmausgabe in der Konsole wird jetzt auch ein Logfile geschrieben (in den Einstellungen kann angegeben werden, ob und wo es geschrieben werden soll)
* Das Format des Log (Konsole und Logfile) wurde übersichtlicher gestaltet
* Die Suche nach einem Programmupdate wurde geändert und auf die neue Website umgestellt


<br />

**Release 1-55**

* Es gibt einen neuen Filter (in "nicht anzeigen"): "Zukunft", damit werden Filme in der Zukunft nicht angezeigt.


<br />

**Release 1-50**

* Die Fehlermeldung "Windows: Fehlermeldung Softwareaktualisierung" beim Start durch fehlende Crypto-lib im System wird unterdrückt.  
http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html  
https://de.wikipedia.org/wiki/Java_Cryptography_Extension


<br />

**Release 1**

* Im Filter ist jetzt neben "," für ODER auch ":" für UND möglich (z.B. "Auto,Schiff" für Filme die entweder "Auto" oder "Schiff" im entsprechenden Feld enthalten, "Auto:Schiff" findet nur Filme die sowohl "Auto" als auch "Schiff" im gesuchten Feld enthalten).

* In den Filtern für "Sender" und "Thema" kann man jetzt auch mit freien Begriffen suchen.

* Es ist jetzt auch möglich nach Uhrzeit zu filtern und auch die Suche nach einer URL ist möglich. Auch können doppelte Filme (z.B. in ARD und BR) ausgeblendet werden. Beim Suchen nach Filmlänge kann ein min. und ein max. Wert vorgegeben werden.

* Filter kann man aus- und einblenden. Zur Suche von Filmen werden nur eingeblendete und benutzte Filter verwendet (auch Filter die eingeblendet aber leer sind, werden nicht ausgewertet). Die Suche wird damit beschleunigt.
  
* Es können beliebig viele eigene Filtereinstellungen gespeichert und wieder abgerufen werden.

* In der Blacklist kann man die Filmliste auf xx Tage beschränken (die Einstellung ist jetzt getrennt vom Filter möglich), was die Suche in der Filmliste deutlich beschleunigen kann.

* Das ganze Zusammenspiel zwischen "kompletter Filmliste - Blacklist - Filter - angezeigte Filme" wurde komplett umgebaut und ist so trotz der erweiterten Filtermöglichkeiten performanter.

* Ein Klick auf den Tab (z.B. Film) bringt den entsprechenden Tab in den Vordergrund, ein weiterer Klick blendet den Filter ein oder aus.

* Im Dialog "In der Mediensammlung suchen" kann man jetzt auch die Liste der bereits geladenen Abos durchsuchen. In diesem Dialog ist es möglich, durch einen Doppelklick auf ein Wort im Suchbegriff dieses frei zu stellen (z.B. wird nach dem Titel "Barcelona gegen Madrid ..." gesucht und man doppelklickt "Madrid", dann steht anschließend nur noch "Madrid" im Suchfeld).

* Die Filtermöglichkeiten zum Suchen von Abos wurden erweitert.

* Die Filmauflösung (HD, Hoch, Klein) für Abos kann jetzt direkt im Abo ausgewählt werden. Die Vorgabe im Set ist nur noch fürs Abspielen von Filmen oder für manuell gestartete Downloads zuständig.

* An allen wichtigen Einstellungen oder Schaltern ist ein "Hilfebutton" mit Erklärungen zu der Funktion.


<br />
<br />

**Links**

[www.p2tools.de/mtplayer/]( https://www.p2tools.de/mtplayer/)
