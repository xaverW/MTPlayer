package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.compdata.CompData;
import de.p2tools.mtplayer.controller.data.compdata.SortData;
import de.p2tools.mtplayer.controller.data.compdata.SortList;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.P2Alert;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import de.p2tools.p2lib.mediathek.filter.FilterCheckRegEx;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FilterSortPane extends VBox {
    private final ObjectProperty<Stage> stage;

    private final SplitPane splitPane = new SplitPane();
    private final VBox vBoxSort = new VBox();
    private final VBox vBoxComp = new VBox();
    private final ListView<SortData> listViewSort = new ListView<>();
    private final ListView<CompData> listViewComp = new ListView<>();
    private final SortList sortList;

    private final ProgData progData;
    private final Button btnAddSort = new Button();
    private final Button btnDelSort = new Button();
    private final Button btnAddComp = new Button();
    private final Button btnDelComp = new Button();
    private final Button btnUpComp = new Button();
    private final Button btnDownComp = new Button();
    private final Button btnSort = new Button("Jetzt sortieren");

    private final TextField txtTextSort = new TextField();
    private final TextField txtTextComp = new TextField();

    private boolean sort = false;

    public FilterSortPane(ObjectProperty<Stage> stage) {
        this.stage = stage;
        this.progData = ProgData.getInstance();
        this.sortList = progData.sortList;

        init();
        initListView();
        initButton();
        initText();
    }

    public void close() {
        sortList.forEach(sortData -> {
            boolean found = sortData.getCompList().removeIf(compData -> compData.getText().isEmpty());
            if (found) sort = true;
        });
        boolean found = sortList.removeIf(sortData -> sortData.getCompList().isEmpty());
        if (found) sort = true;

        if (sort) {
            ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_BTN_RUN_SORT);
        }
    }

    private void init() {
        Button btnHelp = P2Button.helpButton(stage, "Vorgaben zum Sortieren",
                """
                        Wird in den Tabellen die Spalte "Titel" sortiert, ist das jetzt
                        damit blockweise möglich, zB. zuerst alle Filme mit "Tatort"
                        im Namen, dann alle Filme mit "Sport" im Namen und dann der Rest.
                        In den Blöcken werden die Filme auch alphabetisch sortiert.
                        So können zB. lange Downloadlisten Thematisch sortiert werden.
                        
                        Die Blöcke werden in der Reihenfolge angeordnet, wie sie in der \
                        Liste mit den Suchbegriffen aufgelistet \
                        sind. Mit dem Button "Jetzt sortieren" kann die Sortierung \
                        zum Testen sofort angestoßen werden, ansonsten erst \
                        beim Beenden des Dialogs.
                        
                        Bei der Sortierung können auch RegEx benutzt werden. Diese müssen mit "#:" \
                        eingeleitet werden.
                        (ZB. sucht "#:.*\\(S\\d\\d\\/E.*" nach Serientitel die zB. "(S01/E07)" \
                        im Titel enthalten.)
                        
                        Bitte beachten: Je aufwendiger die Sortierung wird, desto länger dauert sie!
                        """);

        Label lblSort = P2Text.getLblTextBold("Name der Sortierung");
        Label lblComp = P2Text.getLblTextBold("Suchbegriffe");

        splitPane.getItems().addAll(vBoxSort, vBoxComp);
        splitPane.getItems().get(0).autosize();
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.SYSTEM_SORT_DIALOG_DIVIDER);
        SplitPane.setResizableWithParent(this, Boolean.FALSE);

        HBox hBoxBtnSort = new HBox(P2LibConst.SPACING_HBOX);
        hBoxBtnSort.getChildren().addAll(btnAddSort, btnDelSort);
        vBoxSort.setSpacing(P2LibConst.SPACING_VBOX);
        vBoxSort.setPadding(new Insets(P2LibConst.PADDING_VBOX));
        vBoxSort.getChildren().addAll(lblSort, listViewSort, hBoxBtnSort, txtTextSort);

        HBox hBoxBtnComp = new HBox(P2LibConst.SPACING_HBOX);
        hBoxBtnComp.getChildren().addAll(btnAddComp, btnDelComp, btnUpComp, btnDownComp,
                P2GuiTools.getHBoxGrower(), btnSort, btnHelp);
        vBoxComp.setSpacing(P2LibConst.SPACING_VBOX);
        vBoxComp.setPadding(new Insets(P2LibConst.PADDING_VBOX));
        vBoxComp.getChildren().addAll(lblComp, listViewComp, hBoxBtnComp, txtTextComp);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.getStyleClass().add("extra-pane");
        Label lblText = P2Text.getLblTextBold("Suchbegriffe für die Sortierung");
        hBox.getChildren().add(lblText);

        this.setPadding(new Insets(10, 5, 5, 5));
        this.setSpacing(P2LibConst.SPACING_VBOX);
        this.getChildren().addAll(hBox, splitPane);
    }

    private void initListView() {
        listViewSort.setItems(sortList);
        listViewSort.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            SortData sortData = listViewSort.getSelectionModel().getSelectedItem();
            if (sortData == null) {
                listViewComp.getItems().clear();
                txtTextSort.setText("");

            } else {
                listViewComp.setItems(sortData.getCompList());
                listViewComp.getSelectionModel().selectFirst();
                txtTextSort.setText(sortData.getName());
            }
        });
        listViewComp.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            CompData compData = listViewComp.getSelectionModel().getSelectedItem();
            if (compData == null) {
                txtTextComp.setText("");

            } else {
                txtTextComp.setText(compData.getText());
            }
        });
        listViewSort.getSelectionModel().selectFirst();
    }

    private void initText() {
        txtTextSort.textProperty().addListener((u, o, n) -> {
            SortData sortData = listViewSort.getSelectionModel().getSelectedItem();
            if (sortData != null) {
                if (!sortData.getName().equals(txtTextSort.getText())) {
                    sortData.setName(txtTextSort.getText());
                    listViewSort.refresh();
                    sort = true;
                }
            } else {
                txtTextSort.setText("");
            }
        });
        txtTextSort.disableProperty().bind(listViewSort.getSelectionModel().selectedItemProperty().isNull());

        txtTextComp.textProperty().addListener((u, o, n) -> {
            CompData compData = listViewComp.getSelectionModel().getSelectedItem();
            if (compData != null) {
                if (!compData.getText().equals(txtTextComp.getText())) {
                    compData.setText(txtTextComp.getText());
                    listViewComp.refresh();
                    sort = true;
                }
            }
        });
        txtTextSort.disableProperty().bind(listViewSort.getSelectionModel().selectedItemProperty().isNull());
        txtTextComp.disableProperty().bind(listViewComp.getSelectionModel().selectedItemProperty().isNull());
        new FilterCheckRegEx(txtTextComp);
    }

    private void initButton() {
        btnAddSort.setGraphic(P2IconFactory.P2ICON.BTN_PLUS.getFontIcon());
        btnAddSort.setTooltip(new Tooltip("Einen neue Eintrag hinzufügen"));
        btnDelSort.setGraphic(P2IconFactory.P2ICON.BTN_MINUS.getFontIcon());
        btnDelSort.setTooltip(new Tooltip("Einen Eintrag löschen"));

        btnAddComp.setGraphic(P2IconFactory.P2ICON.BTN_PLUS.getFontIcon());
        btnAddComp.setTooltip(new Tooltip("Einen neue Eintrag hinzufügen"));
        btnDelComp.setGraphic(P2IconFactory.P2ICON.BTN_MINUS.getFontIcon());
        btnDelComp.setTooltip(new Tooltip("Einen Eintrag löschen"));
        btnUpComp.setGraphic(P2IconFactory.P2ICON.BTN_UP.getFontIcon());
        btnUpComp.setTooltip(new Tooltip("Einen Eintrag nach oben schieben"));
        btnDownComp.setGraphic(P2IconFactory.P2ICON.BTN_DOWN.getFontIcon());
        btnDownComp.setTooltip(new Tooltip("Einen Eintrag nach unten schieben"));

        btnAddSort.setOnAction(a -> {
            SortData sortData = new SortData("Neu");
            sortList.add(sortData);
            listViewSort.getSelectionModel().select(sortData);
            sort = true;
        });
        btnDelSort.setOnAction(a -> {
            SortData sortData = getSelSort();
            if (sortData != null) {
                sortList.remove(sortData);
                sort = true;
            }
        });

        btnAddComp.setOnAction(a -> {
            SortData sortData = getSelSort();
            if (sortData != null) {
                CompData compData = new CompData("Sortieren");
                sortData.getCompList().add(compData);
                listViewComp.getSelectionModel().select(compData);
                sort = true;
            }
        });
        btnDelComp.setOnAction(a -> {
            SortData sortData = getSelSort();
            if (sortData != null) {
                CompData compData = getSelComp();
                if (compData != null) {
                    sortData.getCompList().remove(compData);
                }
                sort = true;
            }
        });
        btnUpComp.setOnAction(a -> {
            SortData sortData = getSelSort();
            if (sortData != null) {
                int sel = getSelectedLineComp();
                if (sel >= 0) {
                    int newSel = sortData.getCompList().up(sel, true);
                    listViewComp.getSelectionModel().select(newSel);
                }
                sort = true;
            }
        });
        btnDownComp.setOnAction(a -> {
            SortData sortData = getSelSort();
            if (sortData != null) {
                int sel = getSelectedLineComp();
                if (sel >= 0) {
                    int newSel = sortData.getCompList().up(sel, false);
                    listViewComp.getSelectionModel().select(newSel);
                }
                sort = true;
            }
        });

        btnSort.setOnAction(a -> {
                    startSort();
                }
        );
    }

    private void startSort() {
//        if (getSelSort() != null) {
//            systemSortIdx.set(listViewSort.getSelectionModel().getSelectedIndex() + 1);
//        } else {
//            systemSortIdx.set(0);
//        }

        ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_BTN_RUN_SORT);
        sort = false;
    }

    private int getSelectedLineComp() {
        final int sel = listViewComp.getSelectionModel().getSelectedIndex();
        if (sel < 0) {
            P2Alert.showInfoNoSelection(stage.get());
        }
        return sel;
    }

    private SortData getSelSort() {
        SortData sortData = listViewSort.getSelectionModel().getSelectedItem();
        if (sortData == null) {
            P2Alert.showInfoNoSelection(stage.get());
        } else {
            return sortData;
        }
        return null;
    }

    private CompData getSelComp() {
        CompData compData = listViewComp.getSelectionModel().getSelectedItem();
        if (compData == null) {
            P2Alert.showInfoNoSelection(stage.get());
        } else {
            return compData;
        }
        return null;
    }
}
