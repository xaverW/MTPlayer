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

package de.p2tools.mtplayer.gui.startdialog;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.setdata.SetFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.dialogs.P2DirFileChooser;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Hyperlink;
import de.p2tools.p2lib.tools.P2InfoFactory;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StartPanePath {
    private final GridPane gridPane = new GridPane();
    private int row = 0;
    private final Stage stage;

    private enum PLAYER {VLC, FFMPEG}

    private static class UnBind {
        private final TextField txt;
        private final StringProperty property;

        UnBind(TextField txt, StringProperty property) {
            this.txt = txt;
            this.property = property;
        }

        void unbind() {
            txt.textProperty().unbindBidirectional(property);
        }
    }

    private final List<UnBind> unbindList = new ArrayList<>();

    public StartPanePath(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        unbindList.forEach(UnBind::unbind);
    }

    public TitledPane makePath() {
        VBox vBox = new VBox(10);

        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        Label lbl = new Label("Diese Programme werden zum Abspielen der Filme und zum Download der Filme " +
                "gebraucht. Wird der Pfad nicht automatisch erkannt, muss er hier ausgewählt werden.");
        lbl.setWrapText(true);
        lbl.setPrefWidth(500);
        hBox.getChildren().add(lbl);
        vBox.getChildren().addAll(P2GuiTools.getVDistance(5), hBox, P2GuiTools.getVDistance(20));

        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcComputedSizeAndHgrow());

        switch (P2InfoFactory.getOs()) {
            case WIN32:
            case WIN64:
                // da wird nur der VLC gebraucht, der Rest wird mitgeliefert
                addPlayer(PLAYER.VLC);
                break;
            default:
                // da brauchs alles
                addPlayer(PLAYER.VLC);
                gridPane.add(new Label(" "), 0, ++row);
                ++row;
                addPlayer(PLAYER.FFMPEG);
        }

        final Button btnHelp = P2Button.helpButton(stage,
                "Videoplayer", HelpText.PROG_PATHS);
        gridPane.add(btnHelp, 2, ++row);
        GridPane.setHalignment(btnHelp, HPos.RIGHT);
        vBox.getChildren().add(gridPane);

        return new TitledPane("Programmpfade", vBox);
    }

    private void addPlayer(PLAYER player) {
        Text text;
        P2Hyperlink hyperlink;
        StringProperty property;
        TextField txtPlayer = new TextField();
        final Button btnFind = new Button("suchen");

        switch (player) {
            case FFMPEG:
                text = new Text("Pfad zum ffmpeg-Player auswählen");
                text.getStyleClass().add("downloadGuiMediaText");
                property = ProgConfig.SYSTEM_PATH_FFMPEG;
                btnFind.setOnAction(event -> {
                    ProgConfig.SYSTEM_PATH_FFMPEG.setValue("");
                    txtPlayer.setText(SetFactory.getTemplatePathFFmpeg());
                });
                hyperlink = new P2Hyperlink(stage,
                        ProgConst.ADRESSE_WEBSITE_FFMPEG,
                        ProgConfig.SYSTEM_PROG_OPEN_URL);
                break;
            case VLC:
            default:
                text = new Text("Pfad zum VLC-Player auswählen");
                text.getStyleClass().add("downloadGuiMediaText");
                property = ProgConfig.SYSTEM_PATH_VLC;
                btnFind.setOnAction(event -> {
                    ProgConfig.SYSTEM_PATH_VLC.setValue("");
                    txtPlayer.setText(SetFactory.getTemplatePathVlc());
                });
                hyperlink = new P2Hyperlink(stage,
                        ProgConst.ADRESSE_WEBSITE_VLC,
                        ProgConfig.SYSTEM_PROG_OPEN_URL);
                break;
        }

        text.setStyle("-fx-font-weight: bold");

        txtPlayer.textProperty().addListener((observable, oldValue, newValue) -> {
            File file = new File(txtPlayer.getText());
            if (txtPlayer.getText().isEmpty() || !file.exists() || !file.isFile()) {
                txtPlayer.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtPlayer.setStyle("");
            }
        });
        txtPlayer.textProperty().bindBidirectional(property);
        if (txtPlayer.getText().isEmpty()) {
            txtPlayer.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
        }

        unbindList.add(new UnBind(txtPlayer, property));

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            P2DirFileChooser.FileChooserOpenFile(stage, txtPlayer);
        });
        btnFile.setGraphic(ProgIcons.ICON_BUTTON_FILE_OPEN.getImageView());
        btnFile.setTooltip(new Tooltip("Programmdatei auswählen"));

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.getChildren().addAll(new Label("Website"), hyperlink);

        gridPane.add(text, 0, row);
        gridPane.add(txtPlayer, 0, ++row);
        gridPane.add(btnFile, 1, row);
        gridPane.add(btnFind, 2, row);
        gridPane.add(hBox, 0, ++row, 3, 1);
    }
}
