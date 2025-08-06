package de.p2tools.mtplayer.gui.tools.table;

import de.p2tools.mtplayer.controller.data.download.DownloadData;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

public class TableDownloadFactory {
    private TableDownloadFactory() {

    }

    public static void columnFactoryList(TableColumn<DownloadData, Boolean> column) {
        column.setCellFactory(c -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                if (item) {
                    setText("Audio");
                } else {
                    setText("Film");
                }
            }
        });
    }
}
