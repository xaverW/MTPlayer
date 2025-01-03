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

import de.p2tools.mtplayer.controller.ProgQuit;
import de.p2tools.mtplayer.controller.ProgStartAfterGui;
import de.p2tools.mtplayer.controller.ProgStartBeforeGui;
import de.p2tools.mtplayer.controller.config.*;
import de.p2tools.p2lib.P2LibInit;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2GuiSize;
import de.p2tools.p2lib.tools.P2Lock;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MTPlayer extends Application {

    private Stage primaryStage;
    private ProgData progData;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        P2Duration.counterStart("start");
        progData = ProgData.getInstance();
        progData.primaryStage = primaryStage;

        ProgStartBeforeGui.workBeforeGui();

        //wenn gewünscht, Lock-File prüfen
        final String xmlFilePath = ProgInfos.getLockFileStr();
        if (ProgConfig.SYSTEM_ONLY_ONE_INSTANCE.getValue() && !P2Lock.getLockInstance(xmlFilePath)) {
            //dann kann man sich den Rest sparen
            return;
        }

        initRootLayout();
        ProgStartAfterGui.doWorkAfterGui();

        P2Duration.onlyPing("Gui steht!");
        P2Duration.counterStop("start");
    }

    private void initRootLayout() {
        try {
            progData.mtPlayerController = new MTPlayerController();
            Scene scene;

            if (!ProgData.startMinimized &&
                    (ProgConfig.SYSTEM_GUI_MAXIMISED.get() || ProgConfig.SYSTEM_GUI_START_ALWAYS_MAXIMISED.get())) {
                //========= MAXIMISED ===========
                // dann wars maximiert oder soll immer so gestartet werden
                scene = new Scene(progData.mtPlayerController,
                        P2GuiSize.getSceneSize(ProgConfig.SYSTEM_SIZE_GUI, true),
                        P2GuiSize.getSceneSize(ProgConfig.SYSTEM_SIZE_GUI, false)); //Größe der scene != Größe stage!!!

                primaryStage.setScene(scene);
                P2GuiSize.setPos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage);
                primaryStage.setMaximized(true);
//                 // Das geht!! aber GNOME mag nicht!!!!!!
//                 if (PlatformUtil.isLinux()) {
//                   // bug in Java
//                   // https://bugs.openjdk.org/browse/JDK-8325549
//                   // https://stackoverflow.com/questions/24519668/javafx-maximized-window-moves-if-dialog-shows-up
//                   primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<>() {
//                        @Override
//                        public void handle(WindowEvent windowEvent) {
//                            primaryStage.removeEventHandler(WindowEvent.WINDOW_SHOWN, this);
//                            Platform.runLater(() -> {
//                                var bounds = new Rectangle2D(primaryStage.getX(), primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight());
//                                primaryStage.setMaximized(false);
//                                primaryStage.setX(bounds.getMinX());
//                                primaryStage.setY(bounds.getMinY());
//                                primaryStage.setWidth(bounds.getWidth());
//                                primaryStage.setHeight(bounds.getHeight());
//                                primaryStage.setMaximized(true);
//                            });
//                        }
//                    });
//                }

            } else {
                //========= !MAXIMISED ===========
                scene = new Scene(progData.mtPlayerController,
                        P2GuiSize.getSceneSize(ProgConfig.SYSTEM_SIZE_GUI, true),
                        P2GuiSize.getSceneSize(ProgConfig.SYSTEM_SIZE_GUI, false)); //Größe der scene != Größe stage!!!

                primaryStage.setScene(scene);
                primaryStage.setOnShowing(e -> P2GuiSize.setSizePos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, null));
                primaryStage.setOnShown(e -> P2GuiSize.setSizePos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, null));
            }

            // primaryStage.sizeToScene(); // macht Probleme
            primaryStage.setOnCloseRequest(e -> {
                //beim Beenden
                e.consume();
                ProgQuit.quit(false);
            });

            ProgConfig.SYSTEM_DARK_THEME.addListener((u, o, n) -> ProgColorList.setColorTheme());
            PShortKeyFactory.addShortKey(scene);
            P2LibInit.addP2CssToScene(scene); // und jetzt noch CSS einstellen

            if (ProgData.startMinimized) {
                primaryStage.setIconified(true);
                P2DialogExtra.getDialogList().forEach(d -> d.getStage().setIconified(true));
            }
            primaryStage.iconifiedProperty().addListener((u, o, n) -> {
                P2DialogExtra.getDialogList().forEach(d -> d.getStage().setIconified(n));
            });

            primaryStage.show();
            if (ProgData.firstProgramStart) {
                // dann gabs den Startdialog
                ProgConfig.SYSTEM_DARK_THEME.set(ProgConfig.SYSTEM_DARK_THEME_START.get());
                ProgConfig.SYSTEM_BLACK_WHITE_ICON.set(ProgConfig.SYSTEM_BLACK_WHITE_ICON_START.get());
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
