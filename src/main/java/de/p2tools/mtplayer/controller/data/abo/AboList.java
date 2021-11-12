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
import de.p2tools.mtplayer.controller.data.film.Film;
import de.p2tools.mtplayer.controller.data.film.FilmXml;
import de.p2tools.mtplayer.controller.data.film.Filmlist;
import de.p2tools.mtplayer.gui.dialog.AboEditDialogController;
import de.p2tools.mtplayer.tools.filmListFilter.FilmFilter;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.tools.GermanStringSorter;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public class AboList extends SimpleListProperty<Abo> {
    private final ProgData progData;
    //    private static final String[] LEER = {""};
    private static final GermanStringSorter sorter = GermanStringSorter.getInstance();

    private BooleanProperty listChanged = new SimpleBooleanProperty(true);

    public AboList(ProgData progData) {
        super(FXCollections.observableArrayList());
        this.progData = progData;
    }

    private int nr;

    public BooleanProperty listChangedProperty() {
        return listChanged;
    }

    public synchronized void initAboList() {
        this.stream().forEach(abo -> abo.initAbo(progData));
    }

    public synchronized void addAbo(Abo abo) {
        // die Änderung an der Liste wird nicht gemeldet!!
        // für das Lesen der Konfig-Datei beim Programmstart
        ++nr;
        abo.setNr(nr);

        if (abo.getName().isEmpty()) {
            // Downloads ohne "Aboname" sind manuelle Downloads
            abo.setName("Abo_" + nr);
        }
        if (abo.getResolution().isEmpty()) {
            abo.setResolution(Film.RESOLUTION_NORMAL);
        }
        super.add(abo);
    }

    public synchronized void addNewAboFromFilter(SelectedFilter selectedFilter) {
        // abo anlegen, oder false wenns schon existiert
        String channel = selectedFilter.isChannelVis() ? selectedFilter.getChannel() : "";
        String theme = selectedFilter.isThemeVis() ? selectedFilter.getTheme().trim() : "";
        boolean themeExact = selectedFilter.isThemeExact();
        String title = selectedFilter.isTitleVis() ? selectedFilter.getTitle().trim() : "";
        String themeTitle = selectedFilter.isThemeTitleVis() ? selectedFilter.getThemeTitle().trim() : "";
        String somewhere = selectedFilter.isSomewhereVis() ? selectedFilter.getSomewhere().trim() : "";
        int minDuration = selectedFilter.isMinMaxDurVis() ? selectedFilter.getMinDur() : FilmFilter.FILTER_DURATION_MIN_MINUTE;
        int maxDuration = selectedFilter.isMinMaxDurVis() ? selectedFilter.getMaxDur() : FilmFilter.FILTER_DURATION_MAX_MINUTE;

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

        final Abo abo = new Abo(progData,
                searchTitle /* name */,
                channel,
                theme,
                themeTitle,
                title,
                somewhere,
                selectedFilter.getTimeRange(),
                minDuration,
                maxDuration,
                searchTitle);

        if (!theme.isEmpty()) {
            abo.setThemeExact(themeExact);
        }

//        if (new AboEditDialogController(progData, abo).getOk()) {
//            // als Vorgabe merken
//            ProgConfig.ABO_MINUTE_MIN_SIZE.setValue(abo.getMinDurationMinute());
//            ProgConfig.ABO_MINUTE_MAX_SIZE.setValue(abo.getMaxDurationMinute());
//            addAbo(abo);
//            notifyChanges();
//        }

        new AboEditDialogController(progData, abo);
    }

    public synchronized void changeAboFromFilter(Optional<Abo> oAbo, SelectedFilter selectedFilter) {
        // abo mit den Filterwerten einstellen
        if (!oAbo.isPresent()) {
            return;
        }

        final Abo abo = oAbo.get();
        new AboEditDialogController(progData, selectedFilter, abo);
    }

    public synchronized void addNewAbo(String aboName, String filmChannel, String filmTheme, String filmTitle) {
        // abo anlegen, oder false wenns schon existiert
        int minDuration, maxDuration;
        try {
            minDuration = ProgConfig.ABO_MINUTE_MIN_SIZE.getValue();
            maxDuration = ProgConfig.ABO_MINUTE_MAX_SIZE.getValue();
        } catch (final Exception ex) {
            minDuration = FilmFilter.FILTER_DURATION_MIN_MINUTE;
            maxDuration = FilmFilter.FILTER_DURATION_MAX_MINUTE;
            ProgConfig.ABO_MINUTE_MIN_SIZE.setValue(FilmFilter.FILTER_DURATION_MIN_MINUTE);
            ProgConfig.ABO_MINUTE_MAX_SIZE.setValue(FilmFilter.FILTER_DURATION_MAX_MINUTE);
        }

        String namePath = DownloadTools.replaceEmptyFileName(aboName,
                false /* nur ein Ordner */,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());

        final Abo abo = new Abo(progData,
                namePath /* name */,
                filmChannel,
                filmTheme,
                "" /* filmThemaTitel */,
                filmTitle,
                "",
                FilmFilter.FILTER_TIME_RANGE_ALL_VALUE,
                minDuration,
                maxDuration,
                namePath);

        new AboEditDialogController(progData, abo);
    }

    public synchronized void changeAbo(Abo abo) {
        //Abo aus Tab Filme/Download ändern
        if (abo != null) {
            ObservableList<Abo> lAbo = FXCollections.observableArrayList(abo);
            changeAbo(lAbo);
        }
    }

    public synchronized void changeAbo(ObservableList<Abo> lAbo) {
        if (!lAbo.isEmpty()) {
            new AboEditDialogController(progData, lAbo);
        }
    }

    public synchronized void setAboActive(ObservableList<Abo> lAbo, boolean on) {
        if (!lAbo.isEmpty()) {
            lAbo.stream().forEach(abo -> abo.setActive(on));
            notifyChanges();
        }
    }

    public synchronized void deleteAbo(Abo abo) {
        if (abo == null) {
            return;
        }
        ObservableList<Abo> lAbo = FXCollections.observableArrayList(abo);
        deleteAbo(lAbo);
    }

    public synchronized void deleteAbo(ObservableList<Abo> lAbo) {
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
        if (!progData.loadFilmlist.getPropLoadFilmlist()) {
            // wird danach eh gemacht
            setAboForFilm(progData.filmlist);
        }
        listChanged.setValue(!listChanged.get());
    }

    public synchronized void sort() {
        Collections.sort(this);

        nr = 0;
        for (Abo abo : this) {
            abo.setNr(++nr);
        }
    }

    public synchronized ArrayList<String> getAboDestinationPathList() {
        // liefert ein Array mit allen Pfaden
        final ArrayList<String> path = new ArrayList<>();
        for (final Abo abo : this) {
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
        for (final Abo abo : this) {

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
        // liefert ein Array mit allen Abonamen
        final ArrayList<String> name = new ArrayList<>();
        name.add("");
        for (final Abo abo : this) {
            final String s = abo.getName();
            if (!name.contains(s)) {
                name.add(abo.getName());
            }
        }
        name.sort(sorter);
        return name;
    }

    public boolean aboExistsAlready(Abo abo) {
        // true wenn es das Abo schon gibt
        for (final Abo dataAbo : this) {
            if (FilmFilter.aboExistsAlready(dataAbo, abo)) {
                return true;
            }
        }
        return false;
    }

    public synchronized Abo getAboForFilm_quick(Film film, boolean checkLength) {
        // da wird nur in der Filmliste geschaut, ob in "DatenFilm" ein Abo eingetragen ist
        // geht schneller, "assignAboToFilm" muss aber vorher schon gelaufen sein!!
        Abo abo = film.getAbo();
        if (abo == null) {
            return null;
        } else {
            if (checkLength) {
                if (!FilmFilter.checkLength(abo.getMinDurationMinute(), abo.getMaxDurationMinute(), film.getDurationMinute())) {
                    return null;
                }
            }
            return abo;
        }
    }

    private void deleteAboInFilm(Film film) {
        // für jeden Film Abo löschen
        film.arr[FilmXml.FILM_ABO_NAME] = "";
        film.setAbo(null);
    }


    /**
     * Assign found abo to the film objects. Time-intensive procedure!
     *
     * @param film assignee
     */
    private void assignAboToFilm(Film film) {
        if (film.isLive()) {
            // Livestreams gehören nicht in ein Abo
            deleteAboInFilm(film);
            return;
        }

        final Abo foundAbo = stream()
                .filter(abo -> abo.isActive())
                .filter(abo -> FilmFilter.checkFilmWithFilter(
                        abo.fChannel,
                        abo.fTheme,
                        abo.fThemeTitle,
                        abo.fTitle,
                        abo.fSomewhere,

                        abo.getTimeRange(),
                        abo.getMinDurationMinute(),
                        abo.getMaxDurationMinute(),

                        film,
                        false))

                .findFirst()
                .orElse(null);

        if (foundAbo != null) {
            if (!FilmFilter.checkLengthMin(foundAbo.getMinDurationMinute(), film.getDurationMinute())) {
                // dann ist der Film zu kurz
                film.arr[FilmXml.FILM_ABO_NAME] = foundAbo.arr[AboXml.ABO_NAME] + (" [zu kurz]");
                film.setAbo(foundAbo);
            } else if (!FilmFilter.checkLengthMax(foundAbo.getMaxDurationMinute(), film.getDurationMinute())) {
                // dann ist der Film zu lang
                film.arr[FilmXml.FILM_ABO_NAME] = foundAbo.arr[AboXml.ABO_NAME] + (" [zu lang]");
                film.setAbo(foundAbo);
            } else {
                film.arr[FilmXml.FILM_ABO_NAME] = foundAbo.arr[AboXml.ABO_NAME];
                film.setAbo(foundAbo);
            }

        } else {
            deleteAboInFilm(film);
        }
    }

    public synchronized void setAboForFilm(Filmlist filmlist) {
        // hier wird tatsächlich für jeden Film die Liste der Abos durchsucht
        // braucht länger
        PDuration.counterStart("Abo in Filmliste eintragen");

        // leere Abos löschen, die sind Fehler
        stream().filter((abo) -> (abo.isEmpty())).forEach(this::remove);

        if (isEmpty()) {
            // dann nur die Abos in der Filmliste löschen
            filmlist.parallelStream().forEach(this::deleteAboInFilm);
            return;
        }

        // das kostet die Zeit!!
        filmlist.parallelStream().forEach(this::assignAboToFilm);

        PDuration.counterStop("Abo in Filmliste eintragen");
    }

}
