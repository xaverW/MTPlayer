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

public class HelpTextPset {
    public static final String PSET_DEST_FILE_SIZE =
            "Die Länge des Dateinamens eines Downloads kann beschränkt werden.\n" +
                    "\n" +
                    "\"Länge des ganzen Dateinamens:\" Der gesamte Dateiname wird ermittelt und falls nötig gekürzt.\n" +
                    "\n" +
                    "\"Länge einzelner Felder:\" Die Länge einzelner Felder (Parameter) des Dateinamens wird begrenzt.\n" +
                    "Das bezieht sich nur auf die Felder mit variabler Länge:\n" +
                    "%t, %T, %s, %N (Thema, Titel, Sender, Originaldateiname).\n";

    public static final String PSET_SWITCH =
            "-- Schalter --\n" +
                    "In diesem Feld werden die Argumente (Schalter, Optionen, etc.) des jeweiligen " +
                    "Programms angegeben. Sie sollten in dessen Dokumentation zu finden sein." +
                    "\n" +
                    "\n" +
                    "Von MTPlayer können folgende Parameter genutzt werden:\n" +
                    "\n" +
                    "%f Original-URL des Films\n" +
                    "%a Zielverzeichnis des Downloads\n" +
                    "%b Dateiname des Downloads\n" +
                    "** (= zwei Sterne) Zielpfad (= Zielverzeichnis mit Dateiname)\n" +
                    "'**' ist identisch mit '%a/%b' (Windows: '%a\\%b')" +
                    "\n\n" +
                    "Für ffmpeg könnte der Schalter z.B. so aussehen:\n" +
                    "-user_agent \"Mozilla/5.0\" -i %f -c copy -bsf:a aac_adtstoasc **\n" +
                    "\n\n" +
                    "Weiter sind diese Parameter möglich (gedacht z.B. für Downloadmanager, oder Buttons " +
                    "die z.B. eine Suchmaschine aufrufen):" +
                    "\n\n" +
                    "%w Website-URL des Film\n" +
                    "%s Sender des Films\n" +
                    "%T Titel des Films\n" +
                    "%t Thema des Films\n" +
                    "\n" +
                    "Z.B. wäre das ein Google-Aufruf zur Suche nach dem Titel:" +
                    "\n\n" +
                    "Programm (Linux):   firefox\n" +
                    "Programm (Windows):   C:\\Program Files\\Mozilla Firefox\\firefox.exe\n" +
                    "Schalter:   http://www.google.de/search?q='%T'";

    public static final String PSET_FILE_HELP_PROG =
            "Hier werden die Programme zum jeweiligen Set eingetragen. Falls mehrere Programme " +
                    "eingetragen sind, bestimmen die Inhalte der Felder [Präfix] und [Suffix], für welche " +
                    "URLs jedes Programm zuständig ist.\n" +
                    "\n" +
                    "-- Zieldateiname --\n" +
                    "Kann für jedes Hilfsprogramm eigene Zieldateinamen festlegen. Ist das " +
                    "Feld leer, wird die Vorgabe des Sets (unter \"Speicherziel\") verwendet. Meist muss " +
                    "nichts angegeben werden.\n" +
                    "\n" +
                    "-- Programm --\n" +
                    "In dem Feld steht NUR das Programm: ('Pfad/Programmdatei', " +
                    "Windows: 'Pfad\\Programmdatei'). " +
                    "Keine Argumente (Schalter, Optionen, etc.)!\n" +
                    "\n" +
                    PSET_SWITCH +
                    "\n" +
                    "\n" +
                    "\n" +
                    "-- Restart --\n" +
                    "Hiermit kann festgelegt werden, wie sich das Programm bei einem Downloadfehler " +
                    "verhalten soll. Ist \"Restart\" eingeschaltet, wird der Download nochmal gestartet. " +
                    "Ansonsten wird er sofort auf *fehlgeschlagen* gesetzt.\n" +
                    "\n" +
                    "-- Downloadmanager --\n" +
                    "Wenn \"Downloadmanager\" eingeschaltet ist, übergibt MTPlayer die " +
                    "Film-URL an dieses Programm und registriert für sich den Download als *erfolgreich " +
                    "abgeschlossen*. Das eingerichtete Hilfsprogramm muss sich um den Download komplett selbst " +
                    "kümmern, und MTPlayer erhält darüber keine Rückmeldung.\n" +
                    "\n" +
                    "Beispiel für VLC:\n" +
                    "Programm: '/usr/bin/vlc'\n" +
                    "(Windows: '%PROGRAMFILES%\\VideoLAN\\VLC\\vlc.exe')\n" +
                    "Schalter: '%f :sout=#standard{access=file,mux=ts,dst=**} -I dummy --play-and-exit'\n" +
                    "Dateiname: '%t-%T.ts'\n" +
                    "\n" +
                    "Hier wird %f durch die URL des Films ersetzt. %t und %T werden durch einen Pfad und " +
                    "Dateinamen ersetzt und in den Programmschalter anstatt der '**' eingesetzt. Als " +
                    "Downloaddatei resultiert:\n" +
                    "'Volumes/Pfad/Thema-Titel.ts' bzw. 'C:\\Pfad\\Thema-Titel.ts'.\n" +
                    "\n" +
                    "Beispiel für ffmpeg (nur Windows):\n" +
                    "Der Pfad wird hier relativ zur Programmdatei von MTPlayer angegeben, weil MTPlayer für " +
                    "Windows das Programm ffmpeg schon im Ordner 'bin' mitbringt:\n" +
                    "Programm: 'bin\\ffmpeg.exe'\n" +
                    "Schalter: '-i %f -c copy -bsf:a aac_adtstoasc \"**\"'\n" +
                    "Dateiname: '%t-%T.mp4'\n" +
                    "\n" +
                    "Hier wird %f durch die URL des Films ersetzt. %t und %T werden durch einen Pfad und " +
                    "Dateinamen ersetzt und in den Programmschalter anstatt der '**' eingesetzt. Als " +
                    "Downloaddatei resultiert:\n" +
                    "'C:\\Pfad\\Thema-Titel.mp4'.";

    public static final String PSET_PARAMETER_FILE_NAME =
            "Diese Parameter sind möglich:\n" +
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
                    "%m Dauer des Films in Minuten, z.B.: 25\n" +
                    "%M Dauer des Films in Minuten, z.B.: 025\n" +
                    "\n" +
                    "%N Originaldateiname des Films (der kann sehr kryptisch und lang sein)\n" +
                    "%S Suffix des Originaldateinamens des Films (z.B. 'mp4')\n" +
                    "\n" +
                    "%i Filmnummer (ändert sich beim Neuladen der Filmliste!)\n" +
                    "%q Qualität des Films ('HD', 'H', 'L')\n" +
                    "\n" +
                    "%Z Hashwert der URL, z.B.: '1433245578'\n" +
                    "%z Hashwert der URL, angehängtes Suffix (entspricht '%Z.%S'), z.B.: '1433245578.mp4'\n" +
                    "\n\n" +
                    "Im Set kann eine maximale Länge eines \"Elements\". z.B. %T und die maximale Länge des gesamten " +
                    "Namens vorgegeben werden. Ist ein \"Element\" oder der gesamte Name länger, wird gekürzt." +
                    "\n\n" +
                    "Beispiele:\n" +
                    "Am 10.05.2021 liefert '%H__%t__%T' z.B. '20210510__Natur__Wildes Shetland' (kein Suffix)\n" +
                    "und '%H__%t__%T.%S' liefert z.B. '20210510__Natur__Wildes Shetland.xxx' (mit dem Originalsuffix)";


    public static final String HELP_PSET_PLAY =
            "\"Abspielen\" ruft ein Programm zur Wiedergabe auf, wenn ein Film zum Abspielen gestartet wird. " +
                    "Es kann nur ein Set dafür zuständig sein.";

    public static final String HELP_PSET_SAVE_ABO_BUTTON =
            "-- Funktion Speichern --\n" +
                    "\"Speichern\" ist für das Speichern eines Films zuständig. " +
                    "Wenn mehrere Sets zum Speichern angelegt sind, muss für jeden Film in einem " +
                    "Dialog das Set ausgewählt werden.\n" +
                    "\n" +
                    "-- Funktion Abo --\n" +
                    "\"Abo\": Wenn Abos genutzt werden, muss dafür ein Set bereitstehen, bei " +
                    "dem dies eingeschaltet ist.\n" +
                    "\n" +
                    "In der Regel eignet sich das Set \"Speichern\" auch für die Abos, es muss also meist für die " +
                    "Abos kein eigenes Set angelegt werden. Bei diesem Set (Speichern-Set) wird dann also " +
                    " \"Speichern\" und \"Abo\" eingeschaltet.\n" +
                    "\n" +
                    "-- Funktion Button --\n" +
                    "\"Button\" stellt einen Button für dieses Set in der Ansicht \"Filme\" zur " +
                    "Verfügung (unter der Tabelle, im Tab \"Startbutton\"), mit dem es direkt aus der " +
                    "Filmtabelle gestartet werden kann.";

    public static final String HELP_PSET_SAVE =
            "\"Speichern\" ist für das Speichern eines Films zuständig. " +
                    "Wenn mehrere Sets zum Speichern angelegt sind, muss für jeden Film in einem " +
                    "Dialog das Set ausgewählt werden.";

    public static final String HELP_PSET_ABO =
            "\"Abo\": Wenn Abos genutzt werden, muss dafür ein Set bereitstehen, bei " +
                    "dem dies eingeschaltet ist.\n" +
                    "\n" +
                    "In der Regel eignet sich das Set \"Speichern\" auch für die Abos, es muss also meist für die " +
                    "Abos kein eigenes Set angelegt werden. Bei diesem Set (Speichern-Set) wird dann also " +
                    " \"Speichern\" und \"Abo\" eingeschaltet.";

    public static final String HELP_PSET_BUTTON =
            "\"Button\" stellt einen Button für dieses Set in der Ansicht \"Filme\" zur " +
                    "Verfügung (unter der Tabelle, im Tab \"Startbutton\"), mit dem es direkt aus der " +
                    "Filmtabelle gestartet werden kann.";

    public static final String HELP_PSET =
            "Ein Set ist ein Satz von Einstellungen mit dem ein Film angesehen oder gespeichert werden kann. " +
                    "Im Set können Hilfsprogramme angegeben werden, die den Film weiter " +
                    "verarbeiten können z.B. herunterladen, " +
                    "abspielen, konvertieren,…\n" +
                    "\n" +
                    "MTPlayer benötigt mindestens ein Set zum Abspielen und eins zum Aufzeichnen. Diese zwei Sets " +
                    "werden beim ersten Programmstart mit Standardeinstellungen angelegt.\n" +
                    "\n" +
                    "-- Set-Funktionen --\n" +
                    "Die Funktion eines Sets kann im Kontextmenü der Tabelle oder in \"Funktionen\" " +
                    "angegeben werden.\n" +

                    "\n" +
                    "\"Abspielen\" ruft ein Programm zum Ansehen der Filme auf. " +
                    "Es kann nur ein Set dafür zuständig sein. In der Tabelle erscheint das Set zum " +
                    "Abspielen in roter Schrift.\n" +

                    "\n" +
                    "\"Speichern\" ist für das Speichern eines Films zuständig. Wenn mehrere Sets zum Speichern angelegt " +
                    "sind, muss für jeden Film in einem Dialog das Set ausgewählt werden.\n" +

                    "\n" +
                    "\"Abo\": Wenn Abos genutzt werden, muss dafür ein Set bereitstehen, bei dem dies eingeschaltet ist. " +
                    "In der Regel eignet sich das Set \"Speichern\" auch für die Abos, es muss also " +
                    "meist für die Abos kein eigenes Set angelegt werden. Beim Speichern-Set " +
                    "wird dann \"Speichern\" und \"Abo\" eingeschaltet.\n" +

                    "\n" +
                    "\"Button\" stellt einen Button für dieses Set in der Ansicht \"Filme\" zur Verfügung (unter der " +
                    "Tabelle, im Tab \"Startbutton\"). Mit dem Button kann dann direkt ein Film aus der Filmtabelle " +
                    "mit diesem Set gestartet werden.\n" +

                    "\n" +
                    "Soll ein Film mit einem Set aufgezeichnet werden, müssen [Zielpfad] und [Zieldateiname] " +
                    "in \"Speicherziel\" ausgefüllt werden.";
}
