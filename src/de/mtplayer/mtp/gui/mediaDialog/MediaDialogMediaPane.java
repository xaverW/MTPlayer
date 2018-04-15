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
import de.mtplayer.mLib.tools.Functions;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.gui.mediaDb.MediaDbData;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.gui.tools.MTOpen;
import de.mtplayer.mtp.tools.storedFilter.Filter;
import javafx.application.Platform;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Date;
import java.util.regex.Pattern;

public class MediaDialogMediaPane extends ScrollPane {

    Button btnIndex = new Button("Mediensammlung neu aufbauen");
    Button btnPlay = new Button();
    Button btnOpen = new Button();

    ProgressBar progress = new ProgressBar();

    Label lblGesamtMedia = new Label();
    Label lblTrefferMedia = new Label();
    TableView<MediaDbData> tableMedia = new TableView();
    TextField txtTitleMedia = new TextField();
    TextField txtPathMedia = new TextField();

    Daten daten = Daten.getInstance();
    private String searchStr = "";
    private final Listener listenerDbStop;

    public MediaDialogMediaPane() {
        initPanel();
        listenerDbStop = new Listener(Listener.EREIGNIS_MEDIA_DB_STOP, MediaDialogController.class.getSimpleName()) {
            @Override
            public void ping() {
                filter(searchStr);
            }
        };
    }

    public void mediaPaneClose() {
        Listener.removeListener(listenerDbStop);
    }

    private void initPanel() {
        HBox hBoxSum = new HBox();
        hBoxSum.setPadding(new Insets(10));
        hBoxSum.setSpacing(10);
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        hBoxSum.getChildren().addAll(new Label("Treffer:"), lblTrefferMedia,
                region, new Label("Anzahl Medien gesamt:"), lblGesamtMedia);


        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.add(new Label("Titel:"), 0, 0);
        GridPane.setHgrow(txtTitleMedia, Priority.ALWAYS);
        txtTitleMedia.setEditable(false);
        gridPane.add(txtTitleMedia, 1, 0);
        gridPane.add(btnPlay, 2, 0);

        gridPane.add(new Label("Pfad:"), 0, 1);
        txtPathMedia.setEditable(false);
        GridPane.setHgrow(txtPathMedia, Priority.ALWAYS);
        gridPane.add(txtPathMedia, 1, 1);
        gridPane.add(btnOpen, 2, 1);


        region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        HBox hBoxProgess = new HBox();
        hBoxProgess.setSpacing(10);
        hBoxProgess.setPadding(new Insets(10));
        progress.setVisible(false);
        hBoxProgess.getChildren().addAll(progress, region, btnIndex);


        tableMedia.setMinHeight(Const.MIN_TABLE_HEIGHT);

        VBox vBoxMedia = new VBox();
        VBox.setVgrow(tableMedia, Priority.ALWAYS);
        vBoxMedia.getChildren().addAll(hBoxSum, tableMedia, gridPane, hBoxProgess);

        this.setContent(vBoxMedia);
    }

    public void make() {
        Listener.addListener(listenerDbStop);

        daten.mediaDbList.sizeProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> lblGesamtMedia.setText(daten.mediaDbList.size() + "")));

        progress.visibleProperty().bind(daten.mediaDbList.propSearchProperty());
        btnIndex.disableProperty().bind(daten.mediaDbList.propSearchProperty());
        btnIndex.setOnAction(e -> daten.mediaDbList.createMediaDB());

        btnOpen.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnOpen.setOnAction(e -> open());
        btnOpen.disableProperty().bind(txtPathMedia.textProperty().isEmpty().and(txtTitleMedia.textProperty().isEmpty()));

        btnPlay.setGraphic(new Icons().ICON_BUTTON_PLAY);
        btnPlay.setOnAction(e -> play());
        btnPlay.disableProperty().bind(txtPathMedia.textProperty().isEmpty().and(txtTitleMedia.textProperty().isEmpty()));

        initTableMedien();
        setTableDate();
    }

    private void play() {
        String file = txtTitleMedia.getText();
        String dir = txtPathMedia.getText();
        if (!file.isEmpty() && !dir.isEmpty()) {
            MTOpen.playStoredFilm(Functions.addsPfad(dir, file));
        }
    }

    private void open() {
        String s = txtPathMedia.getText();
        MTOpen.openDestDir(s);
    }

    private void setTableDate() {
        SortedList<MediaDbData> sortedList = daten.mediaDbList.getSortedList();
        lblGesamtMedia.setText(daten.mediaDbList.size() + "");
        tableMedia.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableMedia.comparatorProperty());
    }


    private void initTableMedien() {
        txtTitleMedia.setText("");
        txtPathMedia.setText("");

        tableMedia.getColumns().clear();

        final TableColumn<MediaDbData, String> nameColumn = new TableColumn<>("Name");
        final TableColumn<MediaDbData, String> pathColumn = new TableColumn<>("Pfad");
        final TableColumn<MediaDbData, Date> sizeColumn = new TableColumn<>("Größe [MB]");
        final TableColumn<MediaDbData, Boolean> externColumn = new TableColumn<>("extern");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        externColumn.setCellValueFactory(new PropertyValueFactory<>("extern"));
        externColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);

        tableMedia.getColumns().addAll(nameColumn, pathColumn, sizeColumn, externColumn);

        nameColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(50.0 / 100));
        pathColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(25.0 / 100));
        sizeColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(15.0 / 100));
        externColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(10.0 / 100));

        tableMedia.getSelectionModel().selectedItemProperty().addListener((observableValue, dataOld, dataNew) -> {
            if (dataNew != null) {
                txtPathMedia.setText(dataNew.getPath());
                txtTitleMedia.setText(dataNew.getName());
            } else {
                txtPathMedia.setText("");
                txtTitleMedia.setText("");
            }
        });
    }


    public void filter(String searchStr) {
        this.searchStr = searchStr;
        daten.mediaDbList.filterdListSetPred(media -> {
            if (searchStr.isEmpty()) {
                return false;
            }
            final Pattern p = Filter.makePattern(searchStr);
            if (p != null) {
                return filterMedien(media, p);
            } else {
                return filterMedien(media, searchStr);
            }
        });
        lblTrefferMedia.setText(daten.mediaDbList.getFilteredList().size() + "");
    }

    private boolean filterMedien(MediaDbData media, Pattern p) {
        return p.matcher(media.getName()).matches();
    }

    private boolean filterMedien(MediaDbData media, String search) {
        return media.getName().toLowerCase().contains(search);
    }

}
