package de.p2tools.mtplayer.gui.filter.live;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.livesearch.LiveSearchZdf;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.controller.livesearch.zdf.ZdfDto;
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

public class LiveFilterTabZdf extends Tab {

    private final ProgData progData;
    private final ZdfDto zdfDto = new ZdfDto();
    private final VBox vBoxTab = new VBox();
    private final Label lblCount = new Label();

    public LiveFilterTabZdf() {
        super("ZDF");
        this.progData = ProgData.getInstance();
        setClosable(false);

        addTabZdf();
        addProgress();
    }

    private void addTabZdf() {
        // Live-Suche
        vBoxTab.setSpacing(P2LibConst.SPACING_VBOX);
        vBoxTab.setPadding(new Insets(P2LibConst.PADDING));
        vBoxTab.setAlignment(Pos.TOP_CENTER);

        final PCboString cboSearch;
        cboSearch = new PCboString(progData.stringFilterLists.getFilterListLiveZdf(),
                ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF);

        Button btnSearch = new Button();
        btnSearch.setGraphic(P2IconFactory.P2ICON.BTN_SEARCH_S.getFontIcon());
        btnSearch.getStyleClass().add("buttonLow");
        btnSearch.setTooltip(new Tooltip("Suche starten"));
        btnSearch.setOnAction(a -> searchZdf());
        btnSearch.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF.length().lessThan(LiveConst.MIN_SEARCH_LENGTH))
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        VBox vBox = new VBox();
        vBox.setSpacing(2);

        HBox hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearch, btnSearch);
        HBox.setHgrow(cboSearch, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Livesuche ZDF"), hBox);
        vBoxTab.getChildren().add(vBox);

        Button btnSearchUrl = new Button();
        btnSearchUrl.setGraphic(P2IconFactory.P2ICON.BTN_SEARCH_S.getFontIcon());
        btnSearchUrl.getStyleClass().add("buttonLow");
        btnSearchUrl.setTooltip(new Tooltip("Suche starten"));
        btnSearchUrl.setOnAction(a -> searchUrl());
        btnSearchUrl.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ZDF.isEmpty())
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        final PCboString cboSearchUrl;
        cboSearchUrl = new PCboString(progData.stringFilterLists.getFilterListLiveZdfUrl(),
                ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ZDF);

        vBox = new VBox(2);

        hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearchUrl, btnSearchUrl);
        HBox.setHgrow(cboSearchUrl, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("URL ZDF-Filmseite"), hBox);

        vBoxTab.getChildren().add(vBox);
        this.setContent(vBoxTab);
    }

    private void searchZdf() {
        new Thread(() -> new LiveSearchZdf().loadLive(zdfDto)).start();
    }

    private void searchUrl() {
        new Thread(() -> new LiveSearchZdf().loadUrl(zdfDto)).start();
    }

    private void addProgress() {
        final ProgressBar progress = new ProgressBar();
        final Button btnStop = new Button();
        final Button btnClear = new Button();

        progress.setMaxHeight(Double.MAX_VALUE);
        btnClear.setGraphic(P2IconFactory.P2ICON.P2_BTN_CLEAR_FILTER_S.getFontIcon());
        btnClear.getStyleClass().add("buttonLow");
        btnClear.setTooltip(new Tooltip("Suche löschen"));
        btnClear.setOnAction(a -> {
            ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF.set("");
            ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ZDF.set("");
        });

        btnStop.setGraphic(P2IconFactory.P2ICON.BTN_CLEAR_S.getFontIcon());
        btnStop.getStyleClass().add("buttonLow");
        btnStop.setOnAction(a -> zdfDto.setStop(true));
        progress.progressProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF));
        progress.setMaxWidth(Double.MAX_VALUE);

        LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).addListener((u, o, n) -> {
            final String text = LiveFactory.getActProperty(LiveFactory.CHANNEL.ZDF).get() + " / " +
                    LiveFactory.getMaxProperty(LiveFactory.CHANNEL.ZDF).get();
            lblCount.setText(text);
        });

        lblCount.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).greaterThan(-1).and(
                LiveFactory.getMaxProperty(LiveFactory.CHANNEL.ZDF).greaterThan(0)));
        lblCount.managedProperty().bind(lblCount.visibleProperty());
        progress.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).greaterThan(-1));
        btnStop.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).greaterThan(-1));
        btnClear.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).greaterThan(-1).not());
        btnClear.managedProperty().bind(btnClear.visibleProperty());

        HBox hBox = new HBox(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(lblCount, progress, btnStop, btnClear);
        HBox.setHgrow(progress, Priority.ALWAYS);
        VBox vBox = new VBox();
        vBox.getChildren().addAll(P2GuiTools.getDistance(5), hBox);
        vBoxTab.getChildren().addAll(vBox);
    }
}
