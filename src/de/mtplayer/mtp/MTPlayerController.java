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

package de.mtplayer.mtp;

import de.mtplayer.mtp.controller.ProgQuit;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.*;
import de.mtplayer.mtp.gui.configDialog.ConfigDialogController;
import de.mtplayer.mtp.gui.dialog.AboutDialogController;
import de.mtplayer.mtp.gui.dialog.ResetDialogController;
import de.mtplayer.mtp.gui.mediaDialog.MediaConfigController;
import de.p2tools.p2Lib.guiTools.pMask.PMaskerPane;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class MTPlayerController extends StackPane {

    Button btnFilmlist = new Button("Filmliste");
    Button btnFilm = new Button("Filme");
    Button btnDownload = new Button("Downloads");
    Button btnAbo = new Button("Abos");
    Button btnMsg = new Button("Meldungen");

    MenuButton menuButton = new MenuButton("");

    BorderPane borderPane = new BorderPane();
    StackPane stackPaneCont = new StackPane();
    private PMaskerPane maskerPane = new PMaskerPane();
    private StatusBarController statusBarController;

    private SplitPane splitPaneFilm;
    private SplitPane splitPaneDownoad;
    private SplitPane splitPaneAbo;
    private SplitPane splitPaneMsg;

    private final ProgData progData;
    BooleanProperty msgVisProperty = ProgConfig.MSG_VISIBLE.getBooleanProperty();

    FilmGuiPack filmGuiPack = new FilmGuiPack();
    DownloadGuiPack downloadGuiPack = new DownloadGuiPack();
    AboGuiPack aboGuiPack = new AboGuiPack();


    public MTPlayerController() {
        progData = ProgData.getInstance();
        init();
    }

    public Button getBtnFilmlist() {
        // ist erst mal so bis "Filmliste laden" überarbeitet wird
        return btnFilmlist;
    }

    private void init() {
        try {
            // Top
            this.setPadding(new Insets(0));

            HBox hBoxTop = new HBox();
            hBoxTop.setPadding(new Insets(10));
            hBoxTop.setSpacing(20);
            hBoxTop.setAlignment(Pos.CENTER);
            HBox.setHgrow(hBoxTop, Priority.ALWAYS);

            TilePane tilePane = new TilePane();
            tilePane.setHgap(20);
            tilePane.setAlignment(Pos.CENTER);
            HBox.setHgrow(tilePane, Priority.ALWAYS);

            tilePane.getChildren().addAll(btnFilm, btnDownload, btnAbo, btnMsg);
            hBoxTop.getChildren().addAll(btnFilmlist, tilePane, menuButton);


            splitPaneFilm = filmGuiPack.pack();
            splitPaneDownoad = downloadGuiPack.pack();
            splitPaneAbo = aboGuiPack.pack();
            splitPaneMsg = new MsgGuiPack().pack();
            splitPaneMsg.visibleProperty().bind(msgVisProperty);
            stackPaneCont.getChildren().addAll(splitPaneFilm, splitPaneDownoad, splitPaneAbo, splitPaneMsg);

            statusBarController = new StatusBarController(progData);

            VBox.setVgrow(hBoxTop, Priority.NEVER);
            VBox.setVgrow(statusBarController, Priority.NEVER);

            borderPane.setTop(hBoxTop);
            borderPane.setCenter(stackPaneCont);
            borderPane.setBottom(statusBarController);

            this.setPadding(new Insets(0));
            maskerPane.setPadding(new Insets(4, 1, 1, 1));
            this.getChildren().addAll(borderPane, maskerPane);
            StackPane.setAlignment(maskerPane, Pos.CENTER);
            maskerPane.toFront();
            maskerPane.setVisible(false);

            btnFilmlist.getStyleClass().add("btnFilmlist");
            btnFilmlist.setOnAction(e -> progData.loadFilmlist.loadFilmlist(""));

            btnFilm.getStyleClass().add("btnFilm");
            btnFilm.setOnAction(e -> selPanelFilm());
            btnFilm.setMaxWidth(Double.MAX_VALUE);

            btnDownload.getStyleClass().add("btnDownlad");
            btnDownload.setOnAction(e -> selPanelDownload());
            btnDownload.setMaxWidth(Double.MAX_VALUE);

            btnAbo.getStyleClass().add("btnAbo");
            btnAbo.setOnAction(e -> selPanelAbo());
            btnAbo.setMaxWidth(Double.MAX_VALUE);

            btnMsg.getStyleClass().add("btnMsg");
            btnMsg.setOnAction(e -> selPanelMsg());
            btnMsg.visibleProperty().bind(msgVisProperty);
            btnMsg.managedProperty().bind(msgVisProperty);

            final MenuItem miConfig = new MenuItem("Einstellungen");
            miConfig.setOnAction(e -> new ConfigDialogController());

            final MenuItem miMedia = new MenuItem("Mediensammlung");
            miMedia.setOnAction(e -> new MediaConfigController());

            final CheckMenuItem miMsg = new CheckMenuItem("Meldungen");
            miMsg.selectedProperty().bindBidirectional(msgVisProperty);
            miMsg.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selPanelMsg();
                    splitPaneMsg.requestLayout();
                } else {
                    selPanelFilm();
                }
            });

            final MenuItem miQuit = new MenuItem("Beenden");
            miQuit.setOnAction(e -> new ProgQuit().quit(true, false));

            final MenuItem miAbout = new MenuItem("Über dieses Programm");
            miAbout.setOnAction(event -> new AboutDialogController(progData));

            final MenuItem miReset = new MenuItem("Einstellungen zurücksetzen");
            miReset.setOnAction(event -> new ResetDialogController(progData));

            final Menu mHelp = new Menu("Hilfe");
            mHelp.getItems().addAll(miAbout, new SeparatorMenuItem(), miReset);

            menuButton.getStyleClass().add("btnFunction");
            menuButton.setText("");
            menuButton.setGraphic(new Icons().FX_ICON_TOOLBAR_MENU_TOP);
            menuButton.getItems().addAll(miConfig, miMedia, miMsg, mHelp, new SeparatorMenuItem(), miQuit);

            selPanelFilm();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void selPanelFilm() {
        if (maskerPane.isVisible()) {
            return;
        }

        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneFilm)) {
            filmGuiPack.closeSplit();
            return;
        }

        btnFilm.getStyleClass().clear();
        btnDownload.getStyleClass().clear();
        btnAbo.getStyleClass().clear();
        btnMsg.getStyleClass().clear();

        btnFilm.getStyleClass().add("btnTab-sel");
        btnDownload.getStyleClass().add("btnTab");
        btnAbo.getStyleClass().add("btnTab");
        btnMsg.getStyleClass().add("btnTab");

        splitPaneFilm.toFront();
        progData.filmGuiController.isShown();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.FILM);
    }

    private void selPanelDownload() {
        if (maskerPane.isVisible()) {
            return;
        }

        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneDownoad)) {
            downloadGuiPack.closeSplit();
            return;
        }

        btnFilm.getStyleClass().clear();
        btnDownload.getStyleClass().clear();
        btnAbo.getStyleClass().clear();
        btnMsg.getStyleClass().clear();

        btnFilm.getStyleClass().add("btnTab");
        btnDownload.getStyleClass().add("btnTab-sel");
        btnAbo.getStyleClass().add("btnTab");
        btnMsg.getStyleClass().add("btnTab");

        progData.downloadGuiController.isShown();
        splitPaneDownoad.toFront();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.DOWNLOAD);
    }

    private void selPanelAbo() {
        if (maskerPane.isVisible()) {
            return;
        }

        if (stackPaneCont.getChildren().get(stackPaneCont.getChildren().size() - 1).equals(splitPaneAbo)) {
            aboGuiPack.closeSplit();
            return;
        }

        btnFilm.getStyleClass().clear();
        btnDownload.getStyleClass().clear();
        btnAbo.getStyleClass().clear();
        btnMsg.getStyleClass().clear();

        btnFilm.getStyleClass().add("btnTab");
        btnDownload.getStyleClass().add("btnTab");
        btnAbo.getStyleClass().add("btnTab-sel");
        btnMsg.getStyleClass().add("btnTab");

        progData.aboGuiController.isShown();
        splitPaneAbo.toFront();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.ABO);
    }

    private void selPanelMsg() {
        if (maskerPane.isVisible()) {
            return;
        }
        btnFilm.getStyleClass().clear();
        btnDownload.getStyleClass().clear();
        btnAbo.getStyleClass().clear();
        btnMsg.getStyleClass().clear();

        btnFilm.getStyleClass().add("btnTab");
        btnDownload.getStyleClass().add("btnTab");
        btnAbo.getStyleClass().add("btnTab");
        btnMsg.getStyleClass().add("btnTab-sel");

        splitPaneMsg.toFront();
        statusBarController.setStatusbarIndex(StatusBarController.StatusbarIndex.NONE);
    }

    public void setMasker() {
        maskerPane.setVisible(true);
    }

    public void resetMasker() {
        Platform.runLater(() -> {
            maskerPane.setVisible(false);
        });
    }
}
