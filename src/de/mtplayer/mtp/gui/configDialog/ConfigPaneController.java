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
import de.p2tools.p2Lib.PConst;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
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
import javafx.stage.Stage;

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
    private final Stage stage;

    public ConfigPaneController(Stage stage) {
        this.stage = stage;
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
        new ColorPane(stage).makeColor(result);
        result.add(new GeoPane(stage).makeGeo());
        makeProg(result);
        makeUpdate(result);
        return result;
    }

    private void makeConfig(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Allgemein", gridPane);
        result.add(tpConfig);

        final PToggleSwitch tglSearchAbo = new PToggleSwitch("Abos automatisch suchen:");
        tglSearchAbo.selectedProperty().bindBidirectional(propAbo);

        final Button btnHelpAbo = new PButton().helpButton(stage, "Abos automatisch suchen",
                HelpText.SEARCH_ABOS_IMMEDIATELY);
        GridPane.setHalignment(btnHelpAbo, HPos.RIGHT);


        final PToggleSwitch tglStartDownload = new PToggleSwitch("Downloads aus Abos sofort starten:");
        tglStartDownload.selectedProperty().bindBidirectional(propDown);

        final Button btnHelpDownload = new PButton().helpButton(stage, "Downloads sofort starten",
                HelpText.START_DOWNLOADS_FROM_ABOS_IMMEDIATELY);
        GridPane.setHalignment(btnHelpDownload, HPos.RIGHT);


        gridPane.add(tglSearchAbo, 0, 0);
        gridPane.add(btnHelpAbo, 1, 0);
        gridPane.add(tglStartDownload, 0, 1);
        gridPane.add(btnHelpDownload, 1, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());
    }

    private void makeLogfile(Collection<TitledPane> result) {
        final VBox vBox = new VBox();
        vBox.setFillWidth(true);
        TitledPane tpConfig = new TitledPane("Logfile", vBox);
        result.add(tpConfig);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        final PToggleSwitch tglEnableLog = new PToggleSwitch("Ein Logfile anlegen:", false, false);
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

        final Button btnHelp = new PButton().helpButton(stage, "Logfile", HelpText.LOGFILE);

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
        gridPane.add(tglEnableLog, 0, row, 2, 1);
        gridPane.add(btnHelp, 3, row);

        gridPane.add(new Label(""), 0, ++row);

        gridPane.add(new Label("Ordner:"), 0, ++row);
        gridPane.add(txtFileManager, 1, row);
        gridPane.add(btnFile, 2, row);
        gridPane.add(btnReset, 3, row);

        gridPane.add(btnChange, 0, ++row, 2, 1);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

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
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Programme", gridPane);
        result.add(tpConfig);

        addFilemanager(gridPane, 0);
        addVideoPlayer(gridPane, 2);
        addWebbrowser(gridPane, 4);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

    }

    private void addFilemanager(GridPane gridPane, int row) {
        gridPane.add(new Label("Dateimanager zum Öffnen des Downloadordners"), 0, row);
        TextField txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(propDir);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Dateimanager manuell auswählen"));

        final Button btnHelp = new PButton().helpButton(stage, "Dateimanager", HelpText.FILEMANAGER);

        gridPane.add(txtFileManager, 0, row + 1);
        gridPane.add(btnFile, 1, row + 1);
        gridPane.add(btnHelp, 2, row + 1);
    }

    private void addVideoPlayer(GridPane gridPane, int row) {
        gridPane.add(new Label("Videoplayer zum Abspielen gespeicherter Filme"), 0, row);
        TextField txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(propPlay);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Videoplayer zum Abspielen der gespeicherten Filme auswählen."));

        final Button btnHelp = new PButton().helpButton(stage, "Videoplayer", HelpText.VIDEOPLAYER);

        gridPane.add(txtFileManager, 0, row + 1);
        gridPane.add(btnFile, 1, row + 1);
        gridPane.add(btnHelp, 2, row + 1);
    }

    private void addWebbrowser(GridPane gridPane, int row) {
        gridPane.add(new Label("Webbrowser zum Öffnen von URLs"), 0, row);
        TextField txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(propUrl);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Webbrowser zum Öffnen von URLs auswählen."));

        final Button btnHelp = new PButton().helpButton(stage, "Webbrowser", HelpText.WEBBROWSER);

        gridPane.add(txtFileManager, 0, row + 1);
        gridPane.add(btnFile, 1, row + 1);
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
        gridPane.setPadding(new Insets(20));
        vBox.getChildren().add(gridPane);

        //einmal am Tag Update suchen
        final PToggleSwitch tglSearch = new PToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");
        tglSearch.selectedProperty().bindBidirectional(propUpdateSearch);
        tglSearch.setHGrow(false);

        final Button btnHelp = new PButton().helpButton(stage, "Programmupdate suchen",
                "Beim Programmstart wird geprüft, ob es eine neue Version des Programms gibt. " +
                        "Ist eine aktualisierte Version vorhanden, wird das dann gemeldet." + PConst.LINE_SEPARATOR +
                        "Das Programm wird aber nicht ungefragt ersetzt.");
        GridPane.setHalignment(btnHelp, HPos.RIGHT);

        //jetzt suchen
        Button btnNow = new Button("Jetzt suchen");
        btnNow.setOnAction(event -> new SearchProgramUpdate(stage).checkVersion(true, true /* anzeigen */));

        PHyperlink hyperlink = new PHyperlink(ProgConst.ADRESSE_WEBSITE,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new Icons().ICON_BUTTON_FILE_OPEN);

        HBox hBoxHyper = new HBox();
        hBoxHyper.setAlignment(Pos.CENTER_LEFT);
        hBoxHyper.setPadding(new Insets(10, 0, 0, 0));
        hBoxHyper.setSpacing(10);
        hBoxHyper.getChildren().addAll(new Label("Infos auch auf der Website:"), hyperlink);

        int row = 0;
        gridPane.add(tglSearch, 0, row);
        gridPane.add(btnHelp, 1, row);
        gridPane.add(new Label(" "), 0, ++row);

        gridPane.add(btnNow, 0, ++row);
        gridPane.add(hBoxHyper, 0, ++row);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());
    }

}
