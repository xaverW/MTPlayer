package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkFactory;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.data.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.mediathek.film.FilmSize;
import de.p2tools.p2lib.tools.date.P2Date;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

public class TableFilmFactory {
    private TableFilmFactory() {

    }

    public static void columnFactoryString(TableColumn<FilmDataMTP, String> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item);
                FilmDataMTP film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryInteger(TableColumn<FilmDataMTP, Integer> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
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

                FilmDataMTP film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryBoolean(TableColumn<FilmDataMTP, Boolean> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                setAlignment(Pos.CENTER);
                CheckBox box = new CheckBox();
                box.setMaxHeight(6);
                box.setMinHeight(6);
                box.setPrefSize(6, 6);
                box.setDisable(true);
                box.getStyleClass().add("checkbox-table");
                box.setSelected(item);
                setGraphic(box);

                FilmDataMTP film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryP2Date(TableColumn<FilmDataMTP, P2Date> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(P2Date item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item.toString());
                FilmDataMTP film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryFilmSize(TableColumn<FilmDataMTP, FilmSize> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(FilmSize item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item.toString());
                FilmDataMTP film = getTableView().getItems().get(getIndex());
                set(film, this);
            }
        });
    }

    public static void columnFactoryButton(TableColumn<FilmDataMTP, String> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                final FilmDataMTP film = getTableView().getItems().get(getIndex());
                final HBox hbox = new HBox();
                hbox.setSpacing(4);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));


                final Button btnPlay;
                final Button btnSave;
                final Button btnBookmark;

                btnPlay = new Button("");
                btnPlay.getStyleClass().addAll("btnProgMenu", "btnFuncTable");
                btnPlay.setGraphic(PIconFactory.PICON.TABLE_FILM_PLAY.getFontIcon());
                btnPlay.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmPlayFactory.playFilm(false, film);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                btnSave = new Button("");
                btnSave.getStyleClass().addAll("btnProgMenu", "btnFuncTable");
                btnSave.setGraphic(PIconFactory.PICON.TABLE_FILM_SAVE.getFontIcon());
                btnSave.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmSaveFactory.saveFilm(false, film);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                btnBookmark = new Button("");
                btnBookmark.getStyleClass().addAll("btnProgMenu", "btnFuncTable");
                if (film.isBookmark()) {
                    btnBookmark.setGraphic(PIconFactory.PICON.TABLE_BOOKMARK_DEL.getFontIcon());
                } else {
                    btnBookmark.setGraphic(PIconFactory.PICON.TABLE_BOOKMARK_ADD.getFontIcon());
                }
                btnBookmark.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    if (film.isBookmark()) {
                        BookmarkFactory.removeBookmark(film);
                    } else {
                        BookmarkFactory.addBookmark(false, film);
                    }

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                if (ProgConfig.SYSTEM_SMALL_TABLE_ROW_FILM.get()) {
                    btnPlay.setMaxHeight(18);
                    btnPlay.setMinHeight(18);
                    btnSave.setMaxHeight(18);
                    btnSave.setMinHeight(18);
                    btnBookmark.setMaxHeight(18);
                    btnBookmark.setMinHeight(18);
                    btnPlay.setGraphic(PIconFactory.PICON.TABLE_FILM_PLAY.getFontIcon());
                } else {
                    btnPlay.setGraphic(PIconFactory.PICON.TABLE_FILM_PLAY_BIG.getFontIcon());
                }

                hbox.getChildren().addAll(btnPlay, btnSave, btnBookmark);
                setGraphic(hbox);

                set(film, this);
            }
        });
    }

    public static void set(FilmDataMTP film, TableCell tableCell) {
        if (film.isLive()) {
            // livestream
            tableCell.setStyle(ProgColorList.FILM_LIVESTREAM.getCssFontBold());

        } else if (ProgConfig.SYSTEM_MARK_GEO.get() && film.isGeoBlocked()) {
            // geoGeblockt
            tableCell.setStyle(ProgColorList.FILM_GEOBLOCK.getCssFontBold());

        } else if (film.isNewFilm()) {
            // neuer Film
            tableCell.setStyle(ProgColorList.FILM_NEW.getCssFont());

        } else {
            tableCell.setStyle("");
        }
    }
}
