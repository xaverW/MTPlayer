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
import de.mtplayer.mtp.controller.data.ProgIcons;
import de.mtplayer.mtp.controller.mediaDb.MediaData;
import de.mtplayer.mtp.controller.mediaDb.MediaDataWorker;
import de.mtplayer.mtp.controller.mediaDb.MediaFileSize;
import de.mtplayer.mtp.gui.mediaConfig.SearchPredicateWorker;
import de.mtplayer.mtp.gui.tools.Listener;
import de.mtplayer.mtp.tools.storedFilter.FilterCheckRegEx;
import de.p2tools.p2Lib.guiTools.PColumnConstraints;
import de.p2tools.p2Lib.guiTools.PGuiTools;
import de.p2tools.p2Lib.guiTools.POpen;
import de.p2tools.p2Lib.tools.file.PFileUtils;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PaneMedia extends ScrollPane {

    private Button btnCreateMediaDB = new Button("_Mediensammlung neu aufbauen");
    private Button btnPlay = new Button();
    private Button btnOpen = new Button();

    private final TextField txtSearch = new TextField();
    private final Button btnReset = new Button("");
    private ProgressBar progress = new ProgressBar();

    private Label lblGesamtMedia = new Label();
    private Label lblTrefferMedia = new Label();
    private TableView<MediaData> tableMedia = new TableView();
    private TextField txtTitleMedia = new TextField();
    private TextField txtPathMedia = new TextField();
    private final Listener listenerDbStart;
    private final Listener listenerDbStop;
    private ProgData progData = ProgData.getInstance();

    private final String searchStrOrg;
    private StringProperty searchStrProp;

    private ChangeListener sizeListener;

    public PaneMedia(Stage stage, String searchStrOrg, StringProperty searchStrProp) {
        this.searchStrOrg = searchStrOrg;
        this.searchStrProp = searchStrProp;

        listenerDbStart = new Listener(Listener.EREIGNIS_MEDIA_DB_START, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB suchen
                txtSearch.setDisable(true);
            }
        };
        listenerDbStop = new Listener(Listener.EREIGNIS_MEDIA_DB_STOP, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB liegt vor
                txtSearch.setDisable(false);
                filter();
            }
        };
    }

    public void make() {
        initPanel();
        initTableMedia();
        initAction();
        filter();
    }

    public void close() {
        Listener.removeListener(listenerDbStart);
        Listener.removeListener(listenerDbStop);
        Listener.removeListener(listenerDbStop);
        progData.mediaDataList.sizeProperty().removeListener(sizeListener);
        progress.visibleProperty().unbind();
        btnCreateMediaDB.disableProperty().unbind();
    }

    private void initPanel() {
        HBox hBoxSearch = new HBox(10);
        hBoxSearch.setPadding(new Insets(10));
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        hBoxSearch.getChildren().addAll(new Label("Suchen: "), txtSearch, btnReset);
        hBoxSearch.getStyleClass().add("extra-pane");

        btnReset.setGraphic(new ProgIcons().ICON_BUTTON_RESET);
        btnReset.setTooltip(new Tooltip("Suchtext wieder herstellen"));

        HBox hBoxSum = new HBox(10);
        hBoxSum.setPadding(new Insets(10));
        hBoxSum.getChildren().addAll(new Label("Treffer:"), lblTrefferMedia,
                PGuiTools.getHBoxGrower(), new Label("Anzahl Medien gesamt:"), lblGesamtMedia);

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        txtTitleMedia.setEditable(false);
        txtPathMedia.setEditable(false);

        gridPane.add(new Label("Titel:"), 0, 0);
        gridPane.add(txtTitleMedia, 1, 0);
        gridPane.add(btnPlay, 2, 0);
        gridPane.add(new Label("Pfad:"), 0, 1);
        gridPane.add(txtPathMedia, 1, 1);
        gridPane.add(btnOpen, 2, 1);

        gridPane.getColumnConstraints().addAll(PColumnConstraints.getCcPrefSize(),
                PColumnConstraints.getCcComputedSizeAndHgrow());

        HBox hBoxProgess = new HBox();
        hBoxProgess.setSpacing(10);
        hBoxProgess.setPadding(new Insets(10));
        progress.setVisible(false);
        progress.setMaxHeight(Double.MAX_VALUE);
        progress.setMaxWidth(Double.MAX_VALUE);
        hBoxProgess.getChildren().addAll(btnCreateMediaDB, progress);
        HBox.setHgrow(progress, Priority.ALWAYS);

        tableMedia.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
        VBox.setVgrow(tableMedia, Priority.ALWAYS);

        VBox vBoxMedia = new VBox();
        vBoxMedia.getChildren().addAll(hBoxSearch, PGuiTools.getHDistance(10), tableMedia, hBoxSum, gridPane, hBoxProgess);

        this.setContent(vBoxMedia);
    }

    private void initTableMedia() {
        final TableColumn<MediaData, String> nameColumn = new TableColumn<>("Name");
        final TableColumn<MediaData, String> pathColumn = new TableColumn<>("Pfad");
        final TableColumn<MediaData, MediaFileSize> sizeColumn = new TableColumn<>("Größe [MB]");
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

        SortedList<MediaData> sortedList = progData.mediaDataList.getSortedList();
        sortedList.comparatorProperty().bind(tableMedia.comparatorProperty());
        tableMedia.setItems(sortedList);
    }

    private void initAction() {
        FilterCheckRegEx fTT = new FilterCheckRegEx(txtSearch);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            fTT.checkPattern();
            filter();
            searchStrProp.setValue(txtSearch.getText());
        });
        txtSearch.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearch.getSelectedText();
                txtSearch.setText(sel);
            }
        });
        btnReset.setOnAction(a -> txtSearch.setText(searchStrOrg));

        Listener.addListener(listenerDbStart);
        Listener.addListener(listenerDbStop);

        lblGesamtMedia.setText(progData.mediaDataList.size() + "");
        sizeListener = (observable, oldValue, newValue) -> {
            Platform.runLater(() -> lblGesamtMedia.setText(progData.mediaDataList.size() + ""));
        };
        progData.mediaDataList.sizeProperty().addListener(sizeListener);

        progress.visibleProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.disableProperty().bind(progData.mediaDataList.searchingProperty());
        btnCreateMediaDB.setOnAction(e -> MediaDataWorker.createMediaDb());

        btnOpen.setGraphic(new ProgIcons().ICON_BUTTON_FILE_OPEN);
        btnOpen.setTooltip(new Tooltip("Ausgewählten Pfad im Dateimanager öffnen"));
        btnOpen.setOnAction(e -> open());
        btnOpen.disableProperty().bind(txtPathMedia.textProperty().isEmpty().and(txtTitleMedia.textProperty().isEmpty()));

        btnPlay.setGraphic(new ProgIcons().ICON_BUTTON_PLAY);
        btnPlay.setTooltip(new Tooltip("Ausgewählten Film abspielen"));
        btnPlay.setOnAction(e -> play());
        btnPlay.disableProperty().bind(txtPathMedia.textProperty().isEmpty().and(txtTitleMedia.textProperty().isEmpty()));
    }

    public void filter(String searStr) {
        txtSearch.setText(searStr);
        filter();
    }


    private void filter() {
        progData.mediaDataList.filteredListSetPredicate(SearchPredicateWorker.getPredicateMediaData(txtSearch.getText(), false));
//        progData.mediaDataList.filteredListSetPredicate(SearchPredicateWorker.getPredicateMediaData_(txtSearch.getText(), false));
        lblTrefferMedia.setText(progData.mediaDataList.getFilteredList().size() + "");
    }

    private void play() {
        final String path = txtPathMedia.getText();
        final String name = txtTitleMedia.getText();
        if (!name.isEmpty() && !path.isEmpty()) {
            POpen.playStoredFilm(PFileUtils.addsPath(path, name),
                    ProgConfig.SYSTEM_PROG_PLAY_FILME.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
        }
    }

    private void open() {
        final String s = txtPathMedia.getText();
        POpen.openDir(s, ProgConfig.SYSTEM_PROG_OPEN_DIR.getStringProperty(), new ProgIcons().ICON_BUTTON_FILE_OPEN);
    }
}
