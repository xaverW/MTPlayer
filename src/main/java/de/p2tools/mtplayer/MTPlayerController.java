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

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.load.LoadFactory;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.mtplayer.gui.*;
import de.p2tools.mtplayer.gui.filter.FastFilter;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadConst;
import de.p2tools.p2lib.p2event.P2Listener;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

public class MTPlayerController extends StackPane {

    public static BooleanProperty TAB_FILM_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty TAB_AUDIO_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty TAB_LIVE_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty TAB_DOWNLOAD_ON = new SimpleBooleanProperty(Boolean.FALSE);
    public static BooleanProperty TAB_ABO_ON = new SimpleBooleanProperty(Boolean.FALSE);

    private final Button btnFilmlist = new Button("Filmliste");
    private final Button btnFilm = new Button("Filme");
    private final Button btnAudio = new Button("Audios");
    private final Button btnLive = new Button("Live");
    private final Button btnDownload = new Button("Downloads");
    private final Button btnAbo = new Button("Abos");
    private final StackPane stackPaneFast = new StackPane();
    private final FastFilter fastFilterFilm = new FastFilter(false);
    private final FastFilter fastFilterAudio = new FastFilter(true);

    private final BorderPane borderPane = new BorderPane();
    private final StackPane stackPaneCont = new StackPane();
    private StatusBarController statusBarController;

    private SplitPane splitPaneFilm;
    private SplitPane splitPaneAudio;
    private SplitPane splitPaneLiveFilm;
    private SplitPane splitPaneDownload;
    private SplitPane splitPaneAbo;

    private final ProgData progData;
    private final FilmGui filmGui = new FilmGui();
    private final AudioGui audioGui = new AudioGui();
    private final LiveFilmGui liveFilmGui = new LiveFilmGui();
    private final DownloadGui downloadGui = new DownloadGui();
    private final AboGui aboGui = new AboGui();

    public MTPlayerController() {
        progData = ProgData.getInstance();
        init();
    }

    private void init() {
        try {
            stackPaneFast.getChildren().addAll(fastFilterFilm, fastFilterAudio);
            stackPaneFast.setAlignment(Pos.CENTER_RIGHT);

            final Button btnSize = new Button("Downloads");
            btnSize.setVisible(false);
            btnSize.getStyleClass().add("btnSize");
            Platform.runLater(() -> {
                double size = btnSize.getWidth();
                btnFilm.setMinWidth(size);
                btnAudio.setMinWidth(size);
                btnLive.setMinWidth(size);
                btnDownload.setMinWidth(size);
                btnAbo.setMinWidth(size);
            });

            // Toolbar
            TilePane tilePaneButton = new TilePane();
//            tilePaneButton.setPrefColumns(5);
            tilePaneButton.setHgap(5);
            tilePaneButton.setPadding(new Insets(0));
            tilePaneButton.setAlignment(Pos.CENTER);
            tilePaneButton.getChildren().addAll(btnFilm, btnAudio, btnLive, btnDownload, btnAbo);

            StackPane stackPane = new StackPane();
            stackPane.setAlignment(Pos.CENTER);
            stackPane.getChildren().addAll(btnSize, tilePaneButton);
            HBox.setHgrow(stackPane, Priority.ALWAYS);

            HBox hBoxTop = new HBox();
            hBoxTop.setPadding(new Insets(5, 10, 5, 10));
            hBoxTop.setSpacing(5);
            hBoxTop.setAlignment(Pos.CENTER);
            hBoxTop.getChildren().addAll(btnFilmlist, stackPane, stackPaneFast, new MTPlayerMenu());
            HBox.setHgrow(stackPane, Priority.ALWAYS);
            HBox.setHgrow(stackPaneFast, Priority.NEVER);

            // Center
            splitPaneFilm = filmGui.pack();
            splitPaneAudio = audioGui.pack();
            splitPaneLiveFilm = liveFilmGui.pack();
            splitPaneDownload = downloadGui.pack();
            splitPaneAbo = aboGui.pack();
            stackPaneCont.getChildren().addAll(splitPaneFilm, splitPaneAudio, splitPaneLiveFilm, splitPaneDownload, splitPaneAbo);

            // Statusbar
            statusBarController = new StatusBarController(progData);
            statusBarController.visibleProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_ON);
            statusBarController.managedProperty().bind(ProgConfig.SYSTEM_STATUS_BAR_ON);

            // Gui zusammenbauen
            borderPane.setTop(hBoxTop);
            borderPane.setCenter(stackPaneCont);
            borderPane.setBottom(statusBarController);
            this.setPadding(new Insets(0));
            this.getChildren().addAll(borderPane, progData.maskerPane);

            initMaskerPane();
            initButtonFilmList();
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
        StackPane.setAlignment(progData.maskerPane, Pos.CENTER);
        progData.maskerPane.setPadding(new Insets(4, 1, 1, 1));
        progData.maskerPane.toFront();
        Button btnStop = progData.maskerPane.getButton();
        progData.maskerPane.setButtonText("");
        btnStop.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        btnStop.setOnAction(a -> P2LoadConst.stop.set(true));
    }

    private void initButtonFilmList() {
        btnFilmlist.setMinWidth(Region.USE_PREF_SIZE);
        btnFilmlist.getStyleClass().addAll("pFuncBtn");
        btnFilmlist.setTooltip(new Tooltip("Eine neue Filmliste laden.\n" +
                "Wenn die Filmliste nicht zu alt ist, wird nur ein Update geladen. [" +
                ProgConfig.SHORTCUT_UPDATE_FILMLIST.getValueSafe() + "]\n" +
                "Mit der rechten Maustaste wird immer die komplette Filmliste geladen. [" +
                ProgConfig.SHORTCUT_LOAD_FILMLIST.getValueSafe() + "]"));
        btnFilmlist.setOnAction(e -> {
            LoadFactory.updateLists(false); // Filmliste/Audioliste aktualisieren, wenn notwendig
        });
        btnFilmlist.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                LoadFactory.updateLists(true); // Filmliste/Audioliste aktualisieren, immer alles

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
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILMLIST_LOAD_FINISHED) {
            @Override
            public void pingGui() {
                if (stackPaneCont.getChildren().isEmpty()) {
                    return;
                }
                setFocus();
            }
        });
    }

    private void initButton() {
        ProgConfig.FAST_FILM_SEARCH_ON.addListener((u, o, n) -> setButtonStyle());
        ProgConfig.FAST_AUDIO_SEARCH_ON.addListener((u, o, n) -> setButtonStyle());
        ProgConfig.SYSTEM_USE_AUDIOLIST.addListener((u, o, n) -> {
            // wenn Audio ein, dann Film
            fastFilterAudio.setManaged(ProgConfig.SYSTEM_USE_AUDIOLIST.get());
            if (TAB_AUDIO_ON.get()) {
                selPanelFilm();
            } else {
                setButtonStyle();
            }
        });
        ProgConfig.SYSTEM_USE_LIVE.addListener((u, o, n) -> {
            // wenn Audio ein, dann Film
            if (TAB_LIVE_ON.get()) {
                selPanelFilm();
            }
        });

        btnFilm.setTooltip(new Tooltip("Filme anzeigen"));
        btnFilm.setOnAction(e -> selPanelFilm());
        btnFilm.setMaxWidth(Double.MAX_VALUE);

        btnAudio.setTooltip(new Tooltip("Audios anzeigen"));
        btnAudio.setOnAction(e -> selPanelAudio());
        btnAudio.setMaxWidth(Double.MAX_VALUE);
        btnAudio.visibleProperty().bind(ProgConfig.SYSTEM_USE_AUDIOLIST);
        btnAudio.managedProperty().bind(ProgConfig.SYSTEM_USE_AUDIOLIST);

        btnLive.setTooltip(new Tooltip("Live-Filme suchen"));
        btnLive.setOnAction(e -> selPanelLiveFilm());
        btnLive.setMaxWidth(Double.MAX_VALUE);
        btnLive.visibleProperty().bind(ProgConfig.SYSTEM_USE_LIVE);
        btnLive.managedProperty().bind(btnLive.visibleProperty());

        btnDownload.setTooltip(new Tooltip("Downloads anzeigen"));
        btnDownload.setOnAction(e -> selPanelDownload());
        btnDownload.setMaxWidth(Double.MAX_VALUE);

        btnAbo.setTooltip(new Tooltip("Abos anzeigen"));
        btnAbo.setOnAction(e -> selPanelAbo());
        btnAbo.setMaxWidth(Double.MAX_VALUE);

        // rechte Maus
        btnFilm.setOnMouseClicked(mouseEvent -> {
            if (progData.maskerPane.isVisible() || !TAB_FILM_ON.get()) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                MTPlayerFactory.setInfos();
            }
        });
        btnAudio.setOnMouseClicked(mouseEvent -> {
            if (progData.maskerPane.isVisible() || !TAB_AUDIO_ON.get()) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                MTPlayerFactory.setInfos();
            }
        });
        btnLive.setOnMouseClicked(mouseEvent -> {
            if (progData.maskerPane.isVisible() || !TAB_LIVE_ON.get()) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                MTPlayerFactory.setInfos();
            }
        });
        btnDownload.setOnMouseClicked(mouseEvent -> {
            if (progData.maskerPane.isVisible() || !TAB_DOWNLOAD_ON.get()) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                MTPlayerFactory.setInfos();
            }
        });
        btnAbo.setOnMouseClicked(mouseEvent -> {
            if (progData.maskerPane.isVisible() || !TAB_ABO_ON.get()) {
                return;
            }
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                MTPlayerFactory.setInfos();
            }
        });
    }

    private void selPanelFilm() {
        if (TAB_FILM_ON.get()) {
            // dann ist der 2. Klick
            MTPlayerFactory.setFilter();
            return;
        }

        setTabFilmOn();
        setButtonStyle();
        splitPaneFilm.toFront();
        progData.filmGuiController.isShown();
        statusBarController.setStatusbarIndex();
    }

    private void selPanelAudio() {
        if (TAB_AUDIO_ON.get()) {
            // dann ist der 2. Klick
            MTPlayerFactory.setFilter();
            return;
        }

        setTabAudioOn();
        setButtonStyle();
        splitPaneAudio.toFront();
        progData.audioGuiController.isShown();
        statusBarController.setStatusbarIndex();
    }

    private void selPanelLiveFilm() {
        if (TAB_LIVE_ON.get()) {
            // dann ist der 2. Klick
            MTPlayerFactory.setFilter();
            return;
        }

        setTabLiveOn();
        setButtonStyle();
        splitPaneLiveFilm.toFront();
        progData.liveFilmGuiController.isShown();
        statusBarController.setStatusbarIndex();
    }

    private void selPanelDownload() {
        if (TAB_DOWNLOAD_ON.get()) {
            // dann ist der 2. Klick
            MTPlayerFactory.setFilter();
            return;
        }

        setTabDownloadOn();
        setButtonStyle();
        splitPaneDownload.toFront();
        progData.downloadGuiController.isShown();
        statusBarController.setStatusbarIndex();
    }

    private void selPanelAbo() {
        if (TAB_ABO_ON.get()) {
            // dann ist der 2. Klick
            MTPlayerFactory.setFilter();
            return;
        }

        setTabAboOn();
        setButtonStyle();
        splitPaneAbo.toFront();
        progData.aboGuiController.isShown();
        statusBarController.setStatusbarIndex();
    }

    private void setButtonStyle() {
        btnFilm.getStyleClass().clear();
        btnAudio.getStyleClass().clear();
        btnLive.getStyleClass().clear();
        btnDownload.getStyleClass().clear();
        btnAbo.getStyleClass().clear();


        fastFilterFilm.setVisible(false);
        fastFilterAudio.setVisible(false);

        if (TAB_FILM_ON.get()) {
            fastFilterFilm.setVisible(true);
            btnFilm.getStyleClass().addAll("pFuncBtn", "pFuncBtnSel");
        } else {
            btnFilm.getStyleClass().addAll("pFuncBtn");
        }

        if (TAB_AUDIO_ON.get()) {
            fastFilterAudio.setVisible(true);
            btnAudio.getStyleClass().addAll("pFuncBtn", "pFuncBtnSel");
        } else {
            btnAudio.getStyleClass().addAll("pFuncBtn");
        }

        if (TAB_LIVE_ON.get()) {
            btnLive.getStyleClass().addAll("pFuncBtn", "pFuncBtnSel");
        } else {
            btnLive.getStyleClass().addAll("pFuncBtn");
        }

        if (TAB_DOWNLOAD_ON.get()) {
            btnDownload.getStyleClass().addAll("pFuncBtn", "pFuncBtnSel");
        } else {
            btnDownload.getStyleClass().addAll("pFuncBtn");
        }

        if (TAB_ABO_ON.get()) {
            btnAbo.getStyleClass().addAll("pFuncBtn", "pFuncBtnSel");
        } else {
            btnAbo.getStyleClass().addAll("pFuncBtn");
        }

        if (ProgConfig.FAST_FILM_SEARCH_ON.get() ||
                ProgConfig.FAST_AUDIO_SEARCH_ON.get() && ProgConfig.SYSTEM_USE_AUDIOLIST.get()) {
            btnFilm.getStyleClass().addAll("pFuncBtn");
            btnAudio.getStyleClass().addAll("pFuncBtn");
            btnLive.getStyleClass().addAll("pFuncBtn");
            btnDownload.getStyleClass().addAll("pFuncBtn");
            btnAbo.getStyleClass().addAll("pFuncBtn");
        }
    }

    public void setFocus() {
        if (TAB_FILM_ON.get()) {
            progData.filmGuiController.isShown();
        }
        if (TAB_AUDIO_ON.get()) {
            progData.audioGuiController.isShown();
        }
        if (TAB_LIVE_ON.get()) {
            progData.liveFilmGuiController.isShown();
        }
        if (TAB_DOWNLOAD_ON.get()) {
            progData.downloadGuiController.isShown();
        }
        if (TAB_ABO_ON.get()) {
            progData.aboGuiController.isShown();
        }
    }

    private void setTabFilmOn() {
        clearTab();
        TAB_FILM_ON.set(true);
    }

    private void setTabAudioOn() {
        clearTab();
        TAB_AUDIO_ON.set(true);
    }

    private void setTabLiveOn() {
        clearTab();
        TAB_LIVE_ON.set(true);
    }

    private void setTabDownloadOn() {
        clearTab();
        TAB_DOWNLOAD_ON.set(true);
    }

    private void setTabAboOn() {
        clearTab();
        TAB_ABO_ON.set(true);
    }

    private void clearTab() {
        TAB_FILM_ON.set(false);
        TAB_AUDIO_ON.set(false);
        TAB_LIVE_ON.set(false);
        TAB_DOWNLOAD_ON.set(false);
        TAB_ABO_ON.set(false);
    }
}
