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
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmXml;
import de.p2tools.p2Lib.dialog.PDialog;
import de.p2tools.p2Lib.guiTools.PHyperlink;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 */
public class FilmInfoDialogController extends PDialog {

    private final Text[] textTitle = new Text[FilmXml.MAX_ELEM];
    private final Text[] textCont = new Text[FilmXml.MAX_ELEM];

    private final GridPane gridPane = new GridPane();
    private final Button btnOk = new Button("Ok");
    private final ProgData progData;


    private final VBox vBoxDialog = new VBox();
    private final VBox vBoxCont = new VBox();
    private final TilePane tilePaneOk = new TilePane();
    private final HBox hBoxUrl = new HBox();
    private final HBox hBoxWebsite = new HBox();


    public FilmInfoDialogController(ProgData progData) {
        super(null, ProgConfig.SYSTEM_SIZE_DIALOG_FILMINFO.getStringProperty(),
                "Filminfos", false);
        this.progData = progData;

        initDialog();
        tilePaneOk.getChildren().addAll(btnOk);
        init(vBoxDialog);
    }

    private void initDialog() {
        vBoxDialog.setSpacing(10);
        vBoxDialog.setPadding(new Insets(10));


        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(vBoxCont);

        tilePaneOk.setHgap(10);
        tilePaneOk.setAlignment(Pos.CENTER_RIGHT);

        VBox vBox = new VBox();
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.getStyleClass().add("dialog-filminfo");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        vBox.getChildren().add(scrollPane);

        vBoxDialog.getChildren().addAll(vBox, tilePaneOk);
    }


    public void showFilmInfo() {
        showDialog();
    }


    public void set(Film film) {
        Platform.runLater(() -> {

            hBoxUrl.getChildren().clear();
            hBoxWebsite.getChildren().clear();
            for (int i = 0; i < FilmXml.MAX_ELEM; ++i) {
                if (film == null) {
                    textCont[i].setText("");
                } else {
                    switch (i) {
                        case FilmXml.FILM_NR:
                            textCont[i].setText(film.getNr() + "");
                            break;
                        case FilmXml.FILM_URL:
                            hBoxUrl.getChildren().add(new PHyperlink(film.arr[FilmXml.FILM_URL],
                                    ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN));
                            break;
                        case FilmXml.FILM_WEBSITE:
                            hBoxWebsite.getChildren().add(new PHyperlink(film.arr[FilmXml.FILM_WEBSITE],
                                    ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN));
                            break;
                        default:
                            textCont[i].setText(film.arr[i]);
                    }
                }
            }
        });
    }

    @Override
    public void make() {
        btnOk.setOnAction(a -> close());
        vBoxCont.getChildren().add(gridPane);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;
        for (int i = 0; i < FilmXml.MAX_ELEM; ++i) {

            textTitle[i] = new Text(FilmXml.COLUMN_NAMES[i]);
            textTitle[i].setFont(Font.font(null, FontWeight.BOLD, -1));
            textCont[i] = new Text("");

            switch (i) {
                case FilmXml.FILM_DATE_LONG:
                case FilmXml.FILM_PLAY:
                case FilmXml.FILM_RECORD:
                case FilmXml.FILM_URL_AUTH:
                case FilmXml.FILM_URL_HD:
                case FilmXml.FILM_URL_HISTORY:
                case FilmXml.FILM_URL_SMALL:
                case FilmXml.FILM_URL_RTMP:
                case FilmXml.FILM_URL_RTMP_HD:
                case FilmXml.FILM_URL_RTMP_SMALL:
                case FilmXml.FILM_URL_SUBTITLE:
                case FilmXml.FILM_NEW:
                    // bis hier nicht anzeigen
                    break;
                case FilmXml.FILM_URL:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(hBoxUrl, 1, row++);
                    break;
                case FilmXml.FILM_WEBSITE:
                    gridPane.add(textTitle[i], 0, row);
                    gridPane.add(hBoxWebsite, 1, row++);
                    break;
                default:
                    gridPane.add(textTitle[i], 0, row);

                    HBox hBox = new HBox();
                    hBox.getChildren().add(textCont[i]);
                    GridPane.setHgrow(textCont[i], Priority.ALWAYS);
                    textCont[i].wrappingWidthProperty().bind(vBoxDialog.widthProperty().subtract(250));
                    gridPane.add(hBox, 1, row++);

                    final int ii = i;
                    textCont[i].setOnContextMenuRequested(event -> getMenu(textCont[ii].getText()).show(textCont[ii], event.getScreenX(), event.getScreenY()));

            }
        }
    }

    private ContextMenu getMenu(String url) {
        final ContextMenu contextMenu = new ContextMenu();

        MenuItem resetTable = new MenuItem("kopieren");
        resetTable.setOnAction(a -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(url);
            clipboard.setContent(content);
        });
        contextMenu.getItems().addAll(resetTable);
        return contextMenu;
    }
}
