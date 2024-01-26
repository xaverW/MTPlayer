package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class BackwardFilmFilter {

    private static final int MAX_FILTER_GO_BACK = 15;
    private final BooleanProperty backwardIsEmpty = new SimpleBooleanProperty(false);
    private final BooleanProperty forwardIsEmpty = new SimpleBooleanProperty(false);
    private boolean thema = false, themaTitle = false, title = false, somewhere = false, url = false;

    // ist die Liste der zuletzt verwendeten Filter
    private final ObservableList<FilmFilter> backwardFilterList =
            FXCollections.observableList(new ArrayList<>() {
                @Override
                public void add(int index, FilmFilter e) {
                    while (this.size() > MAX_FILTER_GO_BACK) {
                        remove(0);
                    }
                    super.add(e);
                }
            }, (FilmFilter filmFilter) -> new Observable[]{filmFilter.nameProperty()});
    private final ObservableList<FilmFilter> forwardFilterList =
            FXCollections.observableList(new ArrayList<>() {
                @Override
                public void add(int index, FilmFilter e) {
                    while (this.size() > MAX_FILTER_GO_BACK) {
                        remove(0);
                    }
                    super.add(e);
                }
            }, (FilmFilter filmFilter) -> new Observable[]{filmFilter.nameProperty()});

    public BackwardFilmFilter() {
        init();
    }

    private void init() {
        backwardFilterList.addListener((ListChangeListener<FilmFilter>) c -> {
            // Eintrag 1 ist der aktuelle Filter
            backwardIsEmpty.setValue(backwardFilterList.size() > 1);
        });
        forwardFilterList.addListener((ListChangeListener<FilmFilter>) c -> {
            forwardIsEmpty.setValue(!forwardFilterList.isEmpty());
        });
    }

    public void clearBackward() {
        backwardFilterList.clear();
    }

    public void clearForward() {
        forwardFilterList.clear();
    }

    public ObservableList<FilmFilter> getBackwardFilterList() {
        return backwardFilterList;
    }

    public ObservableList<FilmFilter> getForwardFilterList() {
        return forwardFilterList;
    }

    public BooleanProperty backwardIsEmptyProperty() {
        return backwardIsEmpty;
    }

    public BooleanProperty forwardIsEmptyProperty() {
        return forwardIsEmpty;
    }

    public void goBackward() {
        if (getBackwardFilterList().size() <= 1) {
            // dann gibts noch keine oder ist nur die aktuelle Einstellung drin
            return;
        }

        FilmFilter sf = getBackwardFilterList().remove(getBackwardFilterList().size() - 1); // ist die aktuelle Einstellung
        forwardFilterList.add(sf);
        sf = backwardFilterList.remove(backwardFilterList.size() - 1); // ist die davor
        ProgData.getInstance().filmFilterWorker.setActFilterSettings(sf);
    }

    public void goForward() {
        if (forwardFilterList.isEmpty()) {
            // dann gibts keine
            return;
        }

        final FilmFilter sf = forwardFilterList.remove(forwardFilterList.size() - 1);
        ProgData.getInstance().filmFilterWorker.setActFilterSettings(sf);
    }


    public void addBackward() {
        final FilmFilter sf = new FilmFilter();
        ProgData.getInstance().filmFilterWorker.getActFilterSettings().copyTo(sf);
        if (backwardFilterList.isEmpty()) {
            backwardFilterList.add(sf);
            return;
        }

        FilmFilter sfB = backwardFilterList.get(backwardFilterList.size() - 1);
        if (sf.isSame(sfB)) {
            // dann hat sich nichts geändert (z.B. mehrmals gelöscht)
            return;
        }

        // jetzt erst mal checken, ob das Feld nur "erweitert" wurde, also weitergetippt wurde
        if (!sf.isThemeIsExact() && checkText(sfB.themeProperty(), sf.themeProperty(), sfB, sf, thema)) {
            setFalse();
            thema = true;
            return;
        }
        if (checkText(sfB.themeTitleProperty(), sf.themeTitleProperty(), sfB, sf, themaTitle)) {
            setFalse();
            themaTitle = true;
            return;
        }
        if (checkText(sfB.titleProperty(), sf.titleProperty(), sfB, sf, title)) {
            setFalse();
            title = true;
            return;
        }
        if (checkText(sfB.somewhereProperty(), sf.somewhereProperty(), sfB, sf, somewhere)) {
            setFalse();
            somewhere = true;
            return;
        }
        if (checkText(sfB.urlProperty(), sf.urlProperty(), sfB, sf, url)) {
            setFalse();
            url = true;
            return;
        }

        // dann wars kein Textfilter
        backwardFilterList.add(sf);
    }

    private boolean checkText(StringProperty old, StringProperty nnew, FilmFilter oldSf, FilmFilter newSf,
                              boolean check) {
        // wenn sich nur ein Teil im Suchtext geändert hat, wird nur das aktualisiert, nicht ein neuer Back angelegt
        if (old.get().equals(nnew.get())) {
            return false;
        }
        if (check && !old.get().isEmpty() && !nnew.get().isEmpty() &&
                (old.get().contains(nnew.get()) || nnew.get().contains(old.get()))) {
            // dann hat sich nur ein Teil geändert und wird ersetzt
            old.setValue(nnew.getValue());
        } else {
            backwardFilterList.add(newSf);
        }
        return true;
    }


    private void setFalse() {
        thema = false;
        themaTitle = false;
        title = false;
        somewhere = false;
        url = false;
    }


}
