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

package de.mtplayer.mtp.gui.dialogStart;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mLib.tools.Log;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.gui.tools.MTOpen;
import de.mtplayer.mtp.gui.tools.SetsPrograms;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.File;

import static de.mtplayer.mLib.tools.Functions.getOs;

public class PathPane {
    StringProperty vlcProp = Config.SYSTEM_PFAD_VLC.getStringProperty();
    StringProperty flvProp = Config.SYSTEM_PFAD_FLVSTREAMER.getStringProperty();
    StringProperty ffmpegProp = Config.SYSTEM_PFAD_FFMPEG.getStringProperty();

    private final ColumnConstraints ccTxt = new ColumnConstraints();

    private enum PLAYER {VLC, FLV, FFMPEG}

    public TitledPane makePath() {

        ccTxt.setFillWidth(true);
        ccTxt.setMinWidth(Region.USE_COMPUTED_SIZE);
        ccTxt.setHgrow(Priority.ALWAYS);

        VBox vBox = new VBox();
        vBox.setSpacing(20);

        TitledPane tpConfig = new TitledPane("Programmpfade", vBox);

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

        final Button btnHelp = new Button("");
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Videoplayer", HelpText.PROG_PATHS));

        HBox hBox = new HBox();
        VBox.setVgrow(hBox, Priority.ALWAYS);

        hBox.setAlignment(Pos.BOTTOM_RIGHT);
        hBox.getChildren().add(btnHelp);
        vBox.getChildren().add(hBox);

        return tpConfig;
    }

    private GridPane addPlayer(PLAYER player) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);

        Text text;
        Hyperlink hyperlink;
        StringProperty property;
        TextField txtPlayer = new TextField();
        final Button btnFind = new Button("suchen");
        switch (player) {
            case FLV:
                text = new Text("Pfad zum flvstreamer-Player auswählen");
                hyperlink = new Hyperlink(Const.ADRESSE_WEBSITE_FLVSTREAMER);
                property = flvProp;
                btnFind.setOnAction(event -> {
                    Config.SYSTEM_PFAD_FLVSTREAMER.setValue("");
                    txtPlayer.setText(SetsPrograms.getMusterPfadFlv());
                });
                hyperlink.setOnAction(a -> {
                    try {
                        MTOpen.openURL(Const.ADRESSE_WEBSITE_FLVSTREAMER);
                    } catch (Exception e) {
                        Log.errorLog(784125469, e);
                    }
                });
                break;
            case FFMPEG:
                text = new Text("Pfad zum ffmpeg-Player auswählen");
                hyperlink = new Hyperlink(Const.ADRESSE_WEBSITE_FFMPEG);
                property = ffmpegProp;
                btnFind.setOnAction(event -> {
                    Config.SYSTEM_PFAD_FFMPEG.setValue("");
                    txtPlayer.setText(SetsPrograms.getMusterPfadFFmpeg());
                });
                hyperlink.setOnAction(a -> {
                    try {
                        MTOpen.openURL(Const.ADRESSE_WEBSITE_FFMPEG);
                    } catch (Exception e) {
                        Log.errorLog(976420301, e);
                    }
                });
                break;
            case VLC:
            default:
                text = new Text("Pfad zum VLC-Player auswählen");
                hyperlink = new Hyperlink(Const.ADRESSE_WEBSITE_VLC);
                property = vlcProp;
                btnFind.setOnAction(event -> {
                    Config.SYSTEM_PFAD_VLC.setValue("");
                    txtPlayer.setText(SetsPrograms.getMusterPfadVlc());
                });
                hyperlink.setOnAction(a -> {
                    try {
                        MTOpen.openURL(Const.ADRESSE_WEBSITE_VLC);
                    } catch (Exception e) {
                        Log.errorLog(701010205, e);
                    }
                });
                break;
        }
        text.setStyle("-fx-font-weight: bold");

        gridPane.add(text, 0, 0);
        txtPlayer.textProperty().addListener((observable, oldValue, newValue) -> {
            File file = new File(txtPlayer.getText());
            if (!file.exists() || !file.isFile()) {
                txtPlayer.setStyle(MTColor.DATEINAME_FEHLER.getCssBackground());
            } else {
                txtPlayer.setStyle("");
            }
        });
        txtPlayer.textProperty().bindBidirectional(property);
        gridPane.add(txtPlayer, 0, 1);

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.FileChooser(Daten.getInstance().primaryStage, txtPlayer);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        gridPane.add(btnFile, 1, 1);

        gridPane.add(btnFind, 2, 1);


        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);

        hBox.getChildren().addAll(new Label("Website"), hyperlink);
        gridPane.add(hBox, 0, 2, 3, 1);

        gridPane.getColumnConstraints().addAll(ccTxt);

        return gridPane;
    }


}
