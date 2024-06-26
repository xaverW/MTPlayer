/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */

package de.p2tools.mtplayer.gui.filter;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.download.DownloadConstants;
import de.p2tools.mtplayer.controller.data.download.DownloadInfosFactory;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.guitools.P2Button;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2MenuButton;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class DownloadFilterController extends FilterController {

    private final ComboBox<String> cboSrc = new ComboBox<>(); //Downloadquelle: Abo, manuell gestartet
    private final ComboBox<String> cboKind = new ComboBox<>(); //Download über Programm / direkter Downlaod, http
    private final P2MenuButton mbChannel;
    private final ComboBox<String> cboAbo = new ComboBox<>();
    private final ComboBox<String> cboState = new ComboBox<>();

    private final Spinner<Integer> spinnerAnz = new Spinner<>(1, 9, 1);
    private final Slider sliderBandwidth = new Slider();
    private final Button btnClear = P2ButtonClearFilterFactory.getPButtonClearFilter();
    private final Label lblBandwidth = new Label();

    private final VBox vBoxFilter;
    private final ProgData progData;

    public DownloadFilterController() {
        super(ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON);
        vBoxFilter = getVBoxFilter(true);
        progData = ProgData.getInstance();
        progData.downloadFilterController = this;

        mbChannel = new P2MenuButton(ProgConfig.FILTER_DOWNLOAD_CHANNEL,
                ThemeListFactory.allChannelList);

        initLayout();
        initFilter();

        initNumberDownloads();
        DownloadInfosFactory.initBandwidth(sliderBandwidth, lblBandwidth);
    }

    private void initLayout() {
        addCont("Quelle", cboSrc, vBoxFilter);
        addCont("Downloadart", cboKind, vBoxFilter);
        addCont("Sender", mbChannel, vBoxFilter);
        addCont("Abo", cboAbo, vBoxFilter);
        addCont("Status", cboState, vBoxFilter);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.getChildren().add(btnClear);
        hBox.setAlignment(Pos.TOP_RIGHT);
        VBox.setVgrow(hBox, Priority.ALWAYS);

        Separator sp = new Separator();
        sp.getStyleClass().add("pseperator3");
        sp.setMinHeight(0);
        vBoxFilter.getChildren().addAll(hBox, sp);

        VBox vb = new VBox(FilterController.FILTER_SPACING_TEXTFILTER);
        addCont("Gleichzeitige Downloads", spinnerAnz, vb);

        Label lblText = new Label("Max. Bandbreite: ");
        lblText.setMinWidth(0);
        lblText.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Download beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));
        sliderBandwidth.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Download beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));
        HBox hh = new HBox();
        HBox.setHgrow(hh, Priority.ALWAYS);
        HBox h = new HBox();
        h.getChildren().addAll(lblText, hh, lblBandwidth);
        addCont(h, sliderBandwidth, vb);

        final Button btnHelp = P2Button.helpButton("Filter", HelpText.GUI_DOWNLOAD_FILTER);
        hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().addAll(btnHelp);
        vb.getChildren().add(hBox);

        vBoxFilter.getChildren().add(vb);
    }

    private void initFilter() {
        btnClear.setOnAction(a -> clearFilter());

        cboSrc.getItems().addAll(DownloadConstants.ALL,
                DownloadConstants.SRC_COMBO_DOWNLOAD,
                DownloadConstants.SRC_COMBO_ABO);

        Bindings.bindBidirectional(cboSrc.valueProperty(), ProgConfig.FILTER_DOWNLOAD_SOURCE,
                new StringConverter<>() {

                    @Override
                    public String fromString(String cb) {
                        switch (cb) {
                            case DownloadConstants.ALL:
                                return DownloadConstants.ALL;
                            case DownloadConstants.SRC_COMBO_ABO:
                                return DownloadConstants.SRC_ABO;
                            case DownloadConstants.SRC_COMBO_DOWNLOAD:
                                return DownloadConstants.SRC_DOWNLOAD;
                            default:
                                return DownloadConstants.ALL;
                        }
                    }

                    @Override
                    public String toString(String prop) {
                        switch (prop) {
                            case DownloadConstants.ALL:
                                return DownloadConstants.ALL;
                            case DownloadConstants.SRC_ABO:
                                return DownloadConstants.SRC_COMBO_ABO;
                            case DownloadConstants.SRC_DOWNLOAD:
                                return DownloadConstants.SRC_COMBO_DOWNLOAD;
                            default:
                                return DownloadConstants.ALL;
                        }
                    }
                });

        cboKind.getItems().addAll(DownloadConstants.ALL,
                DownloadConstants.TYPE_COMBO_DOWNLOAD,
                DownloadConstants.TYPE_COMBO_PROGRAM);

        Bindings.bindBidirectional(cboKind.valueProperty(), ProgConfig.FILTER_DOWNLOAD_TYPE,
                new StringConverter<>() {

                    @Override
                    public String fromString(String cb) {
                        switch (cb) {
                            case DownloadConstants.ALL:
                                return DownloadConstants.ALL;
                            case DownloadConstants.TYPE_COMBO_DOWNLOAD:
                                return DownloadConstants.TYPE_DOWNLOAD;
                            case DownloadConstants.TYPE_COMBO_PROGRAM:
                                return DownloadConstants.TYPE_PROGRAM;
                            default:
                                return DownloadConstants.ALL;
                        }
                    }

                    @Override
                    public String toString(String prop) {
                        switch (prop) {
                            case DownloadConstants.ALL:
                                return DownloadConstants.ALL;
                            case DownloadConstants.TYPE_DOWNLOAD:
                                return DownloadConstants.TYPE_COMBO_DOWNLOAD;
                            case DownloadConstants.TYPE_PROGRAM:
                                return DownloadConstants.TYPE_COMBO_PROGRAM;
                            default:
                                return DownloadConstants.ALL;
                        }
                    }
                });

        cboState.getItems().addAll(DownloadConstants.ALL,
                DownloadConstants.STATE_COMBO_NOT_STARTED,
                DownloadConstants.STATE_COMBO_WAITING,
                DownloadConstants.STATE_COMBO_STARTED,
                DownloadConstants.STATE_COMBO_LOADING,
                DownloadConstants.STATE_COMBO_ERROR);
        cboState.valueProperty().bindBidirectional(ProgConfig.FILTER_DOWNLOAD_STATE);

        cboAbo.setItems(ThemeListFactory.allAboNamesList);
        cboAbo.valueProperty().bindBidirectional(ProgConfig.FILTER_DOWNLOAD_ABO);
    }

    private void initNumberDownloads() {
        spinnerAnz.valueProperty().addListener((u, o, n) -> {
            ProgConfig.DOWNLOAD_MAX_DOWNLOADS.setValue(spinnerAnz.getValue());
        });
        ProgConfig.DOWNLOAD_MAX_DOWNLOADS.addListener((u, o, n) -> {
            spinnerAnz.getValueFactory().setValue(ProgConfig.DOWNLOAD_MAX_DOWNLOADS.getValue());
        });
        spinnerAnz.getValueFactory().setValue(ProgConfig.DOWNLOAD_MAX_DOWNLOADS.getValue());
    }

    private void clearFilter() {
        if (cboSrc.getSelectionModel() != null) {
            cboSrc.getSelectionModel().selectFirst();
        }
        if (cboKind.getSelectionModel() != null) {
            cboKind.getSelectionModel().selectFirst();
        }

        ProgConfig.FILTER_DOWNLOAD_CHANNEL.setValue("");
        if (cboAbo.getSelectionModel() != null) {
            cboAbo.getSelectionModel().selectFirst();
        }
        if (cboState.getSelectionModel() != null) {
            cboState.getSelectionModel().selectFirst();
        }
    }
}
