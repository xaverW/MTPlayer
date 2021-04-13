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
import de.p2tools.mtplayer.gui.configDialog.ConfigDialogController;
import de.p2tools.mtplayer.gui.dialog.AboutDialogController;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.PLogger;
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
    private SystemTray systemTray = null;


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
        if (systemTray != null) {
            Arrays.stream(systemTray.getTrayIcons()).sequential().forEach(e -> systemTray.remove(e));
            systemTray = null;
        }
    }

    public void setTray() {
        if (!SystemTray.isSupported()) {
            return;
        }

        systemTray = SystemTray.getSystemTray();
        PopupMenu popup = new PopupMenu();

//                --------------------------------
//        "1 aktiver Download (76,3%; 2.304 # 3019 MB, 6,2 MB/s, 3,2 Minuten verbleibend), 2 wartende Downloads" (so als Beispiel)
//        Die Ausgabe von "1 aktiver ..." könnte man auch als MouseOver-Funktion vom TrayIcon implementieren. Nur so als Idee.


        java.awt.MenuItem miMaxMin = new java.awt.MenuItem("Programm maximieren/minimieren");
        java.awt.MenuItem miConfig = new java.awt.MenuItem("Einstellungen öffnen");
        java.awt.MenuItem miLogfile = new java.awt.MenuItem("LogDatei öffnen");
        java.awt.MenuItem miAbout = new java.awt.MenuItem("über dieses Programm");
        java.awt.MenuItem miQuit = new java.awt.MenuItem("Programm Beenden");

        miLogfile.addActionListener((e -> Platform.runLater(() -> PLogger.openLogFile())));
        miConfig.addActionListener(e -> Platform.runLater(() -> new ConfigDialogController()));
        miMaxMin.addActionListener(e -> Platform.runLater(() -> maxMin()));
        miAbout.addActionListener(e -> Platform.runLater(() -> new AboutDialogController(progData)));
        miQuit.addActionListener(e -> Platform.runLater(() -> ProgQuit.quit(false)));

        popup.add(miMaxMin);
        popup.add(miConfig);
        popup.add(miLogfile);

        popup.addSeparator();
        popup.add(miAbout);
        popup.addSeparator();
        popup.add(miQuit);

        String resource = "/de/p2tools/mtplayer/res/P2_24.png";
        URL res = getClass().getResource(resource);
        Image image = Toolkit.getDefaultToolkit().getImage(res);

        TrayIcon trayicon = new TrayIcon(image, "MTPlayer", popup);
        trayicon.setImageAutoSize(true);
        trayicon.setToolTip(null);
//        System.out.println("tooltip: " + trayicon.getToolTip());
//        trayicon.setToolTip("tooltip");
        trayicon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    maxMin();
                }
            }
        });

        try {
            systemTray.add(trayicon);
        } catch (AWTException exception) {
            PLog.errorLog(945120364, exception.getMessage());
        }
    }

    private void maxMin() {
        if (progData.primaryStage.isShowing()) {
            Platform.runLater(() -> progData.primaryStage.close());
        } else {
            Platform.runLater(() -> progData.primaryStage.show());
        }

        if (progData.quitDialogController != null) {
            if (progData.quitDialogController.isShowing()) {
                Platform.runLater(() -> progData.quitDialogController.getStage().close());
            } else {
                Platform.runLater(() -> progData.quitDialogController.getStage().show());
            }
        }
    }
}
