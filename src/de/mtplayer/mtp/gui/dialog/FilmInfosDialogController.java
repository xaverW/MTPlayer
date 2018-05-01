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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * FXML Controller class
 */
public class FilmInfosDialogController extends MTDialogExtra {

    Button btnOk = new Button("Ok");

    Label[] lbl = new Label[FilmXml.MAX_ELEM];
    TextInputControl[] txt = new TextInputControl[FilmXml.MAX_ELEM];


    final GridPane gridPane = new GridPane();
    private final ProgData progData;

    public FilmInfosDialogController(ProgData progData) {
        super(null, ProgConfig.SYSTEM_SIZE_DIALOG_FILMINFO,
                "Filminfos", false);

        this.progData = progData;
        getTilePaneOk().getChildren().addAll(btnOk);
        init(getvBoxDialog());
    }


    public void showFilmInfo() {
        showDialog();
    }


    public void set(Film film) {
        Platform.runLater(() -> {
            for (int i = 0; i < FilmXml.MAX_ELEM; ++i) {
                if (film == null) {
                    txt[i].setText("");
                } else {
                    switch (i) {
                        case FilmXml.FILM_NR:
                            txt[i].setText(film.getNr() + "");
                            break;
                        default:
                            txt[i].setText(film.arr[i]);
                    }
                }
            }
        });
    }

    @Override
    public void make() {
        btnOk.setOnAction(a -> close());
        int row = 0;
        getVboxCont().getChildren().add(gridPane);

        gridPane.setHgap(5);
        gridPane.setVgap(10);
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        for (int i = 0; i < FilmXml.MAX_ELEM; ++i) {
            lbl[i] = new Label(FilmXml.COLUMN_NAMES[i]);
            txt[i] = new TextField("");
            txt[i].setEditable(false);
            switch (i) {
                case FilmXml.FILM_DATUM_LONG:
                case FilmXml.FILM_ABSPIELEN:
                case FilmXml.FILM_AUFZEICHNEN:
                case FilmXml.FILM_URL_AUTH:
                case FilmXml.FILM_URL_HD:
                case FilmXml.FILM_URL_HISTORY:
                case FilmXml.FILM_URL_KLEIN:
                case FilmXml.FILM_URL_RTMP:
                case FilmXml.FILM_URL_RTMP_HD:
                case FilmXml.FILM_URL_RTMP_KLEIN:
                case FilmXml.FILM_URL_SUBTITLE:
                case FilmXml.FILM_NEU:
                    // bis hier nicht anzeigen
                    break;
                case FilmXml.FILM_BESCHREIBUNG:
                    final TextArea ta = new TextArea();
                    ta.setEditable(false);
                    ta.setWrapText(true);
                    txt[i] = ta;
                default:
                    GridPane.setHgrow(txt[i], Priority.ALWAYS);
                    gridPane.add(lbl[i], 0, row);
                    gridPane.add(txt[i], 1, row);
                    ++row;

            }
        }
    }

}
