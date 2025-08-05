package de.p2tools.mtplayer.controller.filter;

import de.p2tools.mtplayer.controller.config.ProgData;
import javafx.beans.property.StringProperty;

public class FilterBackward {

    private boolean thema = false, themaTitle = false, title = false, somewhere = false, url = false;
    private boolean audio;

    public FilterBackward(boolean audio) {
        this.audio = audio;
    }

    public void goBackward() {
        // Button back
        if (audio) {
            if (ProgData.getInstance().filterWorkerAudio.getBackwardFilterList().size() <= 1) {
                // dann gibts noch keine oder ist nur die aktuelle Einstellung drin
                return;
            }
            FilmFilter sf = ProgData.getInstance().filterWorkerAudio.getBackwardFilterList()
                    .remove(ProgData.getInstance().filterWorkerAudio.getBackwardFilterList().size() - 1); // ist die aktuelle Einstellung

            ProgData.getInstance().filterWorkerAudio.getForwardFilterList().addToList(sf);
            sf = ProgData.getInstance().filterWorkerAudio.getBackwardFilterList()
                    .remove(ProgData.getInstance().filterWorkerAudio.getBackwardFilterList().size() - 1); // ist die davor
            ProgData.getInstance().filterWorkerAudio.setActFilterSettings(sf);

        } else {
            if (ProgData.getInstance().filterWorkerFilm.getBackwardFilterList().size() <= 1) {
                // dann gibts noch keine oder ist nur die aktuelle Einstellung drin
                return;
            }
            FilmFilter sf = ProgData.getInstance().filterWorkerFilm.getBackwardFilterList()
                    .remove(ProgData.getInstance().filterWorkerFilm.getBackwardFilterList().size() - 1); // ist die aktuelle Einstellung

            ProgData.getInstance().filterWorkerFilm.getForwardFilterList().addToList(sf);
            sf = ProgData.getInstance().filterWorkerFilm.getBackwardFilterList()
                    .remove(ProgData.getInstance().filterWorkerFilm.getBackwardFilterList().size() - 1); // ist die davor
            ProgData.getInstance().filterWorkerFilm.setActFilterSettings(sf);
        }
    }

    public void goForward() {
        // Button forward
        if (audio) {
            if (ProgData.getInstance().filterWorkerAudio.getForwardFilterList().isEmpty()) {
                // dann gibts keine
                return;
            }

            final FilmFilter sf = ProgData.getInstance().filterWorkerAudio.getForwardFilterList()
                    .remove(ProgData.getInstance().filterWorkerAudio.getForwardFilterList().size() - 1);
            ProgData.getInstance().filterWorkerAudio.setActFilterSettings(sf);

        } else {
            if (ProgData.getInstance().filterWorkerFilm.getForwardFilterList().isEmpty()) {
                // dann gibts keine
                return;
            }

            final FilmFilter sf = ProgData.getInstance().filterWorkerFilm.getForwardFilterList()
                    .remove(ProgData.getInstance().filterWorkerFilm.getForwardFilterList().size() - 1);
            ProgData.getInstance().filterWorkerFilm.setActFilterSettings(sf);

        }
    }

    public void addBackward() {
        final FilmFilter actFilter = new FilmFilter();
        FilmFilter backwardFilter;
        if (audio) {
            ProgData.getInstance().filterWorkerAudio.getActFilterSettings().copyTo(actFilter);
            if (ProgData.getInstance().filterWorkerAudio.getBackwardFilterList().isEmpty()) {
                ProgData.getInstance().filterWorkerAudio.getBackwardFilterList().addToList(actFilter);
                return;
            }
            backwardFilter = ProgData.getInstance().filterWorkerAudio.getBackwardFilterList()
                    .get(ProgData.getInstance().filterWorkerFilm.getBackwardFilterList().size() - 1);

        } else {
            ProgData.getInstance().filterWorkerFilm.getActFilterSettings().copyTo(actFilter);
            if (ProgData.getInstance().filterWorkerFilm.getBackwardFilterList().isEmpty()) {
                ProgData.getInstance().filterWorkerFilm.getBackwardFilterList().addToList(actFilter);
                return;
            }
            backwardFilter = ProgData.getInstance().filterWorkerFilm.getBackwardFilterList()
                    .get(ProgData.getInstance().filterWorkerFilm.getBackwardFilterList().size() - 1);
        }

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
        if (audio) {
            ProgData.getInstance().filterWorkerAudio.getBackwardFilterList().addToList(actFilter);
        } else {
            ProgData.getInstance().filterWorkerFilm.getBackwardFilterList().addToList(actFilter);
        }
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
            if (audio) {
                ProgData.getInstance().filterWorkerAudio.getBackwardFilterList().add(newSf);
            } else {
                ProgData.getInstance().filterWorkerFilm.getBackwardFilterList().add(newSf);
            }
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
