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

import de.mtplayer.mLib.tools.CheckBoxCell;
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.mediaDb.MediaData;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Collection;

public class MediaConfigPaneMediaListController extends AnchorPane {

    private final ProgData progData;
    private Label lblGesamtMedia = new Label();
    private VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final BooleanProperty accordionProp = ProgConfig.MEDIA_CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    private ScrollPane scrollPane = new ScrollPane();

    public MediaConfigPaneMediaListController() {
        progData = ProgData.getInstance();

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

        lblGesamtMedia.setText(progData.mediaList.size() + "");
        progData.mediaList.sizeProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> lblGesamtMedia.setText(progData.mediaList.size() + "")));

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

        TableView<MediaData> tableView = new TableView<>();
        tableView.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TableColumn<MediaData, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        final TableColumn<MediaData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        final TableColumn<MediaData, String> sizeColumn = new TableColumn<>("Größe [MB]");
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));

        final TableColumn<MediaData, String> colColumn = new TableColumn<>("Sammlung");
        colColumn.setCellValueFactory(new PropertyValueFactory<>("collectionName"));

        final TableColumn<MediaData, Boolean> externalColumn = new TableColumn<>("extern");
        externalColumn.setCellValueFactory(new PropertyValueFactory<>("external"));
        externalColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);

        tableView.getColumns().addAll(nameColumn, pathColumn, sizeColumn, colColumn, externalColumn);


        nameColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(40.0 / 100));
        pathColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(20.0 / 100));
        sizeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(10.0 / 100));
        colColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(20.0 / 100));
        externalColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(5.0 / 100));

        SortedList<MediaData> sortedList = progData.mediaList.getSortedList();
        sortedList.comparatorProperty().bind(tableView.comparatorProperty());
        progData.mediaList.filterdListClearPred(true);
        tableView.setItems(sortedList);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView, hBoxSum);
    }


}