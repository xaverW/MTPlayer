package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FilterSwitchPane extends VBox {
    private final ObjectProperty<Stage> stage;

    private final P2ToggleSwitch tglFilm = new P2ToggleSwitch("Filme");
    private final P2ToggleSwitch tglAudio = new P2ToggleSwitch("Audios");
    private final P2ToggleSwitch tglLive = new P2ToggleSwitch("Live");
    private final P2ToggleSwitch tglDownload = new P2ToggleSwitch("Download");
    private final P2ToggleSwitch tglAbo = new P2ToggleSwitch("Abos");
    private final ProgData progData;

    public FilterSwitchPane(ObjectProperty<Stage> stage) {
        this.stage = stage;
        this.progData = ProgData.getInstance();
        init();
    }

    public void close() {
        tglFilm.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SORT_FILM_TABLE);
        tglAudio.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SORT_AUDIO_TABLE);
        tglLive.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SORT_LIVE_TABLE);
        tglDownload.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SORT_DOWNLOAD_TABLE);
        tglAbo.selectedProperty().unbindBidirectional(ProgConfig.SYSTEM_SORT_ABO_TABLE);
    }

    private void init() {
        tglFilm.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SORT_FILM_TABLE);
        tglAudio.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SORT_AUDIO_TABLE);
        tglLive.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SORT_LIVE_TABLE);
        tglDownload.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SORT_DOWNLOAD_TABLE);
        tglAbo.selectedProperty().bindBidirectional(ProgConfig.SYSTEM_SORT_ABO_TABLE);

        Button btnHelp = P2Button.helpButton(stage, "Sortierung einschalten",
                """
                        Hier kann die eigene Sortierung der "Titel-Spalte" \
                        in den Tabellen ein- und ausgeschaltet werden.
                        """);

        HBox hBoxHelp = new HBox();
        hBoxHelp.setAlignment(Pos.CENTER_RIGHT);
        hBoxHelp.getChildren().add(btnHelp);

        HBox hBoxTitle = new HBox();
        hBoxTitle.setPadding(new Insets(10));
        hBoxTitle.getStyleClass().add("extra-pane");
        Label lblText = P2Text.getLblTextBold("Sortierung einschalten");
        hBoxTitle.getChildren().add(lblText);

        this.setPadding(new Insets(10, 5, 5, 5));
        this.setSpacing(20);
        this.getChildren().addAll(hBoxTitle,
                tglFilm, tglAudio, tglLive, tglDownload, tglAbo,
                P2GuiTools.getVBoxGrower(), hBoxHelp);
    }
}
