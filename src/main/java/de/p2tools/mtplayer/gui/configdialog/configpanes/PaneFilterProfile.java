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

package de.p2tools.mtplayer.gui.configdialog.configpanes;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.filter.FilmFilter;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.guitools.pcbo.P2CboSeparator;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Collection;
import java.util.Optional;

public class PaneFilterProfile {


    private final Stage stage;
    private final ComboBox<FilmFilter> cboFilterProfilesFilm = new P2CboSeparator<>();
    private final ComboBox<FilmFilter> cboFilterProfilesAudio = new P2CboSeparator<>();
    private final P2ToggleSwitch tglFilmOn = new P2ToggleSwitch("Filter-Profil für die Filme verwenden");
    private final P2ToggleSwitch tglAudioOn = new P2ToggleSwitch("Filter-Profil für die Audios verwenden");
    private final ProgData progData;

    public PaneFilterProfile(Stage stage) {
        this.stage = stage;
        this.progData = ProgData.getInstance();
    }

    public void close() {
        tglFilmOn.selectedProperty().unbindBidirectional(ProgConfig.FILTER_FILM_PROG_START_ON);
        tglAudioOn.selectedProperty().unbindBidirectional(ProgConfig.FILTER_AUDIO_PROG_START_ON);
    }

    public void make(Collection<TitledPane> result) {
        final VBox vBox = new VBox(10);
        vBox.setFillWidth(true);
        vBox.setPadding(new Insets(P2LibConst.PADDING));

        HBox hBox = new HBox();
        hBox.getStyleClass().add("extra-pane");
        hBox.setPadding(new Insets(P2LibConst.PADDING));
        hBox.setMaxWidth(Double.MAX_VALUE);
        hBox.setMinHeight(Region.USE_PREF_SIZE);
        Label lbl = new Label("Hier kann ein Filter-Profil ausgewählt werden, das " +
                "dann beim Programmstart geladen wird.");
        lbl.setWrapText(true);
        hBox.getChildren().add(lbl);
        vBox.getChildren().addAll(hBox, P2GuiTools.getHDistance(20));

        make(vBox);
        initCbo();

        TitledPane tpOffer = new TitledPane("Filter-Profile", vBox);
        result.add(tpOffer);
        tpOffer.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpOffer, Priority.ALWAYS);
    }

    private void make(VBox vBox) {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        vBox.getChildren().add(gridPane);

        int row = 0;
        gridPane.add(tglFilmOn, 0, row, 2, 1);
        gridPane.add(P2Text.getLblTextBold("Filme:"), 0, ++row);
        gridPane.add(cboFilterProfilesFilm, 1, row);

        gridPane.add(new Label(), 0, ++row);

        gridPane.add(tglAudioOn, 0, ++row, 2, 1);
        gridPane.add(P2Text.getLblTextBold("Audios:"), 0, ++row);
        gridPane.add(cboFilterProfilesAudio, 1, row);

        GridPane.setVgrow(cboFilterProfilesFilm, Priority.ALWAYS);
        GridPane.setVgrow(cboFilterProfilesAudio, Priority.ALWAYS);
        cboFilterProfilesFilm.setMaxWidth(Double.MAX_VALUE);
        cboFilterProfilesAudio.setMaxWidth(Double.MAX_VALUE);

        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow());

        final Button btnHelp = PIconFactory.getHelpButton(stage, "Filter-Profile",
                "Hier kann ein Filter-Profile (eins für die Filmliste, eins für " +
                        "die Audioliste) ausgewählt werden. Wird das Programm gestartet " +
                        "wird dieses Profil ausgewählt und der Filter aktiviert. " +
                        "Ist die Funktion nicht eingeschaltet, wird immer der zuletzt " +
                        "eingestellte Filter wieder geladen.");
        HBox hBox = new HBox();
        hBox.getChildren().add(btnHelp);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        vBox.getChildren().addAll(P2GuiTools.getVBoxGrower(), hBox);
    }

    private void initCbo() {
        cboFilterProfilesFilm.setItems(progData.filterWorkerFilm.getFilmFilterList());
        cboFilterProfilesFilm.setTooltip(new Tooltip("Das ausgewählte Filterprofil\n" +
                "wird beim Programmstart geladen."));

        cboFilterProfilesAudio.setItems(progData.filterWorkerAudio.getFilmFilterList());
        cboFilterProfilesAudio.setTooltip(new Tooltip("Das ausgewählte Filterprofil\n" +
                "wird beim Programmstart geladen."));


        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) ->
                cboFilterProfilesFilm.setCellFactory(new ListViewListCellCallback())); // todo notwendig??
        cboFilterProfilesFilm.setCellFactory(new ListViewListCellCallback());
        ProgConfig.SYSTEM_THEME_CHANGED.addListener((u, o, n) ->
                cboFilterProfilesAudio.setCellFactory(new ListViewListCellCallback())); // todo notwendig??
        cboFilterProfilesAudio.setCellFactory(new ListViewListCellCallback());


        if (!ProgConfig.FILTER_FILM_PROG_START.getValueSafe().isEmpty()) {
            Optional<FilmFilter> opt = progData.filterWorkerFilm.getFilmFilterList()
                    .stream()
                    .filter(f -> f.getName().equals(ProgConfig.FILTER_FILM_PROG_START.get())).findAny();
            opt.ifPresent(filmFilter -> cboFilterProfilesFilm.getSelectionModel().select(filmFilter));
        }
        if (!ProgConfig.FILTER_AUDIO_PROG_START.getValueSafe().isEmpty()) {
            Optional<FilmFilter> opt = progData.filterWorkerAudio.getFilmFilterList()
                    .stream()
                    .filter(f -> f.getName().equals(ProgConfig.FILTER_AUDIO_PROG_START.get())).findAny();
            opt.ifPresent(filmFilter -> cboFilterProfilesAudio.getSelectionModel().select(filmFilter));
        }


        cboFilterProfilesFilm.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (cboFilterProfilesFilm.getSelectionModel().getSelectedItem() != null) {
                ProgConfig.FILTER_FILM_PROG_START.set(cboFilterProfilesFilm.getSelectionModel().getSelectedItem().getName());
            }
        });
        cboFilterProfilesAudio.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (cboFilterProfilesAudio.getSelectionModel().getSelectedItem() != null) {
                ProgConfig.FILTER_AUDIO_PROG_START.set(cboFilterProfilesAudio.getSelectionModel().getSelectedItem().getName());
            }
        });


        cboFilterProfilesFilm.disableProperty().bind(ProgConfig.FILTER_FILM_PROG_START_ON.not());
        cboFilterProfilesAudio.disableProperty().bind(ProgConfig.FILTER_AUDIO_PROG_START_ON.not());
        tglFilmOn.selectedProperty().bindBidirectional(ProgConfig.FILTER_FILM_PROG_START_ON);
        tglAudioOn.selectedProperty().bindBidirectional(ProgConfig.FILTER_AUDIO_PROG_START_ON);
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

                    if (P2CboSeparator.isSeparator(item.toString())) {
                        setGraphic(PIconFactory.PICON.BTN_SEPARATOR_WIDTH.getFontIcon());
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
