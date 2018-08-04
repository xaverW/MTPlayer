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

package de.mtplayer.mtp.controller.data.abo;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.DownloadTools;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.mtplayer.mtp.gui.dialog.AboEditDialogController;
import de.mtplayer.mtp.tools.filmListFilter.FilmFilter;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.tools.GermanStringSorter;
import de.p2tools.p2Lib.tools.log.Duration;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collections;

public class AboList extends SimpleListProperty<Abo> {
    private final ProgData progData;
    private static final String[] LEER = {""};
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

    public synchronized boolean addAbo(String aboName) {
        return addAbo(aboName, "", "", "");
    }

    public synchronized boolean addAbo(String aboName, String filmChannel, String filmTheme, String filmTitle) {
        // abo anlegen, oder false wenns schon existiert
        boolean ret = false;

        int minDuration, maxDuration;
        try {
            minDuration = ProgConfig.ABO_MINUTE_MIN_SIZE.getInt();
            maxDuration = ProgConfig.ABO_MINUTE_MAX_SIZE.getInt();
        } catch (final Exception ex) {
            minDuration = 0;
            maxDuration = SelectedFilter.FILTER_DURATION_MAX_MIN;
            ProgConfig.ABO_MINUTE_MIN_SIZE.setValue("0");
            ProgConfig.ABO_MINUTE_MAX_SIZE.setValue(SelectedFilter.FILTER_DURATION_MAX_MIN);
        }

        String namePath = DownloadTools.replaceEmptyFileName(aboName,
                false /* nur ein Ordner */,
                Boolean.parseBoolean(ProgConfig.SYSTEM_USE_REPLACETABLE.get()),
                Boolean.parseBoolean(ProgConfig.SYSTEM_ONLY_ASCII.get()));

        final Abo abo = new Abo(namePath /* name */,
                filmChannel,
                filmTheme,
                "" /* filmThemaTitel */,
                filmTitle,
                "",
                minDuration,
                maxDuration,
                namePath,
                "" /* pset */);


        final AboEditDialogController editAboController = new AboEditDialogController(progData, abo);
        if (editAboController.getOk()) {
            if (!aboExistsAlready(abo)) {
                // als Vorgabe merken
                ProgConfig.ABO_MINUTE_MIN_SIZE.setValue(abo.getMinDuration());
                ProgConfig.ABO_MINUTE_MAX_SIZE.setValue(abo.getMaxDuration());
                addAbo(abo);
                sort();
                notifyChanges();
                ret = true;
            } else {
                PAlert.showErrorAlert("Abo anlegen", "Abo existiert bereits");
            }
        }
        return ret;
    }

    public synchronized void changeAbo(Abo abo) {
        if (abo != null) {
            ObservableList<Abo> lAbo = FXCollections.observableArrayList(abo);
            changeAbo(lAbo);
        }
    }

    public synchronized void changeAbo(ObservableList<Abo> lAbo) {
        if (!lAbo.isEmpty()) {
            final AboEditDialogController editAboController = new AboEditDialogController(progData, lAbo);
            if (editAboController.getOk()) {
                notifyChanges();
            }
        }
    }

    public synchronized void onOffAbo(ObservableList<Abo> lAbo, boolean on) {
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
            text = "Soll das Abo:" + PConst.LINE_SEPARATORx2 + "\"" + lAbo.get(0).getName() + "\"" + PConst.LINE_SEPARATORx2 + "gelöscht werden?";
        } else {
            text = "Sollen die " + lAbo.size() + " markierten Abos gelöscht werden?";
        }

        if (PAlert.showAlert("Löschen", "Abo löschen", text)) {
            this.removeAll(lAbo);
            notifyChanges();
        }
    }

    int i = 0;

    public synchronized void notifyChanges() {
        // Filmliste anpassen
        if (!progData.loadFilmlist.getPropLoadFilmlist()) {
            // wird danach eh gemacht
            setAboForFilm(progData.filmlist);
        }
        listChanged.setValue(!listChanged.get());
    }

    public synchronized void sort() {
        Collections.sort(this);
    }

    public synchronized ArrayList<String> getPath() {
        // liefert eine Array mit allen Pfaden
        final ArrayList<String> path = new ArrayList<>();
        for (final Abo abo : this) {
            final String s = abo.getDestination();
            if (!path.contains(s)) {
                path.add(abo.getDestination());
            }
        }
        final GermanStringSorter sorter = GermanStringSorter.getInstance();
        path.sort(sorter);
        return path;
    }

    public synchronized ArrayList<String> generateAboChannelList() {
        // liefert eine Array mit allen Sendern
        final ArrayList<String> sender = new ArrayList<>();
        sender.add("");
        for (final Abo abo : this) {
            final String s = abo.getChannel();
            if (!sender.contains(s)) {
                sender.add(abo.getChannel());
            }
        }
        sender.sort(sorter);
        return sender;
    }

    public synchronized ArrayList<String> generateAboNameList() {
        // liefert eine Array mit allen Abonamen
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

    private boolean aboExistsAlready(Abo abo) {
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
        // geht schneller, "getAboFuerFilm" muss aber vorher schon gelaufen sein!!
        Abo abo = film.getAbo();
        if (abo == null) {
            return null;
        } else {
            if (checkLength) {
                if (!FilmFilter.checkLength(abo.getMinSec(), abo.getMaxSec(), film.dauerL)) {
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
        final Abo foundAbo = stream().filter(abo -> FilmFilter.checkFilmWithFilter(
                abo.fChannel,
                abo.fTheme,
                abo.fThemeTitle,
                abo.fTitle,
                abo.fSomewhere,
                abo.getMinSec(),
                abo.getMaxSec(),
                film,
                false))
                .findFirst()
                .orElse(null);

        if (foundAbo != null) {
            if (!FilmFilter.checkLengthMin(foundAbo.getMinSec(), film.dauerL)) {
                // dann ist der Film zu kurz
                film.arr[FilmXml.FILM_ABO_NAME] = foundAbo.arr[AboXml.ABO_NAME] + (" [zu kurz]");
                film.setAbo(foundAbo);
            } else if (!FilmFilter.checkLengthMax(foundAbo.getMaxSec(), film.dauerL)) {
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

        Duration.counterStart("Abo in Filmliste eintragen");

        // leere Abos löschen, die sind Fehler
        stream().filter((abo) -> (abo.isEmpty())).forEach(this::remove);

        if (isEmpty()) {
            // dann nur die Abos in der Filmliste löschen
            filmlist.parallelStream().forEach(this::deleteAboInFilm);
            return;
        }

        // das kostet die Zeit!!
        filmlist.parallelStream().forEach(this::assignAboToFilm);

        Duration.counterStop("Abo in Filmliste eintragen");
    }
}
