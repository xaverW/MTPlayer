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

package de.p2tools.mtplayer.gui;

import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.mtplayer.tools.storedFilter.ProgInitFilter;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilter;
import de.p2tools.mtplayer.tools.storedFilter.SelectedFilterFactory;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.Optional;

public class FilmFilterControllerProfiles extends VBox {

    private final ComboBox<SelectedFilter> cboFilterProfiles = new ComboBox<>();
    private final MenuButton mbFilterTools = new MenuButton("");
    private final Button btnLoadFilter = new Button("laden");
    private final Button btnSaveFilter = new Button("speichern");
    private final Button btnNewFilter = new Button("neu anlegen");

    private final ProgData progData;

    private final IntegerProperty filterProp = ProgConfig.FILTER_FILME_SEL_FILTER.getIntegerProperty();

    public FilmFilterControllerProfiles() {
        super();
        progData = ProgData.getInstance();

        setPadding(new Insets(15, 15, 15, 15));
        setSpacing(20);

        initButton();
        filterProfiles();

        initRest();

        progData.storedFilters.filterChangeProperty().addListener((observable, oldValue, newValue) -> checkCboFilter());
        checkCboFilter();
    }

    private void markFilterOk(boolean ok) {
        if (ok) {
            cboFilterProfiles.getStyleClass().removeAll("markFilterOk");
            cboFilterProfiles.getStyleClass().add("markFilterOk");
        } else {
            cboFilterProfiles.getStyleClass().removeAll("markFilterOk");
        }
    }

    private void initButton() {
        btnLoadFilter.setOnAction(a -> loadFilter());
        btnLoadFilter.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());
        btnLoadFilter.setGraphic(new ProgIcons().FX_ICON_FILTER_FILM_LOAD);
        btnLoadFilter.setText("");
        btnLoadFilter.setTooltip(new Tooltip("Filterprofil wieder laden"));

        btnSaveFilter.setOnAction(a -> {
            if (cboFilterProfiles.getSelectionModel().getSelectedItem() == null
                    || PAlert.showAlertOkCancel("Speichern", "Filterprofil speichern",
                    "Soll das Filterprofil überschrieben werden?")) {
                saveFilter();
            }
        });
        btnSaveFilter.setGraphic(new ProgIcons().FX_ICON_FILTER_FILM_SAVE);
        btnSaveFilter.setText("");
        btnSaveFilter.setTooltip(new Tooltip("Aktuelle Filtereinstellung als Filterprofil speichern"));

        btnNewFilter.setOnAction(a -> newFilter());
        btnNewFilter.setGraphic(new ProgIcons().FX_ICON_FILTER_FILM_NEW);
        btnNewFilter.setText("");
        btnNewFilter.setTooltip(new Tooltip("Aktuelle Filtereinstellung als neues Filterprofil anlegen"));
    }

    private void filterProfiles() {
        // Filterprofile einrichten
        cboFilterProfiles.setItems(progData.storedFilters.getStordeFilterList());
        cboFilterProfiles.setTooltip(new Tooltip("Gespeicherte Filterprofile können\n" +
                "hier geladen werden"));

        final StringConverter<SelectedFilter> converter = new StringConverter<SelectedFilter>() {
            @Override
            public String toString(SelectedFilter selFilter) {
                return selFilter == null ? "" : selFilter.getName();
            }

            @Override
            public SelectedFilter fromString(String id) {
                final int i = cboFilterProfiles.getSelectionModel().getSelectedIndex();
                return progData.storedFilters.getStordeFilterList().get(i);
            }
        };
        cboFilterProfiles.setConverter(converter);

        final MenuItem miLoad = new MenuItem("aktuelles Filterprofil wieder laden");
        miLoad.setOnAction(e -> loadFilter());
        miLoad.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miRename = new MenuItem("aktuelles Filterprofil umbenennen");
        miRename.setOnAction(e -> renameFilter());
        miRename.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miDel = new MenuItem("aktuelles Filterprofil löschen");
        miDel.setOnAction(e -> delFilter());
        miDel.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miDelAll = new MenuItem("alle Filterprofile löschen");
        miDelAll.setOnAction(e -> delAllFilter());
        miDelAll.disableProperty().bind(Bindings.size(cboFilterProfiles.getItems()).isEqualTo(0));

        final MenuItem miSave = new MenuItem("Filtereinstellungen in aktuellem Filterprofil speichern");
        miSave.setOnAction(e -> saveFilter());
        miSave.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miNew = new MenuItem("Filtereinstellungen in neuem Filterprofil speichern");
        miNew.setOnAction(e -> newFilter());

        final MenuItem miAbo = new MenuItem("aus den Filtereinstellungen ein Abo erstellen");
        miAbo.setOnAction(a -> {
            SelectedFilter selectedFilter = progData.storedFilters.getActFilterSettings();
            progData.aboList.addNewAbo(selectedFilter);
        });

        final MenuItem miReset = new MenuItem("alle Filterprofile wieder herstellen");
        miReset.setOnAction(e -> resetFilter());

        mbFilterTools.setGraphic(new ProgIcons().ICON_BUTTON_MENU);
        mbFilterTools.getItems().addAll(miLoad, miRename, miDel, miDelAll, miSave, miNew, miAbo, new SeparatorMenuItem(), miReset);
        mbFilterTools.setTooltip(new Tooltip("Gespeicherte Filterprofile bearbeiten"));

        cboFilterProfiles.getSelectionModel().select(filterProp.get());
        filterProp.bind(cboFilterProfiles.getSelectionModel().selectedIndexProperty());

        cboFilterProfiles.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadFilter();
            }
        });

    }

    private void initRest() {
        // Filterprofile
        HBox hBox = new HBox(10);
        btnLoadFilter.setMaxWidth(Double.MAX_VALUE);
        btnSaveFilter.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnLoadFilter, Priority.ALWAYS);
        HBox.setHgrow(btnSaveFilter, Priority.ALWAYS);
        hBox.getChildren().addAll(btnLoadFilter, btnNewFilter, btnSaveFilter);
        getChildren().add(hBox);

        cboFilterProfiles.setMaxWidth(Double.MAX_VALUE);
        VBox vBox = new VBox(3);
        vBox.getChildren().addAll(new Label("Filterprofile:"), cboFilterProfiles);
        getChildren().add(vBox);

        final Button btnHelp = PButton.helpButton("Filter", HelpText.GUI_FILM_FILTER);
        hBox = new HBox(10);
        hBox.getChildren().addAll(mbFilterTools, PGuiTools.getHBoxGrower(), btnHelp);
        getChildren().add(hBox);
    }

    private void loadFilter() {
        progData.storedFilters.setActFilterSettings(cboFilterProfiles.getSelectionModel().getSelectedItem());
    }

    private void saveFilter() {
        final SelectedFilter sf = cboFilterProfiles.getSelectionModel().getSelectedItem();
        if (sf == null) {
            newFilter();
        } else {
            progData.storedFilters.saveStoredFilter(sf);
            checkCboFilter();
        }
    }

    private void delFilter() {
        progData.storedFilters.removeStoredFilter(cboFilterProfiles.getSelectionModel().getSelectedItem());
        cboFilterProfiles.getSelectionModel().selectFirst();
    }

    private void delAllFilter() {
        if (PAlert.showAlertOkCancel("Löschen", "Filterprofile löschen",
                "Sollen alle Filterprofile gelöscht werden?")) {
            progData.storedFilters.removeAllStoredFilter();
            cboFilterProfiles.getSelectionModel().selectFirst();
        }
    }

    private void resetFilter() {
        if (PAlert.showAlertOkCancel("Zurücksetzen", "Filterprofile zurücksetzen",
                "Sollen alle Filterprofile gelöscht " +
                        "und durch die Profile vom ersten Programmstart " +
                        "ersetzt werden?")) {
            progData.storedFilters.getStordeFilterList().clear();
            ProgInitFilter.setProgInitFilter();
            cboFilterProfiles.getSelectionModel().selectFirst();
        }
    }

    private void newFilter() {
        final TextInputDialog dialog = new TextInputDialog(progData.storedFilters.getNextName());
        dialog.setTitle("Filterprofilname");
        dialog.setHeaderText("Den Namen des Filterprofils vorgeben");
        dialog.setContentText("Name:");
        dialog.setResizable(true);
        dialog.initOwner(progData.primaryStage);

        final Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            progData.storedFilters.addNewStoredFilter(result.get());
            cboFilterProfiles.getSelectionModel().selectLast();
        }
    }

    private void renameFilter() {
        final SelectedFilter sf = cboFilterProfiles.getSelectionModel().getSelectedItem();
        if (sf == null) {
            return;
        }
        final TextInputDialog dialog = new TextInputDialog(sf.getName());
        dialog.setTitle("Filterprofil umbenennen");
        dialog.setHeaderText("Den Namen des Filterprofils ändern");
        dialog.setContentText("Neuer Name:");
        dialog.setResizable(true); // sonst geht der Dialog nicht "auf" und lässt sich nicht vergrößern, bug??
        dialog.initOwner(progData.primaryStage);

        final Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            sf.setName(result.get());
            cboFilterProfiles.getSelectionModel().select(sf);
        }
    }

    private void checkCboFilter() {
        SelectedFilter sf = progData.storedFilters.getActFilterSettings();
        SelectedFilter sfCbo = cboFilterProfiles.getSelectionModel().getSelectedItem();
        if (SelectedFilterFactory.compareFilterWithoutNameOfFilter(sf, sfCbo)) {
            markFilterOk(true);
        } else {
            markFilterOk(false);
        }
    }

}
