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

package de.mtplayer.mtp.gui;

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.tools.storedFilter.ProgInitFilter;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilterFactory;
import de.p2tools.p2Lib.alert.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import de.p2tools.p2Lib.guiTools.pCheckComboBox.PCheckComboBox;
import de.p2tools.p2Lib.guiTools.pRange.PRangeBox;
import de.p2tools.p2Lib.guiTools.pRange.PTimePeriodBox;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import de.p2tools.p2Lib.tools.duration.PDuration;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.Optional;

public class FilmFilterController extends FilterController {

    private final Slider slDays = new Slider();
    private final Label lblDays = new Label();

    private final PRangeBox slDur = new PRangeBox(0, SelectedFilter.FILTER_DURATION_MAX_MINUTE);
    private final Label lblDur = new Label("Filmlänge:");

    private final PTimePeriodBox slFilmTime = new PTimePeriodBox();
    private final PToggleSwitch tglFilmTime = new PToggleSwitch("Zeitraum \"ausschließen\"");
    private final Label lblFilmTime = new Label("Sendezeit:");

    PCheckComboBox checkOnly = new PCheckComboBox();
    PCheckComboBox checkNot = new PCheckComboBox();
    Label lblOnly = new Label("nur anzeigen");
    Label lblNot = new Label("nicht anzeigen");

    private final ComboBox<String> cboChannel = new ComboBox<>();
    private final ComboBox<String> cbxTheme = new ComboBox<>();
    private final TextField txtThemeTitle = new TextField();
    private final TextField txtTitle = new TextField();
    private final TextField txtSomewhere = new TextField();
    private final TextField txtUrl = new TextField();

    private final PToggleSwitch tglBlacklist = new PToggleSwitch("Blacklist einschalten:");
    private final Button btnClearFilter = new Button("Filter löschen");
    private final Button btnEditFilter = new Button("");
    private final ComboBox<SelectedFilter> cboFilterProfiles = new ComboBox<>();
    private final MenuButton mbFilterTools = new MenuButton("");
    private final Button btnLoadFilter = new Button("laden");
    private final Button btnSaveFilter = new Button("speichern");
    private final Button btnNewFilter = new Button("neu anlegen");

    private final String ONLY_BOOKMARK = "Bookmarks";
    private final String ONLY_HD = "HD";
    private final String ONLY_UT = "UT";
    private final String ONLY_NEW = "neue";
    private final String ONLY_LIVE = "Livestreams";
    private final String ONLY_AKT_HISTORY = "aktuelle History";

    private final String NOT_ABO = "Abos";
    private final String NOT_HISTORY = "gesehene";
    private final String NOT_DOUBLE = "doppelte";
    private final String NOT_GEO = "Geo geblockt";
    private final String NOT_FUTURE = "Zukunft";

    private final VBox vBoxFilter;
    private final ProgData progData;

    private final IntegerProperty filterProp = ProgConfig.FILTER_FILME_SEL_FILTER.getIntegerProperty();

    public FilmFilterController() {
        super();
        vBoxFilter = getVBoxFilter(false);
        progData = ProgData.getInstance();

        initButton();
        filterProfiles();

        // Sender, Thema, ..
        initStringFilter();

        // Slider
        initDaysFilter();
        initDurFilter();
        initFilmTimeFilter();
        setSlider();
        setLabelSlider();

        // CheckOnOff
        initCheckFilter();

        initRest();

        progData.storedFilters.filterChangeProperty().addListener((observable, oldValue, newValue) -> checkCboFilter());
        checkCboFilter();
    }

    public void markFilterOk(boolean ok) {
        if (ok) {
            cboFilterProfiles.getStyleClass().removeAll("markFilterOk");
            cboFilterProfiles.getStyleClass().add("markFilterOk");
        } else {
            cboFilterProfiles.getStyleClass().removeAll("markFilterOk");
        }
    }

    private void initButton() {
        btnClearFilter.setOnAction(a -> clearFilter());

        btnEditFilter.setGraphic(new ProgIcons().ICON_BUTTON_EDIT_FILTER);
        btnEditFilter.setOnAction(a -> editFilter());
        btnEditFilter.setTooltip(new Tooltip("Filter ein/ausschalten"));

        btnLoadFilter.setOnAction(a -> loadFilter());
        btnLoadFilter.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());
        btnLoadFilter.setGraphic(new ProgIcons().FX_ICON_FILTER_FILM_LOAD);
        btnLoadFilter.setText("");
        btnLoadFilter.setTooltip(new Tooltip("Filter wieder laden"));

        btnSaveFilter.setOnAction(a -> {
            if (cboFilterProfiles.getSelectionModel().getSelectedItem() == null
                    || PAlert.showAlertOkCancel("Speichern", "Filter speichern", "Soll der Filter überschrieben werden?")) {
                saveFilter();
            }
        });
        btnSaveFilter.setGraphic(new ProgIcons().FX_ICON_FILTER_FILM_SAVE);
        btnSaveFilter.setText("");
        btnSaveFilter.setTooltip(new Tooltip("aktuelle Filtereinstellung als Filter speichern"));

        btnNewFilter.setOnAction(a -> newFilter());
        btnNewFilter.setGraphic(new ProgIcons().FX_ICON_FILTER_FILM_NEW);
        btnNewFilter.setText("");
        btnNewFilter.setTooltip(new Tooltip("aktuelle Filtereinstellung als neuen Filter anlegen"));


        tglBlacklist.setTooltip(new Tooltip("Blacklist einschalten"));
        tglBlacklist.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().blacklistOnProperty());
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

        final MenuItem miLoad = new MenuItem("aktuellen Filter wieder laden");
        miLoad.setOnAction(e -> loadFilter());
        miLoad.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miRename = new MenuItem("aktuellen Filter umbenennen");
        miRename.setOnAction(e -> renameFilter());
        miRename.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miDel = new MenuItem("aktuellen Filter löschen");
        miDel.setOnAction(e -> delFilter());
        miDel.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miDelAll = new MenuItem("alle Filter löschen");
        miDelAll.setOnAction(e -> delAllFilter());
        miDelAll.disableProperty().bind(Bindings.size(cboFilterProfiles.getItems()).isEqualTo(0));

        final MenuItem miSave = new MenuItem("Filtereinstellungen in aktuellem Filter speichern");
        miSave.setOnAction(e -> saveFilter());
        miSave.disableProperty().bind(cboFilterProfiles.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem miNew = new MenuItem("Filtereinstellungen in neuem Filter speichern");
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
        mbFilterTools.setTooltip(new Tooltip("gespeicherte Filter bearbeiten"));

        cboFilterProfiles.getSelectionModel().select(filterProp.get());
        filterProp.bind(cboFilterProfiles.getSelectionModel().selectedIndexProperty());

        cboFilterProfiles.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadFilter();
            }
        });

    }

    private void initStringFilter() {
        cboChannel.editableProperty().bind(progData.storedFilters.getActFilterSettings().channelExactProperty().not());
        cboChannel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cboChannel.setVisibleRowCount(25);
        cboChannel.valueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().channelProperty());

        cboChannel.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                // wenn Änderung beim Sender -> Themen anpassen
                if (newValue.isEmpty()) {
                    progData.worker.getTheme("");
                } else {
                    progData.worker.getTheme(newValue);
                }
            }
        });
        cboChannel.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                // wenn Änderung beim Sender -> Themen anpassen
                if (newValue.isEmpty()) {
                    progData.worker.getTheme("");
                } else {
                    progData.worker.getTheme(newValue);
                }
                progData.storedFilters.getActFilterSettings().setChannel(newValue);
            }
        });


        cbxTheme.editableProperty().bind(progData.storedFilters.getActFilterSettings().themeExactProperty().not());
        cbxTheme.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cbxTheme.setVisibleRowCount(25);
        cbxTheme.valueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeProperty());
        cbxTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                progData.storedFilters.getActFilterSettings().setTheme(newValue);
            }
        });

        cboChannel.setItems(progData.worker.getAllChannelList());
        cbxTheme.setItems(progData.worker.getThemeForChannelList());

        txtThemeTitle.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().themeTitleProperty());
        txtTitle.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().titleProperty());
        txtSomewhere.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().somewhereProperty());
        txtUrl.textProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().urlProperty());


        VBox vBox = new VBox(10);
        addTxt("Sender", cboChannel, vBox, progData.storedFilters.getActFilterSettings().channelVisProperty());
        addTxt("Thema", cbxTheme, vBox, progData.storedFilters.getActFilterSettings().themeVisProperty());
        addTxt("Thema oder Titel", txtThemeTitle, vBox, progData.storedFilters.getActFilterSettings().themeTitleVisProperty());
        addTxt("Titel", txtTitle, vBox, progData.storedFilters.getActFilterSettings().titleVisProperty());
        addTxt("Irgendwo", txtSomewhere, vBox, progData.storedFilters.getActFilterSettings().somewhereVisProperty());
        addTxt("URL", txtUrl, vBox, progData.storedFilters.getActFilterSettings().urlVisProperty());

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator1");
        sp.setMinHeight(10);
        vBox.getChildren().add(sp);

        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().channelVisProperty()
                .or(progData.storedFilters.getActFilterSettings().themeVisProperty()
                        .or(progData.storedFilters.getActFilterSettings().themeTitleVisProperty()
                                .or(progData.storedFilters.getActFilterSettings().titleVisProperty()
                                        .or(progData.storedFilters.getActFilterSettings().somewhereVisProperty()
                                                .or(progData.storedFilters.getActFilterSettings().urlVisProperty())
                                        )
                                )
                        )
                ));
        vBox.managedProperty().bind(vBox.visibleProperty());

        sp.visibleProperty().bind(vBox.visibleProperty());
        sp.managedProperty().bind(vBox.visibleProperty());


        vBoxFilter.getChildren().add(vBox);
    }

    private void addTxt(String txt, Control control, VBox vBoxComplete, BooleanProperty booleanProperty) {
        VBox vBox = new VBox();
        Label label = new Label(txt);
        vBox.getChildren().addAll(label, control);
        vBoxComplete.getChildren().add(vBox);

        vBox.visibleProperty().bind(booleanProperty);
        vBox.managedProperty().bind(booleanProperty);
    }

    private void initDaysFilter() {
        slDays.setMin(1);
        slDays.setMax(SelectedFilter.FILTER_DAYS_MAX);
        slDays.setShowTickLabels(true);
        slDays.setMajorTickUnit(10);
        slDays.setBlockIncrement(1);

        slDays.valueProperty().addListener(l -> {
            setLabelSlider();
        });

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slDays.setValue(progData.storedFilters.getActFilterSettings().getDays());

        progData.storedFilters.getActFilterSettings().daysProperty().addListener(
                l -> slDays.setValue(progData.storedFilters.getActFilterSettings().getDays()));

        slDays.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        progData.storedFilters.getActFilterSettings().setDays((int) slDays.getValue());
                    }
                }
        );
    }

    private void initDurFilter() {
        slDur.minValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minDurProperty());
        slDur.maxValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().maxDurProperty());
        slDur.setValuePrefix("");

//        progData.storedFilter.getSelectedFilter().minDurProperty().addListener((observable, oldValue, newValue) ->
//                System.out.println("getSelectedFilter().getMinDur " + progData.storedFilter.getSelectedFilter().getMinDur()));
//        progData.storedFilter.getSelectedFilter().maxDurProperty().addListener((observable, oldValue, newValue) ->
//                System.out.println("getSelectedFilter().getMaxDur " + progData.storedFilter.getSelectedFilter().getMaxDur()));
    }

    private void initFilmTimeFilter() {
        slFilmTime.minValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minTimeProperty());
        slFilmTime.maxValueProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().maxTimeProperty());
        slFilmTime.setVluePrefix("");

//        progData.storedFilter.getSelectedFilter().minTimeProperty().addListener((observable, oldValue, newValue) ->
//                System.out.println("getSelectedFilter().getMinTime " + progData.storedFilter.getSelectedFilter().getMinTime()));
//        progData.storedFilter.getSelectedFilter().maxTimeProperty().addListener((observable, oldValue, newValue) ->
//                System.out.println("getSelectedFilter().getMaxTime " + progData.storedFilter.getSelectedFilter().getMaxTime()));

        tglFilmTime.selectedProperty().bindBidirectional(progData.storedFilters.getActFilterSettings().minMaxTimeInvertProperty());
        GridPane.setFillWidth(tglFilmTime, false);
    }

    private void setSlider() {
        // Tage
        VBox vBox = new VBox(5);
        vBox.getChildren().addAll(lblDays, slDays);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().daysVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().daysVisProperty());
        vBoxFilter.getChildren().addAll(vBox);

        // MinMax Dauer
        vBox = new VBox(5);
        vBox.getChildren().addAll(lblDur, slDur);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().minMaxDurVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().minMaxDurVisProperty());
        vBoxFilter.getChildren().addAll(vBox);

        // MinMax Uhrzeit
        vBox = new VBox(5);
        vBox.getChildren().addAll(lblFilmTime, slFilmTime, tglFilmTime);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().minMaxTimeVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().minMaxTimeVisProperty());
        vBoxFilter.getChildren().addAll(vBox);
    }

    private void initCheckFilter() {
        checkOnly.addItem(ONLY_BOOKMARK, "nur Filme der Bookmarks anzeigen", progData.storedFilters.getActFilterSettings().onlyBookmarkProperty());
        checkOnly.addItem(ONLY_HD, "nur HD-Filme anzeigen", progData.storedFilters.getActFilterSettings().onlyHdProperty());
        checkOnly.addItem(ONLY_UT, "nur Filme mit Untertitel anzeigen", progData.storedFilters.getActFilterSettings().onlyUtProperty());
        checkOnly.addItem(ONLY_NEW, "nur neue Filme anzeigen", progData.storedFilters.getActFilterSettings().onlyNewProperty());
        checkOnly.addItem(ONLY_LIVE, "nur Livestreams anzeigen", progData.storedFilters.getActFilterSettings().onlyLiveProperty());
        checkOnly.addItem(ONLY_AKT_HISTORY, "nur die aktuelle History anzeigen", progData.storedFilters.getActFilterSettings().onlyAktHistoryProperty());

        checkNot.addItem(NOT_ABO, "keine Filme für die es ein Abo gibt, anzeigen", progData.storedFilters.getActFilterSettings().notAboProperty());
        checkNot.addItem(NOT_HISTORY, "bereits gesehene Filme nicht anzeigen", progData.storedFilters.getActFilterSettings().notHistoryProperty());
        checkNot.addItem(NOT_DOUBLE, "doppelte Filme nur einmal anzeigen", progData.storedFilters.getActFilterSettings().notDoubleProperty());
        checkNot.addItem(NOT_GEO, "geo-geblockte Filme nicht anzeigen", progData.storedFilters.getActFilterSettings().notGeoProperty());
        checkNot.addItem(NOT_FUTURE, "keine Filme mit Datum in der Zukunft anzeigen", progData.storedFilters.getActFilterSettings().notFutureProperty());

        VBox vBox = new VBox();
        vBox.getChildren().addAll(lblOnly, checkOnly);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().onlyVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().onlyVisProperty());
        vBoxFilter.getChildren().add(vBox);

        vBox = new VBox();
        vBox.getChildren().addAll(lblNot, checkNot);
        vBox.visibleProperty().bind(progData.storedFilters.getActFilterSettings().notVisProperty());
        vBox.managedProperty().bind(progData.storedFilters.getActFilterSettings().notVisProperty());
        vBoxFilter.getChildren().add(vBox);
    }

    private void initRest() {
        addVgrowVboxAll();
        VBox vbController = getVBoxBotton();

        tglBlacklist.setMaxWidth(Double.MAX_VALUE);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(btnEditFilter, PGuiTools.getHBoxGrower(), btnClearFilter);

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator3");
        sp.setMinHeight(10);
        vbController.getChildren().addAll(tglBlacklist, hBox, sp);


        // Filterprofile
        hBox = new HBox(10);
        btnLoadFilter.setMaxWidth(Double.MAX_VALUE);
        btnSaveFilter.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnLoadFilter, Priority.ALWAYS);
        HBox.setHgrow(btnSaveFilter, Priority.ALWAYS);
        hBox.getChildren().addAll(btnLoadFilter, btnNewFilter, btnSaveFilter);
        vbController.getChildren().add(hBox);

        cboFilterProfiles.setMaxWidth(Double.MAX_VALUE);
        VBox vBox = new VBox(1);
        vBox.getChildren().addAll(new Label("gespeicherte Filter:"), cboFilterProfiles);
        vbController.getChildren().add(vBox);

        final Button btnHelp = PButton.helpButton("Filter", HelpText.GUI_FILM_FILTER);
        hBox = new HBox(10);
        hBox.getChildren().addAll(mbFilterTools, PGuiTools.getHBoxGrower(), btnHelp);
        vbController.getChildren().add(hBox);
    }

    private void setLabelSlider() {
        final String txtAll = "alles";

        int i = (int) slDays.getValue();
        String tNr = i + "";

        if (i == SelectedFilter.FILTER_DAYS_MAX) {
            lblDays.setText("Zeitraum: " + txtAll);
        } else {
            lblDays.setText("Zeitraum: " + tNr + (i == 1 ? " Tag" : " Tage"));
        }
    }

    private void loadFilter() {
        progData.storedFilters.setActFilterSettings(cboFilterProfiles.getSelectionModel().getSelectedItem());
    }

    private void saveFilter() {
        if (cboFilterProfiles.getSelectionModel().getSelectedItem() == null) {
            newFilter();
        } else {
            progData.storedFilters.saveStoredFilter(cboFilterProfiles.getSelectionModel().getSelectedItem());
        }
    }

    private void delFilter() {
        progData.storedFilters.removeStoredFilter(cboFilterProfiles.getSelectionModel().getSelectedItem());
        cboFilterProfiles.getSelectionModel().selectFirst();
    }

    private void delAllFilter() {
        if (PAlert.showAlertOkCancel("Löschen", "Filter löschen", "Sollen alle Filter gelöscht werden?")) {
            progData.storedFilters.removeAllStoredFilter();
            cboFilterProfiles.getSelectionModel().selectFirst();
        }
    }

    private void resetFilter() {
        if (PAlert.showAlertOkCancel("Zurücksetzen", "Filter zurücksetzen", "Sollen alle Filter gelöscht werden " +
                "und durch die Filter vom ersten Programmstart " +
                "ersetzt werden?")) {
            progData.storedFilters.getStordeFilterList().clear();
            ProgInitFilter.setProgInitFilter();
            cboFilterProfiles.getSelectionModel().selectFirst();
        }
    }

    private void newFilter() {
        final TextInputDialog dialog = new TextInputDialog(progData.storedFilters.getNextName());
        dialog.setTitle("Filtername");
        dialog.setHeaderText("Den Namen des Filters vorgeben");
        dialog.setContentText("Name:");
        dialog.setResizable(true);

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
        dialog.setTitle("Filter umbenennen");
        dialog.setHeaderText("Den Namen des Filters ändern");
        dialog.setContentText("Neuer Name:");
        dialog.setResizable(true); // sonst geht der Dialog nicht "auf" und lässt sich nicht vergrößern, bug??

        final Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            sf.setName(result.get());
            cboFilterProfiles.getSelectionModel().select(sf);
        }
    }

    private void clearFilter() {
        PDuration.onlyPing("Filter löschen");
        if (progData.storedFilters.txtFilterIsEmpty()) {
            progData.storedFilters.clearFilter();
        } else {
            progData.storedFilters.clearTxtFilter();
        }
    }

    private void editFilter() {
        final FilmFilterEditDialog editFilterDialog = new FilmFilterEditDialog(progData);
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
