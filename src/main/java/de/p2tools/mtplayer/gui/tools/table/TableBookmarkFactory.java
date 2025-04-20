package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkData;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.film.FilmSaveFactory;
import de.p2tools.mtplayer.controller.film.FilmToolsFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

public class TableBookmarkFactory {
    private TableBookmarkFactory() {
    }

    public static void columnFactoryButton(TableColumn<BookmarkData, String> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                final BookmarkData bookmarkData = getTableView().getItems().get(getIndex());
                final FilmDataMTP filmDataMTP = bookmarkData.getFilmDataMTP();
                final HBox hbox = new HBox();
                hbox.setSpacing(4);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));


                final Button btnPlay;
                final Button btnSave;
                final Button btnBookmark;

                btnPlay = new Button("");
                btnPlay.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnPlay.setGraphic(ProgIcons.IMAGE_TABLE_FILM_PLAY.getImageView());

                btnSave = new Button("");
                btnSave.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnSave.setGraphic(ProgIcons.IMAGE_TABLE_FILM_SAVE.getImageView());

                btnBookmark = new Button("");
                btnBookmark.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnBookmark.setGraphic(ProgIcons.IMAGE_TABLE_BOOKMARK_DEL.getImageView());

                if (ProgConfig.SYSTEM_SMALL_ROW_TABLE_FILM.get()) { //todo
                    btnPlay.setMaxHeight(18);
                    btnPlay.setMinHeight(18);
                    btnSave.setMaxHeight(18);
                    btnSave.setMinHeight(18);
                    btnBookmark.setMaxHeight(18);
                    btnBookmark.setMinHeight(18);
                }

                btnPlay.setDisable(filmDataMTP == null);
                btnPlay.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmPlayFactory.playFilm(filmDataMTP);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                btnSave.setDisable(filmDataMTP == null);
                btnSave.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmSaveFactory.saveFilm(filmDataMTP);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                btnBookmark.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmToolsFactory.removeBookmark(bookmarkData);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });
                hbox.getChildren().addAll(btnPlay, btnSave, btnBookmark);
                setGraphic(hbox);

                set(bookmarkData, this);
            }
        });
    }

    public static void set(BookmarkData bookmarkData, TableCell tableCell) {
        if (bookmarkData.getFilmDataMTP() == null) {
            tableCell.setStyle(ProgColorList.FILM_GEOBLOCK.getCssFontBold());

        } else {
            tableCell.setStyle("");
        }
    }
}
