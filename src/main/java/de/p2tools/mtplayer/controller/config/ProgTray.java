/*
 * P2tools Copyright (C) 2021 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.controller.config;

import de.p2tools.mtplayer.controller.ProgQuit;
import de.p2tools.mtplayer.gui.dialog.AboutDialogController;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Arrays;

public class ProgTray {
    private final ProgData progData;
    private BooleanProperty propTray = ProgConfig.SYSTEM_TRAY.getBooleanProperty();
    private SystemTray tray = null;


    public ProgTray(ProgData progData) {
        this.progData = progData;
        propTray.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (propTray.get()) {
                    setTray();
                } else {
                    removeTray();
                }
            }
        });
    }

    public void removeTray() {
        if (tray != null) {
            Arrays.stream(tray.getTrayIcons()).sequential().forEach(e -> tray.remove(e));
            tray = null;
        }
    }

    public void setTray() {
        if (!SystemTray.isSupported()) {
            return;
        }

        tray = SystemTray.getSystemTray();
        PopupMenu popup = new PopupMenu();

        java.awt.MenuItem miAbout = new java.awt.MenuItem("über dieses Programm");
        java.awt.MenuItem miQuit = new java.awt.MenuItem("Programm Beenden");
//        java.awt.MenuItem miShutDown = new java.awt.MenuItem("ShutDown");

        miAbout.addActionListener(event ->
                Platform.runLater(() -> new AboutDialogController(progData)));
        miQuit.addActionListener(event ->
                Platform.runLater(() -> new ProgQuit().quit(true, false)));
//        miShutDown.addActionListener(event ->
//                Platform.runLater(() -> new ProgQuit().quitShutDown()));

        popup.add(miAbout);
        popup.addSeparator();
        popup.add(miQuit);

        String resource = "/de/p2tools/mtplayer/res/P2_24.png";
        URL res = getClass().getResource(resource);
        Image image = Toolkit.getDefaultToolkit().getImage(res);

        TrayIcon trayicon = new TrayIcon(image, "System Tray Demo", popup);
        trayicon.setImage(image);
        trayicon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    System.out.println("Clicked");
                    if (progData.primaryStage.isShowing()) {
                        Platform.runLater(() -> progData.primaryStage.close());
                    } else {
                        Platform.runLater(() -> progData.primaryStage.show());
                    }
                }
            }
        });

        try {
            tray.add(trayicon);
        } catch (AWTException exception) {
            PLog.errorLog(945120364, exception.getMessage());
        }
    }
}
