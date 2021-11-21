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

package de.p2tools.mtplayer.gui.dialog;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.SetDataList;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadTools;
import de.p2tools.mtplayer.controller.data.film.FilmData;
import de.p2tools.mtplayer.controller.data.film.FilmTools;
import de.p2tools.mtplayer.gui.tools.SetsPrograms;
import de.p2tools.mtplayer.tools.FileNameUtils;
import de.p2tools.mtplayer.tools.SizeTools;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PTimePicker;
import de.p2tools.p2Lib.tools.PStringUtils;
import de.p2tools.p2Lib.tools.PSystemUtils;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.File;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DownloadAddDialogController extends PDialogExtra {

    private VBox vBoxCont;

    private final VBox vBoxAllDownloads = new VBox();
    private final HBox hBoxTop = new HBox();
    private final HBox hBoxAll = new HBox();
    private final HBox hBoxSize = new HBox();

    private final Button btnPrev = new Button("<");
    private final Button btnNext = new Button(">");
    private final Label lblSum = new Label("");
    private final PTimePicker pTimePicker = new PTimePicker();

    private final CheckBox chkAll = new CheckBox("Änderungen auf alle Filme anwenden");
    private final Label lblSet = new Label("Set:");
    private final ComboBox<SetData> cbSet = new ComboBox<>();
    private final ComboBox<String> cbPath = new ComboBox<>();
    private final Button btnDest = new Button("Pfad");
    private final Button btnPropose = new Button("Vorschlag");
    private final Button btnOk = new Button("_Ok");
    private final Button btnCancel = new Button("_Abbrechen");
    private final TextField txtName = new TextField();
    private final RadioButton rbNot = new RadioButton("noch nicht");
    private final RadioButton rbStart = new RadioButton("sofort");
    private final RadioButton rbTime = new RadioButton("um: ");
    private final ToggleGroup toggleGroupStart = new ToggleGroup();

    private final CheckBox cbxInfo = new CheckBox("Infodatei anlegen: \"Filmname.txt\"");
    private final CheckBox cbxSubtitle = new CheckBox("Untertitel speichern: \"Filmname.xxx\"");

    private final RadioButton rbHd = new RadioButton("HD");
    private final RadioButton rbHigh = new RadioButton("Hoch");
    private final RadioButton rbSmall = new RadioButton("Klein");
    private final ToggleGroup toggleGroupSize = new ToggleGroup();

    private final Label lblFree = new Label("4M noch frei");
    private final Label lblFilm = new Label("Film:");
    private final Label lblFilmTitle = new Label("ARD: Tatort, ..");
    private final GridPane gridPane = new GridPane();

    private final String textHd = "HD";
    private final String textHeight = "hohe Auflösung";
    private final String textLow = "niedrige Auflösung";

    private final ProgData progData;
    final private SetDataList setDataList;
    private SetData setData;
    private String filterResolution;
    final String[] storedPath = ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING.get().split("<>");

    private static final String SEPARATOR = File.separator;
    private static final String FORMATTER_ddMMyyyy_str = "yyyyMMdd";
    private static final FastDateFormat FORMATTER_ddMMyyyy = FastDateFormat.getInstance(FORMATTER_ddMMyyyy_str);

    private boolean ok = false;
    private int actFilmIsShown = 0;
    private ArrayList<FilmData> filmsToDownloadList;
    private DownloadAddInfo[] downloadAddInfos;

    public DownloadAddDialogController(ProgData progData, ArrayList<FilmData> filmsToDownloadList, SetData setData, String filterResolution) {
        super(progData.primaryStage,
                filmsToDownloadList.size() > 1 ? ProgConfig.DOWNLOAD_DIALOG_ADD_MORE_SIZE :
                        ProgConfig.DOWNLOAD_DIALOG_ADD_SIZE,
                "Download anlegen", true, false);

        this.progData = progData;
        this.filmsToDownloadList = filmsToDownloadList;
        this.setData = setData;
        this.filterResolution = filterResolution;
        this.setDataList = progData.setDataList.getSetDataListSave();

        vBoxCont = getvBoxCont();
        init(true);
    }

    @Override
    public void make() {
        initCont();

        if (progData.setDataList.getSetDataListSave().isEmpty()) {
            // Satz mit x, war wohl nix
            ok = false;
            quit();
            return;
        }
        if (setData == null) {
            setData = progData.setDataList.getSetDataListSave().get(0);
        }

        if (filmsToDownloadList.size() == 0) {
            // Satz mit x, war wohl nix
            ok = false;
            quit();
            return;

        } else if (filmsToDownloadList.size() == 1) {
            hBoxTop.setVisible(false);
            hBoxTop.setManaged(false);
            hBoxAll.setVisible(false);
            hBoxAll.setManaged(false);
        }

        if (setDataList.size() == 1) {
            // macht dann keinen Sinn
            lblSet.setVisible(false);
            lblSet.setManaged(false);
            cbSet.setVisible(false);
            cbSet.setManaged(false);
        } else {
            cbSet.getItems().addAll(setDataList);
            cbSet.getSelectionModel().select(setData);
            cbSet.setOnAction(a -> makePsetChange());
        }

        initArrays();
        initButton();
        initPathAndName();
        pathNameBase();
        initResolutionButton();
        initCheckBox();

        changeFilmNr();
    }

    private void initCont() {
        vBoxAllDownloads.getStyleClass().add("downloadDialog");
        hBoxSize.getStyleClass().add("downloadDialog");

        lblFilm.setStyle("-fx-font-weight: bold;");
        lblFilmTitle.setStyle("-fx-font-weight: bold;");

        // Top
        hBoxTop.setSpacing(20);
        hBoxTop.setAlignment(Pos.CENTER);
        hBoxTop.setPadding(new Insets(10));
        hBoxTop.getChildren().addAll(btnPrev, lblSum, btnNext);

        hBoxAll.setAlignment(Pos.CENTER);
        hBoxAll.setPadding(new Insets(10));
        hBoxAll.getChildren().add(chkAll);
        vBoxAllDownloads.getChildren().addAll(hBoxTop, hBoxAll);

        // Gridpane
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        VBox.setVgrow(gridPane, Priority.ALWAYS);

        int row = 0;
        gridPane.add(lblFilm, 0, row);
        gridPane.add(lblFilmTitle, 1, row);

        gridPane.add(lblSet, 0, ++row);
        cbSet.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(cbSet, 1, row, 3, 1);

        gridPane.add(new Label("Auflösung:"), 0, ++row);
        hBoxSize.setSpacing(20);
        hBoxSize.setPadding(new Insets(10, 5, 10, 5));
        hBoxSize.getChildren().addAll(rbHd, rbHigh, rbSmall);
        gridPane.add(hBoxSize, 1, row, 3, 1);

        gridPane.add(new Label("Dateiname:"), 0, ++row);
        gridPane.add(txtName, 1, row, 3, 1);

        gridPane.add(new Label("Zielpfad:"), 0, ++row);
        cbPath.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(cbPath, 1, row);
        gridPane.add(btnDest, 2, row);
        gridPane.add(btnPropose, 3, row);

        HBox hBox2 = new HBox();
        hBox2.getChildren().add(lblFree);
        hBox2.setAlignment(Pos.CENTER_RIGHT);
        gridPane.add(hBox2, 1, ++row, 3, 1);

        gridPane.add(cbxSubtitle, 1, ++row);
        gridPane.add(cbxInfo, 1, ++row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());
        vBoxCont.setSpacing(20);
        vBoxCont.getChildren().addAll(vBoxAllDownloads, gridPane);

        addOkCancelButtons(btnOk, btnCancel);
        getHboxLeft().getChildren().addAll(new Label("Download starten: "), rbNot, rbStart, rbTime, pTimePicker);
    }


    private void initArrays() {
        final int anz = filmsToDownloadList.size();
        downloadAddInfos = new DownloadAddInfo[anz];

        String aktPath = "";
        if (storedPath.length > 0) {
            aktPath = storedPath[0];
        }

        for (int i = 0; i < anz; ++i) {
            downloadAddInfos[i] = new DownloadAddInfo(chkAll.selectedProperty(), downloadAddInfos);
            downloadAddInfos[i].psetData = setData;
            downloadAddInfos[i].film = filmsToDownloadList.get(i);
            downloadAddInfos[i].download = new DownloadData(setData, downloadAddInfos[i].film, DownloadConstants.SRC_DOWNLOAD,
                    null, "", aktPath, "");

            downloadAddInfos[i].path = downloadAddInfos[i].download.getDestPath();
            downloadAddInfos[i].name = downloadAddInfos[i].download.getDestFileName();

            if (i < ProgConst.DOWNLOAD_DIALOG_LOAD_MAX_FILESIZE_FROM_WEB) {
                downloadAddInfos[i].fileSize_HD = downloadAddInfos[i].film.isHd() ?
                        FilmTools.getSizeFromWeb(downloadAddInfos[i].film, downloadAddInfos[i].film.getUrlForResolution(FilmData.RESOLUTION_HD)) : "";
                downloadAddInfos[i].fileSize_high = FilmTools.getSizeFromWeb(downloadAddInfos[i].film,
                        downloadAddInfos[i].film.getUrlForResolution(FilmData.RESOLUTION_NORMAL));
                downloadAddInfos[i].fileSize_small = downloadAddInfos[i].film.isSmall() ?
                        FilmTools.getSizeFromWeb(downloadAddInfos[i].film, downloadAddInfos[i].film.getUrlForResolution(FilmData.RESOLUTION_SMALL)) : "";

            } else {
                // filesize->wenn die Liste länger als ~10 ist, dauert das viel zu lang
                downloadAddInfos[i].fileSize_HD = "";
                downloadAddInfos[i].fileSize_high = FilmTools.getSizeFromWeb(downloadAddInfos[i].film,
                        downloadAddInfos[i].film.getUrlForResolution(FilmData.RESOLUTION_NORMAL));
                downloadAddInfos[i].fileSize_small = "";
            }

            downloadAddInfos[i].info = downloadAddInfos[i].psetData.isInfoFile();

            if (downloadAddInfos[i].film.getUrlSubtitle().isEmpty()) {
                // dann gibts keinen Subtitle
                downloadAddInfos[i].subDisable = true;
                downloadAddInfos[i].subtitle = false;
            } else {
                downloadAddInfos[i].subDisable = false;
                downloadAddInfos[i].subtitle = downloadAddInfos[i].psetData.isSubtitle();
            }

            // die Werte passend zum Film setzen: Auflösung
            if ((ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.get().equals(FilmData.RESOLUTION_HD) ||
                    filterResolution.equals(FilmData.RESOLUTION_HD) ||
                    downloadAddInfos[i].psetData.getResolution().equals(FilmData.RESOLUTION_HD))
                    && downloadAddInfos[i].film.isHd()) {

                //Dann wurde im Filter oder Set HD ausgewählt und wird voreingestellt
                downloadAddInfos[i].resolution = FilmData.RESOLUTION_HD;

            } else if ((ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.get().equals(FilmData.RESOLUTION_SMALL) ||
                    downloadAddInfos[i].psetData.getResolution().equals(FilmData.RESOLUTION_SMALL))
                    && downloadAddInfos[i].film.isSmall()) {
                downloadAddInfos[i].resolution = FilmData.RESOLUTION_SMALL;

            } else {
                downloadAddInfos[i].resolution = FilmData.RESOLUTION_NORMAL;
            }
        }
    }

    private void initButton() {
        btnDest.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnDest.setText("");
        btnDest.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        btnDest.setOnAction(event -> getDestination());

        btnPropose.setGraphic(new ProgIcons().ICON_BUTTON_PROPOSE);
        btnPropose.setText("");
        btnPropose.setTooltip(new Tooltip("Einen Pfad zum Speichern vorschlagen lassen."));
        btnPropose.setOnAction(event -> proposeDestination());

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

    private void initPathAndName() {
        // gespeicherte Pfade eintragen
        cbPath.setEditable(true);
        cbPath.getItems().addAll(storedPath);

        if (downloadAddInfos[actFilmIsShown].path.isEmpty()) {
            cbPath.getSelectionModel().selectFirst();
            downloadAddInfos[actFilmIsShown].setPath(cbPath.getSelectionModel().getSelectedItem());
        } else {
            cbPath.getSelectionModel().select(downloadAddInfos[actFilmIsShown].path);
        }

        cbPath.valueProperty().addListener((observable, oldValue, newValue) -> {
            final String s = cbPath.getSelectionModel().getSelectedItem();
            downloadAddInfos[actFilmIsShown].setPath(s);

            calculateAndCheckDiskSpace();
        });

        txtName.setText(downloadAddInfos[actFilmIsShown].name);
        txtName.textProperty().addListener((observable, oldValue, newValue) -> {
            downloadAddInfos[actFilmIsShown].setName(txtName.getText());

            if (!txtName.getText().equals(FileNameUtils.checkFileName(txtName.getText(), false /* pfad */))) {
                txtName.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtName.setStyle("");
            }
        });
        txtName.disableProperty().bind(chkAll.selectedProperty());
    }

    private void pathNameBase() {
        if (cbPath.getItems().isEmpty() ||
                cbPath.getItems().size() == 1 && cbPath.getItems().get(0).isEmpty()) {
            //leer oder und ein leerer Eintrag
            String path;
            cbPath.getItems().clear();
            if (setData.getDestPath().isEmpty()) {
                path = System.getProperty("user.home");
            } else {
                path = setData.getDestPath();
            }
            cbPath.getItems().add(path);
            cbPath.getSelectionModel().select(path);

        } else {
            //min. ein Eintrag und nicht leer
            cbPath.getSelectionModel().select(0);
        }

        if (txtName.getText().isEmpty()) {
            if (setData.getDestName().isEmpty()) {
                txtName.setText(filmsToDownloadList.get(0).getTitle());
            } else {
                txtName.setText(setData.getDestName());
            }
        }
    }

    private void initResolutionButton() {
        rbHd.setToggleGroup(toggleGroupSize);
        rbHigh.setToggleGroup(toggleGroupSize);
        rbSmall.setToggleGroup(toggleGroupSize);

        // und jetzt für den aktuellen Film das GUI setzen
        makeResolutionButtons();

        rbHd.setOnAction(a -> {
            downloadAddInfos[actFilmIsShown].setResolution(FilmData.RESOLUTION_HD);
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmData.RESOLUTION_HD);
        });
        rbHigh.setOnAction(a -> {
            downloadAddInfos[actFilmIsShown].setResolution(FilmData.RESOLUTION_NORMAL);
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmData.RESOLUTION_NORMAL);
        });
        rbSmall.setOnAction(a -> {
            downloadAddInfos[actFilmIsShown].setResolution(FilmData.RESOLUTION_SMALL);
            ProgConfig.DOWNLOAD_DIALOG_HD_HEIGHT_LOW.setValue(FilmData.RESOLUTION_SMALL);
        });
    }

    private void initCheckBox() {
        // und jetzt noch die Listener anhängen
        pTimePicker.setOnAction(a -> rbTime.setSelected(true));
        rbStart.setToggleGroup(toggleGroupStart);
        rbNot.setToggleGroup(toggleGroupStart);
        rbTime.setToggleGroup(toggleGroupStart);
        rbStart.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOW);
        rbNot.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_NOT);
        rbTime.selectedProperty().bindBidirectional(ProgConfig.DOWNLOAD_DIALOG_START_DOWNLOAD_TIME);
        cbxSubtitle.setOnAction(event -> downloadAddInfos[actFilmIsShown].setSubtitle(cbxSubtitle.isSelected()));
        cbxInfo.setOnAction(event -> downloadAddInfos[actFilmIsShown].setInfo(cbxInfo.isSelected()));
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

        lblFilmTitle.setText(downloadAddInfos[actFilmIsShown].film.getChannel() + "  -  " + downloadAddInfos[actFilmIsShown].film.getTitle());
        makeResolutionButtons();
        makeCheckBox();
        makeFilmName();
        pathNameBase();
        calculateAndCheckDiskSpace();
    }


    private void makeResolutionButtons() {
        rbHd.setDisable(!downloadAddInfos[actFilmIsShown].film.isHd());
        rbSmall.setDisable(!downloadAddInfos[actFilmIsShown].film.isSmall());

        switch (downloadAddInfos[actFilmIsShown].resolution) {
            case FilmData.RESOLUTION_HD:
                rbHd.setSelected(true);
                break;
            case FilmData.RESOLUTION_SMALL:
                rbSmall.setSelected(true);
                break;
            case FilmData.RESOLUTION_NORMAL:
            default:
                rbHigh.setSelected(true);
                break;
        }

        if (!rbHd.isDisable() && !downloadAddInfos[actFilmIsShown].fileSize_HD.isEmpty()) {
            rbHd.setText(textHd + "   [ " + downloadAddInfos[actFilmIsShown].fileSize_HD + " MB ]");
        } else {
            rbHd.setText(textHd);
        }

        if (!downloadAddInfos[actFilmIsShown].fileSize_high.isEmpty()) {
            rbHigh.setText(textHeight + "   [ " + downloadAddInfos[actFilmIsShown].fileSize_high + " MB ]");
        } else {
            rbHigh.setText(textHeight);
        }

        if (!rbSmall.isDisable() && !downloadAddInfos[actFilmIsShown].fileSize_small.isEmpty()) {
            rbSmall.setText(textLow + "   [ " + downloadAddInfos[actFilmIsShown].fileSize_small + " MB ]");
        } else {
            rbSmall.setText(textLow);
        }
    }

    private void makeCheckBox() {
        cbxInfo.setSelected(downloadAddInfos[actFilmIsShown].info);
        cbxSubtitle.setDisable(downloadAddInfos[actFilmIsShown].subDisable);
        cbxSubtitle.setSelected(downloadAddInfos[actFilmIsShown].subtitle);
    }

    private void makeFilmName() {
        txtName.setText(downloadAddInfos[actFilmIsShown].name);
        cbPath.getSelectionModel().select(downloadAddInfos[actFilmIsShown].path);
    }

    private void makePsetChange() {
        if (chkAll.isSelected()) {
            Arrays.stream(downloadAddInfos).forEach(d -> makePsetChange(d));
        } else {
            makePsetChange(downloadAddInfos[actFilmIsShown]);
        }

        changeFilmNr();
    }

    private void makePsetChange(DownloadAddInfo downloadAddInfo) {
        SetData psetData = cbSet.getSelectionModel().getSelectedItem();

        downloadAddInfo.psetData = psetData;
        downloadAddInfo.download = new DownloadData(psetData, downloadAddInfo.film, DownloadConstants.SRC_DOWNLOAD, null, "", "", FilmData.RESOLUTION_NORMAL);
        downloadAddInfo.path = downloadAddInfo.download.getDestPath();
        downloadAddInfo.name = downloadAddInfo.download.getDestFileName();
        downloadAddInfo.info = downloadAddInfo.psetData.isInfoFile();

        if (downloadAddInfo.film.getUrlSubtitle().isEmpty()) {
            // dann gibts keinen Subtitle
            downloadAddInfo.subDisable = true;
            downloadAddInfo.subtitle = false;
        } else {
            downloadAddInfo.subDisable = false;
            downloadAddInfo.subtitle = downloadAddInfo.psetData.isSubtitle();
        }

        // die Werte passend zum Film setzen
        if (downloadAddInfo.psetData.getResolution().equals(FilmData.RESOLUTION_HD)
                && downloadAddInfo.film.isHd()) {
            downloadAddInfo.resolution = FilmData.RESOLUTION_HD;

        } else if (downloadAddInfo.psetData.getResolution().equals(FilmData.RESOLUTION_SMALL)
                && downloadAddInfo.film.isSmall()) {
            downloadAddInfo.resolution = FilmData.RESOLUTION_SMALL;

        } else {
            downloadAddInfo.resolution = FilmData.RESOLUTION_NORMAL;
        }
    }


    private boolean check() {
        ok = false;
        for (DownloadAddInfo d : downloadAddInfos) {
            if (d.download == null) {
                PAlert.showErrorAlert("Fehlerhafter Download!", "Fehlerhafter Download!",
                        "Download konnte nicht erstellt werden.");

            } else if (d.path.isEmpty() || d.name.isEmpty()) {
                PAlert.showErrorAlert("Fehlerhafter Pfad/Name!", "Fehlerhafter Pfad/Name!",
                        "Pfad oder Name ist leer.");

            } else {
                if (!d.path.substring(d.path.length() - 1).equals(File.separator)) {
                    d.path += File.separator;
                }
                if (SetsPrograms.checkPathWritable(d.path)) {
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
        //damit der Focus nicht aus der Tabelle verlorengeht
        progData.mtPlayerController.setFocus();

        if (!ok) {
            close();
            return;
        }

        saveComboPath(cbPath);
        List<DownloadData> list = new ArrayList<>();
        for (DownloadAddInfo d : downloadAddInfos) {
            // jetzt wird mit den angegebenen Pfaden gearbeitet
            DownloadData download = new DownloadData(d.psetData,
                    d.film,
                    DownloadConstants.SRC_DOWNLOAD,
                    null,
                    d.name,
                    d.path,
                    d.resolution);
            download.setSizeDownloadFromWeb(getFilmSize(d));
            download.setInfoFile(d.info);
            download.setSubtitle(d.subtitle);
            if (rbTime.isSelected()) {
                download.setStartTime(pTimePicker.getTime());
            }

            list.add(download);
        }

        progData.downloadList.addWithNr(list);
        if (rbStart.isSelected() || rbTime.isSelected()) {
            // und evtl. auch gleich starten
            progData.downloadList.startDownloads(list);
        }

        close();
    }

    private String getFilmSize(DownloadAddInfo downloadAddInfo) {
        switch (downloadAddInfo.resolution) {
            case FilmData.RESOLUTION_HD:
                return downloadAddInfo.fileSize_HD;

            case FilmData.RESOLUTION_SMALL:
                return downloadAddInfo.fileSize_small;

            case FilmData.RESOLUTION_NORMAL:
            default:
                return downloadAddInfo.fileSize_high;
        }
    }


    private void saveComboPath(ComboBox<String> jcb) {
        final ArrayList<String> path1 = new ArrayList<>(jcb.getItems());

        // und die eingestellten Downloadpafade an den Anfang zu stellen
        for (DownloadAddInfo d : downloadAddInfos) {
            if (path1.contains(d.path)) {
                path1.remove(d.path);
            }
            path1.add(0, d.path);
        }

        // jetzt alle gesammelten Pfade speichern
        final ArrayList<String> path2 = new ArrayList<>();
        for (String s1 : path1) {
            // um doppelte auszusortieren
            final String s2 = StringUtils.removeEnd(s1, SEPARATOR);
            if (!path2.contains(s1) && !path2.contains(s2)) {
                path2.add(s2);
            }

            if (path2.size() > ProgConst.MAX_DEST_PATH_IN_DIALOG_DOWNLOAD) {
                // die Anzahl der Einträge begrenzen
                break;
            }
        }

        String savePath = PStringUtils.appendList(path2, "<>", true, true);
        ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING.setValue(savePath);
    }

    /**
     * Get the free disk space for a selected path.
     *
     * @return Free disk space in bytes.
     */
    private long getFreeDiskSpace(final String strPath) {
        long usableSpace = 0;
        if (!strPath.isEmpty()) {
            try {
                Path path = Paths.get(strPath);
                if (!Files.exists(path)) {
                    path = path.getParent();
                }
                final FileStore fileStore = Files.getFileStore(path);
                usableSpace = fileStore.getUsableSpace();
            } catch (final Exception ignore) {
            }
        }
        return usableSpace;
    }

    /**
     * Calculate free disk space on volume and checkIfExists if the movies can be safely downloaded.
     */
    private void calculateAndCheckDiskSpace() {
        String path = cbPath.getSelectionModel().getSelectedItem();
        if (path == null || path.isEmpty()) {
            return;
        }
        try {
            String noSize = "";
            String sizeFree = "";

            long usableSpace = getFreeDiskSpace(path);
            if (usableSpace > 0) {
                sizeFree = SizeTools.humanReadableByteCount(usableSpace, true);
            }

            // jetzt noch prüfen, obs auf die Platte passt
            usableSpace /= 1_000_000;
            if (usableSpace <= 0) {
                lblFree.setText("");

            } else {
                int size;
                if (!downloadAddInfos[actFilmIsShown].fileSize_HD.isEmpty()) {
                    size = Integer.parseInt(downloadAddInfos[actFilmIsShown].fileSize_HD);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für HD";

                    }
                }
                if (!downloadAddInfos[actFilmIsShown].fileSize_high.isEmpty()) {
                    size = Integer.parseInt(downloadAddInfos[actFilmIsShown].fileSize_high);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für \"hoch\"";
                    }
                }
                if (!downloadAddInfos[actFilmIsShown].fileSize_small.isEmpty()) {
                    size = Integer.parseInt(downloadAddInfos[actFilmIsShown].fileSize_small);
                    if (size > usableSpace) {
                        noSize = ", nicht genug für \"klein\"";
                    }
                }

                if (noSize.isEmpty()) {
                    lblFree.setText(" [ noch frei: " + sizeFree + " ]");
                } else {
                    lblFree.setText(" [ noch frei: " + sizeFree + noSize + " ]");
                }
            }
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getDestination() {
        PDirFileChooser.DirChooser(ProgData.getInstance().primaryStage, cbPath);
    }

    private void proposeDestination() {
        String stdPath, actPath;
        actPath = cbPath.getSelectionModel().getSelectedItem();

        if (setData.getDestPath().isEmpty()) {
            stdPath = PSystemUtils.getStandardDownloadPath();
        } else {
            stdPath = setData.getDestPath();
        }

        actPath = getNextName(stdPath, actPath, downloadAddInfos[actFilmIsShown].download.getTheme());
        if (!cbPath.getItems().contains(actPath)) {
            cbPath.getItems().add(actPath);
        }
        cbPath.getSelectionModel().select(actPath);
    }


    private String getNextName(String stdPath, String actDownPath, String theme) {
        String ret = actDownPath;

        theme = DownloadTools.replaceEmptyFileName(theme,
                false /* pfad */,
                ProgConfig.SYSTEM_USE_REPLACETABLE.getValue(),
                ProgConfig.SYSTEM_ONLY_ASCII.getValue());

        if (actDownPath.endsWith(SEPARATOR)) {
            ret = actDownPath.substring(0, actDownPath.length() - SEPARATOR.length());
        }

        try {
            final String date = FORMATTER_ddMMyyyy.format(new Date());
            final boolean isDate = getTime(ret, FORMATTER_ddMMyyyy);
            final boolean isTheme = ret.endsWith(theme) && !theme.isEmpty();
            final boolean isStandard = actDownPath.equals(stdPath);

            if (isStandard) {
                Path path = Paths.get(stdPath, (theme.isEmpty() ? date : theme));
                ret = path.toString();

            } else if (isTheme) {
                Path path = Paths.get(stdPath, date);
                ret = path.toString();

            } else if (isDate) {
                Path path = Paths.get(stdPath);
                ret = path.toString();

            } else {
                Path path = Paths.get(stdPath);
                ret = path.toString();

            }
        } catch (Exception ex) {
            PLog.errorLog(978451203, ex);
            ret = stdPath;
        }
        return ret;
    }

    private boolean getTime(String name, FastDateFormat format) {
        String ret = "";
        Date d;
        try {
            ret = name.substring(name.lastIndexOf(SEPARATOR) + 1);
            d = new Date(format.parse(ret).getTime());
        } catch (Exception ignore) {
            d = null;
        }

        if (d != null && format.getPattern().length() == ret.length()) {
            return true;
        }
        return false;
    }
}
