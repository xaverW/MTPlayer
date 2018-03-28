/*
 * MTPlayer Copyright (C) 2017 W. Xaver W.Xaver[at]googlemail.com
 * https://www.p2tools.de/mtplayer/
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

package de.mtplayer.mtp.controller.data.film;

import de.mtplayer.mLib.tools.Duration;
import de.mtplayer.mLib.tools.Log;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.tools.filmListFilter.FilmlistBlackFilter;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("serial")
public class FilmList extends SimpleListProperty<Film> {

    public int nr = 1;
    public String[] metaDaten = new String[]{"", "", "", "", ""};
    private final static String DATUM_ZEIT_FORMAT = "dd.MM.yyyy, HH:mm";
    private final SimpleDateFormat sdf = new SimpleDateFormat(DATUM_ZEIT_FORMAT);

    public String[] sender = {""};
    public String[][] themenPerSender = {{""}};

    private FilteredList<Film> filteredList = null;
    private SortedList<Film> sortedList = null;

    public FilmList() {
        super(FXCollections.observableArrayList());
    }

    public synchronized void filterList() {
        // damit wird die Filmlist gegen die Blacklist geprüft:
        // Filmliste geladen, add Black, ConfigDialog, Filter blkBtn
        FilmlistBlackFilter.getFilmListBlackFiltered();
    }

    public SortedList<Film> getSortedList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<Film>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return sortedList;
    }

    public FilteredList<Film> getFilteredList() {
        if (sortedList == null || filteredList == null) {
            filteredList = new FilteredList<Film>(this, p -> true);
            sortedList = new SortedList<>(filteredList);
        }
        return filteredList;
    }

    public synchronized void filterdListSetPred(Predicate<Film> predicate) {
        filteredList.setPredicate(predicate);
    }

    public synchronized void saveFilm(Film film, SetData pSet) {
        FilmTools.saveFilm(film, pSet);
    }

    public synchronized void saveFilm(ArrayList<Film> liste, SetData pSet) {
        FilmTools.saveFilm(liste, pSet);
    }


    public synchronized boolean importFilmliste(Film film) {
        // hier nur beim Laden aus einer fertigen Filmliste mit der GUI
        // die Filme sind schon sortiert, nur die Nummer muss noch ergänzt werden
        film.nr = nr++;
        return addInit(film);
    }

    private void addHash(Film f, HashSet<String> hash, boolean index) {
        if (f.arr[FilmXml.FILM_SENDER].equals(Const.KIKA)) {
            // beim KIKA ändern sich die URLs laufend
            hash.add(f.arr[FilmXml.FILM_THEMA] + f.arr[FilmXml.FILM_TITEL]);
        } else if (index) {
            hash.add(f.getIndex());
        } else {
            hash.add(f.getUrlForHash());
        }
    }

    public synchronized void updateListe(FilmList listeEinsortieren,
                                         boolean index /* Vergleich über Index, sonst nur URL */,
                                         boolean ersetzen) {
        // in eine vorhandene Liste soll eine andere Filmliste einsortiert werden
        // es werden nur Filme die noch nicht vorhanden sind, einsortiert
        // "ersetzen": true: dann werden gleiche (index/URL) in der Liste durch neue ersetzt
        final HashSet<String> hash = new HashSet<>(listeEinsortieren.size() + 1, 0.75F);

        if (ersetzen) {
            listeEinsortieren.forEach((Film f) -> addHash(f, hash, index));

            final Iterator<Film> it = iterator();
            while (it.hasNext()) {
                final Film f = it.next();
                if (f.arr[FilmXml.FILM_SENDER].equals(Const.KIKA)) {
                    // beim KIKA ändern sich die URLs laufend
                    if (hash.contains(f.arr[FilmXml.FILM_THEMA] + f.arr[FilmXml.FILM_TITEL])) {
                        it.remove();
                    }
                } else if (index) {
                    if (hash.contains(f.getIndex())) {
                        it.remove();
                    }
                } else if (hash.contains(f.getUrlForHash())) {
                    it.remove();
                }
            }

            listeEinsortieren.forEach(this::addInit);
        } else {
            // ==============================================
            forEach(f -> addHash(f, hash, index));

            for (final Film f : listeEinsortieren) {
                if (f.arr[FilmXml.FILM_SENDER].equals(Const.KIKA)) {
                    if (!hash.contains(f.arr[FilmXml.FILM_THEMA] + f.arr[FilmXml.FILM_TITEL])) {
                        addInit(f);
                    }
                } else if (index) {
                    if (!hash.contains(f.getIndex())) {
                        addInit(f);
                    }
                } else if (!hash.contains(f.getUrlForHash())) {
                    addInit(f);
                }
            }
        }
        hash.clear();
    }

    int countDouble = 0;

    public synchronized void markGeoBlocked() {
        // geblockte Filme markieren
        this.parallelStream().forEach((Film f) -> f.setGeoBlocked());
    }


    public synchronized void markFilms() {
        // doppelte Filme (URL) markieren
        // viele Filme sind bei mehreren Sendern vorhanden
        final HashSet<String> hash = new HashSet<>(size(), 0.75F);

        this.parallelStream().forEach((Film f) -> {
            f.setGeoBlocked();
            f.setInFuture();
            if (!hash.contains(f.getUrl())) {
                f.setDoubleUrl(false);
                hash.add(f.getUrl());
            } else {
                ++countDouble;
                f.setDoubleUrl(true);
            }
        });
        Log.sysLog("Anzahl doppelte Filme: " + countDouble);
        hash.clear();
    }

    private boolean addInit(Film film) {
        film.init();
        return add(film);
    }

    @Override
    public synchronized void clear() {
        nr = 1;
        super.clear();
    }

    public synchronized void sort() {
        Collections.sort(this);
        // und jetzt noch die Nummerierung in Ordnung bringen
        int i = 1;
        for (final Film film : this) {
            film.nr = i++;
        }
    }

    public synchronized void setMeta(FilmList filmList) {
        System.arraycopy(filmList.metaDaten, 0, metaDaten, 0, FilmListXml.MAX_ELEM);
    }

    public synchronized Film getFilmByUrl(final String url) {
        final Optional<Film> opt =
                parallelStream().filter(f -> f.arr[FilmXml.FILM_URL].equalsIgnoreCase(url)).findAny();
        return opt.orElse(null);
    }

    public synchronized void getThema(String sender, LinkedList<String> liste) {
        stream().filter(film -> film.arr[FilmXml.FILM_SENDER].equals(sender))
                .filter(film -> !liste.contains(film.arr[FilmXml.FILM_THEMA]))
                .forEach(film -> liste.add(film.arr[FilmXml.FILM_THEMA]));
    }

    public synchronized Film getFilmByUrl_klein_hoch_hd(String url) {
        // Problem wegen gleicher URLs
        // wird versucht, einen Film mit einer kleinen/Hoher/HD-URL zu finden
        Film ret = null;
        return parallelStream().filter(f ->

                f.arr[FilmXml.FILM_URL].equals(url) ||
                        f.getUrlFuerAufloesung(FilmXml.AUFLOESUNG_HD).equals(url) ||
                        f.getUrlFuerAufloesung(FilmXml.AUFLOESUNG_KLEIN).equals(url)

        ).findFirst().orElse(null);

    }

    public synchronized String genDate() {
        // Tag, Zeit in lokaler Zeit wann die Filmliste erstellt wurde
        // in der Form "dd.MM.yyyy, HH:mm"
        String ret;
        String date;
        if (metaDaten[FilmListXml.FILMLISTE_DATUM_GMT_NR].isEmpty()) {
            // noch eine alte Filmliste
            ret = metaDaten[FilmListXml.FILMLISTE_DATUM_NR];
        } else {
            date = metaDaten[FilmListXml.FILMLISTE_DATUM_GMT_NR];
            final SimpleDateFormat sdf_ = new SimpleDateFormat(DATUM_ZEIT_FORMAT);
            sdf_.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
            Date filmDate = null;
            try {
                filmDate = sdf_.parse(date);
            } catch (final ParseException ignored) {
            }
            if (filmDate == null) {
                ret = metaDaten[FilmListXml.FILMLISTE_DATUM_GMT_NR];
            } else {
                final FastDateFormat formatter = FastDateFormat.getInstance(DATUM_ZEIT_FORMAT);
                ret = formatter.format(filmDate);
            }
        }
        return ret;
    }

    /**
     * Get the age of the film list.
     *
     * @return Age in seconds.
     */
    public int getAge() {
        int ret = 0;
        final Date now = new Date(System.currentTimeMillis());
        final Date filmDate = getAgeAsDate();
        if (filmDate != null) {
            ret = Math.round((now.getTime() - filmDate.getTime()) / (1000));
            if (ret < 0) {
                ret = 0;
            }
        }
        return ret;
    }

    /**
     * Get the age of the film list.
     *
     * @return Age as a {@link java.util.Date} object.
     */
    public Date getAgeAsDate() {
        String date;
        if (!metaDaten[FilmListXml.FILMLISTE_DATUM_GMT_NR].isEmpty()) {
            date = metaDaten[FilmListXml.FILMLISTE_DATUM_GMT_NR];
            sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        } else {
            date = metaDaten[FilmListXml.FILMLISTE_DATUM_NR];
        }
        if (date.isEmpty()) {
            // dann ist die Filmliste noch nicht geladen
            return null;
        }

        Date filmDate = null;
        try {
            filmDate = sdf.parse(date);
        } catch (final Exception ignored) {
        }

        return filmDate;
    }

    /**
     * Check if available Filmlist is older than a specified value.
     *
     * @return true if too old or if the list is empty.
     */
    public synchronized boolean isTooOld() {
        return (isEmpty()) || (isOlderThan(Const.ALTER_FILMLISTE_SEKUNDEN_FUER_AUTOUPDATE));
    }

    /**
     * Check if Filmlist is too old for using a diff list.
     *
     * @return true if empty or too old.
     */
    public synchronized boolean isTooOldForDiff() {
        if (isEmpty()) {
            return true;
        }
        try {
            final String dateMaxDiff_str =
                    new SimpleDateFormat("yyyy.MM.dd__").format(new Date()) + Const.TIME_MAX_AGE_FOR_DIFF + ":00:00";
            final Date dateMaxDiff = new SimpleDateFormat("yyyy.MM.dd__HH:mm:ss").parse(dateMaxDiff_str);
            final Date dateFilmliste = getAgeAsDate();
            if (dateFilmliste != null) {
                return dateFilmliste.getTime() < dateMaxDiff.getTime();
            }
        } catch (final Exception ignored) {
        }
        return true;
    }

    /**
     * Check if list is older than specified parameter.
     *
     * @param sekunden The age in seconds.
     * @return true if older.
     */
    public boolean isOlderThan(int sekunden) {
        final int ret = getAge();
        if (ret != 0) {
            Log.sysLog("Die Filmliste ist " + ret / 60 + " Minuten alt");
        }
        return ret > sekunden;
    }

    public synchronized long countNewFilms() {
        return stream().filter(Film::isNewFilm).count();
    }

    /**
     * Erstellt ein StringArray der Themen eines Senders oder wenn "sender" leer, aller Sender. Ist
     * für die Filterfelder in GuiFilme.
     */
    @SuppressWarnings("unchecked")
    public synchronized void themenLaden() {
        Duration.counterStart("Themen in Filmliste suchen");
        final LinkedHashSet<String> senderSet = new LinkedHashSet<>(21);
        // der erste Sender ist ""
        senderSet.add("");

        stream().forEach((film) -> {
            senderSet.add(film.arr[FilmXml.FILM_SENDER]);
        });
        sender = senderSet.toArray(new String[senderSet.size()]);

        // für den Sender "" sind alle Themen im themenPerSender[0]
        final int senderLength = sender.length;
        themenPerSender = new String[senderLength][];
        final TreeSet<String>[] tree = (TreeSet<String>[]) new TreeSet<?>[senderLength];
        final HashSet<String>[] hashSet = (HashSet<String>[]) new HashSet<?>[senderLength];
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
        String filmThema, filmSender;
        for (final Film film : this) {
            filmSender = film.arr[FilmXml.FILM_SENDER];
            filmThema = film.arr[FilmXml.FILM_THEMA];
            // hinzufügen
            if (!hashSet[0].contains(filmThema)) {
                hashSet[0].add(filmThema);
                tree[0].add(filmThema);
            }
            for (int i = 1; i < senderLength; ++i) {
                if (filmSender.equals(sender[i])) {
                    if (!hashSet[i].contains(filmThema)) {
                        hashSet[i].add(filmThema);
                        tree[i].add(filmThema);
                    }
                }
            }
        }
        for (int i = 0; i < themenPerSender.length; ++i) {
            themenPerSender[i] = tree[i].toArray(new String[tree[i].size()]);
            tree[i].clear();
            hashSet[i].clear();
        }

        Duration.counterStop("Themen in Filmliste suchen");
    }

}
