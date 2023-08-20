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

package de.p2tools.mtplayer.gui.configdialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIconsMTPlayer;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.gui.configdialog.panesetdata.ControllerSet;
import de.p2tools.mtplayer.gui.tools.MTListener;
import de.p2tools.p2lib.dialogs.dialog.PDialogExtra;
import de.p2tools.p2lib.mtfilm.film.FilmFactory;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerFilmlistLoadEvent;
import de.p2tools.p2lib.mtfilm.loadfilmlist.ListenerLoadFilmlist;
import de.p2tools.p2lib.tools.log.PLog;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class ConfigDialogController extends PDialogExtra {

    private final TabPane tabPane = new TabPane();
    private final Button btnOk = new Button("_Ok");
    private final Button btnApply = new Button("_Anwenden");
    private final String geo = ProgConfig.SYSTEM_GEO_HOME_PLACE.get();
    private final BooleanProperty blackChanged = new SimpleBooleanProperty(false);
    private final BooleanProperty diacriticChanged = new SimpleBooleanProperty(false);

    private ControllerConfig controllerConfig;
    private ControllerLoadFilmList controllerLoadFilmList;
    private ControllerBlackList controllerBlackList;
    private ControllerDownload controllerDownload;
    private ControllerAbo controllerAbo;
    private ControllerMedia controllerMedia;
    private ControllerSet controllerSet;

    private IntegerProperty propSelectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_TAB;
    private ListenerLoadFilmlist listener;
    private final ProgData progData;
    private boolean blackListDialog = false;
    public static BooleanProperty dialogIsRunning = new SimpleBooleanProperty(false);

    public ConfigDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConfig.CONFIG_DIALOG_SIZE, "Einstellungen",
                true, false, DECO.NO_BORDER, true);
        this.progData = progData;
        dialogIsRunning.setValue(true);
        btnApply.setVisible(false);

        init(true);
    }

    public ConfigDialogController(ProgData progData, boolean blackListDialog) {
        super(progData.primaryStage, ProgConfig.BLACK_DIALOG_SIZE,
                "Blacklist bearbeiten", false, false, DECO.NO_BORDER, true);

        this.progData = progData;
        this.blackListDialog = blackListDialog;
        dialogIsRunning.setValue(true);
        if (blackListDialog) {
            propSelectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_BLACKLIST_TAB;
        } else {
            btnApply.setVisible(false);
        }

        init(true);
    }

    @Override
    public void make() {
        this.getMaskerPane().visibleProperty().bind(ProgData.getInstance().maskerPane.visibleProperty());
        Button btnStop = getMaskerPane().getButton();
        getMaskerPane().setButtonText("");
        btnStop.setGraphic(ProgIconsMTPlayer.ICON_BUTTON_STOP.getImageView());
        btnStop.setOnAction(a -> LoadFilmFactory.getInstance().loadFilmlist.setStop(true));
        listener = new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                if (event.progress == ListenerLoadFilmlist.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
                    getMaskerPane().setButtonVisible(false);
                } else {
                    getMaskerPane().setButtonVisible(true);
                }
                getMaskerPane().setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void progress(ListenerFilmlistLoadEvent event) {
                getMaskerPane().setMaskerProgress(event.progress, event.text);
            }

            @Override
            public void loaded(ListenerFilmlistLoadEvent event) {
                getMaskerPane().setButtonVisible(false);
                getMaskerPane().setMaskerProgress(ListenerLoadFilmlist.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
            }
        };
        LoadFilmFactory.getInstance().loadFilmlist.filmListLoadNotifier.addListenerLoadFilmlist(listener);


        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(tabPane);
        getVBoxCont().setPadding(new Insets(0));

        if (btnApply.isVisible()) {
            // nur dann einfügen
            addOkCancelApplyButtons(btnOk, null, btnApply);
            btnApply.setOnAction(a -> onlyApply());
        } else {
            addOkButton(btnOk);
        }
        btnOk.setOnAction(a -> onlyClose());

        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        initPanel();
    }

    private void onlyApply() {
        if (!LoadFilmFactory.getInstance().loadFilmlist.getPropLoadFilmlist()) {
            //dann wird die Blacklist immer neu gemacht, sonst wirds dann eh gemacht
            new Thread(() -> {
                BlacklistFilterFactory.markFilmBlack(true);
                blackChanged.setValue(false);
            }).start();
        }
    }

    private void onlyClose() {
        if (!geo.equals(ProgConfig.SYSTEM_GEO_HOME_PLACE.get())) {
            //dann hat sich der Geo-Standort geändert
            progData.filmList.markGeoBlocked();
        }

        if (blackChanged.get() && !LoadFilmFactory.getInstance().loadFilmlist.getPropLoadFilmlist()) {
            // sonst hat sich nichts geändert oder wird dann eh gemacht
            new Thread(() -> BlacklistFilterFactory.markFilmBlack(true)).start();
        }

        if (diacriticChanged.getValue() && ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue()) {
            //Diakritika entfernen, macht nur dann Sinn
            //zum Einfügen der Diakritika muss eine neue Filmliste geladen werden
            new Thread(() -> {
                ProgData.getInstance().maskerPane.setMaskerText("Diakritika entfernen");
                ProgData.getInstance().maskerPane.setMaskerVisible(true, true, false);
                FilmFactory.flattenDiacritic(progData.filmList);
                MTListener.notify(MTListener.EVENT_DIACRITIC_CHANGED, ConfigDialogController.class.getSimpleName());
                ProgData.getInstance().maskerPane.switchOffMasker();
            }).start();
        }
        close();
    }

    @Override
    public void close() {
        controllerConfig.close();
        controllerLoadFilmList.close();
        controllerBlackList.close();
        controllerDownload.close();
        controllerAbo.close();
        controllerMedia.close();
        controllerSet.close();

        MTListener.notify(MTListener.EVENT_SET_DATA_CHANGED, ConfigDialogController.class.getSimpleName());
        LoadFilmFactory.getInstance().loadFilmlist.filmListLoadNotifier.removeListenerLoadFilmlist(listener);
        dialogIsRunning.setValue(false);
        super.close();
    }

    private void initPanel() {
        try {
            Tab tab;
            controllerConfig = new ControllerConfig(this.getStage());
            tab = new Tab("Allgemein");
            tab.setClosable(false);
            tab.setContent(controllerConfig);
            if (!blackListDialog) {
                tabPane.getTabs().add(tab);
            }

            controllerLoadFilmList = new ControllerLoadFilmList(this.getStage(), diacriticChanged);
            tab = new Tab("Filmliste laden");
            tab.setClosable(false);
            tab.setContent(controllerLoadFilmList);
            tabPane.getTabs().add(tab);

            controllerBlackList = new ControllerBlackList(this.getStage(), blackChanged);
            tab = new Tab("Blacklist");
            tab.setClosable(false);
            tab.setContent(controllerBlackList);
            tabPane.getTabs().add(tab);

            controllerDownload = new ControllerDownload(this.getStage());
            tab = new Tab("Download");
            tab.setClosable(false);
            tab.setContent(controllerDownload);
            if (!blackListDialog) {
                tabPane.getTabs().add(tab);
            }

            controllerAbo = new ControllerAbo(this.getStage());
            tab = new Tab("Abo");
            tab.setClosable(false);
            tab.setContent(controllerAbo);
            if (!blackListDialog) {
                tabPane.getTabs().add(tab);
            }

            controllerMedia = new ControllerMedia(this.getStage());
            tab = new Tab("Mediensammlung");
            tab.setClosable(false);
            tab.setContent(controllerMedia);
            if (!blackListDialog) {
                tabPane.getTabs().add(tab);
            }

            controllerSet = new ControllerSet(this.getStage());
            tab = new Tab("Aufzeichnen/Abspielen");
            tab.setClosable(false);
            tab.setContent(controllerSet);
            if (!blackListDialog) {
                tabPane.getTabs().add(tab);
            }

            tabPane.getSelectionModel().select(propSelectedTab.get());
            tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                // readOnlyBinding!!
                propSelectedTab.setValue(newValue);
            });

        } catch (final Exception ex) {
            PLog.errorLog(784459510, ex);
        }
    }
}
