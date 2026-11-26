package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2Text;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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
                        ein- und ausgeschaltet werden.
                        """);

        this.setPadding(new Insets(5));
        this.setSpacing(P2LibConst.SPACING_VBOX);
        this.getChildren().addAll(P2Text.getLblTextBold("Sortierung einschalten"),
                tglFilm, tglAudio, tglLive, tglDownload, tglAbo,
                P2GuiTools.getVBoxGrower(), btnHelp);
    }
}
