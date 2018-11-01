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

package de.mtplayer.mtp.gui.dialog;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mLib.tools.FileNameUtils;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.data.ProgramData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadConstants;
import de.mtplayer.mtp.controller.data.download.DownloadTools;
import de.mtplayer.mtp.controller.data.download.DownloadXml;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.tools.file.GetFile;
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;

public class DownloadEditDialogController extends PDialogExtra {

    private Button btnOk = new Button("Ok");
    private Button btnCancel = new Button("Abbrechen");

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

    public DownloadEditDialogController(ProgData progData, Download download, boolean isStarted) {
        super(ProgConfig.DOWNLOAD_DIALOG_EDIT_SIZE.getStringProperty(),
                "Download ändern", true);

        this.progData = progData;
        this.download = download;
        this.isStarted = isStarted;

        orgProgArray = download.arr[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY];
        orgPath = download.getDestPathFile();

        addOkButtons(btnOk, btnCancel);

        getVboxCont().getChildren().add(gridPane);
        init(true);
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
        try {
            final File file = new File(dataDownload.getDestPathFile());
            if (!file.exists()) {
                return true; // gibt nichts zu löschen
            }

            if (!PAlert.showAlert("Film Löschen?", "Auflösung wurde geändert",
                    "Die Auflösung wurde geändert, der Film kann nicht weitergeführt werden." + PConst.LINE_SEPARATOR +
                            "Datei muss zuerst gelöscht werden.")) {
                return false; // user will nicht
            }

            // und jetzt die Datei löschen
            PLog.sysLog(new String[]{"Datei löschen: ", file.getAbsolutePath()});
            if (!file.delete()) {
                throw new Exception();
            }
        } catch (final Exception ex) {
            PAlert.showErrorAlert("Film löschen",
                    "Konnte die Datei nicht löschen!",
                    "Fehler beim löschen: " + dataDownload.getDestPathFile());
            PLog.errorLog(812036789, "Fehler beim löschen: " + dataDownload.arr[DownloadXml.DOWNLOAD_DEST_PATH_FILE_NAME]);
        }
        return true;
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
        DirFileChooser.DirChooser(getStage(), cbPath);
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

        if (download.getArt().equals(DownloadConstants.ART_PROGRAM) && download.getpSet() != null) {
            // muss noch der Programmaufruf neu gebaut werden
            final Download d = new Download(download.getpSet(), download.getFilm(), download.getSource(), download.getAbo(),
                    download.getDestFileName(),
                    download.getDestPath(), res);

            download.setProgramCall(d.getProgramCall());
            download.setProgramCallArray(d.getProgramCallArray());
            txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].setText(download.getProgramCall());
            txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].setText(download.getProgramCallArray());
        }

        download.setSizeDownloadFromWeb(size);
    }


    private void initButton() {
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
                rbHigh.setText(rbHigh.getText() + "   [ " + fileSize_high + " MB ]");
            }

            if (download.getFilm().isHd()) {
                rbHd.setDisable(isStarted);
                rbHd.setSelected(download.getUrl().equals(download.getFilm().getUrlForResolution(Film.RESOLUTION_HD)));
                fileSize_HD = FilmTools.getSizeFromWeb(download.getFilm(),
                        download.getFilm().getUrlForResolution(Film.RESOLUTION_HD));
                if (!fileSize_HD.isEmpty()) {
                    rbHd.setText(rbHd.getText() + "   [ " + fileSize_HD + " MB ]");
                }
            }

            if (download.getFilm().isSmall()) {
                rbSmall.setDisable(isStarted);
                rbSmall.setSelected(download.getUrl().equals(download.getFilm().getUrlForResolution(Film.RESOLUTION_SMALL)));
                fileSize_small = FilmTools.getSizeFromWeb(download.getFilm(),
                        download.getFilm().getUrlForResolution(Film.RESOLUTION_SMALL));
                if (!fileSize_small.isEmpty()) {
                    rbSmall.setText(rbSmall.getText() + "   [ " + fileSize_small + " MB ]");
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

        if (download.getArt().equals(DownloadConstants.ART_PROGRAM)) {
            // nur bei Downloads über ein Programm

            gridPane.add(lbl[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY], 0, row);
            txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].setEditable(!isStarted);
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

                final Button btnHelp = new PButton().helpButton("Den Programmaufruf ändern",
                        new GetFile().getHelpSearch(GetFile.PATH_HELPTEXT_EDIT_DOWNLOAD_PROG));

                VBox vBox = new VBox(5);
                HBox hBoxArray1 = new HBox(10);
                hBoxArray1.getChildren().addAll(btnHelp, txt[DownloadXml.DOWNLOAD_PROGRAM_CALL]);

                HBox hBoxArray2 = new HBox(10);
                hBoxArray2.getChildren().addAll(btnReset, txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY]);

                vBox.getChildren().addAll(hBoxArray1, hBoxArray2);

                gridPane.add(vBox, 1, row);
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
        gridPane.add(vBox, 1, row);
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
            DownloadTools.calculateAndCheckDiskSpace(download, s, lblSizeFree);
        });

        DownloadTools.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);

        return row;
    }

    private int initName(int row) {

        txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].setEditable(!isStarted);
        txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].setText(download.getDestFileName());
        gridPane.add(lbl[DownloadXml.DOWNLOAD_DEST_FILE_NAME], 0, row);
        gridPane.add(txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME], 1, row);
        ++row;

        txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].textProperty().addListener((observable, oldValue, newValue) -> {

            if (!txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].getText().equals(
                    FileNameUtils.checkFileName(txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].getText(), false /* pfad */))) {
                txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].setStyle(MTColor.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txt[DownloadXml.DOWNLOAD_DEST_FILE_NAME].setStyle("");
            }
        });

        return row;
    }

    private void initGridPane() {
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

//        gridPane.setMinWidth(Control.USE_PREF_SIZE);
//        gridPane.setMinWidth(Control.USE_COMPUTED_SIZE);
//        gridPane.setPrefWidth(Control.USE_COMPUTED_SIZE);
//        gridPane.setMaxWidth(Double.MAX_VALUE);
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

            row = setGrid(i, row);
        }
    }

    private int setGrid(int i, int row) {
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
                if (!download.getArt().equals(DownloadConstants.ART_DOWNLOAD) && download.getpSet() == null) {
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
                lblCont[i].textProperty().bind(download.durationProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_GEO:
                if (download.getGeoBlocked()) {
                    ImageView imageView = new ImageView();
                    imageView.setImage(new ProgIcons().ICON_DIALOG_EIN_SW);
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

            case DownloadXml.DOWNLOAD_PROGRAM_SET:
                lblCont[i].textProperty().bind(download.setProperty());
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
                        download.getStart().getTimeLeft() > 0) {
                    lblCont[i].setText(DownloadConstants.getTimeLeft(download.getStart().getTimeLeft()));
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
                if (isStarted) {
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
}
