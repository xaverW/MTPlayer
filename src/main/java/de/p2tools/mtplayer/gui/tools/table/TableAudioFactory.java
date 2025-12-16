package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkFactory;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.data.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

public class TableAudioFactory {
    private TableAudioFactory() {
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
                btnPlay.getStyleClass().addAll("pFuncBtn", "btnTable");
                btnPlay.setGraphic(PIconFactory.PICON.TABLE_FILM_PLAY.getFontIcon());
                btnPlay.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmPlayFactory.playFilm(true, film);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                btnSave = new Button("");
                btnSave.getStyleClass().addAll("pFuncBtn", "btnTable");
                btnSave.setGraphic(PIconFactory.PICON.TABLE_FILM_SAVE.getFontIcon());
                btnSave.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmSaveFactory.saveFilm(true, film);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                btnBookmark = new Button("");
                btnBookmark.getStyleClass().addAll("pFuncBtn", "btnTable");
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
                        BookmarkFactory.addBookmark(true, film);
                    }

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                if (ProgConfig.SYSTEM_SMALL_TABLE_ROW_AUDIO.get()) {
                    btnPlay.setMaxHeight(Table.ROW_HEIGHT_MIN);
                    btnPlay.setMinHeight(Table.ROW_HEIGHT_MIN);
                    btnSave.setMaxHeight(Table.ROW_HEIGHT_MIN);
                    btnSave.setMinHeight(Table.ROW_HEIGHT_MIN);
                    btnBookmark.setMaxHeight(Table.ROW_HEIGHT_MIN);
                    btnBookmark.setMinHeight(Table.ROW_HEIGHT_MIN);

                } else {
                    btnPlay.setMaxHeight(Table.ROW_HEIGHT_MAX);
                    btnPlay.setMinHeight(Table.ROW_HEIGHT_MAX);
                    btnSave.setMaxHeight(Table.ROW_HEIGHT_MAX);
                    btnSave.setMinHeight(Table.ROW_HEIGHT_MAX);
                    btnBookmark.setMaxHeight(Table.ROW_HEIGHT_MAX);
                    btnBookmark.setMinHeight(Table.ROW_HEIGHT_MAX);

                    btnPlay.setGraphic(PIconFactory.PICON.TABLE_FILM_PLAY_BIG.getFontIcon());
                    btnSave.setGraphic(PIconFactory.PICON.TABLE_FILM_SAVE_BIG.getFontIcon());
                    if (film.isBookmark()) {
                        btnBookmark.setGraphic(PIconFactory.PICON.TABLE_BOOKMARK_DEL_BIG.getFontIcon());
                    } else {
                        btnBookmark.setGraphic(PIconFactory.PICON.TABLE_BOOKMARK_ADD_BIG.getFontIcon());
                    }
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
