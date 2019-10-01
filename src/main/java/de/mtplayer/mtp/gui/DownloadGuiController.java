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
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadConstants;
import de.mtplayer.mtp.controller.data.download.DownloadFactory;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.gui.dialog.DownloadEditDialogController;
import de.mtplayer.mtp.gui.mediaDialog.MediaDialogController;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.gui.tools.table.Table;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.guiTools.PTableFactory;
import de.p2tools.p2Lib.tools.PSystemUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Optional;

public class DownloadGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();

    private final TabPane tabPane = new TabPane();
    private final AnchorPane tabFilmInfo = new AnchorPane();
    private final AnchorPane tabBandwidth = new AnchorPane();
    private final AnchorPane tabDownloadInfos = new AnchorPane();
    private final TableView<Download> tableView = new TableView<>();

    private FilmGuiInfoController filmGuiInfoController;
    private DownloadGuiChart downloadGuiChart;
    private DownloadGuiInfo downloadGuiInfo;

    private final ProgData progData;
    private boolean bound = false;
    private final FilteredList<Download> filteredDownloads;
    private final SortedList<Download> sortedDownloads;

    DoubleProperty splitPaneProperty = ProgConfig.DOWNLOAD_GUI_DIVIDER.getDoubleProperty();
    BooleanProperty boolInfoOn = ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getBooleanProperty();

    public DownloadGuiController() {
        progData = ProgData.getInstance();

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);

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
        boolInfoOn.addListener((observable, oldValue, newValue) -> setInfoPane());

        filmGuiInfoController = new FilmGuiInfoController(tabFilmInfo);
        downloadGuiChart = new DownloadGuiChart(progData, tabBandwidth);
        downloadGuiInfo = new DownloadGuiInfo(tabDownloadInfos);

        filteredDownloads = new FilteredList<>(progData.downloadList, p -> true);
        sortedDownloads = new SortedList<>(filteredDownloads);

        setInfoPane();
        initTable();
        initListener();
        setFilterProperty();
        setFilter();
    }

    public void tableRefresh() {
        tableView.refresh();
    }

    public void isShown() {
        setFilm();
    }

    public int getDownloadsShown() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }

    public void playFilm() {
        final Optional<Download> download = getSel();
        if (download.isPresent()) {
            POpen.playStoredFilm(download.get().getDestPathFile(),
                    ProgConfig.SYSTEM_PROG_PLAY_FILME.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
        }
    }

    public void deleteFilmFile() {
        // Download nur löschen wenn er nicht läuft

        final Optional<Download> download = getSel();
        if (!download.isPresent()) {
            return;
        }
        DownloadFactory.deleteFilmFile(download.get());

//        if (download.get().isStateStartedRun()) {
//            PAlert.showErrorAlert("Film löschen", "Download läuft noch", "Download erst stoppen!");
//        }
//        try {
//            File file = new File(download.get().getDestPathFile());
//            if (!file.exists()) {
//                PAlert.showErrorAlert("Film löschen", "", "Die Datei existiert nicht!");
//                return;
//            }
//
//            String header = "", text = "";
//
//            Path infoPath = null;
//            if (download.get().getInfoFile()) {
//                infoPath = MTInfoFile.getInfoFilePath(download.get());
//            }
//            Path subtitlePath = null;
//            if (download.get().isSubtitle()) {
//                subtitlePath = MTSubtitle.getSubtitlePath(download.get());
//            }
//
//            if (infoPath != null || subtitlePath != null) {
//                header = "Gespeicherten Film + Infofile löschen?";
//            } else {
//                header = "Gespeicherten Film löschen?";
//            }
//
//            text = "Die Filmdatei löschen:" + PConst.LINE_SEPARATORx2 + download.get().getDestPathFile();
//            if (infoPath != null) {
//                text += PConst.LINE_SEPARATORx2 + "die Infodatei löschen:" + PConst.LINE_SEPARATOR + infoPath.getFileName().toString();
//            } else if (subtitlePath != null) {
//                text += PConst.LINE_SEPARATORx2 + "die Untertiteldatei löschen:" + PConst.LINE_SEPARATOR + subtitlePath.getFileName().toString();
//            }
//
//            if (PAlert.showAlertOkCancel("Film Löschen?",
//                    header, text)) {
//
//                // und jetzt die Datei löschen
//                PLog.sysLog(new String[]{"Datei löschen: ", file.getAbsolutePath()});
//                if (!file.delete()) {
//                    throw new Exception();
//                }
//            }
//        } catch (Exception ex) {
//            PAlert.showErrorAlert("Film löschen", "Konnte die Datei nicht löschen!", "Fehler beim löschen von:" + PConst.LINE_SEPARATORx2 +
//                    download.get().getDestPathFile());
//            PLog.errorLog(915236547, "Fehler beim löschen: " + download.get().getDestPathFile());
//        }
    }

    public void openDestinationDir() {
        final Optional<Download> download = getSel();
        if (!download.isPresent()) {
            return;
        }

        String s = download.get().getDestPath();
        POpen.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
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
        film.arr[Film.FILM_URL_SMALL] = "";
        film.arr[Film.FILM_URL_RTMP_SMALL] = "";
        // und starten
        FilmTools.playFilm(film, null);
    }


    public void copyUrl() {
        final Optional<Download> download = getSel();
        if (!download.isPresent()) {
            return;
        }
        PSystemUtils.copyToClipboard(download.get().getUrl());
    }

    private void setFilm() {
        Download download = tableView.getSelectionModel().getSelectedItem();
        if (download != null) {
            filmGuiInfoController.setFilm(download.getFilm());
            progData.filmInfoDialogController.setFilm(download.getFilm());
        } else {
            filmGuiInfoController.setFilm(null);
            progData.filmInfoDialogController.setFilm(null);
        }
    }

    public void showFilmInfo() {
        progData.filmInfoDialogController.showFilmInfo();
    }

    public void guiFilmMediaCollection() {
        final Optional<Download> download = getSel();
        if (download.isPresent()) {
            new MediaDialogController(download.get().getTitle());
        }
    }

    public void startDownload(boolean all) {
        downloadStartAgain(all);
    }

    public void stopDownload(boolean all) {
        stopDownloads(all);
    }

    public void stopWaitingDownloads() {
        stopWaiting();
    }

    public void preferDownload() {
        progData.downloadList.preferDownloads(getSelList());
    }

    public void moveDownloadBack() {
        progData.downloadList.putBackDownloads(getSelList());
    }

    public void deleteDownloads() {
        int sel = tableView.getSelectionModel().getSelectedIndex();
        progData.downloadList.delDownloads(getSelList());
        if (sel >= 0) {
            tableView.getSelectionModel().clearSelection();
            if (tableView.getItems().size() > sel) {
                tableView.getSelectionModel().select(sel);
            } else {
                tableView.getSelectionModel().selectLast();
            }
        }
    }

    public void cleanUp() {
        progData.downloadList.cleanUpList();
    }

    public void changeDownload() {
        change();
    }

    public void setFilmShown() {
        // Filme als gesehen markieren
        setFilmShown(true);
    }

    public void setFilmNotShown() {
        // Filme als ungesehen markieren
        setFilmShown(false);
    }

    public void invertSelection() {
        PTableFactory.invertSelection(tableView);
    }

    public void saveTable() {
        new Table().saveTable(tableView, Table.TABLE.DOWNLOAD);
    }

    private ArrayList<Download> getSelList() {
        // todo observableList -> abo
        final ArrayList<Download> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    private Optional<Download> getSel() {
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            PAlert.showInfoNoSelection();
            return Optional.empty();
        }
    }

    private void initListener() {
        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                if (!ProgConfig.FILTER_DOWNLOAD_STATE.get().isEmpty()) {
                    // dann den Filter aktualisieren
                    // todo?? bei vielen Downloads kann das sonst die ganze Tabelle ausbremsen
                    setFilter();
                }
            }
        });
        Listener.addListener(new Listener(Listener.EREIGNIS_BLACKLIST_GEAENDERT, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                if ((Boolean.parseBoolean(ProgConfig.ABO_SEARCH_NOW.get()) || ProgData.automode)
                        && Boolean.parseBoolean(ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.get())) {
                    // nur auf Blacklist reagieren, wenn auch für Abos eingeschaltet
                    progData.worker.searchForAbosAndMaybeStart();
                }
            }
        });
        Listener.addListener(new Listener(Listener.EREIGNIS_SETDATA_CHANGED, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                tableView.refresh();
            }
        });

        progData.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> setFilter()));

        ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getBooleanProperty().addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getBool() || ProgData.automode) {
                Platform.runLater(() -> progData.worker.searchForAbosAndMaybeStart());
            }
        });
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getBool() || ProgData.automode) {
                Platform.runLater(() -> progData.worker.searchForAbosAndMaybeStart());
            }
        });
    }

    private void setInfoPane() {
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
            SplitPane.setResizableWithParent(tabPane, false);
        }
    }

    private void initTable() {
        tableView.setTableMenuButtonVisible(true);
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        new Table().setTable(tableView, Table.TABLE.DOWNLOAD);

        tableView.setItems(sortedDownloads);
        sortedDownloads.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setOnMouseClicked(m -> {
            if (m.getButton().equals(MouseButton.PRIMARY) && m.getClickCount() == 2) {
                changeDownload();
            }
        });

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<Download> download = getSel();
                if (download.isPresent()) {
                    tableView.setContextMenu(new DownloadGuiTableContextMenu(progData, this, tableView).getContextMenu(download.get()));
                } else {
                    tableView.setContextMenu(null);
                }
            }
        });

        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> setFilm());
        });
    }

    private void setFilterProperty() {
        ProgConfig.FILTER_DOWNLOAD_CHANNEL.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_ABO.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_SOURCE.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_KIND.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_STATE.getStringProperty().addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
    }

    private void setFilter() {
        final String sender = ProgConfig.FILTER_DOWNLOAD_CHANNEL.get();
        final String abo = ProgConfig.FILTER_DOWNLOAD_ABO.get();
        final String source = ProgConfig.FILTER_DOWNLOAD_SOURCE.get();
        final String art = ProgConfig.FILTER_DOWNLOAD_KIND.get();
        final String state = ProgConfig.FILTER_DOWNLOAD_STATE.get();

        //System.out.println("Sender: " + sender + " Abo: " + abo + " Quelle: " + quelle + " Art: " + art);
        filteredDownloads.setPredicate(download -> !download.getPlacedBack() &&

                (sender.isEmpty() ? true : download.getChannel().equals(sender)) &&
                (abo.isEmpty() ? true : download.getAboName().equals(abo)) &&
                (source.isEmpty() ? true : download.getSource().equals(source)) &&
                (art.isEmpty() ? true : download.getArt().equals(art)) &&

                (state.isEmpty() ? true : (
                        state.equals(DownloadConstants.STATE_COMBO_NOT_STARTED) && !download.isStarted() ||
                                state.equals(DownloadConstants.STATE_COMBO_WAITING) && download.isStateStartedWaiting() ||
                                state.equals(DownloadConstants.STATE_COMBO_LOADING) && download.isStateStartedRun()
                ))
        );

    }

    private void setFilmShown(boolean shown) {
        // Filme als (un)gesehen markieren
        final ArrayList<Download> arrayDownloads = getSelList();
        final ArrayList<Film> filmArrayList = new ArrayList<>();

        arrayDownloads.stream().forEach(download -> {
            if (download.getFilm() != null) {
                filmArrayList.add(download.getFilm());
            }
        });
        FilmTools.setFilmShown(progData, filmArrayList, shown);
    }

    private void stopWaiting() {
        // es werden alle noch nicht gestarteten Downloads gelöscht
        final ArrayList<Download> listStopDownload = new ArrayList<>();
        tableView.getItems().stream().filter(download -> download.isStateStartedWaiting()).forEach(download -> {
            listStopDownload.add(download);
        });
        progData.downloadList.stopDownloads(listStopDownload);
    }


    private void downloadStartAgain(boolean all) {
        // bezieht sich auf "alle" oder nur die markierten Filme
        // der/die noch nicht gestartet sind, werden gestartet
        // Filme dessen Start schon auf fehler steht werden wieder gestartet

        final ArrayList<Download> startDownloadsList = new ArrayList<>();
        startDownloadsList.addAll(all ? tableView.getItems() : getSelList());

        progData.downloadList.startDownloads(startDownloadsList, true);
    }

    private void stopDownloads(boolean all) {
        // bezieht sich auf "alle" oder nur die markierten Filme

        final ArrayList<Download> listDownloadsSelected = new ArrayList<>();

        // die URLs sammeln
        listDownloadsSelected.addAll(all ? tableView.getItems() : getSelList());
        progData.downloadList.stopDownloads(listDownloadsSelected);
        setFilter();
    }


    private synchronized void change() {
        final Optional<Download> download = getSel();
        if (download.isPresent()) {

            Download downloadCopy = download.get().getCopy();
            DownloadEditDialogController downloadEditDialogController =
                    new DownloadEditDialogController(progData, downloadCopy, download.get().isStateStartedRun());

            if (downloadEditDialogController.isOk()) {
                download.get().copyToMe(downloadCopy);
            }
        }
    }
}
