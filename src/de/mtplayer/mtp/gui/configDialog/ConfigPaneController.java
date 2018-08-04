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

package de.mtplayer.mtp.gui.configDialog;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.config.ProgInfos;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.tools.update.SearchProgramUpdate;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import de.p2tools.p2Lib.tools.log.PLogger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Collection;

public class ConfigPaneController extends AnchorPane {

    private final ProgData progData;
    VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");

    BooleanProperty logfileChanged = new SimpleBooleanProperty(false);

    BooleanProperty accordionProp = ProgConfig.CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    BooleanProperty propUpdateSearch = ProgConfig.SYSTEM_UPDATE_SEARCH.getBooleanProperty();
    BooleanProperty propAbo = ProgConfig.ABO_SEARCH_NOW.getBooleanProperty();
    BooleanProperty propDown = ProgConfig.DOWNLOAD_START_NOW.getBooleanProperty();
    StringProperty propDir = ProgConfig.SYSTEM_PROG_OPEN_DIR.getStringProperty();
    StringProperty propUrl = ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty();
    StringProperty propPlay = ProgConfig.SYSTEM_PROG_PLAY_FILME.getStringProperty();
    BooleanProperty propLog = ProgConfig.SYSTEM_LOG_ON.getBooleanProperty();
    StringProperty propLogDir = ProgConfig.SYSTEM_LOG_DIR.getStringProperty();

    ScrollPane scrollPane = new ScrollPane();

    public ConfigPaneController() {
        progData = ProgData.getInstance();

        cbxAccordion.selectedProperty().bindBidirectional(accordionProp);
        cbxAccordion.selectedProperty().addListener((observable, oldValue, newValue) -> setAccordion());

        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        hBox.getChildren().addAll(cbxAccordion, scrollPane);
        getChildren().addAll(hBox);

        accordion.setPadding(new Insets(1));
        noaccordion.setPadding(new Insets(1));
        noaccordion.setSpacing(1);

        AnchorPane.setLeftAnchor(hBox, 10.0);
        AnchorPane.setBottomAnchor(hBox, 10.0);
        AnchorPane.setRightAnchor(hBox, 10.0);
        AnchorPane.setTopAnchor(hBox, 10.0);

        setAccordion();
    }

    private void setAccordion() {
        if (cbxAccordion.isSelected()) {
            noaccordion.getChildren().clear();
            accordion.getPanes().addAll(createPanes());
            scrollPane.setContent(accordion);
        } else {
            accordion.getPanes().clear();
            noaccordion.getChildren().addAll(createPanes());
            scrollPane.setContent(noaccordion);
        }
    }

    private Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        makeConfig(result);
        makeLogfile(result);
        new ColorPane().makeColor(result);
        result.add(new GeoPane().makeGeo());
        makeProg(result);
        makeUpdate(result);
        return result;
    }

    private void makeConfig(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Allgemein", gridPane);
        result.add(tpConfig);

        final PToggleSwitch tglSearchAbo = new PToggleSwitch("Abos automatisch suchen");
        tglSearchAbo.setMaxWidth(Double.MAX_VALUE);
        tglSearchAbo.selectedProperty().bindBidirectional(propAbo);

        final Button btnHelpAbo = new PButton().helpButton("Abos automatisch suchen",
                HelpText.SEARCH_ABOS_IMMEDIATELY);

        final PToggleSwitch tglStartDownload = new PToggleSwitch("Downloads aus Abos sofort starten");
        tglStartDownload.setMaxWidth(Double.MAX_VALUE);
        tglStartDownload.selectedProperty().bindBidirectional(propDown);

        final Button btnHelpDownload = new PButton().helpButton("Downloads sofort starten",
                HelpText.START_DOWNLOADS_FROM_ABOS_IMMEDIATELY);

        gridPane.add(tglSearchAbo, 0, 0, 3, 1);
        gridPane.add(btnHelpAbo, 3, 0);
        gridPane.add(tglStartDownload, 0, 1, 3, 1);
        gridPane.add(btnHelpDownload, 3, 1);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
    }

    private void makeLogfile(Collection<TitledPane> result) {
        final VBox vBox = new VBox();
        vBox.setFillWidth(true);
        TitledPane tpConfig = new TitledPane("Logfile", vBox);
        result.add(tpConfig);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().add(gridPane);

        final PToggleSwitch tglEnableLog = new PToggleSwitch("Ein Logfile anlegen");
        tglEnableLog.setMaxWidth(Double.MAX_VALUE);
        tglEnableLog.selectedProperty().bindBidirectional(propLog);
        tglEnableLog.selectedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            if (newValue) {
                PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
            } else {
                PLogger.removeFileHandler();
            }
        }));

        final Button btnHelp = new PButton().helpButton("Logfile", HelpText.LOGFILE);

        TextField txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(propLogDir);
        if (txtFileManager.getText().isEmpty()) {
            txtFileManager.setText(ProgInfos.getLogDirectory_String());
        }

        final Button btnFile = new Button();
        btnFile.setTooltip(new Tooltip("Einen Ordner für das Logfile auswählen."));
        btnFile.setOnAction(event -> {
            DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);

        final Button btnReset = new Button();
        btnReset.setGraphic(new Icons().ICON_BUTTON_RESET);
        btnReset.setTooltip(new Tooltip("Standardpfad für das Logfile wieder herstellen."));
        btnReset.setOnAction(event -> {
            txtFileManager.setText(ProgInfos.getStandardLogDirectory_String());
        });

        final Button btnChange = new Button("Logfile ändern");
        btnChange.setTooltip(new Tooltip("Mit den geänderten Einstellungen ein neues Logfile erstellen"));
        btnChange.setOnAction(event -> {
            PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
            logfileChanged.setValue(false);
        });

        int row = 0;
        gridPane.add(tglEnableLog, 0, row, 3, 1);
        gridPane.add(btnHelp, 3, row);
        gridPane.add(new Label("Ordner:"), 0, ++row);
        gridPane.add(txtFileManager, 1, row);
        gridPane.add(btnFile, 2, row);
        gridPane.add(btnReset, 3, row);
        gridPane.add(btnChange, 0, ++row, 2, 1);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);


        txtFileManager.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnFile.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnReset.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnChange.disableProperty().bind(tglEnableLog.selectedProperty().not().or(logfileChanged.not()));

        txtFileManager.textProperty().addListener((observable, oldValue, newValue) -> {
            logfileChanged.setValue(true);
        });


    }

    private void makeProg(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(ccTxt);

        TitledPane tpConfig = new TitledPane("Programme", gridPane);
        result.add(tpConfig);

        addFilemanager(gridPane, 0);
        addVideoPlayer(gridPane, 2);
        addWebbrowser(gridPane, 4);

    }

    private void addFilemanager(GridPane gridPane, int row) {
        gridPane.add(new Label("Dateimanager zum Öffnen des Downloadordners"), 0, row);
        TextField txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(propDir);
        gridPane.add(txtFileManager, 0, row + 1);


        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        gridPane.add(btnFile, 1, row + 1);

        final Button btnHelp = new Button("");
        btnHelp.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> PAlert.showHelpAlert("Dateimanager", HelpText.FILEMANAGER));
        gridPane.add(btnHelp, 2, row + 1);

    }

    private void addVideoPlayer(GridPane gridPane, int row) {
        gridPane.add(new Label("Videoplayer zum Abspielen gespeicherter Filme"), 0, row);
        TextField txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(propPlay);
        gridPane.add(txtFileManager, 0, row + 1);


        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        gridPane.add(btnFile, 1, row + 1);

        final Button btnHelp = new Button("");
        btnHelp.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> PAlert.showHelpAlert("Videoplayer", HelpText.VIDEOPLAYER));
        gridPane.add(btnHelp, 2, row + 1);
    }

    private void addWebbrowser(GridPane gridPane, int row) {
        gridPane.add(new Label("Webbrowser zum Öffnen von URLs"), 0, row);
        TextField txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(propUrl);
        gridPane.add(txtFileManager, 0, row + 1);


        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        gridPane.add(btnFile, 1, row + 1);

        final Button btnHelp = new Button("");
        btnHelp.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> PAlert.showHelpAlert("Webbrowser", HelpText.WEBBROWSER));
        gridPane.add(btnHelp, 2, row + 1);
    }

    private void makeUpdate(Collection<TitledPane> result) {
        final VBox vBox = new VBox();
        vBox.setFillWidth(true);
        TitledPane tpConfig = new TitledPane("Programmupdate", vBox);
        result.add(tpConfig);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        vBox.getChildren().add(gridPane);

        //einmal am Tag Update suchen
        final PToggleSwitch tglSearch = new PToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");
        tglSearch.selectedProperty().bindBidirectional(propUpdateSearch);
        gridPane.add(tglSearch, 0, 0);

        final Button btnHelp = new Button("");
        btnHelp.setTooltip(new Tooltip("Hilfe anzeigen."));
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> PAlert.showHelpAlert("Programmupdate suchen",
                "Beim Programmstart wird geprüft, ob es eine neue Version des Programms gibt. " +
                        "Ist eine aktualisierte Version vorhanden, wird das dann gemeldet.\n" +
                        "Das Programm wird aber nicht ungefragt ersetzt."));
        GridPane.setHalignment(btnHelp, HPos.RIGHT);
        gridPane.add(btnHelp, 1, 0);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);

        //jetzt suchen
        Button btnNow = new Button("Jetzt suchen");
        btnNow.setMaxWidth(Double.MAX_VALUE);
        btnNow.setOnAction(event -> new SearchProgramUpdate().checkVersion(true, true /* anzeigen */));

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(btnNow);
        gridPane.add(hBox, 0, 1);

        PHyperlink hyperlink = new PHyperlink(ProgConst.ADRESSE_WEBSITE,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new Icons().ICON_BUTTON_FILE_OPEN);

        hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(new Label("Infos auch auf der Website:"), hyperlink);
        gridPane.add(hBox, 0, 2);


    }

}
