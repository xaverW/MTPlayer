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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.Download;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.mtplayer.controller.data.film.Film;
import de.p2tools.mtplayer.controller.data.film.FilmTools;
import de.p2tools.mtplayer.gui.chart.DownloadGuiChart;
import de.p2tools.mtplayer.gui.dialog.DownloadEditDialogController;
import de.p2tools.mtplayer.gui.dialog.DownloadStartAtTimeController;
import de.p2tools.mtplayer.gui.mediaDialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableRowDownload;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.guiTools.PTableFactory;
import de.p2tools.p2Lib.guiTools.pClosePane.PClosePaneH;
import de.p2tools.p2Lib.tools.PSystemUtils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Optional;

public class DownloadGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final TableView<Download> tableView = new TableView<>();
    private final TabPane tabPane = new TabPane();

    private FilmGuiInfoController filmGuiInfoController;
    private DownloadGuiChart downloadGuiChart;
    private DownloadGuiInfo downloadGuiInfo;
    private final PClosePaneH pClosePaneH;

    private final ProgData progData;
    private boolean bound = false;
    private final FilteredList<Download> filteredDownloads;
    private final SortedList<Download> sortedDownloads;

    DoubleProperty splitPaneProperty = ProgConfig.DOWNLOAD_GUI_DIVIDER;
    BooleanProperty boolInfoOn = ProgConfig.DOWNLOAD_GUI_DIVIDER_ON;

    public DownloadGuiController() {
        progData = ProgData.getInstance();
        filmGuiInfoController = new FilmGuiInfoController();
        downloadGuiChart = new DownloadGuiChart(progData);
        downloadGuiInfo = new DownloadGuiInfo();

        pClosePaneH = new PClosePaneH(ProgConfig.DOWNLOAD_GUI_DIVIDER_ON, false);

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
        tabFilm.setContent(filmGuiInfoController);

        Tab tabBand = new Tab("Bandbreite");
        tabBand.setClosable(false);
        tabBand.setContent(downloadGuiChart);

        Tab tabDown = new Tab("DownloadInfos");
        tabDown.setClosable(false);
        tabDown.setContent(downloadGuiInfo);

        tabPane.getTabs().addAll(tabFilm, tabBand, tabDown);
        pClosePaneH.getVBoxAll().getChildren().clear();
        pClosePaneH.getVBoxAll().getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        boolInfoOn.addListener((observable, oldValue, newValue) -> setInfoPane());

        filteredDownloads = new FilteredList<>(progData.downloadList, p -> true);
        sortedDownloads = new SortedList<>(filteredDownloads);

        setInfoPane();
        initTable();
        initListener();
        setFilterProperty();
        setFilter();

        Listener.addListener(new Listener(Listener.EREIGNIS_TIMER, DownloadGuiChart.class.getSimpleName()) {
            @Override
            public void pingFx() {
                downloadGuiChart.searchInfos(progData.mtPlayerController.isDownloadPaneShown() &&
                        tabPane.getSelectionModel().getSelectedItem().equals(tabBand));
            }
        });
    }

    public void tableRefresh() {
        tableView.refresh();
    }

    public void isShown() {
        setFilm();
        tableView.requestFocus();
        progData.filmFilterControllerClearFilter.setClearText("Filter löschen");
        progData.downloadFilterController.setClearText("Filter _löschen");
        progData.aboFilterController.setClearText("Filter löschen");
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
                    ProgConfig.SYSTEM_PROG_PLAY_FILME, new ProgIcons().ICON_BUTTON_FILE_OPEN);
        }
    }

    public void deleteFilmFile() {
        // Download nur löschen wenn er nicht läuft

        final Optional<Download> download = getSel();
        if (!download.isPresent()) {
            return;
        }
        DownloadFactory.deleteFilmFile(download.get());
    }

    public void openDestinationDir() {
        final Optional<Download> download = getSel();
        if (!download.isPresent()) {
            return;
        }

        String s = download.get().getDestPath();
        POpen.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR, new ProgIcons().ICON_BUTTON_FILE_OPEN);
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

    public void startDownloadTime() {
        new DownloadStartAtTimeController(progData,
                tableView.getItems(),
                tableView.getSelectionModel().getSelectedItems());
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

    public void selectAll() {
        tableView.getSelectionModel().selectAll();
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
        return getSel(true);
    }

    private Optional<Download> getSel(boolean show) {
        final int selectedTableRow = tableView.getSelectionModel().getSelectedIndex();
        if (selectedTableRow >= 0) {
            return Optional.of(tableView.getSelectionModel().getSelectedItem());
        } else {
            if (show) {
                PAlert.showInfoNoSelection();
            }
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
                if ((ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.automode)
                        && ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getValue()) {
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

        ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.automode) {
                Platform.runLater(() -> progData.worker.searchForAbosAndMaybeStart());
            }
        });
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.automode) {
                Platform.runLater(() -> progData.worker.searchForAbosAndMaybeStart());
            }
        });
    }

    private void setInfoPane() {
        pClosePaneH.setVisible(boolInfoOn.getValue());
        pClosePaneH.setManaged(boolInfoOn.getValue());

        if (!boolInfoOn.getValue()) {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(splitPaneProperty);
            }
            splitPane.getItems().clear();
            splitPane.getItems().add(scrollPane);

        } else {
            bound = true;

            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPane, pClosePaneH);
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(splitPaneProperty);
            SplitPane.setResizableWithParent(pClosePaneH, false);
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

        tableView.setRowFactory(tv -> {
            TableRowDownload<Download> row = new TableRowDownload<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    changeDownload();
                }
            });
            return row;
        });
        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<Download> optionalDownload = getSel(false);
                Download download;
                if (optionalDownload.isPresent()) {
                    download = optionalDownload.get();
                } else {
                    download = null;
                }
                ContextMenu contextMenu = new DownloadGuiTableContextMenu(progData, this, tableView).getContextMenu(download);
                tableView.setContextMenu(contextMenu);
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> setFilm());
        });
        tableView.getItems().addListener((ListChangeListener<Download>) c -> {
            if (tableView.getItems().size() == 1) {
                // wenns nur eine Zeile gibt, dann gleich selektieren
                tableView.getSelectionModel().select(0);
            }
        });
        tableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (PTableFactory.SPACE.match(event)) {
                PTableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (PTableFactory.SPACE_SHIFT.match(event)) {
                PTableFactory.scrollVisibleRangeUp(tableView);
                event.consume();
            }
        });
    }

    private void setFilterProperty() {
        ProgConfig.FILTER_DOWNLOAD_CHANNEL.addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_ABO.addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_SOURCE.addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_TYPE.addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
        ProgConfig.FILTER_DOWNLOAD_STATE.addListener((observable, oldValue, newValue) -> {
            setFilter();
        });
    }

    private void setFilter() {
        final String sender = ProgConfig.FILTER_DOWNLOAD_CHANNEL.getValueSafe();
        final String abo = ProgConfig.FILTER_DOWNLOAD_ABO.getValueSafe();
        final String source = ProgConfig.FILTER_DOWNLOAD_SOURCE.getValueSafe();
        final String type = ProgConfig.FILTER_DOWNLOAD_TYPE.getValueSafe();
        final String state = ProgConfig.FILTER_DOWNLOAD_STATE.getValueSafe();

        filteredDownloads.setPredicate(download -> !download.getPlacedBack() &&

                (sender.isEmpty() ? true : download.getChannel().equals(sender)) &&
                (abo.isEmpty() ? true : download.getAboName().equals(abo)) &&
                (source.isEmpty() ? true : download.getSource().equals(source)) &&
                (type.isEmpty() ? true : download.getType().equals(type)) &&

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
        // es werden alle noch nicht gestarteten Downloads gestoppt
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
