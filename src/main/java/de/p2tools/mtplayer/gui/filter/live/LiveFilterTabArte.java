package de.p2tools.mtplayer.gui.filter.live;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.livesearch.LiveSearchArte;
import de.p2tools.mtplayer.controller.livesearch.arte.ArteDto;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.gui.filter.helper.PCboString;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.ikonli.P2IconFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LiveFilterTabArte extends Tab {

    private final ProgData progData;
    private final ArteDto arteDto = new ArteDto();
    private final ProgressBar progress = new ProgressBar();
    private final VBox vBoxTab = new VBox();

    public LiveFilterTabArte() {
        super("ARTE");
        this.progData = ProgData.getInstance();
        this.setClosable(false);
        this.setContent(vBoxTab);

        ProgConfig.LIVE_FILM_GUI_SEARCH_ARTE.addListener((u, o, n) -> arteDto.init()); // damit "weiter" nicht mehr geht
        addTabArte();
        addProgress();
    }

    private void addTabArte() {
        vBoxTab.setSpacing(P2LibConst.SPACING_VBOX);
        vBoxTab.setPadding(new Insets(P2LibConst.PADDING));
        vBoxTab.setAlignment(Pos.TOP_CENTER);

        Button btnSearchArte = new Button();
        btnSearchArte.setGraphic(P2IconFactory.P2ICON.BTN_SEARCH.getFontIcon(20));
        btnSearchArte.setTooltip(new Tooltip("Suche starten"));
        btnSearchArte.setOnAction(a -> searchArte());
        btnSearchArte.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_ARTE.length().lessThan(LiveConst.MIN_SEARCH_LENGTH))
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARTE).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        final PCboString cboSearch;
        cboSearch = new PCboString(progData.stringFilterLists.getFilterListLiveArte(),
                ProgConfig.LIVE_FILM_GUI_SEARCH_ARTE);
        VBox vBox = new VBox();
        vBox.setSpacing(2);
        HBox hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearch, btnSearchArte);
        HBox.setHgrow(cboSearch, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Livesuche ARTE"), hBox);
        vBoxTab.getChildren().add(vBox);

        Button btnSearchUrl = new Button();
        btnSearchUrl.setGraphic(P2IconFactory.P2ICON.BTN_SEARCH.getFontIcon(20));
        btnSearchUrl.setTooltip(new Tooltip("Suche starten"));
        btnSearchUrl.setOnAction(a -> searchUrl());
        btnSearchUrl.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARTE.isEmpty())
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARTE).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        final PCboString cboSearchUrl;
        cboSearchUrl = new PCboString(progData.stringFilterLists.getFilterListLiveArteUrl(),
                ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARTE);
        vBox = new VBox(2);
        hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearchUrl, btnSearchUrl);
        HBox.setHgrow(cboSearchUrl, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("URL ARTE-Filmseite"), hBox);
        vBoxTab.getChildren().add(vBox);

        Button btnClear = new Button();
        btnClear.setGraphic(P2IconFactory.P2ICON.P2_BTN_CLEAR_FILTER.getFontIcon());
        btnClear.setTooltip(new Tooltip("Suche löschen"));
        btnClear.setOnAction(a -> {
            ProgConfig.LIVE_FILM_GUI_SEARCH_ARTE.set("");
            ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARTE.set("");
        });
        HBox hBoxClear = new HBox();
        hBoxClear.getChildren().add(btnClear);
        hBoxClear.setAlignment(Pos.CENTER_RIGHT);
        vBoxTab.getChildren().add(hBoxClear);
    }

    private void searchArte() {
        new Thread(() -> new LiveSearchArte().loadLive(arteDto)).start();
    }

    private void searchUrl() {
        new Thread(() -> new LiveSearchArte().loadUrl(arteDto)).start();
    }

    private void addProgress() {
        progress.progressProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARTE));
        progress.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARTE).greaterThan(-1));
        progress.setMaxWidth(Double.MAX_VALUE);
        vBoxTab.getChildren().addAll(P2GuiTools.getVBoxGrower(), progress);
//        VBox.setVgrow(progress, Priority.ALWAYS);
    }
}
