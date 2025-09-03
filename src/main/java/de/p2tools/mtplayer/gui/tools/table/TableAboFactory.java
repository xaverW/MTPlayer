package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.config.ProgConst;
import de.p2tools.mtplayer.controller.data.abo.AboData;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class TableAboFactory {
    private TableAboFactory() {

    }

    public static void columnFactoryList(TableColumn<AboData, Integer> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                AboData aboData = getTableView().getItems().get(getIndex());
                if (aboData.getList() == ProgConst.LIST_FILM) {
                    setText("Film");
                } else if (aboData.getList() == ProgConst.LIST_AUDIO) {
                    setText("Audio");
                } else {
                    setText("F & A");
                }
            }
        });
    }
}
