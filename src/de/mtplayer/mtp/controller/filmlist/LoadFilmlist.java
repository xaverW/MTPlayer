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
import de.mtplayer.mtp.controller.filmlist.checkFilmlistUpdate.SearchForFilmlistUpdate;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ImportNewFilmlist;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ReadFilmlist;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.tools.log.PDuration;
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
    private final SearchForFilmlistUpdate searchForFilmlistUpdate;

    private final NotifyProgress notifyProgress = new NotifyProgress();
    private BooleanProperty propLoadFilmlist = new SimpleBooleanProperty(false);
    private static final AtomicBoolean stop = new AtomicBoolean(false); // damit kannn das Laden gestoppt werden kann

    public LoadFilmlist(ProgData progData) {
        this.progData = progData;
        diffListe = new Filmlist();

        searchForFilmlistUpdate = new SearchForFilmlistUpdate();
        checkForFilmlistUpdate();

        importNewFilmliste = new ImportNewFilmlist(progData);
        importNewFilmliste.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public synchronized void start(ListenerFilmlistLoadEvent event) {
                // Start ans Prog melden
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.START, event);
            }

            @Override
            public synchronized void progress(ListenerFilmlistLoadEvent event) {
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS, event);
            }

            @Override
            public synchronized void finished(ListenerFilmlistLoadEvent event) {
                // Laden ist durch
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                        new ListenerFilmlistLoadEvent("", "Filme verarbeiten",
                                ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));


                // Ergebnisliste listeFilme eintragen -> Feierabend!
                PDuration.onlyPing("Filme laden: Ende");
                afterImportNewFilmlist(event);
                PDuration.onlyPing("Filme nachbearbeiten: Ende");

                // alles fertig ans Prog melden
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.FINISHED, event);
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

    public synchronized boolean isStop() {
        return stop.get();
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
        progData.maskerPane.setButtonVisible(true);

        if (!fileUrl.isEmpty()) {
            // der Benutzer hat eine Datei vorgegeben, es wird diese Liste NEU geladen
            alwaysLoadNew = true;
        } else if (fileUrl.isEmpty() && !ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.get().isEmpty()) {
            // der Benutzer hat eine Datei vorgegeben, es wird diese Liste NEU geladen
            fileUrl = ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.get();
            alwaysLoadNew = true;
        }

        PDuration.onlyPing("Filme laden, start");
        PLog.sysLog("");
        PLog.sysLog("Alte Liste erstellt am: " + ProgData.getInstance().filmlist.genDate());
        PLog.sysLog("  Anzahl Filme: " + progData.filmlist.size());
        PLog.sysLog("  Anzahl Neue: " + progData.filmlist.countNewFilms());

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
                PLog.sysLog("Filmliste laden (auto)");
                setStop(false);
                importNewFilmliste.importFilmListAuto(progData.filmlist,
                        diffListe, ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getInt());
            } else {
                // Filme als Liste importieren, feste URL/Datei
                PLog.sysLog("Filmliste laden von: " + fileUrl);
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
    public void loadFilmlistProgStart(boolean firstProgramStart) {

        setPropLoadFilmlist(true);
        // Start des Ladens, gibt keine Vortschrittsanzeige und keine Abbrechen
        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.START,
                new ListenerFilmlistLoadEvent("", "gespeicherte Filmliste laden",
                        ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false));

        // Gui startet ein wenig flüssiger
        Thread th = new Thread(() -> {
            PDuration.onlyPing("Programmstart Daten laden");

            final ProgData progData = ProgData.getInstance();
            ArrayList<String> list = new ArrayList<>();

            if (!firstProgramStart) {
                // gespeicherte Filmliste laden, macht beim ersten Programmstart keinen Sinn
                new ReadFilmlist().readFilmlist(ProgInfos.getFilmListFile(),
                        progData.filmlist, ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getInt(),
                        // Datumcheck macht nur Sinn, wenn beim Programmstart auch ein Update gemacht werden soll
//                        ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getBool());
                        false);

                list.add(PLog.LILNE3);
                list.add("Liste Filme gelesen am: " + StringFormatters.FORMATTER_ddMMyyyyHHmm.format(new Date()));
                list.add("  erstellt am: " + progData.filmlist.genDate());
                list.add("  Anzahl Filme: " + progData.filmlist.size());
                list.add("  Anzahl Neue: " + progData.filmlist.countNewFilms());
            }

            if (progData.filmlist.isTooOld() && ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getBool()) {
                list.add("Filmliste zu alt, neue Filmliste laden");
                setPropLoadFilmlist(false);
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS,
                        new ListenerFilmlistLoadEvent("", "Filmliste ist zu alt, eine neue downloaden",
                                ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

                loadFilmlist("", false);

            } else {
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                        new ListenerFilmlistLoadEvent("", "Filme verarbeiten",
                                ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

                // beim Neuladen wird es dann erst gemacht
                afterLoadFilmlist();
                notifyProgress.notifyFinishedOk();
            }

            list.add(PLog.LILNE3);
            PLog.sysLog(list);

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
        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                new ListenerFilmlistLoadEvent("", "Filme markieren, Themen suchen",
                        ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

        PLog.sysLog("Filme markieren");
        progData.filmlist.markFilms();

        PLog.sysLog("Themen suchen");
        progData.filmlist.loadTheme();


        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                new ListenerFilmlistLoadEvent("", "Abos eintragen, Blacklist filtern",
                        ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

        if (!progData.aboList.isEmpty()) {
            PLog.sysLog("Abos eintragen");
            progData.aboList.setAboForFilm(progData.filmlist);
        }

        PLog.sysLog("Blacklist filtern");
        progData.filmlist.filterList();


        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                new ListenerFilmlistLoadEvent("", "neue Downloads suchen",
                        ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

        PLog.sysLog("neue Downloads suchen");
        progData.downloadList.addFilmInList();

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

        PLog.sysLog("");

        // wenn nur ein Update
        if (!diffListe.isEmpty()) {
            PLog.sysLog("Liste Diff gelesen am: " + StringFormatters.FORMATTER_ddMMyyyyHHmm.format(new Date()));
            PLog.sysLog("  Liste Diff erstellt am: " + diffListe.genDate());
            PLog.sysLog("  Anzahl Filme: " + diffListe.size());

            progData.filmlist.updateList(diffListe, true/* Vergleich über Index, sonst nur URL */, true /* ersetzen */);
            progData.filmlist.metaData = diffListe.metaData;
            progData.filmlist.sort(); // jetzt sollte alles passen
            diffListe.clear();

        } else {
            PLog.sysLog("Liste Kompl. gelesen am: " + StringFormatters.FORMATTER_ddMMyyyyHHmm.format(new Date()));
            PLog.sysLog("  Liste Kompl erstellt am: " + progData.filmlist.genDate());
            PLog.sysLog("  Anzahl Filme: " + progData.filmlist.size());
        }

        findAndMarkNewFilms(progData.filmlist);

        if (event.error) {
            PLog.sysLog("");
            PLog.sysLog("Filmliste laden war fehlerhaft, alte Liste wird wieder geladen");
            final boolean stopped = isStop();
            Platform.runLater(() -> PAlert.showErrorAlert("Filmliste laden",
                    stopped ? "Das Laden einer neuen Filmliste wurde abgebrochen!" :
                            "Das Laden einer neuen Filmliste hat nicht geklappt!")
            );

            // dann die alte Liste wieder laden
            progData.filmlist.clear();
            setStop(false);
            new ReadFilmlist().readFilmlist(ProgInfos.getFilmListFile(),
                    progData.filmlist, ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getInt());
            PLog.sysLog("");

        } else {
            new ProgSave().saveFilmlist();
        }

        PLog.sysLog("");
        PLog.sysLog("Jetzige Liste erstellt am: " + progData.filmlist.genDate());
        PLog.sysLog("  Anzahl Filme: " + progData.filmlist.size());
        PLog.sysLog("  Anzahl Neue:  " + progData.filmlist.countNewFilms());
        PLog.sysLog("");

        afterLoadFilmlist();
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

    private void checkForFilmlistUpdate() {
        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, LoadFilmlist.class.getSimpleName()) {
            @Override
            public void ping() {
                try {

                    if (getPropLoadFilmlist()) {
                        // dann laden wir gerade
                        return;
                    }

                    // URL direkt aus der Liste holen, sonst wird minütlich! die URL-Liste aktualisiert!!
                    final String url = ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.get().isEmpty() ?
                            progData.searchFilmListUrls.getFilmlistUrlList_akt().getRand(null) :
                            ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.get();

                    if (searchForFilmlistUpdate.doCheck(url, progData.filmlist.genDate())) {
                        Platform.runLater(() -> ProgData.getInstance().mtPlayerController.setButtonFilmlistUpdate());
                    }

                } catch (final Exception ex) {
                    PLog.errorLog(963014785, ex);
                }
            }
        });
    }

}