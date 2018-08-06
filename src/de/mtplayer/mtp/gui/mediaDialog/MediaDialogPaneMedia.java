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
import de.mtplayer.mtp.controller.config.ProgConfig;
import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.Icons;
import de.mtplayer.mtp.controller.mediaDb.MediaData;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.tools.storedFilter.Filter;
import de.p2tools.p2Lib.guiTools.POpen;
import javafx.application.Platform;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Date;
import java.util.regex.Pattern;

public class MediaDialogPaneMedia extends ScrollPane {

    Button btnCreateMediaDB = new Button("Mediensammlung neu aufbauen");
    Button btnPlay = new Button();
    Button btnOpen = new Button();

    ProgressBar progress = new ProgressBar();

    Label lblGesamtMedia = new Label();
    Label lblTrefferMedia = new Label();
    TableView<MediaData> tableMedia = new TableView();
    TextField txtTitleMedia = new TextField();
    TextField txtPathMedia = new TextField();

    ProgData progData = ProgData.getInstance();
    private String searchStr = "";
    private final Listener listenerDbStop;

    public MediaDialogPaneMedia() {
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
        hBoxProgess.getChildren().addAll(progress, region, btnCreateMediaDB);


        tableMedia.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);

        VBox vBoxMedia = new VBox();
        VBox.setVgrow(tableMedia, Priority.ALWAYS);
        vBoxMedia.getChildren().addAll(hBoxSum, tableMedia, gridPane, hBoxProgess);

        this.setContent(vBoxMedia);
    }

    public void make() {
        Listener.addListener(listenerDbStop);

        progData.mediaList.sizeProperty().addListener((observable, oldValue, newValue) ->
                Platform.runLater(() -> lblGesamtMedia.setText(progData.mediaList.size() + "")));

        progress.visibleProperty().bind(progData.mediaList.propSearchProperty());
        btnCreateMediaDB.disableProperty().bind(progData.mediaList.propSearchProperty());
        btnCreateMediaDB.setOnAction(e -> progData.mediaList.createMediaDb());

        btnOpen.setGraphic(new Icons().ICON_BUTTON_FILE_OPEN);
        btnOpen.setTooltip(new Tooltip("Ausgewählten Pfad im Dateimanager öffnen."));
        btnOpen.setOnAction(e -> open());
        btnOpen.disableProperty().bind(txtPathMedia.textProperty().isEmpty().and(txtTitleMedia.textProperty().isEmpty()));

        btnPlay.setGraphic(new Icons().ICON_BUTTON_PLAY);
        btnPlay.setOnAction(e -> play());
        btnPlay.disableProperty().bind(txtPathMedia.textProperty().isEmpty().and(txtTitleMedia.textProperty().isEmpty()));

        initTableMedia();
        setTableDate();
    }

    private void play() {
        String file = txtTitleMedia.getText();
        String dir = txtPathMedia.getText();
        if (!file.isEmpty() && !dir.isEmpty()) {
            POpen.playStoredFilm(Functions.addsPath(dir, file),
                    ProgConfig.SYSTEM_PROG_PLAY_FILME.getStringProperty(), new Icons().ICON_BUTTON_FILE_OPEN);
        }
    }

    private void open() {
        String s = txtPathMedia.getText();
        POpen.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR.getStringProperty(), new Icons().ICON_BUTTON_FILE_OPEN);
    }

    private void setTableDate() {
        SortedList<MediaData> sortedList = progData.mediaList.getSortedList();
        lblGesamtMedia.setText(progData.mediaList.size() + "");
        tableMedia.setItems(sortedList);
        sortedList.comparatorProperty().bind(tableMedia.comparatorProperty());
    }


    private void initTableMedia() {
        txtTitleMedia.setText("");
        txtPathMedia.setText("");

        tableMedia.getColumns().clear();

        final TableColumn<MediaData, String> nameColumn = new TableColumn<>("Name");
        final TableColumn<MediaData, String> pathColumn = new TableColumn<>("Pfad");
        final TableColumn<MediaData, Date> sizeColumn = new TableColumn<>("Größe [MB]");
        final TableColumn<MediaData, Boolean> externalColumn = new TableColumn<>("extern");

        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        pathColumn.setCellValueFactory(new PropertyValueFactory<>("path"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        externalColumn.setCellValueFactory(new PropertyValueFactory<>("external"));
        externalColumn.setCellFactory(new CheckBoxCell().cellFactoryBool);

        tableMedia.getColumns().addAll(nameColumn, pathColumn, sizeColumn, externalColumn);

        nameColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(50.0 / 100));
        pathColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(25.0 / 100));
        sizeColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(15.0 / 100));
        externalColumn.prefWidthProperty().bind(tableMedia.widthProperty().multiply(10.0 / 100));

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
        progData.mediaList.filteredListSetPredicate(media -> {
            if (searchStr.isEmpty()) {
                return false;
            }
            final Pattern p = Filter.makePattern(searchStr);
            if (p != null) {
                return filterMedia(media, p);
            } else {
                return filterMedia(media, searchStr);
            }
        });
        lblTrefferMedia.setText(progData.mediaList.getFilteredList().size() + "");
    }

    private boolean filterMedia(MediaData media, Pattern p) {
        return p.matcher(media.getName()).matches();
    }

    private boolean filterMedia(MediaData media, String search) {
        return media.getName().toLowerCase().contains(search);
    }

}
