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
import de.p2tools.p2lib.guitools.P2GuiSize;
import de.p2tools.p2lib.tools.P2Lock;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MTPlayer extends Application {

    private Stage primaryStage;
    private ProgData progData;
    private Scene scene = null;

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
            scene = new Scene(progData.mtPlayerController,
                    P2GuiSize.getWidth(ProgConfig.SYSTEM_SIZE_GUI),
                    P2GuiSize.getHeight(ProgConfig.SYSTEM_SIZE_GUI)); //Größe der scene != Größe stage!!!

            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
            primaryStage.setOnCloseRequest(e -> {
                //beim Beenden
                e.consume();
                // P2GuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene);
                ProgQuit.quit(false);
            });
            primaryStage.setOnShowing(e -> {
                P2GuiSize.setSizePos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, null);
            });
            primaryStage.setOnShown(e -> {
                P2GuiSize.setSizePos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, null);
            });

            PShortKeyFactory.addShortKey(scene);

            // Pos setzen
            // P2GuiSize.setOnlyPos(ProgConfig.SYSTEM_SIZE_GUI, primaryStage);
            if (ProgConfig.SYSTEM_GUI_MAXIMISED.get() || ProgConfig.SYSTEM_GUI_START_ALWAYS_MAXIMISED.get()) {
                // dann wars maximiert oder soll immer so gestartet werden
                primaryStage.setMaximized(true);
            }

            scene.heightProperty().addListener((v, o, n) -> P2GuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));
            scene.widthProperty().addListener((v, o, n) -> P2GuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));
            primaryStage.xProperty().addListener((v, o, n) -> P2GuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));
            primaryStage.yProperty().addListener((v, o, n) -> P2GuiSize.getSizeScene(ProgConfig.SYSTEM_SIZE_GUI, primaryStage, scene));

            P2LibInit.addP2CssToScene(scene); // und jetzt noch CSS einstellen
            ProgConfig.SYSTEM_DARK_THEME.addListener((u, o, n) -> {
                ProgColorList.setColorTheme();
            });

            primaryStage.show();
            primaryStage.setIconified(ProgData.startMinimized);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
