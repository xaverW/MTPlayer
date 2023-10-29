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
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.data.setdata.SetFactory;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;

import java.util.ArrayList;
import java.util.List;

public class DownloadAddDialogController extends PDialogExtra {

    private final ProgData progData;
    private final Button btnOk = new Button("_Ok");
    private final Button btnCancel = new Button("_Abbrechen");
    private boolean ok = false;

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
        this.addDownloadDto = new AddDownloadDto(progData, setDataStart, filmsToDownloadList, filterResolution);
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
        init(true);
    }

    @Override
    public void make() {
        initGui();
        initButton();
        addDownloadDto.updateAct();
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
        addDownloadDto.btnDest.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
        addDownloadDto.btnDest.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        addDownloadDto.btnDest.setOnAction(event -> PDirFileChooser.DirChooser(ProgData.getInstance().primaryStage, addDownloadDto.cboPath));

        addDownloadDto.btnPropose.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_PROPOSE.getImageView());
        addDownloadDto.btnPropose.setTooltip(new Tooltip("Einen Pfad zum Speichern vorschlagen lassen."));
        addDownloadDto.btnPropose.setOnAction(event ->
                addDownloadDto.initPathName.proposeDestination());

        addDownloadDto.btnClean.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_CLEAN.getImageView());
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
                PAlert.showErrorAlert("Fehlerhafter Download!", "Fehlerhafter Download!",
                        "Download konnte nicht erstellt werden.");

            } else if (d.path.isEmpty() || d.name.isEmpty()) {
                PAlert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                        "Pfad oder Name ist leer.");

            } else {
                if (SetFactory.checkPathWritable(d.path)) {
                    ok = true;
                } else {
                    PAlert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
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
        close();
    }

    private void addNewDownloads() {
        List<DownloadData> list = new ArrayList<>();
        List<DownloadData> listStarts = new ArrayList<>();
        for (AddDownloadData addDownloadData : addDownloadDto.addDownloadData) {
            final DownloadData downloadData = addDownloadData.download;
            downloadData.setSetData(addDownloadData.setData, false);
            downloadData.setPathName(addDownloadData.path, addDownloadData.name);
            downloadData.setUrl(downloadData.getFilm().getUrlForResolution(addDownloadData.resolution));
            downloadData.setSizeDownloadFromWeb(DownloadAddDialogFactory.getFilmSize(addDownloadData));
            if (downloadData.getStartTime().isEmpty() && addDownloadData.startNow) {
                listStarts.add(downloadData);
            }
            list.add(downloadData);
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

            final DownloadData downloadData = addDownloadData.download;
            final DownloadData downloadDataOrg = addDownloadData.downloadOrg;
            downloadDataOrg.copyToMe(downloadData);
            downloadDataOrg.setSetData(addDownloadData.setData, false);
            downloadDataOrg.setPathName(addDownloadData.path, addDownloadData.name);
            if (downloadData.getStartTime().isEmpty() && addDownloadData.startNow) {
                list.add(addDownloadData.downloadOrg);
            }
        }
        progData.downloadList.startDownloads(list, false);
    }
}
