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

package de.p2tools.mtplayer.gui.dialog.propose;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.propose.ProposeFactory;
import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.gui.dialog.FilmInfoDialogController;
import de.p2tools.mtplayer.gui.tools.table.CellFilmButton;
import de.p2tools.mtplayer.gui.tools.table.TableRowFilm;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.prange.P2RangeBox;
import de.p2tools.p2lib.guitools.ptable.P2CellIntNull;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import de.p2tools.p2lib.tools.date.P2Date;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PanePropFilmList {


    private final ProgData progData;
    private final Stage stage;

    private final TableView<FilmDataMTP> tableFilm = new TableView<>();

    public PanePropFilmList(ProgData progData, Stage stage) {
        this.progData = progData;
        this.stage = stage;
    }

    public AnchorPane makePane() {
        final VBox vBox = new VBox(10);
        vBox.setPadding(new Insets(P2LibConst.PADDING));
        initTableFilm(vBox);
        initUnderTable(vBox);

        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);
        AnchorPane.setLeftAnchor(vBox, 0.0);
        anchorPane.getChildren().add(vBox);
        return anchorPane;
    }

    public void close() {
    }

    private void initTableFilm(VBox vBox) {
        tableFilm.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableFilm.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableFilm.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableFilm.setEditable(true);

        final TableColumn<FilmDataMTP, String> channelColumn = new TableColumn<>("Sender");
        channelColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        channelColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        final TableColumn<FilmDataMTP, Integer> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        final TableColumn<FilmDataMTP, String> startColumn = new TableColumn<>("");
        startColumn.setCellFactory(new CellFilmButton().cellFactory);
        startColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, P2Date> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, Integer> proposeColumn = new TableColumn<>("Vorschlag");
        proposeColumn.setCellValueFactory(new PropertyValueFactory<>("propose"));
        proposeColumn.getStyleClass().add("alignCenterRight");

        final TableColumn<FilmDataMTP, String> timeColumn = new TableColumn<>("Zeit");
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        timeColumn.getStyleClass().add("alignCenter");

        final TableColumn<FilmDataMTP, Integer> durationColumn = new TableColumn<>("Dauer [min]");
        durationColumn.setCellFactory(new P2CellIntNull().cellFactory);
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationMinute"));
        durationColumn.getStyleClass().add("alignCenterRightPadding_25");

        channelColumn.setPrefWidth(80);
        themeColumn.setPrefWidth(200);
        titleColumn.setPrefWidth(400);
        tableFilm.getColumns().addAll(channelColumn, themeColumn, titleColumn, startColumn,
                proposeColumn, dateColumn, timeColumn, durationColumn);
        tableFilm.getSelectionModel().selectedItemProperty().addListener((observableValue, dataOld, dataNew) -> {
            FilmInfoDialogController.getInstance().setFilm(tableFilm.getSelectionModel().getSelectedItem());
        });
        tableFilm.setRowFactory(tableView -> {
            TableRowFilm<FilmDataMTP> row = new TableRowFilm<>();

            row.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 &&
                        !row.isEmpty()) {
                    FilmInfoDialogController.getInstanceAndShow().showFilmInfo();
                }
            });
            return row;
        });

        progData.proposeList.getFilmSortedList().comparatorProperty().bind(tableFilm.comparatorProperty());
        tableFilm.setItems(progData.proposeList.getFilmSortedList());

        vBox.getChildren().addAll(tableFilm);
        VBox.setVgrow(tableFilm, Priority.ALWAYS);
    }

    private void initUnderTable(VBox vBox) {
        final P2RangeBox slDur = new P2RangeBox("Filmlänge:", false, 0, FilterCheck.FILTER_DURATION_MAX_MINUTE);

        Button btnGenerate = new Button("Filme suchen");
        btnGenerate.setOnAction(a -> ProposeFactory.generateFilmList(slDur.getActMinValue(), slDur.getActMaxValue()));

        Label lblCountList = new Label();
        lblCountList.textProperty().bind(progData.proposeList.getFilmDataList().sizeProperty().asString());

        // Gridpane
        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);
        gridPane.setPadding(new Insets(0));

        int row = 0;
        gridPane.add(btnGenerate, 0, row);
        gridPane.add(lblCountList, 1, row);
        GridPane.setHalignment(lblCountList, HPos.RIGHT);

        gridPane.add(new Label(), 0, ++row, 2, 1);
        gridPane.add(slDur, 0, ++row, 2, 1);

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());
        vBox.getChildren().addAll(gridPane);
    }
}
