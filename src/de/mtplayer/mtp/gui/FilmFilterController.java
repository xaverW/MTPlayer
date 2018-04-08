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
import de.mtplayer.mtp.controller.loadFilmlist.ListenerFilmListLoad;
import de.mtplayer.mtp.controller.loadFilmlist.ListenerFilmListLoadEvent;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.tools.HelpText;
import de.mtplayer.mtp.tools.storedFilter.SelectedFilter;
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
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.RangeSlider;
import org.controlsfx.control.ToggleSwitch;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class FilmFilterController extends FilterController {


    private final Slider slDays = new Slider();
    private final Label lblDays = new Label();

    private final RangeSlider slDur = new RangeSlider();
    private final Label lblDur = new Label();

    private final RangeSlider slFilmTime = new RangeSlider();
    private final ToggleSwitch tglFilmTime = new ToggleSwitch("Zeitraum \"ausschließen\"");
    private final Label lblFilmTime = new Label();

    CheckComboBox<String> checkOnly = new CheckComboBox<>();
    CheckComboBox<String> checkNot = new CheckComboBox<>();
    Label lblOnly = new Label("nur anzeigen");
    Label lblNot = new Label("nicht anzeigen");

    private final ComboBox<String> cbxSender = new ComboBox<>();
    private final ComboBox<String> cbxTheme = new ComboBox<>();
    private final TextField txtThemeTitle = new TextField();
    private final TextField txtTitle = new TextField();
    private final TextField txtSomewhere = new TextField();
    private final TextField txtUrl = new TextField();

    private final ToggleSwitch tglBlacklist = new ToggleSwitch("Blacklist einschalten:");
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


    public FilmFilterController() {
        initButton();
        filterProfiles();

        initStringFiter();

        initDaysFilter();
        initDurFilter();
        initFilmTimeFilter();

        setSlider();

        initCheckFilter();

        initRest();
        daten.loadFilmList.addAdListener(new ListenerFilmListLoad() {
            @Override
            public void start(ListenerFilmListLoadEvent event) {
                dis(true);
            }

            @Override
            public void fertig(ListenerFilmListLoadEvent event) {
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
                    || new MTAlert().showAlert("Speichern", "Filter speichern", "Soll der Filter überschrieben werden?")) {
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
        tglBlacklist.selectedProperty().bindBidirectional(daten.storedFilter.getSelectedFilter().blacklistOnProperty());
    }

    private void filterProfiles() {
        // Filterprofile einrichten
        cbFilter.setItems(daten.storedFilter.getStordeFilterList());
        cbFilter.getSelectionModel().selectFirst();

        final StringConverter<SelectedFilter> converter = new StringConverter<SelectedFilter>() {
            @Override
            public String toString(SelectedFilter selFilter) {
                return selFilter == null ? "" : selFilter.getName();
            }

            @Override
            public SelectedFilter fromString(String id) {
                final int i = cbFilter.getSelectionModel().getSelectedIndex();
                return daten.storedFilter.getStordeFilterList().get(i);
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

        mbFilterTools.setGraphic(new Icons().ICON_BUTTON_MENUE);
        mbFilterTools.getItems().addAll(load, save, neu, rename, del, delAll);
        mbFilterTools.setTooltip(new Tooltip("gespeicherte Filter bearbeiten"));

        cbFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadFilter();
            }
        });

    }

    private void initStringFiter() {
        cbxSender.editableProperty().bind(daten.storedFilter.getSelectedFilter().senderExactProperty().not());
        cbxSender.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cbxSender.setVisibleRowCount(25);
        cbxSender.valueProperty().bindBidirectional(daten.storedFilter.getSelectedFilter().senderProperty());
        cbxSender.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                // wenn Änderung beim Sender -> Themen anpassen
                if (newValue.isEmpty()) {
                    daten.nameLists.getThemen("");
                } else {
                    daten.nameLists.getThemen(newValue);
                }
            }
        });
        cbxSender.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                // wenn Änderung beim Sender -> Themen anpassen
                if (newValue.isEmpty()) {
                    daten.nameLists.getThemen("");
                } else {
                    daten.nameLists.getThemen(newValue);
                }
                daten.storedFilter.getSelectedFilter().setSender(newValue);
            }
        });


        cbxTheme.editableProperty().bind(daten.storedFilter.getSelectedFilter().themeExactProperty().not());
        cbxTheme.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        cbxTheme.setVisibleRowCount(25);
        cbxTheme.valueProperty().bindBidirectional(daten.storedFilter.getSelectedFilter().themeProperty());
        cbxTheme.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null && newValue != null) {
                daten.storedFilter.getSelectedFilter().setTheme(newValue);
            }
        });


        cbxSender.setItems(daten.nameLists.getObsAllSender());
        cbxTheme.setItems(daten.nameLists.getObsThemaForSelSender());

        txtThemeTitle.textProperty().bindBidirectional(daten.storedFilter.getSelectedFilter().themeTitleProperty());
        txtTitle.textProperty().bindBidirectional(daten.storedFilter.getSelectedFilter().titleProperty());
        txtSomewhere.textProperty().bindBidirectional(daten.storedFilter.getSelectedFilter().somewhereProperty());
        txtUrl.textProperty().bindBidirectional(daten.storedFilter.getSelectedFilter().urlProperty());


        VBox vBox = new VBox();
        vBox.setSpacing(10);
        addTxt("Sender", cbxSender, vBox, daten.storedFilter.getSelectedFilter().senderVisProperty());
        addTxt("Thema", cbxTheme, vBox, daten.storedFilter.getSelectedFilter().themeVisProperty());
        addTxt("Thema oder Titel", txtThemeTitle, vBox, daten.storedFilter.getSelectedFilter().themeTitleVisProperty());
        addTxt("Titel", txtTitle, vBox, daten.storedFilter.getSelectedFilter().titleVisProperty());
        addTxt("Irgendwo", txtSomewhere, vBox, daten.storedFilter.getSelectedFilter().somewhereVisProperty());
        addTxt("URL", txtUrl, vBox, daten.storedFilter.getSelectedFilter().urlVisProperty());

        Separator sp = new Separator();
        sp.setMinHeight(20);
        vBox.getChildren().add(sp);

        vbFilter.getChildren().add(vBox);
    }

    private void addTxt(String txt, Control control, VBox vBox, BooleanProperty booleanProperty) {
        VBox v = new VBox();
        Label label = new Label(txt);
        v.getChildren().addAll(label, control);
        vBox.getChildren().add(v);

        v.visibleProperty().bind(booleanProperty);
        v.managedProperty().bind(booleanProperty);

        control.visibleProperty().bind(booleanProperty);
        control.managedProperty().bind(booleanProperty);

        label.visibleProperty().bind(booleanProperty);
        label.managedProperty().bind(booleanProperty);
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
        slDays.setValue(daten.storedFilter.getSelectedFilter().getDays());

        daten.storedFilter.getSelectedFilter().daysProperty().addListener(
                l -> slDays.setValue(daten.storedFilter.getSelectedFilter().getDays()));

        slDays.valueChangingProperty().addListener((observable, oldvalue, newvalue) -> {
                    if (!newvalue) {
                        daten.storedFilter.getSelectedFilter().setDays((int) slDays.getValue());
                    }
                }
        );
    }

    private void initDurFilter() {
        slDur.setMin(0);
        slDur.setMax(SelectedFilter.FILTER_DURATIION_MAX_MIN);

        slDur.setShowTickLabels(true);
        slDur.setMinorTickCount(3);
        slDur.setMajorTickUnit(50);
        slDur.setBlockIncrement(5);
        slDur.setSnapToTicks(false);

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slDur.setHighValue(daten.storedFilter.getSelectedFilter().getMaxDur());
        slDur.setLowValue(daten.storedFilter.getSelectedFilter().getMinDur());

        // lowvalue
        daten.storedFilter.getSelectedFilter().minDurProperty().addListener(l -> {
            slDur.setLowValue(daten.storedFilter.getSelectedFilter().getMinDur());
        });
        slDur.lowValueProperty().addListener(l -> setLabelSlider());
        slDur.lowValueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                daten.storedFilter.getSelectedFilter().setMinDur((int) slDur.getLowValue());
            }
        });

        // hightvalue
        daten.storedFilter.getSelectedFilter().maxDurProperty().addListener(l -> slDur.setHighValue(daten.storedFilter.getSelectedFilter().getMaxDur()));
        slDur.highValueProperty().addListener(l -> setLabelSlider());
        slDur.highValueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                daten.storedFilter.getSelectedFilter().setMaxDur((int) slDur.getHighValue());
            }
        });
    }

    private void initFilmTimeFilter() {
        slFilmTime.setMin(0);
        slFilmTime.setMax(SelectedFilter.FILTER_FILMTIME_MAX_SEC);

        slFilmTime.setShowTickLabels(true);
        slFilmTime.setMinorTickCount(2);
        slFilmTime.setMajorTickUnit(4 * 60 * 60);
        slFilmTime.setBlockIncrement(20 * 60);
        slFilmTime.setSnapToTicks(false);

        slFilmTime.setLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number x) {
                int i = x.intValue();
                i = i / (60 * 60 - 1);
                return i + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        tglFilmTime.selectedProperty().bindBidirectional(daten.storedFilter.getSelectedFilter().minMaxTimeInvertProperty());
        GridPane.setFillWidth(tglFilmTime, false);

        // kein direktes binding wegen: valueChangingProperty, nur melden wenn "steht"
        slFilmTime.setHighValue(daten.storedFilter.getSelectedFilter().getMaxTime());
        slFilmTime.setLowValue(daten.storedFilter.getSelectedFilter().getMinTime());

        // lowvalue
        daten.storedFilter.getSelectedFilter().minTimeProperty().addListener(l -> {
            slFilmTime.setLowValue(daten.storedFilter.getSelectedFilter().getMinTime());
        });
        slFilmTime.lowValueProperty().addListener(l -> setLabelSlider());
        slFilmTime.lowValueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                daten.storedFilter.getSelectedFilter().setMinTime((int) slFilmTime.getLowValue());
            }
        });

        // hightvalue
        daten.storedFilter.getSelectedFilter().maxTimeProperty().addListener(l ->
                slFilmTime.setHighValue(daten.storedFilter.getSelectedFilter().getMaxTime()));
        slFilmTime.highValueProperty().addListener(l -> setLabelSlider());
        slFilmTime.highValueChangingProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                daten.storedFilter.getSelectedFilter().setMaxTime((int) slFilmTime.getHighValue());
            }
        });
    }

    private void setSlider() {
        // Tage
        VBox vBox = new VBox(5);
        vBox.getChildren().addAll(lblDays, slDays);
        vbFilter.getChildren().addAll(vBox);

        slDays.visibleProperty().bind(daten.storedFilter.getSelectedFilter().daysVisProperty());
        slDays.managedProperty().bind(daten.storedFilter.getSelectedFilter().daysVisProperty());

        lblDays.visibleProperty().bind(daten.storedFilter.getSelectedFilter().daysVisProperty());
        lblDays.managedProperty().bind(daten.storedFilter.getSelectedFilter().daysVisProperty());


        // MinMax Dauer
        vBox = new VBox(5);
        vBox.getChildren().addAll(lblDur, slDur);
        vbFilter.getChildren().addAll(vBox);

        slDur.visibleProperty().bind(daten.storedFilter.getSelectedFilter().minMaxDurVisProperty());
        slDur.managedProperty().bind(daten.storedFilter.getSelectedFilter().minMaxDurVisProperty());

        lblDur.visibleProperty().bind(daten.storedFilter.getSelectedFilter().minMaxDurVisProperty());
        lblDur.managedProperty().bind(daten.storedFilter.getSelectedFilter().minMaxDurVisProperty());


        // MinMax Uhrzeit
        vBox = new VBox(5);
        vBox.getChildren().addAll(lblFilmTime, slFilmTime, tglFilmTime);
        vbFilter.getChildren().addAll(vBox);

        slFilmTime.visibleProperty().bind(daten.storedFilter.getSelectedFilter().minMaxTimeVisProperty());
        slFilmTime.managedProperty().bind(daten.storedFilter.getSelectedFilter().minMaxTimeVisProperty());

        lblFilmTime.visibleProperty().bind(daten.storedFilter.getSelectedFilter().minMaxTimeVisProperty());
        lblFilmTime.managedProperty().bind(daten.storedFilter.getSelectedFilter().minMaxTimeVisProperty());

        tglFilmTime.visibleProperty().bind(daten.storedFilter.getSelectedFilter().minMaxTimeVisProperty());
        tglFilmTime.managedProperty().bind(daten.storedFilter.getSelectedFilter().minMaxTimeVisProperty());


        setLabelSlider();
    }

    private void initCheckFilter() {
        checkOnly.getItems().addAll(ONLY_HD, ONLY_UT, ONLY_NEW, ONLY_LIVE, ONLY_AKT_HISTORY);
        checkNot.getItems().addAll(NOT_ABO, NOT_HISTORY, NOT_DOUBLE, NOT_GEO, NOT_FUTURE);

        checkOnly.getItemBooleanProperty(ONLY_HD)
                .bindBidirectional(daten.storedFilter.getSelectedFilter().onlyHdProperty());
        checkOnly.getItemBooleanProperty(ONLY_UT)
                .bindBidirectional(daten.storedFilter.getSelectedFilter().onlyUtProperty());
        checkOnly.getItemBooleanProperty(ONLY_NEW)
                .bindBidirectional(daten.storedFilter.getSelectedFilter().onlyNewProperty());
        checkOnly.getItemBooleanProperty(ONLY_LIVE)
                .bindBidirectional(daten.storedFilter.getSelectedFilter().onlyLiveProperty());
        checkOnly.getItemBooleanProperty(ONLY_AKT_HISTORY)
                .bindBidirectional(daten.storedFilter.getSelectedFilter().onlyAktHistoryProperty());
        checkNot.getItemBooleanProperty(NOT_ABO)
                .bindBidirectional(daten.storedFilter.getSelectedFilter().notAboProperty());
        checkNot.getItemBooleanProperty(NOT_HISTORY)
                .bindBidirectional(daten.storedFilter.getSelectedFilter().notHistoryProperty());
        checkNot.getItemBooleanProperty(NOT_DOUBLE)
                .bindBidirectional(daten.storedFilter.getSelectedFilter().notDoubleProperty());
        checkNot.getItemBooleanProperty(NOT_GEO)
                .bindBidirectional(daten.storedFilter.getSelectedFilter().notGeoProperty());
        checkNot.getItemBooleanProperty(NOT_FUTURE)
                .bindBidirectional(daten.storedFilter.getSelectedFilter().notFutureProperty());

        checkOnly.setMaxWidth(Double.MAX_VALUE);
        checkNot.setMaxWidth(Double.MAX_VALUE);

        VBox v = new VBox();
        v.getChildren().addAll(lblOnly, checkOnly);
        vbFilter.getChildren().add(v);

        v = new VBox();
        v.getChildren().addAll(lblNot, checkNot);
        vbFilter.getChildren().add(v);


        lblOnly.visibleProperty().bind(daten.storedFilter.getSelectedFilter().onlyVisProperty());
        checkOnly.visibleProperty().bind(daten.storedFilter.getSelectedFilter().onlyVisProperty());

        lblOnly.managedProperty().bind(daten.storedFilter.getSelectedFilter().onlyVisProperty());
        checkOnly.managedProperty().bind(daten.storedFilter.getSelectedFilter().onlyVisProperty());

        lblNot.visibleProperty().bind(daten.storedFilter.getSelectedFilter().notVisProperty());
        checkNot.visibleProperty().bind(daten.storedFilter.getSelectedFilter().notVisProperty());

        lblNot.managedProperty().bind(daten.storedFilter.getSelectedFilter().notVisProperty());
        checkNot.managedProperty().bind(daten.storedFilter.getSelectedFilter().notVisProperty());
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

        final Button btnHelp = new Button("");
        btnHelp.setGraphic(new Icons().ICON_BUTTON_HELP);
        btnHelp.setOnAction(a -> new MTAlert().showHelpAlert("Filter", HelpText.GUI_FILM_FILTER));

        h = new HBox();
        h.setSpacing(10);
        h.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(h, Priority.ALWAYS);
        h.getChildren().addAll(mbFilterTools, btnHelp);
        vb.getChildren().add(h);
        vbFilter.getChildren().add(vb);
    }

    final String pattern = "HH:mm";
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

    private void setLabelSlider() {
        String txtAll;

        // Tag
        txtAll = "alles";

        int i = (int) slDays.getValue();
        String tNr = i + "";

        if (i == SelectedFilter.FILTER_DAYS_MAX) {
            lblDays.setText("Zeitraum: " + txtAll);
        } else {
            lblDays.setText("Zeitraum: " + tNr + (i == 1 ? " Tag" : " Tage"));
        }


        // Filmlänge
        int min = (int) slDur.getLowValue();
        int max = (int) slDur.getHighValue();
        String tMin = min + "";
        String tMax = max + "";

        if (min == 0 && max == SelectedFilter.FILTER_DURATIION_MAX_MIN) {
            lblDur.setText("Filmlänge: " + txtAll);
        } else if (min == 0) {
            lblDur.setText("Filmlänge: weniger als " + tMax + " Minuten");
        } else if (max == SelectedFilter.FILTER_DURATIION_MAX_MIN) {
            lblDur.setText("Filmlänge: mehr als " + tMin + " Minuten");
        } else {
            lblDur.setText("Filmlänge: von " + tMin + " bis " + tMax + " Minuten");
        }

        // Film-Uhrzeit
        int iLow = (int) slFilmTime.getLowValue();
        LocalTime lt = LocalTime.ofSecondOfDay(iLow);
        String timeL = lt.format(formatter);

        int iHi = (int) slFilmTime.getHighValue();
        lt = LocalTime.ofSecondOfDay(iHi);
        String timeH = iHi == SelectedFilter.FILTER_FILMTIME_MAX_SEC ? "24:00" : lt.format(formatter);

        if (iLow == 0 && iHi == SelectedFilter.FILTER_FILMTIME_MAX_SEC) {
            lblFilmTime.setText("Sendezeit: " + txtAll);
        } else {
            lblFilmTime.setText("Sendezeit: von " + timeL + " bis " + timeH);
        }
    }

    /*
     * VBox mit den FilterStrings
     */

    private void loadFilter() {
        daten.storedFilter.loadStoredFilter(cbFilter.getSelectionModel().getSelectedItem());
    }

    private void saveFilter() {
        if (cbFilter.getSelectionModel().getSelectedItem() == null) {
            newFilter();
        } else {
            daten.storedFilter.saveStoredFilter(cbFilter.getSelectionModel().getSelectedItem());
        }
    }

    private void delFilter() {
        daten.storedFilter.removeStoredFilter(cbFilter.getSelectionModel().getSelectedItem());
        cbFilter.getSelectionModel().clearSelection();
    }

    private void delAllFilter() {
        if (new MTAlert().showAlert("Löschen", "Filter löschen", "Sollen alle Filter gelöscht werden?")) {
            daten.storedFilter.removeAllStoredFilter();
        }
    }

    private void newFilter() {
        final TextInputDialog dialog = new TextInputDialog(daten.storedFilter.getNextName());
        dialog.setTitle("Filtername");
        dialog.setHeaderText("Den Namen des Filters vorgeben");
        dialog.setContentText("Name:");

        final Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            daten.storedFilter.addNewStoredFilter(result.get());
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
        if (daten.storedFilter.txtFilterIsEmpty()) {
            daten.storedFilter.clearFilter();
        } else {
            daten.storedFilter.clearTxtFilter();
        }
    }

    private void editFilter() {
        final FilmFilterEditDialog editFilterDialog = new FilmFilterEditDialog(daten);
        editFilterDialog.init();
    }

}
