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


package de.p2tools.mtplayer.gui.tools;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.ptipofday.PTipOfDay;
import de.p2tools.p2lib.guitools.ptipofday.PTipOfDayDialog;
import de.p2tools.p2lib.guitools.ptipofday.PTipOfDayFactory;
import de.p2tools.p2lib.tools.date.DateFactory;
import de.p2tools.p2lib.tools.log.PLog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProgTipOfDayFactory {

    private static final String START = "                                                     " + P2LibConst.LINE_SEPARATOR;
    private static final int listSize = 17;

    private ProgTipOfDayFactory() {
    }

    public static void showDialog(ProgData progData, boolean showAlways) {
        if (!showAlways && !ProgConfig.TIP_OF_DAY_SHOW.getValue()) {
            //dann wills der User nicht :(
            PLog.sysLog("TipOfDay: Will der User nicht");
            return;
        }

        if (ProgData.debug || showAlways ||
                !ProgConfig.TIP_OF_DAY_DATE.get().equals(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date())) &&
                        PTipOfDayFactory.containsToolTipNotShown(ProgConfig.TIP_OF_DAY_WAS_SHOWN.get(), listSize)) {

            //nur wenn "DEBUG" / "immer" / heute noch nicht und nicht angezeigte ToolTips enthalten sind
            ProgConfig.TIP_OF_DAY_DATE.setValue(DateFactory.F_FORMAT_yyyy_MM_dd.format(new Date()));

            final List<PTipOfDay> pTipOfDayArrayList = new ArrayList<>();
            addTips(pTipOfDayArrayList);
            new PTipOfDayDialog(progData.primaryStage, pTipOfDayArrayList,
                    ProgConfig.TIP_OF_DAY_WAS_SHOWN, ProgConfig.TIP_OF_DAY_SHOW);
        } else {
            PLog.sysLog("TipOfDay: Heute schon gemacht oder keine neuen Tips");
        }
    }

    private static void addTips(List<PTipOfDay> pToolTipList) {
        String text = START;
        text += "Die Filteransicht kann über einen\n" +
                "zweiten Klick mit der rechten\n" +
                "Maustaste auf den Tab-Button\n" +
                "(Filme, Downloads oder Abos)\n" +
                "ein- und ausgeblendet werden.\n\n" +
                "Ein Klick mit der linken\n" +
                "Maustaste blendet den\n" +
                "Infobereich unter der\n" +
                "Tabelle ein und aus.\n\n" +
                "Beides ist auch über das\n" +
                "Menü möglich";
        String image = "/de/p2tools/mtplayer/res/tooltips/GuiFilme_Filter.png";
        PTipOfDay pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Suche in der Mediensammlung:\n\n" +
                "Ein Doppelklick auf einen\n" +
                "Suchbegriff stellt diesen frei.\n\n" +
                "Der Button daneben, stellt den\n" +
                "ursprünglichen Suchbegriff\n" +
                "wieder her.";
        image = "/de/p2tools/mtplayer/res/tooltips/Mediensammlung.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Über das Menü ist die\n" +
                "Funktion zum Zurücksetzen\n" +
                "von Programmeinstellungen\n" +
                "erreichbar.\n\n" +
                "Zuerst sollten nur die\n" +
                "Einstellungen zum Abspielen\n" +
                "und Aufzeichnen zurückgesetzt\n" +
                "werden.";
        image = "/de/p2tools/mtplayer/res/tooltips/Ruecksetzen.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Über das Menü ist die\n" +
                "Funktion zum Zurücksetzen\n" +
                "von Programmeinstellungen\n" +
                "erreichbar.\n\n" +
                "Wenn gar nichts mehr geht,\n" +
                "kann das Programm komplett\n" +
                "zurückgesetzt werden.\n\n" +
                "Es werden alle Einstellungen\n" +
                "zurückgesetzt und gehen\n" +
                "verloren!";
        image = "/de/p2tools/mtplayer/res/tooltips/Ruecksetzen_alles.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Im Download-Filter Panel können\n" +
                "auch noch weitere Einstellungen\n" +
                "vorgenommen werden:\n\n" +
                "* Die maximale Anzahl der\n" +
                "   gleichzeitigen Downloads kann\n" +
                "   hier vorgegeben werden.\n\n" +
                "* Die maximale Bandbreite pro\n" +
                "   Download kann hier\n" +
                "   vorgegeben werden.";
        image = "/de/p2tools/mtplayer/res/tooltips/Bandbreite.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        pToolTipList.add(PTipOfDay.getTipWebsite(ProgConfig.SYSTEM_PROG_OPEN_URL));

        text = START;
        text += "In den Einstellungen des Abos\n" +
                "kann auch die maximale Zeit,\n" +
                "die zurück gesucht wird,\n" +
                "vorgegeben werden.\n\n" +
                "Es werden also nur Filme der\n" +
                "letzten xx Tage gesucht wenn\n" +
                "xx Tage dort vorgegeben sind.";
        image = "/de/p2tools/mtplayer/res/tooltips/AboEinstellungen_Zeit.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen des Abos\n" +
                "kann auch die Filmlänge\n" +
                "eines Films (min, max)\n" +
                "vorgegeben werden.\n\n" +
                "Es werden dann nur Filme\n" +
                "gefunden, deren Dauer zu\n" +
                "den Vorgaben passt.";
        image = "/de/p2tools/mtplayer/res/tooltips/AboEinstellungen_Dauer.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Im Programm sind immer zwei\n" +
                "Menüs sichtbar.\n\n" +
                "Das obere enthält Menüpunkte,\n" +
                "die für das ganze Programm\n" +
                "wichtig sind.\n\n" +
                "Das untere Menü ist immer\n" +
                "für den jeweils angezeigten\n" +
                "Tab (Filme, Downloads Abos).";
        image = "/de/p2tools/mtplayer/res/tooltips/Menue.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann das Systemtray\n" +
                "ein- und ausgeschaltet werden.\n\n" +
                "Im Systemtray werden Infos\n" +
                "zur aktuellen Situation des\n" +
                "Programms angezeigt.";
        image = "/de/p2tools/mtplayer/res/tooltips/Einstellungen_Systemtray.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann die Filmliste beim\n" +
                "Laden beschränkt werden:\n\n" +
                "Z.B. können nur die letzten\n" +
                "50 Tage geladen werden. Filme\n" +
                "die kein Datum haben, werden\n" +
                "immer geladen.\n\n" +
                "Das hat den Vorteil, dass die\n" +
                "Filmliste deutlich kleiner ist\n" +
                "und das Programm schneller\n" +
                "reagiert (vor allem wenn der\n" +
                "Rechner mit wenig Speicher\n" +
                "ausgestattet ist).";
        image = "/de/p2tools/mtplayer/res/tooltips/Einstellungen_Filmliste.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "wird der Downloadpfad\n" +
                "und -name für Downloads\n" +
                "vorgegeben. Der Hilfebutton\n" +
                "verrät die Möglichkeiten.";
        image = "/de/p2tools/mtplayer/res/tooltips/Einstellungen_SetSpeicherziel.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "können für Downloads die ein\n" +
                "Hilfsprogramm brauchen, die\n" +
                "entsprechenden Programme\n" +
                "und Einstellungen vorgegeben\n" +
                "werden.\n\n" +
                "Es wird auch angegeben, für\n" +
                "welche Downloads das zutrifft.";
        image = "/de/p2tools/mtplayer/res/tooltips/Einstellungen_SetHilfsprogramme.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann die verwendete\n" +
                "Schriftgröße im Programm\n" +
                "vorgegeben werden.";
        image = "/de/p2tools/mtplayer/res/tooltips/Einstellungen_Schriftgroesse.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Tabellen kann\n" +
                "mit der Leertaste nach\n" +
                "unten \"geblättert\" werden.";
        image = "/de/p2tools/mtplayer/res/tooltips/Leertaste.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Einstellungen des Abos\n" +
                "kann auch die Startzeit\n" +
                "der Downloads aus diesem\n" +
                "Abo vorgegeben werden.\n\n" +
                "Downloads aus diesem Abo\n" +
                "haben dann automatisch\n" +
                "diese Startzeit.";
        image = "/de/p2tools/mtplayer/res/tooltips/AboEinstellungen_Startzeit.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Filtereinstellungen bei\n" +
                "den Filmen kann eine Wartezeit\n" +
                "vorgegeben werden.\n\n" +
                "Diese wird dann bei der Suche\n" +
                "in den Textfeldern abgewartet,\n" +
                "bis die Suche beginnt.\n\n" +
                "Es wird dann nicht bei jedem\n" +
                "Buchstaben die Suche gestartet.";
        image = "/de/p2tools/mtplayer/res/tooltips/GuiFilme_Wartezeit.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);
    }
}
