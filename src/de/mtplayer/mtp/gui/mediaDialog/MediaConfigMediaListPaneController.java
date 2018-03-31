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

package de.mtplayer.mtp.gui.mediaDialog;

import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.gui.mediaDb.MediaDbData;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Collection;

public class MediaConfigMediaListPaneController extends AnchorPane {

    private final Daten daten;
    private Label lblGesamtMedia = new Label();
    private VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final BooleanProperty accordionProp = Config.MEDIA_CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    private ScrollPane scrollPane = new ScrollPane();

    public MediaConfigMediaListPaneController() {
        daten = Daten.getInstance();

        cbxAccordion.selectedProperty().bindBidirectional(accordionProp);
        cbxAccordion.selectedProperty().addListener((observable, oldValue, newValue) -> setAccordion());

        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        hBox.getChildren().addAll(cbxAccordion, scrollPane);
        getChildren().addAll(hBox);

        AnchorPane.setLeftAnchor(hBox, 10.0);
        AnchorPane.setBottomAnchor(hBox, 10.0);
        AnchorPane.setRightAnchor(hBox, 10.0);
        AnchorPane.setTopAnchor(hBox, 10.0);

        setAccordion();
    }

    private void setAccordion() {
        if (cbxAccordion.isSelected()) {
            noaccordion.getChildren().clear();
            accordion.getPanes().addAll(createPanes());
            scrollPane.setContent(accordion);
        } else {
            accordion.getPanes().clear();
            noaccordion.getChildren().addAll(createPanes());
            scrollPane.setContent(noaccordion);
        }
    }

    private Collection<TitledPane> createPanes() {
        Collection<TitledPane> result = new ArrayList<TitledPane>();
        initTable(result);

        lblGesamtMedia.setText(daten.mediaDbList.size() + "");
        daten.mediaDbList.sizeProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> lblGesamtMedia.setText(daten.mediaDbList.size() + "")));

        return result;
    }

    private void initTable(Collection<TitledPane> result) {
        HBox hBoxSum = new HBox();
        hBoxSum.setPadding(new Insets(10));
        hBoxSum.setSpacing(10);
        hBoxSum.getChildren().addAll(new Label("Anzahl Medien gesamt:"), lblGesamtMedia);

        VBox vBox = new VBox();
        vBox.setSpacing(10);

        TitledPane tpConfig = new TitledPane("Mediensammlung", vBox);
        result.add(tpConfig);
        tpConfig.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpConfig, Priority.ALWAYS);

        TableView<MediaDbData> tableView = new TableView<>();
        tableView.setMinHeight(Region.USE_PREF_SIZE);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TableColumn<MediaDbData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<MediaDbData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        final TableColumn<MediaDbData, String> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));

        tableView.getColumns().addAll(nameColumn, pathColumn, sizeColumn);

        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(60.0 / 100));
        pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(25.0 / 100));
        sizeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(14.0 / 100));

        SortedList<MediaDbData> sortedList = daten.mediaDbList.getSortedList();
        daten.mediaDbList.filterdListClearPred(true);
        tableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView, hBoxSum);
    }


}
