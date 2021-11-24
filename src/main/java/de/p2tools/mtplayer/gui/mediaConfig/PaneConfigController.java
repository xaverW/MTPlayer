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

package de.p2tools.mtplayer.gui.mediaConfig;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.mediaDb.MediaDataWorker;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.dialogs.accordion.PAccordionPane;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class PaneConfigController extends PAccordionPane {

    private final RadioButton rbWithOutSuff = new RadioButton("keine Dateien mit diesem Suffix (z.B.: txt,xml,jpg)");
    private final RadioButton rbWithSuff = new RadioButton("nur Dateien mit diesem Suffix (z.B.: mp4,flv,m4v)");
    private TextField txtSuff = new TextField();
    private final PToggleSwitch tglNoHiddenFiles = new PToggleSwitch("keine versteckten Dateien suchen:");
    private final Slider slFileSize = new Slider();
    private final Label lblFileSize = new Label();
    private final String TXT_ALL = "alle Dateien";
    private int intValue = 0;
    private TextField txtExport = new TextField();
    private Button btnExport = new Button("exportieren");
    private Button btnExportFile = new Button("");
    private final RadioButton rbIntern = new RadioButton("interne");
    private final RadioButton rbExtern = new RadioButton("externe");
    private final RadioButton rbInternExtern = new RadioButton("beide");

    BooleanProperty propExportIntern = ProgConfig.MEDIA_DB_EXPORT_INTERN;
    BooleanProperty propExportExtern = ProgConfig.MEDIA_DB_EXPORT_EXTERN;
    BooleanProperty propExportInternExtern = ProgConfig.MEDIA_DB_EXPORT_INTERN_EXTERN;
    StringProperty propExportFile = ProgConfig.MEDIA_DB_EXPORT_FILE;
    BooleanProperty propSuff = ProgConfig.MEDIA_DB_WITH_OUT_SUFFIX;
    StringProperty propSuffStr = ProgConfig.MEDIA_DB_SUFFIX;
    BooleanProperty propNoHiddenFiles = ProgConfig.MEDIA_DB_NO_HIDDEN_FILES;

    PanePath panePathIntern;
    PanePath panePathExtern;

    private final ProgData progData;
    private final Stage stage;

    public PaneConfigController(Stage stage) {
        super(stage, ProgConfig.MEDIA_CONFIG_DIALOG_ACCORDION, ProgConfig.SYSTEM_MEDIA_DIALOG_CONFIG);
        this.stage = stage;
        progData = ProgData.getInstance();

        init();
    }

    @Override
    public void close() {
        super.close();
        rbWithOutSuff.selectedProperty().unbindBidirectional(propSuff);
        tglNoHiddenFiles.selectedProperty().unbindBidirectional(propNoHiddenFiles);

        rbIntern.selectedProperty().unbindBidirectional(propExportIntern);
        rbExtern.selectedProperty().unbindBidirectional(propExportExtern);
        rbInternExtern.selectedProperty().unbindBidirectional(propExportInternExtern);

        txtSuff.textProperty().unbindBidirectional(propSuffStr);
        txtExport.textProperty().unbindBidirectional(propExportFile);

        panePathIntern.close();
        panePathExtern.close();
    }

    @Override
    public Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        initPane(result);
        initProp();

        panePathIntern = new PanePath(stage, false);
        panePathIntern.make(result);
        panePathExtern = new PanePath(stage, true);
        panePathExtern.make(result);

        initAction();
        return result;
    }

    private void initPane(Collection<TitledPane> result) {
        VBox vBox = new VBox();
        TitledPane tpConfig = new TitledPane("Allgemein", vBox);
        result.add(tpConfig);

        final Button btnHelp = PButton.helpButton(stage,
                "Mediensammlungen verwalten", HelpText.MEDIA_COLLECTION);
        btnExportFile.setTooltip(new Tooltip("Einen Ordner für den Export auswählen"));
        btnExportFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);

        final ToggleGroup tg = new ToggleGroup();
        rbWithOutSuff.setToggleGroup(tg);
        rbWithSuff.setToggleGroup(tg);

        final ToggleGroup tgExport = new ToggleGroup();
        rbIntern.setToggleGroup(tgExport);
        rbExtern.setToggleGroup(tgExport);
        rbInternExtern.setToggleGroup(tgExport);

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        int row = 0;
        gridPane.add(rbWithOutSuff, 0, row);
        gridPane.add(btnHelp, 1, row);
        GridPane.setHalignment(btnHelp, HPos.RIGHT);
        gridPane.add(rbWithSuff, 0, ++row);
        gridPane.add(txtSuff, 0, ++row, 2, 1);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(tglNoHiddenFiles, 0, ++row, 2, 1);

        Label lbl = new Label(TXT_ALL);
        lbl.setVisible(false);
        gridPane.add(lbl, 1, ++row);
        gridPane.add(new Label("nur Dateien mit Mindestgröße suchen:"), 0, ++row);
        gridPane.add(lblFileSize, 1, row);
        GridPane.setHalignment(lblFileSize, HPos.RIGHT);
        gridPane.add(slFileSize, 0, ++row, 2, 1);

        gridPane.add(new Label(" "), 0, ++row);
        gridPane.add(new Label(" "), 0, ++row);
        HBox hBox = new HBox(15);
        HBox.setHgrow(txtExport, Priority.ALWAYS);
        hBox.getChildren().addAll(new Label("Datei:"), txtExport, btnExportFile);
        gridPane.add(new Label("Mediensammlung in eine Datei exportieren:"), 0, ++row);
        gridPane.add(hBox, 0, ++row, 2, 1);
        HBox hBoxExport = new HBox(15);
        Label lblExport = new Label("Datei:");
        lblExport.setVisible(false);
        hBoxExport.getChildren().addAll(lblExport, rbIntern, rbExtern, rbInternExtern);
        gridPane.add(hBoxExport, 0, ++row);
        gridPane.add(btnExport, 1, row);
        GridPane.setHalignment(btnExport, HPos.RIGHT);

        for (int i = 0; i < gridPane.getRowCount(); ++i) {
            RowConstraints rowC = new RowConstraints();
            rowC.setValignment(VPos.CENTER);
            gridPane.getRowConstraints().add(rowC);
        }
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
        vBox.getChildren().addAll(gridPane);

        initFileSizeSlider();
    }

    private void initProp() {
        rbWithSuff.setSelected(!propSuff.getValue());
        rbWithOutSuff.selectedProperty().bindBidirectional(propSuff);

        rbIntern.setSelected(propExportIntern.getValue());
        rbIntern.selectedProperty().bindBidirectional(propExportIntern);
        rbExtern.setSelected(propExportExtern.getValue());
        rbExtern.selectedProperty().bindBidirectional(propExportExtern);
        rbInternExtern.setSelected(propExportInternExtern.getValue());
        rbInternExtern.selectedProperty().bindBidirectional(propExportInternExtern);

        if (propExportFile.getValueSafe().isBlank()) {
            File initFile = new File(System.getProperty("user.home"), ProgConst.MEDIA_COLLECTION_EXPORT_FILE_NAME);
            propExportFile.setValue(initFile.getAbsolutePath());
        }
        txtExport.textProperty().bindBidirectional(propExportFile);
        txtSuff.textProperty().bindBidirectional(propSuffStr);
        tglNoHiddenFiles.selectedProperty().bindBidirectional(propNoHiddenFiles);
    }

    private void initAction() {
        btnExportFile.setOnAction(event -> {
            PDirFileChooser.FileChooserSaveFile(ProgData.getInstance().primaryStage, txtExport);
        });

        btnExport.setOnAction(a -> {
            MediaDataWorker.exportMediaDB(progData.mediaDataList, txtExport.getText(), rbIntern.isSelected(), rbExtern.isSelected());
        });
    }

    private void initFileSizeSlider() {
        slFileSize.setPadding(new Insets(0, 10, 0, 5));
        slFileSize.setMin(ProgConst.MEDIA_COLLECTION_FILESIZE_ALL_FILES);
        slFileSize.setMax(ProgConst.MEDIA_COLLECTION_FILESIZE_MAX);
        slFileSize.setShowTickLabels(true);
        slFileSize.setMajorTickUnit(5);
        slFileSize.setMinorTickCount(2);
        slFileSize.setBlockIncrement(5);

        slFileSize.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == ProgConst.MEDIA_COLLECTION_FILESIZE_ALL_FILES) return "alles";
                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slFileSize.setValue(ProgConfig.MEDIA_DB_FILE_SIZE_MBYTE.getValue());
        slFileSize.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        ProgConfig.MEDIA_DB_FILE_SIZE_MBYTE.setValue(intValue);
                    }
                }
        );

        slFileSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            intValue = (int) Math.round(slFileSize.getValue());
            setLabelSlider();
        });
        setLabelSlider();
    }

    private void setLabelSlider() {
        if (intValue == ProgConst.MEDIA_COLLECTION_FILESIZE_ALL_FILES) {
            lblFileSize.setText(TXT_ALL);
        } else {
            lblFileSize.setText(intValue + " MB");
        }
    }
}
