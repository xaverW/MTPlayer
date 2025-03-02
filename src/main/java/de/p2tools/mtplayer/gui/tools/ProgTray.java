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


package de.p2tools.mtplayer.gui.tools;

import de.p2tools.mtplayer.controller.ProgQuit;
import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadInfosFactory;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.StatusBarController;
import de.p2tools.mtplayer.gui.configdialog.ConfigDialogController;
import de.p2tools.mtplayer.gui.dialog.AboutDialogController;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.P2LoadListener;
import de.p2tools.p2lib.tools.P2InfoFactory;
import de.p2tools.p2lib.tools.log.P2Log;
import de.p2tools.p2lib.tools.log.P2Logger;
import javafx.application.Platform;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URL;
import java.util.Arrays;

public class ProgTray {
    private final ProgData progData;
    private SystemTray systemTray = null;
    private boolean stopTimer = false;

    public ProgTray(ProgData progData) {
        this.progData = progData;
    }

    public void initProgTray() {
        ProgConfig.SYSTEM_TRAY.addListener((observableValue, aBoolean, t1) -> {
            Platform.runLater(() -> setTray());
        });
        ProgConfig.SYSTEM_TRAY_USE_OWN_ICON.addListener((observableValue, aBoolean, t1) -> {
            Platform.runLater(() -> setTray());
        });
        ProgConfig.SYSTEM_TRAY_ICON_PATH.addListener((observableValue, aBoolean, t1) -> {
            Platform.runLater(() -> setTray());
        });
        PListener.addListener(new PListener(PListener.EVENT_TIMER_SECOND, StatusBarController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                try {
                    if (!stopTimer) {
                        String toolTip = DownloadInfosFactory.getTrayInfo(); //da gibts eine max Größe vom Text!!
                        if (systemTray != null) {
                            Arrays.stream(systemTray.getTrayIcons()).sequential().forEach(e -> e.setToolTip(toolTip));
                        }
                    }
                } catch (final Exception ex) {
                    P2Log.errorLog(936251087, ex);
                }
            }
        });
        LoadFilmFactory.getInstance().loadFilmlist.p2LoadNotifier.addListenerLoadFilmlist(new P2LoadListener() {
            @Override
            public void start(P2LoadEvent event) {
                stopTimer = true;
            }

            @Override
            public void finished(P2LoadEvent event) {
                stopTimer = false;
            }
        });
        setTray();
    }

    public SystemTray getSystemTray() {
        return systemTray;
    }

    private void removeTray() {
        if (systemTray != null) {
            Arrays.stream(systemTray.getTrayIcons()).sequential().forEach(e -> systemTray.remove(e));
            systemTray = null;
        }
    }

    private void setTray() {
        if (!SystemTray.isSupported()) {
            return;
        }
        if (!ProgConfig.SYSTEM_TRAY.get()) {
            removeTray();
            return;
        }

        if (systemTray == null) {
            systemTray = SystemTray.getSystemTray();
        }
        setIcon();
    }

    private void setIcon() {
        if (systemTray == null) {
            return;
        }

        for (TrayIcon tr : systemTray.getTrayIcons()) {
            // vorhandene Icons erst mal entfernen
            systemTray.remove(tr);
        }

        Image image = null;
        if (ProgConfig.SYSTEM_TRAY_USE_OWN_ICON.getValue() && !ProgConfig.SYSTEM_TRAY_ICON_PATH.getValueSafe().isEmpty()) {
            File file = new File(ProgConfig.SYSTEM_TRAY_ICON_PATH.getValueSafe());
            if (file.exists()) {
                String resource = ProgConfig.SYSTEM_TRAY_ICON_PATH.getValueSafe();
                image = Toolkit.getDefaultToolkit().getImage(resource);
            }
        }

        if (image == null) {
            URL res = ClassLoader.getSystemResource(ProgConst.PROGRAM_ICON);
            image = Toolkit.getDefaultToolkit().getImage(res);
        }

        TrayIcon trayicon = new TrayIcon(image, "MTPlayer");
        addMenu(trayicon);
        trayicon.setImageAutoSize(true);
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
            P2Log.errorLog(912547030, exception.getMessage());
        }
    }

    private void addMenu(TrayIcon trayicon) {
//        "1 aktiver Download (76,3%; 2.304 # 3019 MB, 6,2 MB/s, 3,2 Minuten verbleibend), 2 wartende Downloads" (so als Beispiel)
//        Die Ausgabe von "1 aktiver ..." könnte man auch als MouseOver-Funktion vom TrayIcon implementieren. Nur so als Idee.

        java.awt.MenuItem miMaxMin = new java.awt.MenuItem("Programm maximieren/minimieren");
        java.awt.MenuItem miConfig = new java.awt.MenuItem("Einstellungen öffnen");
        java.awt.MenuItem miLogfile = new java.awt.MenuItem("LogDatei öffnen");
        java.awt.MenuItem miTray = new java.awt.MenuItem("Tray-Icon ausblenden");
        java.awt.MenuItem miAbout = new java.awt.MenuItem("über dieses Programm");
        java.awt.MenuItem miQuit = new java.awt.MenuItem("Programm Beenden");

        miMaxMin.addActionListener(e -> Platform.runLater(() -> maxMin()));

        miConfig.addActionListener(e -> Platform.runLater(() -> new ConfigDialogController(ProgData.getInstance())));
        miConfig.setEnabled(!ConfigDialogController.dialogIsRunning.getValue());
        ConfigDialogController.dialogIsRunning.addListener((o, u, n) ->
                miConfig.setEnabled(!ConfigDialogController.dialogIsRunning.getValue()));

        miLogfile.addActionListener(e -> Platform.runLater(() -> P2Logger.openLogFile()));
        miTray.addActionListener(e -> Platform.runLater(() -> {
            // vor dem Ausschalten des Tray GUI anzeigen!!
            closeTray();
        }));
        miAbout.addActionListener(e -> Platform.runLater(() -> new AboutDialogController(progData).showDialog()));
        miQuit.addActionListener(e -> Platform.runLater(() -> {
            ProgQuit.quit(false);
        }));

        PopupMenu popupMenu = new PopupMenu();
        popupMenu.add(miMaxMin);
        popupMenu.add(miConfig);
        popupMenu.add(miLogfile);
        if (!P2InfoFactory.getOs().equals(P2InfoFactory.OperatingSystemType.MAC)) {
            // machen unter MAC Probleme
            popupMenu.add(miTray);
        }
        popupMenu.addSeparator();
        popupMenu.add(miAbout);
        if (!P2InfoFactory.getOs().equals(P2InfoFactory.OperatingSystemType.MAC)) {
            // machen unter MAC Probleme
            popupMenu.addSeparator();
            popupMenu.add(miQuit);
        }
        trayicon.setPopupMenu(popupMenu);
    }

    private void closeTray() {
        // dann die Dialoge wieder anzeigen
        showDialog();
        ProgConfig.SYSTEM_TRAY.setValue(false);
    }

    private synchronized void maxMin() {
        if (progData.primaryStage.isShowing()) {
            hideDialog();
        } else {
            showDialog();
        }
    }

    public void hideDialog() {
        Platform.runLater(() -> {
            progData.primaryStage.hide();
        });
        P2DialogExtra.hideAllDialog();
    }

    private void showDialog() {
        Platform.runLater(() -> {
            progData.primaryStage.show();
        });
        P2DialogExtra.showAllDialog();
    }
}
