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

import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.HistoryData;
import de.p2tools.p2Lib.dialog.PAlert;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Collection;

public class MediaConfigPaneHistoryController extends AnchorPane {

    private final ProgData progData;
    private final boolean history;
    private Label lblGesamtMedia = new Label();
    private VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final BooleanProperty accordionProp = ProgConfig.MEDIA_CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    private ScrollPane scrollPane = new ScrollPane();

    public MediaConfigPaneHistoryController(boolean history) {
        progData = ProgData.getInstance();
        this.history = history;

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
        makeTable(result);
        if (history) {
            lblGesamtMedia.setText(progData.history.size() + "");
            progData.history.addListener((ListChangeListener.Change<? extends HistoryData> c) -> {
                Platform.runLater(() -> {
                    lblGesamtMedia.setText(progData.history.size() + "");
                });
            });
        } else {
            lblGesamtMedia.setText(progData.erledigteAbos.size() + "");
            progData.erledigteAbos.addListener((ListChangeListener.Change<? extends HistoryData> c) -> {
                Platform.runLater(() -> {
                    lblGesamtMedia.setText(progData.erledigteAbos.size() + "");
                });
            });
        }

        return result;
    }

    private void makeTable(Collection<TitledPane> result) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        TitledPane tpConfig = new TitledPane(history ? "History" : "Downloads", vBox);
        result.add(tpConfig);
        tpConfig.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(tpConfig, Priority.ALWAYS);

        TableView<HistoryData> tableView = new TableView<>();
        tableView.setMinHeight(Region.USE_PREF_SIZE);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        final TableColumn<HistoryData, String> dateColumn = new TableColumn<>("Datum");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        final TableColumn<HistoryData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));

        final TableColumn<HistoryData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        final TableColumn<HistoryData, String> urlColumn = new TableColumn<>("Url");
        urlColumn.setCellValueFactory(new PropertyValueFactory<>("url"));

        dateColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(20.0 / 100));
        themeColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(30.0 / 100));
        titleColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(30.0 / 100));
        urlColumn.prefWidthProperty().bind(tableView.widthProperty().multiply(19.0 / 100));

        tableView.getColumns().addAll(dateColumn, themeColumn, titleColumn, urlColumn);
        if (history) {
            tableView.setItems(progData.history);
        } else {
            tableView.setItems(progData.erledigteAbos);
        }

        tableView.setOnMousePressed(m -> {
            if (m.getButton().equals(MouseButton.SECONDARY)) {
                ArrayList<HistoryData> historyDataArrayList = new ArrayList<>();
                historyDataArrayList.addAll(tableView.getSelectionModel().getSelectedItems());
                if (historyDataArrayList.isEmpty()) {
                    PAlert.showInfoNoSelection();
                } else {
                    ContextMenu contextMenu = new MediaConfigPaneHistoryContextMenu(historyDataArrayList, history).getContextMenu();
                    tableView.setContextMenu(contextMenu);

                }

            }
        });

        Button btnDel = new Button("Liste löschen");
        btnDel.setOnAction(event -> {
            if (history) {
                progData.history.clearAll();
            } else {
                progData.erledigteAbos.clearAll();
            }
        });

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        hBox.getChildren().addAll(new Label("Anzahl Medien gesamt:"), lblGesamtMedia,
                region, btnDel);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView, hBox);
    }


}
