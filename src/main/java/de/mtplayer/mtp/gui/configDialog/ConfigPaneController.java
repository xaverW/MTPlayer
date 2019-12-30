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
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.tools.update.SearchProgramUpdate;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.guiTools.PAccordion;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import de.p2tools.p2Lib.tools.PStringUtils;
import de.p2tools.p2Lib.tools.log.PLogger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
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

    private final PToggleSwitch tglSearch = new PToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");
    private final PToggleSwitch tglSearchBeta = new PToggleSwitch("auch nach neuen Vorabversionen suchen");
    private final Button btnNow = new Button("_Jetzt suchen");
    private final Button btnNowBeta = new Button("_Jetzt suchen");
    private Button btnHelpBeta;

    BooleanProperty logfileChanged = new SimpleBooleanProperty(false);

    BooleanProperty accordionProp = ProgConfig.CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    BooleanProperty propUpdateSearch = ProgConfig.SYSTEM_UPDATE_SEARCH.getBooleanProperty();
    BooleanProperty propUpdateBetaSearch = ProgConfig.SYSTEM_UPDATE_BETA_SEARCH.getBooleanProperty();
    BooleanProperty propAbo = ProgConfig.ABO_SEARCH_NOW.getBooleanProperty();
    BooleanProperty propDown = ProgConfig.DOWNLOAD_START_NOW.getBooleanProperty();
    StringProperty propDir = ProgConfig.SYSTEM_PROG_OPEN_DIR.getStringProperty();
    StringProperty propUrl = ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty();
    StringProperty propPlay = ProgConfig.SYSTEM_PROG_PLAY_FILME.getStringProperty();
    BooleanProperty propLog = ProgConfig.SYSTEM_LOG_ON.getBooleanProperty();
    StringProperty propLogDir = ProgConfig.SYSTEM_LOG_DIR.getStringProperty();
    BooleanProperty propSizeFilm = ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM.getBooleanProperty();
    BooleanProperty propSizeDownload = ProgConfig.SYSTEM_SMALL_ROW_TABLE_DOWNLOAD.getBooleanProperty();

    IntegerProperty selectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_CONFIG;

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

        PAccordion.initAccordionPane(accordion, selectedTab);
        setAccordion();
    }

    private void setAccordion() {
        if (cbxAccordion.isSelected()) {
            noaccordion.getChildren().clear();
            accordion.getPanes().addAll(createPanes());
            scrollPane.setContent(accordion);

            PAccordion.setAccordionPane(accordion, selectedTab);

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
        gridPane.setVgap(5);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Allgemein", gridPane);
        result.add(tpConfig);

        final PToggleSwitch tglSearchAbo = new PToggleSwitch("Abos automatisch suchen:");
        tglSearchAbo.selectedProperty().bindBidirectional(propAbo);
        final Button btnHelpAbo = PButton.helpButton(stage, "Abos automatisch suchen",
                HelpText.SEARCH_ABOS_IMMEDIATELY);
        GridPane.setHalignment(btnHelpAbo, HPos.RIGHT);


        final PToggleSwitch tglStartDownload = new PToggleSwitch("Downloads aus Abos sofort starten:");
        tglStartDownload.selectedProperty().bindBidirectional(propDown);
        final Button btnHelpDownload = PButton.helpButton(stage, "Downloads sofort starten",
                HelpText.START_DOWNLOADS_FROM_ABOS_IMMEDIATELY);
        GridPane.setHalignment(btnHelpDownload, HPos.RIGHT);


        final PToggleSwitch tglSmallFilm = new PToggleSwitch("In der Tabelle \"Film\" nur kleine Button anzeigen:");
        tglSmallFilm.selectedProperty().bindBidirectional(propSizeFilm);
        final PToggleSwitch tglSmallDownload = new PToggleSwitch("In der Tabelle \"Download\" nur kleine Button anzeigen:");
        tglSmallDownload.selectedProperty().bindBidirectional(propSizeDownload);
        final Button btnHelpSize = PButton.helpButton(stage, "Nur kleine Button anzeigen",
                HelpText.SMALL_BUTTON);
        GridPane.setHalignment(btnHelpSize, HPos.RIGHT);


        final Button btnHelpUserAgent = PButton.helpButton(stage, "User Agent festlegen",
                HelpText.USER_AGENT);
        GridPane.setHalignment(btnHelpUserAgent, HPos.RIGHT);
        TextField txtUserAgent = new TextField() {

            @Override
            public void replaceText(int start, int end, String text) {
                if (check(text)) {
                    super.replaceText(start, end, text);
                }
            }

            @Override
            public void replaceSelection(String text) {
                if (check(text)) {
                    super.replaceSelection(text);
                }
            }

            private boolean check(String text) {
                String str = PStringUtils.convertToASCIIEncoding(text);
                final int size = getText().length() + text.length();

                if (text.isEmpty() || (size < ProgConst.MAX_USER_AGENT_SIZE) && text.equals(str)) {
                    return true;
                }
                return false;
            }
        };
        txtUserAgent.textProperty().bindBidirectional(ProgConfig.SYSTEM_USERAGENT.getStringProperty());


        int row = 0;
        gridPane.add(tglSearchAbo, 0, row, 2, 1);
        gridPane.add(btnHelpAbo, 2, row);
        gridPane.add(tglStartDownload, 0, ++row, 2, 1);
        gridPane.add(btnHelpDownload, 2, row);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(tglSmallFilm, 0, ++row, 2, 1);
        gridPane.add(btnHelpSize, 2, row);
        gridPane.add(tglSmallDownload, 0, ++row, 2, 1);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label("User Agent:"), 0, ++row);
        gridPane.add(txtUserAgent, 1, row);
        gridPane.add(btnHelpUserAgent, 2, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(), PColumnConstraints.getCcPrefSize());
    }

    private void makeLogfile(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Logfile", gridPane);
        result.add(tpConfig);

        final PToggleSwitch tglEnableLog = new PToggleSwitch("Ein Logfile anlegen:");
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

        final Button btnHelp = PButton.helpButton(stage, "Logfile", HelpText.LOGFILE);

        TextField txtLogFile = new TextField();
        txtLogFile.textProperty().bindBidirectional(propLogDir);
        if (txtLogFile.getText().isEmpty()) {
            txtLogFile.setText(ProgInfos.getLogDirectory_String());
        }

        final Button btnFile = new Button();
        btnFile.setTooltip(new Tooltip("Einen Ordner für das Logfile auswählen."));
        btnFile.setOnAction(event -> {
            DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtLogFile);
        });
        btnFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);

        final Button btnReset = new Button();
        btnReset.setGraphic(new ProgIcons().ICON_BUTTON_RESET);
        btnReset.setTooltip(new Tooltip("Standardpfad für das Logfile wieder herstellen."));
        btnReset.setOnAction(event -> {
            txtLogFile.setText(ProgInfos.getStandardLogDirectory_String());
        });

        final Button btnChange = new Button("_Pfad zum Logfile jetzt schon ändern");
        btnChange.setTooltip(new Tooltip("Den geänderten Pfad für das Logfile\n" +
                "jetzt schon verwenden.\n\n" +
                "Ansonsten wird er erst beim nächsten\n" +
                "Programmstart verwendet."));
        btnChange.setOnAction(event -> {
            PLogger.setFileHandler(ProgInfos.getLogDirectory_String());
            logfileChanged.setValue(false);
        });

        int row = 0;
        gridPane.add(tglEnableLog, 0, row, 3, 1);
        gridPane.add(btnHelp, 3, row);

        gridPane.add(new Label(""), 0, ++row);

        gridPane.add(new Label("Ordner:"), 0, ++row);
        gridPane.add(txtLogFile, 1, row);
        gridPane.add(btnFile, 2, row);
        gridPane.add(btnReset, 3, row);

        gridPane.add(btnChange, 0, ++row, 2, 1);
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcPrefSize());

        txtLogFile.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnFile.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnReset.disableProperty().bind(tglEnableLog.selectedProperty().not());
        btnChange.disableProperty().bind(tglEnableLog.selectedProperty().not().or(logfileChanged.not()));

        txtLogFile.textProperty().addListener((observable, oldValue, newValue) -> logfileChanged.setValue(true));
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
        btnFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Dateimanager manuell auswählen"));

        final Button btnHelp = PButton.helpButton(stage, "Dateimanager", HelpText.FILEMANAGER);

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
        btnFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Videoplayer zum Abspielen der gespeicherten Filme auswählen."));

        final Button btnHelp = PButton.helpButton(stage, "Videoplayer", HelpText.VIDEOPLAYER);

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
        btnFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Einen Webbrowser zum Öffnen von URLs auswählen."));

        final Button btnHelp = PButton.helpButton(stage, "Webbrowser", HelpText.WEBBROWSER);

        gridPane.add(txtFileManager, 0, row + 1);
        gridPane.add(btnFile, 1, row + 1);
        gridPane.add(btnHelp, 2, row + 1);
    }

    private void makeUpdate(Collection<TitledPane> result) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        TitledPane tpConfig = new TitledPane("Programmupdate", gridPane);
        result.add(tpConfig);

        //einmal am Tag Update suchen
        tglSearch.selectedProperty().bindBidirectional(propUpdateSearch);
        final Button btnHelp = PButton.helpButton(stage, "Programmupdate suchen",
                "Beim Programmstart wird geprüft, ob es eine neue Version des Programms gibt. " +
                        "Ist eine aktualisierte Version vorhanden, dann wird das gemeldet."
                        + P2LibConst.LINE_SEPARATOR +
                        "Das Programm wird aber nicht ungefragt ersetzt.");

        tglSearchBeta.selectedProperty().bindBidirectional(propUpdateBetaSearch);
        btnHelpBeta = PButton.helpButton(stage, "Vorabversionen suchen",
                "Beim Programmstart wird geprüft, ob es eine neue Vorabversion des Programms gibt. " +
                        P2LibConst.LINE_SEPARATORx2 +
                        "Das sind \"Zwischenschritte\" auf dem Weg zur nächsten Version. Hier ist die " +
                        "Entwicklung noch nicht abgeschlossen und das Programm kann noch Fehler enthalten. Wer Lust hat " +
                        "einen Blick auf die nächste Version zu werfen, ist eingeladen, die Vorabversionen zu testen." +
                        P2LibConst.LINE_SEPARATORx2 +
                        "Ist eine aktualisierte Vorabversion vorhanden, dann wird das gemeldet."
                        + P2LibConst.LINE_SEPARATOR +
                        "Das Programm wird aber nicht ungefragt ersetzt.");

        //jetzt suchen
        btnNow.setOnAction(event -> new SearchProgramUpdate(stage)
                .checkVersion(true, true /* anzeigen */, false));

        btnNowBeta.setOnAction(event -> new SearchProgramUpdate(stage)
                .checkBetaVersion(true, true /* anzeigen */));

        checkBeta();
        tglSearch.selectedProperty().addListener((ob, ol, ne) -> checkBeta());

        PHyperlink hyperlink = new PHyperlink(ProgConst.ADRESSE_WEBSITE,
                ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
        HBox hBoxHyper = new HBox();
        hBoxHyper.setAlignment(Pos.CENTER_LEFT);
        hBoxHyper.setPadding(new Insets(10, 0, 0, 0));
        hBoxHyper.setSpacing(10);
        hBoxHyper.getChildren().addAll(new Label("Infos auch auf der Website:"), hyperlink);

        int row = 0;
        gridPane.add(tglSearch, 0, row);
        gridPane.add(btnNow, 1, row);
        gridPane.add(btnHelp, 2, row);

        gridPane.add(new Label(" "), 0, ++row);

        gridPane.add(tglSearchBeta, 0, ++row);
        gridPane.add(btnNowBeta, 1, row);
        gridPane.add(btnHelpBeta, 2, row);

        gridPane.add(new Label(" "), 0, ++row);

        gridPane.add(hBoxHyper, 0, ++row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
        gridPane.getRowConstraints().addAll(PColumnConstraints.getRcPrefSize(), PColumnConstraints.getRcPrefSize(),
                PColumnConstraints.getRcPrefSize(), PColumnConstraints.getRcVgrow(), PColumnConstraints.getRcPrefSize());
    }

    private void checkBeta() {
        if (tglSearch.isSelected()) {
            tglSearchBeta.setDisable(false);
            btnNowBeta.setDisable(false);
            btnHelpBeta.setDisable(false);
        } else {
            tglSearchBeta.setDisable(true);
            tglSearchBeta.setSelected(false);
            btnNowBeta.setDisable(true);
            btnHelpBeta.setDisable(true);
        }
    }
}
