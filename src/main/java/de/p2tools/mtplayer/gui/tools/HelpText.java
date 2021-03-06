/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.p2tools.mtplayer.gui.tools;

import de.p2tools.p2Lib.P2LibConst;

public class HelpText {

    public static final String FILTER_FIELD =
            "Mit den Textfeldern kann entweder nach einem wörtlichen Suchtext oder nach " +
                    "regulären Ausdrücken (RegExp) gesucht werden.\n" +
                    "\n" +
                    "Groß- und Kleinschreibung wird bei beiden Arten der Suche nicht unterschieden.\n" +
                    "\n" +
                    "-- Wörtlicher Suchtext --\n" +
                    "Ein wörtlicher Suchtext findet alle Dateien bei denen der Suchtext an beliebiger " +
                    "Stelle im durchsuchten Bereich enthalten ist.\n" +
                    "\n" +
                    "Um mehrere Begriffe zu suchen müssen diese durch Komma oder Doppelpunkt " +
                    "getrennt werden. Das Komma verknüpft die Begriffe mit ODER (=> mindestens einer der Begriffe muss vorkommen), der Doppelpunkt mit UND (=> alle Begriffe müssen vorkommen).\n" +
                    "\n" +
                    "Suchtext und Suchbegriffe dürfen Leerzeichen enthalten, aber kein Komma und keinen Doppelpunkt.\n" +
                    "\n" +
                    "Beispiele:\n" +
                    "'Tagesschau' findet u.a. 'Tagesschau, 12:00 Uhr', 'ARD Tagesschau Livestream', 'Bei Logo und der Tagesschau'.\n" +
                    "'Sport,Fussball' (Komma-getrennt) findet Filme bei denen 'Sport' oder 'Fussball' " +
                    "oder beides vorkommt, u.a. 'Wintersport im Mumintal' und 'Wie wird man Fussballprofi?'.\n" +
                    "'Sport:Fussball' (Doppelpunkt-getrennt) findet nur Dateien bei denen " +
                    "beides ('Sport' und 'Fussball') vorkommt, z.B. 'Wintersport, Fussball und Formel 1'.\n" +
                    "\n" +
                    "-- Reguläre Ausdrücke --\n" +
                    "Ein Suchtext aus regulären Ausdrücken (RegExp) muss mit '#:' (ohne die ' ') beginnen.\n" +
                    "\n" +
                    "Beliebige Zeichen mit in die Suche aufnehmen (als '.+', '.*' oder '.?'') macht einen Unterschied!\n" +
                    "\n" +
                    "Beispiele:\n" +
                    "'#:Burger' oder '#:^burger$' -- beide finden 'Burger' und 'burger' und nichts sonst.\n" +
                    "'#:Burger.+' findet nicht 'Burger', aber alles was nach 'Burger' noch Zeichen " +
                    "enthält (z.B. 'Burger für die Straubing Tigers').\n" +
                    "'#:.+burger' findet alles was auf 'burger' oder 'Burger' " +
                    "endet (z.B. 'Der Hamburger' und '1. Burger').\n" +
                    "\n" +
                    "Mehr zu regulären Ausdrücken:\n" +
                    "https://de.wikipedia.org/wiki/Regul%C3%A4rer_Ausdruck";

    public static final String SEARCH_MEDIA_DIALOG = "" +
            "In Mediensammlung und erledigten Abos können bestimmte Daten " +
            "durchsucht werden: In der Mediensammlung DATEINAME, " +
            "in der Liste der erledigten Abos THEMA und TITEL.\n" +
            "\n" +
            FILTER_FIELD + "\n" +
            "\n" +
            "-- Besonderheiten --\n" +
            "Beim Durchsuchen der Mediensammlung muss der Suchtext den eigenen Vorgaben für " +
            "Dateinamen entsprechen. Eine Suche nach 'mein Film' würde den Film 'Das ist mein Film.avi' " +
            "finden, aber nicht 'Das_ist_mein_Film.avi'.\n" +
            "\n" +
            "Bei der Suche nach mehreren Suchbegriffen in erledigten Abos müssen alle im selben Datenfeld " +
            "vorkommen. Ein erledigtes Abo mit 'Sport' in THEMA und 'Fussball' in TITEL wird " +
            "von 'Sport:Fussball' nicht erfasst.\n" +
            "\n" +
            "Ein Doppelklick auf ein Wort im Suchtext stellt dieses frei. So kann man den Suchtext " +
            "schnell auf einen entscheidenden Begriff reduzieren. Der Button rechts daneben stellt den " +
            "ursprünglichen Suchtext wieder her. Nach Start der Suche aus einem Kontextmenü in den " +
            "Ansichten 'Filme' oder 'Downloads' ist dies ein automatisch eingetragener Text; nach Start der Suche " +
            "aus dem Programm-Menü ist es ein leeres Suchfeld.";


    public static final String PROG_PATHS =
            "Hier können Standardprogramme zum Ansehen und Aufzeichnen der Filme eingetragen werden.\n" +
                    "\n" +
                    "Es müssen mindestens diese Programme installiert sein:\n" +
                    "VLC -- zum Ansehen (und teilweise auch Aufzeichnen) der Filme\n" +
                    "ffmpeg -- zum Aufzeichnen von Playlisten (URL endet mit 'm3u8')\n" +
                    "\n" +
                    "Wenn die Pfade nicht automatisch erkannt wurden kann man sie auch per Hand auswählen.\n" +
                    "\n" +
                    "Installation unter Linux:\n" +
                    "Am einfachsten über die Paketverwaltung.\n" +
                    "\n" +
                    "Installation unter Windows:\n" +
                    "MTPlayer enthält bereits ffmpeg, es kann aber auch separat installiert werden.\n" +
                    "VLC muss aus dem Internet geladen werden.\n" +
                    "\n" +
                    "Downloadquellen:\n" +
                    "http://www.videolan.org\n" +
                    "http://ffmpeg.org";

    public static final String GUI_FILM_FILTER =
            "Die Menüs, Textfelder und Schieberegler erlauben ein detailliertes " +
                    "Durchsuchen und Filtern der vorhandenen Filme.\n" +
                    "\n" +
                    "Im Bereich darunter können ganze Such-/Filtereinstellungen als Profile " +
                    "angelegt und verwaltet werden.\n" +
                    "\n" +
                    "Die Blacklist (anzulegen unter Einstellungen > Blacklist) kann für die " +
                    "Anzeige aktiviert bzw. deaktiviert werden.\n" +
                    "\n" +
                    "Der Einstellungs-Button (das Zahnrad) öffnet einen Dialog in dem die einzelnen " +
                    "Filter ein- oder ausgeschaltet werden können.\n" +
                    "\n" +
                    "-- Menüs \"Sender\" und \"Thema\" --\n" +
                    "Die Einträge der Filtermenüs \"Sender\" und \"Thema\" werden automatisch aus der " +
                    "Filmliste erstellt.\n" +
                    "\n" +
                    "Im Einstellungs-Dialog kann für \"Thema\" ein Textfeld für freies Suchen " +
                    "zugeschaltet werden.\n" +
                    "\n" +
                    "-- Suchen und Filtern --\n" +
                    "Mit den Textfeldern ([Thema] wenn eingeschaltet, [Thema oder Titel], [Titel], [Irgendwo] " +
                    "und [URL]) kann sehr detailliert gesucht werden. \"Zeitraum\", \"Filmlänge\", \"Sendezeit\" " +
                    "(ein- oder ausschließen), \"anzeigen:\" und \"ausschließen:\" erlauben noch " +
                    "weitergehende Filterung.\n" +
                    "\n" +
                    FILTER_FIELD + "\n" +
                    "\n" +
                    "-- Besonderheiten --\n" +
                    "[Thema oder Titel] durchsucht THEMA und TITEL der Filmliste. [Irgendwo] sucht " +
                    "außerdem noch in BESCHREIBUNG.\n" +
                    "Bei einer Suche nach mehreren Suchbegriffen müssen hier alle Suchbegriffe im selben " +
                    "Datenfeld vorkommen. Ein Film mit 'Sport' in THEMA und 'Fussball' in TITEL wird " +
                    "von 'Sport:Fussball' nicht erfasst.\n" +
                    "\n" +
                    "[URL] sucht in der URL des Films sowie in der URL der Webseite des Films.\n" +
                    "\n" +
                    "-- Filterprofile --\n" +
                    "Die Buttons \"<\" und \">\") blättern durch die letzten Such/Filtereinstellungen.\n" +
                    "\n" +
                    "Darunter kann man die aktuellen Einstellungen als Profil speichern bzw. aus dem " +
                    "gespeicherten Zustand wieder laden. " +
                    "Solange ein geladenes Profil nicht verändert wurde ist sein Name unterstrichen.\n";

    public static final String BLACKLIST_WHITELIST =
            "Die Funktion \"Blacklist\" blendet alle Filme aus, die den Angaben in mindestens einer Zeile " +
                    "in der Tabelle entsprechen.\n" +
                    "Bei der \"Whitelist\" ist es umgekehrt, es werden nur Filme angezeigt, die den " +
                    "Angaben in mindestens einer Zeile entsprechen.\n" +
                    "Beim Umschalten zwischen ihnen wird also die Auswahl der angezeigten Filme invertiert.\n" +
                    "\n" +
                    FILTER_FIELD + "\n" +
                    "\n" +
                    "-- Besonderheiten --\n" +
                    "Wenn bei \"Thema\" der Schalter \"Exakt\" eingeschaltet ist, darf der Suchtext " +
                    "nicht \"an beliebiger Stelle darin enthalten\" sein, sondern muss das gesamte " +
                    "Feld darstellen.\n" +
                    "Beispiele:\n" +
                    "\"Exakt\" eingeschaltet: 'Dokumentation' erfasst nur 'Dokumentation' oder " +
                    "'dokumentation', nichts sonst.\n" +
                    "\"Exakt\" ausgeschaltet: 'Dokumentation' erfasst u.a. 'Dokumentationen und Reportagen', " +
                    "'Reportage & Dokumentation', 'Geschichtsdokumentationen'.\n" +
                    "\n" +
                    "[Thema/Titel] durchsucht in der Filmliste THEMA und TITEL.\n" +
                    "Bei einer Suche nach mehreren Suchbegriffen müssen hier alle Suchbegriffe im selben Bereich " +
                    "vorkommen. Ein Film mit 'Sport' in THEMA und 'Fussball' in TITEL wird von " +
                    "'Sport:Fussball' nicht erfasst.";

    public static final String CONFIG_GEO =
            "Nicht alle Filme lassen sich aus allen Ländern abrufen (Geoblocking). Man kann hier " +
                    "seinen Standort angeben, wenn geblockte Filme in der Liste markiert werden sollen " +
                    "oder um sie per Blacklist auszuschließen.\n" +
                    "\n" +
                    "Manche Sender überprüfen die abfragende URL, so dass man trotz zum Senderland " +
                    "passender Einstellung nicht auf geblockte Downloads zugreifen kann.\n" +
                    "\n" +
                    "Ein Downloadversuch geblockter Sendungen bricht sofort ab und der Download " +
                    "wird als 'fehlerhaft' angezeigt.\n" +
                    "\n" +
                    "MTPlayer kennt aber nicht alle Muster für geogeblockte Sendungen. Ob ein fehlerhafter " +
                    "Download auf Geoblocking zurückzuführen ist zeigt sich beim Klick auf den Link zur " +
                    "Sendung ('zur Website' ganz unten in der Ansicht 'Filme'). Wenn die Sendung auch auf der " +
                    "Website des Senders nicht abgespielt werden kann liegt fast immer Geoblocking vor.";

    public static final String CONFIG_STYLE =
            "Die Schriftgröße sollte sich automatisch an die vorgegebene Größe im " +
                    "Betriebssystem einstellen. Sie kann hier eingestellt werden, wenn " +
                    "die Automatik nicht funktioniert oder eine andere Größe gewünscht wird.\n" +
                    "\n" +
                    "Damit die Änderung wirksam wird ist evtl. ein Neustart des Programms erforderlich.";

    public static final String GUI_FILMS_EDIT_FILTER =
            "Hier können Filter aktiviert und deaktiviert werden.\n" +
                    "\n" +
                    "Deaktivierte Filter werden beim Suchen der Filme nicht berücksichtigt, " +
                    "daher ist eine Suche mit weniger Filtern schneller.";

    public static final String SETDATA_PRAEFIX =
            "Wenn die URL eines Films mit <Präfix> beginnt bzw. mit <Suffix> " +
                    "endet wird der Film von MTPlayer selbst geladen und nicht mit einem " +
                    "Hilfsprogramm gespeichert.\n" +
                    "\n" +
                    "Mehrere Einträge sind möglich, wenn sie durch Kommas " +
                    "getrennt sind (z.B. 'mp4,mp3,m4v,m4a').";

    public static final String SETDATA_RES =
            "Nicht jede Auflösung wird von jedem Sender angeboten. Wenn die gewünschte " +
                    "Auflösung nicht verfügbar ist wird automatisch die hohe Auflösung heruntergeladen.\n" +
                    "\n" +
                    "Die Auflösung gilt nur für manuell gestartete Downloads und zum " +
                    "Abspielen von Filmen. Für Abos wird die im Abo ausgewählte Auflösung verwendet.";

    public static final String ABO_RES =
            "Nicht jede Auflösung wird von jedem Sender angeboten. " +
                    "Wenn die gewünschte Auflösung nicht verfügbar ist wird " +
                    "automatisch die hohe Auflösung heruntergeladen.";

    public static final String ABO_SUBDIR =
            "Downloads aus Abos werden in einem Abo-eigenen Unterordner gespeichert, " +
                    "wenn \"bei Abos Unterordner anlegen\" im ausgewählten Programmset eingeschaltet ist. " +
                    "Diese Einstellung wird überschrieben, wenn hier ein eigener Zielpfad angegeben ist. " +
                    "Der Download wird dann immer in einem Unterordner mit dem im Zielpfad " +
                    "angegebenen Namen gespeichert." + "\n" +
                    "\n" +
                    "Der Button rechts (Zahnrad) schaltet zwischen der Vorgabe des Programmsets " +
                    "und dem hier eingetragenen Pfad um.\n" +
                    "\n" +
                    "Beim Namen sind diese Parameter möglich:\n" +
                    "\n" +
                    "%D Sendedatum des Films, wenn leer von 'heute'\n" +
                    "%d Sendezeit des Films, wenn leer von 'jetzt'\n" +
                    "%H 'heute', aktuelles Datum im Format JJJJMMTT, z.B. '20090815' am 15.08.2009\n" +
                    "%h 'jetzt', aktuelle Uhrzeit im Format HHMMss, z.B. '152059' um 15:20:59 Uhr\n" +
                    "\n" +
                    "%1 Tag, vom Sendedatum des Films, wenn leer von 'heute'\n" +
                    "%2 Monat, ebenso\n" +
                    "%3 Jahr, ebenso\n" +
                    "\n" +
                    "%4 Stunde, von der Sendezeit des Films, wenn leer von 'jetzt'\n" +
                    "%5 Minute, ebenso\n" +
                    "%6 Sekunde, ebenso\n" +
                    "\n" +
                    "%s Sender des Films\n" +
                    "%T Titel des Films\n" +
                    "%t Thema des Films\n" +
                    "\n" +
                    "%q Qualität des Films ('HD', 'H', 'L')\n" +
                    "\n" +
                    "Beispiel:\n" +
                    "Am 10.05.2021 liefert '%H__%t__%T' z.B. '20210510__Natur__Wildes Shetland'.";

    public static final String SETDATA_ABO_SUBDIR =
            "Downloads aus Abos werden in einem Abo-eigenen Unterordner gespeichert, " +
                    "wenn hier \"bei Abos Unterordner anlegen\" eingeschaltet ist. " +
                    "Der Name des Unterordners kann mit dem Auswahlmenü festgelegt werden.\n" +
                    "\n" +
                    "Diese Einstellung wird überschrieben, wenn im Abo ein eigener " +
                    "Abozielpfad angegeben ist. Der Download wird dann immer in einem " +
                    "Unterordner mit dem im Abozielpfad angegebenen Namen gespeichert.";

    public static final String SETDATA_RESET_COLOR =
            "Wenn das Set in der Ansicht \"Filme\" als Button gestartet werden " +
                    "kann (\"Button\" im Set ist eingeschaltet), kann hier die " +
                    "Schriftfarbe des Buttons festgelegt werden.";

    public static final String DOWNLOAD_REPLACELIST =
            "Die Tabelle wird von oben nach unten abgearbeitet. Es ist also möglich, " +
                    "dass eine Ersetzung durch eine weitere ganz oder teilweise " +
                    "rückgängig gemacht wird!";

    public static final String DOWNLOAD_ONLY_ASCII =
            "Es werden alle Zeichen über ASCII 127 ersetzt. Umlaute werden aufgelöst (z.B. 'ö' -> 'oe').\n" +
                    "\n" +
                    "Wenn die Ersetzungstabelle aktiv ist wird sie vorher abgearbeitet.";

    public static final String DOWNLOAD_ONE_SERVER =
            "Es sind maximal 2 gleichzeitige Downloads pro Server möglich. Hier kann " +
                    "auf nur 1 Download begrenzt werden, was manchmal bei " +
                    "Downloadproblemen hilft.";

    public static final String DOWNLOAD_SSL_ALWAYS_TRUE =
            "Bei Downloads mit \"https-URL\" wird die Verbindung über SSL " +
                    "aufgebaut. Wenn SSL-Zertifikate auf dem Rechner fehlen oder das Server-Zertifikat fehlerhaft ist, kommt es " +
                    "zu Download-Fehlern. Der Download bricht mit einer Fehlermeldung ab. " +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Die Überprüfung der Zertifikate kann mit dieser Funktion abgeschaltet werden.";

    public static final String DOWNLOAD_FINISHED =
            "Wenn ein Download erfolgreich beendet ist wird mit einem Fenster informiert.";

    public static final String DOWNLOAD_ERROR =
            "Wenn ein Download mit einem Fehler endet wird mit einem Fenster informiert.";

    public static final String BLACKLIST_GEO =
            "Einschalten um geogeblockte Filme von Anzeige und Abo-Download auszuschließen.\n" +
                    "Es wird der unter \"Allgemein\" angegebene Standort verwendet.";

    public static final String BLACKLIST_SIZE =
            "Filme ohne Längenangabe werden immer angezeigt und ggf. von Abos als Download angelegt.\n" +
                    "\n" +
                    "Kurze Filme sind oft nur Trailer.\n" +
                    "\n" +
                    "Wenn die Filmliste hiermit verkürzt wird kann das Arbeiten mit den Filmen schneller werden.";

    public static final String BLACKLIST_DAYS =
            "Filme ohne Datum werden immer angezeigt und ggf. von Abos als Download angelegt.\n" +
                    "\n" +
                    "Wenn die Filmliste hiermit verkürzt wird kann das Arbeiten mit den Filmen schneller werden.";

    public static final String BLACKLIST_COUNT =
            "Beim Treffer zählen wird jeder Film gegen alle Filter geprüft und jeder Treffer " +
                    "wird gezählt.\n" +
                    "Anders beim Filtern der Filmliste: " +
                    "Dort wird nach dem ersten Treffer die weitere Suche abgebrochen. " +
                    "Es beschleunigt also das Filtern der Filmliste, " +
                    "wenn hier in der Tabelle die Einträge mit den meisten Treffern " +
                    "am Anfang stehen.";

    public static final String BLACKLIST_FUTURE =
            "Filme mit Datum in der Zukunft sind meist nur Trailer.";

    public static final String BLACKLIST_ABO =
            "Einschalten, wenn auch die von Abos gefundenen Downloads mit der " +
                    "Blacklist/Whitelist gefiltert werden sollen.";

    public static final String LOAD_ONLY_FILMS =
            "\"Nur Filme der letzten Tage laden:\" Die Filmliste enthält nur Filme aus diesem Zeitraum. " +
                    "Filme ohne Datum sind immer enthalten.\n" +
                    "\n" +
                    "\"Nur Filme mit Mindestlänge laden:\" Die Filmliste enthält nur Filme von " +
                    "mindestens dieser Dauer. Filme ohne Längenangabe sind immer enthalten.\n" +
                    "\n" +
                    "Bei 'alles laden' sind alle Filme enthalten.\n" +
                    "\n" +
                    "Das Filtern der Filmliste kann bei älteren Rechnern mit wenig Speicher " +
                    "hilfreich sein: Bei 'maximal 250 Tage' oder 'mindestens 5 Minuten' ist die " +
                    "Filmliste nur etwa halb so groß (~ 140.000 Filme).\n" +
                    "\n" +
                    "Auswirkung hat das Filtern erst nach dem Neustart des Programms oder dem " +
                    "Neuladen der Filmliste.";

    public static final String LOAD_FILMLIST_SENDER =
            "Filme der markierten Sender werden aus der Filmliste ausgeschlossen.\n" +
                    "\n" +
                    "Wirksam erst nach Neustart des Programms oder Neuladen der kompletten Filmliste.";

    public static final String LOAD_FILMLIST_PROGRAMSTART =
            "Die Filmliste wird beim Programmstart automatisch geladen, " +
                    "wenn sie älter als 3 Stunden ist. Sie kann auch über den " +
                    "Button \"Filmliste\" in der Ansicht \"Filme\" aktualisiert werden.\n" +
                    "\n" +
                    "Zum Update werden dann nur noch Differenzlisten geladen " +
                    "(diese enthalten nur neu hinzugekommene Filme).";

    public static final String LOAD_FILMLIST_MANUAL =
            "Die Filmliste wird von der angegebenen Datei oder URL geladen. " +
                    "Bei leerem Feld wird die Filmliste auf herkömmliche Art geladen " +
                    "und die URL dafür wird automatisch gewählt.";

    public static final String SEARCH_ABOS_IMMEDIATELY =
            "Nach dem Neuladen einer Filmliste wird automatisch nach neuen " +
                    "Downloads aus Abos gesucht. Wenn dies ausgeschaltet ist, muss man die " +
                    "Suche manuell anstoßen (in der Ansicht \"Downloads\" " +
                    "auf \"Downloads aktualisieren\" klicken).";

    public static final String SMALL_BUTTON =
            "In den Tabellen der Ansichten \"Filme\" und \"Downloads\" können " +
                    "kleinere Buttons gewählt werden, um die Zeilenhöhe zu verringern.";

    public static final String TRAY =
            "Im System Tray wird für das Programm ein Symbol angezeigt. " +
                    "Damit kann das Programm auf dem Desktop ausgeblendet werden.";

    public static final String DARK_THEME =
            "Das Programm wird damit mit einer dunklen Programmoberfläche angezeigt. " +
                    "Damit alle Elemente der Programmoberfläche geändert werden, kann ein " +
                    "Programmneustart notwendig sein.";

    public static final String SHORTCUT =
            "Zum Ändern eines Tastenkürzels seinen \"Ändern\"-Button klicken und dann " +
                    "die gewünschten neuen Tasten drücken.\n" +
                    "\n" +
                    "Der \"Zurücksetzen\"-Button stellt den Originalzustand wieder her.\n" +
                    "\n" +
                    "Damit die Änderungen wirksam werden muss das Programm neu gestartet werden.";

    public static final String USER_AGENT =
            "Hier kann ein User Agent angegeben werden, der bei Downloads als Absender " +
                    "verwendet wird. Bleibt das Feld leer, wird kein User Agent verwendet.\n" +
                    "\n" +
                    "Solange alles funktioniert, kann das Feld leer bleiben. Ansonsten wäre " +
                    "das z.B. eine Möglichkeit: 'Mozilla/5.0'.\n" +
                    "\n" +
                    "Es sind nur ASCII-Zeichen erlaubt und die Textlänge ist begrenzt auf 100 Zeichen.";

    public static final String START_DOWNLOADS_FROM_ABOS_IMMEDIATELY =
            "Downloads die aus Abos neu angelegt wurden starten sofort. " +
                    "Wenn dies ausgeschaltet ist muss man sie manuell starten.";

    public static final String LOGFILE =
            "Im Logfile wird der Programmverlauf aufgezeichnet. Das kann hilfreich sein " +
                    "wenn das Programm nicht wie erwartet funktioniert.\n" +
                    "\n" +
                    "Der Standardordner für das Log ist 'Log' im Konfigurations-Ordner des " +
                    "Programms. Der Ort kann geändert werden.\n" +
                    "\n" +
                    "Ein geänderter Pfad zum Logfile wird erst nach einem Neustart des " +
                    "Programms genutzt; mit dem Button \"Pfad zum Logfile jetzt schon verwenden\" wird " +
                    "sofort ins neue Log geschrieben.";

    public static final String FILEMANAGER =
            "In der Ansicht \"Downloads\" kann man über das Kontextmenü den Downloadordner " +
                    "(Zielordner) des jeweiligen Downloads öffnen. Normalerweise wird dafür der " +
                    "Dateimanager des Betriebssystems gefunden und geöffnet. Klappt das nicht, kann " +
                    "hier ein Programm dafür angegeben werden.";

    public static final String VIDEOPLAYER =
            "In der Ansicht \"Downloads\" kann man über das Kontextmenü den gespeicherten " +
                    "Film in einem Videoplayer öffnen. Normalerweise wird der Videoplayer des " +
                    "Betriebssystems gefunden und geöffnet. Klappt das nicht, kann hier ein " +
                    "Programm dafür angegeben werden.";

    public static final String WEBBROWSER =
            "Wenn das Programm versucht, einen Link zu öffnen (z.B. \"Anleitung im Web\" im " +
                    "Programm-Menü unter \"Hilfe\") und der Standardbrowser nicht startet, " +
                    "kann damit ein Programm (Firefox, Chromium, …) ausgewählt und fest " +
                    "zugeordnet werden.";

    public static final String EXTERN_PROGRAM_SHORT_CUT =
            "In den Tastenkürzeln gibt es eines, das ein externes Programm aufruft. Dieses " +
                    "Programm kann hier gesetzt werden. Gedacht ist das z.B. um per Tastenkürzel ein " +
                    "Programm zum Aufräumen des Download-Ordners laufen zu lassen, oder ähnliches.";

    public static final String MEDIA_DIALOG =
            "Hier kann eine Mediensammlung angelegt werden. Das ist eine Liste der " +
                    "bereits vorhandenen Filme.\n" +
                    "\n" +
                    "Vor dem Download eines Films kann darin der Filmtitel gesucht werden, " +
                    "um doppelte Downloads zu vermeiden. Bei Filmen in den Ansichten \"Filme\" " +
                    "und \"Downloads\" geht dies mit dem Kontextmenü \"Titel in der Mediensammlung " +
                    "suchen\". Ein Dialog listet dann gefundene Filmtitel aus der Mediensammlung auf.\n" +
                    "\n" +
                    "Hier im Konfigurations-Dialog werden die Ordner angegeben, deren Filmdateien " +
                    "in die Mediensammlung aufgenommen werden.\n" +
                    "\n" +
                    "\"Interne Medien\": Die angegebenen Pfade werden beim Programmstart durchsucht, " +
                    "sie müssen deshalb verfügbar sein, während das Programm läuft. Die Mediensammlung " +
                    "ist daher für interne Medien immer aktuell.\n" +
                    "\n" +
                    "\"Externe Medien\": Ihr Inhalt an Mediendateien wird beim Hinzufügen zur Liste " +
                    "hinzugefügt und steht dann dauerhaft im Programm und bei der Suche in der " +
                    "Mediensammlung zur Verfügung. Es ist daher nicht nötig, dass externe Medien " +
                    "beim Programmstart vorhanden sind.\n" +
                    "\n" +
                    "\"Mediensammlung neu aufbauen\": Auch hierfür müssen externe Medien nicht " +
                    "angeschlossen sein.";

    public static final String MEDIA_COLLECTION =
            "\"Keine Dateien mit diesem Suffix\" oder \"nur Dateien mit diesem Suffix\": Bestimmt " +
                    "durch die angegebenen Suffixe welche Dateien in die Mediensammlung aufgenommen werden. " +
                    "Mehrere Suffixe können durch Komma getrennt angegeben werden, z.B. 'txt,xml'. Ist die " +
                    "Zeile leer, werden alle Dateien aufgenommen.\n" +
                    "\n" +
                    "\"Keine versteckten Dateien suchen:\" Unabhängig vom Suffix werden versteckte " +
                    "Dateien nicht in die Mediensammlung aufgenommen.\n" +
                    "\n" +
                    "\"Nur Dateien mit Mindestgröße suchen:\" Kleinere Dateien werden nicht in die " +
                    "Mediensammlung aufgenommen.\n" +
                    "\n" +
                    "Mit dem \"Export\"-Button kann die komplette Mediensammlung in eine " +
                    "Textdatei geschrieben werden.";

    public static final String EXTERN_MEDIA_COLLECTION =
            "Hier können externe Medienordner in der Mediensammlung verwaltet werden: " +
                    "Ordner hinzufügen, die Mediensammlung mit vorhandenen Ordnern " +
                    "manuell aktualisieren, oder vorhandene Ordner aus der " +
                    "Mediensammlung entfernen.";

    public static final String INTERN_MEDIA_COLLECTION =
            "Hier können interne Medienordner in der Mediensammlung verwaltet werden: Ordner " +
                    "hinzufügen oder vorhandene Ordner aus der Mediensammlung entfernen. " +
                    "Eine Aktualisierung der Mediensammlung mit ihrem Inhalt erfolgt bei " +
                    "jedem Programmstart.";

    public static final String RESET_DIALOG =
            "-- Nichts ändern --\n" +
                    "Der Dialog wird ohne eine Änderung geschlossen.\n" +
                    "\n" +
                    "-- Einstellungen zum Abspielen und Aufzeichnen zurücksetzen --\n" +
                    "ALLE SETS WERDEN GELÖSCHT! Auch die selbst neu angelegten.\n" +
                    "Anschließend werden die aktuellen Standardsets eingerichtet. Es kann dann direkt " +
                    "damit weitergearbeitet werden.\n" +
                    "Abos und Blacklist bleiben erhalten.\n" +
                    "\n" +
                    "Bei Problemen sollte dies zuerst versucht werden, vor einem kompletten " +
                    "Zurücksetzen des Programms.\n" +
                    "\n" +
                    "-- Alle Einstellungen zurücksetzen --\n" +
                    "ALLE EINSTELLUNGEN WERDEN GELÖSCHT! Das Programm wird in den " +
                    "Ursprungszustand zurückgesetzt.\n" +
                    "Es beendet sich und muss neu gestartet werden.\n" +
                    "Der neue Start beginnt mit dem Einrichtungsdialog.";
}
