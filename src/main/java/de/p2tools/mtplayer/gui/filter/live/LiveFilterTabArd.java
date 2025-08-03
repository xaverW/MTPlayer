package de.p2tools.mtplayer.gui.filter.live;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.livesearch.JsonInfoDto;
import de.p2tools.mtplayer.controller.livesearch.LiveSearchArd;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.gui.filter.helper.PCboString;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LiveFilterTabArd extends Tab {

    private final ProgData progData;
    private final JsonInfoDto jsonInfoDto = new JsonInfoDto();
    private final ProgressBar progress = new ProgressBar();
    private final VBox vBoxTab = new VBox();

    public LiveFilterTabArd() {
        super("ARD");
        this.progData = ProgData.getInstance();
        setClosable(false);

        ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.addListener((u, o, n) -> jsonInfoDto.init()); // damit "weiter" nicht mehr geht
        addTabArd();
        addProgress();
    }

    private void addTabArd() {
        vBoxTab.setSpacing(P2LibConst.SPACING_VBOX);
        vBoxTab.setPadding(new Insets(P2LibConst.PADDING));
        vBoxTab.setAlignment(Pos.TOP_CENTER);

        Button btnClear = new Button();
        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClear.setTooltip(new Tooltip("Suche löschen"));
        btnClear.setOnAction(a -> {
            ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.set("");
        });

        Button btnSearchArd = new Button();
        btnSearchArd.setGraphic(ProgIcons.ICON_BUTTON_SEARCH.getImageView());
        btnSearchArd.setTooltip(new Tooltip("Suche starten"));
        btnSearchArd.setOnAction(a -> searchArd(false));
        btnSearchArd.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.length().lessThan(LiveConst.MIN_SEARCH_LENGTH))
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        Button btnKeepOnArd = new Button("Weitersuchen");
        btnKeepOnArd.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnKeepOnArd.setTooltip(new Tooltip("Weitersuchen"));
        btnKeepOnArd.setOnAction(a -> searchArd(true));
        btnKeepOnArd.disableProperty().bind((jsonInfoDto.nextUrlProperty().isEmpty())
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        final PCboString cboSearch;
        cboSearch = new PCboString(progData.filmFilterStringLists.getFilterListArdLive(),
                ProgConfig.LIVE_FILM_GUI_SEARCH_ARD);

        VBox vBox = new VBox();
        vBox.setSpacing(2);

        HBox hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearch, btnClear);
        HBox.setHgrow(cboSearch, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Livesuche ARD"), hBox);

        hBox = new HBox();
        hBox.setPadding(new Insets(2, 0, 0, 0));
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnKeepOnArd, btnSearchArd);
        vBox.getChildren().add(hBox);

        vBoxTab.getChildren().add(vBox);

        // Search URL
        Button btnClearUrl = new Button();
        btnClearUrl.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClearUrl.setTooltip(new Tooltip("Suche löschen"));
        btnClearUrl.setOnAction(a -> {
            ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARD.set("");
        });

        Button btnSearchUrlArd = new Button();
        btnSearchUrlArd.setGraphic(ProgIcons.ICON_BUTTON_SEARCH.getImageView());
        btnSearchUrlArd.setTooltip(new Tooltip("Suche starten"));
        btnSearchUrlArd.setOnAction(a -> searchUrl());
        btnSearchUrlArd.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARD.isEmpty())
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        final PCboString cboSearchUrl;
        cboSearchUrl = new PCboString(progData.filmFilterStringLists.getFilterListArdUrl(),
                ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARD);

        vBox = new VBox(2);

        hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearchUrl, btnClearUrl);
        HBox.setHgrow(cboSearchUrl, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("URL ARD-Filmseite"), hBox);

        hBox = new HBox();
        hBox.setPadding(new Insets(2, 0, 0, 0));
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnSearchUrlArd);
        vBox.getChildren().add(hBox);

        vBoxTab.getChildren().add(vBox);

        this.setContent(vBoxTab);
    }

    private void searchArd(boolean next) {
        new Thread(() -> new LiveSearchArd().loadLive(jsonInfoDto, next)).start();
    }

    private void searchUrl() {
        new Thread(() -> new LiveSearchArd().loadUrl(jsonInfoDto)).start();
    }

    private void addProgress() {
        progress.progressProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD));
        progress.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).greaterThan(-1));
        progress.setMaxWidth(Double.MAX_VALUE);
        vBoxTab.getChildren().addAll(P2GuiTools.getVBoxGrower(), progress);
        VBox.setVgrow(progress, Priority.ALWAYS);
    }

}
