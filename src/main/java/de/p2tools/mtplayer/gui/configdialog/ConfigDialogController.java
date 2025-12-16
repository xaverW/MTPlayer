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

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgInfos;
import de.p2tools.mtplayer.controller.data.blackdata.BlacklistFilterFactory;
import de.p2tools.mtplayer.gui.configdialog.panesetdata.ControllerSet;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.mediathek.film.FilmFactory;
import de.p2tools.p2lib.mediathek.filmlistload.P2LoadConst;
import de.p2tools.p2lib.mediathek.filmlistreadwrite.P2WriteFilmlistJson;
import de.p2tools.p2lib.tools.log.P2Log;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class ConfigDialogController extends P2DialogExtra {

    private final TabPane tabPane = new TabPane();
    private final Button btnOk = new Button("_Ok");
    private final Button btnApply = new Button("_Anwenden");
    private final String geo = ProgConfig.SYSTEM_GEO_HOME_PLACE.get();
    private final BooleanProperty blackChanged = new SimpleBooleanProperty(false);
    private final BooleanProperty setDataChanged = new SimpleBooleanProperty(false);
    private final BooleanProperty diacriticChanged = new SimpleBooleanProperty(false);

    private ControllerConfig controllerConfig;
    private ControllerLoad controllerLoad;
    private ControllerBlackList controllerBlackList;
    private ControllerDownload controllerDownload;
    private ControllerMedia controllerMedia;
    private ControllerSet controllerSet;

    private IntegerProperty propSelectedTab = ProgConfig.SYSTEM_CONFIG_DIALOG_TAB;
    private final ProgData progData;
    private boolean blackListDialog = false;
    public static BooleanProperty dialogIsRunning = new SimpleBooleanProperty(false);

    public ConfigDialogController(ProgData progData) {
        super(progData.primaryStage, ProgConfig.CONFIG_DIALOG_SIZE, "Einstellungen",
                true, true, true, DECO.NO_BORDER, true);
        this.progData = progData;
        dialogIsRunning.setValue(true);
        btnApply.setVisible(false);

        init(true);
    }

    public ConfigDialogController(ProgData progData, boolean blackListDialog) {
        super(progData.primaryStage, ProgConfig.BLACK_DIALOG_SIZE, "Blacklist bearbeiten",
                false, true, true, DECO.NO_BORDER, true);

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

        initPanel();
    }

    private void onlyApply() {
        if (!ProgData.FILMLIST_IS_DOWNLOADING.get()) {
            //dann wird die Blacklist immer neu gemacht, sonst wirds dann eh gemacht
            new Thread(() -> {
                BlacklistFilterFactory.markFilmsIfBlack(true);
                blackChanged.setValue(false);
            }).start();
        }
    }

    private void onlyClose() {
        if (!geo.equals(ProgConfig.SYSTEM_GEO_HOME_PLACE.get())) {
            //dann hat sich der Geo-Standort geändert
            P2LoadConst.GEO_HOME_PLACE = ProgConfig.SYSTEM_GEO_HOME_PLACE.getValue();
            progData.filmList.markGeoBlocked();
        }

        if (blackChanged.get() && !ProgData.FILMLIST_IS_DOWNLOADING.get()) {
            // sonst hat sich nichts geändert oder wird dann eh gemacht
            new Thread(() -> BlacklistFilterFactory.markFilmsIfBlack(true)).start();
        }

        if (diacriticChanged.getValue() && ProgConfig.SYSTEM_REMOVE_DIACRITICS.getValue()) {
            //Diakritika entfernen, macht nur dann Sinn
            //zum Einfügen der Diakritika muss eine neue Filmliste geladen werden
            new Thread(() -> {
                ProgData.getInstance().maskerPane.setMaskerText("Diakritika entfernen");
                ProgData.getInstance().maskerPane.setMaskerVisible(true, true, false);
                FilmFactory.flattenDiacritic(progData.filmList);
                new P2WriteFilmlistJson().write(ProgInfos.getLocalFilmListFile(), progData.filmList);

                progData.pEventHandler.notifyListener(PEvents.EVENT_DIACRITIC_CHANGED);
                ProgData.getInstance().maskerPane.switchOffMasker();
            }).start();
        }

        if (setDataChanged.get()) {
            progData.pEventHandler.notifyListener(PEvents.EVENT_SET_DATA_CHANGED);
        }
        close();
    }

    @Override
    public void close() {
        controllerConfig.close();
        controllerLoad.close();
        controllerBlackList.close();
        controllerDownload.close();
        controllerMedia.close();
        controllerSet.close();

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

            controllerLoad = new ControllerLoad(this.getStage(), diacriticChanged);
            tab = new Tab("Filme - Audios");
            tab.setClosable(false);
            tab.setContent(controllerLoad);
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
            tab.selectedProperty().addListener((u, o, n) -> setDataChanged.set(true));
            if (!blackListDialog) {
                tabPane.getTabs().add(tab);
            }

            tabPane.getSelectionModel().select(propSelectedTab.get());
            tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
                // readOnlyBinding!!
                propSelectedTab.setValue(newValue);
            });

        } catch (final Exception ex) {
            P2Log.errorLog(784459510, ex);
        }
    }
}
