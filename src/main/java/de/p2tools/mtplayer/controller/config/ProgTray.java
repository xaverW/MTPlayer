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
import de.p2tools.mtplayer.controller.data.download.DownloadInfosFactory;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.mtplayer.controller.filmlist.loadFilmlist.ListenerLoadFilmlist;
import de.p2tools.mtplayer.gui.StatusBarController;
import de.p2tools.mtplayer.gui.configDialog.ConfigDialogController;
import de.p2tools.mtplayer.gui.dialog.AboutDialogController;
import de.p2tools.mtplayer.gui.tools.Listener;
import de.p2tools.p2Lib.dialogs.dialog.PDialog;
import de.p2tools.p2Lib.dialogs.dialog.PDialogFactory;
import de.p2tools.p2Lib.guiTools.PGuiSize;
import de.p2tools.p2Lib.tools.log.PLog;
import de.p2tools.p2Lib.tools.log.PLogger;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class ProgTray {
    private final ProgData progData;
    BooleanProperty propTray = ProgConfig.SYSTEM_TRAY;
    BooleanProperty propTrayUseOwnIcon = ProgConfig.SYSTEM_TRAY_USE_OWN_ICON;
    StringProperty propTrayIconPath = ProgConfig.SYSTEM_TRAY_ICON_PATH;
    private SystemTray systemTray = null;
    private boolean stopTimer = false;

    public ProgTray(ProgData progData) {
        this.progData = progData;
    }

    public void initProgTray() {
        propTray.addListener((observableValue, aBoolean, t1) -> {
            Platform.runLater(() -> setTray());
        });
        propTrayUseOwnIcon.addListener((observableValue, aBoolean, t1) -> {
            Platform.runLater(() -> setTray());
        });
        propTrayIconPath.addListener((observableValue, aBoolean, t1) -> {
            Platform.runLater(() -> setTray());
        });
        Listener.addListener(new Listener(Listener.EVENT_TIMER, StatusBarController.class.getSimpleName()) {
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
                    PLog.errorLog(936251087, ex);
                }
            }
        });
        progData.loadFilmlist.addListenerLoadFilmlist(new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                stopTimer = true;
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                stopTimer = false;
            }
        });
        setTray();
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
        if (!propTray.get()) {
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
            //vorhandene Icons erst mal entfernen
            systemTray.remove(tr);
        }

        Image image;
        if (propTrayUseOwnIcon.getValue() && !propTrayIconPath.getValueSafe().isEmpty()) {
            String resource = propTrayIconPath.getValueSafe();
            image = Toolkit.getDefaultToolkit().getImage(resource);
        } else {
            String resource = "/de/p2tools/mtplayer/res/P2_24.png";
            URL res = getClass().getResource(resource);
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
            PLog.errorLog(912547030, exception.getMessage());
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
        miConfig.addActionListener(e -> Platform.runLater(() -> ConfigDialogController.getInstanceAndShow()));
        miLogfile.addActionListener(e -> Platform.runLater(() -> PLogger.openLogFile()));
        miTray.addActionListener(e -> Platform.runLater(() -> {
            //vor dem Ausschalten des Tray GUI anzeigen!!
            closeTray();
        }));
        miAbout.addActionListener(e -> Platform.runLater(() -> new AboutDialogController(progData)));
        miQuit.addActionListener(e -> Platform.runLater(() -> {
            ProgQuit.quit(false);
        }));

        PopupMenu popupMenu = new PopupMenu();
        popupMenu.add(miMaxMin);
        popupMenu.add(miConfig);
        popupMenu.add(miLogfile);
        popupMenu.add(miTray);

        popupMenu.addSeparator();
        popupMenu.add(miAbout);
        popupMenu.addSeparator();
        popupMenu.add(miQuit);

        trayicon.setPopupMenu(popupMenu);
    }

    private void closeTray() {
        //dann die Dialoge wieder anzeigen
        showDialog();
        propTray.setValue(false);
    }

    private ArrayList<PDialog> dialogList = new ArrayList<>();

    public synchronized void addDialog(PDialog pDialog) {
        boolean found = false;
        for (PDialog dialog : dialogList) {
            if (dialog.equals(pDialog)) {
                found = true;
            }
        }
        if (!found) {
            dialogList.add(pDialog);
        }
    }

    public synchronized void removeDialog(PDialog pDialog) {
        Platform.runLater(() -> {
            //sonst ist evtl. noch ein showDialog/closeDialog im Gang!!
            dialogList.remove(pDialog);
        });
    }

    private synchronized void maxMin() {
        Platform.runLater(() -> {
            if (progData.primaryStage.isShowing()) {
                closeDialog();
            } else {
                showDialog();
            }
        });
    }

    private void closeDialog() {
        PGuiSize.getSizeStage(ProgConfig.SYSTEM_SIZE_GUI, ProgData.getInstance().primaryStage);
        progData.primaryStage.close();
        dialogList.stream().forEach(pDialog -> {
            pDialog.hide();
        });
    }

    private void showDialog() {
        PDialogFactory.showDialog(progData.primaryStage, ProgConfig.SYSTEM_SIZE_GUI);
        dialogList.stream().forEach(pDialog -> {
            pDialog.showDialog();
        });
    }
}
