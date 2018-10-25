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
import de.mtplayer.mtp.controller.filmlist.writeFilmlist.WriteFilmlistJson;
import de.mtplayer.mtp.gui.tools.Listener;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.tools.log.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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


                PDuration.onlyPing("Filme laden: Ende");
                afterImportNewFilmlist(event);
                PDuration.onlyPing("Filme nachbearbeiten: Ende");

                PLog.addSysLog("Filmliste laden, fertig");

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

    public void loadFilmlist() {
        loadFilmlist(false);
    }

    public void loadFilmlist(boolean alwaysLoadNew) {
        // damit wird die Filmliste geladen UND auch gleich im Konfig-Ordner gespeichert
        progData.maskerPane.setButtonVisible(true);

        if (!getPropLoadFilmlist()) {
            setPropLoadFilmlist(true);
            // nicht doppelt starten

            final List<String> logList = new ArrayList<>();

            PDuration.onlyPing("Filmliste laden: start");
            startMsg();
            logList.add("");
            logList.add("Alte Liste erstellt  am: " + ProgData.getInstance().filmlist.genDate());
            logList.add("           Anzahl Filme: " + progData.filmlist.size());
            logList.add("           Anzahl  Neue: " + progData.filmlist.countNewFilms());
            logList.add(" ");

            String fileUrl = "";
            if (!ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.get().isEmpty()) {
                // dann hat der Benutzer eine URL in den Einstellungen vorgegeben
                fileUrl = ProgConfig.SYSTEM_LOAD_FILMS_MANUALLY.get();
                alwaysLoadNew = true;
            }

            // Hash mit URLs füllen
            fillHash(progData.filmlist);

            if (alwaysLoadNew) {
                // dann die alte Filmliste löschen, damit immer komplett geladen wird, aber erst nach dem Hash!!
                progData.filmlist.clear();
            }

            PLog.addSysLog(logList);
            loadList(fileUrl);
        }
    }


    private void loadList(String fileUrl) {
        progData.filmlistFiltered.clear();
        setStop(false);

        if (fileUrl.isEmpty()) {
            // Filmeliste laden und Url automatisch ermitteln
            PLog.addSysLog("Filmliste laden (auto)");
            importNewFilmliste.importFilmListAuto(progData.filmlist,
                    diffListe, ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getInt());

        } else {
            // Filmeliste laden von URL/Datei
            PLog.addSysLog("Filmliste mit fester URL/Datei laden");
            progData.filmlist.clear();
            importNewFilmliste.importFilmlistFromFile(fileUrl,
                    progData.filmlist, ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getInt());
        }
    }

    /**
     * Filmliste beim Programmstart laden
     */
    public void loadFilmlistProgStart(boolean firstProgramStart) {
        // Start des Ladens, gibt keine Vortschrittsanzeige und keine Abbrechen

        setPropLoadFilmlist(true);
        PDuration.onlyPing("Programmstart Filmliste laden: start");
        startMsg();
        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.START,
                new ListenerFilmlistLoadEvent("", "gespeicherte Filmliste laden",
                        ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false));

        // Gui startet ein wenig flüssiger
        Thread th = new Thread(() -> {

            final ProgData progData = ProgData.getInstance();
            final List<String> logList = new ArrayList<>();

            if (!firstProgramStart) {
                // gespeicherte Filmliste laden, macht beim ersten Programmstart keinen Sinn
                new ReadFilmlist().readFilmlist(ProgInfos.getFilmListFile(),
                        progData.filmlist, ProgConfig.SYSTEM_NUM_DAYS_FILMLIST.getInt(),
                        // Datumcheck macht nur Sinn, wenn beim Programmstart auch ein Update gemacht werden soll
                        ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getBool() ? hashSet : null);

                PDuration.onlyPing("Programmstart Filmliste laden: geladen");
            }

            if (progData.filmlist.isTooOld() && ProgConfig.SYSTEM_LOAD_FILMS_ON_START.getBool()) {
                logList.add("Filmliste zu alt, neue Filmliste laden");
                logList.add(PLog.LILNE3);
                PLog.addSysLog(logList);

                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.PROGRESS,
                        new ListenerFilmlistLoadEvent("", "Filmliste ist zu alt, eine neue downloaden",
                                ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

                PDuration.onlyPing("Programmstart Filmliste laden: neue Liste laden");
                loadList("");
                PDuration.onlyPing("Programmstart Filmliste laden: neue Liste geladen");

            } else {
                notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                        new ListenerFilmlistLoadEvent("", "Filme verarbeiten",
                                ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

                afterLoadFilmlist(logList);
                PLog.addSysLog(logList);
                stopMsg();
                notifyProgress.notifyFinishedOk();
            }
        });

        th.setName("loadFilmlistProgStart");
        th.start();
    }

    // #######################################
    // #######################################

    private void startMsg() {
        PLog.addSysLog("");
        PLog.sysLog(PLog.LILNE1);
        PLog.addSysLog("Filmliste laden");
    }

    private void stopMsg() {
        PLog.addSysLog("Filmliste geladen");
        PLog.sysLog(PLog.LILNE1);
        PLog.addSysLog("");
    }

    /**
     * alles was nach einem Neuladen oder Einlesen einer gespeicherten Filmliste ansteht
     */
    private void afterLoadFilmlist(List<String> logList) {
        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                new ListenerFilmlistLoadEvent("", "Filme markieren, Themen suchen",
                        ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

        logList.add("Filme markieren");
        final int count = progData.filmlist.markFilms();
        logList.add("Anzahl doppelte Filme: " + count);

        logList.add("Themen suchen");
        progData.filmlist.loadTheme();


        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                new ListenerFilmlistLoadEvent("", "Abos eintragen, Blacklist filtern",
                        ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

        if (!progData.aboList.isEmpty()) {
            logList.add("Abos eintragen");
            progData.aboList.setAboForFilm(progData.filmlist);
        }

        logList.add("Blacklist filtern");
        progData.filmlist.filterList();


        notifyProgress.notifyEvent(NotifyProgress.NOTIFY.LOADED,
                new ListenerFilmlistLoadEvent("", "Filme in Downloads eingetragen",
                        ListenerFilmlistLoad.PROGRESS_INDETERMINATE, 0, false/* Fehler */));

        logList.add("Filme in Downloads eingetragen");
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

        final List<String> logList = new ArrayList<>();

        logList.add(PLog.LILNE3);
        // wenn nur ein Update
        if (!diffListe.isEmpty()) {
            progData.filmlist.updateList(diffListe, true/* Vergleich über Index, sonst nur URL */, true /* ersetzen */);
            progData.filmlist.metaData = diffListe.metaData;
            progData.filmlist.sort(); // jetzt sollte alles passen
            diffListe.clear();
        }

        findAndMarkNewFilms(progData.filmlist);

        if (event.error) {
            logList.add("");
            logList.add("Filmliste laden war fehlerhaft, alte Liste wird wieder geladen");
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
            logList.add("");

        } else {
            logList.add("");
            logList.add("Filme schreiben (" + progData.filmlist.size() + " Filme) :");
            logList.add("   --> Start Schreiben nach: " + ProgInfos.getFilmListFile());
            new WriteFilmlistJson().write(ProgInfos.getFilmListFile(), progData.filmlist);
            logList.add("   --> geschrieben!");
            logList.add("");
        }

        logList.add("");
        logList.add("Jetzige Liste erstellt am: " + progData.filmlist.genDate());
        logList.add("  Anzahl Filme: " + progData.filmlist.size());
        logList.add("  Anzahl Neue:  " + progData.filmlist.countNewFilms());
        logList.add("");

        afterLoadFilmlist(logList);
        PLog.addSysLog(logList);
    }

    private void fillHash(Filmlist filmlist) {
        hashSet.clear();
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