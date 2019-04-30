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

package de.mtplayer.mtp.gui.tools.table;

import de.mtplayer.mLib.tools.CheckBoxCell;
import de.mtplayer.mLib.tools.MDate;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.MTColor;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.data.film.Film;
import de.mtplayer.mtp.controller.data.film.FilmSize;
import de.mtplayer.mtp.controller.data.film.FilmTools;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

public class TableFilm {

    private final ProgData progData;
    private final BooleanProperty geoMelden;
    private final BooleanProperty small;

    public TableFilm(ProgData progData) {
        this.progData = progData;
        geoMelden = ProgConfig.SYSTEM_MARK_GEO.getBooleanProperty();
        small = ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM.getBooleanProperty();
    }

    public TableColumn[] initFilmColumn(TableView table) {
        table.getColumns().clear();

        ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM.getStringProperty().addListener((observableValue, s, t1) -> table.refresh());
        MTColor.FILM_LIVESTREAM.colorProperty().addListener((a, b, c) -> table.refresh());
        MTColor.FILM_GEOBLOCK.colorProperty().addListener((a, b, c) -> table.refresh());
        MTColor.FILM_NEW.colorProperty().addListener((a, b, c) -> table.refresh());
        MTColor.FILM_HISTORY.colorProperty().addListener((a, b, c) -> table.refresh());

        final TableColumn<Film, Integer> nrColumn = new TableColumn<>("Nr");
        nrColumn.setCellValueFactory(new PropertyValueFactory<>("nr"));
        nrColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Film, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Film, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Film, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Film, String> startColumn = new TableColumn<>("");
        startColumn.setCellFactory(cellFactoryStart);
        startColumn.getStyleClass().add("alignCenter");
        startColumn.setPrefWidth(150);

        final TableColumn<Film, MDate> datumColumn = new TableColumn<>("Datum");
        datumColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        datumColumn.getStyleClass().add("alignCenter");

        final TableColumn<Film, String> timeColumn = new TableColumn<>("Zeit");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeColumn.getStyleClass().add("alignCenter");

        final TableColumn<Film, Integer> durationColumn = new TableColumn<>("Dauer [Min]");
        durationColumn.setCellFactory(cellFactoryDuration);
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinute"));
        durationColumn.getStyleClass().add("alignCenter");

        final TableColumn<Film, FilmSize> sizeColumn = new TableColumn<>("Größe [MB]");
//        sizeColumn.setCellFactory(cellFactorySize);
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("filmSize"));
        sizeColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Film, Boolean> hdColumn = new TableColumn<>("HD");
        hdColumn.setCellValueFactory(new PropertyValueFactory<>("hd"));
        hdColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
        hdColumn.getStyleClass().add("alignCenter");

        final TableColumn<Film, Boolean> utColumn = new TableColumn<>("UT");
        utColumn.setCellValueFactory(new PropertyValueFactory<>("ut"));
        utColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
        utColumn.getStyleClass().add("alignCenter");

//        final TableColumn<Film, String> descriptionColumn = new TableColumn<>("Beschreibung");
//        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        final TableColumn<Film, String> geoColumn = new TableColumn<>("Geo");
        geoColumn.setCellValueFactory(new PropertyValueFactory<>("geo"));
        geoColumn.getStyleClass().add("alignCenter");

        final TableColumn<Film, String> urlColumn = new TableColumn<>("URL");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        urlColumn.getStyleClass().add("alignCenterLeft");

        final TableColumn<Film, String> aboColumn = new TableColumn<>("Abo");
        aboColumn.setCellValueFactory(new PropertyValueFactory<>("aboName"));
        aboColumn.getStyleClass().add("alignCenterLeft");

//        final TableColumn<Film, Boolean> newColumn = new TableColumn<>("Neu");
//        newColumn.setCellValueFactory(new PropertyValueFactory<>("newFilm"));
//        newColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);

//        final TableColumn<Film, Boolean> shownColumn = new TableColumn<>("Gesehen");
//        shownColumn.setCellValueFactory(new PropertyValueFactory<>("shown"));
//        shownColumn.setCellFactory(cellFactoryShown);

//        final TableColumn<Film, Boolean> doubleColumn = new TableColumn<>("Doppelt");
//        doubleColumn.setCellValueFactory(new PropertyValueFactory<>("doubleUrl"));
//        doubleColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
//        doubleColumn.getStyleClass().add("alignCenter");
//
//        final TableColumn<Film, Boolean> actHistColumn = new TableColumn<>("akt. gesehen");
//        actHistColumn.setCellValueFactory(new PropertyValueFactory<>("actHist"));
//        actHistColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);
//        actHistColumn.getStyleClass().add("alignCenter");

//        final TableColumn<Film, Boolean> futureColumn = new TableColumn<>("Zukunft");
//        futureColumn.setCellValueFactory(new PropertyValueFactory<>("inFuture"));
//        futureColumn.setCellFactory(new PCheckBoxCell().cellFactoryBool);

        addRowFact(table);

        return new TableColumn[]{
                nrColumn,
                senderColumn, themeColumn, titleColumn,
                startColumn,
                datumColumn, timeColumn, durationColumn, sizeColumn,
                hdColumn, utColumn,
//                descriptionColumn,
                geoColumn,
                urlColumn, aboColumn,
//                newColumn,
//                shownColumn,
//                doubleColumn, actHistColumn,
//                futureColumn
        };

    }

    private void addRowFact(TableView<Film> table) {

        table.setRowFactory(tableview -> new TableRow<Film>() {
            @Override
            public void updateItem(Film film, boolean empty) {
                super.updateItem(film, empty);

                if (film == null || empty) {
                    setStyle("");
                } else {
                    if (film.getTheme().equals(FilmTools.THEME_LIVE)) {
                        // livestream
                        for (int i = 0; i < getChildren().size(); i++) {
                            getChildren().get(i).setStyle(MTColor.FILM_LIVESTREAM.getCssFontBold());
                        }

                    } else if (geoMelden.get() && film.isGeoBlocked()) {
                        // geogeblockt
                        for (int i = 0; i < getChildren().size(); i++) {
                            getChildren().get(i).setStyle("");
                            getChildren().get(i).setStyle(MTColor.FILM_GEOBLOCK.getCssFontBold());
                        }

                    } else if (film.isNewFilm()) {
                        // neue Filme
                        for (int i = 0; i < getChildren().size(); i++) {
                            getChildren().get(i).setStyle(MTColor.FILM_NEW.getCssFont());
                        }

                    } else {
                        for (int i = 0; i < getChildren().size(); i++) {
                            getChildren().get(i).setStyle("");
                        }
                    }

                    if (film.isBookmark()) {
                        setStyle(MTColor.FILM_BOOKMARK.getCssBackgroundSel());

                    } else if (film.isShown()) {
                        setStyle(MTColor.FILM_HISTORY.getCssBackgroundSel());

                    } else {
                        setStyle("");
                    }

                }
            }
        });

    }

    private Callback<TableColumn<Film, String>, TableCell<Film, String>> cellFactoryStart
            = (final TableColumn<Film, String> param) -> {

        final TableCell<Film, String> cell = new TableCell<>() {

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Film film = getTableView().getItems().get(getIndex());

                final HBox hbox = new HBox();
                hbox.setSpacing(4);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button btnPlay;
                final Button btnSave;
//                final Button btnBookmark;


                btnPlay = new Button("");
                btnPlay.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_FILM_PLAY));

                btnSave = new Button("");
                btnSave.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_FILM_SAVE));

//                btnBookmark = new Button("");
//                btnBookmark.setGraphic(new ImageView(ProgIcons.IMAGE_TABLE_FILM_BOOKMARK));

                if (small.get()) {
                    btnPlay.setMaxHeight(18);
                    btnPlay.setMinHeight(18);
                    btnSave.setMaxHeight(18);
                    btnSave.setMinHeight(18);
//                    btnBookmark.setMaxHeight(18);
//                    btnBookmark.setMinHeight(18);
                }

                btnPlay.setOnAction((ActionEvent event) -> {
                    FilmTools.playFilm(film, null);
                });
                btnSave.setOnAction(event -> {
                    ProgData.getInstance().filmlist.saveFilm(film, null);
                });
//                btnBookmark.setOnAction(event -> {
//                    FilmTools.bookmarkFilm(progData, film, !film.isBookmark());
//                });
                hbox.getChildren().addAll(btnPlay, btnSave/*, btnBookmark*/);
                setGraphic(hbox);
            }
        };
        return cell;
    };


    private Callback<TableColumn<Film, Integer>, TableCell<Film, Integer>> cellFactoryDuration
            = (final TableColumn<Film, Integer> param) -> {

        final TableCell<Film, Integer> cell = new TableCell<>() {

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                if (item == 0) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(null);
                    setText(item + "");
                }

            }
        };
        return cell;
    };

}
