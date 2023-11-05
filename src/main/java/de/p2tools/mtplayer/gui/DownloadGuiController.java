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

import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.mtplayer.controller.data.download.DownloadFactoryDelDownloadFiles;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.film.FilmToolsFactory;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.dialog.downloadadd.DownloadAddDialogController;
import de.p2tools.mtplayer.gui.dialog.downloaddialog.DownloadStartAtTimeController;
import de.p2tools.mtplayer.gui.infoPane.DownloadInfoController;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableDownload;
import de.p2tools.mtplayer.gui.tools.table.TableRowDownload;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.PSystemUtils;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Orientation;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class DownloadGuiController extends AnchorPane {

    private final SplitPane splitPane = new SplitPane();
    private final ScrollPane scrollPane = new ScrollPane();
    private final TableDownload tableView;
    private final DownloadInfoController downloadInfoController;
    private final ProgData progData;
    private final FilteredList<DownloadData> filteredListDownloads;
    private final SortedList<DownloadData> sortedListDownloads;
    private boolean bound = false;

    public DownloadGuiController() {
        progData = ProgData.getInstance();
        downloadInfoController = new DownloadInfoController();
        tableView = new TableDownload(Table.TABLE_ENUM.DOWNLOAD);

        AnchorPane.setLeftAnchor(splitPane, 0.0);
        AnchorPane.setBottomAnchor(splitPane, 0.0);
        AnchorPane.setRightAnchor(splitPane, 0.0);
        AnchorPane.setTopAnchor(splitPane, 0.0);
        splitPane.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(splitPane);

        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(tableView);

        ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.addListener((observable, oldValue, newValue) -> setInfoPane());

        filteredListDownloads = new FilteredList<>(progData.downloadList, p -> true);
        sortedListDownloads = new SortedList<>(filteredListDownloads);

        setInfoPane();
        initTable();
        initListener();
        setFilterProperty();
        setFilter();
    }

    public void tableRefresh() {
        Platform.runLater(() -> P2TableFactory.refreshTable(tableView));
    }

    public void isShown() {
        setFilmInfos(tableView.getSelectionModel().getSelectedItem());
        tableView.requestFocus();
    }

    public ArrayList<DownloadData> getSelList() {
        // todo observableList -> abo
        final ArrayList<DownloadData> ret = new ArrayList<>();
        ret.addAll(tableView.getSelectionModel().getSelectedItems());
        if (ret.isEmpty()) {
            PAlert.showInfoNoSelection();
        }
        return ret;
    }

    public Optional<DownloadData> getSel() {
        return getSel(true);
    }

    public Optional<DownloadData> getSel(boolean show) {
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

    public int getDownloadsShown() {
        return tableView.getItems().size();
    }

    public int getSelCount() {
        return tableView.getSelectionModel().getSelectedItems().size();
    }

    public void playFilm() {
        final Optional<DownloadData> download = getSel();
        download.ifPresent(downloadData -> P2Open.playStoredFilm(downloadData.getDestPathFile(),
                ProgConfig.SYSTEM_PROG_PLAY_FILME, ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView()));
    }

    public void deleteFilmFile() {
        // Download nur löschen, wenn er nicht läuft
        final Optional<DownloadData> download = getSel();
        if (download.isEmpty()) {
            return;
        }
        DownloadFactoryDelDownloadFiles.deleteFilesOfDownload(download.get());
    }

    public void openDestinationDir() {
        final Optional<DownloadData> download = getSel();
        if (download.isEmpty()) {
            return;
        }

        String s = download.get().getDestPath();
        P2Open.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
    }

    public void playUrl() {
        // aus Menü
        final Optional<DownloadData> download = getSel();
        if (download.isEmpty()) {
            return;
        }
        FilmPlayFactory.playUrl(download.get());
    }

    public void copyFilmThemeTitle(boolean theme) {
        final Optional<DownloadData> downloadData = getSel(true);
        downloadData.ifPresent(data -> PSystemUtils.copyToClipboard(theme ? data.getTheme() : data.getTitle()));
    }

    public void copyUrl() {
        final Optional<DownloadData> download = getSel();
        if (download.isEmpty()) {
            return;
        }
        PSystemUtils.copyToClipboard(download.get().getUrl());
    }

    private void setFilmInfos(DownloadData download) {
        downloadInfoController.setDownloadInfos(download);
        FilmInfoDialogController.getInstance().setFilm(download != null ? download.getFilm() : null);
    }

    public void showFilmInfo() {
        FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
    }

    public void searchFilmInMediaCollection() {
        final Optional<DownloadData> download = getSel();
        download.ifPresent(downloadData -> new MediaDialogController(downloadData.getTheme(), downloadData.getTitle()));
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
        P2TableFactory.invertSelection(tableView);
    }

    public void saveTable() {
        Table.saveTable(tableView, Table.TABLE_ENUM.DOWNLOAD);
    }

    private void initListener() {
        PListener.addListener(new PListener(PListener.EVENT_TIMER_SECOND, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                if (!ProgConfig.FILTER_DOWNLOAD_STATE.get().isEmpty()) {
                    // dann den Filter aktualisieren
                    // todo?? bei vielen Downloads kann das sonst die ganze Tabelle ausbremsen
                    setFilter();
                }
            }
        });
        PListener.addListener(new PListener(PListener.EVENT_BLACKLIST_CHANGED, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                if ((ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode)
                        && ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.getValue()) {
                    // nur auf Blacklist reagieren, wenn auch für Abos eingeschaltet
                    DownloadFactory.searchForAbosAndMaybeStart();
                }
            }
        });
        PListener.addListener(new PListener(PListener.EVENT_SET_DATA_CHANGED, DownloadGuiController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                tableView.refresh();
            }
        });

        progData.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(this::setFilter));

//        ProgConfig.SYSTEM_BLACKLIST_SHOW_ABO.addListener((observable, oldValue, newValue) -> {
//            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode) {
//                Platform.runLater(DownloadFactory::searchForAbosAndMaybeStart);
//            }
//        });
//        progData.aboList.listChangedProperty().addListener((observable, oldValue, newValue) -> {
//            if (ProgConfig.ABO_SEARCH_NOW.getValue() || ProgData.autoMode) {
//                Platform.runLater(DownloadFactory::searchForAbosAndMaybeStart);
//            }
//        });
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(sortedListDownloads);
        sortedListDownloads.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(tv -> {
            TableRowDownload<DownloadData> row = new TableRowDownload<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                    changeDownload();
                }
            });

            row.hoverProperty().addListener((observable) -> {
                final DownloadData downloadData = (DownloadData) row.getItem();
                if (row.isHover() && downloadData != null) { // null bei den leeren Zeilen unterhalb
                    setFilmInfos(downloadData);
                } else if (downloadData == null) {
                    setFilmInfos(tableView.getSelectionModel().getSelectedItem());
                }
            });
            return row;
        });
        tableView.hoverProperty().addListener((o) -> {
            if (!tableView.isHover()) {
                setFilmInfos(tableView.getSelectionModel().getSelectedItem());
            }
        });
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                //wird auch durch FilmlistenUpdate ausgelöst
                Platform.runLater(() -> setFilmInfos(tableView.getSelectionModel().getSelectedItem())));

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                final Optional<DownloadData> optionalDownload = getSel(false);
                DownloadData download;
                download = optionalDownload.orElse(null);
                ContextMenu contextMenu = new DownloadTableContextMenu(progData, this, tableView).getContextMenu(download);
                tableView.setContextMenu(contextMenu);
            }
        });

        tableView.addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (P2TableFactory.SPACE.match(event)) {
                P2TableFactory.scrollVisibleRangeDown(tableView);
                event.consume();
            }
            if (P2TableFactory.SPACE_SHIFT.match(event)) {
                P2TableFactory.scrollVisibleRangeUp(tableView);
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

        filteredListDownloads.setPredicate(predicate);
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
        FilmToolsFactory.setFilmShown(filmArrayList, shown);
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
        ArrayList<DownloadData> list = getSelList();
        if (!list.isEmpty()) {
            new DownloadAddDialogController(progData, list);

//            DownloadData downloadCopy = download.get().getCopy();
//            DownloadEditDialogController downloadEditDialogController =
//                    new DownloadEditDialogController(progData, downloadCopy, download.get().isStateStartedRun());
//            if (downloadEditDialogController.isOk()) {
//                download.get().copyToMe(downloadCopy);
//            }
        }
    }

    private void setInfoPane() {
        if (!ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getValue()) {
            if (bound) {
                splitPane.getDividers().get(0).positionProperty().unbindBidirectional(ProgConfig.DOWNLOAD_GUI_DIVIDER);
            }
            splitPane.getItems().clear();
            splitPane.getItems().add(scrollPane);

        } else {
            bound = true;

            splitPane.getItems().clear();
            splitPane.getItems().addAll(scrollPane, downloadInfoController);
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.DOWNLOAD_GUI_DIVIDER);
            SplitPane.setResizableWithParent(downloadInfoController, false);
        }
    }
}
