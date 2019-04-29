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

import de.p2tools.p2Lib.PConst;

public class HelpText {

    public static final String SEARCH_MEDIA_DIALOG = "Ein Filterwort kann auch Leerzeichen enthalten, " +
            "z.B.: \"Hallo ich\". Es wird dann nach Dateien " +
            "gesucht, die im Namen \"Hallo ich\" an beliebiger " +
            "Stelle enthalten. Man sollte den Suchbegriff " +
            "also so formulieren, dass die eigenen Vorgaben " +
            "für Dateinamen erfüllt sind." +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "Groß- und Kleinschreibung wird beim Suchen " +
            "nicht beachtet." +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "In dem Filter kann auch mit regulären Ausdrücken (RegExp)  " +
            "gesucht werden. Diese müssen mit \"#:\" eingeleitet " +
            "werden." +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "#:Abend.*" + PConst.LINE_SEPARATOR +
            "Das bedeutet z.B.: Es werden alle Dateien gefunden, die " +
            "mit \"Abend\" beginnen." +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "Auch bei den regulären Ausdrücken spielt Groß- und " +
            "Kleinschreibung keine Rolle." +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "https://de.wikipedia.org/wiki/Regul%C3%A4rer_Ausdruck";

    public static final String PROG_PATHS = "Hiermit können die Standardprogramme zum \"Ansehen\" und \"Aufzeichnen\" " +
            "der Filme eingetragen werden. Werden die Pfade nicht automatisch erkannt, " +
            "kann man sie auch per Hand auswählen." +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "Um alle Filme ansehen und aufzeichnen zu können, müssen mindestens " +
            "diese Programme installiert sein:" +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "VLC" + PConst.LINE_SEPARATOR +
            "Dieses Programm dient zum Ansehen und teilweise auch zum Aufzeichnen der Filme." +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "flvstreamer" + PConst.LINE_SEPARATOR +
            "Damit können Flashfilme (die URL beginnt mit \"rtmp\") aufgezeichnet werden." +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "ffmpeg" + PConst.LINE_SEPARATOR +
            "Zum Aufzeichnen von Playlisten (URL endet mit \"m3u8\")" +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "Linux:" + PConst.LINE_SEPARATOR +
            "Die Programme können über die Paketverwaltung eingespielt werden." + PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "Windows:" + PConst.LINE_SEPARATOR +
            "Die Programme \"flvstreamer\" und \"ffmpeg\" sind bereits im Programm " +
            "integriert. Es muss nur noch \"VLC\" aus dem Internet geladen werden:" +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "http://www.videolan.org" + PConst.LINE_SEPARATOR +
            "https://savannah.nongnu.org/projects/flvstreamer/" + PConst.LINE_SEPARATOR +
            "http://ffmpeg.org" + PConst.LINE_SEPARATOR;

    public static final String FILTER_FIELD =
            PConst.LINE_SEPARATOR +
                    "\"Sender\" und \"Thema\" können exakt verglichen werden. " +
                    "Das heißt, der im Feld \"Sender\\Thema\" angegebene Text muss " +
                    "genau dem \"Sender\\Thema\" des Films entsprechen. " +
                    PConst.LINE_SEPARATOR +
                    PConst.LINE_SEPARATOR +
                    "Bei den anderen Feldern (oder wenn exakt ausgeschaltet ist) " +
                    "muss die Eingabe im " +
                    "entsprechendem Feld des Films nur enthalten sein." +
                    PConst.LINE_SEPARATOR +
                    PConst.LINE_SEPARATOR +
                    "Beim Feld \"Thema/Titel\" muss der Filter im " +
                    "\"Thema\" ODER \"Titel\" enthalten sein." +
                    PConst.LINE_SEPARATOR;

    public static final String FILTER_EXACT =
            PConst.LINE_SEPARATOR +
                    "\"exakt\" bedeutet, dass z.B. \"Abend\" im Feld Thema nur die Filme " +
                    "findet, die genau das Thema \"Abend\" haben. " +
                    "Ist \"exakt\" ausgeschaltet und steht im Feld \"Sender\" z.B. \"a\" " +
                    "dann werden alle Sender die ein \"a\" enthalten gefunden!" +
                    PConst.LINE_SEPARATOR +
                    PConst.LINE_SEPARATOR +
                    "Groß- und Kleinschreibung wird beim Filtern " +
                    "nicht beachtet." +
                    PConst.LINE_SEPARATOR +
                    PConst.LINE_SEPARATOR +
                    "In allen Feldern (wenn nicht \"exakt\" eingestellt ist) " +
                    "kann auch nach mehreren Begriffen gesucht werden (diese " +
                    "werden durch \"Komma\" oder \"Doppelpunkt\" getrennt angegeben " +
                    "und können auch Leerzeichen enthalten)." +
                    PConst.LINE_SEPARATOR +
                    "\"Sport,Fussball\" sucht nach Filmen die im jeweiligen Feld den " +
                    "Begriff \"Sport\" ODER \"Fussball\" haben." +
                    PConst.LINE_SEPARATOR +
                    "\"Sport:Fussball\" sucht nach Filmen die im jeweiligen Feld den " +
                    "Begriff \"Sport\" UND \"Fussball\" haben." +
                    PConst.LINE_SEPARATOR +
                    PConst.LINE_SEPARATOR +
                    "In allen Feldern (wenn nicht \"exakt\" eingestellt ist) " +
                    "kann auch mit regulären Ausdrücken gesucht " +
                    "werden. Diese müssen mit \"#:\" eingeleitet werden. " +
                    "Auch bei den regulären Ausdrücken wird nicht zwischen " +
                    "Groß- und Kleinschreibung unterschieden. " +
                    PConst.LINE_SEPARATOR +
                    "#:Abend.*" + PConst.LINE_SEPARATOR +
                    "Das bedeutet z.B.: Es werden alle Filme gefunden, die " + PConst.LINE_SEPARATOR +
                    "im jeweiligen Feld mit \"Abend\" beginnen." + PConst.LINE_SEPARATOR +
                    PConst.LINE_SEPARATOR +
                    PConst.LINE_SEPARATOR +
                    "https://de.wikipedia.org/wiki/Regul%C3%A4rer_Ausdruck" + PConst.LINE_SEPARATOR +
                    PConst.LINE_SEPARATOR;

    public static final String GUI_FILM_FILTER =
            FILTER_FIELD +
                    PConst.LINE_SEPARATOR +
                    "Im Filter \"Irgendwo\" wird zum Titel und Thema " +
                    "auch die Beschreibung geprüft." +
                    PConst.LINE_SEPARATOR +
                    PConst.LINE_SEPARATOR +
                    "Beim Feld \"URL\" muss der Filter in der URL " +
                    "des Films ODER der Website des Films enthalten sein." +
                    PConst.LINE_SEPARATOR +
                    FILTER_EXACT +
                    PConst.LINE_SEPARATOR +
                    PConst.LINE_SEPARATOR +
                    "Filterprofile:" + PConst.LINE_SEPARATOR +
                    "==================" + PConst.LINE_SEPARATOR +
                    "Mit den Buttons unten, kann man eingestellte Filter speichern " +
                    "und auch wieder abrufen. So wird der gespeicherte Zustand genau " +
                    "wieder hergestellt.";

    public static final String BLACKLIST_WHITELIST =
            "Bei der Funktion \"Blacklist\" werden Filme, die den " +
                    "Angaben in einer Zeile in der Tabelle entsprechen, " +
                    "nicht angezeigt." +
                    PConst.LINE_SEPARATORx2 +
                    "Die Funktion \"Whitelist\" zeigt nur die Filme an, die " +
                    "den Angaben in einer Zeile in der Tabelle entsprechen." +
                    PConst.LINE_SEPARATORx2 +
                    "Beim Umschalten \"Blacklist - Whitelist\" werden genau " +
                    "die vorher nicht angezeigten Filme jetzt angezeigt." +
                    PConst.LINE_SEPARATORx2 +
                    "Mit \"Treffer zählen\" kann überprüft werden, wieviele Filme " +
                    "in der Filmliste jeder Eintrag in der Blacklist findet. Damit lässt " +
                    "sich die Blacklist optimieren. Eine kürzere Blacklist führt zu schnelleren " +
                    "Ergebnissen." +
                    PConst.LINE_SEPARATORx2 +

                    FILTER_FIELD +
                    FILTER_EXACT;

    public static final String CONFIG_GEO = "Nicht alle Filme lassen sich im Ausland abrufen." + PConst.LINE_SEPARATOR +
            "Wenn geblockte Filme markiert werden sollen, kann man hier seinen Standort angeben." + PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "Der Download geogeblockter Sendungen bricht im Ausland sofort ab (Download \"fehlerhaft\")." + PConst.LINE_SEPARATOR +
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
            PConst.LINE_SEPARATORx2 +
            "Ausgeschaltete Filter werden beim Suchen der Filme " +
            "auch nicht berücksichtigt." +
            PConst.LINE_SEPARATORx2 +
            "Mit weniger Filtern ist auch der Suchvorgang schneller";

    public static final String SET = "" +
            PConst.LINE_SEPARATORx2 +
            "Mehrere Einträge können mit \"Komma\" getrennt, angegeben werden";

    public static final String SETDATA_PRAEFIX = "Filme, deren URL mit \"Präfix\" beginnt und mit \"Suffix\" endet, " +
            "werden nicht mit einem Hilfsprogramm gespeichert, " +
            "sondern direkt geladen." +
            PConst.LINE_SEPARATORx2 +
            "Mehrere Einträge können mit \"Komma\" getrennt, angegeben werden." +
            PConst.LINE_SEPARATOR +
            "z.B.: mp4,mp3,m4v,flv,m4a";

    public static final String SETDATA_RES = "Nicht jede Auflösung ist bei jedem Sender möglich. Ist die " +
            "gewünschte Auflösung nicht verfügbar, " +
            "wird in \"hoher\" Auflösung geladen." +
            PConst.LINE_SEPARATORx2 +
            "Die Auflösung gilt nur für manuell gestartete Downloads " +
            "und zum Abspielen von Filmen. Für Abos wird die " +
            "im Abo vorgegebene Auflösung verwendet.";

    public static final String ABO_RES = "Nicht jede Auflösung ist bei jedem Sender möglich. Ist die " +
            "gewünschte Auflösung nicht verfügbar, " +
            "wird in \"hoher\" Auflösung geladen.";

    public static final String SETDATA_SUBDIR = "Bei Downloads aus Abos wird ein Unterordner mit dem " +
            "Abozielpfad angelegt. Ist der Abozielpfad leer, " +
            "wird das Thema des Films verwendete";

    public static final String SETDATA_RESET_COLOR = "Wird das Set als Button verwendet, " +
            "kann damit die Schriftfarbe verändert werden.";

    public static final String DOWNLOAD_REPLACELIST = "Die Tabelle wird von oben nach unten abgearbeitet. " +
            "Es ist also möglich, dass eine Ersetzung durch eine weitere " +
            "wieder ersetzt wird!";

    public static final String DOWNLOAD_ONLY_ASCII = "Es werden alle Zeichen \"über 127\" ersetzt. " +
            "Auch Umlaute wie \"ö -> oe\" werden ersetzt." +
            PConst.LINE_SEPARATORx2 +
            "Wenn die Ersetzungstabelle aktiv ist, wird sie vorher abgearbeitet.";

    public static final String DOWNLOAD_ONE_SERVER = "Es gibt eine Begrenzung auf " +
            "2 Downloads pro Server " +
            "die nicht überschritten wird. " +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "Das kann auch noch " +
            "auf 1 Download pro Server " +
            "(z.B. nur ein Download von \"www.zdf.de\") " +
            "weiter begrenzt werden." +
            PConst.LINE_SEPARATOR +
            PConst.LINE_SEPARATOR +
            "Das kann bei Downloadproblemen helfen.";

    public static final String DOWNLOAD_FINISHED = "Wird ein Download beendet, wird man mit einem Fenster informiert.";

    public static final String DOWNLOAD_ERROR = "Wird ein Download mit einem Fehler beendet, " +
            "wird man darüber mit einem Fenster informiert.";


    public static final String BLACKLIST_GEO = "Geogeblockte Filme können im jeweiligen \"Ausland\" nicht abgerufen werden. " +
            PConst.LINE_SEPARATORx2 +
            "(Dazu muss die eigene Position in den Einstellungen angegeben werden)";

    public static final String BLACKLIST_SIZE = "Kurze Filme sind oft nur Trailer. Filme, die keine " +
            "Längenangabe haben, werden immer angezeigt.";

    public static final String BLACKLIST_DAYS = "Damit kann man die Anzahl der angezeigten Filme " +
            "reduzieren. Dadurch kann das Arbeiten mit den " +
            "Filmen schneller werden.";

    public static final String BLACKLIST_COUNT = "Beim Treffer zählen wird jeder Film gegen die Blacklist geprüft. " +
            "Jeder Film läuft also die Blacklist von Anfang nach Ende ab und jeder Treffer wird gezählt. " +
            PConst.LINE_SEPARATORx2 +
            "Beim Filtern der Filmliste wird nach einem Treffer wird die Suche abgebrochen. Es beschleunigt die Suche " +
            "wenn die Blacklisteinträge mit den meisten Treffern am Anfang liegen";

    public static final String BLACKLIST_FUTURE = "Filme, deren Datum in der Zukunft liegt, sind meist nur Trailer.";

    public static final String BLACKLIST_ABO = "Wenn Downloads aus Abos gesucht werden, " +
            "werden alle gefundenen Filme geladen oder nur die, die nicht durch " +
            "die Blacklist geblockt werden.";

    public static final String LOAD_FILM_ONLY_DAYS = "Es werden nur Filme der letzten " +
            "xx Tage geladen. Filme ohne Datum werden immer geladen. " +
            PConst.LINE_SEPARATORx2 +
            "Bei \"Alle\" werden alle Filme geladen. Bei \"250 Tagen\" ist die Filmliste nur " +
            "etwa halb so groß: ~ 130.000 Filme. (Eine kleinere Filmliste kann bei älteren " +
            "Rechnern mit wenig Speicher hilfreich sein.) " +
            PConst.LINE_SEPARATORx2 +
            "Auswirkung hat das erst nach dem Neustart des Programms oder dem " +
            "Neuladen der kompletten Filmliste.";

    public static final String LOAD_FILMLIST_SENDER = "Filme der markierten Sender " +
            "werden beim Neuladen der Filmliste  _nicht_  geladen." +
            PConst.LINE_SEPARATORx2 +
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

    public static final String SEARCH_ABOS_IMMEDIATELY = "Nach dem Neuladen einer Filmliste wird dann " +
            "sofort nach neuen Abos gesucht. Ansonsten muss man " +
            "im Tab Download auf \"Downloads aktualisieren\" klicken.";

    public static final String SMALL_BUTTON = "In der Tabelle Filme und Downloads können auch " +
            "kleine Buttons angezeigt werden. Die Zeilenhöhe wird dadurch kleiner.";

    public static final String USER_AGENT = "Hier kann ein User Agent angegeben werden. " +
            "Bei Downloads wird er dann als Absender verwendet. Bleibt das Feld leer, wird kein User Agent " +
            "verwendet. Solange alles funktioniert, kann das Feld leer bleiben. Ansonsten wäre das z.B. eine " +
            "Möglichkeit: " +
            PConst.LINE_SEPARATORx2 +
            "\"Mozilla/5.0\"" +
            PConst.LINE_SEPARATORx2 +
            "(Es sind nur ASCII-Zeichen erlaubt und die Textlänge ist begrenzt auf 100 Zeichen)";

    public static final String START_DOWNLOADS_FROM_ABOS_IMMEDIATELY = "Neu angelegte Downloads (aus Abos) werden " +
            "sofort gestartet. Ansonsten muss man sie selbst starten.";

    public static final String LOGFILE = "Hier kann ein Ordner angegeben werden " +
            "in dem ein Logfile erstellt wird. Darin wird der Programmverlauf skizziert. " +
            "Das kann hilfreich sein, wenn das Programm nicht wie erwartet funktioniert." + PConst.LINE_SEPARATORx2 +
            "Der Standardordner ist \"Log\" im Konfigordner des Programms." + PConst.LINE_SEPARATORx2 +
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
            "Download eines Films, kann dieser mit der Mediensammlung abgeglichen werden. So können doppelte " +
            "Downloads vermieden werden. Im Tab \"Filme\" und Tab \"Download\" kann " +
            "mit der rechten Maustaste auf einen Film: \"Titel in der Mediensammlung suchen\" " +
            "nach dem Filmtitel gesucht werden." +
            PConst.LINE_SEPARATORx3 +
            "Dazu werden Ordner mit Medien angegeben, die das Programm absucht. " +
            "Die angegebenen Pfade zum Durchsuchen, werden beim Programmstart " +
            "abgesucht, diese müssen also verfügbar sein. Dafür ist der aktuelle " +
            "Bestand an Filmen auch in der MedienDB enthalten." +
            PConst.LINE_SEPARATOR +
            "Externe Medien werden nur beim Anlegen abgesucht. " +
            "Deren Inhalt an Medien steht dauerhaft im Programm  und " +
            "bei der Suche in der Mediensammlung zur Verfügung. " +
            "Diese müssen also beim Programmstart nicht vorhanden sein.";

    public static final String MEDIA_COLLECTION = "\"Keine Dateien mit diesem Suffix\": Dateien mit einem dort " +
            "angegebenen Suffix werden nicht in den Index aufgenommen." +
            PConst.LINE_SEPARATORx2 +
            "\"Nur Dateien mit diesem Suffix\": Da werden dann genau die Dateien " +
            "genommen, auf die das zutrifft." +
            PConst.LINE_SEPARATORx2 +
            "Suffixe können durch \"Komma\" getrennt angegeben werden: " +
            "\"txt,xml\" besagt, Dateien die mit \".txt\" oder \".xml\" enden " +
            "werden nicht - oder nur diese werden - in den Index " +
            "aufgenommen." +
            PConst.LINE_SEPARATORx2 +
            "\"keine versteckten Dateien suchen\" besagt, dass eben diese Dateien nicht in die Mediensammlung " +
            "aufgenommen werden, unabhängig davon ob die Datei in das Schema oben passen würde.";


    public static final String EXTERN_MEDIA_COLLECTION = "Externe Mediensammlungen werden nicht bei " +
            "jedem Programmstart neu eingelesen. Die können dadurch auf externen Medien liegen und " +
            "müssen nur einmal eingelesen werden. Hier können externe Mediensammlungen verwaltet werden:" + PConst.LINE_SEPARATOR +
            "Neue anlegen, aktualisieren oder auch wieder löschen." +
            "";
    public static final String INTERN_MEDIA_COLLECTION = "Die hier angegebenen Ordner werden bei jedem Programmstart " +
            "nach Medien abgesucht. Diese Ordner werden also immer aktuell mit dem Programm abgeglichen.";

    public static final String RESET_DIALOG =
            "==> Einstellungen zum Abspielen und Aufzeichnen zurücksetzen" +

                    PConst.LINE_SEPARATORx2 +
                    "Damit werden alle Sets (auch eigene), die zum Abspielen und Aufzeichnen der " +
                    "Filme gebraucht werden, gelöscht. Anschließend werden die aktuellen Standardsets eingerichtet. " +
                    "Es kann dann direkt damit weitergearbeitet werden. Abos und Blacklist bleiben erhalten." +
                    PConst.LINE_SEPARATORx2 +
                    "Das sollte vor dem kompletten Zurücksetzen des Programms versucht werden. " +

                    PConst.LINE_SEPARATORx3 +
                    "=====   ODER   =====" +
                    PConst.LINE_SEPARATORx3 +

                    "==> Alle Einstellungen zurücksetzen" +
                    PConst.LINE_SEPARATORx2 +
                    "Damit wird das Programm in den Ursprungszustand zurückgesetzt. Es gehen " +
                    "ALLE Einstellungen verloren. Das Programm beendet sich " +
                    "und muss neu gestartet werden. Der neue Start beginnt " +
                    "mit dem Einrichtungsdialog." +
                    PConst.LINE_SEPARATOR;
}
