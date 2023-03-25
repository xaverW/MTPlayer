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

package de.p2tools.mtplayer.controller.data.abo;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadTools;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.gui.dialog.AboEditDialogController;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.configfile.pdata.PDataList;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.GermanStringSorter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class AboList extends SimpleListProperty<AboData> implements PDataList<AboData> {
    private final ProgData progData;
    public static final String TAG = "AboList";
    private int nr;
    private static final GermanStringSorter sorter = GermanStringSorter.getInstance();
    private BooleanProperty listChanged = new SimpleBooleanProperty(true);

    public AboList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller Abos";
    }

    @Override
    public AboData getNewItem() {
        return new AboData();
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(AboData.class)) {
            add((AboData) obj);
        }
    }

    public BooleanProperty listChangedProperty() {
        return listChanged;
    }

    public synchronized void initAboList() {
        this.stream().forEach(abo -> abo.initAbo(progData));
    }

    public synchronized void addAbo(AboData abo) {
        // die Änderung an der Liste wird nicht gemeldet!!
        // für das Lesen der Konfig-Datei beim Programmstart
        ++nr;
        abo.setNo(nr);

        if (abo.getName().isEmpty()) {
            // Downloads ohne "Aboname" sind manuelle Downloads
            abo.setName("Abo_" + nr);
        }
        if (abo.getResolution().isEmpty()) {
            abo.setResolution(FilmDataMTP.RESOLUTION_NORMAL);
        }
        super.add(abo);
    }

    public synchronized void addNewAboFromFilterButton(FilmFilter filmFilter) {
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

        searchTitle = DownloadTools.replaceEmptyFileName(searchTitle,
                false /* nur ein Ordner */,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());

        final AboData abo = new AboData(progData,
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

        new AboEditDialogController(progData, abo);
    }

    public synchronized void changeAboFromFilterButton(Optional<AboData> oAbo, FilmFilter filmFilter) {
        // abo mit den Filterwerten einstellen
        if (!oAbo.isPresent()) {
            return;
        }

        final AboData abo = oAbo.get();
        new AboEditDialogController(progData, filmFilter, abo);
    }

    public synchronized void addNewAboButton(String aboName, String filmChannel, String filmTheme, String filmTitle) {
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

        String namePath = DownloadTools.replaceEmptyFileName(aboName,
                false /* nur ein Ordner */,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());

        final AboData abo = new AboData(progData,
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

        new AboEditDialogController(progData, abo);
    }

    public synchronized void aboEditDialog(AboData abo) {
        //Abo aus Tab Filme/Download ändern
        if (abo != null) {
            aboEditDialog(FXCollections.observableArrayList(abo));
        }
    }

    public synchronized void aboEditDialog(ObservableList<AboData> lAbo) {
        if (!lAbo.isEmpty()) {
            new AboEditDialogController(progData, lAbo);
        }
    }

    public synchronized void setAboActive(ObservableList<AboData> lAbo, boolean on) {
        if (!lAbo.isEmpty()) {
            lAbo.stream().forEach(abo -> abo.setActive(on));
            notifyChanges();
        }
    }

    public synchronized void deleteAbo(AboData abo) {
        if (abo == null) {
            return;
        }
        ObservableList<AboData> lAbo = FXCollections.observableArrayList(abo);
        deleteAbo(lAbo);
    }

    public synchronized void deleteAbo(ObservableList<AboData> lAbo) {
        if (lAbo.isEmpty()) {
            return;
        }

        String text;
        if (lAbo.size() == 1) {
            text = "Soll das Abo:" + P2LibConst.LINE_SEPARATORx2 + "\"" + lAbo.get(0).getName() + "\"" + P2LibConst.LINE_SEPARATORx2 + "gelöscht werden?";
        } else {
            text = "Sollen die " + lAbo.size() + " markierten Abos gelöscht werden?";
        }

        if (PAlert.showAlertOkCancel("Löschen", "Abo löschen", text)) {
            this.removeAll(lAbo);
            notifyChanges();
        }
    }

    public synchronized void notifyChanges() {
        if (!LoadFilmFactory.getInstance().loadFilmlist.getPropLoadFilmlist()) {
            // wird danach eh gemacht
            AboFactory.setAboForFilmlist();
        }
        listChanged.setValue(!listChanged.get());
    }

    public synchronized void sort() {
        Collections.sort(this);

        nr = 0;
        for (AboData abo : this) {
            abo.setNo(++nr);
        }
    }

    public synchronized ArrayList<String> getAboDestinationPathList() {
        // liefert ein Array mit allen Pfaden
        final ArrayList<String> path = new ArrayList<>();
        for (final AboData abo : this) {
            final String s = abo.getAboSubDir();
            if (!path.contains(s)) {
                path.add(abo.getAboSubDir());
            }
        }
        final GermanStringSorter sorter = GermanStringSorter.getInstance();
        path.sort(sorter);
        return path;
    }

    public synchronized ArrayList<String> getAboChannelList() {
        // liefert ein Array mit allen Sendern
        final ArrayList<String> sender = new ArrayList<>();
        sender.add("");
        for (final AboData abo : this) {

            final String s = abo.getChannel();

            List<String> channelFilterList = new ArrayList<>();
            String channelFilter = abo.getChannel();
            if (channelFilter != null) {
                if (channelFilter.contains(",")) {
                    channelFilterList.addAll(Arrays.asList(channelFilter.replace(" ", "").split(",")));
                } else {
                    channelFilterList.add(channelFilter);
                }
                channelFilterList.stream().forEach(st -> st = st.trim());
            }
            for (String sf : channelFilterList) {
                if (sf.isEmpty()) {
                    continue;
                }
                if (!sender.contains(sf)) {
                    sender.add(sf);
                }
            }
        }

        sender.sort(sorter);
        return sender;
    }

    public synchronized ArrayList<String> getAboNameList() {
        // liefert ein Array mit allen AboNamen
        final ArrayList<String> name = new ArrayList<>();
        name.add("");
        for (final AboData abo : this) {
            final String s = abo.getName();
            if (!name.contains(s)) {
                name.add(abo.getName());
            }
        }
        name.sort(sorter);
        return name;
    }
}
