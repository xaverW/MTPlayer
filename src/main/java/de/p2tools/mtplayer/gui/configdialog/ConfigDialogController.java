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
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.film.LoadFilmFactory;
import de.p2tools.mtplayer.controller.filmfilter.BlacklistFilterFactory;
import de.p2tools.mtplayer.gui.configdialog.setdata.ControllerSet;
import de.p2tools.mtplayer.gui.tools.Listener;
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

    private TabPane tabPane = new TabPane();
    private Button btnOk = new Button("_Ok");
    private Button btnApply = new Button("_Anwenden");
    private String geo = ProgConfig.SYSTEM_GEO_HOME_PLACE.get();
    private BooleanProperty blackChanged = new SimpleBooleanProperty(false);
    private BooleanProperty diacriticChanged = new SimpleBooleanProperty(false);

    private ControllerConfig controllerConfig;
    private ControllerFilm controllerFilm;
    private ControllerBlack controllerBlack;
    private ControllerDownload controllerDownload;
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

        init(false);
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

        init(false);
    }

    @Override
    public void make() {
//        setMaskerPane();

        this.getMaskerPane().visibleProperty().bind(ProgData.getInstance().maskerPane.visibleProperty());
//        progData.maskerPane.visibleProperty().addListener((u, o, n) -> {
//            //bind geht da nicht: wird im Listener gesetzt->todo nur Button dort setzen
//            setMaskerPane();
//        });
        Button btnStop = getMaskerPane().getButton();
        getMaskerPane().setButtonText("");
        btnStop.setGraphic(ProgIcons.Icons.ICON_BUTTON_STOP.getImageView());
        btnStop.setOnAction(a -> LoadFilmFactory.getInstance().loadFilmlist.setStop(true));
        listener = new ListenerLoadFilmlist() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                if (event.progress == ListenerLoadFilmlist.PROGRESS_INDETERMINATE) {
                    // ist dann die gespeicherte Filmliste
//                    getMaskerPane().setMaskerVisible(true, false);
                    getMaskerPane().setButtonVisible(false);
                } else {
//                    getMaskerPane().setMaskerVisible(true, true);
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
//                getMaskerPane().setMaskerVisible(true, false);
                getMaskerPane().setButtonVisible(false);
                getMaskerPane().setMaskerProgress(ListenerLoadFilmlist.PROGRESS_INDETERMINATE, "Filmliste verarbeiten");
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
//                getMaskerPane().setMaskerVisible(false);
            }
        };
        LoadFilmFactory.getInstance().loadFilmlist.addListenerLoadFilmlist(listener);


        VBox.setVgrow(tabPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(tabPane);
        getVBoxCont().setPadding(new Insets(0));

        addOkCancelApplyButtons(btnOk, null, btnApply);
        btnOk.setOnAction(a -> close());
        btnApply.setOnAction(a -> apply());

        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) -> updateCss());
        initPanel();
    }

    public void apply() {
        if (!geo.equals(ProgConfig.SYSTEM_GEO_HOME_PLACE.get())) {
            // dann hat sich der Geo-Standort geändert
            progData.filmlist.markGeoBlocked();
        }

        if (blackChanged.get() && !LoadFilmFactory.loadFilmlist.getPropLoadFilmlist()) {
            // sonst hat sich nichts geändert oder wird dann eh gemacht
            new Thread(() -> BlacklistFilterFactory.markFilmBlack(true)).start();
        }

        if (diacriticChanged.getValue() && ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue()) {
            //Diakritika entfernen, macht nur dann Sinn
            //zum Einfügen der Diakritika muss eine neue Filmliste geladen werden
            new Thread(() -> {
                ProgData.getInstance().maskerPane.setMaskerText("Diakritika entfernen");
                ProgData.getInstance().maskerPane.setMaskerVisible(true, true, false);
                FilmFactory.flattenDiacritic(progData.filmlist);
                Listener.notify(Listener.EVENT_DIACRITIC_CHANGED, ConfigDialogController.class.getSimpleName());
                ProgData.getInstance().maskerPane.switchOffMasker();
            }).start();
        }
    }

    @Override
    public void close() {
        apply();
        controllerConfig.close();
        controllerFilm.close();
        controllerBlack.close();
        controllerDownload.close();
        controllerSet.close();

        Listener.notify(Listener.EVEMT_SETDATA_CHANGED, ConfigDialogController.class.getSimpleName());
        LoadFilmFactory.getInstance().loadFilmlist.removeListenerLoadFilmlist(listener);
        dialogIsRunning.setValue(false);
        super.close();
    }

//    private void setMaskerPane() {
//        if (progData.maskerPane.isVisible()) {
//            this.setMaskerVisible(true);
//        } else {
//            this.setMaskerVisible(false);
//        }
//    }

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

            controllerFilm = new ControllerFilm(this.getStage(), diacriticChanged);
            tab = new Tab("Filmliste laden");
            tab.setClosable(false);
            tab.setContent(controllerFilm);
            tabPane.getTabs().add(tab);

            controllerBlack = new ControllerBlack(this.getStage(), blackChanged);
            tab = new Tab("Blacklist");
            tab.setClosable(false);
            tab.setContent(controllerBlack);
            tabPane.getTabs().add(tab);

            controllerDownload = new ControllerDownload(this.getStage());
            tab = new Tab("Download");
            tab.setClosable(false);
            tab.setContent(controllerDownload);
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
