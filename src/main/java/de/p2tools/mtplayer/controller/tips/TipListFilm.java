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

public class TipListFilm {

    private TipListFilm() {
    }

    public static List<TipData> getTips() {
        List<TipData> pToolTipList = new ArrayList<>();


        String text = "Im Tab \"Filme\" wird die Liste " +
                "aller Filme angezeigt." +
                "\n\n" +
                "Links neben " +
                "der Tabelle sind die Filter, mit " +
                "denen die Filme gefiltert werden " +
                "können." +
                "\n\n" +
                "In der Tabelle und rechts " +
                "daneben, können Filme gestartet " +
                "und gespeichert werden. Unter der " +
                "Tabelle sind Infos zum " +
                "ausgewählten Film.";
        String image = "/de/p2tools/mtplayer/res/tips/film/film-1.png";
        TipData pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Hier können Bookmarks angelegt " +
                "werden. Das sind Lesezeichen " +
                "um sich Filme zu merken, die " +
                "dann später angesehen oder " +
                "gespeichert werden sollen." +
                "\n\n" +
                "Die unteren zwei Button sind " +
                "ein Filter, der dann nur " +
                "Bookmarks in der Tabelle " +
                "anzeigt. Der letzte Button " +
                "öffnet einen Dialog, um " +
                "Bookmarks zu verwalten.";
        image = "/de/p2tools/mtplayer/res/tips/film/film-2.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Das Programm kann eine Mediensammlung " +
                "verwalten. Dort kann man seine Ordner " +
                "mit den gespeicherten Filmen " +
                "angeben." +
                "\n\n" +
                "In dem Infobereich unter der " +
                "Tabelle, kann man dann diese " +
                "Sammlung mit den gespeicherten Filmen " +
                "durchsuchen und einen doppelten " +
                "Download vermeiden.";
        image = "/de/p2tools/mtplayer/res/tips/film/film-3.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Im Infotab der Mediensammlung kann " +
                "die angelegte Liste der eigenen " +
                "Medien und auch die Liste der " +
                "bereits gespeicherten Abos (rechte " +
                "Spalte) durchsucht werden." +
                "\n\n" +
                "Das Suchfeld links sucht in der " +
                "Liste der eigenen Medien und das " +
                "rechte Suchfeld sucht in den " +
                "erledigten Abos." +
                "\n\n" +
                "Ein Doppelklick auf ein Wort im " +
                "Suchfeld, stellt dieses frei. " +
                "Die Button mit den gedrehten " +
                "Kreisen, wählen aus, ob das " +
                "Film-Thema/der Film-Titel oder " +
                "beides zum Suchen verwendet wird.";
        image = "/de/p2tools/mtplayer/res/tips/film/film-4.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Die Mediensammlung kann " +
                "in den Programmeinstellungen " +
                "verwaltet werden. Man kann " +
                "angeben, welche Ordner nach " +
                "Filmen durchsucht werden sollen." +
                "\n\n" +
                "Es gibt eine Liste mit \"internen\"" +
                "Medien, diese Liste wird bei jedem " +
                "Programmstart durchsucht." +
                "\n\n" +
                "Die andere List mit Medien wird nur " +
                "beim Einrichten durchsucht. Hier " +
                "kann man z.B.: Medien auf einer " +
                "USB-Platte die nicht immer am Rechner " +
                "angeschlossen ist, vorgeben.";
        image = "/de/p2tools/mtplayer/res/tips/film/film-5.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "In den Einstellungen " +
                "(erreichbar über das Menü) " +
                "kann die Filmliste beim " +
                "Laden beschränkt werden:" +
                "\n\n" +
                "Z.B.: können nur die Filme der letzten " +
                "50 Tage geladen werden. Filme " +
                "die kein Datum haben, werden " +
                "immer geladen." +
                "\n\n" +
                "Das hat den Vorteil, dass die " +
                "Filmliste deutlich kleiner ist " +
                "und das Programm schneller " +
                "reagiert (vor allem wenn der " +
                "Rechner mit wenig Speicher " +
                "ausgestattet ist).";
        image = "/de/p2tools/mtplayer/res/tips/film/film-6.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);


        text = "Das Programm verwaltet zwei Blacklists. Die eine (Filme) " +
                "wird beim Laden der Filmliste bereits angewendet. Die Filme " +
                "landen dann gar nicht in der Filmliste." +
                "\n\n" +
                "Die zweite (Backlist) wird auf die geladene Filmliste angewendet. Sie kann " +
                "ein- und ausgeschaltet werden. So kann ich Filme mal ausblenden und auch wieder " +
                "einblenden. Im Filmfilter kann sie ein- und ausgeschaltet werden." +
                "\n\n" +
                "In den Einstellungen können beide Listen verwaltet werden. Dort ist es auch " +
                "möglich, die Blacklist als \"Whitelist\" zu verwenden. Es werden dann nur " +
                "Filme die zur Liste passen angezeigt." +
                "\n\n" +
                "Es kann auch festgelegt werden, ob ein Eintrag für die Filmliste, Audioliste oder beide " +
                "zuständig ist.";
        image = "/de/p2tools/mtplayer/res/tips/film/film-7.png";
        pToolTip = new TipData(text, image);
        pToolTipList.add(pToolTip);

        return pToolTipList;
    }
}
