package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.*;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkData;
import de.p2tools.mtplayer.controller.data.bookmark.BookmarkFactory;
import de.p2tools.mtplayer.controller.data.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.data.film.FilmPlayFactory;
import de.p2tools.mtplayer.controller.data.film.FilmSaveFactory;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.tools.date.P2Date;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;

public class TableBookmarkFactory {
    private TableBookmarkFactory() {
    }

    public static void columnFactoryList(TableColumn<BookmarkData, Boolean> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                if (item) {
                    setText("Audio");
                } else {
                    setText("Film");
                }
                BookmarkData bookmarkData = getTableView().getItems().get(getIndex());
                set(bookmarkData, this);
            }
        });
    }

    public static void columnFactoryString(TableColumn<BookmarkData, String> column) {
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
                BookmarkData bookmarkData = getTableView().getItems().get(getIndex());
                set(bookmarkData, this);
            }
        });
    }

    public static void columnFactoryP2Date(TableColumn<BookmarkData, P2Date> column) {
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
                BookmarkData bookmarkData = getTableView().getItems().get(getIndex());
                set(bookmarkData, this);
            }
        });
    }

    public static void columnFactoryAge(TableColumn<BookmarkData, P2Date> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(P2Date item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                int age = item.diffInDays();
                setText(age + "");
                BookmarkData bookmarkData = getTableView().getItems().get(getIndex());
                set(bookmarkData, this);
            }
        });
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
                final FilmDataMTP filmDataMTP = bookmarkData.getFilmData();
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

                if (ProgConfig.BOOKMARK_DIALOG_SMALL_TABLE_ROW.get()) {
                    btnPlay.setMaxHeight(18);
                    btnPlay.setMinHeight(18);
                    btnSave.setMaxHeight(18);
                    btnSave.setMinHeight(18);
                    btnBookmark.setMaxHeight(18);
                    btnBookmark.setMinHeight(18);
                }

                btnPlay.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    // todo audio
                    if (bookmarkData.getFilmData() == null) {
                        FilmDataMTP filmData = new FilmDataMTP();
                        filmData.arr[FilmData.FILM_TITLE] = bookmarkData.getTitle();
                        filmData.arr[FilmData.FILM_THEME] = bookmarkData.getTheme();
                        filmData.arr[FilmData.FILM_URL] = bookmarkData.getUrl();
                        FilmPlayFactory.playFilm(bookmarkData.isAudio(), filmData);
                    } else {
                        FilmPlayFactory.playFilm(bookmarkData.isAudio(), filmDataMTP);
                    }

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                btnSave.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    if (bookmarkData.getFilmData() == null) {
                        FilmDataMTP filmData = new FilmDataMTP();
                        filmData.arr[FilmData.FILM_TITLE] = bookmarkData.getTitle();
                        filmData.arr[FilmData.FILM_THEME] = bookmarkData.getTheme();
                        filmData.arr[FilmData.FILM_URL] = bookmarkData.getUrl();
                        FilmSaveFactory.saveFilm(bookmarkData.isAudio(), filmData);
                    } else {
                        FilmSaveFactory.saveFilm(bookmarkData.isAudio(), filmDataMTP);
                    }

                    getTableView().refresh();
                    getTableView().requestFocus();
                });

                btnBookmark.setOnAction(e -> {
                    getTableView().getSelectionModel().clearSelection();
                    getTableView().getSelectionModel().select(getIndex());

                    BookmarkFactory.removeBookmark(bookmarkData);
                    getTableView().refresh();
                    getTableView().requestFocus();
                    ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_BOOKMARK_CHANGED);
                });
                hbox.getChildren().addAll(btnPlay, btnSave, btnBookmark);
                setGraphic(hbox);

                set(bookmarkData, this);
            }
        });
    }

    public static void set(BookmarkData bookmarkData, TableCell tableCell) {
        if (bookmarkData.getFilmData() == null) {

        } else if (ProgConfig.SYSTEM_MARK_GEO.get() && bookmarkData.getFilmData().isGeoBlocked()) {
            // geoGeblockt
            tableCell.setStyle(ProgColorList.FILM_GEOBLOCK.getCssFontBold());

        } else if (bookmarkData.getFilmData().isNewFilm()) {
            // neuer Film
            tableCell.setStyle(ProgColorList.FILM_NEW.getCssFont());

        } else {
            tableCell.setStyle("");
        }
    }
}
