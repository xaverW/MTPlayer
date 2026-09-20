package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.data.history.HistoryData;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class TableHistoryFactory {
    private TableHistoryFactory() {

    }

    public static void columnFactoryList(TableColumn<HistoryData, Boolean> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                HistoryData aboData = getTableView().getItems().get(getIndex());
                if (aboData.isAudio()) {
                    setText("Audio");
                } else {
                    setText("Film");
                }
            }
        });
    }
}
