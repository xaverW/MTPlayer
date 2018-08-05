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

import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoad;
import de.mtplayer.mtp.controller.filmlist.loadFilmlist.ListenerFilmlistLoadEvent;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
import de.p2tools.p2Lib.dialog.PAlert;
import de.p2tools.p2Lib.guiTools.PButton;
import de.p2tools.p2Lib.guiTools.pCheckComboBox.PCheckComboBox;
import de.p2tools.p2Lib.guiTools.pRange.PRangeBox;
import de.p2tools.p2Lib.guiTools.pRange.PTimePeriodBox;
import de.p2tools.p2Lib.guiTools.pToggleSwitch.PToggleSwitch;
import de.p2tools.p2Lib.tools.log.Duration;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FilmFilterController extends FilterController {

    final String pattern = "HH:mm";
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

    private final Slider slDays = new Slider();
    private final Label lblDays = new Label();

    private final PRangeBox slDur = new PRangeBox(0, SelectedFilter.FILTER_DURATION_MAX_MIN);
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
    private final ComboBox<SelectedFilter> cbFilter = new ComboBox<>();
    private final MenuButton mbFilterTools = new MenuButton("");
    private final Button btnLoadFilter = new Button("laden");
    private final Button btnSaveFilter = new Button("speichern");
    private final Button btnNewFilter = new Button("neu anlegen");

    private final String ONLY_HD = "HD";
    private final String ONLY_UT = "UT";
    private final String ONLY_NEW = "Neue";
    private final String ONLY_LIVE = "Livestreams";
    private final String ONLY_AKT_HISTORY = "aktuelle History";
    private final String NOT_ABO = "Abos";
    private final String NOT_HISTORY = "gesehene";
    private final String NOT_DOUBLE = "doppelte";
    private final String NOT_GEO = "Geo geblockt";
    private final String NOT_FUTURE = "Zukunft";

    private final SplitMenuButton menuItem = new SplitMenuButton();


    public FilmFilterController() {
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
        progData.loadFilmlist.addAdListener(new ListenerFilmlistLoad() {
            @Override
            public void start(ListenerFilmlistLoadEvent event) {
                dis(true);
            }

            @Override
            public void finished(ListenerFilmlistLoadEvent event) {
                dis(false);
            }
        });
    }

    private void dis(boolean dis) {
        this.setDisable(dis);
    }

    private void initButton() {
        btnClearFilter.setOnAction(a -> clearFilter());

        btnEditFilter.setGraphic(new Icons().ICON_BUTTON_EDIT_FILTER);
        btnEditFilter.setOnAction(a -> editFilter());
        btnEditFilter.setTooltip(new Tooltip("Filter ein/ausschalten"));

        btnLoadFilter.setOnAction(a -> loadFilter());
        btnLoadFilter.disableProperty().bind(cbFilter.getSelectionModel().selectedItemProperty().isNull());
        btnLoadFilter.setGraphic(new Icons().FX_ICON_FILTER_FILM_LOAD);
        btnLoadFilter.setText("");
        btnLoadFilter.setTooltip(new Tooltip("Filter wieder laden"));

        btnSaveFilter.setOnAction(a -> {
            if (cbFilter.getSelectionModel().getSelectedItem() == null
                    || PAlert.showAlert("Speichern", "Filter speichern", "Soll der Filter überschrieben werden?")) {
                saveFilter();
            }
        });
        btnSaveFilter.setGraphic(new Icons().FX_ICON_FILTER_FILM_SAVE);
        btnSaveFilter.setText("");
        btnSaveFilter.setTooltip(new Tooltip("aktuelle Filtereinstellung als Filter speichern"));

        btnNewFilter.setOnAction(a -> newFilter());
        btnNewFilter.setGraphic(new Icons().FX_ICON_FILTER_FILM_NEW);
        btnNewFilter.setText("");
        btnNewFilter.setTooltip(new Tooltip("aktuelle Filtereinstellung als neuen Filter anlegen"));


        tglBlacklist.setTooltip(new Tooltip("Blacklist einschalten"));
        tglBlacklist.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().blacklistOnProperty());
    }

    private void filterProfiles() {
        // Filterprofile einrichten
        cbFilter.setItems(progData.storedFilter.getStordeFilterList());
        cbFilter.getSelectionModel().selectFirst();

        final StringConverter<SelectedFilter> converter = new StringConverter<SelectedFilter>() {
            @Override
            public String toString(SelectedFilter selFilter) {
                return selFilter == null ? "" : selFilter.getName();
            }

            @Override
            public SelectedFilter fromString(String id) {
                final int i = cbFilter.getSelectionModel().getSelectedIndex();
                return progData.storedFilter.getStordeFilterList().get(i);
            }
        };
        cbFilter.setConverter(converter);

        final MenuItem load = new MenuItem("anwenden");
        load.setOnAction(e -> loadFilter());
        load.disableProperty().bind(cbFilter.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem save = new MenuItem("speichern");
        save.setOnAction(e -> saveFilter());
        save.disableProperty().bind(cbFilter.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem neu = new MenuItem("neu");
        neu.setOnAction(e -> newFilter());

        final MenuItem rename = new MenuItem("umbenennen");
        rename.setOnAction(e -> renameFilter());
        rename.disableProperty().bind(cbFilter.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem del = new MenuItem("löschen");
        del.setOnAction(e -> delFilter());
        del.disableProperty().bind(cbFilter.getSelectionModel().selectedItemProperty().isNull());

        final MenuItem delAll = new MenuItem("alle löschen");
        delAll.setOnAction(e -> delAllFilter());
        delAll.disableProperty().bind(Bindings.size(cbFilter.getItems()).isEqualTo(0));

        mbFilterTools.setGraphic(new Icons().ICON_BUTTON_MENU);
        mbFilterTools.getItems().addAll(load, save, neu, rename, del, delAll);
        mbFilterTools.setTooltip(new Tooltip("gespeicherte Filter bearbeiten"));

        cbFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadFilter();
            }
        });

    }

    private void initStringFilter() {
        cboChannel.editableProperty().bind(progData.storedFilter.getSelectedFilter().channelExactProperty().not());
        cboChannel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cboChannel.setVisibleRowCount(25);
        cboChannel.valueProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().channelProperty());
        cboChannel.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                // wenn Änderung beim Sender -> Themen anpassen
                if (newValue.isEmpty()) {
                    progData.nameLists.getTheme("");
                } else {
                    progData.nameLists.getTheme(newValue);
                }
            }
        });
        cboChannel.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                // wenn Änderung beim Sender -> Themen anpassen
                if (newValue.isEmpty()) {
                    progData.nameLists.getTheme("");
                } else {
                    progData.nameLists.getTheme(newValue);
                }
                progData.storedFilter.getSelectedFilter().setChannel(newValue);
            }
        });


        cbxTheme.editableProperty().bind(progData.storedFilter.getSelectedFilter().themeExactProperty().not());
        cbxTheme.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cbxTheme.setVisibleRowCount(25);
        cbxTheme.valueProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().themeProperty());
        cbxTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                progData.storedFilter.getSelectedFilter().setTheme(newValue);
            }
        });


        cboChannel.setItems(progData.nameLists.getObsAllChannel());
        cbxTheme.setItems(progData.nameLists.getObsThemeForSelChannel());

        txtThemeTitle.textProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().themeTitleProperty());
        txtTitle.textProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().titleProperty());
        txtSomewhere.textProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().somewhereProperty());
        txtUrl.textProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().urlProperty());


        VBox vBox = new VBox();
        vBox.setSpacing(10);
        addTxt("Sender", cboChannel, vBox, progData.storedFilter.getSelectedFilter().channelVisProperty());
        addTxt("Thema", cbxTheme, vBox, progData.storedFilter.getSelectedFilter().themeVisProperty());
        addTxt("Thema oder Titel", txtThemeTitle, vBox, progData.storedFilter.getSelectedFilter().themeTitleVisProperty());
        addTxt("Titel", txtTitle, vBox, progData.storedFilter.getSelectedFilter().titleVisProperty());
        addTxt("Irgendwo", txtSomewhere, vBox, progData.storedFilter.getSelectedFilter().somewhereVisProperty());
        addTxt("URL", txtUrl, vBox, progData.storedFilter.getSelectedFilter().urlVisProperty());

        Separator sp = new Separator();
        sp.setMinHeight(20);
        vBox.getChildren().add(sp);

        vBox.visibleProperty().bind(progData.storedFilter.getSelectedFilter().channelVisProperty()
                .or(progData.storedFilter.getSelectedFilter().themeVisProperty()
                        .or(progData.storedFilter.getSelectedFilter().themeTitleVisProperty()
                                .or(progData.storedFilter.getSelectedFilter().titleVisProperty()
                                        .or(progData.storedFilter.getSelectedFilter().somewhereVisProperty()
                                                .or(progData.storedFilter.getSelectedFilter().urlVisProperty())
                                        )
                                )
                        )
                ));
        vBox.managedProperty().bind(vBox.visibleProperty());

        vbFilter.getChildren().add(vBox);
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
        slDays.setValue(progData.storedFilter.getSelectedFilter().getDays());

        progData.storedFilter.getSelectedFilter().daysProperty().addListener(
                l -> slDays.setValue(progData.storedFilter.getSelectedFilter().getDays()));

        slDays.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        progData.storedFilter.getSelectedFilter().setDays((int) slDays.getValue());
                    }
                }
        );
    }

    private void initDurFilter() {
        slDur.minValueProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().minDurProperty());
        slDur.maxValueProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().maxDurProperty());
        slDur.setVluePrefix("");

        progData.storedFilter.getSelectedFilter().minDurProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("getSelectedFilter().getMinDur " + progData.storedFilter.getSelectedFilter().getMinDur()));
        progData.storedFilter.getSelectedFilter().maxDurProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("getSelectedFilter().getMaxDur " + progData.storedFilter.getSelectedFilter().getMaxDur()));
    }

    private void initFilmTimeFilter() {
        slFilmTime.minValueProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().minTimeProperty());
        slFilmTime.maxValueProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().maxTimeProperty());
        slFilmTime.setVluePrefix("");

        progData.storedFilter.getSelectedFilter().minTimeProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("getSelectedFilter().getMinTime " + progData.storedFilter.getSelectedFilter().getMinTime()));
        progData.storedFilter.getSelectedFilter().maxTimeProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("getSelectedFilter().getMaxTime " + progData.storedFilter.getSelectedFilter().getMaxTime()));

        tglFilmTime.selectedProperty().bindBidirectional(progData.storedFilter.getSelectedFilter().minMaxTimeInvertProperty());
        GridPane.setFillWidth(tglFilmTime, false);
    }

    private void setSlider() {
        // Tage
        VBox vBox = new VBox(5);
        vBox.getChildren().addAll(lblDays, slDays);
        vBox.visibleProperty().bind(progData.storedFilter.getSelectedFilter().daysVisProperty());
        vBox.managedProperty().bind(progData.storedFilter.getSelectedFilter().daysVisProperty());
        vbFilter.getChildren().addAll(vBox);

        // MinMax Dauer
        vBox = new VBox(5);
        vBox.getChildren().addAll(lblDur, slDur);
        vBox.visibleProperty().bind(progData.storedFilter.getSelectedFilter().minMaxDurVisProperty());
        vBox.managedProperty().bind(progData.storedFilter.getSelectedFilter().minMaxDurVisProperty());
        vbFilter.getChildren().addAll(vBox);

        // MinMax Uhrzeit
        vBox = new VBox(5);
        vBox.getChildren().addAll(lblFilmTime, slFilmTime, tglFilmTime);
        vBox.visibleProperty().bind(progData.storedFilter.getSelectedFilter().minMaxTimeVisProperty());
        vBox.managedProperty().bind(progData.storedFilter.getSelectedFilter().minMaxTimeVisProperty());
        vbFilter.getChildren().addAll(vBox);
    }

    private void initCheckFilter() {
        checkOnly.addItem(ONLY_HD, progData.storedFilter.getSelectedFilter().onlyHdProperty());
        checkOnly.addItem(ONLY_UT, progData.storedFilter.getSelectedFilter().onlyUtProperty());
        checkOnly.addItem(ONLY_NEW, progData.storedFilter.getSelectedFilter().onlyNewProperty());
        checkOnly.addItem(ONLY_LIVE, progData.storedFilter.getSelectedFilter().onlyLiveProperty());
        checkOnly.addItem(ONLY_AKT_HISTORY, progData.storedFilter.getSelectedFilter().onlyAktHistoryProperty());

        checkNot.addItem(NOT_ABO, progData.storedFilter.getSelectedFilter().notAboProperty());
        checkNot.addItem(NOT_HISTORY, progData.storedFilter.getSelectedFilter().notHistoryProperty());
        checkNot.addItem(NOT_DOUBLE, progData.storedFilter.getSelectedFilter().notDoubleProperty());
        checkNot.addItem(NOT_GEO, progData.storedFilter.getSelectedFilter().notGeoProperty());
        checkNot.addItem(NOT_FUTURE, progData.storedFilter.getSelectedFilter().notFutureProperty());

        VBox vBox = new VBox();
        vBox.getChildren().addAll(lblOnly, checkOnly);
        vBox.visibleProperty().bind(progData.storedFilter.getSelectedFilter().onlyVisProperty());
        vBox.managedProperty().bind(progData.storedFilter.getSelectedFilter().onlyVisProperty());
        vbFilter.getChildren().add(vBox);

        vBox = new VBox();
        vBox.getChildren().addAll(lblNot, checkNot);
        vBox.visibleProperty().bind(progData.storedFilter.getSelectedFilter().notVisProperty());
        vBox.managedProperty().bind(progData.storedFilter.getSelectedFilter().notVisProperty());
        vbFilter.getChildren().add(vBox);
    }

    private void initRest() {
        tglBlacklist.setMaxWidth(Double.MAX_VALUE);
        vbFilter.getChildren().add(tglBlacklist);

        HBox h = new HBox();
        HBox.setHgrow(h, Priority.ALWAYS);
        h.setAlignment(Pos.BOTTOM_RIGHT);
        h.getChildren().add(btnClearFilter);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(btnEditFilter, h);
        vbFilter.getChildren().add(hBox);


        VBox vb = new VBox();
        vb.setSpacing(10);
        vb.setAlignment(Pos.BOTTOM_LEFT);
        VBox.setVgrow(vb, Priority.ALWAYS);

        hBox = new HBox();
        hBox.setSpacing(10);
        btnLoadFilter.setMaxWidth(Double.MAX_VALUE);
        btnSaveFilter.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(btnLoadFilter, Priority.ALWAYS);
        HBox.setHgrow(btnSaveFilter, Priority.ALWAYS);
        hBox.getChildren().addAll(btnLoadFilter, btnNewFilter, btnSaveFilter);
        vb.getChildren().add(hBox);

        hBox = new HBox();
        HBox.setHgrow(cbFilter, Priority.ALWAYS);
        cbFilter.setMaxWidth(Double.MAX_VALUE);
        hBox.getChildren().add(cbFilter);
        vb.getChildren().add(hBox);

        final Button btnHelp = new PButton().helpButton("Filter", HelpText.GUI_FILM_FILTER);

        h = new HBox();
        h.setSpacing(10);
        h.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(h, Priority.ALWAYS);
        h.getChildren().addAll(mbFilterTools, btnHelp);
        vb.getChildren().add(h);
        vbFilter.getChildren().add(vb);
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
        progData.storedFilter.loadStoredFilter(cbFilter.getSelectionModel().getSelectedItem());
    }

    private void saveFilter() {
        if (cbFilter.getSelectionModel().getSelectedItem() == null) {
            newFilter();
        } else {
            progData.storedFilter.saveStoredFilter(cbFilter.getSelectionModel().getSelectedItem());
        }
    }

    private void delFilter() {
        progData.storedFilter.removeStoredFilter(cbFilter.getSelectionModel().getSelectedItem());
        cbFilter.getSelectionModel().clearSelection();
    }

    private void delAllFilter() {
        if (PAlert.showAlert("Löschen", "Filter löschen", "Sollen alle Filter gelöscht werden?")) {
            progData.storedFilter.removeAllStoredFilter();
        }
    }

    private void newFilter() {
        final TextInputDialog dialog = new TextInputDialog(progData.storedFilter.getNextName());
        dialog.setTitle("Filtername");
        dialog.setHeaderText("Den Namen des Filters vorgeben");
        dialog.setContentText("Name:");

        final Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            progData.storedFilter.addNewStoredFilter(result.get());
            cbFilter.getSelectionModel().selectLast();
        }
    }

    private void renameFilter() {
        final SelectedFilter sf = cbFilter.getSelectionModel().getSelectedItem();
        if (sf == null) {
            return;
        }
        final TextInputDialog dialog = new TextInputDialog(sf.getName());
        dialog.setTitle("Filter umbenennen");
        dialog.setHeaderText("Den Namen des Filters ändern");
        dialog.setContentText("Neuer Name:");

        final Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            sf.setName(result.get());
            cbFilter.getSelectionModel().select(sf);
        }
    }

    private void clearFilter() {
        Duration.staticPing("Filter löschen");
        if (progData.storedFilter.txtFilterIsEmpty()) {
            progData.storedFilter.clearFilter();
        } else {
            progData.storedFilter.clearTxtFilter();
        }
    }

    private void editFilter() {
        final FilmFilterEditDialog editFilterDialog = new FilmFilterEditDialog(progData);
        editFilterDialog.init();
    }

}
