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


package de.p2tools.mtplayer.controller.data.abo;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConfigAskBeforeDelete;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.mtplayer.controller.filter.FilmFilter;
import de.p2tools.mtplayer.gui.dialog.AboDelDialogController;
import de.p2tools.mtplayer.gui.dialog.abodialog.AboAddDialogController;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.mediathek.filter.Filter;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;

public class AboListFactory {
    private AboListFactory() {
    }

    public static void addNewAbo(int audio, String aboName, String filmChannel, String filmTheme, String filmTitle) {
        // Menü: abo anlegen
        int minDuration, maxDuration;
        try {
            minDuration = ProgConfig.ABO_MINUTE_MIN_SIZE.getValue();
            maxDuration = ProgConfig.ABO_MINUTE_MAX_SIZE.getValue();
        } catch (final Exception ex) {
            minDuration = FilterCheck.FILTER_ALL_OR_MIN;
            maxDuration = FilterCheck.FILTER_DURATION_MAX_MINUTE;
            ProgConfig.ABO_MINUTE_MIN_SIZE.setValue(FilterCheck.FILTER_ALL_OR_MIN);
            ProgConfig.ABO_MINUTE_MAX_SIZE.setValue(FilterCheck.FILTER_DURATION_MAX_MINUTE);
        }

        String namePath = DownloadFactory.replaceFileNameWithReplaceList(aboName, false /* nur ein Ordner */);

        final AboData abo = new AboData(ProgData.getInstance(),
                audio,
                namePath /* name */,
                filmChannel,
                filmTheme,
                false, /* themeIsExact */
                "" /* filmThemaTitel */,
                filmTitle,
                "",
                FilterCheck.FILTER_ALL_OR_MIN,
                minDuration,
                maxDuration,
                namePath);
        new AboAddDialogController(ProgData.getInstance(), abo);
    }

    public static void addNewAboFromFilterButton(boolean audio) {
        // aus Menü/TableContextMenü
        FilmFilter filmFilter;
        if (audio) {
            filmFilter = ProgData.getInstance().filterWorkerAudio.getActFilterSettings();
        } else {
            filmFilter = ProgData.getInstance().filterWorkerFilm.getActFilterSettings();
        }
        String channel = filmFilter.isChannelVis() ? filmFilter.getChannel() : "";

        boolean themeIsExact = filmFilter.isThemeIsExact();
        String theme = filmFilter.isThemeVis() ? filmFilter.getResTheme().trim() : "";

        String title = filmFilter.isTitleVis() ? filmFilter.getTitle().trim() : "";
        String themeTitle = filmFilter.isThemeTitleVis() ? filmFilter.getThemeTitle().trim() : "";
        String somewhere = filmFilter.isSomewhereVis() ? filmFilter.getSomewhere().trim() : "";
        int minDuration = filmFilter.isMinMaxDurVis() ? filmFilter.getMinDur() : FilterCheck.FILTER_ALL_OR_MIN;
        int maxDuration = filmFilter.isMinMaxDurVis() ? filmFilter.getMaxDur() : FilterCheck.FILTER_DURATION_MAX_MINUTE;

        String searchTitle = "";
        String searchChannel = channel.isEmpty() ? "" : channel + " - ";

        if (!themeTitle.isEmpty()) {
            searchTitle = searchChannel + themeTitle;

        } else if (!theme.isEmpty() && !title.isEmpty()) {
            searchTitle = searchChannel + theme + "-" + title;

        } else if (!theme.isEmpty() || !title.isEmpty()) {
            searchTitle = searchChannel + theme + title;

        } else if (!somewhere.isEmpty()) {
            searchTitle = searchChannel + somewhere;
        }

        if (searchTitle.startsWith(Filter.FILTER_REG_EX)) {
            searchTitle = searchTitle.replaceFirst(Filter.FILTER_REG_EX, "");
        } else if (searchTitle.startsWith(Filter.FILTER_EXCLUDE)) {
            searchTitle = searchTitle.replaceFirst(Filter.FILTER_EXCLUDE, "");
        }
        if (searchTitle.isEmpty()) {
            searchTitle = "Abo aus Filter";
        }
        searchTitle = DownloadFactory.replaceFileNameWithReplaceList(searchTitle, false /* nur ein Ordner */);

        final AboData abo = new AboData(ProgData.getInstance(),
                audio ? ProgConst.LIST_AUDIO : ProgConst.LIST_FILM,
                searchTitle /* name */,
                channel,
                theme,
                themeIsExact,
                themeTitle,
                title,
                somewhere,
                filmFilter.getTimeRange(),
                minDuration,
                maxDuration,
                searchTitle);

        new AboAddDialogController(ProgData.getInstance(), abo);
    }

    public static void changeAboFromFilterButton() {
        // Menü
        Optional<AboData> oAbo = ProgData.getInstance().aboGuiController.getSel();
        if (oAbo.isEmpty()) {
            return;
        }

        final AboData abo = oAbo.get();
        new AboAddDialogController(ProgData.getInstance(),
                ProgData.getInstance().filterWorkerFilm.getActFilterSettings(), abo);
    }

    public static void setFilmFilterFromAbo() {
        // Menü
        Optional<AboData> abo = ProgData.getInstance().aboGuiController.getSel();
        ProgData.getInstance().filterWorkerFilm.setFilterFromAbo(abo);
    }

    public static void editAbo() {
        //Abos aus Tab Abo (Menü, Doppelklick Tabelle) ändern
        List<AboData> aboList = ProgData.getInstance().aboGuiController.getSelList();
        if (!aboList.isEmpty()) {
            new AboAddDialogController(ProgData.getInstance(), aboList);
        }
    }

    public static void editAbo(AboData abo) {
        //Abo aus Tab Filme/Download ändern
        if (abo != null) {
            new AboAddDialogController(ProgData.getInstance(), FXCollections.observableArrayList(abo));
        }
    }

    public static void setAboActive(boolean on) {
        // Menü
        List<AboData> lAbo = ProgData.getInstance().aboGuiController.getSelList();
        ProgData.getInstance().aboList.setAboActive(lAbo, on);
    }

    public static void setAboActive(AboData abo, boolean on) {
        // Menü
        ProgData.getInstance().aboList.setAboActive(abo, on);
    }

    public static void deleteAbo() {
        // Menü
        deleteAbo(ProgData.getInstance().aboGuiController.getSelList());
    }

    public static void deleteAbo(AboData abo) {
        // Menü/Tabelle Button
        if (abo == null) {
            return;
        }
        deleteAbo(FXCollections.observableArrayList(abo));
    }

    private static void deleteAbo(ObservableList<AboData> lAbo) {
        if (lAbo.isEmpty()) {
            return;
        }

        if (ProgConfig.ABO_ONLY_STOP.getValue() == ProgConfigAskBeforeDelete.ABO_DELETE__ASK) {
            // dann erst mal fragen
            AboDelDialogController aboDelDialog =
                    new AboDelDialogController(lAbo);
            if (aboDelDialog.getState() != P2DialogExtra.STATE.STATE_OK) {
                //dann soll nix gemacht werden
                P2Log.sysLog("Abo löschen: Abbruch");
                return;
            }
        }

        ProgData.getInstance().aboList.deleteAbo(lAbo);
    }
}
