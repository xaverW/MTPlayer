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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.mtplayer.mtp.gui.dialog.DownloadEditDialogController;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.mediaDialog.MediaDialogController;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.gui.tools.MTOpen;
import de.mtplayer.mtp.gui.tools.Table;
import de.p2tools.p2Lib.tools.SysTools;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class DownloadGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final TableView<Download> table = new TableView<>();

    private final TabPane tabPane = new TabPane();
    private final AnchorPane tabFilmInfo = new AnchorPane();
    private final AnchorPane tabBandwidth = new AnchorPane();
    private final AnchorPane tabDownloadInfos = new AnchorPane();
    private final ScrollPane scrollPane = new ScrollPane();

    private final FilteredList<Download> filteredDownloads;
    private final SortedList<Download> sortedDownloads;
    private FilmGuiInfoController filmGuiInfoController;
    private DownloadGuiChart downloadGuiChart;
    private DownloadGuiInfo downloadGuiInfo;

    private final ProgData progData;
    DoubleProperty splitPaneProperty = ProgConfig.DOWNLOAD_GUI_DIVIDER.getDoubleProperty();
    BooleanProperty boolInfoOn = ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getBooleanProperty();
    private boolean bound = false;

    public DownloadGuiController() {
        progData = ProgData.getInstance();


        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(table);

        Tab tabFilm = new Tab("Filminfo");
        tabFilm.setClosable(false);
        tabFilm.setContent(tabFilmInfo);

        Tab tabBand = new Tab("Bandbreite");
        tabBand.setClosable(false);
        tabBand.setContent(tabBandwidth);

        Tab tabDown = new Tab("DownloadInfos");
        tabDown.setClosable(false);
        tabDown.setContent(tabDownloadInfos);

        tabPane.getTabs().addAll(tabFilm, tabBand, tabDown);

        splitPane.setOrientation(Orientation.VERTICAL);
        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);

        boolInfoOn.addListener((observable, oldValue, newValue) -> setSplit());
        setSplit();
        getChildren().addAll(splitPane);

        filmGuiInfoController = new FilmGuiInfoController(tabFilmInfo);
        downloadGuiChart = new DownloadGuiChart(progData, tabBandwidth);
        downloadGuiInfo = new DownloadGuiInfo(tabDownloadInfos);

        filteredDownloads = new FilteredList<>(progData.downloadList, p -> true);
        sortedDownloads = new SortedList<>(filteredDownloads);
        setFilterProperty();

        initTable();
        initListener();
        setfilter();
    }


    public void isShown() {
        setFilm();
    }

    public int getFilmCount() {
        return table.getItems().size();
    }

    public int getSelCount() {
        return table.getSelectionModel().getSelectedItems().size();
    }

    public void aktualisieren() {
        if (progData.loadFilmlist.getPropLoadFilmlist()) {
            // wird danach eh gemacht
            return;
        }

        // erledigte entfernen, nicht gestartete Abos entfernen und neu nach Abos suchen
        progData.downloadList.abosSuchen();

        if (Boolean.parseBoolean(ProgConfig.DOWNLOAD_START_NOW.get())) {
            // und wenn gewollt auch gleich starten
            downloadStartenWiederholen(true /* alle */, false /* fertige wieder starten */);
        }
    }

    public void filmAbspielen() {
        final Optional<Download> download = getSel();
        if (download.isPresent()) {
            MTOpen.playStoredFilm(download.get().getZielPfadDatei());
        }
    }

    public void filmDateiLoeschen() {
        // Download nur löschen wenn er nicht läuft

        final Optional<Download> download = getSel();
        if (!download.isPresent()) {
            return;
        }

        if (download.get().isStateStartedRun()) {
            new MTAlert().showErrorAlert("Film löschen", "Download läuft noch", "Download erst stoppen!");
        }
        try {
            File file = new File(download.get().getZielPfadDatei());
            if (!file.exists()) {
                new MTAlert().showErrorAlert("Film löschen", "", "Die Datei existiert nicht!");
                return;
            }

            if (new MTAlert().showAlert("Film Löschen?", "", "Die Datei löschen:\n\n" + download.get().getZielPfadDatei())) {

                // und jetzt die Datei löschen
                PLog.sysLog(new String[]{"Datei löschen: ", file.getAbsolutePath()});
                if (!file.delete()) {
                    throw new Exception();
                }
            }
        } catch (Exception ex) {
            new MTAlert().showErrorAlert("Film löschen", "Konnte die Datei nicht löschen!", "Fehler beim löschen von:\n\n" +
                    download.get().getZielPfadDatei());
            PLog.errorLog(915236547, "Fehler beim löschen: " + download.get().getZielPfadDatei());
        }
    }

    public void openDestDir() {
        final Optional<Download> download = getSel();
        if (!download.isPresent()) {
            return;
        }

        String s = download.get().getZielPfad();
        MTOpen.openDestDir(s);
    }

    public void playUrl() {
        final Optional<Download> download = getSel();
        if (!download.isPresent()) {
            return;
        }

        Film film;
        if (download.get().getFilm() == null) {
            film = new Film();
        } else {
            film = download.get().getFilm().getCopy();
        }

        // und jetzt die tatsächlichen URLs des Downloads eintragen
        film.arr[Film.FILM_URL] = download.get().getUrl();
        film.arr[Film.FILM_URL_RTMP] = download.get().getUrlRtmp();
        film.arr[Film.FILM_URL_KLEIN] = "";
        film.arr[Film.FILM_URL_RTMP_KLEIN] = "";
        // und starten
        FilmTools.playFilm(film, null);
    }


    public void copyUrl() {
        final Optional<Download> download = getSel();
        if (!download.isPresent()) {
            return;
        }
        SysTools.copyToClipboard(download.get().getUrl());
    }

    private void setFilm() {
        Download download = table.getSelectionModel().getSelectedItem();
        if (download != null) {
            filmGuiInfoController.setFilm(download.getFilm());
            progData.filmInfosDialogController.set(download.getFilm());
        } else {
            filmGuiInfoController.setFilm(null);
            progData.filmInfosDialogController.set(null);
        }
    }

    public void showFilmInfo() {
        progData.filmInfosDialogController.showFilmInfo();
    }

    public void guiFilmMediensammlung() {
        final Optional<Download> download = getSel();
        if (download.isPresent()) {
            new MediaDialogController(download.get().getTitel());
        }
    }

    public void starten(boolean alle) {
        downloadStartenWiederholen(alle, true /* auch fertige */);
    }

    public void stoppen(boolean alle) {
        downloadStoppen(alle);
    }

    public void wartendeStoppen() {
        wartendeDownloadsStoppen();
    }

    public void vorziehen() {
        progData.downloadList.downloadsVorziehen(getSelList());
    }

    public void zurueckstellen() {
        progData.downloadList.putbackDownloads(getSelList());
    }

    public void loeschen() {

        int sel = table.getSelectionModel().getSelectedIndex();
        progData.downloadList.delDownloads(getSelList());
        if (sel >= 0) {
            table.getSelectionModel().clearSelection();
            if (table.getItems().size() > sel) {
                table.getSelectionModel().select(sel);
            } else {
                table.getSelectionModel().selectLast();
            }
        }
    }

    public void aufraeumen() {
        progData.downloadList.listePutzen();
    }

    public void aendern() {
        downloadAendern();
    }

    public void filmGesehen() {
        setFilmGesehen(true);
    }

    public void filmUngesehen() {
        setFilmGesehen(false);
    }

    public void invertSelection() {
        for (int i = 0; i < table.getItems().size(); ++i)
            if (table.getSelectionModel().isSelected(i)) {
                table.getSelectionModel().clearSelection(i);
            } else {
                table.getSelectionModel().select(i);
            }
    }

    public void saveTable() {
        new Table().saveTable(table, Table.TABLE.DOWNLOAD);
    }

    private void setSplit() {
        tabPane.setVisible(boolInfoOn.getValue());
        tabPane.setManaged(boolInfoOn.getValue());
        if (!boolInfoOn.getValue()) {

            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(splitPaneProperty);
            }

            splitPane.getItems().clear();
            splitPane.getItems().add(scrollPane);

        } else {
            bound = true;
            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPane, tabPane);
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(splitPaneProperty);
        }
    }

    private ArrayList<Download> getSelList() {
        final ArrayList<Download> ret = new ArrayList<>();
        ret.addAll(table.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            new MTAlert().showInfoNoSelection();
        }
        return ret;
    }

    private Optional<Download> getSel() {
        final int selectedTableRow = table.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(table.getSelectionModel().getSelectedItem());
        } else {
            new MTAlert().showInfoNoSelection();
            return Optional.empty();
        }
    }

    private void initListener() {

        progData.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> setfilter()));
        Listener.addListener(new Listener(Listener.EREIGNIS_BLACKLIST_GEAENDERT, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void ping() {
                if (Boolean.parseBoolean(ProgConfig.ABO_SEARCH_NOW.get())
                        && Boolean.parseBoolean(ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.get())) {
                    // nur auf Blacklist reagieren, wenn auch für Abos eingeschaltet
                    aktualisieren();
                }
            }
        });
        ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getBooleanProperty().addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getBool()) {
                aktualisieren();
            }
        });
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getBool()) {
                aktualisieren();
            }
        });
        progData.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {

            }

            @Override
            public void fertig(ListenerFilmlistLoadEvent event) {
                if (ProgConfig.ABO_SEARCH_NOW.getBool()) {
                    aktualisieren();
                }
            }
        });
    }

    private void initTable() {
        table.setTableMenuButtonVisible(true);
        table.setEditable(false);
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        new Table().setTable(table, Table.TABLE.DOWNLOAD);

        table.setItems(sortedDownloads);
        sortedDownloads.comparatorProperty().bind(table.comparatorProperty());

        table.setOnMouseClicked(m -> {
            if (m.getButton().equals(MouseButton.PRIMARY) && m.getClickCount() == 2) {
                aendern();
            }
        });

        table.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<Download> download = getSel();
                if (download.isPresent()) {
                    table.setContextMenu(new DownloadGuiContextMenu(progData, this, table).getContextMenue(download.get()));
                }
            }
        });

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> setFilm());
        });
    }

    private void setFilterProperty() {
        ProgConfig.FILTER_DOWNLOAD_SENDER.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setfilter();
        });
        ProgConfig.FILTER_DOWNLOAD_ABO.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setfilter();
        });
        ProgConfig.FILTER_DOWNLOAD_SOURCE.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setfilter();
        });
        ProgConfig.FILTER_DOWNLOAD_KIND.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setfilter();
        });
    }

    private void setfilter() {
        final String sender = ProgConfig.FILTER_DOWNLOAD_SENDER.get();
        final String abo = ProgConfig.FILTER_DOWNLOAD_ABO.get();
        final String quelle = ProgConfig.FILTER_DOWNLOAD_SOURCE.get();
        final String art = ProgConfig.FILTER_DOWNLOAD_KIND.get();

        //System.out.println("Sender: " + sender + " Abo: " + abo + " Quelle: " + quelle + " Art: " + art);
        filteredDownloads.setPredicate(download -> (!download.isZurueckgestellt() &&
                sender.isEmpty() ? true : download.getSender().equals(sender)) &&
                (abo.isEmpty() ? true : download.getAboName().equals(abo)) &&
                (quelle.isEmpty() ? true : download.getSource().equals(quelle)) &&
                (art.isEmpty() ? true : download.getArt().equals(art)));

    }

    private void setFilmGesehen(boolean gesehen) {
        final ArrayList<Download> arrayDownloads = getSelList();
        final ArrayList<Film> filmArrayList = new ArrayList<>();

        arrayDownloads.stream().forEach(download -> {
            if (download.getFilm() != null) {
                filmArrayList.add(download.getFilm());
            }
        });
        FilmTools.setFilmShown(progData, filmArrayList, gesehen);
    }

    private void wartendeDownloadsStoppen() {
        // es werden alle noch nicht gestarteten Downloads gelöscht
        final ArrayList<Download> listeStopDownload = new ArrayList<>();
        table.getItems().stream().filter(download -> download.isStateStartedWaiting()).forEach(download -> {
            listeStopDownload.add(download);
        });
        progData.downloadList.stopDownloads(listeStopDownload);
    }

    private void downloadStartenWiederholen(boolean alle, boolean auchFertige /* auch fertige wieder starten */) {
        // bezieht sich auf "alle" oder nur die markierten Filme
        // der/die noch nicht gestartet sind, werden gestartet
        // Filme dessen Start schon auf fehler steht wirden wieder gestartet

        final ArrayList<Download> listeDownloadsMarkiert = new ArrayList<>();

        // die URLs sammeln
        listeDownloadsMarkiert.addAll(alle ? table.getItems() : getSelList());

        progData.downloadList.startDownloads(listeDownloadsMarkiert, auchFertige);
    }

    private void downloadStoppen(boolean alle) {
        // bezieht sich auf "alle" oder nur die markierten Filme

        final ArrayList<Download> listeDownloadsMarkiert = new ArrayList<>();

        // die URLs sammeln
        listeDownloadsMarkiert.addAll(alle ? table.getItems() : getSelList());
        progData.downloadList.stopDownloads(listeDownloadsMarkiert);
        setfilter();
    }


    private synchronized void downloadAendern() {
        final Optional<Download> download = getSel();
        if (download.isPresent()) {

            Download datenDownloadKopy = download.get().getCopy();
            DownloadEditDialogController downloadEditDialogController =
                    new DownloadEditDialogController(progData, datenDownloadKopy, download.get().isStateStartedRun());

            if (downloadEditDialogController.isOk()) {
                download.get().copyToMe(datenDownloadKopy);
            }
        }
    }
}
