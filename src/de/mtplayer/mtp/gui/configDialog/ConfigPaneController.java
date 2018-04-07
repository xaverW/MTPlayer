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
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.gui.tools.MTOpen;
import de.mtplayer.mtp.tools.update.SearchProgramUpdate;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.controlsfx.control.ToggleSwitch;

import java.util.ArrayList;
import java.util.Collection;

public class ConfigPaneController extends AnchorPane {

    private final Daten daten;
    VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");

    BooleanProperty accordionProp = Config.CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    BooleanProperty propUpdateSearch = Config.SYSTEM_UPDATE_SEARCH.getBooleanProperty();
    BooleanProperty propAbo = Config.ABO_SEARCH_NOW.getBooleanProperty();
    BooleanProperty propDown = Config.DOWNLOAD_START_NOW.getBooleanProperty();
    StringProperty propDir = Config.SYSTEM_PROG_OPEN_DIR.getStringProperty();
    StringProperty propUrl = Config.SYSTEM_PROG_OPEN_URL.getStringProperty();
    StringProperty propPlay = Config.SYSTEM_PROG_PLAY_FILE.getStringProperty();

    ScrollPane scrollPane = new ScrollPane();

    public ConfigPaneController() {
        daten = Daten.getInstance();

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

        final ToggleSwitch tglSearchAbo = new ToggleSwitch("Abos automatisch suchen");
        tglSearchAbo.setMaxWidth(Double.MAX_VALUE);
        tglSearchAbo.selectedProperty().bindBidirectional(propAbo);
        gridPane.add(tglSearchAbo, 0, 1);
        final Button btnHelpAbo = new Button("");
        btnHelpAbo.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpAbo.setOnAction(a -> new MTAlert().showHelpAlert("Abos automatisch suchen",
                HelpText.ABOS_SOFRT_SUCHEN));
        GridPane.setHalignment(btnHelpAbo, HPos.RIGHT);
        gridPane.add(btnHelpAbo, 1, 1);

        final ToggleSwitch tglStartDownload = new ToggleSwitch("Downloads aus Abos sofort starten");
        tglStartDownload.setMaxWidth(Double.MAX_VALUE);
        tglStartDownload.selectedProperty().bindBidirectional(propDown);
        gridPane.add(tglStartDownload, 0, 2);
        final Button btnHelpDownload = new Button("");
        btnHelpDownload.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelpDownload.setOnAction(a -> new MTAlert().showHelpAlert("Downloads sofort starten",
                HelpText.DOWNLOADS_AUS_ABOS_SOFORT_STARTEN));
        GridPane.setHalignment(btnHelpDownload, HPos.RIGHT);
        gridPane.add(btnHelpDownload, 1, 2);

        final ColumnConstraints ccTxt = new ColumnConstraints();
        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);
        gridPane.getColumnConstraints().addAll(new ColumnConstraints(), ccTxt);
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

        addDateimanager(gridPane, 0);
        addVideoPlayer(gridPane, 2);
        addWebbrowser(gridPane, 4);

    }

    private void addDateimanager(GridPane gridPane, int row) {
        gridPane.add(new Label("Dateimanager zum Öffnen des Downloadordners"), 0, row);
        TextField txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(propDir);
        gridPane.add(txtFileManager, 0, row + 1);


        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(Daten.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        gridPane.add(btnFile, 1, row + 1);

        final Button btnHelp = new Button("");
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Dateimanager", HelpText.FILEMANAGER));
        gridPane.add(btnHelp, 2, row + 1);

    }

    private void addVideoPlayer(GridPane gridPane, int row) {
        gridPane.add(new Label("Videoplayer zum Abspielen gespeicherter Filme"), 0, row);
        TextField txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(propPlay);
        gridPane.add(txtFileManager, 0, row + 1);


        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(Daten.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        gridPane.add(btnFile, 1, row + 1);

        final Button btnHelp = new Button("");
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Videoplayer", HelpText.VIDEOPLAYER));
        gridPane.add(btnHelp, 2, row + 1);
    }

    private void addWebbrowser(GridPane gridPane, int row) {
        gridPane.add(new Label("Webbrowser zum Öffnen von URLs"), 0, row);
        TextField txtFileManager = new TextField();
        txtFileManager.textProperty().bindBidirectional(propUrl);
        gridPane.add(txtFileManager, 0, row + 1);


        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(Daten.getInstance().primaryStage, txtFileManager);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        gridPane.add(btnFile, 1, row + 1);

        final Button btnHelp = new Button("");
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Webbrowser", HelpText.WEBBROWSER));
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
        final ToggleSwitch tglSearch = new ToggleSwitch("einmal am Tag nach einer neuen Programmversion suchen");
        tglSearch.selectedProperty().bindBidirectional(propUpdateSearch);
        gridPane.add(tglSearch, 0, 0);

        final Button btnHelp = new Button("");
        javafx.scene.image.ImageView i1 = new Icons().ICON_BUTTON_HELP;
        btnHelp.setGraphic(i1);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Programmupdate suchen",
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


        Hyperlink hyperlink = new Hyperlink(Const.ADRESSE_WEBSITE);
        hyperlink.setOnAction(a -> {
            try {
                MTOpen.openURL(Const.ADRESSE_WEBSITE);
            } catch (Exception e) {
                PLog.errorLog(932012478, e);
            }
        });
        hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.setSpacing(10);
        hBox.getChildren().addAll(new Label("Infos auch auf der Website:"), hyperlink);
        gridPane.add(hBox, 0, 2);


    }

}
