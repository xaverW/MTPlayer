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

package de.p2tools.mtplayer.gui.startDialog;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.mtplayer.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.dialogs.PDirFileChooser;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import de.p2tools.p2Lib.tools.ProgramToolsFactory;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PathPane {
    StringProperty vlcProp = ProgConfig.SYSTEM_PATH_VLC;
    //    StringProperty flvProp = ProgConfig.SYSTEM_PATH_FLVSTREAMER;
    StringProperty ffmpegProp = ProgConfig.SYSTEM_PATH_FFMPEG;
    private GridPane gridPane = new GridPane();
    private int row = 0;
    private final Stage stage;

    private enum PLAYER {VLC, /*FLV,*/ FFMPEG}

    private class UnBind {
        private TextField txt;
        private StringProperty property;

        UnBind(TextField txt, StringProperty property) {
            this.txt = txt;
            this.property = property;
        }

        void unbind() {
            txt.textProperty().unbindBidirectional(property);
        }
    }

    private List<UnBind> unbindList = new ArrayList<>();

    public PathPane(Stage stage) {
        this.stage = stage;
    }

    public void close() {
        unbindList.stream().forEach(unBind -> unBind.unbind());
    }

    public TitledPane makePath() {
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));
        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

        Button btnEmpty = new Button(" "); // ist nur für die Zeilenhöhe
        btnEmpty.setVisible(false);
        gridPane.add(btnEmpty, 2, row);

        switch (ProgramToolsFactory.getOs()) {
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
//                addPlayer(PLAYER.FLV);
//                gridPane.add(new Label(" "), 0, ++row);
//                ++row;
                addPlayer(PLAYER.FFMPEG);
        }

        final Button btnHelp = PButton.helpButton(stage,
                "Videoplayer", HelpText.PROG_PATHS);
        gridPane.add(btnHelp, 2, ++row);
        GridPane.setHalignment(btnHelp, HPos.RIGHT);

        TitledPane tpConfig = new TitledPane("Programmpfade", gridPane);
        return tpConfig;
    }

    private void addPlayer(PLAYER player) {
        Text text;
        PHyperlink hyperlink;
        StringProperty property;
        TextField txtPlayer = new TextField();
        final Button btnFind = new Button("suchen");

        switch (player) {
            case FFMPEG:
                text = new Text("Pfad zum ffmpeg-Player auswählen");
                property = ffmpegProp;
                btnFind.setOnAction(event -> {
                    ProgConfig.SYSTEM_PATH_FFMPEG.setValue("");
                    txtPlayer.setText(SetsPrograms.getTemplatePathFFmpeg());
                });
                hyperlink = new PHyperlink(stage,
                        ProgConst.ADRESSE_WEBSITE_FFMPEG,
                        ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
                break;
            case VLC:
            default:
                text = new Text("Pfad zum VLC-Player auswählen");
                property = vlcProp;
                btnFind.setOnAction(event -> {
                    ProgConfig.SYSTEM_PATH_VLC.setValue("");
                    txtPlayer.setText(SetsPrograms.getTemplatePathVlc());
                });
                hyperlink = new PHyperlink(stage,
                        ProgConst.ADRESSE_WEBSITE_VLC,
                        ProgConfig.SYSTEM_PROG_OPEN_URL, ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
                break;
        }

        text.setStyle("-fx-font-weight: bold");

        txtPlayer.textProperty().addListener((observable, oldValue, newValue) -> {
            File file = new File(txtPlayer.getText());
            if (!file.exists() || !file.isFile()) {
                txtPlayer.setStyle(ProgColorList.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtPlayer.setStyle("");
            }
        });
        txtPlayer.textProperty().bindBidirectional(property);
        unbindList.add(new UnBind(txtPlayer, property));

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            PDirFileChooser.FileChooserOpenFile(stage, txtPlayer);
        });
        btnFile.setGraphic(ProgIcons.Icons.ICON_BUTTON_FILE_OPEN.getImageView());
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
