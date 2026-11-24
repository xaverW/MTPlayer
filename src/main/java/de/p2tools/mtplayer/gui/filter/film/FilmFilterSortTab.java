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
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import de.p2tools.p2lib.mediathek.filter.FilterCheckRegEx;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FilmFilterSortTab extends Tab {
    private final boolean audio;
    private final ObjectProperty<Stage> stage;

    private final SplitPane splitPane = new SplitPane();
    private final VBox vBoxSort = new VBox();
    private final VBox vBoxComp = new VBox();
    private final ListView<SortData> sortDataListView = new ListView<>();
    private final ListView<CompData> compDataListView = new ListView<>();
    private final SortList sortList;

    private final ProgData progData;
    private final VBox vBoxAll = new VBox(P2GuiTools.getVBoxGrower());
    private final Button btnAddSort = new Button();
    private final Button btnDelSort = new Button();
    private final Button btnAddComp = new Button();
    private final Button btnDelComp = new Button();
    private final Button btnUpComp = new Button();
    private final Button btnDownComp = new Button();
    private final Button btnSort = new Button("Jetzt sortieren");
    private final TextField txtTextSort = new TextField();
    private final TextField txtTextComp = new TextField();
    private final P2ToggleSwitch tglOn = new P2ToggleSwitch("Sortierung einschalten");
    private boolean sort = false;

    public FilmFilterSortTab(ObjectProperty<Stage> stage, boolean audio) {
        this.stage = stage;
        this.audio = audio;
        this.progData = ProgData.getInstance();
        sortList = audio ? progData.sortListAudio : progData.sortListFilm;

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
                """
                        Hier kann man in der Tabelle die Spalte "Titel" blockweise \
                        sortieren, zB. zuerst alle Filme die "Tatort" im Namen \
                        haben, dann alle Filme die "Sport" im Namen haben und dann die restlichen Filme. \
                        Jeder Block wird dabei alphabetisch sortiert in der Tabelle angezeigt.
                        
                        Die Blöcke werden in der Reihenfolge angeordnet, wie sie in der Liste aufgelistet \
                        sind. Mit dem Button "Jetzt sortieren" kann die Sortierung sofort angestoßen werden, ansonsten erst beim \
                        Beenden des Dialogs.
                        
                        Bei der Sortierung können auch RegEx benutzt werden. Diese müssen mit "#:" \
                        eingeleitet werden (zB. "#:.*\\(S\\d\\d\\/E.*" sucht nach Serientitel die zB. "(S01/E07)" \
                        im Titel haben).
                        
                        Bitte beachten: Je aufwendiger die Sortierung wird, desto länger dauert sie!
                        """);

        Label lblSort = P2Text.getLblTextBold("Name der Sortierung");
        Label lblComp = P2Text.getLblTextBold("Suchbegriffe");

        splitPane.getItems().addAll(vBoxSort, vBoxComp);
        splitPane.getItems().get(0).autosize();
        splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.SYSTEM_SORT_DIALOG_DIVIDER);
        SplitPane.setResizableWithParent(vBoxAll, Boolean.FALSE);

        HBox hBoxBtnSort = new HBox(P2LibConst.SPACING_HBOX);
        hBoxBtnSort.getChildren().addAll(btnAddSort, btnDelSort);
        vBoxSort.setSpacing(P2LibConst.SPACING_VBOX);
        vBoxSort.setPadding(new Insets(P2LibConst.PADDING_VBOX));
        vBoxSort.getChildren().addAll(lblSort, sortDataListView, hBoxBtnSort, txtTextSort);

        HBox hBoxBtnComp = new HBox(P2LibConst.SPACING_HBOX);
        hBoxBtnComp.getChildren().addAll(btnAddComp, btnDelComp, btnUpComp, btnDownComp, P2GuiTools.getHBoxGrower(), btnSort, btnHelp);
        vBoxComp.setSpacing(P2LibConst.SPACING_VBOX);
        vBoxComp.setPadding(new Insets(P2LibConst.PADDING_VBOX));
        vBoxComp.getChildren().addAll(lblComp, compDataListView, hBoxBtnComp, txtTextComp);

        vBoxAll.setPadding(new Insets(5));
        vBoxAll.setSpacing(P2LibConst.SPACING_VBOX);
        vBoxAll.getChildren().addAll(tglOn, splitPane);
    }

    private void initListView() {
        sortDataListView.setItems(sortList);
        sortDataListView.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            SortData sortData = sortDataListView.getSelectionModel().getSelectedItem();
            if (sortData == null) {
                compDataListView.getItems().clear();
                txtTextSort.setText("");

            } else {
                compDataListView.setItems(sortData.getCompList());
                compDataListView.getSelectionModel().selectFirst();
                txtTextSort.setText(sortData.getName());
            }
        });
        compDataListView.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            CompData compData = compDataListView.getSelectionModel().getSelectedItem();
            if (compData == null) {
                txtTextComp.setText("");

            } else {
                txtTextComp.setText(compData.getText());
            }
        });
        sortDataListView.getSelectionModel().selectFirst();
    }

    private void initText() {
        txtTextSort.textProperty().addListener((u, o, n) -> {
            SortData sortData = sortDataListView.getSelectionModel().getSelectedItem();
            if (sortData != null) {
                sortData.setName(txtTextSort.getText());
                sortDataListView.refresh();
                sort = true;
            } else {
                txtTextSort.setText("");
            }
        });
        txtTextSort.disableProperty().bind(sortDataListView.getSelectionModel().selectedItemProperty().isNull());

        txtTextComp.textProperty().addListener((u, o, n) -> {
            CompData compData = compDataListView.getSelectionModel().getSelectedItem();
            if (compData != null) {
                compData.setText(txtTextComp.getText());
                compDataListView.refresh();
                sort = true;
            }
        });
        txtTextSort.disableProperty().bind(sortDataListView.getSelectionModel().selectedItemProperty().isNull());
        txtTextComp.disableProperty().bind(compDataListView.getSelectionModel().selectedItemProperty().isNull());
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
            sortDataListView.getSelectionModel().select(sortData);
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
                compDataListView.getSelectionModel().select(compData);
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
                    compDataListView.getSelectionModel().select(newSel);
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

    private int getSelectedLineComp() {
        final int sel = compDataListView.getSelectionModel().getSelectedIndex();
        if (sel < 0) {
            P2Alert.showInfoNoSelection(stage.get());
        }
        return sel;
    }

    private SortData getSelSort() {
        SortData sortData = sortDataListView.getSelectionModel().getSelectedItem();
        if (sortData == null) {
            P2Alert.showInfoNoSelection(stage.get());
        } else {
            return sortData;
        }
        return null;
    }

    private CompData getSelComp() {
        CompData compData = compDataListView.getSelectionModel().getSelectedItem();
        if (compData == null) {
            P2Alert.showInfoNoSelection(stage.get());
        } else {
            return compData;
        }
        return null;
    }
}
