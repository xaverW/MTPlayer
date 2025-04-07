# MTPlayer

**Version 20**

* Live-Suche ZDF wurde an die geänderte Mediathek vom ZDF angepasst
* Die Film-Info Datei kann auch ohne Download erstellt werden: Tabelle Filme->Maus-Kontextmenü->Film
* Die Abläufe beim Laden der Filmliste wurden überarbeitet
* ...

**Version 19**

* Eine neue Programmversion wurde nicht in jedem Fall gemeldet

**Version 18**

* Die Nummerierung der Abos wurde korrigiert
* Das Suchen der Downloads läuft jetzt im Hintergrund ab und blockiert so das Programm nicht mehr.
* Der Fortschritt beim Suchen (Downloads, Filtern der Blacklist) wird jetzt an verschiedenen Stellen im Programm angezeigt
* Tabelle Blacklist: Die Spalten-Einstellungen werden gespeichert
* Einstellungen Blacklist: Eingestellter Filter in der Tabelle wurde nicht zurückgesetzt
* Einstellungen Blacklist: Doppelklick auf eine Blacklist-Tabellen-Zeile: Es wird dann das entsprechende Feld unter der Tabelle selektiert
* Tabelle Filme: Anzeige der neuen Filme (Schriftfarbe) hatte gelegentlich Probleme, wurde überarbeitet
* Schwarz-Weiß Anzeige der Programmoberfläche (Einstellungen->Farben)
* Das Umschalten "Darkmode, Schwarz-Weiß Anzeige" klappt jetzt auch ohne Programm-Neustart
* Der "Quitt-Dialog" wurde überarbeitet (-> Programm minimieren)
* Filmfilter, Filterprofile wurde etwas überarbeitet: Die Standard-Filterprofile können jetzt auch angefügt werden (ohne die vorhandenen zu löschen)
* Abofilter: Es gibt einen neuen Filter: "Filtertext", der in den Textfeldern sucht
* Anzeige der Filter in externem Fenster möglich
* Neues ShortCut: Alt+m -> GUI minimieren
* Set: Das Neuanlegen oder Ändern wurde überarbeitet für Programme (vlc, ffmpeg) die nicht im Standardpfad liegen
* Programm minimiert oder maximiert starten überarbeitet
* Anpassungen für den Raspberry
* Filme markieren: Damit können Filme nach eigenen Vorgaben markiert werden, so können z.B. Filme die "Gebärdensprache" im Titel haben, markiert werden. Im Filmfilter gibt es den neuen Punkt: "Anzeigen->Markiert" der dann die markierten (z.B. mit "Gebärdensprache") filtert.
* Start-Dialog überarbeitet
* Beim Programmstart werden verwendete Programme/Pfade in den Speichern-Sets überprüft
* Nach einem Download wird er direkt in die Medien-Liste (wenn dort gespeichert) übernommen
* Ersetzungstabelle: Die Einträge können ein- und ausgeschaltet werden
* Ersetzungstabelle: Bei einem Treffer, kann das weitere Abarbeiten der Liste, abgebrochen werden
* Fehler beim Ändern der Diacritics, behoben
* ...

**Version 17**

* Neuer Tab: Live-Suche (kann über einen Menü-Button oder das Programm-Menü ein- und ausgeblendet werden). Hier kann in den Mediatheken von ARD und ZDF nach Filmen gesucht werden. Es kann auch mit einer URL einer Film-Seite aus der Mediathek ein Film angelegt werden.
* Tabelle Filme: Es gibt eine neue Spalte für "Doppelte"
* Doppelte Filme können beim Laden der Filmliste ausgeschlossen werden (Achtung: Bei Abos muss dann evtl. der Sender angepasst werden.)
* Beim Markieren von doppelten Filmen, kann die Reihenfolge der Sender vorgegeben werden (also welche Sender als "Doppelt" markiert werden). Es kann weiter auch noch festgelegt werden, dass zur gleichen URL auch Thema/Titel gleich sein muss
* Blacklist: Es können jetzt auch doppelte Filme ausgeblendet werden
* Filme mit dem Untertitel im "Filmtitel" können jetzt auch als UT markiert werden und es gibt einen Eintrag im Kontextmenü zum Download der Untertitel-Dateien
* Filmfilter: "Thema exakt", die Liste der Themen kann mit Texteingabe gefiltert werden
* Filmfilter: Textfilter werden in einer eigenen ComboBox gesammelt und werden gespeichert. Bei der Auswahl eines der Filter, werden die angezeigten Filter *NICHT* geändert, es werden nur die Inhalte in die Textfilter eingetragen.
* Alle Textfilter im Programm werden jetzt gespeichert, es gibt eine ComboBox in der sie gelistet werden und ausgewählt/gelöscht werden können
* Im Filminfo-Dialog sind jetzt alle URLs (niedrig, hoch, hd) enthalten und können kopiert werden
* Selektierte Tabellenzeile (Filme) nach dem Filtern überarbeitet und es gibt eine neue Einstellung: Nichts machen, es wird nichts ausgewählt
* Die Filmliste wird jetzt in komprimierter Form gespeichert, (das PlugIn im TV-Browser läuft jetzt damit)
* Im Tab Download gibt es einen neuen Info-Tab der die Downloadfehler anzeigt und die Anzeige bei Downloadfehlern wurde erweitert
* Loginfos bei Downloads wurden erweitert
* Überholte Einstellungen beim Download: "Nur ein Download pro Server" entfernt
* Für Pfad- und Dateinamen von Downloads können in der Ersetzungstabelle (Einstellungen->Download) jetzt auch RegEx verwendet werden
* Max. Bandbreite wurde überarbeitet: Max (10 MByte/s) ist jetzt 10x der alte Wert und der Max-Wert ist jetzt "0", also links, so kann der Max-Wert auch eingestellt werden
* Bei der Startzeit von Downloads kann jetzt die Zeit von 00:00 bis 23:45 Uhr angegeben werden. Liegt die Zeit in der Vergangenheit wird der Download morgen um diese Zeit gestartet.
* In Abos können jetzt ein eigener Pfad/Dateiname zum Speichern vorgegeben werden. Wird das gesetzt, werden die Vorgaben aus dem Set überschrieben.
* Auto-Mode überarbeitet: Downloads werden alle sofort gestartet (unabhängig von der Startzeit)
* ToolTips in den Tabellen Filme/Live/Downloads/Abos können im Kontextmenü der Tabelle ein- und ausgeschaltet werden
* Gibt's neue Funktionen im Programm, zeigt ein Dialog beim Start dies an
* Der Sender "Radio Bremen TV" wurden mit dem Sendernamen "Radio Bremen TV", "rbtv" und "RBTV" gelistet und die werden jetzt zusammengefasst zu "RBTV".
* Es gibt ein neues ShortCut zum Anzeigen der Blacklist-Einstellungen: ALT+B
* In den Programmeinstellungen->Proxy kann ein Proxy-Server eingerichtet werden
* Erledigte Abos / History: Es kann jetzt auch eine Auswahl gelöscht werden
* Das Prgramm startet jetzt auch unter Win maximiert, wenn es so beendet wurde
* In den Einstellungen kann vorgegeben werden, dass das Programm immer maximiert startet, egal wie es beendet wurde
* Dialog "Warten aufs Beenden" hat jetzt eine neue Animation mit weniger Prozessor-Last
* Rechte Menüleiste kann ausgeblendet werden
* FR kann als GEO-Standort eingestellt werden
* Fehler mit einem eigenem Pfad für das Logfile wurde behoben
* Farben im Darkmode wurden etwas angepasst
* About Dialog enthält jetzt auch den Speicherverbrauch des Programms
* Tips (Menü->Hilfe) wurden an die aktuelle Version angepasst und erweitert
* GUI-Toolkit aktualisiert
* ffmpeg für Win aktualisiert
* Java für die Versionen "mit Java" aktualisiert
* ...


**Version 16**

* Es gab einen Fehler wenn der Film bei gespeicherten Downloads nicht mehr in der Filmliste ist
* Filmfiter Sendezeit: Gui überarbeitet
* ...


**Version 15**

* Die Java-Version wurde auf mind. Java 17 angehoben
* In den Tabellen (Filme, Downloads, Abos) gibt es einen Tooltip mit den Infos: Thema, Titel
* Max. Download Bandbreite, max Anzahl Downloads kann jetzt auch in den Einstellungen eingestellt werden
* Ist die max. Download Bandbreite nicht "MAX", wird das in der Statusbar angezeigt
* Performance Optimierungen beim Download
* Nach dem Anlegen eines Abos/Downloads wird die Programmkonfiguration gespeichert
* Die Infopanes unter den Tabellen (Filme, Downloads, Abos) können abgerissen und als Extrafenster angezeigt werden (Button "kleines Dreieck)
* Fürs Löschen gibts jetzt überall eine Undo-Funktion: Downloads, Abos, Blacklist, Replacelist, Mediadata, Sets
* Button für den Dark Mode im Progamm-Menü, und durch Rechtsklick auf das Programmmenü
* Filme zum Abspielen starten: Es können jetzt auch gleich mehrere Filme gestartet werden
* Dialog Abo-Ändern: Anzeige der Anzahl von Treffern
* Tab Filme: Infopane für die Mediensammlung hinzugekommen
* Einstellungen: Beim Anlegen von Mediendaten können sie jetzt auch eingesehen werden durch einen neuen Tab für die Mediendaten
* Fast Filmfilter: Es gibt jetzt einen "schnellen Filmfilter" in der Toolbar, er kann ein- und ausgeblendet werden
* Downloadeinschränkungen (Max. Downloads) wurden entfernt
* Gui-Elemente überarbeitet: RangeSlider, Notification
* Suche in der Filmliste erweitert: Nach der Suche immer die erste Tabellenzeile auswählen (-> Einstellungen der Suche)
* Neues Tastenkürzel: ctrl+w -> Programmfenster im Bildschirm zentrieren
* Notifications überarbeitet: Film starten, Ordner öffnen
* Add Download Dialog überarbeitet: Liste der Ordner kann gelöscht werden, ...
* Fehlermeldungen im Log bei Downloadfehlern erweitert
* Bookmark: Zusätzlicher Button in der Tabelle "Filme" zum Setzen/Löschen eines Bookmarks
* Download-Infos (Fortschrittsanzeige, ...) für den yt-dlp downloader
* Chart für die Download-Bandbreite erweitert: Summe aller Downloads und die Downloads können jetzt im selben Chart gezeichnet werden -> Kontext-Menü des Chart
* Neuer Programmschalter: -s, ist zum Testen, es wird dann immer ein Programmupdate angezeigt
* Film-Info Dialog überarbeitet
* "In die Zwischenablage kopieren" wurde an einigen Stellen hinzugefügt (Download-Dialog, "Über dieses Programm" Dialog)
* Beim Anlegen eines Abos aus dem Filmfilter: Der Vorschlag des Aboname überarbeitet
* Tabelle Download, Kontextmenü: "Abo deaktivieren" kam hinzu
* Dialog Download anlegen/ändern: Es können jetzt auch mehrere Downloads (im Tab Download) geändert werden
* Dialog Download/Abo anlegen/ändern: Das Feld "Für alle ändern" ist jetzt ein Button, der alle ein/ausschaltet
* Dialog Abo anlegen/ändern: Mehrere Abos können jetzt auch einzeln geändert werden
* Dialog Download anlegen: Die CheckBoxen "alle" werden mit der letzten Einstellung wieder vorbelegt
* Tab Abo: Aboinfos hinzugekommen
* Dateigröße für m3u8-URLs: Wird jetzt beim Downloadstart ermittelt
* ...


**Version 14**
* Markierung der gesehenen Filme korrigiert
* In den Einstellungen kann eine Lock-Datei eingeschaltet werden: Es wird dann sichergestellt, dass nur eine Instanz vom Programm läuft
* Es wurden neue ShortCuts und Programm-Schalter hinzugefügt -> Einstellungen
* ShortCuts über Menü->Hilfe direkt erreichbar
* Möglichkeit die Filmliste automatisch zu aktualisieren wenn es eine neue gibt -> Einstellungen
* Blacklist-Dialog und die Filmfilter wurden ausgebaut, es ist jetzt möglich darin zu suchen, Filter vom Filmfilter in die Blacklist zu schieben und umgekehrt, es gibt neue Filter und Filter können ein- und ausgeschaltet werden
* Blacklist: Hat jetzt ein Erstelldatum, so kann man sehen, wann der Eintrag erstellt wurde
* Infobereich in Filme/Downloads/Abos wurde erweitert
* Eine Erweiterung beim Filtern (in allen Filtern im Programm): Wörtlicher Suchtext kann in " angegeben werden. Dann wird alles gefunden, was im durchsuchten Bereich den Inhalt zwischen den " enthält. Hier sind dann auch "," und ":" erlaubt. Der gesamte Suchtext muss aber in " eingeschlossen werden.
* Statusbar: Der Zustand der Downloads wird mit einem Farbpunkt angezeigt: Es laufen keine (schwarz), alles OK (grün), es sind fehlerhafte enthalten (rot) und der Punkt blinkt, wenn noch nicht gestartete Downloads dabei sind. Dies kann in den Einstellungen oder im Kontextmenü derselben ein- und ausgeblendet werden
* Statusbar: Diese kann in den Einstellungen oder im Kontextmenü derselben ausgeblendet und angepasst werden
* Tabelle Filme: Wird der selektierte Film entfernt (zur Blacklist hinzufügen, als gesehen markieren), dann wird jetzt der Film davor selektiert
* Der Dialog beim Beenden während noch Downloads laufen wurde überarbeitet: Hier kann jetzt der BS-Befehl der nach dem Beenden ausgeführt wird, angepasst werden und das Handling des Dialogs wurde verbessert
* Stopp oder Löschen eines Downloads: In einem Dialog wird jetzt abgefragt ob der Stopp abgebrochen werden soll und ob die angefangene Filmdatei auch gelöscht werden soll, die Entscheidung kann man "merken" damit der Dialog nicht mehr erscheint
* Die Mediensammlung wurde erweitert: Es gibt einen neuen Infotab bei den Downloads, es werden die Medien und die erledigten Abos darin gelistet, das schnelle Suchen darin wird damit ermöglicht
* Mediensammlung/erledigte Abos/History: Zur Suche darin können der Filmtitel (Suchbegriff) "aufbereitet" werden, es werden unnütze Infos daraus entfernt -> Einstellungen
* Mediensammlung: Ein Doppelklick auf ein Wort im Suchtext stellt es frei
* ...


**Version 13**
* Das GUI wurde überarbeitet
* Es kann ein eigenes Programm-Icon verwendet werden -> Einstellungen
* Shortcuts wurden hinzugefügt z.B. Ctrl+G "Film gesehen" und Ctrl+Shift+G "Film ungesehen"
* Größerer Umbau beim Laden-/Neuladen einer Filmliste
* Das Laden der Filmliste beim Programmstart wurde überarbeitet: Wenn zu alt, wird sofort eine neue Filmliste geladen wodurch das Programm schneller startet
* Gibts eine neue Filmliste? Das wird jetzt mit einem großen Rahmen um den Ladebutton "Filmliste" angezeigt
* Die Anzeige des Filmlisten-Alters korrigiert
* MediathekView Einstellungen (Abos, Blacklist) können importiert werden -> Menü->Hilfe->Import
* HTTPS Zertifikat-Fehler: Es wird jetzt ein Dialog angezeigt, ob man trotzdem laden will
* Abo-Dialog wurde überarbeitet
* In Filtern (Film, Blacklist, Abo) können jetzt auch Begriffe ausgeschlossen werden: "!:"
* Die möglichen Parameter der Sets wurden erweitert, jetzt sind z.B. Button möglich, die die Suche nach dem Filmtitel bei Google, YouTube, .. ermöglichen
* Eine zweite Filterliste wurde hinzugefügt, mit der kann die Filmliste bereits beim Download gefiltert werden z.B. "Gebärdensprache" ausschließen -> Einstellungen
* Beim Hinzufügen von neuen Einträgen in die Blacklist, wird dann der letzte Film "davor" selektiert
* Das Anlegen von Blacklist-Einträgen wurde überarbeitet
* Dialog zum Ändern der Blacklist im Filmfilter beim Schalter zum ein-/ausschalten der Blacklist
* Die Verarbeitung der Blacklist wurde verbessert
* ...


**Version 12**
*    Die interne Verarbeitung der Daten/Einstellungen wurde komplett überarbeitet
*    Gelöschter Download: Der kann jetzt zurückgenommen werden (-> Menü)
*    Im Filmfilter wurden fehlende Sender nachgetragen
*    Die Möglichkeiten der gespeicherten Filmfilter wurden erweitert
*    Diakritische Zeichen im Filmtitel/Thema können jetzt umgewandelt werden (z.B. ń ǹ ň ñ > n) -> Einstellungen
*    Für Abos gibt es jetzt ein Datum, an dem das Abo angelegt wurde
*    Für das Systemtray kann jetzt ein eigenes Icon verwendet werden -> Einstellungen
*    Angefanene Downloads: “Neu starten/weiterführen”, das kann jetzt festgesetzt werden, so dass die Frage nicht mehr kommt -> Einstellungen
*    Es gibt einen weiteren Filter->Status bei den Downloads: “Gestartet (läuft oder wartet)”
*    Filmfilter, Textfilter: Der Umgang beim Eintippen wurde verbesser
*    Filmfilter: Die Textfilter haben jetzt eine History
*    Mediensammlung: Die Pfade der Mediensammlungen können nachträglich geändert werden
*    Mediensammlung: Die Mediensammlung kann in eine json-Datei exportiert werden
*    Tab Filme/Download: Titel/Thema kann in die Zwischenablage kopiert werden (Kontextmenü Filmliste/Downloadliste)
*    An vielen Stellen wurden kleinere Änderungen/Erweiterungen in der Funktion oder dem Design vorgenommen
*    …


**Version 11**
*    Download-Chart und Download-Infos wurden überarbeitet
*    Der Dialog: “Programm nach Downloads beenden” hat jetzt auch die Möglichkeit den Rechner anschließend herunterzufahren
*    Tray: Das Programm kann ins Tray gelegt werden -> In den Einstellungen kann das Tray eingeschaltet werden
*    Downloads: Es gibt einen weiteren Parameter: “%w” für die Website des Films, z.B. für den youtube-dl
*    In den Tabellen kann mit der Leertaste seitenweise gescrollt werden
*    Dateigröße bei Streams wird ermittelt
*    Download über HTTPS kann bei Problemen abgeschaltet werden
*    Programmupdate-Suche wurde überarbeitet
*    Downloads können mit einer Startzeit gestartet werden
*    In den Abos kann eine Startzeit für die Downloads des Abos vorgegeben werden
*    Im Filmfilter (bei den Textfiltern) kann eine Zeitverzögerung zum Start der Suche, eingestellt werden
*    Im Filmfilter (bei den Textfiltern) kann eingestellt werden, dass die Suche erst nach Eingabe von “Return” startet
*    Es gibt einen “Tip des Tages”
*    Es gibt einen neuen Filmfilter: Sendedatum
*    Die gespeicherten Filterprofile können sortiert werden
*    …


**Version 10**
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


**Version 9**
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


**Version 8**
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
* ...


**Version 7**
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
* ...


**Version 6**
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
* ...


**Version 5**
* In bestimmten Kon­s­tel­la­ti­onen kam es zu hoher Prozessorlast
* ...


**Version 4**
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
* ...


**Version 3**
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
* ...


**Version 2**
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
* ...


**Version 1-81**
* Zur Programmausgabe in der Konsole wird jetzt auch ein Logfile geschrieben (in den Einstellungen kann angegeben
  werden, ob und wo es geschrieben werden soll)
* Das Format des Log (Konsole und Logfile) wurde übersichtlicher gestaltet
* Die Suche nach einem Programmupdate wurde geändert und auf die neue Website umgestellt
* ...


**Version 1-55**
* Es gibt einen neuen Filter (in "nicht anzeigen"): "Zukunft", damit werden Filme in der Zukunft nicht angezeigt.
* ...


**Version 1-50**
* Die Fehlermeldung "Windows: Fehlermeldung Softwareaktualisierung" beim Start durch fehlende Crypto-lib im System wird
  unterdrückt.  
  http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html  
  https://de.wikipedia.org/wiki/Java_Cryptography_Extension
* ...


**Version 1**
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
* ...


**Links**

[www.p2tools.de/mtplayer/]( https://www.p2tools.de/mtplayer/)
