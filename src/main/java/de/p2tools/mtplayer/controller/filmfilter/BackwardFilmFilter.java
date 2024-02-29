package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import javafx.beans.property.StringProperty;

public class BackwardFilmFilter {

    private boolean thema = false, themaTitle = false, title = false, somewhere = false, url = false;

    public void goBackward() {
        // Button back
        if (ProgData.getInstance().filmFilterWorker.getBackwardFilterList().size() <= 1) {
            // dann gibts noch keine oder ist nur die aktuelle Einstellung drin
            return;
        }

        FilmFilter sf = ProgData.getInstance().filmFilterWorker.getBackwardFilterList().remove(ProgData.getInstance().filmFilterWorker.getBackwardFilterList().size() - 1); // ist die aktuelle Einstellung
        ProgData.getInstance().filmFilterWorker.getForwardFilterList().addToList(sf);
        sf = ProgData.getInstance().filmFilterWorker.getBackwardFilterList().remove(ProgData.getInstance().filmFilterWorker.getBackwardFilterList().size() - 1); // ist die davor
        ProgData.getInstance().filmFilterWorker.setActFilterSettings(sf);
    }

    public void goForward() {
        // Button forward
        if (ProgData.getInstance().filmFilterWorker.getForwardFilterList().isEmpty()) {
            // dann gibts keine
            return;
        }

        final FilmFilter sf = ProgData.getInstance().filmFilterWorker.getForwardFilterList().remove(ProgData.getInstance().filmFilterWorker.getForwardFilterList().size() - 1);
        ProgData.getInstance().filmFilterWorker.setActFilterSettings(sf);
    }

    public void addBackward() {
        final FilmFilter actFilter = new FilmFilter();
        ProgData.getInstance().filmFilterWorker.getActFilterSettings().copyTo(actFilter);
        if (ProgData.getInstance().filmFilterWorker.getBackwardFilterList().isEmpty()) {
            ProgData.getInstance().filmFilterWorker.getBackwardFilterList().addToList(actFilter);
            return;
        }

        FilmFilter backwardFilter = ProgData.getInstance().filmFilterWorker.getBackwardFilterList().get(ProgData.getInstance().filmFilterWorker.getBackwardFilterList().size() - 1);
        if (actFilter.isSame(backwardFilter)) {
            // dann hat sich nichts geändert (z.B. mehrmals gelöscht)
            return;
        }

        // jetzt erst mal checken, ob das Feld nur "erweitert" wurde, also weiter getippt wurde
        if (!actFilter.isThemeIsExact() && checkText(backwardFilter.themeProperty(), actFilter.themeProperty(), backwardFilter, actFilter, thema)) {
            setFalse();
            thema = true;
            return;
        }
        if (checkText(backwardFilter.themeTitleProperty(), actFilter.themeTitleProperty(), backwardFilter, actFilter, themaTitle)) {
            setFalse();
            themaTitle = true;
            return;
        }
        if (checkText(backwardFilter.titleProperty(), actFilter.titleProperty(), backwardFilter, actFilter, title)) {
            setFalse();
            title = true;
            return;
        }
        if (checkText(backwardFilter.somewhereProperty(), actFilter.somewhereProperty(), backwardFilter, actFilter, somewhere)) {
            setFalse();
            somewhere = true;
            return;
        }
        if (checkText(backwardFilter.urlProperty(), actFilter.urlProperty(), backwardFilter, actFilter, url)) {
            setFalse();
            url = true;
            return;
        }

        // dann wars kein Textfilter
        ProgData.getInstance().filmFilterWorker.getBackwardFilterList().addToList(actFilter);
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
            ProgData.getInstance().filmFilterWorker.getBackwardFilterList().add(newSf);
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
