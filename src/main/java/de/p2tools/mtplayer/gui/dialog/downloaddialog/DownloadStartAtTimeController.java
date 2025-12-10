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

package de.p2tools.mtplayer.gui.dialog.downloaddialog;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.data.download.DownloadData;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.dialogs.dialog.P2DialogExtra;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2TimePicker;
import de.p2tools.p2lib.guitools.grid.P2GridConstraints;
import de.p2tools.p2lib.tools.GermanStringIntSorter;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.util.Comparator;
import java.util.List;

public class DownloadStartAtTimeController extends P2DialogExtra {

    private boolean ok = false;
    private Button btnOk = new Button("_Ok");
    private Button btnCancel = new Button("_Abbrechen");

    private final ToggleGroup toggleGroup = new ToggleGroup();
    private final P2TimePicker p2TimePicker = new P2TimePicker(true);
    private final CheckBox chkStartNow = new CheckBox();
    private final RadioButton rbAll = new RadioButton();
    private final RadioButton rbOnlySel = new RadioButton();

    private final GridPane gridPane = new GridPane();
    private final TableView<DownloadData> tableView = new TableView<>();
    private final SplitPane splitPane = new SplitPane();
    private final VBox vBoxTable = new VBox(10);
    private final VBox vBoxStart = new VBox(10);

    private final ObservableList<DownloadData> downloadListAll = FXCollections.observableArrayList();
    private final ObservableList<DownloadData> downloadListSel = FXCollections.observableArrayList();

    private final ProgData progData;
    private final boolean onlyAll;


    public DownloadStartAtTimeController(ProgData progData, List<DownloadData> dListAll,
                                         List<DownloadData> dListSel) {
        super(progData.primaryStage, ProgConfig.DOWNLOAD_DIALOG_START_AT_TIME_SIZE,
                "Downloads starten", true, true, true, DECO.NO_BORDER);

        this.progData = progData;
        //nur noch nicht gestartete in die Listen laden
        dListAll.stream().filter(DownloadData::isNotStartedOrFinished).forEach(downloadListAll::add);
        dListSel.stream().filter(DownloadData::isNotStartedOrFinished).forEach(downloadListSel::add);
        onlyAll = this.downloadListSel.isEmpty();
        init(true);
    }

    @Override
    public void make() {
        addOkCancelButtons(btnOk, btnCancel);
        Button btnHelp = P2Button.helpButton(getStage(), "Downloads starten",
                HelpText.DOWNLOAD_ADD_AT_TIME);
        addHlpButton(btnHelp);

        initGui();
        initRadio();
        initTable();
        initButton();
        initGridPane();
    }

    public boolean isOk() {
        return ok;
    }

    private void initGui() {
        p2TimePicker.disableProperty().bind(chkStartNow.selectedProperty());

        //linke Seite
        vBoxTable.setPadding(new Insets(10));
        SplitPane.setResizableWithParent(vBoxTable, true);
        if (onlyAll) {
            vBoxTable.getChildren().addAll(tableView);

        } else {
            VBox vBox = new VBox(5);
            vBox.getChildren().addAll(rbOnlySel, rbAll);
            vBoxTable.getChildren().addAll(tableView, vBox);
        }
        VBox.setVgrow(tableView, Priority.ALWAYS);

        //rechte Seite
        vBoxStart.setPadding(new Insets(10));
        vBoxStart.setMaxWidth(Region.USE_PREF_SIZE);
        gridPane.setMaxWidth(Region.USE_PREF_SIZE);
        vBoxStart.getChildren().add(gridPane);

        splitPane.getItems().addAll(vBoxTable, vBoxStart);
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        getVBoxCont().getChildren().add(splitPane);
    }

    private void initRadio() {
        rbAll.setToggleGroup(toggleGroup);
        rbOnlySel.setToggleGroup(toggleGroup);
        if (onlyAll) {
            rbAll.setSelected(true);
        } else {
            rbOnlySel.setSelected(true);
        }
        rbAll.selectedProperty().addListener((u, o, n) -> fillTable());
        rbOnlySel.selectedProperty().addListener((u, o, n) -> fillTable());

        rbAll.setText("Alle Downloads starten");
        rbOnlySel.setText("Nur ausgewählte Downloads starten");
    }

    private void initTable() {
        final Comparator<String> sorter = GermanStringIntSorter.getInstance();
        tableView.setTableMenuButtonVisible(true);
        tableView.setEditable(false);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        final TableColumn<DownloadData, Integer> startColumn = new TableColumn<>("");
        startColumn.setCellValueFactory(new PropertyValueFactory<>("guiState"));
        startColumn.setCellFactory(cellFactoryState);
        startColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, String> senderColumn = new TableColumn<>("Sender");
        senderColumn.setCellValueFactory(new PropertyValueFactory<>("channel"));
        senderColumn.getStyleClass().add("alignCenter");

        final TableColumn<DownloadData, String> themeColumn = new TableColumn<>("Thema");
        themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
        themeColumn.getStyleClass().add("alignCenterLeft");
        themeColumn.setComparator(sorter);

        final TableColumn<DownloadData, String> titleColumn = new TableColumn<>("Titel");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.getStyleClass().add("alignCenterLeft");
        titleColumn.setComparator(sorter);

        tableView.getColumns().addAll(startColumn, senderColumn, themeColumn, titleColumn);

        fillTable();
    }

    private void fillTable() {
        tableView.getItems().clear();
        if (rbAll.isSelected()) {
            tableView.getItems().addAll(downloadListAll);

        } else {
            tableView.getItems().addAll(downloadListSel);
        }
    }

    private void initButton() {
        checkBtnOk();
        rbAll.selectedProperty().addListener((u, o, n) -> checkBtnOk());
        rbOnlySel.selectedProperty().addListener((u, o, n) -> checkBtnOk());
        downloadListAll.addListener((ListChangeListener<DownloadData>) c -> checkBtnOk());
        downloadListSel.addListener((ListChangeListener<DownloadData>) c -> checkBtnOk());

        btnOk.setOnAction(event -> {
            ok = true;
            setStartTimeAndStart();
            close();
        });
        btnCancel.setOnAction(event -> {
            ok = false;
            close();
        });
    }

    private void checkBtnOk() {
        boolean ret;
        if (rbAll.isSelected()) {
            ret = !downloadListAll.isEmpty();
        } else {
            ret = !downloadListSel.isEmpty();
        }

        btnOk.setDisable(!ret);
    }

    private void setStartTimeAndStart() {
        ObservableList<DownloadData> downloadList;
        if (rbAll.isSelected()) {
            downloadList = downloadListAll;
        } else {
            downloadList = downloadListSel;
        }
        if (downloadList == null || downloadList.isEmpty()) {
            //dann gibts nix zu tun
            return;
        }

        if (chkStartNow.isSelected()) {
            //dann sofort starten und evtl. Startzeit löschen
            downloadList.forEach(download -> download.setStartTime(""));
        } else {
            //noch nicht gestartete nach Zeitangabe starten
            //Downloads dessen Start schon auf Fehler steht, werden nicht gestartet
            final String time = p2TimePicker.getTime();
            downloadList.forEach(download -> download.setStartTimeAlsoTomorrow(time));
        }
        downloadList.forEach(DownloadData::resetDownload);
        progData.downloadList.startDownloads(downloadList, false);
    }

    private void initGridPane() {
        gridPane.getColumnConstraints().addAll(P2GridConstraints.getCcPrefSize(),
                P2GridConstraints.getCcPrefSize());

        gridPane.setHgap(10);
        gridPane.setVgap(10);

        int row = 0;
        gridPane.add(new Label("Sofort starten:"), 0, row);
        gridPane.add(chkStartNow, 1, row);
        gridPane.add(new Label("Startzeit:"), 0, ++row);
        gridPane.add(p2TimePicker, 1, row);
    }

    private Callback<TableColumn<DownloadData, Integer>, TableCell<DownloadData, Integer>> cellFactoryState
            = (final TableColumn<DownloadData, Integer> param) -> {

        final TableCell<DownloadData, Integer> cell = new TableCell<DownloadData, Integer>() {

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                final HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.setAlignment(Pos.CENTER);
                hbox.setPadding(new Insets(0, 2, 0, 2));

                final Button btnDownDel;
                btnDownDel = new Button("");
                btnDownDel.setTooltip(new Tooltip("Download löschen"));
                btnDownDel.setGraphic(ProgIcons.IMAGE_TABLE_DOWNLOAD_DEL.getImageView());
                btnDownDel.setOnAction(event -> {
                    DownloadData download = getTableView().getItems().get(getIndex());
                    if (rbAll.isSelected()) {
                        downloadListAll.remove(download);
                    } else {
                        downloadListSel.remove(download);
                    }
                    fillTable(); // todo sollte es nicht brauchen??
                });

                hbox.getChildren().addAll(btnDownDel);
                setGraphic(hbox);
            }
        };
        return cell;
    };
}
