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
import de.p2tools.mtplayer.gui.AboGuiPack;
import de.p2tools.mtplayer.gui.DownloadGuiPack;
import de.p2tools.mtplayer.gui.FilmGuiPack;
import de.p2tools.mtplayer.gui.StatusBarController;
import de.p2tools.p2lib.guitools.pmask.PMaskerPane;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerLoadFilmlist;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

public class MTPlayerController extends StackPane {

    public enum PANE_SHOWN {FILM, DOWNLOAD, ABO}

    public static PANE_SHOWN paneShown = null;

    private final Button btnFilmlist = new Button("Filmliste");
    private final Button btnFilm = new Button("Filme");
    private final Button btnDownload = new Button("Downloads");
    private final Button btnAbo = new Button("Abos");

    private final BorderPane borderPane = new BorderPane();
    private final StackPane stackPaneCont = new StackPane();

    private PMaskerPane maskerPane = new PMaskerPane();
    private StatusBarController statusBarController;

    private SplitPane splitPaneFilm;
    private SplitPane splitPaneDownload;
    private SplitPane splitPaneAbo;

    private final ProgData progData;
    private final FilmGuiPack filmGuiPack = new FilmGuiPack();
    private final DownloadGuiPack downloadGuiPack = new DownloadGuiPack();
    private final AboGuiPack aboGuiPack = new AboGuiPack();

    public MTPlayerController() {
        progData = ProgData.getInstance();
        init();
    }

    private void init() {
        try {
            // Toolbar
            HBox hBoxTop = new HBox();
            hBoxTop.setPadding(new Insets(10));
            hBoxTop.setSpacing(20);
            hBoxTop.setAlignment(Pos.CENTER);

            TilePane tilePaneFilmDownloadAbo = new TilePane();
            tilePaneFilmDownloadAbo.setHgap(20);
            tilePaneFilmDownloadAbo.setAlignment(Pos.CENTER);
            tilePaneFilmDownloadAbo.getChildren().addAll(btnFilm, btnDownload, btnAbo);
            HBox.setHgrow(tilePaneFilmDownloadAbo, Priority.ALWAYS);
            hBoxTop.getChildren().addAll(btnFilmlist, tilePaneFilmDownloadAbo, new MTPlayerMenu());

            // Center
            splitPaneFilm = filmGuiPack.pack();
            splitPaneDownload = downloadGuiPack.pack();
            splitPaneAbo = aboGuiPack.pack();
            stackPaneCont.getChildren().addAll(splitPaneFilm, splitPaneDownload, splitPaneAbo);

            // Statusbar
            statusBarController = new StatusBarController(progData);
            statusBarController.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_ON);
            statusBarController.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_ON);

            // Gui zusammenbauen
            borderPane.setTop(hBoxTop);
            borderPane.setCenter(stackPaneCont);
            borderPane.setBottom(statusBarController);
            this.setPadding(new Insets(0));
            this.getChildren().addAll(borderPane, maskerPane);

            initMaskerPane();
            initButton();
            selPanelFilm();
        } catch (Exception ex) {
            PLog.errorLog(597841023, ex);
        }
    }

    private void initMaskerPane() {
        StackPane.setAlignment(maskerPane, Pos.CENTER);
        progData.maskerPane = maskerPane;
        maskerPane.setPadding(new Insets(4, 1, 1, 1));
        maskerPane.toFront();
        Button btnStop = maskerPane.getButton();
        maskerPane.setButtonText("");
        btnStop.setGraphic(ProgIcons.Icons.ICON_BUTTON_STOP.getImageView());
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
        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                if (stackPaneCont.getChildren().size() == 0) {
                    return;
                }
                setFocus();
            }
        });

        btnFilm.setTooltip(new Tooltip("Filme anzeigen"));
        btnFilm.setOnAction(e -> selPanelFilm());
        btnFilm.setMaxWidth(Double.MAX_VALUE);

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
                ProgConfig.FILM_GUI_DIVIDER_ON.setValue(!ProgConfig.FILM_GUI_DIVIDER_ON.getValue());
            }
        });
        btnDownload.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() || paneShown != PANE_SHOWN.DOWNLOAD) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.setValue(!ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getValue());
            }
        });
        btnAbo.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() || paneShown != PANE_SHOWN.ABO) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.ABO_GUI_DIVIDER_ON.setValue(!ProgConfig.ABO_GUI_DIVIDER_ON.getValue());
            }
        });
    }

    private void selPanelFilm() {
        if (paneShown == PANE_SHOWN.FILM) {
            // dann ist der 2. Klick
            filmGuiPack.closeSplit();
            return;
        }

        paneShown = PANE_SHOWN.FILM;
        setButtonStyle();
        splitPaneFilm.toFront();
        progData.filmGuiController.isShown();
        statusBarController.setStatusbarIndex();
        ProgConfig.DOWNLOAD_TAB_ON.setValue(false);
        ProgConfig.ABO_TAB_ON.setValue(false);
        ProgConfig.FILM_TAB_ON.setValue(true);
    }

    private void selPanelDownload() {
        if (paneShown == PANE_SHOWN.DOWNLOAD) {
            // dann ist der 2. Klick
            downloadGuiPack.closeSplit();
            return;
        }

        paneShown = PANE_SHOWN.DOWNLOAD;
        setButtonStyle();
        splitPaneDownload.toFront();
        progData.downloadGuiController.isShown();
        statusBarController.setStatusbarIndex();
        ProgConfig.FILM_TAB_ON.setValue(false);
        ProgConfig.ABO_TAB_ON.setValue(false);
        ProgConfig.DOWNLOAD_TAB_ON.setValue(true);
    }

    private void selPanelAbo() {
        if (paneShown == PANE_SHOWN.ABO) {
            // dann ist der 2. Klick
            aboGuiPack.closeSplit();
            return;
        }

        paneShown = PANE_SHOWN.ABO;
        setButtonStyle();
        splitPaneAbo.toFront();
        progData.aboGuiController.isShown();
        statusBarController.setStatusbarIndex();
        ProgConfig.FILM_TAB_ON.setValue(false);
        ProgConfig.DOWNLOAD_TAB_ON.setValue(false);
        ProgConfig.ABO_TAB_ON.setValue(true);
    }

    private void setButtonStyle() {
        btnFilm.getStyleClass().clear();
        btnDownload.getStyleClass().clear();
        btnAbo.getStyleClass().clear();

        if (paneShown == PANE_SHOWN.FILM) {
            btnFilm.getStyleClass().add("btnTabTop-sel");
        } else {
            btnFilm.getStyleClass().add("btnTabTop");
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

    public void setFocus() {
        Node node = stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1);
        if (paneShown == PANE_SHOWN.FILM) {
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
