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

package de.mtplayer.mtp.gui.tools;

import de.p2tools.p2Lib.P2LibConst;

public class HelpText {

    public static final String SEARCH_MEDIA_DIALOG = "Ein Filterwort kann auch Leerzeichen enthalten, " +
            "z.B.: \"Hallo ich\". Es wird dann nach Dateien " +
            "gesucht, die im Namen \"Hallo ich\" an beliebiger " +
            "Stelle enthalten. Man sollte den Suchbegriff " +
            "also so formulieren, dass die eigenen Vorgaben " +
            "für Dateinamen erfüllt sind." +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "Ein Doppelklick auf ein Wort im Suchtext stellt dieses frei. So kann man den Suchtext schnell " +
            "auf den entscheidenden Begriff reduzieren. Der Button rechts daneben, stellt den ursprünglichen " +
            "Suchtext weider her." +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "Groß- und Kleinschreibung wird beim Suchen " +
            "nicht beachtet." +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "In dem Filter kann auch mit regulären Ausdrücken (RegExp) " +
            "gesucht werden. Diese müssen mit \"#:\" eingeleitet " +
            "werden. Auch bei den regulären Ausdrücken spielt Groß- und " +
            "Kleinschreibung keine Rolle." +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "#:Abend.*" + P2LibConst.LINE_SEPARATOR +
            "Das bedeutet z.B.: Es werden alle Dateien gefunden, die " +
            "mit \"Abend\" beginnen. " +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "https://de.wikipedia.org/wiki/Regul%C3%A4rer_Ausdruck";

    public static final String PROG_PATHS = "Hiermit können die Standardprogramme zum \"Ansehen\" und \"Aufzeichnen\" " +
            "der Filme eingetragen werden. Werden die Pfade nicht automatisch erkannt, " +
            "kann man sie auch per Hand auswählen." +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "Um alle Filme ansehen und aufzeichnen zu können, müssen mindestens " +
            "diese Programme installiert sein:" +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "VLC" + P2LibConst.LINE_SEPARATOR +
            "Dieses Programm dient zum Ansehen und teilweise auch zum Aufzeichnen der Filme." +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "flvstreamer" + P2LibConst.LINE_SEPARATOR +
            "Damit können Flashfilme (die URL beginnt mit \"rtmp\") aufgezeichnet werden." +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "ffmpeg" + P2LibConst.LINE_SEPARATOR +
            "Zum Aufzeichnen von Playlisten (URL endet mit \"m3u8\")" +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "Linux:" + P2LibConst.LINE_SEPARATOR +
            "Die Programme können über die Paketverwaltung eingespielt werden." + P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "Windows:" + P2LibConst.LINE_SEPARATOR +
            "Die Programme \"flvstreamer\" und \"ffmpeg\" sind bereits im Programm " +
            "integriert. Es muss nur noch \"VLC\" aus dem Internet geladen werden:" +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "http://www.videolan.org" + P2LibConst.LINE_SEPARATOR +
            "https://savannah.nongnu.org/projects/flvstreamer/" + P2LibConst.LINE_SEPARATOR +
            "http://ffmpeg.org" + P2LibConst.LINE_SEPARATOR;

    public static final String FILTER_FIELD =
            P2LibConst.LINE_SEPARATOR +
                    "\"Sender\" und \"Thema\" können exakt verglichen werden. " +
                    "Das heißt, der im Feld \"Sender\\Thema\" angegebene Text muss " +
                    "genau dem \"Sender\\Thema\" des Films entsprechen. " +
                    P2LibConst.LINE_SEPARATOR +
                    P2LibConst.LINE_SEPARATOR +
                    "Bei den anderen Feldern (oder wenn exakt ausgeschaltet ist) " +
                    "muss die Eingabe im " +
                    "entsprechendem Feld des Films nur enthalten sein." +
                    P2LibConst.LINE_SEPARATOR +
                    P2LibConst.LINE_SEPARATOR +
                    "Beim Feld \"Thema/Titel\" muss der Filter im " +
                    "\"Thema\" ODER \"Titel\" enthalten sein." +
                    P2LibConst.LINE_SEPARATOR;

    public static final String FILTER_EXACT =
            P2LibConst.LINE_SEPARATOR +
                    "\"exakt\" bedeutet, dass z.B. \"Abend\" im Feld Thema nur die Filme " +
                    "findet, die genau das Thema \"Abend\" haben. " +
                    "Ist \"exakt\" ausgeschaltet und steht im Feld \"Sender\" z.B. \"a\" " +
                    "dann werden alle Sender die ein \"a\" enthalten gefunden!" +
                    P2LibConst.LINE_SEPARATOR +
                    P2LibConst.LINE_SEPARATOR +
                    "Groß- und Kleinschreibung wird beim Filtern " +
                    "nicht beachtet." +
                    P2LibConst.LINE_SEPARATOR +
                    P2LibConst.LINE_SEPARATOR +
                    "In allen Feldern (wenn nicht \"exakt\" eingestellt ist) " +
                    "kann auch nach mehreren Begriffen gesucht werden (diese " +
                    "werden durch \"Komma\" oder \"Doppelpunkt\" getrennt angegeben " +
                    "und können auch Leerzeichen enthalten)." +
                    P2LibConst.LINE_SEPARATOR +
                    "\"Sport,Fussball\" sucht nach Filmen die im jeweiligen Feld den " +
                    "Begriff \"Sport\" ODER \"Fussball\" haben." +
                    P2LibConst.LINE_SEPARATOR +
                    "\"Sport:Fussball\" sucht nach Filmen die im jeweiligen Feld den " +
                    "Begriff \"Sport\" UND \"Fussball\" haben." +
                    P2LibConst.LINE_SEPARATOR +
                    P2LibConst.LINE_SEPARATOR +
                    "In allen Feldern (wenn nicht \"exakt\" eingestellt ist) " +
                    "kann auch mit regulären Ausdrücken gesucht " +
                    "werden. Diese müssen mit \"#:\" eingeleitet werden. " +
                    "Auch bei den regulären Ausdrücken wird nicht zwischen " +
                    "Groß- und Kleinschreibung unterschieden. " +
                    P2LibConst.LINE_SEPARATOR +
                    "#:Abend.*" + P2LibConst.LINE_SEPARATOR +
                    "Das bedeutet z.B.: Es werden alle Filme gefunden, die " + P2LibConst.LINE_SEPARATOR +
                    "im jeweiligen Feld mit \"Abend\" beginnen." + P2LibConst.LINE_SEPARATOR +
                    P2LibConst.LINE_SEPARATOR +
                    P2LibConst.LINE_SEPARATOR +
                    "https://de.wikipedia.org/wiki/Regul%C3%A4rer_Ausdruck" + P2LibConst.LINE_SEPARATOR +
                    P2LibConst.LINE_SEPARATOR;

    public static final String GUI_FILM_FILTER =
            FILTER_FIELD +
                    P2LibConst.LINE_SEPARATOR +
                    "Im Filter \"Irgendwo\" wird zum Titel und Thema " +
                    "auch die Beschreibung geprüft." +
                    P2LibConst.LINE_SEPARATOR +
                    P2LibConst.LINE_SEPARATOR +
                    "Beim Feld \"URL\" muss der Filter in der URL " +
                    "des Films ODER der Website des Films enthalten sein." +
                    P2LibConst.LINE_SEPARATOR +
                    FILTER_EXACT +
                    P2LibConst.LINE_SEPARATOR +
                    P2LibConst.LINE_SEPARATOR +
                    "Filterprofile:" + P2LibConst.LINE_SEPARATOR +
                    "==================" + P2LibConst.LINE_SEPARATOR +
                    "Mit den Buttons unten, kann man eingestellte Filter speichern " +
                    "und auch wieder abrufen. So wird der gespeicherte Zustand genau " +
                    "wieder hergestellt. Ist der Profilname im Auswahlfeld unterstrichen, " +
                    "besagt das, dass die aktuellen Filtereinstellungen unverändert sind und " +
                    "denen des Profils entsprechen.";

    public static final String BLACKLIST_WHITELIST =
            "Bei der Funktion \"Blacklist\" werden Filme, die den " +
                    "Angaben in einer Zeile in der Tabelle entsprechen, " +
                    "nicht angezeigt." +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Die Funktion \"Whitelist\" zeigt nur die Filme an, die " +
                    "den Angaben in einer Zeile in der Tabelle entsprechen." +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Beim Umschalten \"Blacklist - Whitelist\" werden genau " +
                    "die vorher nicht angezeigten Filme jetzt angezeigt." +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Mit \"Treffer zählen\" kann überprüft werden, wieviele Filme " +
                    "in der Filmliste jeder Eintrag in der Blacklist findet. Damit lässt " +
                    "sich die Blacklist optimieren. Eine kürzere Blacklist führt zu schnelleren " +
                    "Ergebnissen." +
                    P2LibConst.LINE_SEPARATORx2 +

                    FILTER_FIELD +
                    FILTER_EXACT;

    public static final String CONFIG_GEO = "Nicht alle Filme lassen sich im Ausland abrufen." + P2LibConst.LINE_SEPARATOR +
            "Wenn geblockte Filme markiert werden sollen, kann man hier seinen Standort angeben." + P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "Der Download geogeblockter Sendungen bricht im Ausland sofort ab (Download \"fehlerhaft\")." + P2LibConst.LINE_SEPARATOR +
            "MTPlayer kennt nicht alle Muster für geogeblockte Sendungen. Ob ein " +
            "fehlerhafter Download auf Geoblocking " +
            "zurückzuführen ist, zeigt sich beim Klick auf " +
            "den Link zur Sendung (\"zur Website\") ganz " +
            "unten im Tab \"Filme\". Wenn die Sendung " +
            "auch auf der Website des Senders nicht " +
            "abgespielt werden kann, liegt fast immer " +
            "Geoblocking vor.";

    public static final String GUI_FILMS_EDIT_FILTER = "Hier können die Filter " +
            "die angezeigt werden sollen, ein- und ausgeschaltet werden." +
            P2LibConst.LINE_SEPARATORx2 +
            "Ausgeschaltete Filter werden beim Suchen der Filme " +
            "auch nicht berücksichtigt." +
            P2LibConst.LINE_SEPARATORx2 +
            "Mit weniger Filtern ist auch der Suchvorgang schneller";

    public static final String SET = "" +
            P2LibConst.LINE_SEPARATORx2 +
            "Mehrere Einträge können mit \"Komma\" getrennt, angegeben werden";

    public static final String SETDATA_PRAEFIX = "Filme, deren URL mit \"Präfix\" beginnt und mit \"Suffix\" endet, " +
            "werden nicht mit einem Hilfsprogramm gespeichert, " +
            "sondern direkt geladen." +
            P2LibConst.LINE_SEPARATORx2 +
            "Mehrere Einträge können mit \"Komma\" getrennt, angegeben werden." +
            P2LibConst.LINE_SEPARATOR +
            "z.B.: mp4,mp3,m4v,flv,m4a";

    public static final String SETDATA_RES = "Nicht jede Auflösung ist bei jedem Sender möglich. Ist die " +
            "gewünschte Auflösung nicht verfügbar, " +
            "wird in \"hoher\" Auflösung geladen." +
            P2LibConst.LINE_SEPARATORx2 +
            "Die Auflösung gilt nur für manuell gestartete Downloads " +
            "und zum Abspielen von Filmen. Für Abos wird die " +
            "im Abo vorgegebene Auflösung verwendet.";

    public static final String ABO_RES = "Nicht jede Auflösung ist bei jedem Sender möglich. Ist die " +
            "gewünschte Auflösung nicht verfügbar, " +
            "wird in \"hoher\" Auflösung geladen.";

    public static final String ABO_SUBDIR = "Wenn im ausgewählten Programmset eingeschaltet, " +
            "werden Downloads aus Abos in einem Unterordner gespeichert." +
            "\n\n" +
            "Ist im Abo aber ein Abozielpfad angegeben, wird diese Einstellung überschrieben. " +
            "Der Download wird immer in einem Unterordner mit dem im Abozielpfad angegebenen Namen gespeichert. " +
            "\n\n" +
            "Mit dem Button rechts, kann zwischen der Vorgabe des Programmsets und einer eigenen Einstellung für " +
            "dieses Abo umgeschaltet werden." +
            "\n\n" +

            "Beim Namen sind diese Parameter möglich:\n" +
            "\n" +
            "%D Sendedatum des Films oder \"heute\", wenn Sendedatum leer\n" +
            "%d Sendezeit des Films oder \"jetzt\", wenn Sendezeit leer\n" +
            "%H \"heute\", aktuelles Datum\n" +
            "%h \"jetzt\", aktuelle Uhrzeit\n" +
            "Datum in der Form: JJJJMMTT z.B. 20090815 (15.08.2009)\n" +
            "Zeit in der Form: SSMMss z.B. 152059 (15:20:59)\n" +
            "\n" +
            "%1 Tag,\n" +
            "%2 Monat,\n" +
            "%3 Jahr vom Sendedatum des Films oder aktuellem Datum, wenn Sendedatum leer\n" +
            "\n" +
            "%4 Stunde,\n" +
            "%5 Minute,\n" +
            "%6 Sekunde von der Sendezeit des Films oder \"jetzt\", wenn Sendezeit leer\n" +
            "\n" +
            "%s Sender des Films\n" +
            "%T Titel des Films\n" +
            "%t Thema des Films\n" +
            "\n" +
            "%q Qualität des Films (\"HD\", \"H\", \"L\")\n" +
            "\n" +
            "Damit kann man einen Namen z.B. so aufbauen:\n" +
            "%H__%t__%T  \t -> \t  20131206__Doku__Titel_der_Doku\n";


    public static final String SETDATA_ABO_SUBDIR = "Wenn eingeschaltet, werden Downloads aus Abos in einem " +
            "Unterordner gespeichert. Der Ordnername kann mit der Auswahl festgelegt werden. " +
            "\n\n" +
            "Ist im Abo ein Abozielpfad angegeben, wird die Einstellung überschrieben. " +
            "Der Download wird immer in einem Unterordner mit dem im Abozielpfad angegebenen Namen gespeichert.";

    public static final String SETDATA_RESET_COLOR = "Wird das Set als Button verwendet, " +
            "kann damit die Schriftfarbe verändert werden.";

    public static final String DOWNLOAD_REPLACELIST = "Die Tabelle wird von oben nach unten abgearbeitet. " +
            "Es ist also möglich, dass eine Ersetzung durch eine weitere " +
            "wieder ersetzt wird!";

    public static final String DOWNLOAD_ONLY_ASCII = "Es werden alle Zeichen \"über 127\" ersetzt. " +
            "Auch Umlaute wie \"ö -> oe\" werden ersetzt." +
            P2LibConst.LINE_SEPARATORx2 +
            "Wenn die Ersetzungstabelle aktiv ist, wird sie vorher abgearbeitet.";

    public static final String DOWNLOAD_ONE_SERVER = "Es gibt eine Begrenzung auf " +
            "2 Downloads pro Server " +
            "die nicht überschritten wird. " +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "Das kann auch noch " +
            "auf 1 Download pro Server " +
            "(z.B. nur ein Download von \"www.zdf.de\") " +
            "weiter begrenzt werden." +
            P2LibConst.LINE_SEPARATOR +
            P2LibConst.LINE_SEPARATOR +
            "Das kann bei Downloadproblemen helfen.";

    public static final String DOWNLOAD_FINISHED = "Wird ein Download beendet, wird man mit einem Fenster informiert.";

    public static final String DOWNLOAD_ERROR = "Wird ein Download mit einem Fehler beendet, " +
            "wird man darüber mit einem Fenster informiert.";


    public static final String BLACKLIST_GEO = "Geogeblockte Filme können im jeweiligen \"Ausland\" nicht abgerufen werden. " +
            P2LibConst.LINE_SEPARATORx2 +
            "(Dazu muss die eigene Position in den Einstellungen angegeben werden)";

    public static final String BLACKLIST_SIZE = "Kurze Filme sind oft nur Trailer. Filme, die keine " +
            "Längenangabe haben, werden immer angezeigt.";

    public static final String BLACKLIST_DAYS = "Damit kann man die Anzahl der angezeigten Filme " +
            "reduzieren. Filme ohne Datum werden immer angezeigt. " +
            "Durch eine kürzere Filmliste kann das Arbeiten mit den Filmen schneller werden.";

    public static final String BLACKLIST_COUNT = "Beim Treffer zählen wird jeder Film gegen die Blacklist geprüft. " +
            "Jeder Film läuft also die Blacklist von Anfang nach Ende ab und jeder Treffer wird gezählt. " +
            P2LibConst.LINE_SEPARATORx2 +
            "Beim Filtern der Filmliste wird nach einem Treffer wird die Suche abgebrochen. Es beschleunigt die Suche " +
            "wenn die Blacklisteinträge mit den meisten Treffern am Anfang liegen";

    public static final String BLACKLIST_FUTURE = "Filme, deren Datum in der Zukunft liegt, sind meist nur Trailer.";

    public static final String BLACKLIST_ABO = "Wenn Downloads aus Abos gesucht werden, " +
            "werden alle gefundenen Filme geladen oder nur die, die nicht durch " +
            "die Blacklist geblockt werden.";

    public static final String LOAD_ONLY_FILMS = "\n" +
            "nur Filme der letzten Tage laden" +
            "\n==========================\n" +
            "Die Filmliste enthält nur Filme der letzten XX Tage. " +
            "Filme ohne Datum sind immer enthalten. " +
            P2LibConst.LINE_SEPARATORx3 +

            "nur Filme mit Mindestlänge laden" +
            "\n==========================\n" +
            "Die Filmliste enthält nur Filme mit einer Mindestlänge von XX Minuten. " +
            "Filme ohne Längenangabe sind immer enthalten. " +

            P2LibConst.LINE_SEPARATORx3 +
            "\n==========================\n" +
            "Bei \"Alle\" sind alle Filme enthalten.\n" +
            "Beim Filtern mit: \"maximal 250 Tagen\" oder auch " +
            "beim Filtern mit: \"mindestens 5 Minuten\" ist die Filmliste nur " +
            "etwa halb so groß: ~ 140.000 Filme. " +

            P2LibConst.LINE_SEPARATORx2 +
            "Eine kleinere Filmliste kann bei älteren Rechnern mit wenig Speicher hilfreich sein. " +
            "Auswirkung hat das erst nach dem Neustart des Programms oder dem " +
            "Neuladen der kompletten Filmliste.";

//    public static final String LOAD_FILM_ONLY_DAYS = "Die Filmliste enthält nur Filme der letzten " +
//            "xx Tage. Filme ohne Datum sind immer enthalten. " +
//            PConst.LINE_SEPARATORx2 +
//            "Bei \"Alle\" sind alle Filme enthalten. Bei \"250 Tagen\" ist die Filmliste nur " +
//            "etwa halb so groß: ~ 140.000 Filme. (Eine kleinere Filmliste kann bei älteren " +
//            "Rechnern mit wenig Speicher hilfreich sein.) " +
//            PConst.LINE_SEPARATORx2 +
//            "Auswirkung hat das erst nach dem Neustart des Programms oder dem " +
//            "Neuladen der kompletten Filmliste.";
//
//    public static final String LOAD_FILM_ONLY_DURATION = "Die Filmliste enthält nur Filme mit einer " +
//            "Mindestlänge von XX Minuten. Filme ohne Längenangabe sind immer enthalten. " +
//            PConst.LINE_SEPARATORx2 +
//            "Bei \"Alle\" sind alle Filme enthalten. Bei \"5 Minuten\" ist die Filmliste nur " +
//            "etwa halb so groß: ~ 140.000 Filme. (Eine kleinere Filmliste kann bei älteren " +
//            "Rechnern mit wenig Speicher hilfreich sein.) " +
//            "Filme kürzer als 5 Minuten sind meist nur Ausschnitte oder Trailer." +
//            PConst.LINE_SEPARATORx2 +
//            "Auswirkung hat das erst nach dem Neustart des Programms oder dem " +
//            "Neuladen der kompletten Filmliste.";

    public static final String LOAD_FILMLIST_SENDER = "Filme der markierten Sender " +
            "sind in der Filmliste  _nicht_  enthalten. " +
            P2LibConst.LINE_SEPARATORx2 +
            "Auswirkung hat das erst nach dem Neustart des Programms oder dem " +
            "Neuladen der kompletten Filmliste.";

    public static final String LOAD_FILMLIST_PROGRAMSTART = "Die Filmliste wird beim Programmstart " +
            "automatisch geladen (wenn sie " +
            "älter als 3h ist). Zusätzlich kann sie über den " +
            "Button \"Filmliste\" " +
            "aktualisiert werden. Zum Update werden dann nur noch die " +
            "Differenzlisten geladen (diese enthalten nur " +
            "die neuen Filme).";

    public static final String LOAD_FILMLIST_MANUAL = "Die angegebene Datei/URL wird beim " +
            "Neuladen einer Filmliste verwendet. Ist nichts angegeben, wird die Filmliste auf herkömmliche " +
            "Art geladen und die URL dafür wird automatisch gewählt.";

    public static final String SEARCH_ABOS_IMMEDIATELY = "Nach dem Neuladen einer Filmliste wird anschießend " +
            "sofort nach neuen Downloads aus Abos gesucht. Ansonsten muss man " +
            "im Tab Download auf \"Downloads aktualisieren\" klicken.";

    public static final String SMALL_BUTTON = "In der Tabelle Filme und Downloads können auch " +
            "kleine Buttons angezeigt werden. Die Zeilenhöhe wird dadurch kleiner.";

    public static final String DARK_THEME = "Das Programm wird damit mit einer dunklen " +
            "Programmoberfläche angezeigt. Damit alle Elemente der Programmoberfläche " +
            "geändert werden, kann ein Programmneustart notwendig sein.";

    public static final String USER_AGENT = "Hier kann ein User Agent angegeben werden. " +
            "Bei Downloads wird er dann als Absender verwendet. Bleibt das Feld leer, wird kein User Agent " +
            "verwendet. Solange alles funktioniert, kann das Feld leer bleiben. Ansonsten wäre das z.B. eine " +
            "Möglichkeit: " +
            P2LibConst.LINE_SEPARATORx2 +
            "\"Mozilla/5.0\"" +
            P2LibConst.LINE_SEPARATORx2 +
            "(Es sind nur ASCII-Zeichen erlaubt und die Textlänge ist begrenzt auf 100 Zeichen)";

    public static final String START_DOWNLOADS_FROM_ABOS_IMMEDIATELY = "Neu angelegte Downloads (aus Abos) werden " +
            "sofort gestartet. Ansonsten muss man sie selbst starten.";

    public static final String QUIT_PRGRAM_AFTER_DOWNLOAD = "Das Programm aktualisiert automatisch " +
            "beim Programmstart die Filmliste (sofern veraltert), " +
            "sucht nach Downloads aus Abos, speichert alle Downloads und beendet sich dann wieder." +
            P2LibConst.LINE_SEPARATORx2 +
            "Es wird während dessen ein Hinweisfenster angezeigt. Wird dieses Hinweisfenster geschlossen, " +
            "wird damit auch der \"Automodus\" abgebrochen und mit dem Programm " +
            "kann normal weiter gearbeitet werden.";

    public static final String LOGFILE = "Hier kann ein Ordner angegeben werden " +
            "in dem ein Logfile erstellt wird. Darin wird der Programmverlauf skizziert. " +
            "Das kann hilfreich sein, wenn das Programm nicht wie erwartet funktioniert." + P2LibConst.LINE_SEPARATORx2 +
            "Der Standardordner ist \"Log\" im Konfigordner des Programms." + P2LibConst.LINE_SEPARATORx2 +
            "Wird der Pfad zum Logfile geändert, wirkt sich das erst beim Neustart des Programms " +
            "aus. Mit dem Button \"Pfad zum Logfile jetzt schon verwenden\" wird die Programmausgabe ab " +
            "Klick darauf ins neue Logfile geschrieben.";

    public static final String FILEMANAGER = "Im Tab \"Downloads\" kann man mit der rechten " +
            "Maustaste den Downloadordner (Zielordner) " +
            "des jeweiligen Downloads öffnen. " +
            "Normalerweise wird der Dateimanager des " +
            "Betriebssystems gefunden und geöffnet. Klappt das nicht, " +
            "kann hier ein Programm dafür angegeben werden.";

    public static final String VIDEOPLAYER = "Im Tab \"Downloads\" kann man den gespeicherten " +
            "Film in einem Videoplayer öffnen. " +
            "Normalerweise wird der Videoplayer des " +
            "Betriebssystems gefunden und geöffnet. Klappt das nicht, " +
            "kann hier ein Programm dafür angegeben werden.";

    public static final String WEBBROWSER = "Wenn das Programm versucht, einen Link zu öffnen " +
            "(z.B. den Link im Menüpunkt \"Hilfe\" zu den \"Hilfeseiten\") " +
            "und die Standardanwendung (z.B. \"Firefox\") nicht startet, " +
            "kann damit ein Programm ausgewählt und " +
            "fest zugeordnet werden (z.B. der Browser \"Firefox\").";

    public static final String MEDIA_DIALOG = "Hier kann eine Mediensammlung angelegt werden. Vor dem " +
            "Download eines Films, kann der Filmtitel mit der Mediensammlung abgeglichen werden. So können doppelte " +
            "Downloads vermieden werden." +
            P2LibConst.LINE_SEPARATORx2 +

            "Hier im Konfig-Dialog werden Ordner mit Medien angegeben, die das Programm dann absucht." +
            P2LibConst.LINE_SEPARATORx2 +

            "Die angegebenen Pfade der \"Internen Medien\", werden beim Programmstart " +
            "abgesucht, diese müssen also verfügbar sein. Dafür ist der aktuelle " +
            "Bestand an Filmen auch in der MedienDB enthalten." +
            P2LibConst.LINE_SEPARATORx2 +

            "Externe Medien werden nur beim Anlegen abgesucht. " +
            "Deren Inhalt an Mediendateien steht dauerhaft im Programm und " +
            "bei der Suche in der Mediensammlung zur Verfügung. " +
            "Diese müssen also beim Programmstart nicht vorhanden sein." +
            P2LibConst.LINE_SEPARATORx3 +

            "Im Tab \"Filme\" und im Tab \"Download\" kann mit einem Klick " +
            "mit der rechten Maustaste auf einen Film: \"Titel in der Mediensammlung suchen\" " +
            "nach dem Filmtitel gesucht werden. Ein Dialog listet dann gefundene Filmtitel aus der Mediensammlung auf.";


    public static final String MEDIA_COLLECTION = "\"Keine Dateien mit diesem Suffix\"\n" +
            "Dateien mit einem dort angegebenen Suffix werden nicht in den Index aufgenommen." +

            P2LibConst.LINE_SEPARATORx2 +
            "\"Nur Dateien mit diesem Suffix\"\n" +
            "Es werden dann nur Dateien mit diesm Suffix genommen." +

            P2LibConst.LINE_SEPARATORx2 +
            "Suffixe können durch \"Komma\" getrennt angegeben werden: " +
            "\"txt,xml\" besagt, Dateien die mit \".txt\" oder \".xml\" enden " +
            "werden nicht - oder nur diese werden - in den Index " +
            "aufgenommen." +
            P2LibConst.LINE_SEPARATORx2 +
            "\"keine versteckten Dateien suchen\"\n" +
            "besagt, dass eben diese Dateien nicht in die Mediensammlung " +
            "aufgenommen werden, unabhängig davon ob die Datei in das Schema oben passen würde.";


    public static final String EXTERN_MEDIA_COLLECTION = "Externe Mediensammlungen werden nicht bei " +
            "jedem Programmstart neu eingelesen. Die können dadurch auf externen Medien liegen und " +
            "müssen nur einmal eingelesen werden.\n" +
            "Hier können externe Mediensammlungen verwaltet werden: " +
            "Neue anlegen, aktualisieren oder auch wieder löschen." +
            "";
    public static final String INTERN_MEDIA_COLLECTION = "Die hier angegebenen Ordner werden bei jedem Programmstart " +
            "nach Medien abgesucht. Diese Ordner werden also immer aktuell mit dem Programm abgeglichen.";

    public static final String RESET_DIALOG =
            "==> Einstellungen zum Abspielen und Aufzeichnen zurücksetzen" +

                    P2LibConst.LINE_SEPARATORx2 +
                    "Damit werden alle Sets (auch eigene), die zum Abspielen und Aufzeichnen der " +
                    "Filme gebraucht werden, gelöscht. Anschließend werden die aktuellen Standardsets eingerichtet. " +
                    "Es kann dann direkt damit weitergearbeitet werden. Abos und Blacklist bleiben erhalten." +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Das sollte vor dem kompletten Zurücksetzen des Programms versucht werden. " +

                    P2LibConst.LINE_SEPARATORx3 +
                    "=====   ODER   =====" +
                    P2LibConst.LINE_SEPARATORx3 +

                    "==> Alle Einstellungen zurücksetzen" +
                    P2LibConst.LINE_SEPARATORx2 +
                    "Damit wird das Programm in den Ursprungszustand zurückgesetzt. Es gehen " +
                    "ALLE Einstellungen verloren. Das Programm beendet sich " +
                    "und muss neu gestartet werden. Der neue Start beginnt " +
                    "mit dem Einrichtungsdialog." +
                    P2LibConst.LINE_SEPARATOR;
}
