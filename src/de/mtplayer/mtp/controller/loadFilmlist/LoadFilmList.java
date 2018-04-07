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

package de.mtplayer.mtp.controller.loadFilmlist;

import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.ProgSave;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmList;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.p2tools.p2Lib.tools.Duration;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.SysMsg;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javax.swing.event.EventListenerList;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class LoadFilmList {

    private final HashSet<String> hashSet = new HashSet<>();
    private final FilmList diffListe;

    // private
    private final Daten daten;
    private final ImportFilmList importFilmliste;
    private final EventListenerList listeners = new EventListenerList();
    private BooleanProperty propListSearching = new SimpleBooleanProperty(false);
    private boolean onlyOne = false;

    private static final AtomicBoolean stop = new AtomicBoolean(false); // damit kannn das Laden
    // gestoppt werden

    public LoadFilmList(Daten daten) {
        this.daten = daten;
        diffListe = new FilmList();
        importFilmliste = new ImportFilmList();
        importFilmliste.addAdListener(new ListenerFilmListLoad() {
            @Override
            public synchronized void start(ListenerFilmListLoadEvent event) {
                notifyStart(event);
            }

            @Override
            public synchronized void progress(ListenerFilmListLoadEvent event) {
                notifyProgress(event);
            }

            @Override
            public synchronized void fertig(ListenerFilmListLoadEvent event) {
                // Ergebnisliste listeFilme eintragen -> Feierabend!
                Duration.staticPing("Filme laden, ende");
                undEnde(event);
            }
        });
    }

    public boolean getPropListSearching() {
        return propListSearching.get();
    }

    public BooleanProperty propListSearchingProperty() {
        return propListSearching;
    }

    public void setPropListSearching(boolean propListSearching) {
        this.propListSearching.set(propListSearching);
    }

    public void loadFilmlist(String dateiUrl) {
        loadFilmlist(dateiUrl, false);
    }

    public void loadFilmlist(String dateiUrl, boolean immerNeuLaden) {
        // damit wird die Filmliste geladen UND auch gleich im Konfig-Ordner gespeichert

        System.out.println("--------> LOADFILMLIST");

        if (!dateiUrl.isEmpty()) {
            // der Benutzer hat eine Datei vorgegeben, es wird diese Liste NEU geladen
            immerNeuLaden = true;
        } else if (dateiUrl.isEmpty() && !Config.SYSTEM_LOAD_FILMS_MANUALLY.get().isEmpty()) {
            // der Benutzer hat eine Datei vorgegeben, es wird diese Liste NEU geladen
            dateiUrl = Config.SYSTEM_LOAD_FILMS_MANUALLY.get();
            immerNeuLaden = true;
        }


        Duration.staticPing("Filme laden, start");
        SysMsg.sysMsg("");
        SysMsg.sysMsg("Alte Liste erstellt am: " + Daten.getInstance().filmList.genDate());
        SysMsg.sysMsg("  Anzahl Filme: " + daten.filmList.size());
        SysMsg.sysMsg("  Anzahl Neue: " + daten.filmList.countNewFilms());

        if (!getPropListSearching()) {
            // nicht doppelt starten
            setPropListSearching(true);
            // Hash mit URLs füllen
            hashSet.clear();
            fillHash(daten.filmList);
            if (immerNeuLaden) {
                // dann die alte löschen, damit immer komplett geladen wird, aber erst nach dem Hash!!
                // sonst wird eine "zu kurze" Liste wieder nur mit einer Diff-Liste aufgefüllt, wenn das
                // Alter noch passt
                daten.filmList.clear();
            }
            daten.filmListFiltered.clear();
            if (dateiUrl.isEmpty()) {
                // Filme als Liste importieren, Url automatisch ermitteln
                SysMsg.sysMsg("Filmliste laden (auto)");
                importFilmliste.filmeImportierenAuto(daten.filmList,
                        diffListe, Config.SYSTEM_NUM_DAYS_FILMLIST.getInt());
            } else {
                // Filme als Liste importieren, feste URL/Datei
                SysMsg.sysMsg("Filmliste laden von: " + dateiUrl);
                daten.filmList.clear();
                importFilmliste.filmeImportierenDatei(dateiUrl,
                        daten.filmList, Config.SYSTEM_NUM_DAYS_FILMLIST.getInt());
            }
        }
        System.out.println("--------> LOADFILMLIST-->fertig");
    }

    public void afterFilmlistLoad() {
        notifyProgress(new ListenerFilmListLoadEvent("", "doppelte URLs suchen",
                ListenerFilmListLoad.PROGRESS_MAX, ListenerFilmListLoad.PROGRESS_MAX, 0, false/* Fehler */));
        SysMsg.sysMsg("doppelte URLs suchen");
        daten.filmList.markFilms();


        notifyProgress(new ListenerFilmListLoadEvent("", "Themen suchen",
                ListenerFilmListLoad.PROGRESS_MAX, ListenerFilmListLoad.PROGRESS_MAX, 0, false/* Fehler */));
        SysMsg.sysMsg("Themen suchen");
        daten.filmList.themenLaden();


        if (!daten.aboList.isEmpty()) {
            notifyProgress(new ListenerFilmListLoadEvent("", "Abos eintragen",
                    ListenerFilmListLoad.PROGRESS_MAX, ListenerFilmListLoad.PROGRESS_MAX, 0, false/* Fehler */));
            SysMsg.sysMsg("Abos eintragen");
            daten.aboList.setAboFuerFilm(daten.filmList);
        }


        notifyProgress(new ListenerFilmListLoadEvent("", "Blacklist filtern",
                ListenerFilmListLoad.PROGRESS_MAX, ListenerFilmListLoad.PROGRESS_MAX, 0, false/* Fehler */));
        SysMsg.sysMsg("Blacklist filtern");
        daten.filmList.filterList();


        notifyProgress(new ListenerFilmListLoadEvent("", "Filme in Downloads eintragen",
                ListenerFilmListLoad.PROGRESS_MAX, ListenerFilmListLoad.PROGRESS_MAX, 0, false/* Fehler */));
        SysMsg.sysMsg("Filme in Downloads eintragen");
        daten.downloadList.filmEintragen();
    }

    // #######################################
    // #######################################
    public void addAdListener(ListenerFilmListLoad listener) {
        listeners.add(ListenerFilmListLoad.class, listener);
    }

    public synchronized void setStop(boolean set) {
        stop.set(set);
    }

    public synchronized boolean getStop() {
        return stop.get();
    }

    public FilmListUrlList getDownloadUrlsFilmlisten_akt() {
        return importFilmliste.searchFilmListUrls.filmListUrlList_akt;
    }

    public FilmListUrlList getDownloadUrlsFilmlisten_diff() {
        return importFilmliste.searchFilmListUrls.filmListUrlList_diff;
    }

    // #######################################
    // #######################################
    private void undEnde(ListenerFilmListLoadEvent event) {
        // Abos eintragen in der gesamten Liste vor Blacklist da das nur beim Ändern der Filmliste oder
        // beim Ändern von Abos gemacht wird

        SysMsg.sysMsg("");

        // wenn nur ein Update
        if (!diffListe.isEmpty()) {
            SysMsg.sysMsg("Liste Diff gelesen am: " + StringFormatters.FORMATTER_ddMMyyyyHHmm.format(new Date()));
            SysMsg.sysMsg("  Liste Diff erstellt am: " + diffListe.genDate());
            SysMsg.sysMsg("  Anzahl Filme: " + diffListe.size());

            daten.filmList.updateListe(diffListe, true/* Vergleich über Index, sonst nur URL */, true /* ersetzen */);
            daten.filmList.metaDaten = diffListe.metaDaten;
            daten.filmList.sort(); // jetzt sollte alles passen
            diffListe.clear();
        } else {
            SysMsg.sysMsg("Liste Kompl. gelesen am: " + StringFormatters.FORMATTER_ddMMyyyyHHmm.format(new Date()));
            SysMsg.sysMsg("  Liste Kompl erstellt am: " + daten.filmList.genDate());
            SysMsg.sysMsg("  Anzahl Filme: " + daten.filmList.size());
        }

        findAndMarkNewFilms(daten.filmList);

        if (event.fehler) {
            SysMsg.sysMsg("");
            SysMsg.sysMsg("Filmliste laden war fehlerhaft, alte Liste wird wieder geladen");
            Platform.runLater(() -> new MTAlert().showErrorAlert("Filmliste laden", "Das Laden der Filmliste hat nicht geklappt!"));

            // dann die alte Liste wieder laden
            daten.filmList.clear();
            setStop(false);
            new ReadFilmlist().readFilmListe(ProgInfos.getFilmListFile(),
                    daten.filmList, Config.SYSTEM_NUM_DAYS_FILMLIST.getInt());
            SysMsg.sysMsg("");
        } else {
            new ProgSave().filmlisteSpeichern();
        }
        SysMsg.sysMsg("");
        SysMsg.sysMsg("Jetzige Liste erstellt am: " + daten.filmList.genDate());
        SysMsg.sysMsg("  Anzahl Filme: " + daten.filmList.size());
        SysMsg.sysMsg("  Anzahl Neue:  " + daten.filmList.countNewFilms());
        SysMsg.sysMsg("");

        afterFilmlistLoad();

        setPropListSearching(false);
        notifyFertig(event);
    }

    private void fillHash(FilmList filmList) {
        hashSet.addAll(filmList.stream().map(Film::getUrlHistory).collect(Collectors.toList()));
    }

    /**
     * Search through history and mark new films.
     *
     * @param filmList
     */
    private void findAndMarkNewFilms(FilmList filmList) {

        filmList.parallelStream()
                .peek(film -> film.setNewFilm(false))
                .filter(film -> !hashSet.contains(film.getUrlHistory()))
                .forEach(film -> {
                    film.setNewFilm(true);
                });

        hashSet.clear();
    }

    public void notifyStart(ListenerFilmListLoadEvent event) {
        final ListenerFilmListLoadEvent e = event;
        try {
            Platform.runLater(() -> {
                for (final ListenerFilmListLoad l : listeners.getListeners(ListenerFilmListLoad.class)) {
                    l.start(e);
                }

            });
        } catch (final Exception ex) {
            PLog.errorLog(765213654, ex);
        }
    }

    public void notifyProgress(ListenerFilmListLoadEvent event) {
        try {
            Platform.runLater(() -> {
                for (final ListenerFilmListLoad l : listeners.getListeners(ListenerFilmListLoad.class)) {
                    l.progress(event);
                }

            });
        } catch (final Exception ex) {
            PLog.errorLog(201020369, ex);
        }
    }

    public void notifyFertig(ListenerFilmListLoadEvent event) {
        try {
            Platform.runLater(() -> {
                for (final ListenerFilmListLoad l : listeners.getListeners(ListenerFilmListLoad.class)) {
                    l.fertig(event);
                }
                for (final ListenerFilmListLoad l : listeners.getListeners(ListenerFilmListLoad.class)) {
                    if (!onlyOne) {
                        l.fertigOnlyOne(event);
                    }
                }
                onlyOne = true;
            });

        } catch (final Exception ex) {
            PLog.errorLog(912045120, ex);
        }
    }

    public BooleanProperty getIstAmLaufen() {
        return propListSearching;
    }

    public void setIstAmLaufen(BooleanProperty istAmLaufen) {
        this.propListSearching = istAmLaufen;
    }
}
