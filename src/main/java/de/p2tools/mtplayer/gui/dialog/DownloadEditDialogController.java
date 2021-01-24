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

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.MTColor;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.ProgramData;
import de.p2tools.mtplayer.controller.data.SetData;
import de.p2tools.mtplayer.controller.data.download.Download;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadTools;
import de.p2tools.mtplayer.controller.data.download.DownloadXml;
import de.p2tools.mtplayer.controller.data.film.Film;
import de.p2tools.mtplayer.controller.data.film.FilmTools;
import de.p2tools.mtplayer.tools.FileNameUtils;
import de.p2tools.mtplayer.tools.file.GetFile;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.File;

public class DownloadEditDialogController extends PDialogExtra {

    private Button btnOk = new Button("_Ok");
    private Button btnCancel = new Button("_Abbrechen");

    private boolean ok = false;

    private final GridPane gridPane = new GridPane();

    private final Label[] lbl = new Label[DownloadXml.MAX_ELEM];
    private final Label[] lblCont = new Label[DownloadXml.MAX_ELEM];
    private final CheckBox[] cbx = new CheckBox[DownloadXml.MAX_ELEM];
    private final TextField[] txt = new TextField[DownloadXml.MAX_ELEM];
    private final RadioButton rbHd = new RadioButton("HD");
    private final RadioButton rbHigh = new RadioButton("Hoch");
    private final RadioButton rbSmall = new RadioButton("Klein");
    private final ComboBox<String> cbPath = new ComboBox<>();
    private final Button btnPath = new Button();
    private final Label lblSizeFree = new Label();

    private final ToggleGroup group = new ToggleGroup();
    private String fileSize_HD = "";
    private String fileSize_high = "";
    private String fileSize_small = "";
    private String resolution = Film.RESOLUTION_NORMAL;

    private final Download download;
    private final boolean isStarted;
    private final String orgProgArray;
    private final String orgPath;
    private final ProgData progData;
    private final SetData setData;

    public DownloadEditDialogController(ProgData progData, Download download, boolean isStarted) {
        super(progData.primaryStage,
                ProgConfig.DOWNLOAD_DIALOG_EDIT_SIZE.getStringProperty(),
                "Download ändern", true, false);

        this.progData = progData;
        this.download = download;
        this.isStarted = isStarted;

        orgProgArray = download.arr[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY];
        orgPath = download.getDestPathFile();

        addOkCancelButtons(btnOk, btnCancel);

        getvBoxCont().getChildren().add(gridPane);

        setData = download.getSetData();
        if (setData == null) {
            Platform.runLater(() -> new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO));
        } else {
            init(true);
        }

    }


    public void make() {
        btnPath.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnPath.setText("");
        btnPath.setTooltip(new Tooltip("Einen Pfad zum Speichern auswählen."));
        btnPath.setOnAction(event -> getDestination());

        rbHd.setToggleGroup(group);
        rbHd.setOnAction(a -> changeRes());
        rbHigh.setToggleGroup(group);
        rbHigh.setOnAction(a -> changeRes());
        rbSmall.setToggleGroup(group);
        rbSmall.setOnAction(a -> changeRes());

        initButton();
        initResolutionButtons();
        initGridPane();
    }

    public boolean isOk() {
        return ok;
    }

    private void quit() {
        if (!ok) {
            close();
            return;
        }

        close();
    }

    private boolean downloadDeleteFile(Download dataDownload) {
        boolean ret = false;
        try {
            final File file = new File(dataDownload.getDestPathFile());

            if (!file.exists()) {
                // gibt nichts zu löschen
                return true;
            }


            if (!PAlert.showAlertOkCancel("Film Löschen?", "Auflösung wurde geändert",
                    "Die Auflösung wurde geändert, der Film kann nicht weitergeführt werden." + P2LibConst.LINE_SEPARATOR +
                            "Datei muss zuerst gelöscht werden.")) {
                // user will nicht
                return false;
            }

            // und jetzt die Datei löschen
            PLog.sysLog(new String[]{"Datei löschen: ", file.getAbsolutePath()});
            if (!file.delete()) {
                throw new Exception();
            }
            ret = true;

        } catch (final Exception ex) {
            PAlert.showErrorAlert("Film löschen",
                    "Konnte die Datei nicht löschen!",
                    "Fehler beim löschen: " + dataDownload.getDestPathFile());
            PLog.errorLog(812036789, "Fehler beim löschen: " + dataDownload.arr[DownloadXml.DOWNLOAD_DEST_PATH_FILE_NAME]);
        }

        return ret;
    }

    private boolean check() {
        download.setPathName(cbPath.getSelectionModel().getSelectedItem(),
                txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].getText());

        if ((rbHd.isSelected() && !resolution.equals(Film.RESOLUTION_HD))
                || (rbSmall.isSelected() && !resolution.equals(Film.RESOLUTION_SMALL))
                || (rbHigh.isSelected() && !resolution.equals(Film.RESOLUTION_NORMAL))) {
            // dann wurde die Auflösung geändert -> Film kann nicht weitergeführt werden
            ok = downloadDeleteFile(download);
        } else {
            ok = true;
        }
        return ok;
    }

    private void getDestination() {
        PDirFileChooser.DirChooser(getStage(), cbPath);
    }

    private void changeRes() {
        // RadioButton sind nur enabled wenn "datenDownload.film" vorhanden
        final String res;
        if (rbHd.isSelected()) {
            res = Film.RESOLUTION_HD;
        } else if (rbSmall.isSelected()) {
            res = Film.RESOLUTION_SMALL;
        } else {
            res = Film.RESOLUTION_NORMAL;
        }
        download.setUrl(download.getFilm().getUrlForResolution(res));
        download.setUrlRtmp(download.getFilm().getUrlFlvstreamerForResolution(res));
        txt[DownloadXml.DOWNLOAD_URL].setText(download.getUrl());
        txt[DownloadXml.DOWNLOAD_URL_RTMP].setText(download.getUrlRtmp());

        final String size;
        if (rbHd.isSelected()) {
            size = fileSize_HD;
        } else if (rbSmall.isSelected()) {
            size = fileSize_small;
        } else {
            size = fileSize_high;
        }

        resetDownloadCallForProgramm();
        download.setSizeDownloadFromWeb(size);
    }


    private void initButton() {
//        btnOk.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
        btnOk.setOnAction(event -> {
            if (check()) {
                quit();
            }
        });
//        btnCancel.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
        btnCancel.setOnAction(event -> {
            ok = false;
            quit();
        });

    }

    private void initResolutionButtons() {
        rbHd.setDisable(true);
        rbHigh.setDisable(true);
        rbSmall.setDisable(true);

        if (download.getFilm() != null) {

            rbHigh.setDisable(isStarted);
            rbHigh.setSelected(download.getUrl().equals(download.getFilm().getUrlForResolution(Film.RESOLUTION_NORMAL)));
            fileSize_high = FilmTools.getSizeFromWeb(download.getFilm(),
                    download.getFilm().getUrlForResolution(Film.RESOLUTION_NORMAL));
            if (!fileSize_high.isEmpty()) {
                rbHigh.setText(rbHigh.getText() + P2LibConst.LINE_SEPARATOR + "[ " + fileSize_high + " MB ]");
                rbHigh.setTextAlignment(TextAlignment.CENTER);
            }

            if (download.getFilm().isHd()) {
                rbHd.setDisable(isStarted);
                rbHd.setSelected(download.getUrl().equals(download.getFilm().getUrlForResolution(Film.RESOLUTION_HD)));
                fileSize_HD = FilmTools.getSizeFromWeb(download.getFilm(),
                        download.getFilm().getUrlForResolution(Film.RESOLUTION_HD));
                if (!fileSize_HD.isEmpty()) {
                    rbHd.setText(rbHd.getText() + P2LibConst.LINE_SEPARATOR + "[ " + fileSize_HD + " MB ]");
                    rbHd.setTextAlignment(TextAlignment.CENTER);
                }
            }

            if (download.getFilm().isSmall()) {
                rbSmall.setDisable(isStarted);
                rbSmall.setSelected(download.getUrl().equals(download.getFilm().getUrlForResolution(Film.RESOLUTION_SMALL)));
                fileSize_small = FilmTools.getSizeFromWeb(download.getFilm(),
                        download.getFilm().getUrlForResolution(Film.RESOLUTION_SMALL));
                if (!fileSize_small.isEmpty()) {
                    rbSmall.setText(rbSmall.getText() + P2LibConst.LINE_SEPARATOR + "[ " + fileSize_small + " MB ]");
                    rbSmall.setTextAlignment(TextAlignment.CENTER);
                }
            }

        }
        if (rbHd.isSelected()) {
            resolution = Film.RESOLUTION_HD;
            if (!fileSize_HD.isEmpty()) {
                // ist wahrscheinlich leer
                download.setSizeDownloadFromWeb(fileSize_HD);
            }
        } else if (rbSmall.isSelected()) {
            resolution = Film.RESOLUTION_SMALL;
            if (!fileSize_small.isEmpty()) {
                // ist wahrscheinlich leer
                download.setSizeDownloadFromWeb(fileSize_small);
            }
        } else {
            resolution = Film.RESOLUTION_NORMAL;
            if (!fileSize_high.isEmpty()) {
                // dann den auch noch
                download.setSizeDownloadFromWeb(fileSize_high);
            }
        }
    }

    private int initProgramArray(int row) {
        lbl[DownloadXml.DOWNLOAD_PROGRAM_CALL].setText(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_PROGRAM_CALL]);
        lbl[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].setText(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_PROGRAM_CALL]);

        txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].setText(download.getProgramCallArray());
        txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].setText(download.getProgramCall());

        if (download.getType().equals(DownloadConstants.TYPE_PROGRAM)) {
            // nur bei Downloads über ein Programm

            gridPane.add(lbl[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY], 0, row);

            Tooltip t = new Tooltip();
            t.setWrapText(true);
            t.setPrefWidth(800);
            t.textProperty().bind(txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].textProperty());
            txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].setTooltip(t);
            txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].setEditable(!isStarted);

            t = new Tooltip();
            t.setWrapText(true);
            t.setPrefWidth(800);
            t.textProperty().bind(txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].textProperty());
            txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].setTooltip(t);
            txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].setEditable(!isStarted);

            if (download.getProgramCallArray().isEmpty()) {
                // Aufruf über Array ist leer -> Win, Mac
                txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].textProperty().addListener((observable, oldValue, newValue) -> {
                    download.setProgramCall(newValue.trim());
                });
                gridPane.add(txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY], 1, row);

            } else {
                // dann ist ein Array vorhanden -> Linux
                txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].textProperty().addListener((observable, oldValue, newValue) -> {
                    download.setProgramCallArray(newValue.trim());
                    download.setProgramCall(ProgramData.makeProgAufrufArray(download.getProgramCallArray()));
                    txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].setText(download.getProgramCall());
                });

                final Button btnReset = new Button("");
                btnReset.setTooltip(new Tooltip("Reset"));
                btnReset.setGraphic(new ProgIcons().ICON_BUTTON_RESET);
                btnReset.setOnAction(e -> txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].setText(orgProgArray));

                final Button btnHelp = PButton.helpButton("Den Programmaufruf ändern",
                        new GetFile().getHelpSearch(GetFile.PATH_HELPTEXT_EDIT_DOWNLOAD_PROG));

                VBox vBox = new VBox(5);
                HBox hBoxArray1 = new HBox(10);
                HBox.setHgrow(txt[DownloadXml.DOWNLOAD_PROGRAM_CALL], Priority.ALWAYS);
                hBoxArray1.getChildren().addAll(btnHelp, txt[DownloadXml.DOWNLOAD_PROGRAM_CALL]);

                HBox hBoxArray2 = new HBox(10);
                HBox.setHgrow(txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY], Priority.ALWAYS);
                hBoxArray2.getChildren().addAll(btnReset, txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY]);

                vBox.getChildren().addAll(hBoxArray1, hBoxArray2);

                gridPane.add(vBox, 1, row, 3, 1);
            }
            ++row;
        }
        return row;
    }

    private int initPath(int row) {
        txt[DownloadXml.DOWNLOAD_DEST_PATH].setEditable(!isStarted); // für die LabelFarbe
        txt[DownloadXml.DOWNLOAD_DEST_PATH].setText(download.getDestPath());
        gridPane.add(lbl[DownloadXml.DOWNLOAD_DEST_PATH], 0, row);

        VBox vBox = new VBox(5);
        HBox hBoxPath = new HBox(10);

        HBox.setHgrow(cbPath, Priority.ALWAYS); // Kompromiss
        cbPath.setMaxWidth(Double.MAX_VALUE);

        hBoxPath.getChildren().addAll(cbPath, btnPath);
        vBox.getChildren().addAll(hBoxPath, lblSizeFree);
        gridPane.add(vBox, 1, row, 3, 1);
        ++row;

        // gespeicherte Pfade eintragen
        final String[] p = ProgConfig.DOWNLOAD_DIALOG_PATH_SAVING.get().split("<>");
        cbPath.getItems().addAll(p);

        if (download.getDestPath().isEmpty()) {
            cbPath.getSelectionModel().selectFirst();
        } else {
            cbPath.getSelectionModel().select(download.getDestPath());
        }

        cbPath.valueProperty().addListener((observable, oldValue, newValue) -> {
            final String s = cbPath.getSelectionModel().getSelectedItem();
            resetDownloadCallForProgramm();
            DownloadTools.calculateAndCheckDiskSpace(download, s, lblSizeFree);
        });

        DownloadTools.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);
        return row;
    }

    private int initName(int row) {

        txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].setEditable(!isStarted);
        txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].setText(download.getDestFileName());
        gridPane.add(lbl[DownloadXml.DOWNLOAD_DEST_FILE_NAME], 0, row);
        gridPane.add(txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME], 1, row, 3, 1);
        ++row;

        txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].textProperty().addListener((observable, oldValue, newValue) -> {

            if (!txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].getText().equals(
                    FileNameUtils.checkFileName(txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].getText(), false /* pfad */))) {
                txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].setStyle(MTColor.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].setStyle("");
            }

            resetDownloadCallForProgramm();
        });

        return row;
    }

    private void initGridPane() {
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        gridPane.setHgap(5);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(10));

        int row = 0;
        for (int i = 0; i < DownloadXml.MAX_ELEM; ++i) {
            lbl[i] = new Label(DownloadXml.COLUMN_NAMES[i] + ":");
            lbl[i].setPadding(new Insets(2, 0, 2, 0));
            lblCont[i] = new Label("");

            txt[i] = new TextField("");
            txt[i].setEditable(false);
            txt[i].setMaxWidth(Double.MAX_VALUE);
            txt[i].setPrefWidth(Control.USE_COMPUTED_SIZE);

            cbx[i] = new CheckBox();
            cbx[i].setDisable(true);
        }
        setGrid();
    }

    private void addToGrid(int i, boolean second, int row, StringBinding stringProperty) {
        lblCont[i].textProperty().bind(stringProperty);
        gridPane.add(lbl[i], second ? 2 : 0, row);
        gridPane.add(lblCont[i], second ? 3 : 1, row);
    }

    private void addToGrid(int i, boolean second, int row, StringProperty stringProperty, boolean expand) {
        lblCont[i].textProperty().bind(stringProperty);
        gridPane.add(lbl[i], second ? 2 : 0, row);
        if (expand) {
            gridPane.add(lblCont[i], second ? 3 : 1, row, 3, 1);
        } else {
            gridPane.add(lblCont[i], second ? 3 : 1, row);
        }
    }

    private int setGrid() {
//         DownloadXml.DOWNLOAD_NR:
//         DownloadXml.DOWNLOAD_SOURCE:
//         DownloadXml.DOWNLOAD_REF:
//         DownloadXml.DOWNLOAD_PLACED_BACK:
//         DownloadXml.DOWNLOAD_TYPE:
//         DownloadXml.DOWNLOAD_HISTORY_URL:
//         DownloadXml.DOWNLOAD_BANDWIDTH:
//         DownloadXml.DOWNLOAD_INTERRUPTED:
//         DownloadXml.DOWNLOAD_URL_RTMP:
//         DownloadXml.DOWNLOAD_URL_SUBTITLE:
//         DownloadXml.DOWNLOAD_SPOTLIGHT:
//         DownloadXml.DOWNLOAD_BUTTON2:
//         DownloadXml.DOWNLOAD_PROGRAM_CALL:
//

        int row = 0;

        if (!download.getAboName().isEmpty()) {
            addToGrid(DownloadXml.DOWNLOAD_ABO, false, row++, download.aboNameProperty(), false);
        }

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_SENDER, false, row, download.channelProperty(), false);
        addToGrid(DownloadXml.DOWNLOAD_THEME, true, row++, download.themeProperty(), false);

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_TITLE, false, row++, download.titleProperty(), false);

        //---------------------------------
        lblCont[DownloadXml.DOWNLOAD_PROGRESS].setText(DownloadConstants.getTextProgress(
                download.getProgramDownloadmanager(),
                download.getState(),
                download.getProgress()));

        gridPane.add(lbl[DownloadXml.DOWNLOAD_PROGRESS], 0, row);
        gridPane.add(lblCont[DownloadXml.DOWNLOAD_PROGRESS], 1, row);

        if (download.isStateStartedRun() &&
                download.getStart().getTimeLeftSeconds() > 0) {
            lblCont[DownloadXml.DOWNLOAD_REMAINING_TIME].setText(DownloadConstants.getTimeLeft(download.getStart().getTimeLeftSeconds()));
        }
        gridPane.add(lbl[DownloadXml.DOWNLOAD_REMAINING_TIME], 2, row);
        gridPane.add(lblCont[DownloadXml.DOWNLOAD_REMAINING_TIME], 3, row);
        ++row;

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_SIZE, false, row++, download.downloadSizeProperty().asString());

        //---------------------------------
        lblCont[DownloadXml.DOWNLOAD_DATE].setText(download.getFilmDate().toString()); //todo bind
        gridPane.add(lbl[DownloadXml.DOWNLOAD_DATE], 0, row);
        gridPane.add(lblCont[DownloadXml.DOWNLOAD_DATE], 1, row);

        if (download.isHd()) {
            ImageView imageView = new ImageView();
            imageView.setImage(new ProgIcons().ICON_DIALOG_EIN_SW);
            gridPane.add(imageView, 3, row);
        }
        gridPane.add(lbl[DownloadXml.DOWNLOAD_HD], 2, row);
        ++row;

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_TIME, false, row, download.timeProperty(), false);

        if (download.isUt()) {
            ImageView imageView = new ImageView();
            imageView.setImage(new ProgIcons().ICON_DIALOG_EIN_SW);
            gridPane.add(imageView, 3, row);
        }
        gridPane.add(lbl[DownloadXml.DOWNLOAD_UT], 2, row);
        ++row;

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_DURATION, false, row, download.durationMinuteProperty().asString());

        if (download.getGeoBlocked()) {
            ImageView imageView = new ImageView();
            imageView.setImage(ProgConfig.SYSTEM_DARK_THEME.getBool() ? new ProgIcons().ICON_DIALOG_EIN_SW : new ProgIcons().ICON_DIALOG_EIN);
            gridPane.add(imageView, 3, row);
        }
        gridPane.add(lbl[DownloadXml.DOWNLOAD_GEO], 2, row);
        ++row;

        //---------------------------------
        cbx[DownloadXml.DOWNLOAD_PROGRAM_RESTART].setSelected(download.getProgramRestart());
        if (!download.getProgramDownloadmanager() && !isStarted) {
            cbx[DownloadXml.DOWNLOAD_PROGRAM_RESTART].setDisable(false);
            final CheckBox box = cbx[DownloadXml.DOWNLOAD_PROGRAM_RESTART];
            cbx[DownloadXml.DOWNLOAD_PROGRAM_RESTART].setOnAction(event -> download.setProgramRestart(box.isSelected()));
        }
        gridPane.add(lbl[DownloadXml.DOWNLOAD_PROGRAM_RESTART], 0, row);
        gridPane.add(cbx[DownloadXml.DOWNLOAD_PROGRAM_RESTART], 1, row);

        //---------------------------------
        cbx[DownloadXml.DOWNLOAD_PROGRAM_DOWNLOADMANAGER].setSelected(download.getProgramDownloadmanager());
        gridPane.add(lbl[DownloadXml.DOWNLOAD_PROGRAM_DOWNLOADMANAGER], 2, row);
        gridPane.add(cbx[DownloadXml.DOWNLOAD_PROGRAM_DOWNLOADMANAGER], 3, row);
        ++row;

        //---------------------------------
        cbx[DownloadXml.DOWNLOAD_INFO_FILE].setSelected(download.getInfoFile());
        if (!isStarted) {
            cbx[DownloadXml.DOWNLOAD_INFO_FILE].setDisable(false);
            final CheckBox boxInfo = cbx[DownloadXml.DOWNLOAD_INFO_FILE];
            cbx[DownloadXml.DOWNLOAD_INFO_FILE].setOnAction(event -> download.setInfoFile(boxInfo.isSelected()));
        }
        gridPane.add(lbl[DownloadXml.DOWNLOAD_INFO_FILE], 0, row);
        gridPane.add(cbx[DownloadXml.DOWNLOAD_INFO_FILE], 1, row);

        //---------------------------------
        cbx[DownloadXml.DOWNLOAD_SUBTITLE].setSelected(download.isSubtitle());
        Film film = download.getFilm();
        if (!isStarted && film != null && !film.getUrlSubtitle().isEmpty()) {
            cbx[DownloadXml.DOWNLOAD_SUBTITLE].setDisable(false);
            final CheckBox boxSub = cbx[DownloadXml.DOWNLOAD_SUBTITLE];
            cbx[DownloadXml.DOWNLOAD_SUBTITLE].setOnAction(event -> download.setSubtitle(boxSub.isSelected()));
        }
        gridPane.add(lbl[DownloadXml.DOWNLOAD_SUBTITLE], 2, row);
        gridPane.add(cbx[DownloadXml.DOWNLOAD_SUBTITLE], 3, row);
        ++row;
        ++row;

        //---------------------------------
        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD) || download.getSetData() != null) {
            // ansonsten müsste erst der Programmaufruf neu gebaut werden
            HBox hBox = new HBox(20);
            hBox.getChildren().addAll(rbHd, rbHigh, rbSmall);

            gridPane.add(new Label("Auflösung:"), 0, row);
            gridPane.add(hBox, 1, row, 3, 1);
            ++row;
        }

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_FILM_URL, false, row++, download.filmUrlProperty(), true);
        addToGrid(DownloadXml.DOWNLOAD_URL, false, row++, download.urlProperty(), true);
        addToGrid(DownloadXml.DOWNLOAD_SET_DATA, false, row++, setData.visibleNameProperty(), true);
        addToGrid(DownloadXml.DOWNLOAD_PROGRAM, false, row++, download.programProperty(), true);
        //case DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY:
        row = initProgramArray(row);
        //case DownloadXml.DOWNLOAD_DEST_FILE_NAME:
        row = initName(row);
        //case DownloadXml.DOWNLOAD_DEST_PATH:
        row = initPath(row);

        for (int i = 0; i < DownloadXml.MAX_ELEM; ++i) {
            if (txt[i].isEditable() || !cbx[i].isDisabled()) {
                lbl[i].setTextFill(Color.BLUE);
            }
        }
        return row;
    }

    private void initGridPane_() {
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        gridPane.setHgap(5);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(10));

        int row = 0;
        for (int i = 0; i < DownloadXml.MAX_ELEM; ++i) {
            lbl[i] = new Label(DownloadXml.COLUMN_NAMES[i] + ":");
            lbl[i].setPadding(new Insets(2, 0, 2, 0));
            lblCont[i] = new Label("");

            txt[i] = new TextField("");
            txt[i].setEditable(false);
            txt[i].setMaxWidth(Double.MAX_VALUE);
            txt[i].setPrefWidth(Control.USE_COMPUTED_SIZE);

            cbx[i] = new CheckBox();
            cbx[i].setDisable(true);

            row = setGrid_(i, row);
        }
    }

    private int setGrid_(int i, int row) {
        switch (i) {
            case DownloadXml.DOWNLOAD_NR:
            case DownloadXml.DOWNLOAD_SOURCE:
            case DownloadXml.DOWNLOAD_REF:
            case DownloadXml.DOWNLOAD_PLACED_BACK:
            case DownloadXml.DOWNLOAD_TYPE:
            case DownloadXml.DOWNLOAD_HISTORY_URL:
            case DownloadXml.DOWNLOAD_BANDWIDTH:
            case DownloadXml.DOWNLOAD_INTERRUPTED:
            case DownloadXml.DOWNLOAD_URL_RTMP:
            case DownloadXml.DOWNLOAD_URL_SUBTITLE:
            case DownloadXml.DOWNLOAD_SPOTLIGHT:
            case DownloadXml.DOWNLOAD_BUTTON2:
            case DownloadXml.DOWNLOAD_PROGRAM_CALL:
                // bis hier nicht anzeigen
                break;


            case DownloadXml.DOWNLOAD_ABO:
                if (download.getAboName().isEmpty()) {
                    break;
                }
                lblCont[i].textProperty().bind(download.aboNameProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_SENDER:
                lblCont[i].textProperty().bind(download.channelProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_THEME:
                lblCont[i].textProperty().bind(download.themeProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_TITLE:
                lblCont[i].textProperty().bind(download.titleProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_BUTTON1:
                //die Auflösung an der Stelle anzgeigen
                if (!download.getType().equals(DownloadConstants.TYPE_DOWNLOAD) && download.getSetData() == null) {
                    // ansonsten müsste erst der Programmaufruf neu gebaut werden
                    break;
                }
                HBox hBox = new HBox(20);
                hBox.getChildren().addAll(rbHd, rbHigh, rbSmall);

                gridPane.add(new Label("Auflösung:"), 0, row);
                gridPane.add(hBox, 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_SIZE:
                lblCont[i].textProperty().bind(download.downloadSizeProperty().asString());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_DATE:
                lblCont[i].setText(download.getFilmDate().toString()); //todo bind
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_TIME:
                lblCont[i].textProperty().bind(download.timeProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_DURATION:
                lblCont[i].textProperty().bind(download.durationMinuteProperty().asString());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_GEO:
                if (download.getGeoBlocked()) {
                    ImageView imageView = new ImageView();
                    imageView.setImage(ProgConfig.SYSTEM_DARK_THEME.getBool() ? new ProgIcons().ICON_DIALOG_EIN_SW : new ProgIcons().ICON_DIALOG_EIN);
                    gridPane.add(imageView, 1, row);
                }
                gridPane.add(lbl[i], 0, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_HD:
                if (download.isHd()) {
                    ImageView imageView = new ImageView();
                    imageView.setImage(new ProgIcons().ICON_DIALOG_EIN_SW);
                    gridPane.add(imageView, 1, row);
                }
                gridPane.add(lbl[i], 0, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_UT:
                if (download.isUt()) {
                    ImageView imageView = new ImageView();
                    imageView.setImage(new ProgIcons().ICON_DIALOG_EIN_SW);
                    gridPane.add(imageView, 1, row);
                }
                gridPane.add(lbl[i], 0, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_FILM_URL:
                lblCont[i].textProperty().bind(download.filmUrlProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_URL:
                //todo ORF
                txt[i].setEditable(true);
                txt[i].textProperty().bindBidirectional(download.urlProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(txt[i], 1, row);
                ++row;
                break;

//                lblCont[i].textProperty().bind(download.urlProperty());
//                gridPane.add(lbl[i], 0, row);
//                gridPane.add(lblCont[i], 1, row);
//                ++row;
//                break;

            case DownloadXml.DOWNLOAD_SET_DATA:
                lblCont[i].textProperty().bind(setData.visibleNameProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_PROGRAM:
                lblCont[i].textProperty().bind(download.programProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY:
                row = initProgramArray(row);
                break;
            case DownloadXml.DOWNLOAD_DEST_FILE_NAME:
                row = initName(row);
                break;
            case DownloadXml.DOWNLOAD_DEST_PATH:
                row = initPath(row);
                break;
            case DownloadXml.DOWNLOAD_PROGRAM_RESTART:
                cbx[i].setSelected(download.getProgramRestart());
                if (!download.getProgramDownloadmanager() && !isStarted) {
                    cbx[i].setDisable(false);
                    final CheckBox box = cbx[i];
                    cbx[i].setOnAction(event -> download.setProgramRestart(box.isSelected()));
                }
                gridPane.add(lbl[i], 0, row);
                gridPane.add(cbx[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_REMAINING_TIME:
                if (download.isStateStartedRun() &&
                        download.getStart().getTimeLeftSeconds() > 0) {
                    lblCont[i].setText(DownloadConstants.getTimeLeft(download.getStart().getTimeLeftSeconds()));
                }

                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_PROGRESS:
                lblCont[i].setText(DownloadConstants.getTextProgress(
                        download.getProgramDownloadmanager(),
                        download.getState(),
                        download.getProgress()));

                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_PROGRAM_DOWNLOADMANAGER:
                cbx[i].setSelected(download.getProgramDownloadmanager());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(cbx[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_INFO_FILE:
                cbx[i].setSelected(download.getInfoFile());
                if (!isStarted) {
                    cbx[i].setDisable(false);
                    final CheckBox boxInfo = cbx[i];
                    cbx[i].setOnAction(event -> download.setInfoFile(boxInfo.isSelected()));
                }

                gridPane.add(lbl[i], 0, row);
                gridPane.add(cbx[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_SUBTITLE:
                cbx[i].setSelected(download.isSubtitle());
                Film film = download.getFilm();
                if (!isStarted && film != null && !film.getUrlSubtitle().isEmpty()) {
                    cbx[i].setDisable(false);
                    final CheckBox boxSub = cbx[i];
                    cbx[i].setOnAction(event -> download.setSubtitle(boxSub.isSelected()));
                }

                gridPane.add(lbl[i], 0, row);
                gridPane.add(cbx[i], 1, row);
                ++row;
                break;
        }

        if (txt[i].isEditable() || !cbx[i].isDisabled()) {
            lbl[i].setTextFill(Color.BLUE);
        }
        return row;
    }

    private void resetDownloadCallForProgramm() {
        if (download.getType().equals(DownloadConstants.TYPE_PROGRAM) && download.getSetData() != null) {
            // muss noch der Programmaufruf neu gebaut werden
            final String res;
            if (rbHd.isSelected()) {
                res = Film.RESOLUTION_HD;
            } else if (rbSmall.isSelected()) {
                res = Film.RESOLUTION_SMALL;
            } else {
                res = Film.RESOLUTION_NORMAL;
            }

            download.setPathName(cbPath.getSelectionModel().getSelectedItem(), txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].getText());
            final Download d = new Download(download.getSetData(), download.getFilm(), download.getSource(), download.getAbo(),
                    download.getDestFileName(),
                    download.getDestPath(), res);

            download.setProgramCall(d.getProgramCall());
            download.setProgramCallArray(d.getProgramCallArray());
            txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].setText(download.getProgramCall());
            txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].setText(download.getProgramCallArray());
        }
    }
}
