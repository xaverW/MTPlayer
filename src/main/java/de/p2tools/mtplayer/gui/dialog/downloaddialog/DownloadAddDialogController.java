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

package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.ProgSave;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.data.setdata.SetFactory;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.P2TimePicker;
import de.p2tools.p2lib.mtfilm.film.FilmFactory;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class DownloadAddDialogController extends PDialogExtra {
    private final HBox hBoxTop = new HBox();
    private final Button btnPrev = new Button("<");
    private final Button btnNext = new Button(">");
    private final Label lblSum = new Label("");
    private final P2TimePicker p2TimePicker = new P2TimePicker(true);
    private final Label lblSet = new Label("Set:");
    private final ComboBox<SetData> cboSetData = new ComboBox<>();
    private final ComboBox<String> cboPath = new ComboBox<>();
    private final Button btnDest = new Button(); // Pfad auswählen
    private final Button btnPropose = new Button(); // Pfad vorschlagen
    private final Button btnClean = new Button(); // Liste der Pfade löschen
    private final Button btnOk = new Button("_Ok");
    private final Button btnCancel = new Button("_Abbrechen");
    private final TextField txtName = new TextField();
    private final RadioButton rbStartNotYet = new RadioButton("noch nicht");
    private final RadioButton rbStartNow = new RadioButton("sofort");
    private final RadioButton rbAtTime = new RadioButton("um: ");
    private final ToggleGroup toggleGroupStart = new ToggleGroup();
    private final CheckBox chkInfo = new CheckBox("Infodatei anlegen: \"Filmname.txt\"");
    private final CheckBox chkSubtitle = new CheckBox("Untertitel speichern: \"Filmname.xxx\"");
    private final RadioButton rbHd = new RadioButton("HD");
    private final RadioButton rbHigh = new RadioButton("Hoch");
    private final RadioButton rbSmall = new RadioButton("Klein");
    private final Label lblFree = new Label("4M noch frei");
    private final Label lblFilmTitle = new Label("ARD: Tatort, ..");

    private final Label lblAll = new Label("Für alle\nändern");
    private final CheckBox chkSetAll = new CheckBox();
    private final CheckBox chkResolutionAll = new CheckBox();
    private final CheckBox chkPathAll = new CheckBox();
    private final CheckBox chkSubTitleAll = new CheckBox();
    private final CheckBox chkInfoAll = new CheckBox();

    private final ProgData progData;
    private SetData setData; // nur für den Start zur init
    private final String filterResolution;
    private boolean ok = false;
    private int actFilmIsShown = 0;
    private final ArrayList<FilmDataMTP> filmsToDownloadList;
    private DownloadAddData[] downloadAddInfosArr;
    private DownloadAddDialogSetData downloadAddDialogSetData;
    private DownloadAddDataPathName downloadAddDataPathName;
    private DownloadAddDialogResolutionButton downloadAddDialogResolutionButton;
    private DownloadAddDialogInfoSubTitle downloadAddDialogInfoSubTitle;

    public DownloadAddDialogController(ProgData progData, ArrayList<FilmDataMTP> filmsToDownloadList,
                                       SetData setData, String filterResolution) {
        super(progData.primaryStage,
                filmsToDownloadList.size() > 1 ? ProgConfig.DOWNLOAD_DIALOG_ADD_MORE_SIZE :
                        ProgConfig.DOWNLOAD_DIALOG_ADD_SIZE,
                "Download anlegen", true, false);

        this.progData = progData;
        this.filmsToDownloadList = filmsToDownloadList;
        this.setData = setData;
        this.filterResolution = filterResolution;

        init(true);
    }

    @Override
    public void make() {
        initGui();

        initButton();
        initDownloadInfoArray();
        initCheckBoxStartDownload();

        initSetData();
        initPathAndName();
        initResolutionButton();
        initInfoSubTitle();

        changeFilmNr();
    }

    private void initGui() {
        if (progData.setDataList.getSetDataListSave().isEmpty() ||
                filmsToDownloadList.isEmpty()) {
            // Satz mit x, war wohl nix
            ok = false;
            quit();
            return;
        }

        DownloadAddDialogGui daGui = new DownloadAddDialogGui(getVBoxCont(), hBoxTop,
                btnPrev, btnNext, lblSum, lblSet,
                cboSetData, cboPath,
                btnDest, btnPropose, btnClean,
                txtName, chkInfo, chkSubtitle,
                rbHd, rbHigh, rbSmall,
                lblFree, lblFilmTitle, lblAll,
                chkSetAll, chkResolutionAll, chkPathAll, chkSubTitleAll, chkInfoAll);
        daGui.addCont();
        daGui.init(progData, filmsToDownloadList.size());

        addOkCancelButtons(btnOk, btnCancel);
        getHboxLeft().getChildren().addAll(new Label("Download starten: "), rbStartNotYet, rbStartNow, rbAtTime, p2TimePicker);

        if (setData == null) {
            setData = progData.setDataList.getSetDataListSave().get(0);
        }
    }

    private void initButton() {
        btnDest.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_FILE_OPEN.getImageView());
        btnDest.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        btnDest.setOnAction(event -> PDirFileChooser.DirChooser(ProgData.getInstance().primaryStage, cboPath));

        btnPropose.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_PROPOSE.getImageView());
        btnPropose.setTooltip(new Tooltip("Einen Pfad zum Speichern vorschlagen lassen."));
        btnPropose.setOnAction(event ->
                DownloadAddDialogFactory.proposeDestination(cboPath, downloadAddInfosArr, actFilmIsShown));

        btnClean.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_CLEAN.getImageView());
        btnClean.setTooltip(new Tooltip("Die Liste der Pfade löschen"));
        btnClean.setOnAction(a -> downloadAddDataPathName.clearPath());

        btnPrev.setOnAction(event -> {
            --actFilmIsShown;
            changeFilmNr();
        });
        btnNext.setOnAction(event -> {
            ++actFilmIsShown;
            changeFilmNr();
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

    private void initDownloadInfoArray() {
        String aktPath = "";
        if (!ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.isEmpty()) {
            // dann den ersten Pfad setzen
            aktPath = ProgConfig.DOWNLOAD_DIALOG_DOWNLOAD_PATH.get(0);
        }

        // DownloadArr anlegen
        downloadAddInfosArr = new DownloadAddData[filmsToDownloadList.size()];
        for (int i = 0; i < filmsToDownloadList.size(); ++i) {
            downloadAddInfosArr[i] = new DownloadAddData(downloadAddInfosArr);
            downloadAddInfosArr[i].setData = setData;
            downloadAddInfosArr[i].film = filmsToDownloadList.get(i);
            downloadAddInfosArr[i].download = new DownloadData(DownloadConstants.SRC_DOWNLOAD, setData, downloadAddInfosArr[i].film,
                    null, "", aktPath, "");

            downloadAddInfosArr[i].path = downloadAddInfosArr[i].download.getDestPath();
            downloadAddInfosArr[i].name = downloadAddInfosArr[i].download.getDestFileName();

            // Dateigröße
            if (i < ProgConst.DOWNLOAD_ADD_DIALOG_MAX_LOOK_FILE_SIZE) {
                downloadAddInfosArr[i].fileSize_HD = downloadAddInfosArr[i].film.isHd() ?
                        FilmFactory.getSizeFromWeb(downloadAddInfosArr[i].film, downloadAddInfosArr[i].film.getUrlForResolution(FilmDataMTP.RESOLUTION_HD)) : "";
                downloadAddInfosArr[i].fileSize_high = FilmFactory.getSizeFromWeb(downloadAddInfosArr[i].film,
                        downloadAddInfosArr[i].film.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
                downloadAddInfosArr[i].fileSize_small = downloadAddInfosArr[i].film.isSmall() ?
                        FilmFactory.getSizeFromWeb(downloadAddInfosArr[i].film, downloadAddInfosArr[i].film.getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL)) : "";

            } else {
                // filesize->wenn die Liste länger als ~5 ist, dauert das viel zu lang
                downloadAddInfosArr[i].fileSize_HD = "";
                downloadAddInfosArr[i].fileSize_high = FilmFactory.getSizeFromWeb(downloadAddInfosArr[i].film,
                        downloadAddInfosArr[i].film.getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
                downloadAddInfosArr[i].fileSize_small = "";
            }

            // Infofile
            downloadAddInfosArr[i].makeInfo = downloadAddInfosArr[i].setData.isInfoFile();

            // Subtitle
            if (downloadAddInfosArr[i].film.getUrlSubtitle().isEmpty()) {
                // dann gibts keinen Subtitle
                downloadAddInfosArr[i].subIsDisabled = true;
                downloadAddInfosArr[i].makeSubTitle = false;
            } else {
                downloadAddInfosArr[i].subIsDisabled = false;
                downloadAddInfosArr[i].makeSubTitle = downloadAddInfosArr[i].setData.isSubtitle();
            }

            // Auflösung: Die Werte passend zum Film setzen
            if ((ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.get().equals(FilmDataMTP.RESOLUTION_HD) ||
                    filterResolution.equals(FilmDataMTP.RESOLUTION_HD) ||
                    downloadAddInfosArr[i].setData.getResolution().equals(FilmDataMTP.RESOLUTION_HD))
                    && downloadAddInfosArr[i].film.isHd()) {

                //Dann wurde im Filter oder Set HD ausgewählt und wird voreingestellt
                downloadAddInfosArr[i].resolution = FilmDataMTP.RESOLUTION_HD;

            } else if ((ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.get().equals(FilmDataMTP.RESOLUTION_SMALL) ||
                    downloadAddInfosArr[i].setData.getResolution().equals(FilmDataMTP.RESOLUTION_SMALL))
                    && downloadAddInfosArr[i].film.isSmall()) {
                downloadAddInfosArr[i].resolution = FilmDataMTP.RESOLUTION_SMALL;

            } else {
                downloadAddInfosArr[i].resolution = FilmDataMTP.RESOLUTION_NORMAL;
            }
        }
    }

    private void initCheckBoxStartDownload() {
        // starten um ...
        p2TimePicker.disableProperty().bind(rbAtTime.selectedProperty().not());
        p2TimePicker.setOnAction(a -> rbAtTime.setSelected(true));
        rbStartNow.setToggleGroup(toggleGroupStart);
        rbStartNotYet.setToggleGroup(toggleGroupStart);
        rbAtTime.setToggleGroup(toggleGroupStart);
        rbStartNow.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOW);
        rbStartNotYet.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOT);
        rbAtTime.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_TIME);
    }

    private void initSetData() {
        downloadAddDialogSetData = new DownloadAddDialogSetData(progData, cboSetData,
                downloadAddInfosArr);
        downloadAddDialogSetData.initCboSetData(setData);

        cboSetData.setOnAction(a -> {
            downloadAddDialogSetData.makeSetDataChange(chkSetAll.isSelected(), actFilmIsShown);
            changeFilmNr();
        });
        chkSetAll.setOnAction(a -> {
            downloadAddDialogSetData.makeSetDataChange(chkSetAll.isSelected(), actFilmIsShown);
            changeFilmNr();
        });
    }

    private void initResolutionButton() {
        downloadAddDialogResolutionButton = new DownloadAddDialogResolutionButton(rbHd, rbHigh, rbSmall,
                chkResolutionAll, downloadAddInfosArr);
        downloadAddDialogResolutionButton.initResolutionButton(actFilmIsShown);

        rbHd.setOnAction(a -> downloadAddDialogResolutionButton.setRes(actFilmIsShown));
        rbHigh.setOnAction(a -> downloadAddDialogResolutionButton.setRes(actFilmIsShown));
        rbSmall.setOnAction(a -> downloadAddDialogResolutionButton.setRes(actFilmIsShown));
        chkResolutionAll.setOnAction(a -> downloadAddDialogResolutionButton.setRes(actFilmIsShown));
    }

    private void initPathAndName() {
        downloadAddDataPathName = new DownloadAddDataPathName(cboPath, txtName,
                chkPathAll, lblFree, downloadAddInfosArr);
        downloadAddDataPathName.initPathAndName(actFilmIsShown);

        cboPath.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null && !oldValue.equals(newValue)) {
                downloadAddDataPathName.pathChanged(actFilmIsShown, cboPath.getValue());
            }
        });
        chkPathAll.setOnAction(a -> downloadAddDataPathName.pathChanged(actFilmIsShown, cboPath.getValue()));

        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            downloadAddDataPathName.nameChanged(actFilmIsShown);
        });
    }

    private void initInfoSubTitle() {
        downloadAddDialogInfoSubTitle = new DownloadAddDialogInfoSubTitle(chkInfo, chkSubtitle,
                chkSubTitleAll, chkInfoAll, downloadAddInfosArr);
        downloadAddDialogInfoSubTitle.initInfoSubTitle(actFilmIsShown);

        chkInfo.setOnAction(a -> downloadAddDialogInfoSubTitle.setInfoSubTitle(actFilmIsShown));
        chkInfoAll.setOnAction(a -> downloadAddDialogInfoSubTitle.setInfoSubTitle(actFilmIsShown));
        chkSubtitle.setOnAction(a -> downloadAddDialogInfoSubTitle.setInfoSubTitle(actFilmIsShown));
        chkSubTitleAll.setOnAction(a -> downloadAddDialogInfoSubTitle.setInfoSubTitle(actFilmIsShown));
    }

    private void changeFilmNr() {
        final int nr = actFilmIsShown + 1;
        lblSum.setText("Film " + nr + " von " + filmsToDownloadList.size() + " Filmen");

        if (actFilmIsShown == 0) {
            btnPrev.setDisable(true);
            btnNext.setDisable(false);
        } else if (actFilmIsShown == filmsToDownloadList.size() - 1) {
            btnPrev.setDisable(false);
            btnNext.setDisable(true);
        } else {
            btnPrev.setDisable(false);
            btnNext.setDisable(false);
        }

        lblFilmTitle.setText(downloadAddInfosArr[actFilmIsShown].film.getChannel() + "  -  " + downloadAddInfosArr[actFilmIsShown].film.getTitle());

        downloadAddDialogSetData.makeActSetData(actFilmIsShown);
        downloadAddDialogResolutionButton.makeActResolutionButtons(actFilmIsShown);
        downloadAddDataPathName.makeActPathName(actFilmIsShown);
        downloadAddDialogInfoSubTitle.makeInfoSubTitle(actFilmIsShown);
    }

    private boolean check() {
        ok = false;
        for (DownloadAddData d : downloadAddInfosArr) {
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
        downloadAddDataPathName.setUsedPaths();
        progData.mtPlayerController.setFocus();

        if (!ok) {
            close();
            return;
        }

        List<DownloadData> list = new ArrayList<>();
        for (DownloadAddData d : downloadAddInfosArr) {
            // jetzt wird mit den angegebenen Pfaden gearbeitet
            DownloadData download = new DownloadData(DownloadConstants.SRC_DOWNLOAD,
                    d.setData,
                    d.film,
                    null,
                    d.name,
                    d.path,
                    d.resolution);
            download.setSizeDownloadFromWeb(DownloadAddDialogFactory.getFilmSize(d));
            download.setInfoFile(d.makeInfo);
            download.setSubtitle(d.makeSubTitle);
            if (rbAtTime.isSelected()) {
                download.setStartTime(p2TimePicker.getTime());
            }

            list.add(download);
        }

        progData.downloadList.addWithNo(list);
        if (rbStartNow.isSelected() || rbAtTime.isSelected()) {
            // und evtl. auch gleich starten
            progData.downloadList.startDownloads(list, false);
        }

        // und jetzt noch die Einstellungen speichern
        ProgSave.saveAll();
        close();
    }
}
