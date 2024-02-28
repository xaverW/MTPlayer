package de.p2tools.mtplayer.controller.filmfilter;

import de.p2tools.mtplayer.controller.config.ProgData;
import javafx.beans.property.StringProperty;

public class BackwardFilmFilter {

    private boolean thema = false, themaTitle = false, title = false, somewhere = false, url = false;

    public void goBackward() {
        if (ProgData.getInstance().backwardFilterList.size() <= 1) {
            // dann gibts noch keine oder ist nur die aktuelle Einstellung drin
            return;
        }

        FilmFilter sf = ProgData.getInstance().backwardFilterList.remove(ProgData.getInstance().backwardFilterList.size() - 1); // ist die aktuelle Einstellung
        ProgData.getInstance().forwardFilterList.addBackForward(sf);
        sf = ProgData.getInstance().backwardFilterList.remove(ProgData.getInstance().backwardFilterList.size() - 1); // ist die davor
        ProgData.getInstance().filmFilterWorker.setActFilterSettings(sf);
    }

    public void goForward() {
        if (ProgData.getInstance().forwardFilterList.isEmpty()) {
            // dann gibts keine
            return;
        }

        final FilmFilter sf = ProgData.getInstance().forwardFilterList.remove(ProgData.getInstance().forwardFilterList.size() - 1);
        ProgData.getInstance().filmFilterWorker.setActFilterSettings(sf);
    }

    public void addBackward() {
        final FilmFilter sf = new FilmFilter();
        ProgData.getInstance().filmFilterWorker.getActFilterSettings().copyTo(sf);
        if (ProgData.getInstance().backwardFilterList.isEmpty()) {
            ProgData.getInstance().backwardFilterList.addBackForward(sf);
            return;
        }

        FilmFilter sfB = ProgData.getInstance().backwardFilterList.get(ProgData.getInstance().backwardFilterList.size() - 1);
        if (sf.isSame(sfB)) {
            // dann hat sich nichts geändert (z.B. mehrmals gelöscht)
            return;
        }

        // jetzt erst mal checken, ob das Feld nur "erweitert" wurde, also weiter getippt wurde
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
        ProgData.getInstance().backwardFilterList.addBackForward(sf);
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
            ProgData.getInstance().backwardFilterList.add(newSf);
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
