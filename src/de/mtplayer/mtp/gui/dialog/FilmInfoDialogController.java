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
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 */
public class FilmInfoDialogController extends MTDialog {

    final private TextFlow textFlow;
    final private Text[] lbl = new Text[FilmXml.MAX_ELEM];
    final private Text[] txt = new Text[FilmXml.MAX_ELEM];

    final GridPane gridPane = new GridPane();
    Button btnOk = new Button("Ok");
    private final ProgData progData;


    private VBox vBoxDialog = new VBox();
    private VBox vboxCont = new VBox();
    private TilePane tilePaneOk = new TilePane();
    ScrollPane scrollPane = new ScrollPane();
    HBox hBoxUrl = new HBox();
    HBox hBoxWebsite = new HBox();


    public FilmInfoDialogController(ProgData progData) {
        super(null, ProgConfig.SYSTEM_SIZE_DIALOG_FILMINFO,
                "Filminfos", false);
        initDialog();

        this.progData = progData;
        this.textFlow = new TextFlow();

        tilePaneOk.getChildren().addAll(btnOk);
        init(vBoxDialog);
    }

    private void initDialog() {
        vBoxDialog.setSpacing(10);
        vBoxDialog.setPadding(new Insets(10));


        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        VBox.setVgrow(vboxCont, Priority.ALWAYS);
        scrollPane.setContent(vboxCont);

        tilePaneOk.setHgap(10);
        tilePaneOk.setAlignment(Pos.CENTER_RIGHT);

        VBox vBox = new VBox();
        vBox.getStyleClass().add("dialog-filminfo");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        vBox.getChildren().add(scrollPane);
//        vBox.getChildren().add(vboxCont);


        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBoxDialog.getChildren().addAll(vBox, tilePaneOk);
    }


    public void showFilmInfo() {
        showDialog();
//        scrollPane.setHvalue(0.01);
    }


    public void set(Film film) {
        Platform.runLater(() -> {

            hBoxUrl.getChildren().clear();
            hBoxWebsite.getChildren().clear();
            for (int i = 0; i < FilmXml.MAX_ELEM; ++i) {
                if (film == null) {
                    txt[i].setText("");
                } else {
                    switch (i) {
                        case FilmXml.FILM_NR:
                            txt[i].setText(film.getNr() + "");
                            break;
                        case FilmXml.FILM_URL:
                            hBoxUrl.getChildren().add(new PHyperlink(film.arr[FilmXml.FILM_URL],
                                    ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty()));
                            break;
                        case FilmXml.FILM_WEBSITE:
                            hBoxWebsite.getChildren().add(new PHyperlink(film.arr[FilmXml.FILM_WEBSITE],
                                    ProgConfig.SYSTEM_PROG_OPEN_URL.getStringProperty()));
                            break;
                        default:
                            txt[i].setText(film.arr[i]);
                    }
                }
            }

//            scrollPane.setHvalue(0.01);
        });
    }

    @Override
    public void make() {
        btnOk.setOnAction(a -> close());

        vboxCont.getChildren().add(gridPane);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setMinWidth(Control.USE_PREF_SIZE);
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;
        for (int i = 0; i < FilmXml.MAX_ELEM; ++i) {

            lbl[i] = new Text(FilmXml.COLUMN_NAMES[i]);
            lbl[i].setFont(Font.font(null, FontWeight.BOLD, -1));
//            GridPane.setValignment(lbl[i], VPos.CENTER);
            txt[i] = new Text("");

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
                    gridPane.add(lbl[i], 0, row);
                    gridPane.add(hBoxUrl, 1, row++);
//                    hBoxUrl.setMinWidth(0);
//                    hBoxUrl.setMaxWidth(Region.USE_COMPUTED_SIZE);
                    break;
                case FilmXml.FILM_WEBSITE:
                    gridPane.add(lbl[i], 0, row);
                    gridPane.add(hBoxWebsite, 1, row++);
//                    hBoxWebsite.setMinWidth(0);
                    break;
                case FilmXml.FILM_DESCRIPTION:
//                    final TextArea ta = new TextArea();
//                    ta.setMinWidth(0);
//                    ta.setEditable(false);
//                    ta.setWrapText(true);
//                    txt[i] = ta;
                default:
                    gridPane.add(lbl[i], 0, row);

                    HBox hBox = new HBox();
                    hBox.getChildren().add(txt[i]);
                    GridPane.setHgrow(txt[i], Priority.ALWAYS);
                    txt[i].wrappingWidthProperty().bind(vBoxDialog.widthProperty().subtract(250));
                    gridPane.add(hBox, 1, row++);

                    final int ii = i;
                    txt[i].setOnContextMenuRequested(event -> getMenu(txt[ii].getText()).show(txt[ii], event.getScreenX(), event.getScreenY()));

            }
        }
//        scrollPane.setHvalue(0.01);
//        scrollPane.hvalueProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                System.out.println("     " + scrollPane.getHvalue());
//            }
//        });
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
