/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

import de.mtplayer.mLib.tools.DirFileChooser;
import de.mtplayer.mLib.tools.MLAlert;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.dialog.MTAlert;
import de.mtplayer.mtp.gui.mediaDb.MediaPathData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.controlsfx.control.table.TableRowExpanderColumn;

import java.util.ArrayList;
import java.util.Collection;

public class MediaConfigMediaPaneController extends AnchorPane {

    private final Daten daten;
    VBox noaccordion = new VBox();
    private final Accordion accordion = new Accordion();
    private final HBox hBox = new HBox(0);
    private final CheckBox cbxAccordion = new CheckBox("");
    private final BooleanProperty accordionProp = Config.MEDIA_CONFIG_DIALOG_ACCORDION.getBooleanProperty();
    ScrollPane scrollPane = new ScrollPane();

    BooleanProperty prefSuff = Config.MEDIA_DB_WITH_OUT_SUFFIX.getBooleanProperty();
    StringProperty prefSuffStr = Config.MEDIA_DB_SUFFIX.getStringProperty();

    public MediaConfigMediaPaneController() {
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
        makeConfig(result);
        makeTable(result);
        return result;
    }

    private void makeConfig(Collection<TitledPane> result) {
        VBox vBox = new VBox();

        final GridPane gridPane = new GridPane();
        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20, 20, 20, 20));

        TitledPane tpConfig = new TitledPane("Allgemein", vBox);
        result.add(tpConfig);

        final RadioButton rbWithOutSuf = new RadioButton("Keine Dateien mit diesem Suffix (z.B.: txt,xml,jpg");
        final RadioButton rbWithSuff = new RadioButton("Nur Dateien mit diesem Suffix  (z.B.: mp4,flv,m4v");

        final ToggleGroup tg = new ToggleGroup();
        rbWithOutSuf.setToggleGroup(tg);
        rbWithSuff.setToggleGroup(tg);

        rbWithSuff.setSelected(!prefSuff.getValue());
        rbWithOutSuf.selectedProperty().bindBidirectional(prefSuff);

        TextField txtSuff = new TextField();
        txtSuff.textProperty().bindBidirectional(prefSuffStr);


        gridPane.add(rbWithOutSuf, 0, 0);
        gridPane.add(rbWithSuff, 0, 1);
        gridPane.add(txtSuff, 0, 2);

        vBox.getChildren().addAll(gridPane);
    }

    private void makeTable(Collection<TitledPane> result) {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        TitledPane tpConfig = new TitledPane("Pfade", vBox);
        result.add(tpConfig);

        TableView<MediaPathData> tableView = new TableView<>();
        tableView.setMinHeight(Const.MIN_TABLE_HEIGHT);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        final TableColumn<MediaPathData, String> pathColumn = new TableColumn<>("Pfad");
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        tableView.getColumns().addAll(expander, pathColumn);
        tableView.setItems(daten.mediaPathList);

        VBox.setVgrow(tableView, Priority.ALWAYS);
        vBox.getChildren().addAll(tableView);


        Button del = new Button("");
        del.setGraphic(new Icons().ICON_BUTTON_REMOVE);
        del.setOnAction(event -> {
            final ObservableList<MediaPathData> sels = tableView.getSelectionModel().getSelectedItems();
            if (sels == null || sels.isEmpty()) {
                new MTAlert().showInfoNoSelection();
            } else {
                daten.mediaPathList.removeAll(sels);
                tableView.getSelectionModel().clearSelection();
            }
        });

        TextField txtPath = new TextField();
        txtPath.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(txtPath, Priority.ALWAYS);

        final Button btnFile = new Button();
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnFile.setOnAction(event -> {
            DirFileChooser.DirChooser(Daten.getInstance().primaryStage, txtPath);
        });

        Button btnNeu = new Button("");
        btnNeu.setGraphic(new Icons().ICON_BUTTON_ADD);
        btnNeu.setOnAction(event -> {
            MediaPathData mediaPathData = new MediaPathData(txtPath.getText());
            if (daten.mediaPathList.addSave(mediaPathData)) {
                tableView.getSelectionModel().select(mediaPathData);
                tableView.scrollTo(mediaPathData);
            } else {
                new MLAlert().showErrorAlert("Pfad zur Mediensammlung hinzufügen",
                        "Der Pfad ist schon enthalten");
            }
        });
        btnNeu.disableProperty().bind(txtPath.textProperty().isEmpty());

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.getChildren().addAll(del, btnNeu, txtPath, btnFile);

        HBox.setHgrow(txtPath, Priority.ALWAYS);
        vBox.getChildren().addAll(hBox1);
    }


    TableRowExpanderColumn<MediaPathData> expander = new TableRowExpanderColumn<>(param -> {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        gridPane.setStyle("-fx-background-color: #E0E0E0;");
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        TextField txtPath = new TextField();
        txtPath.setMinWidth(500); //todo geht eleganter
        txtPath.textProperty().bindBidirectional(param.getValue().pathProperty());

        final Button btnFile = new Button();
        btnFile.setOnAction(event -> {
            DirFileChooser.DirChooser(Daten.getInstance().primaryStage, txtPath);
        });
        btnFile.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);


        gridPane.add(new Label("Pfad: "), 0, 0);
        gridPane.add(txtPath, 1, 0);
        gridPane.add(btnFile, 2, 0);

        return gridPane;
    });
}
