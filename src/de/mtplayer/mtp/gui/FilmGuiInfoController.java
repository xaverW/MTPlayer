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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class FilmGuiInfoController {
    private final TextArea textArea = new TextArea();
    private final Button btnReset = new Button("@");
    private final Text textTitle = new Text("");
    private final HBox hBox = new HBox(10);

    private Film film = null;
    private String oldDescription = "";

    public FilmGuiInfoController(AnchorPane anchorPane) {
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(textArea, btnReset);
        StackPane.setAlignment(btnReset, Pos.BOTTOM_RIGHT);
        stackPane.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        btnReset.setOnAction(a -> resetFilmDescription());
        btnReset.setTooltip(new Tooltip("Beschreibung zurücksetzen"));
        btnReset.setVisible(false);

        textTitle.setFont(Font.font(null, FontWeight.BOLD, -1));
        hBox.setAlignment(Pos.CENTER_LEFT);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(4);
        textArea.textProperty().addListener((a, b, c) -> setFilmDescription());

        VBox vBox = new VBox(10);
        vBox.getChildren().add(textTitle);
        vBox.getChildren().add(stackPane);
        vBox.getChildren().add(hBox);

        AnchorPane.setLeftAnchor(vBox, 10.0);
        AnchorPane.setBottomAnchor(vBox, 10.0);
        AnchorPane.setRightAnchor(vBox, 10.0);
        AnchorPane.setTopAnchor(vBox, 10.0);
        anchorPane.getChildren().add(vBox);
    }

    public void setFilm(Film film) {
        if (film == null) {
            this.film = null;
            textTitle.setText("");
            textArea.setText("");
            oldDescription = "";
            btnReset.setVisible(false);
            return;
        }

        this.film = film;
        hBox.getChildren().clear();

        textTitle.setText(film.arr[FilmXml.FILM_CHANNEL] + "  -  " + film.arr[FilmXml.FILM_TITLE]);
        textArea.setText(film.getDescription());
        oldDescription = film.getDescription();
        btnReset.setVisible(false);

        if (!film.arr[FilmXml.FILM_WEBSITE].isEmpty()) {
            PHyperlink hyperlink = new PHyperlink(film.arr[FilmXml.FILM_WEBSITE],
                    ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
            hBox.getChildren().addAll(new Label(" zur Website: "), hyperlink);
        }
    }

    private void setFilmDescription() {
        if (film != null) {
            btnReset.setVisible(true);
            film.setDescription(textArea.getText());
        }
    }

    private void resetFilmDescription() {
        if (film != null) {
            film.setDescription(oldDescription);
            textArea.setText(film.getDescription());
            btnReset.setVisible(false);
        }
    }

}

