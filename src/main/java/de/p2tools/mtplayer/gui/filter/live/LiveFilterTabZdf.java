package de.p2tools.mtplayer.gui.filter.live;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.livesearch.JsonInfoDto;
import de.p2tools.mtplayer.controller.livesearch.LiveSearchZdf;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveConst;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.gui.filter.helper.PCboStringSearch;
import de.p2tools.p2lib.P2LibConst;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LiveFilterTabZdf extends Tab {

    private final ProgData progData;
    private final JsonInfoDto jsonInfoDto = new JsonInfoDto();
    private final ProgressBar progress = new ProgressBar();
    private final VBox vBoxTab = new VBox();

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

        Button btnClear = new Button();
        btnClear.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClear.setTooltip(new Tooltip("Suche löschen"));
        btnClear.setOnAction(a -> {
            ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF.set("");
        });

        final PCboStringSearch cboSearch;
        cboSearch = new PCboStringSearch(progData, ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF);

        Button btnSearchZdf = new Button();
        btnSearchZdf.setGraphic(ProgIcons.ICON_BUTTON_SEARCH_16.getImageView());
        btnSearchZdf.setTooltip(new Tooltip("Suche starten"));
        btnSearchZdf.setOnAction(a -> searchZdf(false));
        btnSearchZdf.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF.length().lessThan(LiveConst.MIN_SEARCH_LENGTH))
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        Button btnKeepOnZdf = new Button();
        btnKeepOnZdf.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnKeepOnZdf.setTooltip(new Tooltip("Weitersuchen"));
        btnKeepOnZdf.setOnAction(a -> searchZdf(true));
        btnKeepOnZdf.disableProperty().bind((jsonInfoDto.nextUrlProperty().isEmpty())
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        VBox vBox = new VBox();
        vBox.setSpacing(2);

        HBox hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearch, btnClear);
        HBox.setHgrow(cboSearch, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("Livesuche"), hBox);

        hBox = new HBox();
        hBox.setPadding(new Insets(2, 0, 0, 0));
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnKeepOnZdf, btnSearchZdf);
        vBox.getChildren().add(hBox);

        vBoxTab.getChildren().add(vBox);

        // Search URL
        Button btnClearUrl = new Button();
        btnClearUrl.setGraphic(ProgIcons.ICON_BUTTON_CLEAR.getImageView());
        btnClearUrl.setTooltip(new Tooltip("Suche löschen"));
        btnClearUrl.setOnAction(a -> {
            ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ZDF.set("");
        });

        Button btnSearchUrlZdf = new Button();
        btnSearchUrlZdf.setGraphic(ProgIcons.ICON_BUTTON_SEARCH_16.getImageView());
        btnSearchUrlZdf.setTooltip(new Tooltip("Suche starten"));
        btnSearchUrlZdf.setOnAction(a -> searchUrl());
        btnSearchUrlZdf.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_ZDF.length().lessThan(5))
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        final PCboStringSearch cboSearchUrl;
        cboSearchUrl = new PCboStringSearch(progData, ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ZDF);

        vBox = new VBox(2);

        hBox = new HBox();
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.getChildren().addAll(cboSearchUrl, btnClearUrl);
        HBox.setHgrow(cboSearchUrl, Priority.ALWAYS);
        vBox.getChildren().addAll(new Label("URL Filmseite"), hBox);

        hBox = new HBox();
        hBox.setPadding(new Insets(2, 0, 0, 0));
        hBox.setSpacing(P2LibConst.SPACING_HBOX);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnSearchUrlZdf);
        vBox.getChildren().add(hBox);

        vBoxTab.getChildren().add(vBox);

        this.setContent(vBoxTab);
    }

    private void searchZdf(boolean next) {
        new Thread(() -> new LiveSearchZdf().loadLive(jsonInfoDto, next)).start();
    }

    private void searchUrl() {
        new Thread(() -> new LiveSearchZdf().loadUrl(jsonInfoDto)).start();
    }

    private void addProgress() {
        progress.progressProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF));
        progress.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ZDF).greaterThan(-1));
        progress.setMaxWidth(Double.MAX_VALUE);
        vBoxTab.getChildren().addAll(/*P2GuiTools.getVBoxGrower(),*/ progress);
        VBox.setVgrow(progress, Priority.ALWAYS);
    }
}
