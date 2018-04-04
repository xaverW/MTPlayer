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

package de.mtplayer.mtp.gui;

import de.mtplayer.mLib.tools.MLBandwidthTokenBucket;
import de.mtplayer.mtp.controller.config.Config;
import de.mtplayer.mtp.controller.data.download.DownloadInfos;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.StringConverter;

public class DownloadFilterController extends FilterController {

    ComboBox<String> cbSrc = new ComboBox<>(); //Downloadquelle: Abo, manuell gestartet
    ComboBox<String> cbArt = new ComboBox<>(); //Download über Programm / direkter Downlaod, http
    ComboBox<String> cbSender = new ComboBox<>();
    ComboBox<String> cbAbo = new ComboBox<>();

    Spinner<Integer> spinnerAnz = new Spinner<>();
    Slider sliderBandwidth = new Slider();
    Button btnClear = new Button("Filter löschen");
    Label lblBandwidth = new Label();

    // funktioniert nur wenn hier angelegt, geht sonst die Ref verloren!
    IntegerProperty bandwidthValue = Config.DOWNLOAD_MAX_BANDWITH_KBYTE.getIntegerProperty();
    IntegerProperty anzValue = Config.DOWNLOAD_MAX_DOWNLOADS.getIntegerProperty();
    IntegerProperty integerProperty;

    public DownloadFilterController() {
        initLayout();
        initFilter();

        initAnzahl();
        initBandwidth();
    }

    private void initLayout() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        addCont("Quelle", cbSrc, vBox);
        addCont("Downloadart", cbArt, vBox);
        addCont("Sender", cbSender, vBox);
        addCont("Abo", cbAbo, vBox);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(10, 0, 0, 0));
        hBox.getChildren().add(btnClear);

        Separator sp = new Separator();
        sp.setMinHeight(20);

        vBox.getChildren().addAll(hBox, sp);
        vbFilter.getChildren().addAll(vBox);


        addCont("gleichzeitige Downloads", spinnerAnz, vbFilter);


        hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.getChildren().add(lblBandwidth);

        VBox v = new VBox();
        Label label = new Label("max. Bandbreite je Download");
        v.getChildren().addAll(label, sliderBandwidth, hBox);
        vbFilter.getChildren().add(v);

    }

    private void addCont(String txt, Control control, VBox vBox) {
        control.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox v = new VBox();
        Label label = new Label(txt);
        v.getChildren().addAll(label, control);
        vBox.getChildren().add(v);
    }

    private void initFilter() {
        btnClear.setOnAction(a -> clearFilter());

        cbSrc.getItems().addAll(DownloadInfos.SRC_COMBO_ALL,
                DownloadInfos.SRC_COMBO_DOWNLOAD,
                DownloadInfos.SRC_COMBO_ABO);

        Bindings.bindBidirectional(cbSrc.valueProperty(), Config.FILTER_DOWNLOAD_SOURCE.getStringProperty(),
                new StringConverter<String>() {

                    public String fromString(String cb) {
                        switch (cb) {
                            case DownloadInfos.SRC_COMBO_ALL:
                                return DownloadInfos.SRC_ALL;
                            case DownloadInfos.SRC_COMBO_ABO:
                                return DownloadInfos.SRC_ABO;
                            case DownloadInfos.SRC_COMBO_DOWNLOAD:
                                return DownloadInfos.SRC_DOWNLOAD;
                            default:
                                return DownloadInfos.SRC_ALL;
                        }
                    }

                    public String toString(String prop) {
                        switch (prop) {
                            case DownloadInfos.SRC_ALL:
                                return DownloadInfos.SRC_COMBO_ALL;
                            case DownloadInfos.SRC_ABO:
                                return DownloadInfos.SRC_COMBO_ABO;
                            case DownloadInfos.SRC_DOWNLOAD:
                                return DownloadInfos.SRC_COMBO_DOWNLOAD;
                            default:
                                return DownloadInfos.SRC_COMBO_ALL;
                        }
                    }
                });

        cbArt.getItems().addAll(DownloadInfos.ART_COMBO_ALL,
                DownloadInfos.ART_COMBO_DOWNLOAD,
                DownloadInfos.ART_COMBO_PROGRAMM);

        Bindings.bindBidirectional(cbArt.valueProperty(), Config.FILTER_DOWNLOAD_KIND.getStringProperty(),
                new StringConverter<String>() {

                    public String fromString(String cb) {
                        switch (cb) {
                            case DownloadInfos.ART_COMBO_ALL:
                                return DownloadInfos.ART_ALL;
                            case DownloadInfos.ART_COMBO_DOWNLOAD:
                                return DownloadInfos.ART_DOWNLOAD;
                            case DownloadInfos.ART_COMBO_PROGRAMM:
                                return DownloadInfos.ART_PROGRAMM;
                            default:
                                return DownloadInfos.ART_ALL;
                        }
                    }

                    public String toString(String prop) {
                        switch (prop) {
                            case DownloadInfos.ART_ALL:
                                return DownloadInfos.ART_COMBO_ALL;
                            case DownloadInfos.ART_DOWNLOAD:
                                return DownloadInfos.ART_COMBO_DOWNLOAD;
                            case DownloadInfos.ART_PROGRAMM:
                                return DownloadInfos.ART_COMBO_PROGRAMM;
                            default:
                                return DownloadInfos.ART_COMBO_ALL;
                        }
                    }
                });

        cbSender.valueProperty().bindBidirectional(Config.FILTER_DOWNLOAD_SENDER.getStringProperty());
        cbSender.setItems(daten.nameLists.getObsAllSender());

        cbAbo.valueProperty().bindBidirectional(Config.FILTER_DOWNLOAD_ABO.getStringProperty());
        cbAbo.setItems(daten.nameLists.getObsAllAbonames()); // todo evtl. nur die vorhandenen Abos
    }

    private void initAnzahl() {
        spinnerAnz.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9, 1));
        integerProperty = IntegerProperty.integerProperty(spinnerAnz.getValueFactory().valueProperty());
        integerProperty.bindBidirectional(anzValue);
    }

    private void initBandwidth() {
        sliderBandwidth.setMin(50);
        sliderBandwidth.setMax(1_000);
        sliderBandwidth.setShowTickLabels(true);
        sliderBandwidth.setMinorTickCount(9);
        sliderBandwidth.setMajorTickUnit(250);
        sliderBandwidth.setBlockIncrement(25);
        sliderBandwidth.setSnapToTicks(true);

        sliderBandwidth.valueProperty().bindBidirectional(bandwidthValue);
        setTextBandwith();

        sliderBandwidth.valueProperty().addListener((obs, oldValue, newValue) -> {
            setTextBandwith();
        });
    }

    private void setTextBandwith() {
        int bandbreiteKByte;
        String ret;
        bandbreiteKByte = Config.DOWNLOAD_MAX_BANDWITH_KBYTE.getInt();
        if (bandbreiteKByte == MLBandwidthTokenBucket.BANDWIDTH_MAX_KBYTE) {
            ret = "aus";
        } else {
            ret = bandbreiteKByte + " kByte/s";
        }
        lblBandwidth.setText(ret);
        if (bandbreiteKByte > MLBandwidthTokenBucket.BANDWIDTH_MAX_RED_KBYTE) {
            final Text amount = new Text(ret);
            amount.setFill(Color.RED);
            lblBandwidth.setText(amount.getText());
            lblBandwidth.setTextFill(Color.RED);
        } else {
            final Text amount = new Text(ret);
            lblBandwidth.setText(amount.getText());
            lblBandwidth.setTextFill(Color.BLACK);
        }
    }

    private void clearFilter() {
        if (cbSrc.getSelectionModel() != null) {
            cbSrc.getSelectionModel().selectFirst();
        }
        if (cbArt.getSelectionModel() != null) {
            cbArt.getSelectionModel().selectFirst();
        }
        if (cbSender.getSelectionModel() != null) {
            cbSender.getSelectionModel().selectFirst();
        }
        if (cbAbo.getSelectionModel() != null) {
            cbAbo.getSelectionModel().selectFirst();
        }
    }

}
