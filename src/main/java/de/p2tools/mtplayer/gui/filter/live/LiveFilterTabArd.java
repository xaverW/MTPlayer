package de.p2tools.mtplayer.gui.filter.live;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.livesearch.LiveSearchArd;
import de.p2tools.mtplayer.controller.livesearch.ard.ArdDto;
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

public class LiveFilterTabArd extends Tab {

    private final ProgData progData;
    private final ArdDto ardDto = new ArdDto();
    private final VBox vBoxTab = new VBox();
    private final Label lblCount = new Label();

    public LiveFilterTabArd() {
        super("ARD");
        this.progData = ProgData.getInstance();
        setClosable(false);

        ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.addListener((u, o, n) -> ardDto.init()); // damit "weiter" nicht mehr geht
        addTabArd();
        addProgress();
    }

    private void addTabArd() {
        vBoxTab.setSpacing(P2LibConst.SPACING_VBOX);
        vBoxTab.setPadding(new Insets(P2LibConst.PADDING));
        vBoxTab.setAlignment(Pos.TOP_CENTER);

        Button btnSearchArd = new Button();
        btnSearchArd.setGraphic(P2IconFactory.P2ICON.BTN_SEARCH.getFontIcon(20));
        btnSearchArd.setTooltip(new Tooltip("Suche starten"));
        btnSearchArd.setOnAction(a -> searchArd(false));
        btnSearchArd.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.length().lessThan(LiveConst.MIN_SEARCH_LENGTH))
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        final PCboString cboSearch;
        cboSearch = new PCboString(progData.stringFilterLists.getFilterListLiveArd(),
                ProgConfig.LIVE_FILM_GUI_SEARCH_ARD);

        VBox vBox = new VBox();
        vBox.setSpacing(2);

        HBox hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearch, btnSearchArd);
        HBox.setHgrow(cboSearch, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Livesuche ARD"), hBox);
        vBoxTab.getChildren().add(vBox);

        Button btnSearchUrlArd = new Button();
        btnSearchUrlArd.setGraphic(P2IconFactory.P2ICON.BTN_SEARCH.getFontIcon(20));
        btnSearchUrlArd.setTooltip(new Tooltip("Suche starten"));
        btnSearchUrlArd.setOnAction(a -> searchUrl());
        btnSearchUrlArd.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARD.isEmpty())
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        final PCboString cboSearchUrl;
        cboSearchUrl = new PCboString(progData.stringFilterLists.getFilterListLiveArdUrl(),
                ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARD);

        vBox = new VBox(2);

        hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearchUrl, btnSearchUrlArd);
        HBox.setHgrow(cboSearchUrl, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("URL ARD-Filmseite"), hBox);
        vBoxTab.getChildren().add(vBox);

        this.setContent(vBoxTab);
    }

    private void searchArd(boolean next) {
        new Thread(() -> new LiveSearchArd().loadLive(ardDto)).start();
    }

    private void searchUrl() {
        new Thread(() -> new LiveSearchArd().loadUrl(ardDto)).start();
    }

    private void addProgress() {
        final ProgressBar progress = new ProgressBar();
        final Button btnStop = new Button();
        final Button btnClear = new Button();

        progress.setMaxHeight(Double.MAX_VALUE);
        btnClear.setGraphic(P2IconFactory.P2ICON.P2_BTN_CLEAR_FILTER.getFontIcon());
        btnClear.setTooltip(new Tooltip("Suche löschen"));
        btnClear.setOnAction(a -> {
            ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.set("");
            ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARD.set("");
        });

        btnStop.setGraphic(P2IconFactory.P2ICON.BTN_CLEAR.getFontIcon());
        btnStop.setOnAction(a -> ardDto.setStop(true));
        progress.progressProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD));
        progress.setMaxWidth(Double.MAX_VALUE);

        LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).addListener((u, o, n) -> {
            final String text = LiveFactory.getActProperty(LiveFactory.CHANNEL.ARD).get() + " / " +
                    LiveFactory.getMaxProperty(LiveFactory.CHANNEL.ARD).get();
            lblCount.setText(text);
        });

        lblCount.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).greaterThan(-1).and(
                LiveFactory.getMaxProperty(LiveFactory.CHANNEL.ARD).greaterThan(0)));
        lblCount.managedProperty().bind(lblCount.visibleProperty());
        progress.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).greaterThan(-1));
        btnStop.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).greaterThan(-1));
        btnClear.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).greaterThan(-1).not());
        btnClear.managedProperty().bind(btnClear.visibleProperty());

        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(lblCount, progress, btnStop, btnClear);
        HBox.setHgrow(progress, Priority.ALWAYS);
        vBoxTab.getChildren().addAll(P2GuiTools.getVBoxGrower(), hBox);
    }
}
