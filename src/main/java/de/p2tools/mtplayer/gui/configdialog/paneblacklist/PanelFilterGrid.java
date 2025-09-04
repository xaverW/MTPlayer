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
import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.data.blackdata.BlackData;
import de.p2tools.mtplayer.controller.data.blackdata.BlackList;
import de.p2tools.mtplayer.controller.data.blackdata.BlackListFilter;
import de.p2tools.mtplayer.controller.worker.ThemeListFactory;
import de.p2tools.p2lib.P2LibConst;
import de.p2tools.p2lib.guitools.P2ButtonClearFilterFactory;
import de.p2tools.p2lib.guitools.P2GuiTools;
import de.p2tools.p2lib.guitools.P2MenuButton;
import de.p2tools.p2lib.guitools.ptoggleswitch.P2ToggleSwitch;
import de.p2tools.p2lib.mediathek.filter.Filter;
import de.p2tools.p2lib.mediathek.filter.FilterCheck;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Predicate;

public class PanelFilterGrid {
    private final BlackList list;
    private final P2MenuButton mbFilterChannel;
    private final StringProperty mbFilterChannelProp = new SimpleStringProperty();

    private final TextField txtFilterThema = new TextField();
    private final TextField txtFilterTitel = new TextField();
    private final TextField txtFilterThemaTitel = new TextField();
    private final TextField txtFilterSomewhere = new TextField();

    private final P2ToggleSwitch tglFilterThemeExact = new P2ToggleSwitch("Thema exakt");
    private final P2ToggleSwitch tglFilterActive = new P2ToggleSwitch("Aktiv");

    public final RadioButton rbFilm = new RadioButton("Film");
    public final RadioButton rbAudio = new RadioButton("Audio");
    public final RadioButton rbFilmAudio = new RadioButton("Film/Audio");

    private final Button btnClearFilter = P2ButtonClearFilterFactory.getPButtonClear();
    private final TableView<BlackData> tableView;
    private final BlackListFilter blackListFilterBlackList;

    public PanelFilterGrid(TableView<BlackData> tableView, BlackList list, BlackListFilter blackListFilterBlackList) {
        this.tableView = tableView;
        this.list = list;
        this.blackListFilterBlackList = blackListFilterBlackList;
        this.mbFilterChannel = new P2MenuButton(mbFilterChannelProp, ThemeListFactory.allChannelListFilm);

        bind();
        addPredicate();
    }

    private void bind() {
        rbFilmAudio.setSelected(blackListFilterBlackList.getList() == ProgConst.LIST_FILM_AUDIO);
        rbFilm.setSelected(blackListFilterBlackList.getList() == ProgConst.LIST_FILM);
        rbAudio.setSelected(blackListFilterBlackList.getList() == ProgConst.LIST_AUDIO);

        mbFilterChannelProp.bindBidirectional(blackListFilterBlackList.channelProperty());
        txtFilterThema.textProperty().bindBidirectional(blackListFilterBlackList.themeProperty());
        txtFilterTitel.textProperty().bindBidirectional(blackListFilterBlackList.titleProperty());
        txtFilterThemaTitel.textProperty().bindBidirectional(blackListFilterBlackList.themeTitleProperty());
        txtFilterSomewhere.textProperty().bindBidirectional(blackListFilterBlackList.somewhereProperty());

        tglFilterThemeExact.setAllowIndeterminate(true);
        tglFilterActive.setAllowIndeterminate(true);

        tglFilterThemeExact.indeterminateProperty().bindBidirectional(blackListFilterBlackList.themeExactIndeterminateProperty());
        tglFilterThemeExact.selectedProperty().bindBidirectional(blackListFilterBlackList.themeExactProperty());
        tglFilterActive.indeterminateProperty().bindBidirectional(blackListFilterBlackList.filterActiveIndeterminateProperty());
        tglFilterActive.selectedProperty().bindBidirectional(blackListFilterBlackList.filterActiveProperty());
    }

    public void close() {
        if (rbFilmAudio.isSelected()) {
            blackListFilterBlackList.setList(ProgConst.LIST_FILM_AUDIO);
        } else if (rbFilm.isSelected()) {
            blackListFilterBlackList.setList(ProgConst.LIST_FILM);
        } else {
            blackListFilterBlackList.setList(ProgConst.LIST_AUDIO);
        }

        mbFilterChannelProp.unbindBidirectional(blackListFilterBlackList.channelProperty());
        txtFilterThema.textProperty().unbindBidirectional(blackListFilterBlackList.themeProperty());
        txtFilterTitel.textProperty().unbindBidirectional(blackListFilterBlackList.titleProperty());
        txtFilterThemaTitel.textProperty().unbindBidirectional(blackListFilterBlackList.themeTitleProperty());
        txtFilterSomewhere.textProperty().unbindBidirectional(blackListFilterBlackList.somewhereProperty());

        tglFilterThemeExact.indeterminateProperty().unbindBidirectional(blackListFilterBlackList.themeExactIndeterminateProperty());
        tglFilterThemeExact.selectedProperty().unbindBidirectional(blackListFilterBlackList.themeExactProperty());
        tglFilterActive.indeterminateProperty().unbindBidirectional(blackListFilterBlackList.filterActiveIndeterminateProperty());
        tglFilterActive.selectedProperty().unbindBidirectional(blackListFilterBlackList.filterActiveProperty());
    }

    SplitPane addFilterGrid(VBox vBox, boolean controlBlackListNotFilmFilter) {
        SplitPane splitPane = new SplitPane();
        final int SPACE_TITLE = 1;
        final int SPACE_VBOX = 5;

        VBox vb1 = new VBox(SPACE_VBOX);
        vb1.setAlignment(Pos.TOP_LEFT);
        vb1.setPadding(new Insets(P2LibConst.PADDING));
        vb1.getStyleClass().add("extra-pane");

        Label label = new Label("Blacklist-Eintrag suchen:");
        vb1.getChildren().add(label);

        vb1.getChildren().add(P2GuiTools.getVDistance(5));
        vb1.getChildren().add(tglFilterActive);

        HBox hBox = new HBox(2);
        hBox.getChildren().addAll(rbFilmAudio, rbFilm, rbAudio);
        vb1.getChildren().add(hBox);

        VBox vb = new VBox(SPACE_TITLE);
        vb.getChildren().addAll(new Label("Sender"), mbFilterChannel);
        vb1.getChildren().add(vb);
        HBox.setHgrow(vb, Priority.ALWAYS);

        vb = new VBox(SPACE_TITLE);
        vb.getChildren().addAll(new Label("Thema"), txtFilterThema, P2GuiTools.getVDistance(2), tglFilterThemeExact);
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

        VBox vb2 = new VBox();
        vb2.setAlignment(Pos.TOP_LEFT);
        vb2.setPadding(new Insets(SPACE_VBOX));
        vb2.getStyleClass().add("extra-pane");
        vb2.getChildren().addAll(new Label("In allen Feldern suchen:"), txtFilterSomewhere);

        VBox vb3 = new VBox();
        vb3.setAlignment(Pos.CENTER_RIGHT);
        vb3.setPadding(new Insets(SPACE_VBOX));
        vb3.getStyleClass().add("extra-pane");
        vb3.getChildren().addAll(btnClearFilter);

        VBox vAll = new VBox(SPACE_VBOX);
        vAll.getChildren().addAll(vb1, vb2, vb3);

        tableView.setStyle("-fx-border-width: 1px; -fx-border-color: -text-color-blue;");

        splitPane.getItems().addAll(tableView, vAll);
        SplitPane.setResizableWithParent(vAll, false);
        SplitPane.setResizableWithParent(tableView, false);

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
        final ToggleGroup toggleGroupList = new ToggleGroup();
        rbFilmAudio.setToggleGroup(toggleGroupList);
        rbFilm.setToggleGroup(toggleGroupList);
        rbAudio.setToggleGroup(toggleGroupList);
        toggleGroupList.selectedToggleProperty().addListener((u, o, n) -> addPredicate());

        mbFilterChannelProp.addListener((u, o, n) -> addPredicate());
        txtFilterThema.textProperty().addListener((u, o, n) -> addPredicate());
        txtFilterTitel.textProperty().addListener((u, o, n) -> addPredicate());
        txtFilterThemaTitel.textProperty().addListener((u, o, n) -> addPredicate());
        txtFilterSomewhere.textProperty().addListener((u, o, n) -> addPredicate());

        tglFilterThemeExact.selectedProperty().addListener((u, o, n) -> addPredicate());
        tglFilterThemeExact.indeterminateProperty().addListener((u, o, n) -> addPredicate());
        tglFilterActive.selectedProperty().addListener((u, o, n) -> addPredicate());
        tglFilterActive.indeterminateProperty().addListener((u, o, n) -> addPredicate());

        btnClearFilter.setOnAction(a -> {
            rbFilmAudio.setSelected(true);
            mbFilterChannelProp.setValue("");
            txtFilterThema.clear();
            tglFilterThemeExact.setSelected(false);
            tglFilterThemeExact.setIndeterminate(true);
            txtFilterTitel.clear();
            txtFilterThemaTitel.clear();
            txtFilterSomewhere.clear();
            tglFilterActive.setSelected(false);
            tglFilterActive.setIndeterminate(true);
        });
    }

    private void addPredicate() {
        Predicate<BlackData> predicate = blackData -> true;
        if (rbFilm.isSelected()) {
            predicate = predicate.and(blackData -> blackData.getList() == ProgConst.LIST_FILM);
        } else if (rbAudio.isSelected()) {
            predicate = predicate.and(blackData -> blackData.getList() == ProgConst.LIST_AUDIO);
        }

        if (!mbFilterChannelProp.getValueSafe().isEmpty()) {
            Filter filter = new Filter(mbFilterChannelProp.getValueSafe(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getChannel()));
        }
        if (!txtFilterThema.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterThema.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getTheme()));
        }
        if (!txtFilterTitel.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterTitel.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getTitle()));
        }
        if (!txtFilterThemaTitel.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterThemaTitel.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getThemeTitle()));
        }
        if (!txtFilterSomewhere.getText().isEmpty()) {
            Filter filter = new Filter(txtFilterSomewhere.getText(), true);
            predicate = predicate.and(blackData -> FilterCheck.check(filter, blackData.getChannel()) ||
                    FilterCheck.check(filter, blackData.getTheme()) ||
                    FilterCheck.check(filter, blackData.getTitle()) ||
                    FilterCheck.check(filter, blackData.getThemeTitle())

            );
        }

        if (!tglFilterThemeExact.isIndeterminate()) {
            predicate = predicate.and(blackData -> {
                if (tglFilterThemeExact.isSelected()) {
                    return blackData.isThemeExact();
                } else {
                    return !blackData.isThemeExact();
                }
            });
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

        list.filteredListSetPred(predicate);
    }
}
