package de.p2tools.mtplayer.gui.filter.live;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.config.ProgIcons;
import de.p2tools.mtplayer.controller.livesearch.JsonInfoDto;
import de.p2tools.mtplayer.controller.livesearch.LiveSearchArd;
import de.p2tools.mtplayer.controller.livesearch.tools.LiveFactory;
import de.p2tools.mtplayer.gui.filter.helper.PCboStringSearch;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.alert.PAlert;
import de.p2tools.p2lib.guitools.P2GuiTools;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LiveFilterTabArd extends Tab {

    private final ProgData progData;
    private final JsonInfoDto jsonInfoDto = new JsonInfoDto();
    private IntegerProperty siteNo = new SimpleIntegerProperty(0);
    private final ProgressBar progress = new ProgressBar();
    private final VBox vBoxTab = new VBox();

    public LiveFilterTabArd() {
        super("ARD");
        this.progData = ProgData.getInstance();
        setClosable(false);
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
        btnSearchArd.setGraphic(ProgIcons.ICON_BUTTON_SEARCH_16.getImageView());
        btnSearchArd.setTooltip(new Tooltip("Suche starten"));
        btnSearchArd.setOnAction(a -> {
            siteNo.set(0);
            searchArd(0);
        });
        btnSearchArd.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.length().lessThan(5))
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        Button btnKeepOnArd = new Button();
        btnKeepOnArd.setGraphic(ProgIcons.ICON_BUTTON_FORWARD.getImageView());
        btnKeepOnArd.setTooltip(new Tooltip("Weitersuchen"));
        btnKeepOnArd.setOnAction(a -> {
            long res = jsonInfoDto.getSizeOverAll().get() - (long) siteNo.get() * JsonInfoDto.PAGE_SIZE - JsonInfoDto.PAGE_SIZE;
            if (res > 0) {
                siteNo.setValue(siteNo.get() + 1);
                searchArd(siteNo.get());
            }
        });
        btnKeepOnArd.disableProperty().bind((jsonInfoDto.getSizeOverAll().lessThanOrEqualTo(siteNo.get() * JsonInfoDto.PAGE_SIZE))
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        final PCboStringSearch cboSearch;
        cboSearch = new PCboStringSearch(progData, ProgConfig.LIVE_FILM_GUI_SEARCH_ARD);

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
        btnSearchUrlArd.setGraphic(ProgIcons.ICON_BUTTON_SEARCH_16.getImageView());
        btnSearchUrlArd.setTooltip(new Tooltip("Suche starten"));
        btnSearchUrlArd.setOnAction(a -> {
            searchUrl();
        });
        btnSearchUrlArd.disableProperty().bind((ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.length().lessThan(5))
                .or(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).isNotEqualTo(LiveFactory.PROGRESS_NULL)));

        final PCboStringSearch cboSearchUrl;
        cboSearchUrl = new PCboStringSearch(progData, ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARD);

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
        hBox.getChildren().addAll(btnSearchUrlArd);
        vBox.getChildren().add(hBox);

        vBoxTab.getChildren().add(vBox);

        this.setContent(vBoxTab);
    }

    private void searchArd(int page) {
        new Thread(() -> {
            LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).setValue(LiveFactory.PROGRESS_NULL);
            jsonInfoDto.init();
            jsonInfoDto.setPageNo(page);
            jsonInfoDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_ARD.getValue());
            jsonInfoDto.setPageNo(siteNo.get());

            new LiveSearchArd().loadLive(jsonInfoDto);
            Platform.runLater(() -> {
                jsonInfoDto.getList().forEach(ProgData.getInstance().liveFilmFilterWorker.getLiveFilmList()::importFilmOnlyWithNr);
            });

        }).start();
    }

    private void searchUrl() {
        new Thread(() -> {
            LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).setValue(LiveFactory.PROGRESS_NULL);
            final JsonInfoDto jsonInfoDto = new JsonInfoDto();
            jsonInfoDto.init();
            jsonInfoDto.setPageNo(0);
            jsonInfoDto.setSearchString(ProgConfig.LIVE_FILM_GUI_SEARCH_URL_ARD.getValue());

            new LiveSearchArd().loadUrl(jsonInfoDto);
            Platform.runLater(() -> {
                if (jsonInfoDto.getList().isEmpty()) {
                    // dann hats nicht geklappt
                    PAlert.showErrorAlert("Film suchen", "Der gesuchte Film konnte nicht gefunden werden.");
                } else {
                    jsonInfoDto.getList().forEach(ProgData.getInstance().liveFilmFilterWorker.getLiveFilmList()::importFilmOnlyWithNr);
                }
            });
        }).start();

    }


    private void addProgress() {
        progress.progressProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD));
        progress.visibleProperty().bind(LiveFactory.getProgressProperty(LiveFactory.CHANNEL.ARD).greaterThan(-1));
        progress.setMaxWidth(Double.MAX_VALUE);
        vBoxTab.getChildren().addAll(P2GuiTools.getVBoxGrower(), progress);
        VBox.setVgrow(progress, Priority.ALWAYS);
    }

}
