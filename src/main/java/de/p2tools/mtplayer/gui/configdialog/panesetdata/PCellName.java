package de.p2tools.mtplayer.gui.configdialog.panesetdata;

import de.p2tools.mtplayer.controller.data.setdata.SetData;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class PCellName<S, T> extends TableCell<S, T> {

    public Callback<TableColumn<SetData, String>, TableCell<SetData, String>> cellFactory
            = (final TableColumn<SetData, String> param) -> new TableCell<>() {

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (item == null || empty) {
                setGraphic(null);
                setText(null);
                return;
            }

            SetData setData = getTableView().getItems().get(getIndex());
            Label lbl = new Label(setData.getVisibleName());
            setData.playProperty().addListener((u, o, n) -> {
                if (setData.isPlay()) {
                    lbl.getStyleClass().add("markSetPlay");
                } else {
                    lbl.getStyleClass().removeAll("markSetPlay");
                }
            });
            if (setData.isPlay()) {
                lbl.getStyleClass().add("markSetPlay");
            } else {
                lbl.getStyleClass().removeAll("markSetPlay");
            }
            setGraphic(lbl);
        }
    };
}
