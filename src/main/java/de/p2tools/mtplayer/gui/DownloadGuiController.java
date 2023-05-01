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
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactoryDelFilmFile;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmTools;
import de.p2tools.mtplayer.gui.chart.DownloadGuiChart;
import de.p2tools.mtplayer.gui.dialog.DownloadEditDialogController;
import de.p2tools.mtplayer.gui.dialog.DownloadStartAtTimeController;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableDownload;
import de.p2tools.mtplayer.gui.tools.table.TableRowDownload;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.POpen;
import de.p2tools.p2lib.guitools.PTableFactory;
import de.p2tools.p2lib.guitools.pclosepane.PClosePaneH;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.PSystemUtils;
import javafx.application.Platform;
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
import java.util.function.Predicate;

public class DownloadGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final TableDownload tableView;
    private final TabPane tabPane = new TabPane();

    private FilmGuiInfoController filmGuiInfoController;
    private DownloadGuiMedia downloadGuiMedia;
    private DownloadGuiChart downloadGuiChart;
    private DownloadGuiInfo downloadGuiInfo;
    private final PClosePaneH pClosePaneH;

    private final Tab tabFilm = new Tab("Filminfo");
    private final Tab tabMedia = new Tab("Mediensammlung");
    private final Tab tabBand = new Tab("Bandbreite");
    private final Tab tabDown = new Tab("DownloadInfos");

    private final ProgData progData;
    private boolean bound = false;
    private final FilteredList<DownloadData> filteredDownloads;
    private final SortedList<DownloadData> sortedDownloads;

    public DownloadGuiController() {
        progData = ProgData.getInstance();
        filmGuiInfoController = new FilmGuiInfoController();
        downloadGuiMedia = new DownloadGuiMedia();
        downloadGuiChart = new DownloadGuiChart(progData);
        downloadGuiInfo = new DownloadGuiInfo();
        tableView = new TableDownload(Table.TABLE_ENUM.DOWNLOAD);

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

        pClosePaneH.getVBoxAll().getChildren().clear();
        pClosePaneH.getVBoxAll().getChildren().add(tabPane);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.addListener((observable, oldValue, newValue) -> setInfoPane());

        filteredDownloads = new FilteredList<>(progData.downloadList, p -> true);
        sortedDownloads = new SortedList<>(filteredDownloads);

        addTabs();
        setInfoPane();
        initTable();
        initListener();
        setFilterProperty();
        setFilter();

        Listener.addListener(new Listener(Listener.EVENT_TIMER, DownloadGuiChart.class.getSimpleName()) {
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
        setFilmInfos(tableView.getSelectionModel().getSelectedItem());
        tableView.requestFocus();
    }

    public int getDownloadsShown() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }

    public void playFilm() {
        final Optional<DownloadData> download = getSel();
        if (download.isPresent()) {
            POpen.playStoredFilm(download.get().getDestPathFile(),
                    ProgConfig.SYSTEM_PROG_PLAY_FILME, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
        }
    }

    public void deleteFilmFile() {
        // Download nur löschen, wenn er nicht läuft
        final Optional<DownloadData> download = getSel();
        if (!download.isPresent()) {
            return;
        }
        DownloadFactoryDelFilmFile.deleteFilmFile(download.get());
    }

    public void openDestinationDir() {
        final Optional<DownloadData> download = getSel();
        if (!download.isPresent()) {
            return;
        }

        String s = download.get().getDestPath();
        POpen.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
    }

    public void playUrl() {
        // aus Menü
        final Optional<DownloadData> download = getSel();
        if (!download.isPresent()) {
            return;
        }

        FilmDataMTP film;
        if (download.get().getFilm() == null) {
            film = new FilmDataMTP();
        } else {
            film = download.get().getFilm().getCopy();
        }

        // und jetzt die tatsächlichen URLs des Downloads eintragen
        film.arr[FilmDataMTP.FILM_URL] = download.get().getUrl();
        film.arr[FilmDataMTP.FILM_URL_SMALL] = "";
        // und starten
        FilmTools.playFilm(film, null);
    }


    public void copyUrl() {
        final Optional<DownloadData> download = getSel();
        if (!download.isPresent()) {
            return;
        }
        PSystemUtils.copyToClipboard(download.get().getUrl());
    }

    private void setFilmInfos(DownloadData download) {
        if (tabPane.getSelectionModel().getSelectedItem().equals(tabMedia)) {
            downloadGuiMedia.setSearchPredicate(download);
        }
        filmGuiInfoController.setFilm(download != null ? download.getFilm() : null);
        FilmInfoDialogController.getInstance().setFilm(download != null ? download.getFilm() : null);
    }

    public void showFilmInfo() {
        FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
    }

    public void guiFilmMediaCollection() {
        final Optional<DownloadData> download = getSel();
        if (download.isPresent()) {
            new MediaDialogController(download.get().getTheme(), download.get().getTitle());
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
        // Downloads stoppen -> aus Menü
        stopDownloads(all);
    }

    public void stopWaitingDownloads() {
        // aus dem Menü
        stopWaiting();
    }

    public void preferDownload() {
        progData.downloadList.preferDownloads(getSelList());
    }

    public void moveDownloadBack() {
        progData.downloadList.putBackDownloads(getSelList());
    }

    public void deleteDownloads() {
        // aus dem Menü
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

    public void changeDownload() {
        change();
    }

    public void undoDeleteDownload() {
        progData.downloadList.undoDownloads();
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
        Table.saveTable(tableView, Table.TABLE_ENUM.DOWNLOAD);
    }

    private void addTabs() {
        tabFilm.setClosable(false);
        tabFilm.setContent(filmGuiInfoController);

        tabMedia.setClosable(false);
        tabMedia.setContent(downloadGuiMedia);
        tabPane.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            if (tabPane.getSelectionModel().getSelectedItem().equals(tabMedia)) {
                setFilmInfos(tableView.getSelectionModel().getSelectedItem());
            }
        });

        tabBand.setClosable(false);
        tabBand.setContent(downloadGuiChart);

        tabDown.setClosable(false);
        tabDown.setContent(downloadGuiInfo);

        tabPane.getTabs().addAll(tabFilm, tabMedia, tabBand, tabDown);
    }

    private ArrayList<DownloadData> getSelList() {
        // todo observableList -> abo
        final ArrayList<DownloadData> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    private Optional<DownloadData> getSel() {
        return getSel(true);
    }

    private Optional<DownloadData> getSel(boolean show) {
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
        Listener.addListener(new Listener(Listener.EVENT_TIMER, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                if (!ProgConfig.FILTER_DOWNLOAD_STATE.get().isEmpty()) {
                    // dann den Filter aktualisieren
                    // todo?? bei vielen Downloads kann das sonst die ganze Tabelle ausbremsen
                    setFilter();
                }
            }
        });
        Listener.addListener(new Listener(Listener.EVENT_BLACKLIST_CHANGED, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                if ((ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode)
                        && ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getValue()) {
                    // nur auf Blacklist reagieren, wenn auch für Abos eingeschaltet
                    progData.worker.searchForAbosAndMaybeStart();
                }
            }
        });
        Listener.addListener(new Listener(Listener.EVEMT_SETDATA_CHANGED, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                tableView.refresh();
            }
        });

        progData.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> setFilter()));

        ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode) {
                Platform.runLater(() -> progData.worker.searchForAbosAndMaybeStart());
            }
        });
        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode) {
                Platform.runLater(() -> progData.worker.searchForAbosAndMaybeStart());
            }
        });
    }

    private void setInfoPane() {
        pClosePaneH.setVisible(ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getValue());
        pClosePaneH.setManaged(ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getValue());

        if (!ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getValue()) {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.DOWNLOAD_GUI_DIVIDER);
            }
            splitPane.getItems().clear();
            splitPane.getItems().add(scrollPane);

        } else {
            bound = true;

            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPane, pClosePaneH);
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.DOWNLOAD_GUI_DIVIDER);
            SplitPane.setResizableWithParent(pClosePaneH, false);
        }
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(sortedDownloads);
        sortedDownloads.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tv -> {
            TableRowDownload<DownloadData> row = new TableRowDownload<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    changeDownload();
                }
            });

            row.hoverProperty().addListener((observable) -> {
                final DownloadData downloadData = (DownloadData) row.getItem();
                if (row.isHover() && downloadData != null) {
                    setFilmInfos(downloadData);
                } else {
                    setFilmInfos(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                //wird auch durch FilmlistenUpdate ausgelöst
                Platform.runLater(() -> setFilmInfos(tableView.getSelectionModel().getSelectedItem())));

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<DownloadData> optionalDownload = getSel(false);
                DownloadData download;
                if (optionalDownload.isPresent()) {
                    download = optionalDownload.get();
                } else {
                    download = null;
                }
                ContextMenu contextMenu = new DownloadGuiTableContextMenu(progData, this, tableView).getContextMenu(download);
                tableView.setContextMenu(contextMenu);
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
        Predicate<DownloadData> predicate = downloadData -> true;
        final String sender = ProgConfig.FILTER_DOWNLOAD_CHANNEL.getValueSafe();
        final String abo = ProgConfig.FILTER_DOWNLOAD_ABO.getValueSafe();
        final String source = ProgConfig.FILTER_DOWNLOAD_SOURCE.getValueSafe();
        final String type = ProgConfig.FILTER_DOWNLOAD_TYPE.getValueSafe();
        final String state = ProgConfig.FILTER_DOWNLOAD_STATE.getValueSafe();

        predicate = predicate.and(download -> !download.isPlacedBack());

        if (!sender.isEmpty()) {
            Filter filter = new Filter(sender, true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getChannel()));
        }

        if (!abo.isEmpty()) {
            predicate = predicate.and(downloadData -> downloadData.getAboName().equals(abo));
        }
        if (!source.isEmpty()) {
            predicate = predicate.and(downloadData -> downloadData.getSource().equals(source));
        }
        if (!type.isEmpty()) {
            predicate = predicate.and(downloadData -> downloadData.getType().equals(type));
        }
        if (!state.isEmpty()) {
            predicate = predicate.and(downloadData -> state.equals(DownloadConstants.STATE_COMBO_NOT_STARTED) && !downloadData.isStarted() ||
                    state.equals(DownloadConstants.STATE_COMBO_WAITING) && downloadData.isStateStartedWaiting() ||
                    state.equals(DownloadConstants.STATE_COMBO_STARTED) && downloadData.isStarted() ||
                    state.equals(DownloadConstants.STATE_COMBO_LOADING) && downloadData.isStateStartedRun() ||
                    state.equals(DownloadConstants.STATE_COMBO_ERROR) && downloadData.isStateError());
        }

        filteredDownloads.setPredicate(predicate);
    }


    private void setFilmShown(boolean shown) {
        // Filme als (un)gesehen markieren
        final ArrayList<DownloadData> arrayDownloadData = getSelList();
        final ArrayList<FilmDataMTP> filmArrayList = new ArrayList<>();

        arrayDownloadData.stream().forEach(download -> {
            if (download.getFilm() != null) {
                filmArrayList.add(download.getFilm());
            }
        });
        FilmTools.setFilmShown(progData, filmArrayList, shown);
    }

    private void stopWaiting() {
        // aus dem Menü
        // es werden alle noch nicht gestarteten Downloads, gestoppt
        final ArrayList<DownloadData> listStopDownload = new ArrayList<>();
        tableView.getItems().stream().filter(download -> download.isStateStartedWaiting()).forEach(download -> {
            listStopDownload.add(download);
        });
        progData.downloadList.stopDownloads(listStopDownload);
    }


    private void downloadStartAgain(boolean all) {
        // bezieht sich auf "alle" oder nur die markierten Filme
        // der/die noch nicht gestartet sind, werden gestartet
        // Filme dessen Start schon auf fehler steht werden wieder gestartet
        final ArrayList<DownloadData> startDownloadsList = new ArrayList<>();
        startDownloadsList.addAll(all ? tableView.getItems() : getSelList());
        progData.downloadList.startDownloads(startDownloadsList, true);
    }

    private void stopDownloads(boolean all) {
        // stoppen aus Menü
        // bezieht sich auf "alle" oder nur die markierten Filme
        final ArrayList<DownloadData> listDownloadsSelected = new ArrayList<>();
        // die URLs sammeln
        listDownloadsSelected.addAll(all ? tableView.getItems() : getSelList());

        progData.downloadList.stopDownloads(listDownloadsSelected);
        setFilter();
    }

    private synchronized void change() {
        final Optional<DownloadData> download = getSel();
        if (download.isPresent()) {
            DownloadData downloadCopy = download.get().getCopy();
            DownloadEditDialogController downloadEditDialogController =
                    new DownloadEditDialogController(progData, downloadCopy, download.get().isStateStartedRun());

            if (downloadEditDialogController.isOk()) {
                download.get().copyToMe(downloadCopy);
            }
        }
    }
}
