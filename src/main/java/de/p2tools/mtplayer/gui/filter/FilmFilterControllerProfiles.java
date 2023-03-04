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

package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgColorList;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.ProgIcons;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilter;
import de.p2tools.mtplayer.controller.filmfilter.FilmFilterFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PGuiTools;
import de.p2tools.p2lib.guitools.PSeparatorComboBox;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.Optional;

public class FilmFilterControllerProfiles extends VBox {

    private final ComboBox<FilmFilter> cboFilterProfiles = new PSeparatorComboBox<>();
    private final MenuButton mbFilterTools = new MenuButton("");
    private final Button btnLoadFilter = new Button("laden");
    private final Button btnSaveFilter = new Button("speichern");
    private final Button btnNewFilter = new Button("neu anlegen");

    private final ProgData progData;

    public FilmFilterControllerProfiles() {
        super();
        progData = ProgData.getInstance();

        setPadding(new Insets(10, 15, 5, 15));
        setSpacing(FilterController.FILTER_SPACING_PROFIlE);

        initButton();
        filterProfiles();

        initRest();

        progData.actFilmFilterWorker.filterChangeProperty().addListener((observable, oldValue, newValue) -> checkCboFilter());
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
        btnLoadFilter.setGraphic(ProgIcons.Icons.FX_ICON_FILTER_FILM_LOAD.getImageView());
        btnLoadFilter.setText("");
        btnLoadFilter.setTooltip(new Tooltip("Filterprofil wieder laden"));

        btnSaveFilter.setOnAction(a -> {
            if (cboFilterProfiles.getSelectionModel().getSelectedItem() == null
                    || PAlert.showAlertOkCancel("Speichern", "Filterprofil speichern",
                    "Soll das Filterprofil überschrieben werden?")) {
                saveFilter();
            }
        });
        btnSaveFilter.setGraphic(ProgIcons.Icons.FX_ICON_FILTER_FILM_SAVE.getImageView());
        btnSaveFilter.setText("");
        btnSaveFilter.setTooltip(new Tooltip("Aktuelle Filtereinstellung als Filterprofil speichern"));

        btnNewFilter.setOnAction(a -> newFilter());
        btnNewFilter.setGraphic(ProgIcons.Icons.FX_ICON_FILTER_FILM_NEW.getImageView());
        btnNewFilter.setText("");
        btnNewFilter.setTooltip(new Tooltip("Aktuelle Filtereinstellung als neues Filterprofil anlegen"));
    }

    private void filterProfiles() {
        // Filterprofile einrichten
        cboFilterProfiles.setItems(progData.actFilmFilterWorker.getStoredFilterList());
        cboFilterProfiles.setTooltip(new Tooltip("Gespeicherte Filterprofile können\n" +
                "hier geladen werden"));

        final StringConverter<FilmFilter> converter = new StringConverter<FilmFilter>() {
            @Override
            public String toString(FilmFilter selFilter) {
                return selFilter == null ? "" : selFilter.getName();
            }

            @Override
            public FilmFilter fromString(String id) {
                final int i = cboFilterProfiles.getSelectionModel().getSelectedIndex();
                return progData.actFilmFilterWorker.getStoredFilterList().get(i);
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
            FilmFilter filmFilter = progData.actFilmFilterWorker.getActFilterSettings();
            progData.aboList.addNewAboFromFilterButton(filmFilter);
        });

        final MenuItem miResort = new MenuItem("Filterprofile sortieren");
        miResort.setOnAction(e -> new FilmFilterSortDialog(progData).showDialog());

        final MenuItem miFilterDialog = new MenuItem("Filterprofile in eigenem Fenster anzeigen");
        miFilterDialog.setOnAction(e -> new FilmFilterDialog(progData).showDialog());

        final MenuItem miReset = new MenuItem("alle Filterprofile wieder herstellen");
        miReset.setOnAction(e -> resetFilter());

        mbFilterTools.setGraphic(ProgIcons.Icons.ICON_BUTTON_MENU.getImageView());
        mbFilterTools.getItems().addAll(miLoad, miRename, miDel, miDelAll, miSave, miNew, miAbo,
                new SeparatorMenuItem(), miResort, miFilterDialog,
                new SeparatorMenuItem(), miReset);
        mbFilterTools.setTooltip(new Tooltip("Gespeicherte Filterprofile bearbeiten"));

        cboFilterProfiles.getSelectionModel().select(ProgConfig.FILTER_FILM_SEL_FILTER.get());
        ProgConfig.FILTER_FILM_SEL_FILTER.bind(cboFilterProfiles.getSelectionModel().selectedIndexProperty());

        cboFilterProfiles.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadFilter();
            }
        });
        ProgColorList.FILTER_PROFILE_SEPARATOR.colorProperty().addListener((a, b, c) -> cboFilterProfiles.setCellFactory(new Callback<>() {
            @Override
            public ListCell<FilmFilter> call(ListView<FilmFilter> param) {
                final ListCell<FilmFilter> cell = new ListCell<>() {
                    @Override
                    public void updateItem(FilmFilter item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setText(item.toString());
                            if (PSeparatorComboBox.isSeparator(item.toString())) {
                                this.setDisable(true);
                                setStyle(ProgColorList.FILTER_PROFILE_SEPARATOR.getCssBackgroundAndSel());
                            } else {
                                this.setDisable(false);
                                setStyle("");
                            }
                        }
                    }
                };
                return cell;
            }
        }));

        cboFilterProfiles.setCellFactory(new Callback<>() {
            @Override
            public ListCell<FilmFilter> call(ListView<FilmFilter> param) {
                final ListCell<FilmFilter> cell = new ListCell<>() {
                    @Override
                    public void updateItem(FilmFilter item, boolean empty) {
                        super.updateItem(item, empty);
                        if (!empty) {
                            setText(item.toString());
                            if (PSeparatorComboBox.isSeparator(item.toString())) {
                                this.setDisable(true);
                                setStyle(ProgColorList.FILTER_PROFILE_SEPARATOR.getCssBackgroundAndSel());
                            } else {
                                this.setDisable(false);
                                setStyle("");
                            }
                        }
                    }
                };
                return cell;
            }
        });

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

        final Button btnHelp = PButton.helpButton("Filter", HelpText.GUI_FILM_FILTER);
        hBox = new HBox(10);
        hBox.getChildren().addAll(mbFilterTools, PGuiTools.getHBoxGrower(), btnHelp);
        getChildren().add(hBox);
    }

    private void loadFilter() {
        progData.actFilmFilterWorker.setActFilterSettings(cboFilterProfiles.getSelectionModel().getSelectedItem());
    }

    private void saveFilter() {
        final FilmFilter sf = cboFilterProfiles.getSelectionModel().getSelectedItem();
        if (sf == null) {
            newFilter();
        } else {
            progData.actFilmFilterWorker.saveStoredFilter(sf);
            checkCboFilter();
        }
    }

    private void delFilter() {
        FilmFilter sf = cboFilterProfiles.getSelectionModel().getSelectedItem();
        if (sf == null) {
            PAlert.showInfoNoSelection();
            return;
        }

        if (progData.actFilmFilterWorker.removeStoredFilter(sf)) {
            cboFilterProfiles.getSelectionModel().selectFirst();
        }
    }

    private void delAllFilter() {
        progData.actFilmFilterWorker.removeAllStoredFilter();
    }

    private void resetFilter() {
        if (PAlert.showAlertOkCancel("Zurücksetzen", "Filterprofile zurücksetzen",
                "Sollen alle Filterprofile gelöscht " +
                        "und durch die Profile vom ersten Programmstart " +
                        "ersetzt werden?")) {
            progData.actFilmFilterWorker.getStoredFilterList().clear();
            FilmFilterFactory.addStandardFilter();
            cboFilterProfiles.getSelectionModel().selectFirst();
        }
    }

    private void newFilter() {
        final TextInputDialog dialog = new TextInputDialog(progData.actFilmFilterWorker.getNextName());
        dialog.setTitle("Filterprofilname");
        dialog.setHeaderText("Den Namen des Filterprofils vorgeben");
        dialog.setContentText("Name:");
        dialog.setResizable(true);
        dialog.initOwner(progData.primaryStage);

        final Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            progData.actFilmFilterWorker.addNewStoredFilter(result.get());
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
            cboFilterProfiles.getSelectionModel().select(sf);
        }
    }

    private void checkCboFilter() {
        FilmFilter sf = progData.actFilmFilterWorker.getActFilterSettings();
        FilmFilter sfCbo = cboFilterProfiles.getSelectionModel().getSelectedItem();
        if (sf.isSame(sfCbo, false)) {
            //if (SelectedFilmFilterFactory.compareFilterWithoutNameOfFilter(sf, sfCbo)) {
            markFilterOk(true);
        } else {
            markFilterOk(false);
        }
    }
}
