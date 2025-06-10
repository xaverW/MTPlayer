/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de
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

package de.p2tools.mtplayer.controller.film;

import de.p2tools.p2lib.mediathek.film.P2FilmlistFactory;
import de.p2tools.p2lib.mediathek.filmdata.FilmData;
import de.p2tools.p2lib.mediathek.filmdata.FilmDataXml;
import de.p2tools.p2lib.mediathek.filmdata.Filmlist;
import de.p2tools.p2lib.mediathek.filmdata.FilmlistXml;
import de.p2tools.p2lib.tools.duration.P2Duration;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.*;
import java.util.function.Predicate;

public class FilmListMTP extends Filmlist<FilmDataMTP> {

    public String[][] themePerChannel = {{""}};

    private FilteredList<FilmDataMTP> filteredList = null;
    private SortedList<FilmDataMTP> sortedList = null;

    public FilmListMTP() {
    }

    @Override
    public FilmDataMTP getNewElement() {
        return new FilmDataMTP();
    }

    @Override
    public SortedList<FilmDataMTP> getSortedList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<FilmDataMTP>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    @Override
    public FilteredList<FilmDataMTP> getFilteredList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<FilmDataMTP>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return filteredList;
    }

    @Override
    public synchronized void filteredListSetPred(Predicate<FilmData> predicate) {
        getFilteredList().setPredicate(predicate);
    }

//    @Override
//    public String getFilmlistId() {
//        return metaData[FilmlistXml.FILMLIST_ID_NR];
//    }

//    @Override
//    public synchronized boolean importFilmOnlyWithNr(FilmDataMTP film) {
//        // hier nur beim Laden aus einer fertigen Filmliste mit der GUI
//        // die Filme sind schon sortiert, nur die Nummer muss noch ergänzt werden
//        film.no = nr++;
//        return add(film);
//    }

//    @Override
//    public synchronized void markGeoBlocked() {
//        // geblockte Filme markieren
//        this.parallelStream().forEach((FilmData f) -> f.setGeoBlocked());
//    }

    @Override
    public synchronized int markFilms() {
        // läuft direkt nach dem Laden der Filmliste!
        // doppelte Filme (URL), Geo, InFuture markieren
        // viele Filme sind bei mehreren Sendern vorhanden
        return FilmToolsFactory.markFilms(this);
    }

    @Override
    public synchronized void clear() {
        nr = 1;
        super.clear();
    }

    @Override
    public synchronized void sort() {
        Collections.sort(this);
        // und jetzt noch die Nummerierung in Ordnung bringen
        int i = 1;
        for (final FilmData film : this) {
            film.no = i++;
        }
    }

    public synchronized void setMeta(FilmListMTP filmlist) {
        System.arraycopy(filmlist.metaData, 0, metaData, 0, FilmlistXml.MAX_ELEM);
    }

    @Override
    public synchronized FilmDataMTP getFilmByUrl(final String url) {
        final Optional<FilmDataMTP> opt =
                parallelStream().filter(f -> f.arr[FilmDataXml.FILM_URL].equalsIgnoreCase(url)).findAny();
        return opt.orElse(null);
    }

    @Override
    public synchronized void getTheme(String sender, LinkedList<String> list) {
        stream().filter(film -> film.arr[FilmDataXml.FILM_CHANNEL].equals(sender))
                .filter(film -> !list.contains(film.arr[FilmDataXml.FILM_THEME]))
                .forEach(film -> list.add(film.arr[FilmDataXml.FILM_THEME]));
    }

    @Override
    public synchronized FilmDataMTP getFilmByUrl_small_high_hd(String url) {
        // Problem wegen gleicher URLs
        // wird versucht, einen Film mit einer kleinen/Hoher/HD-URL zu finden
        return parallelStream().filter(f ->
                f.arr[FilmDataXml.FILM_URL].equals(url) ||
                        f.getUrlForResolution(FilmData.RESOLUTION_HD).equals(url) ||
                        f.getUrlForResolution(FilmData.RESOLUTION_SMALL).equals(url)
        ).findFirst().orElse(null);
    }

    @Override
    public synchronized String genDate() {
        // Tag, Zeit in lokaler Zeit wann die Filmliste erstellt wurde
        // in der Form "dd.MM.yyyy, HH:mm"
        return P2FilmlistFactory.genDate(metaData);
    }

    /**
     * Get the age of the film list.
     *
     * @return Age in seconds.
     */
    @Override
    public int getAge() {
        return P2FilmlistFactory.getAge(metaData);
    }

    /**
     * Get the age of the film list.
     *
     * @return Age as a {@link java.util.Date} object.
     */
    public Date getAgeAsDate() {
        return P2FilmlistFactory.getAgeAsDate(metaData);
    }

    /**
     * Check if available Filmlist is older than a specified value.
     *
     * @return true if too old or if the list is empty.
     */
    @Override
    public synchronized boolean isTooOldOrEmpty() {
        return P2FilmlistFactory.isTooOldOrEmpty(this, metaData);
    }

    /**
     * Check if Filmlist is too old for using a diff list.
     *
     * @return true if empty or too old.
     */
    @Override
    public synchronized boolean isTooOldForDiffOrEmpty() {
        return P2FilmlistFactory.isTooOldForDiffOrEmpty(this, metaData);
    }

    /**
     * Check if list is older than specified parameter.
     *
     * @param second The age in seconds.
     * @return true if older.
     */
    @Override
    public boolean isOlderThan(int second) {
        return P2FilmlistFactory.isOlderThan(metaData, second);
    }

    @Override
    public synchronized long countNewFilms() {
        return stream().filter(FilmData::isNewFilm).count();
    }

    /**
     * Erstellt ein StringArray der Themen eines Senders oder wenn "sender" leer, aller Sender. Ist
     * für die Filterfelder in GuiFilme.
     */
    public synchronized void loadTheme() {
        P2Duration.counterStart("loadTheme");
        final LinkedHashSet<String> senderSet = new LinkedHashSet<>(21);
        // der erste Sender ist ""
        senderSet.add("");
        this.forEach(film -> senderSet.add(film.getChannel()));

        sender = senderSet.toArray(new String[0]);

        // für den Sender "" sind alle Themen im themenPerSender[0]
        final int senderLength = sender.length;
        themePerChannel = new String[senderLength][];

        final TreeSet<String>[] tree = (TreeSet<String>[]) new TreeSet<?>[senderLength];
        final HashSet<String>[] hashSet = (HashSet<String>[]) new HashSet<?>[senderLength]; // wäre nicht nötig ist aber so fast doppelt so schnell
        for (int i = 0; i < tree.length; ++i) {
            // tree[i] = new TreeSet<>(GermanStringSorter.getInstance());
            // das Sortieren passt nicht richtig zum Filter!
            // oder die Sortierung passt nicht zum User
            // ist so nicht optimal aber ist 10x !! schneller

            tree[i] = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            tree[i].add("");
            hashSet[i] = new HashSet<>();
        }

        // alle Themen
        String filmTheme, filmChannel;
        for (final FilmData film : this) {
            filmChannel = film.arr[FilmDataXml.FILM_CHANNEL];
            filmTheme = film.arr[FilmDataXml.FILM_THEME];
            // hinzufügen
            if (!hashSet[0].contains(filmTheme)) {
                hashSet[0].add(filmTheme);
                tree[0].add(filmTheme);
            }

            for (int i = 1; i < senderLength; ++i) {
                if (filmChannel.equals(sender[i])) {
                    if (!hashSet[i].contains(filmTheme)) {
                        hashSet[i].add(filmTheme);
                        tree[i].add(filmTheme);
                    }
                }
            }
        }

        for (int i = 0; i < themePerChannel.length; ++i) {
            themePerChannel[i] = tree[i].toArray(new String[0]);
            tree[i].clear();
            hashSet[i].clear();
        }

        P2Duration.counterStop("loadTheme");
    }
}
