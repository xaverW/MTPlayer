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

package de.mtplayer.mtp.controller.filmlist;

import de.mtplayer.mLib.tools.StringFormatters;
import de.mtplayer.mtp.controller.ProgSave;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.Filmlist;
import de.mtplayer.mtp.controller.filmlist.filmlistUrls.FilmlistUrlList;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ImportNewFilmlist;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ReadFilmlist;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.p2tools.p2Lib.tools.log.Duration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class LoadFilmlist {

    private final HashSet<String> hashSet = new HashSet<>();
    private final Filmlist diffListe;

    private final ProgData progData;
    private final ImportNewFilmlist importNewFilmliste;
    private final NotifyProgress notifyProgress = new NotifyProgress();
    private BooleanProperty propLoadFilmlist = new SimpleBooleanProperty(false);
    private static final AtomicBoolean stop = new AtomicBoolean(false); // damit kannn das Laden gestoppt werden kann

    public LoadFilmlist(ProgData progData) {
        this.progData = progData;
        diffListe = new Filmlist();
        importNewFilmliste = new ImportNewFilmlist();
        importNewFilmliste.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public synchronized void start(ListenerFilmlistLoadEvent event) {
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.START, event);
            }

            @Override
            public synchronized void progress(ListenerFilmlistLoadEvent event) {
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS, event);
            }

            @Override
            public synchronized void finished(ListenerFilmlistLoadEvent event) {
                // Ergebnisliste listeFilme eintragen -> Feierabend!
                Duration.staticPing("Filme laden, ende");
                afterImportNewFilmlist(event);
            }
        });
    }

    public boolean getPropLoadFilmlist() {
        return propLoadFilmlist.get();
    }

    public BooleanProperty propLoadFilmlistProperty() {
        return propLoadFilmlist;
    }

    public void setPropLoadFilmlist(boolean propLoadFilmlist) {
        this.propLoadFilmlist.set(propLoadFilmlist);
    }

    public void addAdListener(ListenerFilmlistLoad listener) {
        notifyProgress.listeners.add(ListenerFilmlistLoad.class, listener);
    }

    public synchronized void setStop(boolean set) {
        stop.set(set);
    }

    public synchronized boolean getStop() {
        return stop.get();
    }

    public FilmlistUrlList getDownloadUrlsFilmlisten_akt() {
        return importNewFilmliste.searchFilmListUrls.filmlistUrlList_akt;
    }

    public FilmlistUrlList getDownloadUrlsFilmlisten_diff() {
        return importNewFilmliste.searchFilmListUrls.filmlistUrlList_diff;
    }

    /**
     * Filmliste laden
     *
     * @param fileUrl
     */
    public void loadFilmlist(String fileUrl) {
        loadFilmlist(fileUrl, false);
    }

    /**
     * Filmliste laden
     *
     * @param fileUrl
     * @param alwaysLoadNew
     */
    public void loadFilmlist(String fileUrl, boolean alwaysLoadNew) {
        // damit wird die Filmliste geladen UND auch gleich im Konfig-Ordner gespeichert

        if (!fileUrl.isEmpty()) {
            // der Benutzer hat eine Datei vorgegeben, es wird diese Liste NEU geladen
            alwaysLoadNew = true;
        } else if (fileUrl.isEmpty() && !ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.get().isEmpty()) {
            // der Benutzer hat eine Datei vorgegeben, es wird diese Liste NEU geladen
            fileUrl = ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.get();
            alwaysLoadNew = true;
        }

        Duration.staticPing("Filme laden, start");
        PLog.userLog("");
        PLog.userLog("Alte Liste erstellt am: " + ProgData.getInstance().filmlist.genDate());
        PLog.userLog("  Anzahl Filme: " + progData.filmlist.size());
        PLog.userLog("  Anzahl Neue: " + progData.filmlist.countNewFilms());

        if (!getPropLoadFilmlist()) {
            // nicht doppelt starten
            setPropLoadFilmlist(true);
            // Hash mit URLs füllen
            hashSet.clear();
            fillHash(progData.filmlist);
            if (alwaysLoadNew) {
                // dann die alte löschen, damit immer komplett geladen wird, aber erst nach dem Hash!!
                // sonst wird eine "zu kurze" Liste wieder nur mit einer Diff-Liste aufgefüllt, wenn das
                // Alter noch passt
                progData.filmlist.clear();
            }
            progData.filmlistFiltered.clear();
            if (fileUrl.isEmpty()) {
                // Filme als Liste importieren, Url automatisch ermitteln
                PLog.userLog("Filmliste laden (auto)");
                setStop(false);
                importNewFilmliste.importFilmListAuto(progData.filmlist,
                        diffListe, ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getInt());
            } else {
                // Filme als Liste importieren, feste URL/Datei
                PLog.userLog("Filmliste laden von: " + fileUrl);
                progData.filmlist.clear();
                setStop(false);
                importNewFilmliste.importFilmlistFromFile(fileUrl,
                        progData.filmlist, ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getInt());
            }
        }
    }

    /**
     * Filmliste beim Programmstart laden
     */
    public void loadFilmlistProgStart() {

        setPropLoadFilmlist(true);

        // Gui startet ein wenig flüssiger
        Thread th = new Thread(() -> {

            Duration.staticPing("Programmstart Daten laden");

            final ProgData progData = ProgData.getInstance();
            ArrayList<String> list = new ArrayList<>();

            // gespeicherte Filmliste laden
            Platform.runLater(() -> progData.mtPlayerController.getBtnFilmliste().setDisable(true));
            new ReadFilmlist().readFilmlist(ProgInfos.getFilmListFile(),
                    progData.filmlist, ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getInt());
            Platform.runLater(() -> progData.mtPlayerController.getBtnFilmliste().setDisable(false));

            list.add(PLog.LILNE3);
            list.add("Liste Filme gelesen am: " + StringFormatters.FORMATTER_ddMMyyyyHHmm.format(new Date()));
            list.add("  erstellt am: " + progData.filmlist.genDate());
            list.add("  Anzahl Filme: " + progData.filmlist.size());
            list.add("  Anzahl Neue: " + progData.filmlist.countNewFilms());

            if (progData.filmlist.isTooOld() && ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getBool()) {
                list.add("Filmliste zu alt, neue Filmliste laden");
                setPropLoadFilmlist(false);
                loadFilmlist("", false);

            } else {
                // beim Neuladen wird es dann erst gemacht
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.START, new ListenerFilmlistLoadEvent("", "", 0, 0, false/* Fehler */));
                afterLoadFilmlist();
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.FINISHED, new ListenerFilmlistLoadEvent("", "", 0, 0, false/* Fehler */));
            }
            list.add(PLog.LILNE3);
            PLog.userLog(list);

        });
        th.setName("loadFilmlistProgStart");
        th.start();
    }

    // #######################################
    // #######################################

    /**
     * alles was nach einem Neuladen oder Einlesen einer gespeicherten Filmliste ansteht
     */
    private void afterLoadFilmlist() {
        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS, new ListenerFilmlistLoadEvent("", "Filem markieren: Geo, Zukunft, Doppelt",
                ListenerFilmlistLoad.PROGRESS_MAX, 0, false/* Fehler */));
        PLog.userLog("Filem markieren: Geo, Zukunft, Doppelt");
        progData.filmlist.markFilms();


        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS, new ListenerFilmlistLoadEvent("", "Themen suchen",
                ListenerFilmlistLoad.PROGRESS_MAX, 0, false/* Fehler */));
        PLog.userLog("Themen suchen");
        progData.filmlist.themenLaden();


        if (!progData.aboList.isEmpty()) {
            notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS, new ListenerFilmlistLoadEvent("", "Abos eintragen",
                    ListenerFilmlistLoad.PROGRESS_MAX, 0, false/* Fehler */));
            PLog.userLog("Abos eintragen");
            progData.aboList.setAboFuerFilm(progData.filmlist);
        }


        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS, new ListenerFilmlistLoadEvent("", "Blacklist filtern",
                ListenerFilmlistLoad.PROGRESS_MAX, 0, false/* Fehler */));
        PLog.userLog("Blacklist filtern");
        progData.filmlist.filterList();


        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS, new ListenerFilmlistLoadEvent("", "Filme in Downloads eintragen",
                ListenerFilmlistLoad.PROGRESS_MAX, 0, false/* Fehler */));
        PLog.userLog("Filme in Downloads eintragen");
        progData.downloadList.filmEintragen();

        setPropLoadFilmlist(false);
    }

    /**
     * wird nach dem Import einer neuen Liste gemacht
     *
     * @param event
     */
    private void afterImportNewFilmlist(ListenerFilmlistLoadEvent event) {
        // Abos eintragen in der gesamten Liste vor Blacklist da das nur beim Ändern der Filmliste oder
        // beim Ändern von Abos gemacht wird

        PLog.userLog("");

        // wenn nur ein Update
        if (!diffListe.isEmpty()) {
            PLog.userLog("Liste Diff gelesen am: " + StringFormatters.FORMATTER_ddMMyyyyHHmm.format(new Date()));
            PLog.userLog("  Liste Diff erstellt am: " + diffListe.genDate());
            PLog.userLog("  Anzahl Filme: " + diffListe.size());

            progData.filmlist.updateListe(diffListe, true/* Vergleich über Index, sonst nur URL */, true /* ersetzen */);
            progData.filmlist.metaDaten = diffListe.metaDaten;
            progData.filmlist.sort(); // jetzt sollte alles passen
            diffListe.clear();

        } else {
            PLog.userLog("Liste Kompl. gelesen am: " + StringFormatters.FORMATTER_ddMMyyyyHHmm.format(new Date()));
            PLog.userLog("  Liste Kompl erstellt am: " + progData.filmlist.genDate());
            PLog.userLog("  Anzahl Filme: " + progData.filmlist.size());
        }

        findAndMarkNewFilms(progData.filmlist);

        if (event.fehler) {
            PLog.userLog("");
            PLog.userLog("Filmliste laden war fehlerhaft, alte Liste wird wieder geladen");
            Platform.runLater(() -> new MTAlert().showErrorAlert("Filmliste laden", "Das Laden der Filmliste hat nicht geklappt!"));

            // dann die alte Liste wieder laden
            progData.filmlist.clear();
            setStop(false);
            new ReadFilmlist().readFilmlist(ProgInfos.getFilmListFile(),
                    progData.filmlist, ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getInt());
            PLog.userLog("");

        } else {
            new ProgSave().saveFilmlist();
        }

        PLog.userLog("");
        PLog.userLog("Jetzige Liste erstellt am: " + progData.filmlist.genDate());
        PLog.userLog("  Anzahl Filme: " + progData.filmlist.size());
        PLog.userLog("  Anzahl Neue:  " + progData.filmlist.countNewFilms());
        PLog.userLog("");

        afterLoadFilmlist();

        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.FINISHED, event);
    }

    private void fillHash(Filmlist filmlist) {
        hashSet.addAll(filmlist.stream().map(Film::getUrlHistory).collect(Collectors.toList()));
    }


    private void findAndMarkNewFilms(Filmlist filmlist) {

        filmlist.stream() //genauso schnell wie "parallel": ~90ms
                .peek(film -> film.setNewFilm(false))
                .filter(film -> !hashSet.contains(film.getUrlHistory()))
                .forEach(film -> film.setNewFilm(true));

        hashSet.clear();
    }

//    private void notifyEvent(NOTIFY notify, ListenerFilmlistLoadEvent event) {
//        try {
//            Platform.runLater(() -> {
//
//                for (final ListenerFilmlistLoad l : listeners.getListeners(ListenerFilmlistLoad.class)) {
//                    switch (notify) {
//                        case START:
//                            l.start(event);
//                            break;
//                        case PROGRESS:
//                            l.progress(event);
//                            break;
//                        case FINISHED:
//                            l.fertig(event);
//                            break;
//                    }
//                }
//
//            });
//        } catch (final Exception ex) {
//            PLog.errorLog(912045120, ex);
//        }
//    }
}