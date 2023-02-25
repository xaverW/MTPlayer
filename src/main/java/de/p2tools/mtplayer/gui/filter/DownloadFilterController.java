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
import de.p2tools.mtplayer.controller.tools.MLBandwidthTokenBucket;
import de.p2tools.mtplayer.gui.tools.HelpText;
import de.p2tools.p2lib.guitools.PButton;
import de.p2tools.p2lib.guitools.PButtonClearFilter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class DownloadFilterController extends FilterController {

    private ComboBox<String> cboSrc = new ComboBox<>(); //Downloadquelle: Abo, manuell gestartet
    private ComboBox<String> cboKind = new ComboBox<>(); //Download über Programm / direkter Downlaod, http
    private ComboBox<String> cboChannel = new ComboBox<>();
    private ComboBox<String> cboAbo = new ComboBox<>();
    private ComboBox<String> cboState = new ComboBox<>();

    private Spinner<Integer> spinnerAnz = new Spinner<>();
    private Slider sliderBandwidth = new Slider();
    private PButtonClearFilter btnClear = new PButtonClearFilter();
    private Label lblBandwidth = new Label();

    // funktioniert nur wenn hier angelegt, geht sonst die Ref verloren!
    IntegerProperty bandwidthValue = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE;
    IntegerProperty anzValue = ProgConfig.DOWNLOAD_MAX_DOWNLOADS;
    IntegerProperty integerProperty;

    private final VBox vBoxFilter;
    private final ProgData progData;

    public DownloadFilterController() {
        super(ProgConfig.DOWNLOAD_GUI_FILTER_DIVIDER_ON);
        vBoxFilter = getVBoxFilter(true);
        progData = ProgData.getInstance();
        progData.downloadFilterController = this;

        initLayout();
        initFilter();

        initNumberDownloads();
        initBandwidth();
    }

    private void initLayout() {
        addCont("Quelle", cboSrc, vBoxFilter);
        addCont("Downloadart", cboKind, vBoxFilter);
        addCont("Sender", cboChannel, vBoxFilter);
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

        VBox vb = new VBox(FilterController.FILTER_SPACING_DOWNLOAD);
        addCont("gleichzeitige Downloads", spinnerAnz, vb);

        Label lblText = new Label("max. Bandbreite: ");
        lblText.setMinWidth(0);
        lblText.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Dowload beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));
        sliderBandwidth.setTooltip(new Tooltip("Maximale Bandbreite die ein einzelner Dowload beanspruchen darf \n" +
                "oder unbegrenzt wenn \"aus\""));
        HBox hh = new HBox();
        HBox.setHgrow(hh, Priority.ALWAYS);
        HBox h = new HBox();
        h.getChildren().addAll(lblText, hh, lblBandwidth);
        addCont(h, sliderBandwidth, vb);

        final Button btnHelp = PButton.helpButton("Filter", HelpText.GUI_DOWNLOAD_FILTER);
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
                new StringConverter<String>() {

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
                new StringConverter<String>() {

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
                DownloadConstants.STATE_COMBO_LOADING);
        cboState.valueProperty().bindBidirectional(ProgConfig.FILTER_DOWNLOAD_STATE);

        cboChannel.setItems(progData.worker.getAllChannelList());
        cboChannel.setVisibleRowCount(25);
        cboChannel.valueProperty().bindBidirectional(ProgConfig.FILTER_DOWNLOAD_CHANNEL);

        cboAbo.setItems(progData.worker.getAllAboNamesList()); // todo evtl. nur die vorhandenen Abos
        cboAbo.valueProperty().bindBidirectional(ProgConfig.FILTER_DOWNLOAD_ABO);
    }

    private void initNumberDownloads() {
        spinnerAnz.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9, 1));
        integerProperty = IntegerProperty.integerProperty(spinnerAnz.getValueFactory().valueProperty());
        integerProperty.bindBidirectional(anzValue);
    }

    private void initBandwidth() {
        sliderBandwidth.setMin(50);
        sliderBandwidth.setMax(MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE);
        sliderBandwidth.setShowTickLabels(true);
        sliderBandwidth.setMinorTickCount(9);
        sliderBandwidth.setMajorTickUnit(250);
        sliderBandwidth.setBlockIncrement(25);
        sliderBandwidth.setSnapToTicks(true);

        sliderBandwidth.setLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Double x) {
                if (x == MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE) {
                    return "alles";
                }

                return x.intValue() + "";
            }

            @Override
            public Double fromString(String string) {
                return null;
            }
        });

        sliderBandwidth.valueProperty().bindBidirectional(bandwidthValue);
        setTextBandwidth();

        sliderBandwidth.valueProperty().addListener((obs, oldValue, newValue) -> {
            setTextBandwidth();
        });
    }

    private void setTextBandwidth() {
        int bandwidthKByte;
        String ret;
        bandwidthKByte = ProgConfig.DOWNLOAD_MAX_BANDWIDTH_KBYTE.getValue();
        if (bandwidthKByte == MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE) {
            ret = "alles";
        } else {
            ret = bandwidthKByte + " kB/s";
        }
        lblBandwidth.setText(ret);
    }

    private void clearFilter() {
        if (cboSrc.getSelectionModel() != null) {
            cboSrc.getSelectionModel().selectFirst();
        }
        if (cboKind.getSelectionModel() != null) {
            cboKind.getSelectionModel().selectFirst();
        }
        if (cboChannel.getSelectionModel() != null) {
            cboChannel.getSelectionModel().selectFirst();
        }
        if (cboAbo.getSelectionModel() != null) {
            cboAbo.getSelectionModel().selectFirst();
        }
        if (cboState.getSelectionModel() != null) {
            cboState.getSelectionModel().selectFirst();
        }
    }
}
