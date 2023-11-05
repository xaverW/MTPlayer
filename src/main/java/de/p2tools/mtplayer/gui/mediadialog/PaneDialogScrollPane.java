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

import de.p2tools.mtplayer.controller.config.PListener;
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.history.HistoryData;
import de.p2tools.mtplayer.controller.mediadb.MediaData;
import de.p2tools.mtplayer.gui.mediaSearch.MediaDataDto;
import de.p2tools.mtplayer.gui.mediaSearch.MediaSearchFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ColumnConstraints;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.mtfilter.FilterCheckRegEx;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Collection;

public class PaneDialogScrollPane extends ScrollPane {

    Text textSearch = new Text();
    Button btnCreateMediaDB = new Button("_Mediensammlung neu aufbauen");
    Button btnPlay = new Button();
    Button btnOpen = new Button();

    final Button btnClearList = new Button("_Liste löschen");
    TextField txtSearch = new TextField();

    ProgressBar progress = new ProgressBar();
    private final Button btnStopSearching = new Button();

    Label lblGesamtMedia = new Label();
    Label lblHits = new Label();
    TableView<MediaData> tableMedia = new TableView<>();
    TableView<HistoryData> tableAboOrHistory = new TableView<>();

    TextField txtTitleMedia = new TextField();
    TextField txtPathMedia = new TextField();

    private final PListener listenerDbStart;
    private final PListener listenerDbStop;

    private final boolean mediaDataExist;
    private final ProgData progData = ProgData.getInstance();

    ChangeListener sizeListener;
    ListChangeListener<HistoryData> listener;
    final MediaDataDto mediaDataDto;

    public PaneDialogScrollPane(MediaDataDto mediaDataDto) {
        // für Medien und Abo/History
        // in den Einstellungen -> Mediensammlung
        // im MediaDialog Medien/Abo/History
        // im Infobereich und den Tabellen Filme/Download, Medien/Abo
        this.mediaDataDto = mediaDataDto;
        this.mediaDataExist = !mediaDataDto.searchTheme.isEmpty() || !mediaDataDto.searchTitle.isEmpty();

        listenerDbStart = new PListener(PListener.EVENT_MEDIA_DB_START, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB suchen
                txtSearch.setDisable(true);
            }
        };
        listenerDbStop = new PListener(PListener.EVENT_MEDIA_DB_STOP, MediaDialogController.class.getSimpleName()) {
            @Override
            public void pingFx() {
                // neue DB liegt vor
                txtSearch.setDisable(false);
                filter();
            }
        };
    }

    public void make(Collection<TitledPane> result) {
        VBox vBox = new VBox(P2LibConst.DIST_VBOX);
        vBox.setPadding(new Insets(P2LibConst.DIST_EDGE));
        TitledPane tpConfig = new TitledPane("Medien", vBox);
        result.add(tpConfig);
        vBox.getChildren().add(this);
        VBox.setVgrow(this, Priority.ALWAYS);
        make();
    }

    public void make() {
        initPanel();
        initTable();
        initAction();
        filter();
    }

    public void close() {
        PListener.removeListener(listenerDbStart);
        PListener.removeListener(listenerDbStop);
    }

    private void initPanel() {
        txtTitleMedia.setEditable(false);
        txtPathMedia.setEditable(false);

        VBox vBoxMedia = new VBox(P2LibConst.DIST_EDGE);

        if (mediaDataDto.whatToShow == MediaDataDto.SHOW_WHAT.SHOW_MEDIA) {
            tableMedia.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
            VBox.setVgrow(tableMedia, Priority.ALWAYS);
            HBox hBox = new HBox(P2LibConst.DIST_HBOX);
            hBox.getChildren().addAll(getHBoxProgress(), P2GuiTools.getHBoxGrower(), getHBoxSum());
            vBoxMedia.getChildren().addAll(getVBoxSearch(), tableMedia, hBox, getTextFieldGrid());

        } else {
            tableAboOrHistory.setMinHeight(ProgConst.MIN_TABLE_HEIGHT);
            VBox.setVgrow(tableAboOrHistory, Priority.ALWAYS);
            HBox hBox = new HBox(P2LibConst.DIST_HBOX);
            hBox.getChildren().addAll(btnClearList, P2GuiTools.getHBoxGrower(), getHBoxSum());
            vBoxMedia.getChildren().addAll(getVBoxSearch(), tableAboOrHistory, hBox, getTextFieldGrid());
        }

        this.setPadding(new Insets(P2LibConst.DIST_EDGE));
        this.setFitToHeight(true);
        this.setFitToWidth(true);
        this.setContent(vBoxMedia);
    }

    private VBox getVBoxSearch() {
        // Suchen was
        final Button btnReset = new Button("");
        btnReset.setGraphic(ProgIcons.ICON_BUTTON_RESET.getImageView());
        btnReset.setTooltip(new Tooltip("Suchtext wieder herstellen"));
        btnReset.setOnAction(a -> txtSearch.setText(mediaDataDto.searchTheme + " " + mediaDataDto.searchTitle));

        final Button btnClear = new Button();
        btnClear.setGraphic(ProgIcons.ICON_BUTTON_STOP.getImageView());
        btnClear.setTooltip(new Tooltip("Das Suchfeld löschen"));
        btnClear.setOnAction(a -> txtSearch.clear());

        VBox vBoxSearch = MediaSearchFactory.getSearchVbox(mediaDataDto, null, false);
        mediaDataDto.searchInWhat.addListener((u, o, n) -> filter());

        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        HBox hBoxSearch = new HBox(P2LibConst.DIST_HBOX);
        hBoxSearch.setPadding(new Insets(0));
        hBoxSearch.setAlignment(Pos.CENTER_RIGHT);
        if (mediaDataExist) {
            hBoxSearch.getChildren().addAll(new Label("Suchen: "), txtSearch, btnReset, btnClear);
        } else {
            // wenns keine MediaData gibt, dann brauchts den Reset auch nicht
            hBoxSearch.getChildren().addAll(new Label("Suchen: "), txtSearch, btnClear);
        }
        vBoxSearch.getChildren().addAll(hBoxSearch);
        return vBoxSearch;
    }

    private GridPane getTextFieldGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(P2LibConst.DIST_GRIDPANE_HGAP);
        gridPane.setVgap(P2LibConst.DIST_GRIDPANE_VGAP);

        if (mediaDataDto.whatToShow == MediaDataDto.SHOW_WHAT.SHOW_MEDIA) {
            // Titel
            gridPane.add(new Label("Titel:"), 0, 0);
            gridPane.add(txtTitleMedia, 1, 0);
            gridPane.add(btnPlay, 2, 0);

            // Pfad/URL
            gridPane.add(new Label("Pfad:"), 0, 1);
            gridPane.add(txtPathMedia, 1, 1);
            gridPane.add(btnOpen, 2, 1);

        } else {
            // Titel
            gridPane.add(new Label("Titel:"), 0, 0);
            gridPane.add(txtTitleMedia, 1, 0);

            // Pfad/URL
            gridPane.add(new Label("Url:"), 0, 1);
            gridPane.add(txtPathMedia, 1, 1);
        }

        gridPane.getColumnConstraints().addAll(P2ColumnConstraints.getCcPrefSize(),
                P2ColumnConstraints.getCcComputedSizeAndHgrow());
        return gridPane;
    }

    private HBox getHBoxSum() {
        HBox hBoxSum = new HBox(P2LibConst.DIST_BUTTON);
        hBoxSum.getChildren().addAll(lblHits, new Label(" von: "), lblGesamtMedia);
        return hBoxSum;
    }

    private HBox getHBoxProgress() {
        HBox hBoxProgress = new HBox(P2LibConst.DIST_HBOX);
        hBoxProgress.setPadding(new Insets(0));
        hBoxProgress.setAlignment(Pos.CENTER_RIGHT);

        progress.setVisible(false);
        progress.setMaxHeight(Double.MAX_VALUE);
        progress.setMaxWidth(Double.MAX_VALUE);

        btnStopSearching.setGraphic(ProgIcons.ICON_BUTTON_STOP.getImageView());
        btnStopSearching.setOnAction(event -> progData.mediaDataList.setStopSearching(true));
        btnStopSearching.visibleProperty().bind(progData.mediaDataList.searchingProperty());
        hBoxProgress.getChildren().addAll(btnCreateMediaDB, progress, btnStopSearching);
        HBox.setHgrow(progress, Priority.ALWAYS);
        return hBoxProgress;
    }

    void initTable() {
    }

    void initAction() {
        FilterCheckRegEx filterCheckRegEx = new FilterCheckRegEx(txtSearch);
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterCheckRegEx.checkPattern();
            filter();
            mediaDataDto.searchStringProp.setValue(txtSearch.getText());
        });
        txtSearch.setOnMouseClicked(event -> {
            if (event.getClickCount() > 1) {
                String sel = txtSearch.getSelectedText();
                txtSearch.setText(sel);
            }
        });

        PListener.addListener(listenerDbStart);
        PListener.addListener(listenerDbStop);
    }

    void filter(String searStr) {
        txtSearch.setText(searStr);
        filter();
    }

    public void filter() {
    }
}
