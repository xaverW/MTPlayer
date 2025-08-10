package de.p2tools.mtplayer.controller.filter;

import de.p2tools.p2lib.configfile.pdata.P2DataList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class SelectedFilterList extends SimpleListProperty<FilmFilter> implements P2DataList<FilmFilter> {
    public String TAG = "SelectedFilterList";

    public SelectedFilterList() {
        // ist nur fürs Update nach AUDIO
        super(FXCollections.observableArrayList());
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getComment() {
        return "Liste aller SelectedFilter";
    }

    @Override
    public FilmFilter getNewItem() {
        return new FilmFilter("SelectedFilter", false, "SelectedFilter");
    }

    @Override
    public void addNewItem(Object obj) {
        if (obj.getClass().equals(FilmFilter.class)) {
            add((FilmFilter) obj);
        }
    }
}