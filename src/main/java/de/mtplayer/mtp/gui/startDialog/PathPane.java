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

package de.mtplayer.mtp.gui.startDialog;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import de.p2tools.p2Lib.P2LibConst;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

import static de.mtplayer.mLib.tools.Functions.getOs;

public class PathPane {
    StringProperty vlcProp = ProgConfig.SYSTEM_PATH_VLC.getStringProperty();
    StringProperty flvProp = ProgConfig.SYSTEM_PATH_FLVSTREAMER.getStringProperty();
    StringProperty ffmpegProp = ProgConfig.SYSTEM_PATH_FFMPEG.getStringProperty();

    private final Stage stage;

    private enum PLAYER {VLC, FLV, FFMPEG}

    public PathPane(Stage stage) {
        this.stage = stage;
    }

    public TitledPane makePath() {
        VBox vBox = new VBox();
        vBox.setSpacing(20);
        vBox.setPadding(new Insets(25, 20, 20, 20));

        switch (getOs()) {
            case WIN32:
            case WIN64:
                // da wird nur der VLC gebraucht, der Rest wird mitgeliefert
                vBox.getChildren().add(addPlayer(PLAYER.VLC));
                break;
            default:
                // da brauchs alles
                vBox.getChildren().add(addPlayer(PLAYER.VLC));
                vBox.getChildren().add(addPlayer(PLAYER.FLV));
                vBox.getChildren().add(addPlayer(PLAYER.FFMPEG));
        }

        final Button btnHelp = PButton.helpButton(stage,
                "Videoplayer", HelpText.PROG_PATHS);

        HBox hBox = new HBox();
        VBox.setVgrow(hBox, Priority.ALWAYS);

        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(btnHelp);
        vBox.getChildren().add(hBox);

        TitledPane tpConfig = new TitledPane("Programmpfade", vBox);
        return tpConfig;
    }

    private GridPane addPlayer(PLAYER player) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);

        Text text;
        PHyperlink hyperlink;
        StringProperty property;
        TextField txtPlayer = new TextField();
        final Button btnFind = new Button("suchen");
        btnFind.setMinWidth(P2LibConst.MIN_BUTTON_WIDTH);
        switch (player) {
            case FLV:
                text = new Text("Pfad zum flvstreamer-Player auswählen");

                property = flvProp;
                btnFind.setOnAction(event -> {
                    ProgConfig.SYSTEM_PATH_FLVSTREAMER.setValue("");
                    txtPlayer.setText(SetsPrograms.getTemplatePathFlv());
                });

                hyperlink = new PHyperlink(stage,
                        ProgConst.ADRESSE_WEBSITE_FLVSTREAMER,
                        ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
                break;
            case FFMPEG:
                text = new Text("Pfad zum ffmpeg-Player auswählen");
                property = ffmpegProp;
                btnFind.setOnAction(event -> {
                    ProgConfig.SYSTEM_PATH_FFMPEG.setValue("");
                    txtPlayer.setText(SetsPrograms.getTemplatePathFFmpeg());
                });

                hyperlink = new PHyperlink(stage,
                        ProgConst.ADRESSE_WEBSITE_FFMPEG,
                        ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
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
                        ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
                break;
        }
        text.setStyle("-fx-font-weight: bold");

        gridPane.add(text, 0, 0);
        txtPlayer.textProperty().addListener((observable, oldValue, newValue) -> {
            File file = new File(txtPlayer.getText());
            if (!file.exists() || !file.isFile()) {
                txtPlayer.setStyle(MTColor.DOWNLOAD_NAME_ERROR.getCssBackground());
            } else {
                txtPlayer.setStyle("");
            }
        });
        txtPlayer.textProperty().bindBidirectional(property);
        gridPane.add(txtPlayer, 0, 1);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(stage, txtPlayer);
        });
        btnFile.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnFile.setTooltip(new Tooltip("Programmdatei auswählen."));
        gridPane.add(btnFile, 1, 1);

        gridPane.add(btnFind, 2, 1);


        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);

        hBox.getChildren().addAll(new Label("Website"), hyperlink);
        gridPane.add(hBox, 0, 2, 3, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcComputedSizeAndHgrow());

        return gridPane;
    }


}
