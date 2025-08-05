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

package de.p2tools.mtplayer.gui.filter.audio;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.abo.AboListFactory;
import de.p2tools.mtplayer.controller.filter.FilmFilter;
import de.p2tools.mtplayer.controller.filter.audio.AudioFilterSamples;
import de.p2tools.mtplayer.gui.filter.FilterController;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2SeparatorComboBox;
import de.p2tools.p2lib.p2event.P2Listener;
import javafx.beans.binding.Bindings;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.Optional;

public class AudioFilterControllerProfiles extends VBox {

    private final ComboBox<FilmFilter> cboFilterProfiles = new P2SeparatorComboBox<>();
    private final MenuButton mbFilterTools = new MenuButton("");
    private final Button btnLoadFilter = new Button("");
    private final Button btnSaveFilter = new Button("");
    private final Button btnNewFilter = new Button("");

    private final ProgData progData;

    public AudioFilterControllerProfiles() {
        super();
        progData = ProgData.getInstance();

        setSpacing(FilterController.FILTER_SPACING_TEXTFILTER);
        initButton();
        filterProfiles();
        initRest();

        // ist zum Markieren, ob sich der eingestellte Filter geändert hat
        progData.pEventHandler.addListener(new P2Listener(PEvents.EVENT_FILTER_AUDIO_CHANGED) {
            @Override
            public void pingGui() {
                checkCboFilter();
            }
        });
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
        btnLoadFilter.setGraphic(ProgIcons.ICON_FILTER_LOAD.getImageView());
        btnLoadFilter.setTooltip(new Tooltip("Filterprofil wieder laden"));

        btnSaveFilter.setOnAction(a -> {
            if (cboFilterProfiles.getSelectionModel().getSelectedItem() == null
                    || P2Alert.showAlertOkCancel("Speichern", "Filterprofil speichern",
                    "Soll das Filterprofil überschrieben werden?")) {
                saveFilter();
            }
        });
        btnSaveFilter.setGraphic(ProgIcons.ICON_FILTER_SAVE.getImageView());
        btnSaveFilter.setTooltip(new Tooltip("Aktuelle Filtereinstellung als Filterprofil speichern"));

        btnNewFilter.setOnAction(a -> newFilter());
        btnNewFilter.setGraphic(ProgIcons.ICON_FILTER_NEW.getImageView());
        btnNewFilter.setTooltip(new Tooltip("Aktuelle Filtereinstellung als neues Filterprofil anlegen"));
    }

    private void filterProfiles() {
        // Filterprofile einrichten
        cboFilterProfiles.setItems(progData.filterWorkerAudio.getFilmFilterList());
        cboFilterProfiles.setTooltip(new Tooltip("Gespeicherte Filterprofile können\n" +
                "hier geladen werden"));

        final MenuItem miLoad = new MenuItem("Aktuelles Filterprofil wieder laden");
        miLoad.setOnAction(e -> loadFilter());
        miLoad.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miRename = new MenuItem("Aktuelles Filterprofil umbenennen");
        miRename.setOnAction(e -> renameFilter());
        miRename.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miDel = new MenuItem("Aktuelles Filterprofil löschen");
        miDel.setOnAction(e -> delFilter());
        miDel.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miDelAll = new MenuItem("Alle Filterprofile löschen");
        miDelAll.setOnAction(e -> delAllFilter());
        miDelAll.disableProperty().bind(Bindings.size(cboFilterProfiles.getItems()).isEqualTo(0));

        final MenuItem miSave = new MenuItem("Filtereinstellungen in aktuellem Filterprofil speichern");
        miSave.setOnAction(e -> saveFilter());
        miSave.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miNew = new MenuItem("Filtereinstellungen in neuem Filterprofil speichern");
        miNew.setOnAction(e -> newFilter());

        final MenuItem miAbo = new MenuItem("Aus den Filtereinstellungen ein Abo erstellen");
        miAbo.setOnAction(a -> AboListFactory.addNewAboFromFilterButton());
        miAbo.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miResort = new MenuItem("Filterprofile sortieren");
        miResort.setOnAction(e -> new AudioFilterSortDialog(progData).showDialog());

        final MenuItem miFilterDialog = new MenuItem("Filterprofile in eigenem Fenster anzeigen");
        miFilterDialog.setOnAction(e -> new AudioFilterDialog(progData).showDialog());

        final MenuItem miAddStandard = new MenuItem("Standard-Filterprofile anhängen");
        miAddStandard.setOnAction(e -> resetFilter(false));

        final MenuItem miReset = new MenuItem("Alle Filterprofile wieder herstellen");
        miReset.setOnAction(e -> resetFilter(true));

        mbFilterTools.setGraphic(ProgIcons.ICON_TOOLBAR_MENU.getImageView());
        mbFilterTools.getItems().addAll(miLoad, miRename, miDel, miDelAll, miSave, miNew, miAbo,
                new SeparatorMenuItem(), miResort, miFilterDialog,
                new SeparatorMenuItem(), miAddStandard, miReset);
        mbFilterTools.setTooltip(new Tooltip("Gespeicherte Filterprofile bearbeiten"));

        cboFilterProfiles.getSelectionModel().select(ProgConfig.FILTER_AUDIO_SEL_FILTER.get());
        ProgConfig.FILTER_AUDIO_SEL_FILTER.bind(cboFilterProfiles.getSelectionModel().selectedIndexProperty());

        cboFilterProfiles.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadFilter();
            }
        });

        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) ->
                cboFilterProfiles.setCellFactory(new ListViewListCellCallback()));
        cboFilterProfiles.setCellFactory(new ListViewListCellCallback());
    }


    private void initRest() {
        // Filterprofile
        HBox hBox = new HBox(P2LibConst.DIST_BUTTON);
        btnLoadFilter.setMaxWidth(Double.MAX_VALUE);
        btnSaveFilter.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnLoadFilter, Priority.ALWAYS);
        HBox.setHgrow(btnSaveFilter, Priority.ALWAYS);
        hBox.getChildren().addAll(btnLoadFilter, btnNewFilter, btnSaveFilter);
        getChildren().add(hBox);

        cboFilterProfiles.setMaxWidth(Double.MAX_VALUE);
        VBox vBox = new VBox(2);
        vBox.getChildren().addAll(new Label("Filterprofile:"), cboFilterProfiles);
        getChildren().add(vBox);

        final Button btnHelp = P2Button.helpButton("Filter", HelpText.FILTER_INFO_PROFILE);
        hBox = new HBox(10);
        hBox.getChildren().addAll(mbFilterTools, P2GuiTools.getHBoxGrower(), btnHelp);
        getChildren().add(hBox);
    }

    private void loadFilter() {
        progData.filterWorkerAudio.setActFilterSettings(cboFilterProfiles.getSelectionModel().getSelectedItem());
    }

    private void saveFilter() {
        final FilmFilter sf = cboFilterProfiles.getSelectionModel().getSelectedItem();
        if (sf == null) {
            newFilter();
        } else {
            progData.filterWorkerAudio.getFilmFilterList().saveStoredFilter(sf);
            checkCboFilter();
        }
    }

    private void delFilter() {
        FilmFilter sf = cboFilterProfiles.getSelectionModel().getSelectedItem();
        if (sf == null) {
            P2Alert.showInfoNoSelection();
            return;
        }

        if (progData.filterWorkerAudio.getFilmFilterList().removeStoredFilter(sf)) {
            cboFilterProfiles.getSelectionModel().clearSelection();
        }
    }

    private void delAllFilter() {
        progData.filterWorkerAudio.getFilmFilterList().removeAllStoredFilter();
        cboFilterProfiles.getSelectionModel().clearSelection();
    }

    private void resetFilter(boolean replace) {
        if (replace) {
            if (!P2Alert.showAlertOkCancel("Zurücksetzen", "Filterprofile zurücksetzen",
                    "Sollen alle Filterprofile gelöscht " +
                            "und durch die Profile vom ersten Programmstart " +
                            "ersetzt werden?")) {
                return;
            }
            progData.filterWorkerAudio.getFilmFilterList().clear();

        } else if (!progData.filterWorkerAudio.getFilmFilterList().isEmpty()) {
            // dann eine Markierung
            progData.filterWorkerAudio.getFilmFilterList().add(new FilmFilter(true, P2SeparatorComboBox.SEPARATOR));
            progData.filterWorkerAudio.getFilmFilterList().add(new FilmFilter(true, P2SeparatorComboBox.SEPARATOR));
        }

        AudioFilterSamples.addStandardFilter();
        cboFilterProfiles.getSelectionModel().selectFirst();
    }

    private void newFilter() {
        final TextInputDialog dialog = new TextInputDialog(progData.filterWorkerAudio.getFilmFilterList().getNextName());
        dialog.setTitle("Filterprofilname");
        dialog.setHeaderText("Den Namen des Filterprofils vorgeben");
        dialog.setContentText("Name:");
        dialog.setResizable(true);
        dialog.initOwner(progData.primaryStage);

        final Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            progData.filterWorkerAudio.getFilmFilterList().addNewStoredFilter(result.get());
            cboFilterProfiles.getSelectionModel().selectLast();
        }
    }

    private void renameFilter() {
        final FilmFilter sf = cboFilterProfiles.getSelectionModel().getSelectedItem();
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
            cboFilterProfiles.getSelectionModel().clearSelection();
            cboFilterProfiles.getSelectionModel().select(sf);
        }
    }

    private void checkCboFilter() {
        FilmFilter sf = progData.filterWorkerAudio.getActFilterSettings();
        FilmFilter sfCbo = cboFilterProfiles.getSelectionModel().getSelectedItem();
        if (sf.isSame(sfCbo)) {
            //if (SelectedFilmFilterFactory.compareFilterWithoutNameOfFilter(sf, sfCbo)) {
            markFilterOk(true);
        } else {
            markFilterOk(false);
        }
    }

    private static class ListViewListCellCallback implements Callback<ListView<FilmFilter>, ListCell<FilmFilter>> {
        @Override
        public ListCell<FilmFilter> call(ListView<FilmFilter> param) {
            return new ListCell<>() {
                @Override
                public void updateItem(FilmFilter item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setGraphic(null);
                        setText(null);
                        setStyle("");
                        return;
                    }

                    if (P2SeparatorComboBox.isSeparator(item.toString())) {
                        setGraphic(ProgIcons.ICON_BUTTON_SEPARATOR_WIDTH.getImageView());
                        setText(null);
                        setStyle("-fx-alignment: center;");
                        setDisable(true);
                    } else {
                        setGraphic(null);
                        setText(item.toString());
                        setStyle("");
                        setDisable(false);
                    }
                }
            };
        }
    }
}
