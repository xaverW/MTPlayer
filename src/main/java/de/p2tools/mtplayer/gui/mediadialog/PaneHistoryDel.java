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

package de.p2tools.mtplayer.gui.mediadialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.history.HistoryData;
import de.p2tools.mtplayer.controller.data.history.HistoryFactory;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaneHistoryDel extends VBox {

    private final ProgData progData;
    private final Slider slAge = new Slider();
    private final Label lblAge = new Label();
    private final Stage stage;
    private final Button btnDelAll = new Button("");
    private final Button btnDelSelection = new Button("");
    private final Button btnDelAge = new Button("");
    private final Button btnDelNotInList = new Button("");
    private final TableView<HistoryData> tableView;

    public PaneHistoryDel(ProgData progData, Stage stage, TableView<HistoryData> tableView) {
        this.progData = progData;
        this.stage = stage;
        this.tableView = tableView;
        make();
    }

    public void close() {
        slAge.valueProperty().unbindBidirectional(ProgConfig.HISTORY_DEL_OLD_COUNT_YEARS);
    }

    private void make() {
        slAge.setMin(1);
        slAge.setMax(20);
        slAge.setShowTickLabels(false);
        slAge.setMajorTickUnit(100);
        slAge.setBlockIncrement(5);

        btnDelAll.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        btnDelAll.setOnAction(a -> {
            progData.historyListJson.clearAll(stage);
        });

        btnDelSelection.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        btnDelSelection.setOnAction(a -> {
            HistoryFactory.delSelection(stage, tableView.getSelectionModel().getSelectedItems());
        });

        btnDelNotInList.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        btnDelNotInList.setOnAction(a -> HistoryFactory.delNotInList(stage));

        btnDelAge.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
        btnDelAge.setOnAction(a -> HistoryFactory.delOld(stage, (int) slAge.getValue()));

        Button btnHelp = PIconFactory.getHelpButton(stage,
                "History löschen",
                "Hier können Filme aus der History gelöscht werden." +
                        "\n\n" +
                        "* Alle löschen:\n" +
                        "Es werden alle Einträge gelöscht." +
                        "\n\n" +
                        "* Auswahl löschen:\n" +
                        "Markierte Einträge werden gelöscht." +
                        "\n\n" +
                        "* Nicht mehr in der Film/Audio Liste:\n" +
                        "Einträge die nicht mehr in der Film/Audoliste sind, werden gelöscht." +
                        "\n\n" +
                        "* Alte löschen:\n" +
                        "Einträge die älter sind als vorgegeben und nicht mehr in der Filmliste sind, werden gelöscht.");

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(15);

        gridPane.setPadding(new Insets(P2LibConst.PADDING));
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcComputedSizeAndHgrow(), P2GridConstraints.getCcPrefSize());

        int row = 0;
        gridPane.add(new Label("Alle löschen"), 0, row);
        gridPane.add(btnDelAll, 2, row);

        gridPane.add(new Label("Auswahl löschen"), 0, ++row);
        gridPane.add(btnDelSelection, 2, row);

        gridPane.add(new Label("Nicht mehr in der Film/Audioliste Liste"), 0, ++row);
        gridPane.add(btnDelNotInList, 2, row);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(new Label("Nicht mehr in der Film/Audioliste Liste"), new Label("[mit Mindestalter]"));
        gridPane.add(vBox, 0, ++row);
        HBox hBox = new HBox(P2LibConst.PADDING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(slAge, lblAge);
        HBox.setHgrow(slAge, Priority.ALWAYS);
        gridPane.add(hBox, 1, row);
        gridPane.add(btnDelAge, 2, row);

        gridPane.add(btnHelp, 2, ++row);

        getChildren().add(gridPane);
        setPadding(new Insets(P2LibConst.PADDING));

        slAge.valueProperty().bindBidirectional(ProgConfig.HISTORY_DEL_OLD_COUNT_YEARS);
        slAge.setValue(ProgConfig.HISTORY_DEL_OLD_COUNT_YEARS.get());
        lblAge.setText((int) slAge.getValue() + " Jahre");
        slAge.valueProperty().addListener((u, o, n) -> lblAge.setText((int) slAge.getValue() + " Jahre"));
    }
}
