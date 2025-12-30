/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.tips;

import java.util.ArrayList;
import java.util.List;

public class TipListSet {

    private TipListSet() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();

        String text = "Sets sind die Einstellungen " +
                "die zum Aufzeichnen und Abspielen " +
                "der Filme gebraucht werden." +
                "\n\n" +
                "Es muss mindestens ein Set zum Abspielen " +
                "und eins zum Aufzeichnen vorhanden sein.\n\n" +
                "In den Programm-Einstellungen können die Sets " +
                "angelegt und geändert werden. " +
                "\n\n" +
                "Ein Set hat einen Namen (nur zur eigenen Info) und eine " +
                "Funktion (Abspielen, Speichern, ...) " +
                "Die Sets zum Speichern haben ein Speicherziel " +
                "(wo, wie und welchen Namen der gespeicherte Film haben soll." +
                "\n\n" +
                "In den Hilfsprogrammen wird aufgeführt, welches Programms " +
                "den Film abspielen oder speichern soll.";
        String image = "/de/p2tools/mtplayer/res/tips/set/set-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "In \"Einstellungen\" wird der Name und eine " +
                "Beschreibung des Sets eingetragen. Das dient nur " +
                "für die eigene Info.";
        image = "/de/p2tools/mtplayer/res/tips/set/set-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "\"Funktion\" legt fest, was das Set machen soll." +
                "\n\n" +
                "\"Abspielen\" ist das Set, um Filme anzuzeigen. Dafür kann " +
                "es nur eins geben." +
                "\n\n" +
                "\"Speichern\" ist ein Set, mit dem Filme gespeichert werden. Eins muss " +
                "mindestens vorhanden sein. Es können aber auch mehrere sein. Bei einem Download " +
                "wird man dann vorher gefragt, mit welchem Set gespeichert werde soll (die Sets haben " +
                "z.B.: unterschiedliche Pfade)." +
                "\n\n" +
                "\"Abo\" ist das Set, das Abos zum Speichern verwenden. Meist reicht eins, das dann " +
                "die Funktion: \"Download\" und \"Abo\" hat." +
                "\n\n" +
                "Alle Sets die \"Button\" haben, werden unter der Filmliste " +
                "im Info-Tab \"Button\" angezeigt. Mit den dort angezeigten Button " +
                "kann dann ein Film direkt mit dem Set gestartet werden. So kann man z.B.: ein Set " +
                "anlegen, dass den Browser öffnet und den Filmtitel einer Suchmaschine übergibt. Die Farbe " +
                "wird als Rahmenfarbe für die Button verwendet.";
        image = "/de/p2tools/mtplayer/res/tips/set/set-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "In \"Speicherziel\" legt man fest, wo und mit welchem Dateinamen der Film gespeichert " +
                "werden soll (das braucht es natürlich nur für Sets die auch Speichern).";
        image = "/de/p2tools/mtplayer/res/tips/set/set-4.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "In \"Download\" kann man z.B.: die Auflösung des Films vorgeben. Das Programm kann normale " +
                "Datei-Downloads selbst machen. Hier wird vorgegeben, wie die URL aussehen muss, dass der " +
                "Download vom Programm selbst gemacht werden soll. Die Standardeinstellungen sollten " +
                "meist passen.";
        image = "/de/p2tools/mtplayer/res/tips/set/set-5.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "In \"Hilfsprogramme\" werden die Programme festgelegt, die den Film verarbeiten " +
                "(Abspielen, Speichern, an einen Downloadmanager oder " +
                "Browser übergeben, ..).";
        image = "/de/p2tools/mtplayer/res/tips/set/set-6.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
