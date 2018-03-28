/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

public class HelpText {

    public static final String SEARCH_MEDIA_DIALOG = "Ein Filterwort kann auch Leerzeichen enthalten, " +
            "z.B.: \"Hallo ich\". Es wird dann nach Dateien " +
            "gesucht, die im Namen \"Hallo ich\" an beliebiger " +
            "Stelle enthalten. Man sollte den Suchbegriff " +
            "also so formulieren, dass die eigenen Vorgaben " +
            "für Dateinamen erfüllt sind." +
            "\n" +
            "\n" +
            "Groß- und Kleinschreibung wird beim Suchen " +
            "nicht beachtet." +
            "\n" +
            "\n" +
            "In dem Filter kann auch mit regulären Ausdrücken (RegExp)  " +
            "gesucht werden. Diese müssen mit \"#:\" eingeleitet " +
            "werden." +
            "\n" +
            "\n" +
            "#:Abend.*\n" +
            "Das bedeutet z.B.: Es werden alle Dateien gefunden, die " +
            "mit \"Abend\" beginnen." +
            "\n" +
            "\n" +
            "Auch bei den regulären Ausdrücken spielt Groß- und " +
            "Kleinschreibung keine Rolle." +
            "\n" +
            "\n" +
            "https://de.wikipedia.org/wiki/Regul%C3%A4rer_Ausdruck";

    public static final String PROG_PATHS = "Hiermit können die Standardprogramme zum \"Ansehen\" und \"Aufzeichnen\" " +
            "der Filme eingetragen werden. Werden die Pfade nicht automatisch erkannt, " +
            "kann man sie auch per Hand auswählen." +
            "\n" +
            "\n" +
            "Um alle Filme ansehen und aufzeichnen zu können, müssen mindestens " +
            "diese Programme installiert sein:" +
            "\n" +
            "\n" +
            "VLC\n" +
            "Dieses Programm dient zum Ansehen und teilweise auch zum Aufzeichnen der Filme." +
            "\n" +
            "\n" +
            "flvstreamer\n" +
            "Damit können Flashfilme (die URL beginnt mit \"rtmp\") aufgezeichnet werden." +
            "\n" +
            "\n" +
            "ffmpeg\n" +
            "Zum Aufzeichnen von Playlisten (URL endet mit \"m3u8\")" +
            "\n" +
            "\n" +
            "\n" +
            "Linux:\n" +
            "Die Programme können über die Paketverwaltung eingespielt werden.\n" +
            "\n" +
            "\n" +
            "Windows, OS X:\n" +
            "Die Programme \"flvstreamer\" und \"ffmpeg\" sind bereits im Programm " +
            "integriert. Es muss bloss noch \"VLC\" aus dem Internet geladen werden:" +
            "\n" +
            "\n" +
            "\n" +
            "http://www.videolan.org\n" +
            "https://savannah.nongnu.org/projects/flvstreamer/\n" +
            "http://ffmpeg.org\n";

    public static final String MEDIA_DIALOG = "Die Mediensammlung kann im Tab \"Filme\" und Tab \"Download\" " +
            "mit der rechten Maustaste auf einen Film:\n" +
            "\"Titel in der Mediensammlung suchen\"\n" +
            "nach einem Filmtitel durchsucht werden. " +
            "\n" +
            "\n" +
            "\"Keine Dateien mit diesem Suffix\": Dateien mit einem dort " +
            "angegebenen Suffix werden nicht in den Index aufgenommen." +
            "\n" +
            "\n" +
            "\"Nur Dateien mit diesem Suffix\": Da werden dann genau die Dateien " +
            "genommen, auf die das zutrifft." +
            "\n" +
            "\n" +
            "Suffixe können durch \"Komma\" getrennt angegeben werden: " +
            "\"txt,xml\" besagt, Dateien die mit \".txt\" oder \".xml\" enden " +
            "werden nicht - oder nur diese werden - in den Index " +
            "aufgenommen." +
            "\n" +
            "\n" +
            "Die angegebenen Pfade zum Durchsuchen, werden beim Programmstart " +
            "abgesucht, die müssen also verfügbar sein. Dafür ist der aktuelle " +
            "Bestand an Filmen auch in der MedienDB enthalten." +
            "\n";


    public static final String FILTER_FELDER =
            "\n" +
                    "\"Sender\" und \"Thema\" können exakt verglichen werden. " +
                    "Das heißt, der im Feld \"Sender\\Thema\" angegebene Text muss " +
                    "genau dem \"Sender\\Thema\" des Films entsprechen. " +
                    "\n" +
                    "\n" +
                    "Bei den anderen Feldern (oder wenn exakt ausgeschaltet ist) " +
                    "muss die Eingabe im " +
                    "entsprechendem Feld des Films nur enthalten sein." +
                    "\n" +
                    "\n" +
                    "Beim Feld \"Thema/Titel\" muss der Filter im " +
                    "\"Thema\" ODER \"Titel\" enthalten sein." +
                    "\n";
    public static final String FILTER_EXAKT =
            "\n" +
                    "\"exakt\" bedeutet, dass z.B. \"Abend\" im Feld Thema nur die Filme " +
                    "findet, die genau das Thema \"Abend\" haben. " +
                    "Ist \"exakt\" ausgeschaltet und steht im Feld \"Sender\" z.B. \"a\" " +
                    "dann werden alle Sender die ein \"a\" enthalten gefunden!" +
                    "\n" +
                    "\n" +
                    "Groß- und Kleinschreibung wird beim Filtern " +
                    "nicht beachtet." +
                    "\n" +
                    "\n" +
                    "In allen Feldern (wenn nicht \"exakt\" eingestellt ist) " +
                    "kann auch nach mehreren Begriffen gesucht werden (diese " +
                    "werden durch \"Komma\" oder \"Doppelpunkt\" getrennt angegeben " +
                    "und können auch Leerzeichen enthalten)." +
                    "\n" +
                    "\"Sport,Fussball\" sucht nach Filmen die im jeweiligen Feld den " +
                    "Begriff \"Sport\" ODER \"Fussball\" haben." +
                    "\n" +
                    "\"Sport:Fussball\" sucht nach Filmen die im jeweiligen Feld den " +
                    "Begriff \"Sport\" UND \"Fussball\" haben." +
                    "\n" +
                    "\n" +
                    "In allen Feldern (wenn nicht \"exakt\" eingestellt ist) " +
                    "kann auch mit regulären Ausdrücken gesucht " +
                    "werden. Diese müssen mit \"#:\" eingeleitet werden. " +
                    "Auch bei den regulären Ausdrücken wird nicht zwischen " +
                    "Groß- und Kleinschreibung unterschieden. " +
                    "\n" +
                    "#:Abend.*\n" +
                    "Das bedeutet z.B.: Es werden alle Filme gefunden, die \n" +
                    "im jeweiligen Feld mit \"Abend\" beginnen.\n" +
                    "\n" +
                    "\n" +
                    "https://de.wikipedia.org/wiki/Regul%C3%A4rer_Ausdruck\n" +
                    "\n";

    public static final String GUI_FILM_FILTER =
            FILTER_FELDER +
                    "\n" +
                    "Im Filter \"Irgendwo\" wird zum Titel und Thema " +
                    "auch die Beschreibung geprüft." +
                    "\n" +
                    "\n" +
                    "Beim Feld \"URL\" muss der Filter in der URL " +
                    "des Films ODER der Website des Films enthalten sein." +
                    "\n" +
                    FILTER_EXAKT +
                    "\n" +
                    "\n" +
                    "Filterprofile:\n" +
                    "==================\n" +
                    "Mit den Buttons unten, kann man eingestellte Filter speichern " +
                    "und auch wieder abrufen. So wird der gespeicherte Zustand genau " +
                    "wieder hergestellt.";

    public static final String BLACKLIST_WHITELIST =
            "Bei der Funktion \"Blacklist\" werden Filme, die den " +
                    "Angaben in einer Zeile in der Tabelle entsprechen, " +
                    "nicht angezeigt." +
                    "\n\n" +
                    "Die Funktion \"Whitelist\" zeigt nur die Filme an, die " +
                    "den Angaben in einer Zeile in der Tabelle entsprechen." +
                    "\n\n" +
                    "Beim Umschalten \"Blacklist - Whitelist\" werden genau " +
                    "die vorher nicht angezeigten Filme jetzt angezeigt." +
                    "\n\n" +
                    FILTER_FELDER +
                    FILTER_EXAKT;

    public static final String CONFIG_GEO = "Nicht alle Filme lassen sich im Ausland abrufen.\n" +
            "Wenn geblockte Filme markiert werden sollen, kann man hier seinen Standort angeben.\n" +
            "\n" +
            "Der Download geogeblockter Sendungen bricht im Ausland sofort ab (Download \"fehlerhaft\").\n" +
            "MTPlayer kennt nicht alle Muster für geogeblockte Sendungen. Ob ein " +
            "fehlerhafter Download auf Geoblocking " +
            "zurückzuführen ist, zeigt sich beim Klick auf " +
            "den Link zur Sendung (\"zur Website\") ganz " +
            "unten im Tab \"Filme\". Wenn die Sendung " +
            "auch auf der Website des Senders nicht " +
            "abgespielt werden kann, liegt fast immer " +
            "Geoblocking vor.";

    public static final String GUI_FILME_EDIT_FILTER = "Hier können die Filter\n" +
            "die angezeigt werden sollen, ein- und ausgeschaltet werden.\n" +
            "\n" +
            "Ausgeschaltete Filter werden beim Suchen der Filme\n" +
            "auch nicht berücksichtigt.\n\n" +
            "Mit weniger Filtern ist auch der Suchvorgang schneller";

    public static final String SET = "" +
            "\n\n" +
            "Mehrere Einträge können mit \"Komma\" getrennt, angegeben werden";

    public static final String SETDATA_PRAEFIX = "Filme, deren URL mit \"Präfix\" beginnt und mit \"Suffix\" endet, " +
            "werden nicht mit einem Hilfsprogramm gespeichert, " +
            "sondern direkt geladen." +
            "\n\n" +
            "Mehrere Einträge können mit \"Komma\" getrennt, angegeben werden";

    public static final String SETDATA_RES = "Nicht jede Auflösung ist bei jedem Sender möglich und ist die " +
            "gewünschte Auflösung nicht verfügbar, " +
            "wird in \"hoher\" Auflösung geladen.\n\n" +
            "Die Auflösung gilt nur für manuell gestartete Downloads\n" +
            "und zum Abspielen von Filmen. Für Abos wird die\n" +
            "im Abo vorgegebene Auflösung verwendet.";

    public static final String ABO_RES = "Nicht jede Auflösung ist bei jedem Sender möglich und ist die " +
            "gewünschte Auflösung nicht verfügbar, " +
            "wird in \"hoher\" Auflösung geladen.";

    public static final String SETDATA_SUBDIR = "Es wird ein Unterordner mit dem Thema oder " +
            "Abozielpfad angelegt." +
            "\n" +
            "Ist der Abozielpfad leer, wird das Thema verwendete";

    public static final String SETDATA_RESET_COLOR = "Wird das Set als Button verwendet, " +
            "kann damit die Schriftfarbe verändert werden.";

    public static final String DOWNLOAD_REPLACELIST = "Die Tabelle wird von oben nach unten abgearbeitet. " +
            "Es ist also möglich, dass eine Ersetzung durch eine weitere " +
            "wieder ersetzt wird!";

    public static final String DOWNLOAD_ONLY_ASCII = "Es werden alle Zeichen \"über 127\" ersetzt. " +
            "Auch Umlaute wie \"ö -> oe\" werden ersetzt." +
            "\n\n" +
            "Wenn die Ersetzungstabelle aktiv ist, wird sie vorher abgearbeitet.";

    public static final String DOWNLOAD_ONE_SERVER = "Es gibt eine Begrenzung auf " +
            "2 Downloads pro Server " +
            "die nicht überschritten wird. " +
            "\n" +
            "\n" +
            "Das kann auch noch " +
            "auf 1 Download pro Server " +
            "(z.B. nur ein Download von \"www.zdf.de\") " +
            "weiter begrenzt werden." +
            "\n" +
            "\n" +
            "Das kann bei Downloadproblemen helfen.";

    public static final String DOWNLOAD_FINISHED = "Wird ein Download beendet, wird man mit einem Fenster informiert.";

    public static final String DOWNLOAD_ERROR = "Wird ein Download mit einem Fehler beendet, " +
            "wird man darüber mit einem Fenster informiert.";


    public static final String BLACKLIST_GEO = "Geogeblockte Filme können im jeweiligen \"Ausland\" nicht abgerufen werden. " +
            "\n\n" +
            "(Dazu muss die eigene Position in den Einstellungen angegeben werden)";

    public static final String BLACKLIST_SIZE = "Kurze Filme sind oft nur Trailer. Filme, die keine " +
            "Längenangabe haben, werden immer angezeigt.";

    public static final String BLACKLIST_DAYS = "Damit kann man die Anzahl der angezeigten Filme " +
            "reduzieren. Dadurch kann das Arbeiten mit den " +
            "Filmen schneller werden.";

    public static final String BLACKLIST_FUTURE = "Filme, deren Datum in der Zukunft liegt, sind meist nur Trailer.";

    public static final String BLACKLIST_ABO = "Wenn Downloads aus Abos gesucht werden, " +
            "werden alle gefundenen Filme geladen oder nur die, die nicht durch " +
            "die Blacklist geblockt werden.";

    public static final String LOAD_FILM_ONLY_DAYS = "Es werden nur Filme der letzten " +
            "xx Tage geladen. " +
            "Bei \"Alle\" werden alle Filme geladen. " +
            "\n" +
            "(Eine kleinere Filmliste " +
            "kann bei Rechnern mit wenig " +
            "Speicher hilfreich sein.) " +
            "\n" +
            "Auswirkung hat das erst nach dem " +
            "Neuladen der kompletten Filmliste.";

    public static final String LOAD_FILMLIST_PROGRAMSTART = "Die Filmliste wird beim Programmstart " +
            "automatisch geladen (wenn sie " +
            "älter als 3h ist). Zusätzlich kann sie über den " +
            "Button \"Filmliste\" " +
            "aktualisiert werden. Zum Update werden dann nur noch die " +
            "Differenzlisten geladen (enthalten " +
            "die neuen Filme).";

    public static final String LOAD_FILMLIST_MANUEL = "Die angegebene Datei/URL wird beim " +
            "Neuladen einer Filmliste verwendet. Ist nichts angegeben, wird die Filmliste auf herkömmliche " +
            "Art geladen und die URL dafür wird automatisch gewählt.";

    public static final String ABOS_SOFRT_SUCHEN = "Nach dem Neuladen einer Filmliste wird dann " +
            "sofort nach neuen Abos gesucht. Ansonsten muss man " +
            "im Tab Download auf \"Downloads aktualisieren\" klicken.";


    public static final String DOWNLOADS_AUS_ABOS_SOFORT_STARTEN = "Neu angelegte Downloads (aus Abos) werden " +
            "sofort gestartet. Ansonsten muss man sie selbst starten.";


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
}
