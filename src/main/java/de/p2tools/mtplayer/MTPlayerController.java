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
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.AboGuiPack;
import de.p2tools.mtplayer.gui.DownloadGuiPack;
import de.p2tools.mtplayer.gui.FilmGuiPack;
import de.p2tools.mtplayer.gui.StatusBarController;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

public class MTPlayerController extends StackPane {

    Button btnFilmlist = new Button("Filmliste");
    Button btnFilm = new Button("Filme");
    Button btnDownload = new Button("Downloads");
    Button btnAbo = new Button("Abos");

    BorderPane borderPane = new BorderPane();
    StackPane stackPaneCont = new StackPane();

    private PMaskerPane maskerPane = new PMaskerPane();
    private StatusBarController statusBarController;

    private SplitPane splitPaneFilm;
    private SplitPane splitPaneDownoad;
    private SplitPane splitPaneAbo;

    private final ProgData progData;
    FilmGuiPack filmGuiPack = new FilmGuiPack();
    DownloadGuiPack downloadGuiPack = new DownloadGuiPack();
    AboGuiPack aboGuiPack = new AboGuiPack();


    public MTPlayerController() {
        progData = ProgData.getInstance();
        init();
    }

    public void setButtonFilmlistUpdate() {
        btnFilmlist.getStyleClass().add("btnFilmlist_");
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
            splitPaneDownoad = downloadGuiPack.pack();
            splitPaneAbo = aboGuiPack.pack();
            stackPaneCont.getChildren().addAll(splitPaneFilm, splitPaneDownoad, splitPaneAbo);

            // Statusbar
            statusBarController = new StatusBarController(progData);

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
        btnFilmlist.getStyleClass().add("btnFilmlist");
        btnFilmlist.setTooltip(new Tooltip("Eine neue Filmliste laden.\n" +
                "Wenn die Filmliste nicht zu alt ist, wird nur ein Update geladen.\n" +
                "Mit der rechten Maustaste wird immer die komplette Filmliste geladen."));
        btnFilmlist.setOnAction(e -> {
            LoadFilmFactory.getInstance().loadList(false);
        });
        btnFilmlist.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                LoadFilmFactory.getInstance().loadList(true);
            }
        });

        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(new de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerLoadFilmlist() {
            @Override
            public void start(de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerFilmlistLoadEvent event) {
                // falls "neue Filmliste" aktiv ist
                btnFilmlist.getStyleClass().clear();
                btnFilmlist.getStyleClass().add("btnFilmlist");
            }

            @Override
            public void finished(de.p2tools.p2Lib.mtFilm.loadFilmlist.ListenerFilmlistLoadEvent event) {
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

        infoPane();
    }

    private void selPanelFilm() {
        if (maskerPane.isVisible()) {
            return;
        }

        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
            // dann ist der 2. Klick
            filmGuiPack.closeSplit();
            return;
        }

        setButtonStyle(btnFilm);
        splitPaneFilm.toFront();
        progData.filmGuiController.isShown();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.FILM);
    }

    private void selPanelDownload() {
        if (maskerPane.isVisible()) {
            return;
        }

        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
            // dann ist der 2. Klick
            downloadGuiPack.closeSplit();
            return;
        }

        setButtonStyle(btnDownload);
        splitPaneDownoad.toFront();
        progData.downloadGuiController.isShown();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.DOWNLOAD);
    }

    private void selPanelAbo() {
        if (maskerPane.isVisible()) {
            return;
        }

        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
            // dann ist der 2. Klick
            aboGuiPack.closeSplit();
            return;
        }

        setButtonStyle(btnAbo);
        splitPaneAbo.toFront();
        progData.aboGuiController.isShown();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.ABO);
    }

    private void infoPane() {
        btnFilm.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() ||
                    !stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.FILM_GUI_DIVIDER_ON.setValue(!ProgConfig.FILM_GUI_DIVIDER_ON.getValue());
            }
        });
        btnDownload.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() ||
                    !stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.setValue(!ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getValue());
            }
        });
        btnAbo.setOnMouseClicked(mouseEvent -> {
            if (maskerPane.isVisible() ||
                    !stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                ProgConfig.ABO_GUI_DIVIDER_ON.setValue(!ProgConfig.ABO_GUI_DIVIDER_ON.getValue());
            }
        });
    }

    private void setButtonStyle(Button btnSel) {
        btnFilm.getStyleClass().clear();
        btnDownload.getStyleClass().clear();
        btnAbo.getStyleClass().clear();

        if (btnSel.equals(btnFilm)) {
            btnFilm.getStyleClass().add("btnTab-sel");
        } else {
            btnFilm.getStyleClass().add("btnTab");
        }
        if (btnSel.equals(btnDownload)) {
            btnDownload.getStyleClass().add("btnTab-sel");
        } else {
            btnDownload.getStyleClass().add("btnTab");
        }
        if (btnSel.equals(btnAbo)) {
            btnAbo.getStyleClass().add("btnTab-sel");
        } else {
            btnAbo.getStyleClass().add("btnTab");
        }
    }

    public void setFilter() {
        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
            ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.FILM_GUI_FILTER_DIVIDER_ON.getValue());
        } else if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
            ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON.getValue());
        } else if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
            ProgConfig.ABO_GUI_FILTER_DIVIDER_ON.setValue(!ProgConfig.ABO_GUI_FILTER_DIVIDER_ON.getValue());
        }
    }

    public void setInfos() {
        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
            ProgConfig.FILM_GUI_DIVIDER_ON.setValue(!ProgConfig.FILM_GUI_DIVIDER_ON.getValue());
        } else if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
            ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.setValue(!ProgConfig.DOWNLOAD_GUI_DIVIDER_ON.getValue());
        } else if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
            ProgConfig.ABO_GUI_DIVIDER_ON.setValue(!ProgConfig.ABO_GUI_DIVIDER_ON.getValue());
        }
    }

    public boolean isFilmPaneShown() {
        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDownloadPaneShown() {
        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isAboPaneShown() {
        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
            return true;
        } else {
            return false;
        }
    }

    public void setFocus() {
        Node node = stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1);
        if (node != null && node == splitPaneFilm) {
            progData.filmGuiController.isShown();
        }
        if (node != null && node == splitPaneDownoad) {
            progData.downloadGuiController.isShown();
        }
        if (node != null && node == splitPaneAbo) {
            progData.aboGuiController.isShown();
        }
    }
}
