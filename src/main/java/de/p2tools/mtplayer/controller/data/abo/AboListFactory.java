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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.gui.dialog.AboDelDialogController;
import de.p2tools.mtplayer.gui.dialog.abodialog.AboAddDialogController;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;

public class AboListFactory {
    private AboListFactory() {
    }

//    public static synchronized void initAboList() {
//        ProgData progData = ProgData.getInstance();
//        progData.aboList.forEach(abo -> abo.initAbo(progData));
//        Collections.sort(progData.aboList);
//        int nr = 0;
//        for (AboData abo : progData.aboList) {
//            abo.setNo(++nr);
//        }
//    }

    public static void setFilmFilterFromAbo() {
        Optional<AboData> abo = ProgData.getInstance().aboGuiController.getSel();
        ProgData.getInstance().filmFilterWorker.loadStoredFilterFromAbo(abo);
    }

    public static void setAboFromFilmFilter() {
        Optional<AboData> abo = ProgData.getInstance().aboGuiController.getSel();
        changeAboFromFilterButton(abo, ProgData.getInstance().filmFilterWorker.getActFilterSettings());
    }

    public static void setAboActive(boolean on) {
        List<AboData> lAbo = ProgData.getInstance().aboGuiController.getSelList();
        ProgData.getInstance().aboList.setAboActive(lAbo, on);
    }

    public static void addNewAbo(String aboName, String filmChannel, String filmTheme, String filmTitle) {
        // abo anlegen, oder false wenns schon existiert
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

        String namePath = DownloadFactory.replaceEmptyFileName(aboName,
                false /* nur ein Ordner */,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());

        final AboData abo = new AboData(ProgData.getInstance(),
                namePath /* name */,
                filmChannel,
                filmTheme,
                "" /* filmThemaTitel */,
                filmTitle,
                "",
                FilterCheck.FILTER_ALL_OR_MIN,
                minDuration,
                maxDuration,
                namePath);
        new AboAddDialogController(ProgData.getInstance(), abo);
    }

    public static void deleteAbo() {
        deleteAbo(ProgData.getInstance().aboGuiController.getSelList());
    }

    public static void deleteAbo(AboData abo) {
        if (abo == null) {
            return;
        }
        deleteAbo(FXCollections.observableArrayList(abo));
    }

    public static void deleteAbo(ObservableList<AboData> lAbo) {
        if (lAbo.isEmpty()) {
            return;
        }

        if (ProgConfig.ABO_ONLY_STOP.getValue() == ProgConfigAskBeforeDelete.ABO_DELETE__ASK) {
            // dann erst mal fragen
            AboDelDialogController aboDelDialog =
                    new AboDelDialogController(lAbo);
            if (aboDelDialog.getState() != PDialogExtra.STATE.STATE_OK) {
                //dann soll nix gemacht werden
                PLog.sysLog("Abo löschen: Abbruch");
                return;
            }
        }

        ProgData.getInstance().aboList.deleteAbo(lAbo);
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

    public static void addNewAboFromFilterButton(FilmFilter filmFilter) {
        //abo anlegen, oder false wenns schon existiert
        String channel = filmFilter.isChannelVis() ? filmFilter.getChannel() : "";
        String theme = filmFilter.isThemeVis() ? filmFilter.getTheme().trim() : "";
        boolean themeExact = filmFilter.isThemeExact();
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

        if (searchTitle.isEmpty()) {
            searchTitle = "Abo aus Filter";
        }

        searchTitle = DownloadFactory.replaceEmptyFileName(searchTitle,
                false /* nur ein Ordner */,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());

        final AboData abo = new AboData(ProgData.getInstance(),
                searchTitle /* name */,
                channel,
                theme,
                themeTitle,
                title,
                somewhere,
                filmFilter.getTimeRange(),
                minDuration,
                maxDuration,
                searchTitle);
        if (!theme.isEmpty()) {
            abo.setThemeExact(themeExact);
        }
        new AboAddDialogController(ProgData.getInstance(), abo);
    }

    public static void changeAboFromFilterButton(Optional<AboData> oAbo, FilmFilter filmFilter) {
        // abo mit den Filterwerten einstellen
        if (oAbo.isEmpty()) {
            return;
        }

        final AboData abo = oAbo.get();
        new AboAddDialogController(ProgData.getInstance(), filmFilter, abo);
    }
}
