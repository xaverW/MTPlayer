/*
 * P2tools Copyright (C) 2023 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */


package de.p2tools.mtplayer.controller.data.abo;

import de.p2tools.mtplayer.controller.film.FilmDataMTP;
import de.p2tools.mtplayer.controller.film.FilmlistMTP;
import de.p2tools.mtplayer.controller.filmFilter.FilmFilterFactory;
import de.p2tools.p2Lib.mtFilm.film.FilmDataXml;
import de.p2tools.p2Lib.mtFilter.FilmFilterCheck;
import de.p2tools.p2Lib.tools.duration.PDuration;

import java.util.Iterator;

public class AboFactory {
    private AboFactory() {
    }

    public static AboData findAboToFilm(FilmDataMTP film, AboList aboList) {
        //liefert ein Abo zu dem Film, auch Abos die ausgeschaltet sind, Film zu klein ist, ...
        if (film.isLive()) {
            //Livestreams gehören nicht in ein Abo
            //und gelöscht wird beim Zuordnen zu allen Filmen dann noch
            return null;
        }

        final AboData aboData = aboList.stream()
                .filter(abo -> FilmFilterFactory.checkFilmWithFilter(
                        abo.fChannel,
                        abo.fTheme,
                        abo.fThemeTitle,
                        abo.fTitle,
                        abo.fSomewhere,
                        film))

                .findFirst()
                .orElse(null);

        return aboData;
    }

    public static synchronized void setAboForFilmlist(FilmlistMTP filmlistMTP, AboList aboList) {
        //hier wird tatsächlich für jeden Film die Liste der Abos durchsucht,
        //braucht länger
        PDuration.counterStart("Abo in Filmliste eintragen");

        // leere Abos löschen, die sind Fehler
        Iterator<AboData> it = aboList.listIterator();
        while (it.hasNext()) {
            AboData aboData = it.next();
            if (aboData.isEmpty()) {
                it.remove();
            }
        }

        if (aboList.isEmpty()) {
            // dann nur die Abos in der Filmliste löschen
            filmlistMTP.parallelStream().forEach(film -> {
                //für jeden Film Abo löschen
                film.arr[FilmDataXml.FILM_ABO_NAME] = "";
                film.setAbo(null);
            });
            return;
        }

        aboList.stream().forEach(abo -> {
            //damit jedes Abo auch einen Namen hat
            if (abo.getName().isEmpty()) {
                abo.setName("Abo " + abo.getNo());
            }
        });

        // das kostet die Zeit!!
        filmlistMTP.parallelStream().forEach(filmDataMTP -> AboFactory.assignAboToFilm(filmDataMTP, aboList));

        PDuration.counterStop("Abo in Filmliste eintragen");
    }

    private static void assignAboToFilm(FilmDataMTP film, AboList aboList) {
        final AboData abo = findAboToFilm(film, aboList);
        if (abo == null) {
            //kein Abo gefunden
            film.arr[FilmDataXml.FILM_ABO_NAME] = "";
            film.setAbo(null);

        } else if (!abo.isActive()) {
            // dann ist das Abo ausgeschaltet
            film.arr[FilmDataXml.FILM_ABO_NAME] = abo.getName() + (" [ausgeschaltet]");
            film.setAbo(null);

        } else if (!FilmFilterCheck.checkMaxDays(abo.getTimeRange(), film.filmDate.getTime())) {
            // dann ist der Film zu alt
            film.arr[FilmDataXml.FILM_ABO_NAME] = abo.getName() + (" [zu alt]");
            film.setAbo(null);

        } else if (!FilmFilterCheck.checkLengthMin(abo.getMinDurationMinute(), film.getDurationMinute())) {
            // dann ist der Film zu kurz
            film.arr[FilmDataXml.FILM_ABO_NAME] = abo.getName() + (" [zu kurz]");
            film.setAbo(null);

        } else if (!FilmFilterCheck.checkLengthMax(abo.getMaxDurationMinute(), film.getDurationMinute())) {
            // dann ist der Film zu lang
            film.arr[FilmDataXml.FILM_ABO_NAME] = abo.getName() + (" [zu lang]");
            film.setAbo(null);

        } else {
            //nur dann passt er :)
            film.arr[FilmDataXml.FILM_ABO_NAME] = abo.getName();
            film.setAbo(abo);
        }
    }
}
