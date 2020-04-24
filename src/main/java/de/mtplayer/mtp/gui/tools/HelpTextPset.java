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

public class HelpTextPset {

    public static final String PSET_FILE_NAME = "Beim Dateinamen sind diese Parameter möglich:\n" +
            "\n" +
            "%D Sendedatum des Films oder \"heute\", wenn Sendedatum leer\n" +
            "%d Sendezeit des Films oder \"jetzt\", wenn Sendezeit leer\n" +
            "%H \"heute\", aktuelles Datum\n" +
            "%h \"jetzt\", aktuelle Uhrzeit\n" +
            "Datum in der Form: JJJJMMTT z.B. 20090815 (15.08.2009)\n" +
            "Zeit in der Form: HHMMss z.B. 152059 (15:20:59)\n" +
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
            "%N Originaldateiname des Films (der kann sehr kryptisch und lang sein)\n" +
            "%S Suffix des Originaldateinamens des Films (z.B. \"mp4\")\n" +
            "\n" +
            "%i Filmnummer (die ändert sich beim Neuladen der Filmliste!)\n" +
            "%q Qualität des Films (\"HD\", \"H\", \"L\")\n" +
            "\n" +
            "%Z Hashwert der URL, z.B.: 1433245578\n" +
            "%z Hashwert der URL + Suffix, entspricht also:   %Z.%S   z.B.: 1433245578.mp4\n" +
            "\n" +
            "Damit kann man einen Namen z.B. so aufbauen:\n" +
            "%H__%t__%T.mp4  \t -> \t  20131206__Doku__Titel_der_Doku.mp4\n" +
            "%H__%t__%T.%S  \t -> \t  20131206__Doku__Titel_der_Doku.xxx (hier wird die Originaldateiendung verwendet)\n";

    public static final String HELP_PSET = "\n" +
            "Ein Set ist ein Satz von Hilfsprogrammen, an welches eine URL eines Films übergeben wird. " +
            "Mit dem Programm wird der Film dann aufgezeichnet oder angezeigt.\n" +
            "\n" +
            "====================\n" +
            "Externer Downloadmanager\n" +
            "===\n" +
            "Ist dieser Punkt selektiert, geht das Programm davon aus, dass das eingerichtete" +
            "\n" +
            "Hilfsprogramm sich um den Download komplett selbst kümmert. Für MTPlayer ist mit dem " +
            "Aufruf des Hilfsprogramms der Download erfolgreich abgeschlossen." +
            "\n" +
            "\n" +
            "\n" +
            "\n" +
            "====================\n" +
            "Arten:\n" +
            "===\n" +
            "\n" +
            "Es muss je ein Set zum Abspielen und eins zum Aufzeichnen angelegt sein. " +
            "Diese werden beim ersten Programmstart mit den \"Standardeinstellungen\" angelegt. Das " +
            "Set \"Abspielen\" wird gestartet, wenn ein Film zum Abspielen gestartet wird, das " +
            "Set \"Speichern\" ist dann für das Speichern eines Films zuständig. Sind mehrere Sets " +
            "zum Speichern angelegt, wird das Set zum Speichern für jeden Film in einem Dialog abgefragt.\n" +
            "\n" +
            "Werden Abos genutzt, muss auch dafür ein Set angelegt sein. Meist eignet sich dafür das " +
            "Set \"Speichern\", bei diesem ist also \"Speichern\" und \"Abo\" eingeschaltet.\n" +
            "\n" +
            "\"Button\" ist eine Möglichkeit weitere Programme (Sets) einzurichten. Diese werden dann im " +
            "Tab Filme unter der Tabelle angezeigt. Ein Film kann dann mit dem " +
            "Set gestartet werden.\n" +
            "\n" +
            "\n" +
            "====================\n" +
            "Details der Sets:\n" +
            "===\n" +
            "\n" +
            "Soll ein Film mit einem Set aufgezeichnet werden, ist die Angabe des \"Zielpfads\" und " +
            "des \"Zieldateinamens\" wichtig. " +

            PSET_FILE_NAME +

            "\n" +
            "\"Direkter Download, Präfix, Suffix\": Damit kann man URLs angeben, die nicht über ein Programm " +
            "geladen werden sollen, sondern direkt als Download heruntergeladen werden.\n" +
            "\n" +
            "\n" +
            "Die Auflösung kann ebenfalls vorgegeben werden. Existiert für den Film die angegebene Auflösung " +
            "nicht, wird die nächstkleinere verwendet.\n" +
            "\n" +
            "\n" +
            "Die Länge eines Dateinamens eines Downloads kann mit: \n" +
            "\"ganzen Dateiname beschränken auf:\"\n" +
            "\"einzelne Felder beschränken auf:\"\n" +
            "begrenzt werden. Das erste bezieht sich auf den gesamten Dateinamen. Der wird ermittelt und dann evtl. " +
            "gekürzt. Mit dem zweiten Wert kann man die Länge eins Feldes/Parameters eines Namens begrenzen. " +
            "Das bezieht sich nur auf Felder mit variabler Länge:\n" +
            "%t, %T, %s, %N -> Thema, Titel, Sender, Originaldateiname\n" +
            "\n" +
            "\n" +
            "====================\n" +
            "Hilfsprogramme:\n" +
            "===\n" +
            "\n" +
            "Hier werden die Programme zum jeweiligen Set eingetragen. Sind mehrere Programme eingetragen, " +
            "kann man zu jedem Programm über die Felder Präfix und Suffix wählen, für welche URL ein Programm " +
            "zuständig ist.\n" +
            "\n" +
            "\"Zieldateiname\": Damit kann ein eigener Zieldateiname für das jeweilige Programm gewählt werden. " +
            "Ist das Feld leer, wird der Zieldateiname des Sets verwendet. Meist muss nichts angegeben werden.\n" +
            "\n" +
            "\"Programm\": In dem Feld steht NUR!! das Programm: \"Pfad/Programmdatei\"\n" +
            "\n" +
            "\"Schalter\": In diesem Feld werden die Programmschalter angegeben, die das Programm zum Start braucht. " +
            "Mögliche Parameter sind:\n" +
            "\n" +
            "Diese Angaben werden durch die URL ersetzt:\n" +
            "%f ist die URL des Films (Original-URL)\n" +
            "%F ist die URL des Films für den flvstreamer vorbereitet\n" +
            "\n" +
            "Zwei Sterne \"**\" werden durch den Zielpfad (Zielverzeichnis mit Dateinamen) ersetzt.\n" +
            "\n" +
            "Zusätzlich ist im Schalter:\n" +
            "%a  für das Zielverzeichnis und\n" +
            "%b  für den Dateinamen\n" +
            "des Films möglich. Statt \"**\" wäre also auch \"%a/%b\" möglich.\n" +
            "\n" +
            "==================================================\n" +
            "Beispiel für den VLC:\n" +
            "Programm: \"/usr/bin/vlc\"\n" +
            "Schalter: \"%f :sout=#standard{access=file,mux=ts,dst=**} -I dummy --play-and-exit\"\n" +
            "Dateiname: \"%t-%T.ts\"\n" +
            "\n" +
            "Hier wird %f durch die URL des Films ersetzt. %t und %T werden durch einen Pfad und Dateinamen  ersetzt " +
            "und in den Programmschalter anstatt der \"**\" eingesetzt. Als Downloaddatei resultiert:\n" +
            "Volumes/Pfad/Thema-Titel.ts \n" +
            "bzw. C:\\Pfad\\Thema-Titel.ts.\n" +
            "\n" +
            "==================================================\n" +
            "Beispiel für den ffmpeg:\n" +
            "Programm: \"bin\\ffmpeg.exe\"\n" +
            "Schalter: \"-i %f -c copy -bsf:a aac_adtstoasc \"**\"\"\n" +
            "Dateiname: \"%t-%T.mp4\"\n" +
            "\n" +
            "Hier wird %f durch die URL des Films ersetzt. %t und %T werden durch einen Pfad und Dateinamen ersetzt " +
            "und in den Programmschalter anstatt der \"**\" eingesetzt. Als Downloaddatei resultiert:\n" +
            "Volumes/Pfad/Thema-Titel.mp4 \n" +
            "bzw. C:\\Pfad\\Thema-Titel.mp4.\n" +
            "\n" +
            "Der Pfad wird hier relativ zur Programmdatei von MTPlayer angegeben. Es wird als Programm ffmpeg " +
            "verwendet, das MTPlayer im Ordner \"bin\" mitbringt (nur für Windows).\n" +
            "\n";


}
