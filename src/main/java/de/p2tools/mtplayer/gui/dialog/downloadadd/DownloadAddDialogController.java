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

package de.p2tools.mtplayer.gui.dialog.downloadadd;

import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.data.setdata.SetFactory;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.starter.LogMsgFactory;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.dialogs.P2DirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import java.util.ArrayList;
import java.util.List;

public class DownloadAddDialogController extends P2DialogExtra {

    private final ProgData progData;
    private final Button btnOk = new Button("_Ok");
    private final Button btnCancel = new Button("_Abbrechen");
    private boolean ok = false;
    private boolean setAll = false;

    private final ArrayList filmsToDownloadList;
    private final AddDownloadDto addDownloadDto;

    public DownloadAddDialogController(ProgData progData, ArrayList<FilmDataMTP> filmsToDownloadList,
                                       SetData setDataStart, String filterResolution) {
        super(progData.primaryStage,
                filmsToDownloadList.size() > 1 ? ProgConfig.DOWNLOAD_DIALOG_ADD_MORE_SIZE :
                        ProgConfig.DOWNLOAD_DIALOG_ADD_SIZE,
                "Download anlegen", true, false, DECO.BORDER_SMALL);

        // neue Downloads anlegen
        this.progData = progData;
        this.filmsToDownloadList = filmsToDownloadList;

        if (setDataStart == null) {
            setDataStart = progData.setDataList.getSetDataListSave().get(0);
        }

        setAll = true;
        this.addDownloadDto = new AddDownloadDto(progData, setDataStart, filmsToDownloadList, filterResolution);
        initAll();

        init(true);
    }

    public DownloadAddDialogController(ProgData progData, ArrayList<DownloadData> downloadDataArrayList) {
        super(progData.primaryStage,
                downloadDataArrayList.size() > 1 ? ProgConfig.DOWNLOAD_DIALOG_ADD_MORE_SIZE :
                        ProgConfig.DOWNLOAD_DIALOG_ADD_SIZE,
                "Download ändern", true, false, DECO.BORDER_SMALL);

        // bestehende Downloads ändern
        this.progData = progData;
        this.filmsToDownloadList = downloadDataArrayList;

        this.addDownloadDto = new AddDownloadDto(progData, downloadDataArrayList);
        initAll();

        init(true);
    }

    @Override
    public void make() {
        initGui();
        initButton();
        addDownloadDto.updateAct();
    }

    @Override
    public void close() {
        if (setAll) {
            addDownloadDto.chkSetAll.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_SET_ALL);
            addDownloadDto.chkResolutionAll.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_RESOLUTION_ALL);
            addDownloadDto.chkPathAll.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_PATH_ALL);
            addDownloadDto.chkSubTitleAll.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_SUBTITLE_ALL);
            addDownloadDto.chkInfoAll.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_INFO_ALL);
            addDownloadDto.chkStartTimeAll.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_START_TIME_ALL);
        }
        if (addDownloadDto.addNewDownloads) {
            // Vorgabe nur für neue Downloads
            addDownloadDto.rbStartNow.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOW);
            addDownloadDto.rbStartNotYet.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOT);
            addDownloadDto.rbStartAtTime.selectedProperty().unbindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_TIME);
        }
        super.close();
    }

    private void initAll() {
        if (setAll) {
            // "ALLE" setzen
            addDownloadDto.chkSetAll.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_SET_ALL);
            addDownloadDto.chkResolutionAll.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_RESOLUTION_ALL);
            addDownloadDto.chkPathAll.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_PATH_ALL);
            addDownloadDto.chkSubTitleAll.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_SUBTITLE_ALL);
            addDownloadDto.chkInfoAll.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_INFO_ALL);
            addDownloadDto.chkStartTimeAll.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_ADD_START_TIME_ALL);
        } else {
            addDownloadDto.chkStartTimeAll.setSelected(true);
        }
        if (progData.setDataList.getSetDataListSave().size() <= 1) {
            // nur wenns auch eine Auswahl gibt
            addDownloadDto.chkSetAll.setSelected(false);
        }
    }

    private void initGui() {
        if (progData.setDataList.getSetDataListSave().isEmpty() ||
                filmsToDownloadList.isEmpty()) {
            // Satz mit x, war wohl nix
            ok = false;
            quit();
            return;
        }

        DownloadAddDialogGui downloadAddDialogGui = new DownloadAddDialogGui(progData, addDownloadDto, getVBoxCont());
        downloadAddDialogGui.addCont();
        downloadAddDialogGui.init();
        addOkCancelButtons(btnOk, btnCancel);
    }

    private void initButton() {
        addDownloadDto.btnDest.setGraphic(ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        addDownloadDto.btnDest.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        addDownloadDto.btnDest.setOnAction(event -> {
            P2DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, addDownloadDto.cboPath);
            addDownloadDto.initPathName.pathChanged();
        });

        addDownloadDto.btnPropose.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());
        addDownloadDto.btnPropose.setTooltip(new Tooltip("Einen Pfad zum Speichern vorschlagen lassen."));
        addDownloadDto.btnPropose.setOnAction(event ->
                addDownloadDto.initPathName.proposeDestination());

        addDownloadDto.btnClean.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        addDownloadDto.btnClean.setTooltip(new Tooltip("Die Liste der Pfade löschen"));
        addDownloadDto.btnClean.setOnAction(a -> addDownloadDto.initPathName.clearPath());

        addDownloadDto.btnPrev.setOnAction(event -> {
            addDownloadDto.actFilmIsShown.setValue(addDownloadDto.actFilmIsShown.getValue() - 1);
            addDownloadDto.updateAct();
        });
        addDownloadDto.btnNext.setOnAction(event -> {
            addDownloadDto.actFilmIsShown.setValue(addDownloadDto.actFilmIsShown.getValue() + 1);
            addDownloadDto.updateAct();
        });
        btnOk.setOnAction(event -> {
            if (check()) {
                quit();
            }
        });
        btnCancel.setOnAction(event -> {
            ok = false;
            quit();
        });
    }

    private boolean check() {
        ok = false;
        for (AddDownloadData d : addDownloadDto.addDownloadData) {
            if (d.download == null) {
                P2Alert.showErrorAlert("Fehlerhafter Download!", "Fehlerhafter Download!",
                        "Download konnte nicht erstellt werden.");

            } else if (d.download.getDestPath().isEmpty() || d.download.getDestFileName().isEmpty()) {
                P2Alert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                        "Pfad oder Name ist leer.");

            } else {
                if (SetFactory.checkPathWritable(d.download.getDestPath())) {
                    ok = true;
                } else {
                    P2Alert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                            "Pfad ist nicht beschreibbar.");
                }
            }
        }
        return ok;
    }

    private void quit() {
        //damit der Focus nicht aus der Tabelle verloren geht
        addDownloadDto.initPathName.setUsedPaths();
        progData.mtPlayerController.setFocus();

        if (!ok) {
            close();
            return;
        }

        if (addDownloadDto.addNewDownloads) {
            // dann neue Downloads anlegen
            addNewDownloads();
        } else {
            // oder die bestehenden ändern
            changeDownloads();
        }

        // und jetzt noch die Einstellungen speichern
        ProgSave.saveAll();
        progData.downloadGuiController.tableRefresh();
        close();
    }

    private void addNewDownloads() {
        List<DownloadData> list = new ArrayList<>();
        List<DownloadData> listStarts = new ArrayList<>();
        for (AddDownloadData addDownloadData : addDownloadDto.addDownloadData) {
            if (!addDownloadData.download.getStartTime().isEmpty() || addDownloadData.startNow) {
                // wenn Startzeit vorgegeben, oder SOFORT dann starten
                listStarts.add(addDownloadData.download);
                LogMsgFactory.addNewDownloadMsg(addDownloadData.download, true, false);
            } else {
                LogMsgFactory.addNewDownloadMsg(addDownloadData.download, false, false);
            }

            list.add(addDownloadData.download);
        }

        progData.downloadList.addWithNo(list);
        progData.downloadList.startDownloads(listStarts, false);
    }

    private void changeDownloads() {
        List<DownloadData> list = new ArrayList<>();
        for (AddDownloadData addDownloadData : addDownloadDto.addDownloadData) {
            if (addDownloadData.downloadIsRunning()) {
                // schon gestartet
                continue;
            }

            addDownloadData.download.resetDownload(); // Status wieder zurücksetzen
            addDownloadData.downloadOrg.copyToMe(addDownloadData.download);
            if (!addDownloadData.download.getStartTime().isEmpty() || addDownloadData.startNow) {
                // wenn Startzeit vorgegeben, oder SOFORT dann starten
                list.add(addDownloadData.downloadOrg);
                LogMsgFactory.addNewDownloadMsg(addDownloadData.downloadOrg, true, true);
            } else {
                LogMsgFactory.addNewDownloadMsg(addDownloadData.downloadOrg, false, true);
            }
        }

        progData.downloadList.startDownloads(list, false);
    }
}
