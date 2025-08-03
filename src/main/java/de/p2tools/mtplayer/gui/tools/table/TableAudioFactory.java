package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkFactory;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.data.film.FilmSaveFactory;
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
                btnPlay.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnPlay.setGraphic(ProgIcons.IMAGE_TABLE_FILM_PLAY.getImageView());
                btnPlay.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmPlayFactory.playFilm(film);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                btnSave = new Button("");
                btnSave.getStyleClass().addAll("btnFunction", "btnFuncTable");
                btnSave.setGraphic(ProgIcons.IMAGE_TABLE_FILM_SAVE.getImageView());
                btnSave.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    FilmSaveFactory.saveFilm(film);

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                btnBookmark = new Button("");
                btnBookmark.getStyleClass().addAll("btnFunction", "btnFuncTable");
                if (film.isBookmark()) {
                    btnBookmark.setGraphic(ProgIcons.IMAGE_TABLE_BOOKMARK_DEL.getImageView());
                } else {
                    btnBookmark.setGraphic(ProgIcons.IMAGE_TABLE_BOOKMARK.getImageView());
                }
                btnBookmark.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    if (film.isBookmark()) {
                        BookmarkFactory.removeBookmark(film);
                    } else {
                        BookmarkFactory.addBookmark(film);
                    }

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                if (ProgConfig.SYSTEM_SMALL_ROW_TABLE_AUDIO.get()) {
                    btnPlay.setMaxHeight(18);
                    btnPlay.setMinHeight(18);
                    btnSave.setMaxHeight(18);
                    btnSave.setMinHeight(18);
                    btnBookmark.setMaxHeight(18);
                    btnBookmark.setMinHeight(18);
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
