package de.p2tools.mtplayer.gui.filter.film;

import de.p2tools.mtplayer.controller.config.PEvents;
import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.compdata.CompData;
import de.p2tools.mtplayer.controller.data.compdata.CompList;
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
    private final ListView<CompData> listView = new ListView<>();
    private final CompList compList;
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

        compList = audio ? progData.compListAudio : progData.compListFilm;
        setText("Sortierung");
        setContent(vBoxAll);
        if (compList.isEmpty()) {
            compList.add(new CompData("Tatort", audio));
            compList.add(new CompData("Sport", audio));
        }
        init();
        initListView();
        initButton();
        initText();
    }

    public void close() {
        compList.removeIf(compData -> compData.getText().isEmpty());

        if (audio) {
            tglOn.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SORT_AUDIO_TABLE);
            if (sort) {
                ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_SORT_AUDIO);
            }

        } else {
            tglOn.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SORT_FILM_TABLE);
            if (sort) {
                ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_SORT_FILM);
            }
        }
    }

    private void init() {
        setClosable(false);
        vBoxAll.setPadding(new Insets(5));
        vBoxAll.setSpacing(P2LibConst.SPACING_VBOX);

        if (audio) {
            tglOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SORT_AUDIO_TABLE);
        } else {
            tglOn.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SORT_FILM_TABLE);
        }

        Button btnHelp = P2Button.helpButton(stage, "Vorgaben zum Sortieren",
                "");
        HBox hBoxBtn = new HBox(P2LibConst.SPACING_HBOX);
        hBoxBtn.getChildren().addAll(btnAdd, btnDel, btnUp, btnDown, P2GuiTools.getHBoxGrower(), btnSort);

        HBox hBoxText = new HBox(P2LibConst.SPACING_HBOX);
        HBox.setHgrow(txtText, Priority.ALWAYS);
        hBoxText.getChildren().addAll(txtText, btnHelp);
        vBoxAll.getChildren().addAll(tglOn, listView, hBoxBtn, hBoxText);
    }

    private void initListView() {
        listView.setItems(compList);
        listView.getSelectionModel().selectedItemProperty().addListener((u, o, n) -> {
            CompData compData = getSel();
            if (compData == null) {
                txtText.setText("");

            } else {
                txtText.setText(compData.getText());
            }
        });
    }

    private void initText() {
        txtText.textProperty().addListener((u, o, n) -> {
            CompData compData = getSel();
            if (compData != null) {
                compData.setText(txtText.getText());
                listView.refresh();
                sort = true;
            }
        });
        txtText.disableProperty().bind(listView.getSelectionModel().selectedItemProperty().isNull());
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
            CompData compData = new CompData("", audio);
            compList.add(compData);
            listView.getSelectionModel().select(compData);
        });
        btnDel.setOnAction(a -> {
            CompData compData = getSel();
            if (compData != null) {
                compList.remove(compData);
            }
        });
        btnUp.setOnAction(a -> {
            int sel = getSelectedLine();
            if (sel >= 0) {
                int newSel = compList.up(sel, true);
                listView.getSelectionModel().select(newSel);
            }
        });
        btnDown.setOnAction(a -> {
            int sel = getSelectedLine();
            if (sel >= 0) {
                int newSel = compList.up(sel, false);
                listView.getSelectionModel().select(newSel);
            }
        });

        btnSort.setOnAction(a -> {
                    ProgData.getInstance().pEventHandler.notifyListener(PEvents.EVENT_SORT_FILM);
                    sort = false;
                }
        );
    }

    private int getSelectedLine() {
        final int sel = listView.getSelectionModel().getSelectedIndex();
        if (sel < 0) {
            P2Alert.showInfoNoSelection(stage);
        }
        return sel;
    }

    private CompData getSel() {
        CompData compData = listView.getSelectionModel().getSelectedItem();
        if (compData == null) {
            P2Alert.showInfoNoSelection(stage);
        } else {
            return compData;
        }
        return null;
    }
}
