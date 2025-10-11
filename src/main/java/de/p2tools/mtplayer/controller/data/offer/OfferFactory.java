package de.p2tools.mtplayer.controller.data.offer;

import de.p2tools.mtplayer.controller.config.ProgData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class OfferFactory {
    private OfferFactory() {
    }

    public static ObservableList<OfferData> getActiveList() {
        ObservableList<OfferData> list = FXCollections.observableArrayList();
        ProgData.getInstance().offerList.forEach(o -> {
            if (o.isActive()) {
                list.add(o);
            }
        });
        return list;
    }
}
