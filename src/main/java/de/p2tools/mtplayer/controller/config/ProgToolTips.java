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


package de.p2tools.mtplayer.controller.config;

import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.guiTools.pToolTip.PToolTip;
import de.p2tools.p2Lib.guiTools.pToolTip.PToolTipDialog;
import de.p2tools.p2Lib.guiTools.pToolTip.PToolTipFactory;

import java.util.ArrayList;
import java.util.List;

public class ProgToolTips {

    private int listSize = 0;

    public ProgToolTips() {
    }

    public void showDialog(ProgData progData, boolean showAlways) {
        if (!showAlways && ProgConfig.TOOLTIPS_DONT_SHOW.getBool()) {
            //dann wills der User nicht :(
            return;
        }

        if (ProgData.debug ||
                showAlways || PToolTipFactory.containsToolTipNotShown(ProgConfig.TOOLTIPS_SHOWN.get(), listSize)) {
            //nur wenn "immer" / oder noch nicht angezeigte ToolTips enthalten sind
            final List<PToolTip> pToolTipList = new ArrayList<>();
            addToolTips(pToolTipList);
            new PToolTipDialog(progData.primaryStage, pToolTipList,
                    ProgConfig.TOOLTIPS_SHOWN.getStringProperty(), ProgConfig.TOOLTIPS_DONT_SHOW.getBooleanProperty());
        }
    }

    private final String START = "                                    " + P2LibConst.LINE_SEPARATOR;

    private void addToolTips(List<PToolTip> pToolTipList) {
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
        String image = "/de/p2tools/mtplayer/res/toolTips/GuiFilme_Filter.png";
        PToolTip pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
        text += "Suche in der Mediensammlung:\n\n" +
                "Ein Doppelklick auf einen\n" +
                "Suchbegriff stellt diesen frei.\n\n" +
                "Der Button daneben, stellt den\n" +
                "ursprünglichen Suchbegriff\n" +
                "wieder her.";
        image = "/de/p2tools/mtplayer/res/toolTips/Mediensammlung.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
        text += "Über das Menü ist die\n" +
                "Funktion zum Zurücksetzen\n" +
                "von Programmeinstellungen\n" +
                "erreichbar.\n\n" +
                "Zuerst sollten nur die\n" +
                "Einstellungen zum Abspielen\n" +
                "und Aufzeichnen zurückgesetzt\n" +
                "werden.";
        image = "/de/p2tools/mtplayer/res/toolTips/Ruecksetzen.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
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
        image = "/de/p2tools/mtplayer/res/toolTips/Ruecksetzen_alles.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
        text += "Im Download-Filter Panel können\n" +
                "auch noch weitere Einstellungen\n" +
                "vorgenommen werden:\n\n" +
                "* Die maximale Anzahl der\n" +
                "   gleichzeitigen Downloads kann\n" +
                "   hier vorgegeben werden.\n\n" +
                "* Die maximale Bandbreite pro\n" +
                "   Download kann hier\n" +
                "   vorgegeben werden.";
        image = "/de/p2tools/mtplayer/res/toolTips/Bandbreite.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
        text += "In den Einstellungen des Abos\n" +
                "kann auch die maximale Zeit,\n" +
                "die zurück gesucht wird,\n" +
                "vorgegeben werden.\n\n" +
                "Es werden also nur Filme der\n" +
                "letzten xx Tage gesucht wenn\n" +
                "xx Tage dort vorgegeben sind.";
        image = "/de/p2tools/mtplayer/res/toolTips/AboEinstellungen_Zeit.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
        text += "In den Abos kann auch die\n" +
                "Filmlänge eines Films\n" +
                "(min, max) vorgegeben werden.\n\n" +
                "Es werden dann nur Filme\n" +
                "gefunden, deren Dauer zu\n" +
                "den Vorgaben passt.";
        image = "/de/p2tools/mtplayer/res/toolTips/AboEinstellungen_Dauer.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
        text += "Im Programm sind immer zwei\n" +
                "Menüs sichtbar.\n\n" +
                "Das obere enthält Menüpunkte,\n" +
                "die für das ganze Programm\n" +
                "wichtig sind.\n\n" +
                "Das untere Menü ist immer\n" +
                "für den jeweils angezeigten\n" +
                "Tab (Filme, Downloads Abos).";
        image = "/de/p2tools/mtplayer/res/toolTips/Menue.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "kann das Systemtray\n" +
                "ein- und ausgeschaltet werden.\n\n" +
                "Im Systemtray werden Infos\n" +
                "zur aktuellen Situation des\n" +
                "Programms angezeigt.";
        image = "/de/p2tools/mtplayer/res/toolTips/Einstellungen_Systemtray.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
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
        image = "/de/p2tools/mtplayer/res/toolTips/Einstellungen_Filmliste.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "wird der Downloadpfad\n" +
                "und -name für Downloads\n" +
                "vorgegeben. Der Hilfebutton\n" +
                "verrät die Möglichkeiten.";
        image = "/de/p2tools/mtplayer/res/toolTips/Einstellungen_SetSpeicherziel.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START + "";
        text += "In den Einstellungen\n" +
                "(erreichbar über das Menü)\n" +
                "können für Downloads die ein\n" +
                "Hilfsprogramm brauchen, die\n" +
                "entsprechenden Programme\n" +
                "und Einstellungen vorgegeben\n" +
                "werden.\n\n" +
                "Es wird auch angegeben, für\n" +
                "welche Downloads das zutrifft.";
        image = "/de/p2tools/mtplayer/res/toolTips/Einstellungen_SetHilfsprogramme.png";
        pToolTip = new PToolTip(text, image);
        pToolTipList.add(pToolTip);

        ++listSize;
        text = START;
        text += "Weiter Infos finden sich auch\n" +
                "auf der Website. Ideen zu\n" +
                "den Tips gerne auch per Mail.\n\n";
        image = "/de/p2tools/mtplayer/res/toolTips/Frage.png";
        pToolTip = new PToolTip(text, image, ProgConst.URL_WEBSITE_P2TOOLS, ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty());
        pToolTipList.add(pToolTip);
    }
}
