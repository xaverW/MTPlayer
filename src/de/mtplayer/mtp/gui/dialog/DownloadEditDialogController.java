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
import de.mtplayer.mLib.tools.Log;
import de.mtplayer.mLib.tools.SysMsg;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.ProgData;
import de.mtplayer.mtp.controller.data.download.Download;
import de.mtplayer.mtp.controller.data.download.DownloadInfos;
import de.mtplayer.mtp.controller.data.download.DownloadTools;
import de.mtplayer.mtp.controller.data.download.DownloadXml;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.mtplayer.mtp.tools.file.GetFile;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.File;

public class DownloadEditDialogController extends MTDialogExtra {

    private Button btnOk = new Button("Ok");
    private Button btnCancel = new Button("Abbrechen");

    private boolean ok = false;

    private final GridPane gridPane = new GridPane();

    private final Label[] lbl = new Label[DownloadXml.MAX_ELEM];
    private final Label[] lblCont = new Label[DownloadXml.MAX_ELEM];
    private final CheckBox[] cbx = new CheckBox[DownloadXml.MAX_ELEM];
    private final TextField[] txt = new TextField[DownloadXml.MAX_ELEM];
    private final RadioButton rbHd = new RadioButton("HD");
    private final RadioButton rbHoch = new RadioButton("Hoch");
    private final RadioButton rbKlein = new RadioButton("Klein");
    private final ComboBox<String> cbPath = new ComboBox<>();
    private final Button btnPath = new Button();
    private final Label lblSizeFree = new Label();

    private final ToggleGroup group = new ToggleGroup();
    private String dateiGroesse_HD = "";
    private String dateiGroesse_Hoch = "";
    private String dateiGroesse_Klein = "";
    private String resolution = FilmXml.AUFLOESUNG_NORMAL;

    private final Download download;
    private final boolean isStarted;
    private final String orgProgArray;
    private final String orgPfad;
    private final Daten daten;

    public DownloadEditDialogController(Daten daten, Download download, boolean isStarted) {
        super(null, Config.DOWNLOAD_DIALOG_EDIT_SIZE,
                "Download ändern", true);

        this.daten = daten;
        this.download = download;
        this.isStarted = isStarted;
        orgProgArray = download.arr[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY];
        orgPfad = download.getZielPfadDatei();

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);

        getVboxCont().getChildren().add(gridPane);

        btnOk.setMaxWidth(Double.MAX_VALUE);
        btnCancel.setMaxWidth(Double.MAX_VALUE);
        getTilePaneOk().getChildren().addAll(btnOk, btnCancel);
        init(getvBoxDialog(), true);
    }


    public void make() {
        btnPath.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnPath.setText("");
        btnPath.setOnAction(event -> getDestination());

        rbHd.setToggleGroup(group);
        rbHd.setOnAction(a -> changeRes());
        rbHoch.setToggleGroup(group);
        rbHoch.setOnAction(a -> changeRes());
        rbKlein.setToggleGroup(group);
        rbKlein.setOnAction(a -> changeRes());

        initButton();
        initResolutionButtons();
        initGridPane();
    }

    public boolean isOk() {
        return ok;
    }

    private void beenden() {
        if (!ok) {
            close();
            return;
        }

        close();
    }

    private boolean downloadDateiLoeschen(Download datenDownload) {
        try {
            final File file = new File(datenDownload.getZielPfadDatei());
            if (!file.exists()) {
                return true; // gibt nichts zu löschen
            }

            if (!new MTAlert().showAlert("Film Löschen?", "Auflösung wurde geändert",
                    "Die Auflösung wurde geändert, der Film kann nicht weitergeführt werden.\n " +
                            "Datei muss zuerst gelöscht werden.")) {
                return false; // user will nicht
            }

            // und jetzt die Datei löschen
            SysMsg.sysMsg(new String[]{"Datei löschen: ", file.getAbsolutePath()});
            if (!file.delete()) {
                throw new Exception();
            }
        } catch (final Exception ex) {
            new MTAlert().showErrorAlert("Film löschen",
                    "Konnte die Datei nicht löschen!",
                    "Fehler beim löschen: " + datenDownload.getZielPfadDatei());
            Log.errorLog(812036789, "Fehler beim löschen: " + datenDownload.arr[DownloadXml.DOWNLOAD_ZIEL_PFAD_DATEINAME]);
        }
        return true;
    }

    private boolean check() {
        download.setPathName(cbPath.getSelectionModel().getSelectedItem(),
                txt[DownloadXml.DOWNLOAD_ZIEL_DATEINAME].getText());

        if ((rbHd.isSelected() && !resolution.equals(FilmXml.AUFLOESUNG_HD))
                || (rbKlein.isSelected() && !resolution.equals(FilmXml.AUFLOESUNG_KLEIN))
                || (rbHoch.isSelected() && !resolution.equals(FilmXml.AUFLOESUNG_NORMAL))) {
            // dann wurde die Auflösung geändert -> Film kann nicht weitergeführt werden
            ok = downloadDateiLoeschen(download);
        } else {
            ok = true;
        }
        return ok;
    }

    private void getDestination() {
        DirFileChooser.DirChooser(Daten.getInstance().primaryStage, cbPath);
    }

    private void changeRes() {
        // RadioButton sind nur enabled wenn "datenDownload.film" vorhanden
        final String res;
        if (rbHd.isSelected()) {
            res = FilmXml.AUFLOESUNG_HD;
        } else if (rbKlein.isSelected()) {
            res = FilmXml.AUFLOESUNG_KLEIN;
        } else {
            res = FilmXml.AUFLOESUNG_NORMAL;
        }
        download.setUrl(download.getFilm().getUrlFuerAufloesung(res));
        download.setUrlRtmp(download.getFilm().getUrlFlvstreamerFuerAufloesung(res));
        txt[DownloadXml.DOWNLOAD_URL].setText(download.getUrl());
        txt[DownloadXml.DOWNLOAD_URL_RTMP].setText(download.getUrlRtmp());

        final String size;
        if (rbHd.isSelected()) {
            size = dateiGroesse_HD;
        } else if (rbKlein.isSelected()) {
            size = dateiGroesse_Klein;
        } else {
            size = dateiGroesse_Hoch;
        }

        if (download.getArt().equals(DownloadInfos.ART_PROGRAMM) && download.getpSet() != null) {
            // muss noch der Programmaufruf neu gebaut werden
            final Download d = new Download(download.getpSet(), download.getFilm(), download.getSource(), download.getAbo(),
                    download.getZielDateiname(),
                    download.getZielPfad(), res);

            download.setProgrammAufruf(d.getProgrammAufruf());
            download.setProgrammAufrufArray(d.getProgrammAufrufArray());
            txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF].setText(download.getProgrammAufruf());
            txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY].setText(download.getProgrammAufrufArray());
        }

        download.setSizeDownloadFromWeb(size);
    }


    private void initButton() {
        btnOk.setOnAction(event -> {
            if (check()) {
                beenden();
            }
        });
        btnCancel.setOnAction(event -> {
            ok = false;
            beenden();
        });

    }

    private void initResolutionButtons() {
        rbHd.setDisable(true);
        rbHoch.setDisable(true);
        rbKlein.setDisable(true);

        if (download.getFilm() != null) {

            rbHoch.setDisable(isStarted);
            rbHoch.setSelected(download.getUrl().equals(download.getFilm().getUrlFuerAufloesung(FilmXml.AUFLOESUNG_NORMAL)));
            dateiGroesse_Hoch = FilmTools.getSizeFromWeb(download.getFilm(),
                    download.getFilm().getUrlFuerAufloesung(FilmXml.AUFLOESUNG_NORMAL));
            if (!dateiGroesse_Hoch.isEmpty()) {
                rbHoch.setText(rbHoch.getText() + "   [ " + dateiGroesse_Hoch + " MB ]");
            }

            if (download.getFilm().isHd()) {
                rbHd.setDisable(isStarted);
                rbHd.setSelected(download.getUrl().equals(download.getFilm().getUrlFuerAufloesung(FilmXml.AUFLOESUNG_HD)));
                dateiGroesse_HD = FilmTools.getSizeFromWeb(download.getFilm(),
                        download.getFilm().getUrlFuerAufloesung(FilmXml.AUFLOESUNG_HD));
                if (!dateiGroesse_HD.isEmpty()) {
                    rbHd.setText(rbHd.getText() + "   [ " + dateiGroesse_HD + " MB ]");
                }
            }

            if (download.getFilm().isSmall()) {
                rbKlein.setDisable(isStarted);
                rbKlein.setSelected(download.getUrl().equals(download.getFilm().getUrlFuerAufloesung(FilmXml.AUFLOESUNG_KLEIN)));
                dateiGroesse_Klein = FilmTools.getSizeFromWeb(download.getFilm(),
                        download.getFilm().getUrlFuerAufloesung(FilmXml.AUFLOESUNG_KLEIN));
                if (!dateiGroesse_Klein.isEmpty()) {
                    rbKlein.setText(rbKlein.getText() + "   [ " + dateiGroesse_Klein + " MB ]");
                }
            }

        }
        if (rbHd.isSelected()) {
            resolution = FilmXml.AUFLOESUNG_HD;
            if (!dateiGroesse_HD.isEmpty()) {
                // ist wahrscheinlich leer
                download.setSizeDownloadFromWeb(dateiGroesse_HD);
            }
        } else if (rbKlein.isSelected()) {
            resolution = FilmXml.AUFLOESUNG_KLEIN;
            if (!dateiGroesse_Klein.isEmpty()) {
                // ist wahrscheinlich leer
                download.setSizeDownloadFromWeb(dateiGroesse_Klein);
            }
        } else {
            resolution = FilmXml.AUFLOESUNG_NORMAL;
            if (!dateiGroesse_Hoch.isEmpty()) {
                // dann den auch noch
                download.setSizeDownloadFromWeb(dateiGroesse_Hoch);
            }
        }
    }

    private int initProgrammArray(int row) {
        lbl[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF].setText(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF]);
        lbl[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY].setText(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF]);

        txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY].setText(download.getProgrammAufrufArray());
        txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF].setText(download.getProgrammAufruf());

        if (download.getArt().equals(DownloadInfos.ART_PROGRAMM)) {
            // nur bei Downloads über ein Programm

            gridPane.add(lbl[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY], 0, row);
            txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF].setEditable(!isStarted);
            txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY].setEditable(!isStarted);

            if (download.getProgrammAufrufArray().isEmpty()) {
                // Aufruf über Array ist leer -> Win, Mac
                txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF].textProperty().addListener((observable, oldValue, newValue) -> {
                    download.setProgrammAufruf(newValue.trim());
                });
                gridPane.add(txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY], 1, row);

            } else {
                // dann ist ein Array vorhanden -> Linux
                txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY].textProperty().addListener((observable, oldValue, newValue) -> {
                    download.setProgrammAufrufArray(newValue.trim());
                    download.setProgrammAufruf(ProgData.makeProgAufrufArray(download.getProgrammAufrufArray()));
                    txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF].setText(download.getProgrammAufruf());
                });

                final Button btnReset = new Button("");
                btnReset.setTooltip(new Tooltip("Reset"));
                btnReset.setGraphic(new Icons().ICON_BUTTON_RESET);
                btnReset.setOnAction(e -> txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY].setText(orgProgArray));

                final Button btnHelp = new Button("");
                btnHelp.setTooltip(new Tooltip("Hilfe"));
                btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
                btnHelp.setOnAction(event -> new MTAlert().showHelpAlert("Den Programmaufruf ändern",
                        new GetFile().getHilfeSuchen(GetFile.PFAD_HILFETEXT_EDIT_DOWNLOAD_PROG)));

                VBox vBox = new VBox(5);
                HBox hBoxArray1 = new HBox(10);
                hBoxArray1.getChildren().addAll(btnHelp, txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF]);

                HBox hBoxArray2 = new HBox(10);
                hBoxArray2.getChildren().addAll(btnReset, txt[DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY]);

                vBox.getChildren().addAll(hBoxArray1, hBoxArray2);

                gridPane.add(vBox, 1, row);
            }
            ++row;
        }
        return row;
    }

    private int initPath(int row) {

        txt[DownloadXml.DOWNLOAD_ZIEL_PFAD].setEditable(!isStarted); // für die LabelFarbe
        txt[DownloadXml.DOWNLOAD_ZIEL_PFAD].setText(download.getZielPfad());
        gridPane.add(lbl[DownloadXml.DOWNLOAD_ZIEL_PFAD], 0, row);

        VBox vBox = new VBox(5);
        HBox hBoxPath = new HBox(10);
        HBox.setHgrow(cbPath, Priority.ALWAYS);
        cbPath.setMaxWidth(Double.MAX_VALUE);
        hBoxPath.getChildren().addAll(cbPath, btnPath);
        vBox.getChildren().addAll(hBoxPath, lblSizeFree);
        gridPane.add(vBox, 1, row);
        ++row;

        // gespeicherte Pfade eintragen
        final String[] p = Config.DOWNLOAD_DIALOG_PATH_SAVING.get().split("<>");
        cbPath.getItems().addAll(p);

        if (download.getZielPfad().isEmpty()) {
            cbPath.getSelectionModel().selectFirst();
        } else {
            cbPath.getSelectionModel().select(download.getZielPfad());
        }

        cbPath.valueProperty().addListener((observable, oldValue, newValue) -> {

            final String s = cbPath.getSelectionModel().getSelectedItem();
            DownloadTools.calculateAndCheckDiskSpace(download, s, lblSizeFree);
        });

        DownloadTools.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);

        return row;
    }

    private int initName(int row) {

        txt[DownloadXml.DOWNLOAD_ZIEL_DATEINAME].setEditable(!isStarted);
        txt[DownloadXml.DOWNLOAD_ZIEL_DATEINAME].setText(download.getZielDateiname());
        gridPane.add(lbl[DownloadXml.DOWNLOAD_ZIEL_DATEINAME], 0, row);
        gridPane.add(txt[DownloadXml.DOWNLOAD_ZIEL_DATEINAME], 1, row);
        ++row;

        txt[DownloadXml.DOWNLOAD_ZIEL_DATEINAME].textProperty().addListener((observable, oldValue, newValue) -> {

            if (!txt[DownloadXml.DOWNLOAD_ZIEL_DATEINAME].getText().equals(
                    FileNameUtils.checkDateiname(txt[DownloadXml.DOWNLOAD_ZIEL_DATEINAME].getText(), false /* pfad */))) {
                txt[DownloadXml.DOWNLOAD_ZIEL_DATEINAME].setStyle(MTColor.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txt[DownloadXml.DOWNLOAD_ZIEL_DATEINAME].setStyle("");
            }
        });

        return row;
    }

    private void initGridPane() {
        int row = 0;

        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);

        gridPane.setHgap(5);
        gridPane.setVgap(15);

        for (int i = 0; i < DownloadXml.MAX_ELEM; ++i) {
            lbl[i] = new Label(DownloadXml.COLUMN_NAMES[i] + ":");
            lbl[i].setPadding(new Insets(2, 0, 2, 0));
            lblCont[i] = new Label("");

            txt[i] = new TextField("");
            txt[i].setEditable(false);
            txt[i].prefColumnCountProperty().bind(txt[i].textProperty().length());
            txt[i].setMaxWidth(Double.MAX_VALUE);

            cbx[i] = new CheckBox();
            cbx[i].setDisable(true);

            row = setGrid(i, row);
        }
    }

    private int setGrid(int i, int row) {
        switch (i) {
            case DownloadXml.DOWNLOAD_NR:
            case DownloadXml.DOWNLOAD_QUELLE:
            case DownloadXml.DOWNLOAD_REF:
            case DownloadXml.DOWNLOAD_ZURUECKGESTELLT:
            case DownloadXml.DOWNLOAD_ART:
            case DownloadXml.DOWNLOAD_HISTORY_URL:
            case DownloadXml.DOWNLOAD_BANDBREITE:
            case DownloadXml.DOWNLOAD_UNTERBROCHEN:
            case DownloadXml.DOWNLOAD_URL_RTMP:
            case DownloadXml.DOWNLOAD_URL_SUBTITLE:
            case DownloadXml.DOWNLOAD_SPOTLIGHT:
            case DownloadXml.DOWNLOAD_BUTTON2:
            case DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF:
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
                lblCont[i].textProperty().bind(download.senderProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_THEMA:
                lblCont[i].textProperty().bind(download.themaProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_TITEL:
                lblCont[i].textProperty().bind(download.titelProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_BUTTON1:
                //die Auflösung an der Stelle anzgeigen
                if (!download.getArt().equals(DownloadInfos.ART_DOWNLOAD) && download.getpSet() == null) {
                    // ansonsten müsste erst der Programmaufruf neu gebaut werden
                    break;
                }
                HBox hBox = new HBox();
                hBox.setSpacing(20);
                hBox.getChildren().addAll(rbHd, rbHoch, rbKlein);

                gridPane.add(new Label("Auflösung:"), 0, row);
                gridPane.add(hBox, 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_GROESSE:
                lblCont[i].textProperty().bind(download.downloadSizeProperty().asString());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_DATUM:
                lblCont[i].setText(download.getFilmDate().toString()); //todo bind
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_ZEIT:
                lblCont[i].textProperty().bind(download.zeitProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_DAUER:
                lblCont[i].textProperty().bind(download.dauerProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_GEO:
                if (download.getGeoBlocked()) {
                    ImageView imageView = new ImageView();
                    imageView.setImage(new Icons().ICON_DIALOG_EIN_SW);
                    gridPane.add(imageView, 1, row);
                }
                gridPane.add(lbl[i], 0, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_HD:
                if (download.isHd()) {
                    ImageView imageView = new ImageView();
                    imageView.setImage(new Icons().ICON_DIALOG_EIN_SW);
                    gridPane.add(imageView, 1, row);
                }
                gridPane.add(lbl[i], 0, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_UT:
                if (download.isUt()) {
                    ImageView imageView = new ImageView();
                    imageView.setImage(new Icons().ICON_DIALOG_EIN_SW);
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
                lblCont[i].textProperty().bind(download.urlProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_PROGRAMMSET:
                lblCont[i].textProperty().bind(download.setProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_PROGRAMM:
                lblCont[i].textProperty().bind(download.programmProperty());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_PROGRAMM_AUFRUF_ARRAY:
                row = initProgrammArray(row);
                break;
            case DownloadXml.DOWNLOAD_ZIEL_DATEINAME:
                row = initName(row);
                break;
            case DownloadXml.DOWNLOAD_ZIEL_PFAD:
                row = initPath(row);
                break;
            case DownloadXml.DOWNLOAD_PROGRAMM_RESTART:
                cbx[i].setSelected(download.isProgrammRestart());
                if (!download.isProgrammDownloadmanager() && !isStarted) {
                    cbx[i].setDisable(false);
                    final CheckBox box = cbx[i];
                    cbx[i].setOnAction(event -> download.setProgrammRestart(box.isSelected()));
                }
                gridPane.add(lbl[i], 0, row);
                gridPane.add(cbx[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_RESTZEIT:
                if (download.isStateStartedRun() &&
                        download.getStart().getTimeLeft() > 0) {
                    lblCont[i].setText(DownloadInfos.getTextRestzeit(download.getStart().getTimeLeft()));
                }

                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;
            case DownloadXml.DOWNLOAD_PROGRESS:
                lblCont[i].setText(DownloadInfos.getTextProgress(
                        download.isProgrammDownloadmanager(),
                        download.getState(),
                        download.getProgress()));

                gridPane.add(lbl[i], 0, row);
                gridPane.add(lblCont[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_PROGRAMM_DOWNLOADMANAGER:
                cbx[i].setSelected(download.isProgrammDownloadmanager());
                gridPane.add(lbl[i], 0, row);
                gridPane.add(cbx[i], 1, row);
                ++row;
                break;

            case DownloadXml.DOWNLOAD_INFODATEI:
                cbx[i].setSelected(download.isInfodatei());
                if (!isStarted) {
                    cbx[i].setDisable(false);
                    final CheckBox boxInfo = cbx[i];
                    cbx[i].setOnAction(event -> download.setInfodatei(boxInfo.isSelected()));
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
