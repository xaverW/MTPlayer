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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.P2LibConst;

import java.util.ArrayList;
import java.util.List;

public class TipListAbo {

    private static final String START = "                                                     " +
            P2LibConst.LINE_SEPARATOR;
    private static final int listSize = 17;

    private TipListAbo() {
    }

    public static List<PTipOfDay> getTips() {
        List<PTipOfDay> pToolTipList = new ArrayList<>();

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
        String image = "/de/p2tools/mtplayer/res/tooltips/GuiFilme_Filter_ein_aus.png";
        PTipOfDay pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Live-Suche in den\n" +
                "ARD/ZDF Mediatheken:\n\n" +
                "Ist die Live-Suche eingeschaltet,\n" +
                "kann damit in den Mediatheken\n" +
                "von ARD und ZDF gesucht werden.\n" +
                "Es ist auch möglich, mit der URL\n" +
                "einer Filmseite den Film zu suchen.\n" +
                "Die Filme können dann ganz normal\n" +
                "angesehen oder gespeichert werden.\n" +
                "In den Einstellungen kann die \n" +
                "Live-Suche ausgeblendet werden.";
        image = "/de/p2tools/mtplayer/res/tooltips/Live_Suche.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "Suche in der Mediensammlung:\n\n" +
                "Ein Doppelklick auf einen\n" +
                "Suchbegriff stellt diesen frei.\n\n" +
                "Der Button (Kreis) wählt aus, wo\n" +
                "(Dateiname/Pfad) gesucht werden\n" +
                "soll.";
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
        text += "In den Einstellungen des Abos\n" +
                "kann auch vorgeben werden,\n" +
                "ob ein Beitrag in der\n" +
                "Filmliste/Audioliste oder\n" +
                "beiden gesucht werden soll.";
        image = "/de/p2tools/mtplayer/res/tooltips/AboEinstellungen_Quelle.png";
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
        text += "In den Einstellungen des Abos\n" +
                "kann auch ein eigener Pfad\n" +
                "und auch ein eigener Dateiname\n" +
                "für Downloads aus diesem Abo\n" +
                "vorgegeben werden. Die Einstellungen\n" +
                "des Sets werden dann überschrieben.";
        image = "/de/p2tools/mtplayer/res/tooltips/AboEinstellungen_Pfad.png";
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
                "Tab (Filme, Audios,\n" +
                "Live, Downloads, Abos).";
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
        text += "Einstellungen zum Filtern\n" +
                "im Tab Filme/Audios können\n" +
                "hier erreicht werden.";
        image = "/de/p2tools/mtplayer/res/tooltips/GuiFilme_Filter_1.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Filtereinstellungen bei\n" +
                "den Filmen/Audios können Filter\n" +
                "ein- und ausgeschaltet werden.\n" +
                "Beim Suchen werden nur\n" +
                "die eingeschalteten Filter\n" +
                "verwendet. Das Suchen ist schneller\n" +
                "wenn nicht alle Filter eingeschaltet sind.";
        image = "/de/p2tools/mtplayer/res/tooltips/GuiFilme_Filter_2.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Filtereinstellungen bei\n" +
                "den Filmen/Audios kann eine\n" +
                "Wartezeit vorgegeben werden.\n\n" +
                "Diese wird dann bei der Suche\n" +
                "in den Textfeldern abgewartet,\n" +
                "bis die Suche beginnt.\n\n" +
                "Es wird dann nicht bei jedem\n" +
                "Buchstaben die Suche gestartet.";
        image = "/de/p2tools/mtplayer/res/tooltips/GuiFilme_Filter_3.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        text = START;
        text += "In den Filmen/Audios kann ein\n" +
                "einfacher Filter über das\n" +
                "Menü eingestellt werden. Er\n" +
                "enthält nur das nötigste und\n" +
                "ist dadurch etwas leichter\n" +
                "zu bedienen.";
        image = "/de/p2tools/mtplayer/res/tooltips/EinfacherFilter.png";
        pToolTip = new PTipOfDay(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
