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
import de.p2tools.p2Lib.guiTools.PHyperlink;
import de.p2tools.p2Lib.guiTools.PTimePicker;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.io.File;

public class DownloadEditDialogController extends PDialogExtra {

    private boolean ok = false;
    private Button btnOk = new Button("_Ok");
    private Button btnCancel = new Button("_Abbrechen");

    private final GridPane gridPane = new GridPane();
    private final Text[] text = new Text[DownloadXml.MAX_ELEM];
    private final Label[] lblCont = new Label[DownloadXml.MAX_ELEM];
    private final CheckBox[] cbx = new CheckBox[DownloadXml.MAX_ELEM];
    private final TextField[] txt = new TextField[DownloadXml.MAX_ELEM];
    private final RadioButton rbHd = new RadioButton("HD");
    private final RadioButton rbHigh = new RadioButton("Hoch");
    private final RadioButton rbSmall = new RadioButton("Klein");
    private final ComboBox<String> cbPath = new ComboBox<>();
    private final Button btnPath = new Button();
    private final Label lblSizeFree = new Label();
    private final TextArea textAreaProg = new TextArea();
    private final TextArea textAreaCallArray = new TextArea();
    private final PToggleSwitch tglUrl = new PToggleSwitch("URL");
    PHyperlink pHyperlinkUrlFilm = new PHyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
    PHyperlink pHyperlinkUrlDownload = new PHyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);

    private final ToggleGroup group = new ToggleGroup();
    private String fileSize_HD = "";
    private String fileSize_high = "";
    private String fileSize_small = "";
    private String resolution = Film.RESOLUTION_NORMAL;
    private final PTimePicker pTimePicker = new PTimePicker();
    private final CheckBox chkStartTime = new CheckBox();

    private final Download download;
    private final boolean isStarted;
    private final String orgProgArray;
    private final String orgPath;
    private final ProgData progData;
    private final SetData setData;
    BooleanProperty urlProperty = ProgConfig.DOWNLOAD_INFO_DIALOG_SHOW_URL.getBooleanProperty();

    public DownloadEditDialogController(ProgData progData, Download download, boolean isStarted) {
        super(progData.primaryStage,
                ProgConfig.DOWNLOAD_DIALOG_EDIT_SIZE.getStringProperty(),
                "Download ändern", true, false);

        this.progData = progData;
        this.download = download;
        this.isStarted = isStarted;
        orgProgArray = download.arr[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY];
        orgPath = download.getDestPathFile();

        setData = download.getSetData();
        if (setData == null) {
//            Platform.runLater(() -> new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO));
            new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO);
        } else {
            init(true);
        }
    }

    public void make() {
        addOkCancelButtons(btnOk, btnCancel);
        getvBoxCont().getChildren().add(gridPane);

        getHboxLeft().getChildren().add(tglUrl);
        tglUrl.setTooltip(new Tooltip("URL anzeigen"));
        tglUrl.selectedProperty().bindBidirectional(urlProperty);
        tglUrl.selectedProperty().addListener((observable, oldValue, newValue) -> setUrlVis());

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
        setUrlVis();
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

    private void setUrlVis() {
        text[DownloadXml.DOWNLOAD_FILM_URL].setVisible(urlProperty.get());
        text[DownloadXml.DOWNLOAD_URL].setManaged(urlProperty.get());

        pHyperlinkUrlFilm.setVisible(urlProperty.get());
        pHyperlinkUrlFilm.setManaged(urlProperty.get());

        pHyperlinkUrlDownload.setVisible(urlProperty.get());
        pHyperlinkUrlDownload.setManaged(urlProperty.get());
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

    private void initGridPane() {
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(5));

        for (int i = 0; i < DownloadXml.MAX_ELEM; ++i) {
            text[i] = new Text(DownloadXml.COLUMN_NAMES[i] + ":");
            text[i].setFont(Font.font(null, FontWeight.BOLD, -1));

            lblCont[i] = new Label("");
            final int ii = i;
            lblCont[i].setOnContextMenuRequested(event -> {
                getMenu(lblCont[ii], event);
            });

            txt[i] = new TextField("");
            txt[i].setEditable(false);
            txt[i].setMaxWidth(Double.MAX_VALUE);
            txt[i].setPrefWidth(Control.USE_COMPUTED_SIZE);

            cbx[i] = new CheckBox();
            cbx[i].setDisable(true);
        }
        setGrid();
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
        setHyperLink();

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

    private int initProgramArray(int row) {
        text[DownloadXml.DOWNLOAD_PROGRAM_CALL].setText(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_PROGRAM_CALL]);
        text[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].setText(DownloadXml.COLUMN_NAMES[DownloadXml.DOWNLOAD_PROGRAM_CALL]);

        txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].setText(download.getProgramCallArray());
        txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].setText(download.getProgramCall());

        if (download.getType().equals(DownloadConstants.TYPE_PROGRAM)) {
            // nur bei Downloads über ein Programm
            gridPane.add(text[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY], 0, row);

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
//                HBox hBoxArray1 = new HBox(10);
//                HBox.setHgrow(txt[DownloadXml.DOWNLOAD_PROGRAM_CALL], Priority.ALWAYS);
//                hBoxArray1.getChildren().addAll(btnHelp, txt[DownloadXml.DOWNLOAD_PROGRAM_CALL]);
//
//                HBox hBoxArray2 = new HBox(10);
//                HBox.setHgrow(txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY], Priority.ALWAYS);
//                hBoxArray2.getChildren().addAll(btnReset, txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY]);

                HBox hBoxArray1 = new HBox(10);
                HBox.setHgrow(textAreaProg, Priority.ALWAYS);
                hBoxArray1.getChildren().addAll(btnHelp, textAreaProg);
                textAreaProg.textProperty().bindBidirectional(txt[DownloadXml.DOWNLOAD_PROGRAM_CALL].textProperty());
                textAreaProg.setMaxHeight(Double.MAX_VALUE);
                textAreaProg.setPrefRowCount(4);
                textAreaProg.setWrapText(true);

                HBox hBoxArray2 = new HBox(10);
                HBox.setHgrow(textAreaCallArray, Priority.ALWAYS);
                hBoxArray2.getChildren().addAll(btnReset, textAreaCallArray);
                textAreaCallArray.textProperty().bindBidirectional(txt[DownloadXml.DOWNLOAD_PROGRAM_CALL_ARRAY].textProperty());
                textAreaCallArray.setMaxHeight(Double.MAX_VALUE);
                textAreaCallArray.setPrefRowCount(4);
                textAreaCallArray.setWrapText(true);

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
        gridPane.add(text[DownloadXml.DOWNLOAD_DEST_PATH], 0, row);

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
        gridPane.add(text[DownloadXml.DOWNLOAD_DEST_FILE_NAME], 0, row);
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

    private int initStartTime(int row) {
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_LEFT);

        txt[DownloadXml.DOWNLOAD_START_TIME].setEditable(!isStarted);
        pTimePicker.setDisable(isStarted);
        chkStartTime.setDisable(isStarted);

        chkStartTime.setSelected(!download.getStartTime().isEmpty());
        chkStartTime.setOnAction(a -> {
            if (chkStartTime.isSelected()) {
                download.setStartTime(pTimePicker.getTime());
            } else {
                download.setStartTime("");
            }
        });

        pTimePicker.setTime(download.getStartTime());
        pTimePicker.setOnAction(a -> {
            download.setStartTime(pTimePicker.getTime());
        });

        pTimePicker.disableProperty().bind(chkStartTime.selectedProperty().not());

        hBox.getChildren().addAll(chkStartTime, pTimePicker);
        gridPane.add(text[DownloadXml.DOWNLOAD_START_TIME], 0, row);
        gridPane.add(hBox, 1, row, 3, 1);
        ++row;

        return row;
    }


    private void getMenu(Label lbl, ContextMenuEvent event) {
        if (lbl.getText().isEmpty()) {
            return;
        }

        final ContextMenu contextMenu = new ContextMenu();

        MenuItem menuItem = new MenuItem("kopieren");
        menuItem.setOnAction(a -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(lbl.getText());
            clipboard.setContent(content);
        });
        contextMenu.getItems().addAll(menuItem);
        contextMenu.show(lbl, event.getScreenX(), event.getScreenY());
    }

    private void addToGrid(int i, boolean second, int row, StringBinding stringProperty) {
        lblCont[i].textProperty().bind(stringProperty);
        gridPane.add(text[i], second ? 2 : 0, row);
        gridPane.add(lblCont[i], second ? 3 : 1, row);
    }

    private void addToGrid(int i, boolean second, int row, StringProperty stringProperty, boolean expand) {
        lblCont[i].textProperty().bind(stringProperty);
        gridPane.add(text[i], second ? 2 : 0, row);
        if (expand) {
            gridPane.add(lblCont[i], second ? 3 : 1, row, 3, 1);
        } else {
            gridPane.add(lblCont[i], second ? 3 : 1, row);
        }
    }

    private int setGrid() {
        int row = 0;

        //---------------------------------
        if (!download.getAboName().isEmpty()) {
            addToGrid(DownloadXml.DOWNLOAD_ABO, false, row++, download.aboNameProperty(), true);
        }

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_SENDER, false, row++, download.channelProperty(), true);

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_THEME, false, row++, download.themeProperty(), true);

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_TITLE, false, row++, download.titleProperty(), true);

        //---------------------------------
        lblCont[DownloadXml.DOWNLOAD_DATE].setText(download.getFilmDate().toString()); //todo bind
        gridPane.add(text[DownloadXml.DOWNLOAD_DATE], 0, row);
        gridPane.add(lblCont[DownloadXml.DOWNLOAD_DATE], 1, row);

        addToGrid(DownloadXml.DOWNLOAD_TIME, true, row++, download.timeProperty(), false);

        //---------------------------------
        lblCont[DownloadXml.DOWNLOAD_PROGRESS].setText(DownloadConstants.getTextProgress(
                download.getProgramDownloadmanager(),
                download.getState(),
                download.getProgress()));

        gridPane.add(text[DownloadXml.DOWNLOAD_PROGRESS], 0, row);
        gridPane.add(lblCont[DownloadXml.DOWNLOAD_PROGRESS], 1, row);

        if (download.isStateStartedRun() &&
                download.getStart().getTimeLeftSeconds() > 0) {
            lblCont[DownloadXml.DOWNLOAD_REMAINING_TIME].setText(DownloadConstants.getTimeLeft(download.getStart().getTimeLeftSeconds()));
        }
        gridPane.add(text[DownloadXml.DOWNLOAD_REMAINING_TIME], 2, row);
        gridPane.add(lblCont[DownloadXml.DOWNLOAD_REMAINING_TIME], 3, row);
        ++row;

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_SIZE, false, row, download.downloadSizeProperty().asString());
        addToGrid(DownloadXml.DOWNLOAD_DURATION, true, row++, download.durationMinuteProperty().asString());

        //---------------------------------
        HBox h = new HBox(10);
        h.getChildren().add(text[DownloadXml.DOWNLOAD_HD]);
        if (download.isHd()) {
            ImageView imageView = new ImageView();
            imageView.setImage(new ProgIcons().ICON_DIALOG_ON);
            h.getChildren().add(imageView);
        }
        gridPane.add(h, 0, row);

        h = new HBox(10);
        h.getChildren().add(text[DownloadXml.DOWNLOAD_UT]);
        if (download.isUt()) {
            ImageView imageView = new ImageView();
            imageView.setImage(new ProgIcons().ICON_DIALOG_ON);
            h.getChildren().add(imageView);
        }
        gridPane.add(h, 1, row);

        h = new HBox(10);
        h.getChildren().add(text[DownloadXml.DOWNLOAD_GEO]);
        if (download.getGeoBlocked()) {
            ImageView imageView = new ImageView();
            imageView.setImage(new ProgIcons().ICON_DIALOG_ON);
            h.getChildren().add(imageView);
        }
        gridPane.add(h, 2, row);
        ++row;

        //---------------------------------
        cbx[DownloadXml.DOWNLOAD_PROGRAM_RESTART].setSelected(download.getProgramRestart());
        if (!download.getProgramDownloadmanager() && !isStarted) {
            cbx[DownloadXml.DOWNLOAD_PROGRAM_RESTART].setDisable(false);
            final CheckBox box = cbx[DownloadXml.DOWNLOAD_PROGRAM_RESTART];
            cbx[DownloadXml.DOWNLOAD_PROGRAM_RESTART].setOnAction(event -> download.setProgramRestart(box.isSelected()));
        }
        gridPane.add(text[DownloadXml.DOWNLOAD_PROGRAM_RESTART], 0, row);
        gridPane.add(cbx[DownloadXml.DOWNLOAD_PROGRAM_RESTART], 1, row);

        //---------------------------------
        cbx[DownloadXml.DOWNLOAD_PROGRAM_DOWNLOADMANAGER].setSelected(download.getProgramDownloadmanager());
        gridPane.add(text[DownloadXml.DOWNLOAD_PROGRAM_DOWNLOADMANAGER], 2, row);
        gridPane.add(cbx[DownloadXml.DOWNLOAD_PROGRAM_DOWNLOADMANAGER], 3, row);
        ++row;

        //---------------------------------
        cbx[DownloadXml.DOWNLOAD_INFO_FILE].setSelected(download.getInfoFile());
        if (!isStarted) {
            cbx[DownloadXml.DOWNLOAD_INFO_FILE].setDisable(false);
            final CheckBox boxInfo = cbx[DownloadXml.DOWNLOAD_INFO_FILE];
            cbx[DownloadXml.DOWNLOAD_INFO_FILE].setOnAction(event -> download.setInfoFile(boxInfo.isSelected()));
        }
        gridPane.add(text[DownloadXml.DOWNLOAD_INFO_FILE], 0, row);
        gridPane.add(cbx[DownloadXml.DOWNLOAD_INFO_FILE], 1, row);

        //---------------------------------
        cbx[DownloadXml.DOWNLOAD_SUBTITLE].setSelected(download.isSubtitle());
        Film film = download.getFilm();
        if (!isStarted && film != null && !film.getUrlSubtitle().isEmpty()) {
            cbx[DownloadXml.DOWNLOAD_SUBTITLE].setDisable(false);
            final CheckBox boxSub = cbx[DownloadXml.DOWNLOAD_SUBTITLE];
            cbx[DownloadXml.DOWNLOAD_SUBTITLE].setOnAction(event -> download.setSubtitle(boxSub.isSelected()));
        }
        gridPane.add(text[DownloadXml.DOWNLOAD_SUBTITLE], 2, row);
        gridPane.add(cbx[DownloadXml.DOWNLOAD_SUBTITLE], 3, row);
        ++row;
        ++row;

        //---------------------------------
        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD) || download.getSetData() != null) {
            // ansonsten müsste erst der Programmaufruf neu gebaut werden
            HBox hBox = new HBox(20);
            hBox.getChildren().addAll(rbHd, rbHigh, rbSmall);

            Text t = new Text("Auflösung:");
            t.setFont(Font.font(null, FontWeight.BOLD, -1));
            t.setFill(Color.BLUE);
            gridPane.add(t, 0, row);
            gridPane.add(hBox, 1, row, 3, 1);
            ++row;
        }

        //---------------------------------
//        pHyperlinkUrlFilm.setUrl(download.filmUrlProperty().getValueSafe());
        pHyperlinkUrlFilm.setWrapText(true);
        pHyperlinkUrlFilm.setMinHeight(Region.USE_PREF_SIZE);
        pHyperlinkUrlFilm.setPadding(new Insets(5));

//        pHyperlinkUrlDownload.setUrl(download.urlProperty().getValueSafe());
        pHyperlinkUrlDownload.setWrapText(true);
        pHyperlinkUrlDownload.setMinHeight(Region.USE_PREF_SIZE);
        pHyperlinkUrlDownload.setPadding(new Insets(5));

        setHyperLink();

        gridPane.add(text[DownloadXml.DOWNLOAD_FILM_URL], 0, row);
        gridPane.add(pHyperlinkUrlFilm, 1, row++, 3, 1);
        gridPane.add(text[DownloadXml.DOWNLOAD_URL], 0, row);
        gridPane.add(pHyperlinkUrlDownload, 1, row++, 3, 1);

        //---------------------------------
        addToGrid(DownloadXml.DOWNLOAD_SET_DATA, false, row++, setData.visibleNameProperty(), true);
        addToGrid(DownloadXml.DOWNLOAD_PROGRAM, false, row++, download.programProperty(), true);

        row = initProgramArray(row);
        row = initName(row);
        row = initPath(row);
        row = initStartTime(row);

        for (int i = 0; i < DownloadXml.MAX_ELEM; ++i) {
            if (txt[i].isEditable() || !cbx[i].isDisabled()) {
                text[i].setFill(Color.BLUE);
            }
        }
        return row;
    }

    private void setHyperLink() {
        pHyperlinkUrlFilm.setUrl(download.filmUrlProperty().getValueSafe());
        pHyperlinkUrlDownload.setUrl(download.urlProperty().getValueSafe());
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
