/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
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


package de.p2tools.mtplayer.controller.data.blackdata;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.dialog.AddBlackListDialogController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlacklistFactory {
    public enum BLACK_SRC {FILM, AUDIO, DOWNLOAD}

    private BlacklistFactory() {
    }

    public static List<BlackData> getSubList(boolean audio, List<BlackData> list) {
        final List<BlackData> ret = new ArrayList<>();
        list.stream().filter(b -> b.getList() == ProgConst.LIST_FILM_AUDIO ||
                        audio && b.getList() == ProgConst.LIST_AUDIO ||
                        !audio && b.getList() == ProgConst.LIST_FILM)
                .forEach(ret::add);
        return ret;
    }

    public static boolean blackExistsAlready(BlackData blackData, List<BlackData> list) {
        // true, wenn es das Black schon gibt
        for (final BlackData data : list) {
            if (data.getChannel().equalsIgnoreCase(blackData.getChannel()) &&
                    data.getTheme().equalsIgnoreCase(blackData.getTheme()) &&

                    ((data.getTheme().isEmpty() && blackData.getTheme().isEmpty()) ||
                            data.isThemeExact() == blackData.isThemeExact()) &&

                    data.getTitle().equalsIgnoreCase(blackData.getTitle()) &&
                    data.getThemeTitle().equalsIgnoreCase(blackData.getThemeTitle())) {
                return true;
            }
        }
        return false;
    }

    public static void addBlackFilm(BLACK_SRC blackSRC) {
        // aus dem Menü: mit markiertem Film ein Black erstellen
        // Dialog anzeigen
        BlackData blackData = null;
        if (blackSRC.equals(BLACK_SRC.FILM)) {
            final Optional<FilmDataMTP> filmDataMTP = ProgData.getInstance().filmGuiController.getSel(true, true);
            if (filmDataMTP.isEmpty()) {
                return;
            }
            blackData = new BlackData(ProgConst.LIST_FILM, filmDataMTP.get().getChannel(), filmDataMTP.get().getTheme(),
                    filmDataMTP.get().getTitle(), "");

        } else if (blackSRC.equals(BLACK_SRC.AUDIO)) {
            final Optional<FilmDataMTP> filmDataMTP = ProgData.getInstance().audioGuiController.getSel(true, true);
            if (filmDataMTP.isEmpty()) {
                return;
            }
            blackData = new BlackData(ProgConst.LIST_AUDIO, filmDataMTP.get().getChannel(), filmDataMTP.get().getTheme(),
                    filmDataMTP.get().getTitle(), "");

        } else if (blackSRC.equals(BLACK_SRC.DOWNLOAD)) {
            final Optional<DownloadData> downloadData = ProgData.getInstance().downloadGuiController.getSel(true);
            if (downloadData.isEmpty()) {
                return;
            }
            blackData = new BlackData(ProgConst.LIST_FILM_AUDIO, downloadData.get().getChannel(), downloadData.get().getTheme(),
                    downloadData.get().getTitle(), "");
        }

        AddBlackListDialogController addBlacklistDialogController =
                new AddBlackListDialogController(blackData);
        if (!addBlacklistDialogController.isOk()) {
            //dann doch nicht
            return;
        }

        ProgData.getInstance().blackList.add(blackData);
        new Thread(() -> BlacklistFilterFactory.markFilmBlack(true)).start();
    }

    public static void addBlackThemeFilm(BLACK_SRC blackSRC) {
        // aus dem Menü: mit markiertem Film ein Black erstellen
        // Dialog anzeigen
        if (blackSRC.equals(BLACK_SRC.FILM)) {
            final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().filmGuiController.getSel(true, true);
            if (filmSelection.isEmpty()) {
                return;
            }
            addBlack(ProgConst.LIST_FILM, "", filmSelection.get().getTheme(), "");
        } else if (blackSRC.equals(BLACK_SRC.AUDIO)) {
            final Optional<FilmDataMTP> filmSelection = ProgData.getInstance().audioGuiController.getSel(true, true);
            if (filmSelection.isEmpty()) {
                return;
            }
            addBlack(ProgConst.LIST_AUDIO, "", filmSelection.get().getTheme(), "");
        }
    }

    public static void addBlackTitleDownload() {
        // aus dem Menü: mit markiertem Film ein Black erstellen
        // Dialog anzeigen
        final Optional<DownloadData> downloadData = ProgData.getInstance().downloadGuiController.getSel(true);
        if (downloadData.isEmpty()) {
            return;
        }

        addBlack(ProgConst.LIST_FILM_AUDIO, "", "", downloadData.get().getTitle());
    }

    public static void addBlackSenderThemeDownload() {
        // aus dem Menü: mit markiertem Film ein Black erstellen
        // Dialog anzeigen
        final Optional<DownloadData> downloadData = ProgData.getInstance().downloadGuiController.getSel(true);
        if (downloadData.isEmpty()) {
            return;
        }

        addBlack(ProgConst.LIST_FILM_AUDIO, downloadData.get().getChannel(), downloadData.get().getTheme(), "");
    }

    public static void addBlackThemeDownload() {
        // aus dem Menü: mit markiertem Film ein Black erstellen
        // Dialog anzeigen
        final Optional<DownloadData> downloadData = ProgData.getInstance().downloadGuiController.getSel(true);
        if (downloadData.isEmpty()) {
            return;
        }

        addBlack(ProgConst.LIST_FILM_AUDIO, "", downloadData.get().getTheme(), "");
    }

    public static void addBlack(int list, String sender, String theme, String titel) {
        BlackData blackData = new BlackData(list, sender, theme, titel, "");
        ProgData.getInstance().blackList.add(blackData);
        new Thread(() -> BlacklistFilterFactory.markFilmBlack(true)).start();
    }

    public static boolean blackIsEmpty(BlackData blackData) {
        // true, wenn es das Black schon gibt
        if (blackData.getChannel().isEmpty() &&
                blackData.getTheme().isEmpty() &&
                blackData.getTitle().isEmpty() &&
                blackData.getThemeTitle().isEmpty()) {
            return true;
        }
        return false;
    }

    public static void addStandardsList(BlackList list) {
        //nach Auftreten sortiert!
        BlackData bl = new BlackData(ProgConst.LIST_FILM, "", "", "- Audiodeskription", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData(ProgConst.LIST_FILM, "", "", "(Audiodeskription)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData(ProgConst.LIST_FILM, "", "", "(Gebärdensprache)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData(ProgConst.LIST_FILM, "", "", "mit Gebärdensprache", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData(ProgConst.LIST_FILM, "", "", "(mit Untertitel)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData(ProgConst.LIST_FILM, "", "", "(Originalversion mit Untertitel)", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData(ProgConst.LIST_FILM, "", "", "in Gebärdensprache", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData(ProgConst.LIST_FILM, "", "in Gebärdensprache", "", "");
        bl.setThemeExact(false);
        list.add(bl);

        bl = new BlackData(ProgConst.LIST_FILM, "", "", "\"Trailer:\"", "");
        bl.setThemeExact(false);
        list.add(bl);

        new Thread(() -> BlacklistFilterFactory.markFilmBlack(true)).start();
    }
}
