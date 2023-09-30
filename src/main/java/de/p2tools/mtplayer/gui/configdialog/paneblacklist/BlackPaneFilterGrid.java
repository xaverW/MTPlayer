/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
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


package de.p2tools.mtplayer.gui.configdialog.paneblacklist;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackList;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2MenuButton;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.mtfilter.Filter;
import de.p2tools.p2lib.mtfilter.FilterCheck;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Predicate;

public class BlackPaneFilterGrid {
    private final BlackList list;
    private final P2MenuButton mbFilterChannel;
    private final StringProperty mbFilterChannelProp = new SimpleStringProperty();
    private final TextField txtFilterThema = new TextField();
    private final TextField txtFilterTitel = new TextField();
    private final TextField txtFilterThemaTitel = new TextField();
    private final P2ToggleSwitch tglFilterExact = new P2ToggleSwitch("Thema exakt");
    private final P2ToggleSwitch tglFilterActive = new P2ToggleSwitch("Aktiv");
    private final TextField txtFilterAll = new TextField();
    private final Button btnClearFilter = P2ButtonClearFilterFactory.getPButtonClear();
    private final TableView<BlackData> tableView;


    public BlackPaneFilterGrid(TableView<BlackData> tableView, BlackList list) {
        this.tableView = tableView;
        this.list = list;
        mbFilterChannel = new P2MenuButton(mbFilterChannelProp, ThemeListFactory.allChannelList);
    }

    SplitPane addFilterGrid(VBox vBox, boolean controlBlackListNotFilmFilter) {
        SplitPane splitPane = new SplitPane();
        final int SPACE_TITLE = 1;
        final int SPACE_VBOX = 5;

        VBox vb1 = new VBox(SPACE_VBOX);
        vb1.setAlignment(Pos.TOP_LEFT);
        vb1.setPadding(new Insets(P2LibConst.DIST_EDGE));
        vb1.getStyleClass().add("extra-pane");

        Label label = new Label("Diese Felder durchsuchen:");
        vb1.getChildren().add(label);

        VBox vb = new VBox(SPACE_TITLE);
        vb.getChildren().addAll(new Label("Sender"), mbFilterChannel);
        vb1.getChildren().add(vb);
        HBox.setHgrow(vb, Priority.ALWAYS);

        vb = new VBox(SPACE_TITLE);
        vb.getChildren().addAll(new Label("Thema"), txtFilterThema, P2GuiTools.getVDistance(2), tglFilterExact);
        vb1.getChildren().addAll(vb, P2GuiTools.getVDistance(1));
        HBox.setHgrow(vb, Priority.ALWAYS);

        vb = new VBox(SPACE_TITLE);
        vb.getChildren().addAll(new Label("Titel"), txtFilterTitel);
        vb1.getChildren().add(vb);
        HBox.setHgrow(vb, Priority.ALWAYS);

        vb = new VBox(SPACE_TITLE);
        vb.getChildren().addAll(new Label("Thema-Titel"), txtFilterThemaTitel);
        vb1.getChildren().add(vb);
        HBox.setHgrow(vb, Priority.ALWAYS);

        vb1.getChildren().add(P2GuiTools.getVDistance(5));
        vb1.getChildren().add(tglFilterActive);

        VBox vb2 = new VBox();
        vb2.setAlignment(Pos.TOP_LEFT);
        vb2.setPadding(new Insets(SPACE_VBOX));
        vb2.getStyleClass().add("extra-pane");
        vb2.getChildren().addAll(new Label("Alle Felder durchsuchen:"), txtFilterAll);

        VBox vb3 = new VBox();
        vb3.setAlignment(Pos.CENTER_RIGHT);
        vb3.setPadding(new Insets(SPACE_VBOX));
        vb3.getStyleClass().add("extra-pane");
        vb3.getChildren().addAll(btnClearFilter);

        VBox vAll = new VBox(SPACE_VBOX);
        vAll.getChildren().addAll(vb1, vb2, vb3);

        tableView.setStyle("-fx-border-width: 1px;");
        tableView.setStyle("-fx-border-color: -text-color-blue;");

        splitPane.getItems().addAll(tableView, vAll);
        splitPane.getItems().get(0).autosize();
        SplitPane.setResizableWithParent(vAll, false);

        if (controlBlackListNotFilmFilter) {
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.CONFIG_DIALOG_BLACKLIST_SPLITPANE);
        } else {
            splitPane.getDividers().get(0).positionProperty().bindBidirectional(ProgConfig.CONFIG_DIALOG_FILMLIST_FILTER_SPLITPANE);
        }
        VBox.setVgrow(splitPane, Priority.ALWAYS);
        vBox.getChildren().add(splitPane);

        makeFilter();
        return splitPane;
    }

    private void makeFilter() {
        mbFilterChannelProp.addListener((u, o, n) -> addPredicate());
        txtFilterThema.textProperty().addListener((u, o, n) -> addPredicate());
        txtFilterTitel.textProperty().addListener((u, o, n) -> addPredicate());
        txtFilterThemaTitel.textProperty().addListener((u, o, n) -> addPredicate());
        txtFilterAll.textProperty().addListener((u, o, n) -> addPredicate());

        tglFilterExact.setIndeterminate(true);
        tglFilterExact.setAllowIndeterminate(true);
        tglFilterExact.selectedProperty().addListener((u, o, n) -> addPredicate());
        tglFilterExact.indeterminateProperty().addListener((u, o, n) -> addPredicate());

        tglFilterActive.setIndeterminate(true);
        tglFilterActive.setAllowIndeterminate(true);
        tglFilterActive.selectedProperty().addListener((u, o, n) -> addPredicate());
        tglFilterActive.indeterminateProperty().addListener((u, o, n) -> addPredicate());

        btnClearFilter.setOnAction(a -> {
            mbFilterChannelProp.setValue("");
            txtFilterThema.clear();
            tglFilterExact.setSelected(false);
            tglFilterExact.setIndeterminate(true);
            txtFilterTitel.clear();
            txtFilterThemaTitel.clear();
            txtFilterAll.clear();
            tglFilterActive.setSelected(false);
            tglFilterActive.setIndeterminate(true);
        });
    }

    private void addPredicate() {
        Predicate<BlackData> predicate = blackData -> true;

        if (!mbFilterChannelProp.getValueSafe().isEmpty()) {
            Filter filter = new Filter(mbFilterChannelProp.getValueSafe(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getChannel()));
        }
        if (!txtFilterThema.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterThema.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getTheme()));
        }
        if (!tglFilterExact.isIndeterminate()) {
            predicate = predicate.and(blackData -> {
                if (tglFilterExact.isSelected()) {
                    return blackData.isThemeExact();
                } else {
                    return !blackData.isThemeExact();
                }
            });
        }
        if (!txtFilterTitel.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterTitel.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getTitle()));
        }
        if (!txtFilterThemaTitel.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterThemaTitel.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getThemeTitle()));
        }
        if (!tglFilterActive.isIndeterminate()) {
            predicate = predicate.and(blackData -> {
                if (tglFilterActive.isSelected()) {
                    return blackData.isActive();
                } else {
                    return !blackData.isActive();
                }
            });
        }

        if (!txtFilterAll.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterAll.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getChannel()) ||
                    FilterCheck.check(filter, blackData.getTheme()) ||
                    FilterCheck.check(filter, blackData.getTitle()) ||
                    FilterCheck.check(filter, blackData.getThemeTitle())

            );
        }

        list.filteredListSetPred(predicate);
    }
}
