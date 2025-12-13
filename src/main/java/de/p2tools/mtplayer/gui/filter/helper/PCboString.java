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


package de.p2tools.mtplayer.gui.filter.helper;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.mtplayer.controller.config.ProgData;
import de.p2tools.mtplayer.controller.data.offer.OfferData;
import de.p2tools.mtplayer.controller.data.offer.OfferFactory;
import de.p2tools.mtplayer.controller.picon.PIconFactory;
import de.p2tools.p2lib.mediathek.filter.FilterCheckRegEx;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

public class PCboString extends ComboBox<PCboString.PCboLabel> {
    public static final int MAX_FILTER_HISTORY = 20;
    private final ObservableList<String> storedFilterList; // Liste der gespeicherten Filter
    private final ObservableList<PCboLabel> itemList = FXCollections.observableArrayList(new PCboLabel("")); // aktuelle Liste der Filter

    private final StringProperty strSearchProperty; // ist z.B.: das THEMA_PROPERTY
    private final BooleanSupplier doSomething; // Funktion, was bei Auswahl gemacht werden soll, Rückgabewert wird nicht gebraucht

    public PCboString(ObservableList<String> storedFilterList, StringProperty strSearchProperty) {
        this.storedFilterList = storedFilterList;
        this.strSearchProperty = strSearchProperty;
        this.doSomething = () -> true;
        start();
    }

    public PCboString(ObservableList<String> storedFilterList,
                      StringProperty strSearchProperty, BooleanSupplier doSomething) {
        this.storedFilterList = storedFilterList;
        this.strSearchProperty = strSearchProperty;
        this.doSomething = doSomething;
        start();
    }

    private void start() {
        // mit den gespeicherten füllen
        cleanStoredList(); // leeren und doppelte Einträge entfernen
        fillItemListWithStoredList(); // leeren und doppelte Einträge entfernen

        setEditable(true);
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        setVisibleRowCount(MAX_FILTER_HISTORY);
        initListener();
    }

    private void initListener() {
        // Anzeige wenn RegEx fehlerhaft
        new FilterCheckRegEx(getEditor());

        // Editor setzen
        getEditor().setText(strSearchProperty.getValue());
        strSearchProperty.addListener((u, o, n) -> getEditor().setText(strSearchProperty.getValueSafe()));

        // Filtervorschläge
        getEditor().setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2) {

                    if (!ProgConfig.SYSTEM_USE_OFFERTABLE.get()) {
                        return;
                    }
                    List<OfferData> list = OfferFactory.getActiveList();
                    if (list.isEmpty()) {
                        return;
                    }

                    if (list.size() > 1) {
                        ObjectProperty<OfferData> prop = new SimpleObjectProperty<>();
                        new OfferFilterDialog(ProgData.getInstance(), prop);
                        if (prop.get() != null) {
                            getEditor().setText(prop.get().getOffer());
                        }
                    } else {
                        getEditor().setText(list.get(0).getOffer());
                    }
                }
            }
        });

        // wenn was eingetippt oder ausgewählt wird
        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            System.out.println("==> Editor");
            addToStoredList(getEditor().getText());
            strSearchProperty.setValue(getEditor().getText());
        });

        // wenn die Combo geöffnet wird, Liste bauen
        this.showingProperty().addListener((u, o, n) -> {
            if (isShowing()) {
                // beim Aufmachen
                System.out.println("showingProperty");
                fillItemListWithStoredList();
            }
        });

        // wenn aus Combo was ausgewählt wird und RETURN dann melden
        getSelectionModel().selectedItemProperty().addListener(
                (ChangeListener<Object>) (observable, oldValue, newValue) -> {
                    System.out.println("Aus Combo ausgewählt");
                    // kann auch ein String!!!! sein
                    if (ProgConfig.SYSTEM_FILTER_RETURN.getValue()) { // nuss nicht sein
                        // dann melden, wenn ein Label und nicht der gleiche Text drin steht
                        if (this.isShowing() ||
                                newValue != null &&
                                        newValue.getClass().equals(PCboLabel.class) &&
                                        !strSearchProperty.getValueSafe().equals(((PCboLabel) newValue).getText())) {
                            doSomething.getAsBoolean();
                        }
                    }
                });

        // wenn RETURN, dann immer melden
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                doSomething.getAsBoolean();
            }
        });

        // steuert die Anzeige der Zellen im Combo (Button + Label mit Text)
        this.setCellFactory(cell -> new ListCell<>() {
            final Button btnDel = new Button();
            final HBox hBox = new HBox();
            final Label lblFilter = new Label();

            {
                btnDel.setGraphic(PIconFactory.PICON.BTN_CLEAR.getFontIcon());
                btnDel.getStyleClass().add("buttonVerySmall");
                btnDel.visibleProperty().bind(Bindings.size(itemList).greaterThan(1));

                hBox.setPadding(new Insets(0));
                hBox.setAlignment(Pos.CENTER_LEFT);
                hBox.setSpacing(5);
                hBox.getChildren().addAll(btnDel, lblFilter);
            }

            @Override
            protected void updateItem(PCboLabel pCboLabel, boolean empty) {
                super.updateItem(pCboLabel, empty);

                if (!empty && pCboLabel != null) {
                    btnDel.setOnMousePressed(m -> {
                        if (pCboLabel.getText().isEmpty()) {
                            // dann ist's der erste
                            if (itemList.size() > 1) {
                                itemList.remove(1, itemList.size());
                            }
                        } else {
                            itemList.remove(pCboLabel);
                        }
                        getSelectionModel().select(0);
                        fillStoredListWithItem();
                    });

                    lblFilter.textProperty().bind(pCboLabel.textProperty());
                    setGraphic(hBox);

                } else {
                    // Nothing to display here
                    setGraphic(null);
                }
            }
        });
    }

    private void addToStoredList(String add) {
        if (add.isEmpty()) {
            return;
        }

        if (storedFilterList.isEmpty()) {
            storedFilterList.add(add);

        } else if (add.contains(storedFilterList.get(0)) ||
                storedFilterList.get(0).contains(add)) {
            // wenn der neue Wert nur eine "Erweiterung" ist dann austauschen
            storedFilterList.remove(0);
            storedFilterList.add(0, add);

        } else {
            storedFilterList.add(0, add);
        }

        cleanStoredList();
    }

    private void cleanStoredList() {
        while (storedFilterList.size() > MAX_FILTER_HISTORY) {
            storedFilterList.remove(storedFilterList.size() - 1);
        }
        storedFilterList.setAll(storedFilterList.stream()
                .filter(p -> !p.isEmpty())
                .distinct()
                .collect(Collectors.toList())); // doppelte löschen
    }

    private void fillStoredListWithItem() {
        storedFilterList.clear();
        itemList.stream()
                .filter(s -> !s.getText().isEmpty())
                .forEach(s -> storedFilterList.add(s.toString()));
        cleanStoredList();
    }

    private void fillItemListWithStoredList() {
        getSelectionModel().clearSelection();
        itemList.clear();
        itemList.add(new PCboLabel(""));
        storedFilterList.forEach(s -> itemList.add(new PCboLabel(s)));
        setItems(itemList);
    }

    static class PCboLabel extends Label implements Comparable<PCboLabel> {
        // das sind die Labels mit dem Filter-Text, Klick löst Filter aus
        public PCboLabel(String value) {
            setText(value);
        }

        @Override
        public String toString() {
            return getText();
        }

        @Override
        public int compareTo(PCboLabel arg0) {
            return getText().compareTo(arg0.getText());
        }
    }
}
