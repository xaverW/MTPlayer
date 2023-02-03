/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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
import de.p2tools.mtplayer.controller.data.BlackData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import de.p2tools.mtplayer.controller.mv.LoadMV;
import de.p2tools.mtplayer.controller.mv.MVFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.nio.file.Path;

public class ImportMVDialog extends PDialogExtra {

    private final ProgData progData;
    private final Button btnOk = new Button("_Ok");
    private final TextField txtMVPath = new TextField();
    private final ObservableList<AboData> aboList = FXCollections.observableArrayList();
    private final ObservableList<BlackData> blackList = FXCollections.observableArrayList();

    public ImportMVDialog(ProgData progData) {
        super(progData.primaryStage, ProgConfig.IMPORT_MV_DIALOG_SIZE,
                "MediathekView Einstellungen importieren", true, true, DECO.SMALL, true);
        this.progData = progData;

        initDialog();
        init(false);
        super.showDialog();
    }

    @Override
    public void close() {
        super.close();
    }

    private void initDialog() {
        VBox vBox = getVBoxCont();
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));

        Text text = new Text("1. Pfad zum Konfigfile");
        text.setFont(Font.font(null, FontWeight.BOLD, 16));
        vBox.getChildren().add(text);
        addPath(vBox);
        vBox.getChildren().add(PGuiTools.getHDistance(20));

        text = new Text("2. Einstellungen suchen");
        text.setFont(Font.font(null, FontWeight.BOLD, 16));
        vBox.getChildren().add(text);
        addLoad(vBox);
        vBox.getChildren().add(PGuiTools.getHDistance(20));

        text = new Text("3. Einstellungen anfügen");
        text.setFont(Font.font(null, FontWeight.BOLD, 16));
        vBox.getChildren().add(text);
        importLoads(vBox);

        addOkButton(btnOk);
        btnOk.setOnAction(a -> {
            close();
        });
    }

    private void addPath(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
//        gridPane.setPadding(new Insets(5, 20, 5, 20));
        vBox.getChildren().add(gridPane);

        txtMVPath.setText(MVFactory.getSettingsDirectory());

        final Button btnFile = new Button();
        btnFile.setTooltip(new Tooltip("Den Konfig-Ordner für MediathekView auswählen"));
        btnFile.setOnAction(event -> {
            PDirFileChooser.DirChooser(getStage(), txtMVPath);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());

        final Button btnHelp = PButton.helpButton(getStageProp(), "Konfigordner", HelpText.MV_PATH);

        int row = 0;
        gridPane.add(new Label("Den Pfad zum MediathekView Konfig-Ordner auswählen"), 0, row);
        gridPane.add(btnHelp, 1, row);

        gridPane.add(txtMVPath, 0, ++row);
        gridPane.add(btnFile, 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize());
    }

    private void addLoad(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
//        gridPane.setPadding(new Insets(5, 20, 5, 20));
        vBox.getChildren().add(gridPane);

        final Button btnHelp = PButton.helpButton(getStageProp(), "Konfigordner", HelpText.MV_SEARCH);

        final Button btnLoad = new Button();
        btnLoad.setTooltip(new Tooltip("Die Einstellungen suchen"));
        btnLoad.setOnAction(event -> new LoadMV(aboList, blackList).readConfiguration(Path.of(txtMVPath.getText())));
        btnLoad.setGraphic(ProgIcons.Icons.ICON_BUTTON_PLAY.getImageView());
        GridPane.setHalignment(btnLoad, HPos.RIGHT);

        final Label lblAbo = new Label();
        aboList.addListener((ListChangeListener<AboData>) c -> {
            lblAbo.setText(aboList.size() + "");
        });

        final Label lblBlack = new Label();
        blackList.addListener((ListChangeListener<BlackData>) c -> {
            lblBlack.setText(blackList.size() + "");
        });

        int row = 0;
        gridPane.add(new Label("Suchen:"), 0, row);
        gridPane.add(btnLoad, 1, row);
        gridPane.add(btnHelp, 2, row);

        ++row;
        gridPane.add(new Label("Gefundene Abos:"), 0, ++row);
        gridPane.add(lblAbo, 1, row);
        gridPane.add(new Label("Gefundene Blacks:"), 0, ++row);
        gridPane.add(lblBlack, 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcPrefSize());
    }

    private void importLoads(VBox vBox) {
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
//        gridPane.setPadding(new Insets(5, 20, 5, 20));
        vBox.getChildren().add(gridPane);

        final Button btnHelp = PButton.helpButton(getStageProp(), "Konfigordner", HelpText.MV_IMPORT);

        final Label lblFoundAbos = new Label("");
        final Button btnAddAbo = new Button();
        btnAddAbo.setTooltip(new Tooltip("Gefundene Abos an die eigenen Abos anhängen"));
        btnAddAbo.setOnAction(event -> {
            int found = MVFactory.addAbos(aboList);
            lblFoundAbos.setText(found + "");
        });
        btnAddAbo.setGraphic(ProgIcons.Icons.ICON_BUTTON_UPDATE.getImageView());
        GridPane.setHalignment(btnAddAbo, HPos.RIGHT);

        final Label lblFoundBlacks = new Label("");
        final Button btnAddBlack = new Button();
        btnAddBlack.setTooltip(new Tooltip("Gefundene Blacks an die eigenen Blacks anhängen"));
        btnAddBlack.setOnAction(event -> {
            int found = MVFactory.addBlacks(blackList);
            lblFoundBlacks.setText(found + "");
        });
        btnAddBlack.setGraphic(ProgIcons.Icons.ICON_BUTTON_UPDATE.getImageView());
        GridPane.setHalignment(btnAddBlack, HPos.RIGHT);

        int row = 0;
        gridPane.add(new Label("Hier können die gefundenen Einstellungen importiert werden"), 0, row);
        gridPane.add(btnHelp, 1, row);

        ++row;
        gridPane.add(new Label("Abos importieren:"), 0, ++row);
        gridPane.add(btnAddAbo, 1, row);
        gridPane.add(new Label("Abos eingefügt:"), 0, ++row);
        gridPane.add(lblFoundAbos, 1, row);

        ++row;
        gridPane.add(new Label("Blacks importieren:"), 0, ++row);
        gridPane.add(btnAddBlack, 1, row);
        gridPane.add(new Label("Blacks eingefügt:"), 0, ++row);
        gridPane.add(lblFoundBlacks, 1, row);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow(),
                PColumnConstraints.getCcPrefSize(), PColumnConstraints.getCcPrefSize());
    }
}
