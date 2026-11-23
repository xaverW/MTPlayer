package de.p2tools.mtplayer.gui.filter.film;

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
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import de.p2tools.p2lib.mediathek.filter.FilterCheckRegEx;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FilmFilterSortTab extends Tab {
    private final boolean audio;
    private final Stage stage;

    private final SplitPane splitPane = new SplitPane();
    private final VBox vBoxSort = new VBox();
    private final VBox vBoxComp = new VBox();
    private final ListView<SortData> sortDataListView = new ListView<>();
    private final ListView<CompData> compDataListView = new ListView<>();
    private final SortList sortList;

    private final ProgData progData;
    private final VBox vBoxAll = new VBox(P2GuiTools.getVBoxGrower());
    private final Button btnAdd = new Button();
    private final Button btnDel = new Button();
    private final Button btnUp = new Button();
    private final Button btnDown = new Button();
    private final Button btnSort = new Button("Jetzt sortieren");
    private final TextField txtText = new TextField();
    private final P2ToggleSwitch tglOn = new P2ToggleSwitch("Sortierung einschalten");
    private boolean sort = false;

    public FilmFilterSortTab(Stage stage, boolean audio) {
        this.stage = stage;
        this.audio = audio;
        this.progData = ProgData.getInstance();
        sortList = audio ? progData.sortListAudio : progData.sortListFilm;

        if (sortList.isEmpty()) {
            SortData sortData = new SortData("Sortierung");
            sortData.getCompList().add(new CompData("Tatort", audio));
            sortData.getCompList().add(new CompData("Sport", audio));
            sortList.add(sortData);
        }

        init();
        initListView();
        initButton();
        initText();
    }

    public void close() {
        sortList.forEach(sortData -> sortData.getCompList().removeIf(compData -> compData.getText().isEmpty()));
        if (sort) {
            ProgData.getInstance().pEventHandler.notifyListener(audio ? PEvents.EVENT_SORT_AUDIO : PEvents.EVENT_SORT_FILM);
        }
        tglOn.selectedProperty().unbindBidirectional(audio ? ProgConfig.SYSTEM_SORT_AUDIO_TABLE : ProgConfig.SYSTEM_SORT_FILM_TABLE);
    }

    private void init() {
        setText("Sortierung");
        setContent(vBoxAll);
        setClosable(false);

        tglOn.selectedProperty().bindBidirectional(audio ? ProgConfig.SYSTEM_SORT_AUDIO_TABLE : ProgConfig.SYSTEM_SORT_FILM_TABLE);

        Button btnHelp = P2Button.helpButton(stage, "Vorgaben zum Sortieren",
                "");
        HBox hBoxBtn = new HBox(P2LibConst.SPACING_HBOX);
        hBoxBtn.getChildren().addAll(btnAdd, btnDel, btnUp, btnDown, P2GuiTools.getHBoxGrower(), btnSort);

        HBox hBoxText = new HBox(P2LibConst.SPACING_HBOX);
        HBox.setHgrow(txtText, Priority.ALWAYS);
        hBoxText.getChildren().addAll(txtText, btnHelp);

        splitPane.getItems().addAll(vBoxSort, vBoxComp);
        splitPane.getItems().get(0).autosize();
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.SYSTEM_SORT_DIALOG_DIVIDER);
        SplitPane.setResizableWithParent(vBoxAll, Boolean.FALSE);

        vBoxSort.getChildren().addAll(sortDataListView);
        vBoxComp.getChildren().addAll(compDataListView, hBoxBtn, hBoxText);

        vBoxAll.setPadding(new Insets(5));
        vBoxAll.setSpacing(P2LibConst.SPACING_VBOX);
        vBoxAll.getChildren().addAll(tglOn, splitPane);
    }

    private void initListView() {
        sortDataListView.setItems(sortList);
        sortDataListView.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            SortData sortData = getSelSort();
            if (sortData != null) {
                compDataListView.setItems(sortData.getCompList());
            }
        });
        compDataListView.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            CompData compData = getSelComp();
            if (compData == null) {
                txtText.setText("");

            } else {
                txtText.setText(compData.getText());
            }
        });
    }

    private void initText() {
        txtText.textProperty().addListener((u, o, n) -> {
            CompData compData = getSelComp();
            if (compData != null) {
                compData.setText(txtText.getText());
                compDataListView.refresh();
                sort = true;
            }
        });
        txtText.disableProperty().bind(compDataListView.getSelectionModel().selectedItemProperty().isNull());
        new FilterCheckRegEx(txtText);
    }

    private void initButton() {
        btnAdd.setGraphic(P2IconFactory.P2ICON.BTN_PLUS.getFontIcon());
        btnAdd.setTooltip(new Tooltip("Einen neue Eintrag hinzufügen"));
        btnDel.setGraphic(P2IconFactory.P2ICON.BTN_MINUS.getFontIcon());
        btnDel.setTooltip(new Tooltip("Einen Eintrag löschen"));
        btnUp.setGraphic(P2IconFactory.P2ICON.BTN_UP.getFontIcon());
        btnUp.setTooltip(new Tooltip("Einen Eintrag nach oben schieben"));
        btnDown.setGraphic(P2IconFactory.P2ICON.BTN_DOWN.getFontIcon());
        btnDown.setTooltip(new Tooltip("Einen Eintrag nach unten schieben"));

        btnAdd.setOnAction(a -> {
            SortData sortData = getSelSort();
            if (sortData != null) {
                CompData compData = new CompData("", audio);
                sortData.getCompList().add(compData);
                compDataListView.getSelectionModel().select(compData);
                sort = true;
            }
        });
        btnDel.setOnAction(a -> {
            SortData sortData = getSelSort();
            if (sortData != null) {
                CompData compData = getSelComp();
                if (compData != null) {
                    sortData.getCompList().remove(compData);
                }
                sort = true;
            }
        });
        btnUp.setOnAction(a -> {
            SortData sortData = getSelSort();
            if (sortData != null) {
                int sel = getSelectedLine();
                if (sel >= 0) {
                    int newSel = sortData.getCompList().up(sel, true);
                    compDataListView.getSelectionModel().select(newSel);
                }
                sort = true;
            }
        });
        btnDown.setOnAction(a -> {
            SortData sortData = getSelSort();
            if (sortData != null) {
                int sel = getSelectedLine();
                if (sel >= 0) {
                    int newSel = sortData.getCompList().up(sel, false);
                    compDataListView.getSelectionModel().select(newSel);
                }
                sort = true;
            }
        });

        btnSort.setOnAction(a -> {
                    ProgData.getInstance().pEventHandler.notifyListener(audio ? PEvents.EVENT_SORT_AUDIO : PEvents.EVENT_SORT_FILM);
                    sort = false;
                }
        );
    }

    private int getSelectedLine() {
        final int sel = compDataListView.getSelectionModel().getSelectedIndex();
        if (sel < 0) {
            P2Alert.showInfoNoSelection(stage);
        }
        return sel;
    }

    private SortData getSelSort() {
        SortData sortData = sortDataListView.getSelectionModel().getSelectedItem();
        if (sortData == null) {
            P2Alert.showInfoNoSelection(stage);
        } else {
            return sortData;
        }
        return null;
    }

    private CompData getSelComp() {
        CompData compData = compDataListView.getSelectionModel().getSelectedItem();
        if (compData == null) {
            P2Alert.showInfoNoSelection(stage);
        } else {
            return compData;
        }
        return null;
    }
}
