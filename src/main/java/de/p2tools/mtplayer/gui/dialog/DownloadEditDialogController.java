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
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.controller.data.download.DownloadFactory;
import de.p2tools.mtplayer.controller.data.download.DownloadFieldNames;
import de.p2tools.mtplayer.controller.data.setdata.ProgramData;
import de.p2tools.mtplayer.controller.data.setdata.SetData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.dialogs.PDirFileChooser;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PColumnConstraints;
import de.p2tools.p2lib.guitools.PHyperlink;
import de.p2tools.p2lib.guitools.PTimePicker;
import de.p2tools.p2lib.guitools.ptoggleswitch.PToggleSwitch;
import de.p2tools.p2lib.mtfilm.film.FilmFactory;
import de.p2tools.p2lib.mtfilm.tools.FileNameUtils;
import de.p2tools.p2lib.tools.log.PLog;
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
    private final Text[] text = new Text[DownloadFieldNames.MAX_ELEM];
    private final Label[] lblCont = new Label[DownloadFieldNames.MAX_ELEM];
    private final CheckBox[] cbx = new CheckBox[DownloadFieldNames.MAX_ELEM];
    private final TextField[] txt = new TextField[DownloadFieldNames.MAX_ELEM];
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
            ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
    PHyperlink pHyperlinkUrlDownload = new PHyperlink("",
            ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());

    private final ToggleGroup group = new ToggleGroup();
    private String fileSize_HD = "";
    private String fileSize_high = "";
    private String fileSize_small = "";
    private String resolution = FilmDataMTP.RESOLUTION_NORMAL;
    private final PTimePicker pTimePicker = new PTimePicker(true);
    private final CheckBox chkStartTime = new CheckBox();

    private final DownloadData download;
    private final boolean isStarted;
    private final String orgProgArray;
    private final String orgPath;
    private final ProgData progData;
    private final SetData setData;
    BooleanProperty urlProperty = ProgConfig.DOWNLOAD_INFO_DIALOG_SHOW_URL;

    public DownloadEditDialogController(ProgData progData, DownloadData download, boolean isStarted) {
        super(progData.primaryStage,
                ProgConfig.DOWNLOAD_DIALOG_EDIT_SIZE,
                "Download ändern", true, false);

        this.progData = progData;
        this.download = download;
        this.isStarted = isStarted;
        orgProgArray = download.getProgramCallArray();
        orgPath = download.getDestPathFile();

        setData = download.getSetData();
        if (setData == null) {
//            Platform.runLater(() -> new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO));
            new NoSetDialogController(progData, NoSetDialogController.TEXT.ABO);
        } else {
            init(true);
        }
    }

    @Override
    public void make() {
        addOkCancelButtons(btnOk, btnCancel);
        getVBoxCont().getChildren().add(gridPane);

        getHboxLeft().getChildren().add(tglUrl);
        tglUrl.setTooltip(new Tooltip("URL anzeigen"));
        tglUrl.selectedProperty().bindBidirectional(urlProperty);
        tglUrl.selectedProperty().addListener((observable, oldValue, newValue) -> setUrlVis());

        btnPath.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
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
        text[DownloadFieldNames.DOWNLOAD_FILM_URL_NO].setVisible(urlProperty.get());
        text[DownloadFieldNames.DOWNLOAD_URL_NO].setManaged(urlProperty.get());

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
            rbHigh.setSelected(download.getUrl().equals(download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL)));
            fileSize_high = FilmFactory.getSizeFromWeb(download.getFilm(),
                    download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_NORMAL));
            if (!fileSize_high.isEmpty()) {
                rbHigh.setText(rbHigh.getText() + P2LibConst.LINE_SEPARATOR + "[ " + fileSize_high + " MB ]");
                rbHigh.setTextAlignment(TextAlignment.CENTER);
            }

            if (download.getFilm().isHd()) {
                rbHd.setDisable(isStarted);
                rbHd.setSelected(download.getUrl().equals(download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_HD)));
                fileSize_HD = FilmFactory.getSizeFromWeb(download.getFilm(),
                        download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_HD));
                if (!fileSize_HD.isEmpty()) {
                    rbHd.setText(rbHd.getText() + P2LibConst.LINE_SEPARATOR + "[ " + fileSize_HD + " MB ]");
                    rbHd.setTextAlignment(TextAlignment.CENTER);
                }
            }

            if (download.getFilm().isSmall()) {
                rbSmall.setDisable(isStarted);
                rbSmall.setSelected(download.getUrl().equals(download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL)));
                fileSize_small = FilmFactory.getSizeFromWeb(download.getFilm(),
                        download.getFilm().getUrlForResolution(FilmDataMTP.RESOLUTION_SMALL));
                if (!fileSize_small.isEmpty()) {
                    rbSmall.setText(rbSmall.getText() + P2LibConst.LINE_SEPARATOR + "[ " + fileSize_small + " MB ]");
                    rbSmall.setTextAlignment(TextAlignment.CENTER);
                }
            }
        }

        if (rbHd.isSelected()) {
            resolution = FilmDataMTP.RESOLUTION_HD;
            if (!fileSize_HD.isEmpty()) {
                // ist wahrscheinlich leer
                download.setSizeDownloadFromWeb(fileSize_HD);
            }
        } else if (rbSmall.isSelected()) {
            resolution = FilmDataMTP.RESOLUTION_SMALL;
            if (!fileSize_small.isEmpty()) {
                // ist wahrscheinlich leer
                download.setSizeDownloadFromWeb(fileSize_small);
            }
        } else {
            resolution = FilmDataMTP.RESOLUTION_NORMAL;
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

        for (int i = 0; i < DownloadFieldNames.MAX_ELEM; ++i) {
            text[i] = new Text(DownloadFieldNames.COLUMN_NAMES[i] + ":");
            text[i].setFont(Font.font(null, FontWeight.BOLD, -1));
//            text[i].getStyleClass().add("downloadGuiMediaText");

            lblCont[i] = new Label("");
            final int ii = i;
            lblCont[i].setOnContextMenuRequested(event -> {
                getMenu(lblCont[ii], event);
            });

            txt[i] = new TextField("");
//            txt[i].getStyleClass().add("downloadGuiMediaText");
            txt[i].setEditable(false);
            txt[i].setMaxWidth(Double.MAX_VALUE);
            txt[i].setPrefWidth(Control.USE_COMPUTED_SIZE);

            cbx[i] = new CheckBox();
            cbx[i].setDisable(true);
        }
        setGrid();
    }

    private boolean downloadDeleteFile(DownloadData dataDownload) {
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
            PLog.errorLog(812036789, "Fehler beim löschen: " + dataDownload.getDestPathFile());
        }
        return ret;
    }

    private boolean check() {
        download.setPathName(cbPath.getSelectionModel().getSelectedItem(),
                txt[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO].getText());

        if ((rbHd.isSelected() && !resolution.equals(FilmDataMTP.RESOLUTION_HD))
                || (rbSmall.isSelected() && !resolution.equals(FilmDataMTP.RESOLUTION_SMALL))
                || (rbHigh.isSelected() && !resolution.equals(FilmDataMTP.RESOLUTION_NORMAL))) {
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
            res = FilmDataMTP.RESOLUTION_HD;
        } else if (rbSmall.isSelected()) {
            res = FilmDataMTP.RESOLUTION_SMALL;
        } else {
            res = FilmDataMTP.RESOLUTION_NORMAL;
        }
        download.setUrl(download.getFilm().getUrlForResolution(res));
        txt[DownloadFieldNames.DOWNLOAD_URL_NO].setText(download.getUrl());
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
        text[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO].setText(DownloadFieldNames.COLUMN_NAMES[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO]);
        text[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO].setText(DownloadFieldNames.COLUMN_NAMES[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO]);

        txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO].setText(download.getProgramCallArray());
        txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO].setText(download.getProgramCall());

        if (download.getType().equals(DownloadConstants.TYPE_PROGRAM)) {
            // nur bei Downloads über ein Programm
            gridPane.add(text[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO], 0, row);

            Tooltip t = new Tooltip();
            t.setWrapText(true);
            t.setPrefWidth(800);
            t.textProperty().bind(txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO].textProperty());
            txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO].setTooltip(t);
            txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO].setEditable(!isStarted);

            t = new Tooltip();
            t.setWrapText(true);
            t.setPrefWidth(800);
            t.textProperty().bind(txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO].textProperty());
            txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO].setTooltip(t);
            txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO].setEditable(!isStarted);

            if (download.getProgramCallArray().isEmpty()) {
                // Aufruf über Array ist leer -> Win, Mac
                txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO].textProperty().addListener((observable, oldValue, newValue) -> {
                    download.setProgramCall(newValue.trim());
                });
                gridPane.add(txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO], 1, row);

            } else {
                // dann ist ein Array vorhanden -> Linux
                txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO].textProperty().addListener((observable, oldValue, newValue) -> {
                    download.setProgramCallArray(newValue.trim());
                    download.setProgramCall(ProgramData.makeProgAufrufArray(download.getProgramCallArray()));
                    txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO].setText(download.getProgramCall());
                });

                final Button btnReset = new Button("");
                btnReset.setTooltip(new Tooltip("Reset"));
                btnReset.setGraphic(ProgIcons.Icons.ICON_BUTTON_RESET.getImageView());
                btnReset.setOnAction(e -> txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO].setText(orgProgArray));

                final Button btnHelp = PButton.helpButton("Den Programmaufruf ändern",
                        HelpText.EDIT_DOWNLOAD_WITH_PROG);

                VBox vBox = new VBox(5);
                HBox hBoxArray1 = new HBox(10);
                HBox.setHgrow(textAreaProg, Priority.ALWAYS);
                hBoxArray1.getChildren().addAll(btnHelp, textAreaProg);
                textAreaProg.textProperty().bindBidirectional(txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO].textProperty());
                textAreaProg.setMaxHeight(Double.MAX_VALUE);
                textAreaProg.setPrefRowCount(4);
                textAreaProg.setWrapText(true);

                HBox hBoxArray2 = new HBox(10);
                HBox.setHgrow(textAreaCallArray, Priority.ALWAYS);
                hBoxArray2.getChildren().addAll(btnReset, textAreaCallArray);
                textAreaCallArray.textProperty().bindBidirectional(txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO].textProperty());
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
        txt[DownloadFieldNames.DOWNLOAD_DEST_PATH_NO].setEditable(!isStarted); // für die LabelFarbe
        txt[DownloadFieldNames.DOWNLOAD_DEST_PATH_NO].setText(download.getDestPath());
        gridPane.add(text[DownloadFieldNames.DOWNLOAD_DEST_PATH_NO], 0, row);

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
            DownloadFactory.calculateAndCheckDiskSpace(download, s, lblSizeFree);
        });

        DownloadFactory.calculateAndCheckDiskSpace(download, cbPath.getSelectionModel().getSelectedItem(), lblSizeFree);
        return row;
    }

    private int initName(int row) {
        txt[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO].setEditable(!isStarted);
        txt[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO].setText(download.getDestFileName());
        gridPane.add(text[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO], 0, row);
        gridPane.add(txt[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO], 1, row, 3, 1);
        ++row;

        txt[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO].textProperty().addListener((observable, oldValue, newValue) -> {
            if (!txt[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO].getText().equals(
                    FileNameUtils.checkFileName(txt[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO].getText(), false /* pfad */))) {
                txt[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO].setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txt[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO].setStyle("");
            }
            resetDownloadCallForProgramm();
        });
        return row;
    }

    private int initStartTime(int row) {
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_LEFT);

        txt[DownloadFieldNames.DOWNLOAD_START_TIME_NO].setEditable(!isStarted);
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
        gridPane.add(text[DownloadFieldNames.DOWNLOAD_START_TIME_NO], 0, row);
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
            addToGrid(DownloadFieldNames.DOWNLOAD_ABO_NO, false, row++, download.aboNameProperty(), true);
        }

        //---------------------------------
        addToGrid(DownloadFieldNames.DOWNLOAD_SENDER_NO, false, row++, download.channelProperty(), true);

        //---------------------------------
        addToGrid(DownloadFieldNames.DOWNLOAD_THEME_NO, false, row++, download.themeProperty(), true);

        //---------------------------------
        addToGrid(DownloadFieldNames.DOWNLOAD_TITLE_NO, false, row++, download.titleProperty(), true);

        //---------------------------------
        lblCont[DownloadFieldNames.DOWNLOAD_DATE_NO].setText(download.getFilmDate().toString()); //todo bind
        gridPane.add(text[DownloadFieldNames.DOWNLOAD_DATE_NO], 0, row);
        gridPane.add(lblCont[DownloadFieldNames.DOWNLOAD_DATE_NO], 1, row);

        addToGrid(DownloadFieldNames.DOWNLOAD_TIME_NO, true, row++, download.timeProperty(), false);

        //---------------------------------
        lblCont[DownloadFieldNames.DOWNLOAD_PROGRESS_NO].setText(DownloadConstants.getTextProgress(
                download.getProgramDownloadmanager(),
                download.getState(),
                download.getProgress()));

        gridPane.add(text[DownloadFieldNames.DOWNLOAD_PROGRESS_NO], 0, row);
        gridPane.add(lblCont[DownloadFieldNames.DOWNLOAD_PROGRESS_NO], 1, row);

        if (download.isStateStartedRun() &&
                download.getStart().getTimeLeftSeconds() > 0) {
            lblCont[DownloadFieldNames.DOWNLOAD_REMAINING_TIME_NO].setText(DownloadConstants.getTimeLeft(download.getStart().getTimeLeftSeconds()));
        }
        gridPane.add(text[DownloadFieldNames.DOWNLOAD_REMAINING_TIME_NO], 2, row);
        gridPane.add(lblCont[DownloadFieldNames.DOWNLOAD_REMAINING_TIME_NO], 3, row);
        ++row;

        //---------------------------------
        addToGrid(DownloadFieldNames.DOWNLOAD_SIZE_NO, false, row, download.downloadSizeProperty().asString());
        addToGrid(DownloadFieldNames.DOWNLOAD_DURATION_NO, true, row++, download.durationMinuteProperty().asString());

        //---------------------------------
        HBox h = new HBox(10);
        h.getChildren().add(text[DownloadFieldNames.DOWNLOAD_HD_NO]);
        if (download.isHd()) {
            ImageView imageView = new ImageView();
            imageView.setImage(ProgIcons.Icons.ICON_DIALOG_ON.getImage());
            h.getChildren().add(imageView);
        }
        gridPane.add(h, 0, row);

        h = new HBox(10);
        h.getChildren().add(text[DownloadFieldNames.DOWNLOAD_UT_NO]);
        if (download.isUt()) {
            ImageView imageView = new ImageView();
            imageView.setImage(ProgIcons.Icons.ICON_DIALOG_ON.getImage());
            h.getChildren().add(imageView);
        }
        gridPane.add(h, 1, row);

        h = new HBox(10);
        h.getChildren().add(text[DownloadFieldNames.DOWNLOAD_GEO_NO]);
        if (download.getGeoBlocked()) {
            ImageView imageView = new ImageView();
            imageView.setImage(ProgIcons.Icons.ICON_DIALOG_ON.getImage());
            h.getChildren().add(imageView);
        }
        gridPane.add(h, 2, row);
        ++row;

        //---------------------------------
        cbx[DownloadFieldNames.DOWNLOAD_PROGRAM_RESTART_NO].setSelected(download.getProgramRestart());
        if (!download.getProgramDownloadmanager() && !isStarted) {
            cbx[DownloadFieldNames.DOWNLOAD_PROGRAM_RESTART_NO].setDisable(false);
            final CheckBox box = cbx[DownloadFieldNames.DOWNLOAD_PROGRAM_RESTART_NO];
            cbx[DownloadFieldNames.DOWNLOAD_PROGRAM_RESTART_NO].setOnAction(event -> download.setProgramRestart(box.isSelected()));
        }
        gridPane.add(text[DownloadFieldNames.DOWNLOAD_PROGRAM_RESTART_NO], 0, row);
        gridPane.add(cbx[DownloadFieldNames.DOWNLOAD_PROGRAM_RESTART_NO], 1, row);

        //---------------------------------
        cbx[DownloadFieldNames.DOWNLOAD_PROGRAM_DOWNLOADMANAGER_NO].setSelected(download.getProgramDownloadmanager());
        gridPane.add(text[DownloadFieldNames.DOWNLOAD_PROGRAM_DOWNLOADMANAGER_NO], 2, row);
        gridPane.add(cbx[DownloadFieldNames.DOWNLOAD_PROGRAM_DOWNLOADMANAGER_NO], 3, row);
        ++row;

        //---------------------------------
        cbx[DownloadFieldNames.DOWNLOAD_INFO_FILE_NO].setSelected(download.getInfoFile());
        if (!isStarted) {
            cbx[DownloadFieldNames.DOWNLOAD_INFO_FILE_NO].setDisable(false);
            final CheckBox boxInfo = cbx[DownloadFieldNames.DOWNLOAD_INFO_FILE_NO];
            cbx[DownloadFieldNames.DOWNLOAD_INFO_FILE_NO].setOnAction(event -> download.setInfoFile(boxInfo.isSelected()));
        }
        gridPane.add(text[DownloadFieldNames.DOWNLOAD_INFO_FILE_NO], 0, row);
        gridPane.add(cbx[DownloadFieldNames.DOWNLOAD_INFO_FILE_NO], 1, row);

        //---------------------------------
        cbx[DownloadFieldNames.DOWNLOAD_SUBTITLE_NO].setSelected(download.isSubtitle());
        FilmDataMTP film = download.getFilm();
        if (!isStarted && film != null && !film.getUrlSubtitle().isEmpty()) {
            cbx[DownloadFieldNames.DOWNLOAD_SUBTITLE_NO].setDisable(false);
            final CheckBox boxSub = cbx[DownloadFieldNames.DOWNLOAD_SUBTITLE_NO];
            cbx[DownloadFieldNames.DOWNLOAD_SUBTITLE_NO].setOnAction(event -> download.setSubtitle(boxSub.isSelected()));
        }
        gridPane.add(text[DownloadFieldNames.DOWNLOAD_SUBTITLE_NO], 2, row);
        gridPane.add(cbx[DownloadFieldNames.DOWNLOAD_SUBTITLE_NO], 3, row);
        ++row;
        ++row;

        //---------------------------------
        if (download.getType().equals(DownloadConstants.TYPE_DOWNLOAD) || download.getSetData() != null) {
            // ansonsten müsste erst der Programmaufruf neu gebaut werden
            HBox hBox = new HBox(20);
            hBox.getChildren().addAll(rbHd, rbHigh, rbSmall);

            Text t = new Text("Auflösung:");
            t.setFont(Font.font(null, FontWeight.BOLD, -1));
            if (ProgConfig.SYSTEM_DARK_THEME.getValue()) {
                t.setFill(Color.rgb(31, 162, 206));
            } else {
                t.setFill(Color.BLUE);
            }
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

        gridPane.add(text[DownloadFieldNames.DOWNLOAD_FILM_URL_NO], 0, row);
        gridPane.add(pHyperlinkUrlFilm, 1, row++, 3, 1);
        gridPane.add(text[DownloadFieldNames.DOWNLOAD_URL_NO], 0, row);
        gridPane.add(pHyperlinkUrlDownload, 1, row++, 3, 1);

        //---------------------------------
        addToGrid(DownloadFieldNames.DOWNLOAD_SET_DATA_NO, false, row++, setData.visibleNameProperty(), true);
        addToGrid(DownloadFieldNames.DOWNLOAD_PROGRAM_NO, false, row++, download.programProperty(), true);

        row = initProgramArray(row);
        row = initName(row);
        row = initPath(row);
        row = initStartTime(row);

        for (int i = 0; i < DownloadFieldNames.MAX_ELEM; ++i) {
            if (txt[i].isEditable() || !cbx[i].isDisabled()) {
                if (ProgConfig.SYSTEM_DARK_THEME.getValue()) {
                    text[i].setFill(Color.rgb(31, 162, 206));
                } else {
                    text[i].setFill(Color.BLUE);
                }
            } else {
                text[i].getStyleClass().add("downloadGuiMediaText");
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
                res = FilmDataMTP.RESOLUTION_HD;
            } else if (rbSmall.isSelected()) {
                res = FilmDataMTP.RESOLUTION_SMALL;
            } else {
                res = FilmDataMTP.RESOLUTION_NORMAL;
            }

            download.setPathName(cbPath.getSelectionModel().getSelectedItem(), txt[DownloadFieldNames.DOWNLOAD_DEST_FILE_NAME_NO].getText());
            final DownloadData d = new DownloadData(download.getSetData(), download.getFilm(), download.getSource(), download.getAbo(),
                    download.getDestFileName(),
                    download.getDestPath(), res);

            download.setProgramCall(d.getProgramCall());
            download.setProgramCallArray(d.getProgramCallArray());
            txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_NO].setText(download.getProgramCall());
            txt[DownloadFieldNames.DOWNLOAD_PROGRAM_CALL_ARRAY_NO].setText(download.getProgramCallArray());
        }
    }
}
