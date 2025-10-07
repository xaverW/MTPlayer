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

import de.p2tools.mtplayer.MTPlayerController;
import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactoryDelDownloadFiles;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.data.film.FilmToolsFactory;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.dialog.downloadadd.DownloadAddDialogController;
import de.p2tools.mtplayer.gui.dialog.downloaddialog.DownloadStartAtTimeController;
import de.p2tools.mtplayer.gui.infoPane.*;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.mtplayer.gui.mediadialog.MediaDialogController;
import de.p2tools.mtplayer.gui.tools.table.Table;
import de.p2tools.mtplayer.gui.tools.table.TableDownload;
import de.p2tools.mtplayer.gui.tools.table.TableRowDownload;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Open;
import de.p2tools.p2lib.guitools.P2RowFactory;
import de.p2tools.p2lib.guitools.P2TableFactory;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneController;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneDto;
import de.p2tools.p2lib.guitools.pclosepane.P2ClosePaneFactory;
import de.p2tools.p2lib.mediathek.filter.Filter;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;
import de.p2tools.p2lib.p2event.P2Events;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.P2ToolsFactory;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    public final TableDownload tableView;
    private final ProgData progData;
    private final FilteredList<DownloadData> filteredListDownloads;
    private final SortedList<DownloadData> sortedListDownloads;

    private final PaneFilmInfo paneFilmInfo;
    private final PaneMedia paneMedia;
    private final PaneBandwidthChart paneBandwidthChart;
    private final PaneDownloadError paneDownloadError;
    private final PaneDownloadInfoList paneDownloadInfoList;
    private final P2ClosePaneController infoController;
    private final BooleanProperty boundInfo = new SimpleBooleanProperty(false);

    public DownloadGuiController() {
        progData = ProgData.getInstance();
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


        paneFilmInfo = new PaneFilmInfo(ProgConfig.DOWNLOAD_PANE_INFO_DIVIDER);

        MediaDataDto mDtoMedia = new MediaDataDto();
        MediaDataDto mDtoAbo = new MediaDataDto();
        initDto(mDtoMedia, mDtoAbo);
        paneMedia = new PaneMedia(mDtoMedia, mDtoAbo);

        paneBandwidthChart = new PaneBandwidthChart(progData);
        paneDownloadError = new PaneDownloadError();
        paneDownloadInfoList = new PaneDownloadInfoList();

        ArrayList<P2ClosePaneDto> list = new ArrayList<>();
        P2ClosePaneDto infoDto = new P2ClosePaneDto(paneFilmInfo,
                ProgConfig.DOWNLOAD__INFO_PANE_IS_RIP,
                ProgConfig.DOWNLOAD__INFO_DIALOG_SIZE, MTPlayerController.TAB_DOWNLOAD_ON,
                "Beschreibung", "Beschreibung", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoDto = new P2ClosePaneDto(paneMedia,
                ProgConfig.DOWNLOAD__MEDIA_PANE__IS_RIP,
                ProgConfig.DOWNLOAD__MEDIA_DIALOG_SIZE, MTPlayerController.TAB_DOWNLOAD_ON,
                "Mediensammlung", "Mediensammlung", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoDto = new P2ClosePaneDto(paneBandwidthChart,
                ProgConfig.DOWNLOAD__CHART_PANE_IS_RIP,
                ProgConfig.DOWNLOAD__CHART_DIALOG_SIZE, MTPlayerController.TAB_DOWNLOAD_ON,
                "Bandbreite", "Bandbreite", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoDto = new P2ClosePaneDto(paneDownloadError,
                ProgConfig.DOWNLOAD__ERROR_PANE_IS_RIP,
                ProgConfig.DOWNLOAD__ERROR_DIALOG_SIZE, MTPlayerController.TAB_DOWNLOAD_ON,
                "Fehler", "Fehler", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoDto = new P2ClosePaneDto(paneDownloadInfoList,
                ProgConfig.DOWNLOAD__LIST_PANE_IS_RIP,
                ProgConfig.DOWNLOAD__LIST_DIALOG_SIZE, MTPlayerController.TAB_DOWNLOAD_ON,
                "Infos", "Infos", false,
                progData.maskerPane.getVisibleProperty());
        list.add(infoDto);

        infoController = new P2ClosePaneController(list, ProgConfig.DOWNLOAD__INFO_IS_SHOWING);

        ProgConfig.DOWNLOAD__INFO_IS_SHOWING.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.DOWNLOAD__INFO_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.DOWNLOAD__MEDIA_PANE__IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.DOWNLOAD__ERROR_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.DOWNLOAD__CHART_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());
        ProgConfig.DOWNLOAD__LIST_PANE_IS_RIP.addListener((observable, oldValue, newValue) -> setInfoPane());

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
            P2Alert.showInfoNoSelection();
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
                P2Alert.showInfoNoSelection();
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
                ProgConfig.SYSTEM_PROG_PLAY_FILME, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView()));
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
        P2Open.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR, ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
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
        downloadData.ifPresent(data -> P2ToolsFactory.copyToClipboard(theme ? data.getTheme() : data.getTitle()));
    }

    public void copyUrl() {
        final Optional<DownloadData> download = getSel();
        if (download.isEmpty()) {
            return;
        }
        P2ToolsFactory.copyToClipboard(download.get().getUrl());
    }

    private void setFilmInfos(DownloadData download) {
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.TAB_DOWNLOAD_ON, paneFilmInfo)) {
            paneFilmInfo.setFilm(download);
        }
        if (InfoPaneFactory.paneIsVisible(MTPlayerController.TAB_DOWNLOAD_ON, paneMedia)) {
            paneMedia.setSearchPredicate(download);
        }
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
        // Menü
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
        // Menü: Filme als gesehen markieren
        setFilmShown(true);
    }

    public void setFilmNotShown() {
        // Menü: Filme als ungesehen markieren
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
        ProgData.getInstance().pEventHandler.addListener(new P2Listener(P2Events.EVENT_TIMER_SECOND) {
            @Override
            public void pingGui() {
                paneBandwidthChart.searchInfos(InfoPaneFactory.paneIsVisible(MTPlayerController.TAB_DOWNLOAD_ON,
                        paneBandwidthChart)
                );

                if (InfoPaneFactory.paneIsVisible(MTPlayerController.TAB_DOWNLOAD_ON, paneDownloadInfoList)) {
                    paneDownloadInfoList.setInfoText();
                }
            }
        });
        ProgData.getInstance().pEventHandler.addListener(new P2Listener(P2Events.EVENT_TIMER_SECOND) {
            @Override
            public void pingGui() {
                if (!ProgConfig.FILTER_DOWNLOAD_STATE.get().isEmpty()) {
                    // dann den Filter aktualisieren
                    // todo?? bei vielen Downloads kann das sonst die ganze Tabelle ausbremsen
                    setFilter();
                }
            }
        });

        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_SET_DATA_CHANGED) {
            @Override
            public void pingGui() {
                tableView.refresh();
            }
        });
        progData.downloadList.downloadsChangedProperty().addListener((observable, oldValue, newValue) ->
                setFilter());
    }

    private void initTable() {
        Table.setTable(tableView);
        tableView.setItems(sortedListDownloads);
        sortedListDownloads.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setRowFactory(new P2RowFactory<>(tv -> {
            TableRowDownload<DownloadData> row = new TableRowDownload<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 &&
                        !row.isEmpty()) {
                    changeDownload();
                }
            });

            row.hoverProperty().addListener((observable) -> {
                final DownloadData downloadData = row.getItem();
                if (row.isHover() && downloadData != null) { // null bei den leeren Zeilen unterhalb
                    setFilmInfos(downloadData);
                } else if (downloadData == null) {
                    setFilmInfos(tableView.getSelectionModel().getSelectedItem());
                }
            });

            return row;
        }));

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
        ProgConfig.FILTER_DOWNLOAD_LIST.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_DOWNLOAD_CHANNEL.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_DOWNLOAD_ABO.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_DOWNLOAD_SOURCE.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_DOWNLOAD_TYPE.addListener((observable, oldValue, newValue) -> setFilter());
        ProgConfig.FILTER_DOWNLOAD_STATE.addListener((observable, oldValue, newValue) -> setFilter());
    }

    private void setFilter() {
        Platform.runLater(() -> {
            Predicate<DownloadData> predicate = downloadData -> true;
            final String list = ProgConfig.FILTER_DOWNLOAD_LIST.getValueSafe();
            final String sender = ProgConfig.FILTER_DOWNLOAD_CHANNEL.getValueSafe();
            final String abo = ProgConfig.FILTER_DOWNLOAD_ABO.getValueSafe();
            final String source = ProgConfig.FILTER_DOWNLOAD_SOURCE.getValueSafe();
            final String type = ProgConfig.FILTER_DOWNLOAD_TYPE.getValueSafe();
            final String state = ProgConfig.FILTER_DOWNLOAD_STATE.getValueSafe();

            predicate = predicate.and(download -> !download.isPlacedBack());

            if (!list.isEmpty()) {
                predicate = predicate.and(downloadData ->
                        list.equals(DownloadConstants.SRC_COMBO_FILM) && !downloadData.isAudio() ||
                                list.equals(DownloadConstants.SRC_COMBO_AUDIO) && downloadData.isAudio());
            }

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
        });
    }

    private void setFilmShown(boolean shown) {
        // Menü: Filme als (un)gesehen markieren
        final ArrayList<DownloadData> arrayDownloadData = getSelList();
        final ArrayList<FilmDataMTP> filmArrayList = new ArrayList<>();

        arrayDownloadData.forEach(download -> {
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
        // Menü
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
        final ArrayList<DownloadData> listDownloadsSelected =
                new ArrayList<>(all ? tableView.getItems() : getSelList());

        progData.downloadList.stopDownloads(listDownloadsSelected);
        setFilter();
    }

    private synchronized void change() {
        ArrayList<DownloadData> list = getSelList();
        if (!list.isEmpty()) {
            new DownloadAddDialogController(progData, list);
        }
    }

    private void setInfoPane() {
        P2ClosePaneFactory.setSplit(boundInfo, splitPane,
                infoController, false, scrollPane,
                ProgConfig.DOWNLOAD__INFO_DIVIDER, ProgConfig.DOWNLOAD__INFO_IS_SHOWING);
    }

    private void initDto(MediaDataDto mediaDataDtoMedia, MediaDataDto mediaDataDtoAbo) {
        mediaDataDtoMedia.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_MEDIA;
        mediaDataDtoMedia.buildSearchFrom = ProgConfig.INFO_DOWNLOAD_BUILD_SEARCH_FROM_FOR_MEDIA;
        mediaDataDtoMedia.searchInWhat = ProgConfig.INFO_DOWNLOAD_SEARCH_IN_WHAT_FOR_MEDIA;
        mediaDataDtoMedia.cleaning = ProgConfig.INFO_DOWNLOAD_CLEAN_MEDIA;
        mediaDataDtoMedia.cleaningExact = ProgConfig.INFO_DOWNLOAD_CLEAN_EXACT_MEDIA;
        mediaDataDtoMedia.cleaningAndOr = ProgConfig.INFO_DOWNLOAD_CLEAN_AND_OR_MEDIA;
        mediaDataDtoMedia.cleaningList = ProgConfig.INFO_DOWNLOAD_CLEAN_LIST_MEDIA;

        mediaDataDtoAbo.whatToShow = MediaDataDto.SHOW_WHAT.SHOW_ABO;
        mediaDataDtoAbo.buildSearchFrom = ProgConfig.INFO_DOWNLOAD_BUILD_SEARCH_FROM_FOR_ABO;
        mediaDataDtoAbo.searchInWhat = ProgConfig.INFO_DOWNLOAD_SEARCH_IN_WHAT_FOR_ABO;
        mediaDataDtoAbo.cleaning = ProgConfig.INFO_DOWNLOAD_CLEAN_ABO;
        mediaDataDtoAbo.cleaningExact = ProgConfig.INFO_DOWNLOAD_CLEAN_EXACT_ABO;
        mediaDataDtoAbo.cleaningAndOr = ProgConfig.INFO_DOWNLOAD_CLEAN_AND_OR_ABO;
        mediaDataDtoAbo.cleaningList = ProgConfig.INFO_DOWNLOAD_CLEAN_LIST_ABO;
    }

}
