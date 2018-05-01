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

package de.mtplayer.mtp.gui.dialog;

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mLib.tools.MLAlert;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.Optional;

public class MTAlert extends MLAlert {

    public static String showAlertFileCooser(String title, String header, String content, boolean dir) {
        return showAlertFileCooser(title, header, content, dir, true, "");
    }

    public static String showAlertFileCooser(String title, String header, String content, boolean dir, boolean txtArea, String startFile) {

        String ret = "";

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        dialog.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        if (txtArea) {
            TextArea textArea = new TextArea(content);
            textArea.setEditable(false);
            textArea.setWrapText(true);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);
            grid.add(textArea, 0, 0, 2, 1);
        } else {
            Label label = new Label(content);
            label.setWrapText(true);
            GridPane.setVgrow(label, Priority.ALWAYS);
            GridPane.setHgrow(label, Priority.ALWAYS);
            grid.add(label, 0, 0, 2, 1);
        }
        TextField txtFile = new TextField(startFile);
        GridPane.setVgrow(txtFile, Priority.ALWAYS);

        Button btnDest = new Button("");
        btnDest.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnDest.setOnAction(event -> {
            if (dir) {
                DirFileChooser.DirChooser(ProgData.getInstance().primaryStage, txtFile);
            } else {
                DirFileChooser.FileChooser(ProgData.getInstance().primaryStage, txtFile);
            }
        });

        grid.add(txtFile, 0, 1);
        grid.add(btnDest, 1, 1);

        ColumnConstraints c0 = new ColumnConstraints();
        grid.getColumnConstraints().addAll(c0);
        c0.setMinWidth(GridPane.USE_PREF_SIZE);
        c0.setHgrow(Priority.ALWAYS);

        dialog.getDialogPane().setContent(grid);

        ButtonType btnOk = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, btnCancel);

        Node okBtn = dialog.getDialogPane().lookupButton(btnOk);
        okBtn.setDisable(txtFile.getText().isEmpty());
        txtFile.textProperty().addListener((observable, oldValue, newValue) -> okBtn.setDisable(newValue.trim().isEmpty()));

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == btnCancel) {
                return "";
            }
        }

        return txtFile.getText();
    }

}
