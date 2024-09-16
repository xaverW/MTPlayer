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

package de.p2tools.mtplayer;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.controller.worker.Busy;
import de.p2tools.mtplayer.gui.*;
import de.p2tools.mtplayer.gui.filter.SearchFast;
import de.p2tools.p2lib.guitools.pmask.P2MaskerPane;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadListener;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

public class MTPlayerController extends StackPane {

    public enum PANE_SHOWN {FILM, LIVE_FILM, DOWNLOAD, ABO}

    public static PANE_SHOWN paneShown = null;

    private final Button btnFilmlist = new Button("Filmliste");
    private final Button btnFilm = new Button("Filme");
    private final Button btnLive = new Button("Live");
    private final Button btnDownload = new Button("Downloads");
    private final Button btnAbo = new Button("Abos");
    private final SearchFast searchFast = new SearchFast();

    private final BorderPane borderPane = new BorderPane();
    private final StackPane stackPaneCont = new StackPane();

    private P2MaskerPane maskerPane = new P2MaskerPane();
    private StatusBarController statusBarController;

    private SplitPane splitPaneFilm;
    private SplitPane splitPaneLiveFilm;
    private SplitPane splitPaneDownload;
    private SplitPane splitPaneAbo;

    private final ProgData progData;
    private final FilmGui filmGui = new FilmGui();
    private final LiveFilmGui liveFilmGui = new LiveFilmGui();
    private final DownloadGui downloadGui = new DownloadGui();
    private final AboGui aboGui = new AboGui();

    public MTPlayerController() {
        progData = ProgData.getInstance();
        init();
    }

    private void init() {
        try {
            // Toolbar
            TilePane tilePane = new TilePane();
            tilePane.setPrefColumns(4);
            tilePane.setHgap(15);
            tilePane.setPadding(new Insets(0));
            tilePane.setAlignment(Pos.CENTER);
            tilePane.getChildren().addAll(btnFilm, btnLive, btnDownload, btnAbo);

            HBox hBoxTop = new HBox();
            hBoxTop.setPadding(new Insets(2, 10, 2, 10));
            hBoxTop.setSpacing(10);
            hBoxTop.setAlignment(Pos.CENTER);
            HBox.setHgrow(tilePane, Priority.SOMETIMES);
            HBox.setHgrow(searchFast, Priority.ALWAYS);
            hBoxTop.getChildren().addAll(btnFilmlist, tilePane, searchFast, new MTPlayerMenu());

            // Center
            splitPaneFilm = filmGui.pack();
            splitPaneLiveFilm = liveFilmGui.pack();
            splitPaneDownload = downloadGui.pack();
            splitPaneAbo = aboGui.pack();
            stackPaneCont.getChildren().addAll(splitPaneFilm, splitPaneLiveFilm, splitPaneDownload, splitPaneAbo);

            VBox vBox = new VBox();
            vBox.getChildren().addAll(stackPaneCont, ProgData.busy.getBusyHbox(Busy.BUSY_SRC.GUI));
            VBox.setVgrow(stackPaneCont, Priority.ALWAYS);

            // Statusbar
            statusBarController = new StatusBarController(progData);
            statusBarController.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_ON);
            statusBarController.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_ON);

            // Gui zusammenbauen
            borderPane.setTop(hBoxTop);
            borderPane.setCenter(vBox);
            borderPane.setBottom(statusBarController);
            this.setPadding(new Insets(0));
            this.getChildren().addAll(borderPane, maskerPane);

            initMaskerPane();
            initButton();
            if (ProgData.autoMode) {
                // dann die Downloads anzeigen
                selPanelDownload();
            } else {
                selPanelFilm();
            }
        } catch (Exception ex) {
            P2Log.errorLog(597841023, ex);
        }
    }

    private void initMaskerPane() {
        StackPane.setAlignment(maskerPane, Pos.CENTER);
        progData.maskerPane = maskerPane;
        maskerPane.setPadding(new Insets(4, 1, 1, 1));
        maskerPane.toFront();
        Button btnStop = maskerPane.getButton();
        maskerPane.setButtonText("");
        btnStop.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnStop.setOnAction(a -> LoadFilmFactory.getInstance().loadFilmlist.setStop(true));
    }

    private void initButton() {
        btnFilmlist.setMinWidth(Region.USE_PREF_SIZE);
        btnFilmlist.getStyleClass().addAll("btnFunction", "btnFunc-4");
        btnFilmlist.setTooltip(new Tooltip("Eine neue Filmliste laden.\n" +
                "Wenn die Filmliste nicht zu alt ist, wird nur ein Update geladen.\n" +
                "Mit der rechten Maustaste wird immer die komplette Filmliste geladen."));
        btnFilmlist.setOnAction(e -> {
            LoadFilmFactory.getInstance().loadNewListFromWeb(false);
        });
        btnFilmlist.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                LoadFilmFactory.getInstance().loadNewListFromWeb(true);

            } else if (mouseEvent.getButton().equals(MouseButton.MIDDLE)) {
                progData.checkForNewFilmlist.check();
            }
        });
        progData.checkForNewFilmlist.foundNewListProperty().addListener((u, o, n) -> {
            if (progData.checkForNewFilmlist.isFoundNewList()) {
                btnFilmlist.getStyleClass().add("buttonLoadFilmlistNewList");
            } else {
                btnFilmlist.getStyleClass().remove("buttonLoadFilmlistNewList");
            }
        });
        LoadFilmFactory.getInstance().loadFilmlist.p2LoadNotifier.addListenerLoadFilmlist(new P2LoadListener() {
            @Override
            public void finished(P2LoadEvent event) {
                if (stackPaneCont.getChildren().isEmpty()) {
                    return;
                }
                setFocus();
            }
        });

        btnFilm.setTooltip(new Tooltip("Filme anzeigen"));
        btnFilm.setOnAction(e -> selPanelFilm());
        btnFilm.setMaxWidth(Double.MAX_VALUE);

        btnLive.setTooltip(new Tooltip("Live-Filme suchen"));
        btnLive.setOnAction(e -> selPanelLiveFilm());
        btnLive.setMaxWidth(Double.MAX_VALUE);
        btnLive.visibleProperty().bind(ProgConfig.LIVE_FILM_IS_VISIBLE);
        btnLive.managedProperty().bind(btnLive.visibleProperty());
        ProgConfig.LIVE_FILM_IS_VISIBLE.addListener((u, o, n) -> {
            if (n) {
                selPanelLiveFilm();
            } else {
                paneShown = PANE_SHOWN.LIVE_FILM; // sonst wird Filter ausgeblendet
                selPanelFilm();
            }
        });


        btnDownload.setTooltip(new Tooltip("Downloads anzeigen"));
        btnDownload.setOnAction(e -> selPanelDownload());
        btnDownload.setMaxWidth(Double.MAX_VALUE);

        btnAbo.setTooltip(new Tooltip("Abos anzeigen"));
        btnAbo.setOnAction(e -> selPanelAbo());
        btnAbo.setMaxWidth(Double.MAX_VALUE);

        btnFilm.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() || paneShown != PANE_SHOWN.FILM) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                MTPlayerFactory.setInfos();
            }
        });
        btnLive.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() || paneShown != PANE_SHOWN.LIVE_FILM) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                MTPlayerFactory.setInfos();
            }
        });
        btnDownload.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() || paneShown != PANE_SHOWN.DOWNLOAD) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                MTPlayerFactory.setInfos();
            }
        });
        btnAbo.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() || paneShown != PANE_SHOWN.ABO) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                MTPlayerFactory.setInfos();
            }
        });
    }

    private void selPanelFilm() {
        if (paneShown == PANE_SHOWN.FILM) {
            // dann ist der 2. Klick
            MTPlayerFactory.setFilter();
            return;
        }

        paneShown = PANE_SHOWN.FILM;
        setButtonStyle();
        splitPaneFilm.toFront();
        progData.filmGuiController.isShown();
        statusBarController.setStatusbarIndex();
        ProgData.FILM_TAB_ON.setValue(Boolean.TRUE);
        ProgData.LIVE_FILM_TAB_ON.setValue(Boolean.FALSE);
        ProgData.DOWNLOAD_TAB_ON.setValue(Boolean.FALSE);
        ProgData.ABO_TAB_ON.setValue(Boolean.FALSE);
    }

    private void selPanelLiveFilm() {
        if (paneShown == PANE_SHOWN.LIVE_FILM) {
            // dann ist der 2. Klick
            MTPlayerFactory.setFilter();
            return;
        }

        paneShown = PANE_SHOWN.LIVE_FILM;
        setButtonStyle();
        splitPaneLiveFilm.toFront();
        progData.liveFilmGuiController.isShown();
        statusBarController.setStatusbarIndex();
        ProgData.FILM_TAB_ON.setValue(Boolean.FALSE);
        ProgData.LIVE_FILM_TAB_ON.setValue(Boolean.TRUE);
        ProgData.DOWNLOAD_TAB_ON.setValue(Boolean.FALSE);
        ProgData.ABO_TAB_ON.setValue(Boolean.FALSE);
    }

    private void selPanelDownload() {
        if (paneShown == PANE_SHOWN.DOWNLOAD) {
            // dann ist der 2. Klick
            MTPlayerFactory.setFilter();
            return;
        }

        paneShown = PANE_SHOWN.DOWNLOAD;
        setButtonStyle();
        splitPaneDownload.toFront();
        progData.downloadGuiController.isShown();
        statusBarController.setStatusbarIndex();
        ProgData.FILM_TAB_ON.setValue(Boolean.FALSE);
        ProgData.LIVE_FILM_TAB_ON.setValue(Boolean.FALSE);
        ProgData.DOWNLOAD_TAB_ON.setValue(Boolean.TRUE);
        ProgData.ABO_TAB_ON.setValue(Boolean.FALSE);
    }

    private void selPanelAbo() {
        if (paneShown == PANE_SHOWN.ABO) {
            // dann ist der 2. Klick
            MTPlayerFactory.setFilter();
            return;
        }

        paneShown = PANE_SHOWN.ABO;
        setButtonStyle();
        splitPaneAbo.toFront();
        progData.aboGuiController.isShown();
        statusBarController.setStatusbarIndex();
        ProgData.FILM_TAB_ON.setValue(Boolean.FALSE);
        ProgData.LIVE_FILM_TAB_ON.setValue(Boolean.FALSE);
        ProgData.DOWNLOAD_TAB_ON.setValue(Boolean.FALSE);
        ProgData.ABO_TAB_ON.setValue(Boolean.TRUE);
    }

    private void setButtonStyle() {
        if (ProgConfig.LIVE_FILM_IS_VISIBLE.get()) {
            setButtonStyleSmall();
        } else {
            setButtonStyleBig();
        }
    }

    private void setButtonStyleBig() {
        btnFilm.getStyleClass().clear();
        btnLive.getStyleClass().clear();
        btnDownload.getStyleClass().clear();
        btnAbo.getStyleClass().clear();

        if (paneShown == PANE_SHOWN.FILM) {
            searchFast.setVisible(true);
            btnFilm.getStyleClass().add("btnTabTop-sel");
        } else {
            searchFast.setVisible(false);
            btnFilm.getStyleClass().add("btnTabTop");
        }

        if (paneShown == PANE_SHOWN.LIVE_FILM) {
            btnLive.getStyleClass().add("btnTabTop-sel");
        } else {
            btnLive.getStyleClass().add("btnTabTop");
        }

        if (paneShown == PANE_SHOWN.DOWNLOAD) {
            btnDownload.getStyleClass().add("btnTabTop-sel");
        } else {
            btnDownload.getStyleClass().add("btnTabTop");
        }

        if (paneShown == PANE_SHOWN.ABO) {
            btnAbo.getStyleClass().add("btnTabTop-sel");
        } else {
            btnAbo.getStyleClass().add("btnTabTop");
        }
    }

    private void setButtonStyleSmall() {
        btnFilm.getStyleClass().clear();
        btnLive.getStyleClass().clear();
        btnDownload.getStyleClass().clear();
        btnAbo.getStyleClass().clear();

        if (paneShown == PANE_SHOWN.FILM) {
            searchFast.setVisible(true);
            btnFilm.getStyleClass().add("btnTabTopSmall-sel");
        } else {
            searchFast.setVisible(false);
            btnFilm.getStyleClass().add("btnTabTopSmall");
        }

        if (paneShown == PANE_SHOWN.LIVE_FILM) {
            btnLive.getStyleClass().add("btnTabTopSmall-sel");
        } else {
            btnLive.getStyleClass().add("btnTabTopSmall");
        }

        if (paneShown == PANE_SHOWN.DOWNLOAD) {
            btnDownload.getStyleClass().add("btnTabTopSmall-sel");
        } else {
            btnDownload.getStyleClass().add("btnTabTopSmall");
        }

        if (paneShown == PANE_SHOWN.ABO) {
            btnAbo.getStyleClass().add("btnTabTopSmall-sel");
        } else {
            btnAbo.getStyleClass().add("btnTabTopSmall");
        }
    }

    public void setFocus() {
        if (paneShown == PANE_SHOWN.FILM) {
            progData.filmGuiController.isShown();
        }
        if (paneShown == PANE_SHOWN.LIVE_FILM) {
            progData.filmGuiController.isShown();
        }
        if (paneShown == PANE_SHOWN.DOWNLOAD) {
            progData.downloadGuiController.isShown();
        }
        if (paneShown == PANE_SHOWN.ABO) {
            progData.aboGuiController.isShown();
        }
    }
}
