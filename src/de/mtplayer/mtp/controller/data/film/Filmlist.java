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

package de.mtplayer.mtp.controller.data.film;

import de.mtplayer.mtp.controller.config.ProgConst;
import de.mtplayer.mtp.controller.config.ProgData;
import de.mtplayer.mtp.controller.data.SetData;
import de.mtplayer.mtp.tools.filmListFilter.FilmlistBlackFilter;
import de.p2tools.p2Lib.tools.log.PDuration;
import de.p2tools.p2Lib.tools.log.PLog;
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
public class Filmlist extends SimpleListProperty<Film> {

    public int nr = 1;
    public String[] metaData = new String[]{"", "", "", "", ""};
    private final static String DATE_TIME_FORMAT = "dd.MM.yyyy, HH:mm";
    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);

    public String[] sender = {""};
    public String[][] themePerChannel = {{""}};

    private FilteredList<Film> filteredList = null;
    private SortedList<Film> sortedList = null;

    public Filmlist() {
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

    public String getFilmlistId() {
        return metaData[FilmlistXml.FILMLIST_ID_NR];
    }

    public synchronized void saveFilm(Film film, SetData pSet) {
        FilmTools.saveFilm(film, pSet);
    }

    public synchronized void saveFilm(ArrayList<Film> list, SetData pSet) {
        FilmTools.saveFilm(list, pSet);
    }


    public synchronized boolean importFilmlist(Film film) {
        // hier nur beim Laden aus einer fertigen Filmliste mit der GUI
        // die Filme sind schon sortiert, nur die Nummer muss noch ergänzt werden
        film.nr = nr++;
        return addInit(film);
    }

    private void addHash(Film f, HashSet<String> hash, boolean index) {
        if (f.arr[FilmXml.FILM_CHANNEL].equals(ProgConst.KIKA)) {
            // beim KIKA ändern sich die URLs laufend
            hash.add(f.arr[FilmXml.FILM_THEME] + f.arr[FilmXml.FILM_TITLE]);
        } else if (index) {
            hash.add(f.getIndex());
        } else {
            hash.add(f.getUrlForHash());
        }
    }

    public synchronized void updateList(Filmlist addList,
                                        boolean index /* Vergleich über Index, sonst nur URL */,
                                        boolean replace) {
        // in eine vorhandene Liste soll eine andere Filmliste einsortiert werden
        // es werden nur Filme die noch nicht vorhanden sind, einsortiert
        // "ersetzen": true: dann werden gleiche (index/URL) in der Liste durch neue ersetzt
        final HashSet<String> hash = new HashSet<>(addList.size() + 1, 0.75F);

        if (replace) {
            addList.forEach((Film f) -> addHash(f, hash, index));

            final Iterator<Film> it = iterator();
            while (it.hasNext()) {
                final Film f = it.next();
                if (f.arr[FilmXml.FILM_CHANNEL].equals(ProgConst.KIKA)) {
                    // beim KIKA ändern sich die URLs laufend
                    if (hash.contains(f.arr[FilmXml.FILM_THEME] + f.arr[FilmXml.FILM_TITLE])) {
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

            addList.forEach(this::addInit);
        } else {
            // ==============================================
            forEach(f -> addHash(f, hash, index));

            for (final Film f : addList) {
                if (f.arr[FilmXml.FILM_CHANNEL].equals(ProgConst.KIKA)) {
                    if (!hash.contains(f.arr[FilmXml.FILM_THEME] + f.arr[FilmXml.FILM_TITLE])) {
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
        // läuft direkt nach dem Laden der Filmliste!
        // doppelte Filme (URL), Geo, InFuture markieren
        // viele Filme sind bei mehreren Sendern vorhanden

        final HashSet<String> set = new HashSet<>(size(), 0.75F);

        // todo exception parallel?? Unterschied ~10ms (bei Gesamt: 110ms)
        PDuration.counterStart("Filme markieren");
        try {

            this.stream().forEach((Film f) -> {

                f.setGeoBlocked();
                f.setInFuture();

                if (!set.add(f.getUrl())) {
                    ++countDouble;
                    f.setDoubleUrl(true);
//                } else {
//                    f.setDoubleUrl(false);
                }

            });

        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
        PDuration.counterStop("Filme markieren");

        PLog.sysLog("Anzahl doppelte Filme: " + countDouble);
        set.clear();
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

    public synchronized void setMeta(Filmlist filmlist) {
        System.arraycopy(filmlist.metaData, 0, metaData, 0, FilmlistXml.MAX_ELEM);
    }

    public synchronized Film getFilmByUrl(final String url) {
        final Optional<Film> opt =
                parallelStream().filter(f -> f.arr[FilmXml.FILM_URL].equalsIgnoreCase(url)).findAny();
        return opt.orElse(null);
    }

    public synchronized void getTheme(String sender, LinkedList<String> list) {
        stream().filter(film -> film.arr[FilmXml.FILM_CHANNEL].equals(sender))
                .filter(film -> !list.contains(film.arr[FilmXml.FILM_THEME]))
                .forEach(film -> list.add(film.arr[FilmXml.FILM_THEME]));
    }

    public synchronized Film getFilmByUrl_small_high_hd(String url) {
        // Problem wegen gleicher URLs
        // wird versucht, einen Film mit einer kleinen/Hoher/HD-URL zu finden
        Film ret = null;
        return parallelStream().filter(f ->

                f.arr[FilmXml.FILM_URL].equals(url) ||
                        f.getUrlForResolution(Film.RESOLUTION_HD).equals(url) ||
                        f.getUrlForResolution(Film.RESOLUTION_SMALL).equals(url)

        ).findFirst().orElse(null);

    }

    public synchronized String genDate() {
        return genDate(metaData);
    }

    public static synchronized String genDate(String[] metaData) {
        // Tag, Zeit in lokaler Zeit wann die Filmliste erstellt wurde
        // in der Form "dd.MM.yyyy, HH:mm"
        String ret;
        String date;
        if (metaData[FilmlistXml.FILMLIST_DATE_GMT_NR].isEmpty()) {
            // noch eine alte Filmliste
            ret = metaData[FilmlistXml.FILMLIST_DATE_NR];
        } else {
            date = metaData[FilmlistXml.FILMLIST_DATE_GMT_NR];
            final SimpleDateFormat sdf_ = new SimpleDateFormat(DATE_TIME_FORMAT);
            sdf_.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
            Date filmDate = null;
            try {
                filmDate = sdf_.parse(date);
            } catch (final ParseException ignored) {
            }
            if (filmDate == null) {
                ret = metaData[FilmlistXml.FILMLIST_DATE_GMT_NR];
            } else {
                final FastDateFormat formatter = FastDateFormat.getInstance(DATE_TIME_FORMAT);
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
        if (!metaData[FilmlistXml.FILMLIST_DATE_GMT_NR].isEmpty()) {
            date = metaData[FilmlistXml.FILMLIST_DATE_GMT_NR];
            sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        } else {
            date = metaData[FilmlistXml.FILMLIST_DATE_NR];
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
        if (ProgData.debug) {
            // im Debugmodus nie automatisch laden
            return false;
        }

        return (isEmpty()) || (isOlderThan(ProgConst.ALTER_FILMLISTE_SEKUNDEN_FUER_AUTOUPDATE));
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
                    new SimpleDateFormat("yyyy.MM.dd__").format(new Date()) + ProgConst.TIME_MAX_AGE_FOR_DIFF + ":00:00";
            final Date dateMaxDiff = new SimpleDateFormat("yyyy.MM.dd__HH:mm:ss").parse(dateMaxDiff_str);
            final Date dateFilmlist = getAgeAsDate();
            if (dateFilmlist != null) {
                return dateFilmlist.getTime() < dateMaxDiff.getTime();
            }
        } catch (final Exception ignored) {
        }
        return true;
    }

    /**
     * Check if list is older than specified parameter.
     *
     * @param second The age in seconds.
     * @return true if older.
     */
    public boolean isOlderThan(int second) {
        final int ret = getAge();
        if (ret != 0) {
            PLog.sysLog("Die Filmliste ist " + ret / 60 + " Minuten alt");
        }
        return ret > second;
    }

    public synchronized long countNewFilms() {
        return stream().filter(Film::isNewFilm).count();
    }

    /**
     * Erstellt ein StringArray der Themen eines Senders oder wenn "sender" leer, aller Sender. Ist
     * für die Filterfelder in GuiFilme.
     */
    public synchronized void loadTheme() {
        PDuration.counterStart("Themen in Filmliste suchen");
        final LinkedHashSet<String> senderSet = new LinkedHashSet<>(21);
        // der erste Sender ist ""
        senderSet.add("");

        stream().forEach((film) -> {
            senderSet.add(film.arr[FilmXml.FILM_CHANNEL]);
        });
        sender = senderSet.toArray(new String[senderSet.size()]);

        // für den Sender "" sind alle Themen im themenPerSender[0]
        final int senderLength = sender.length;
        themePerChannel = new String[senderLength][];
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
        String filmTheme, filmChannel;
        for (final Film film : this) {
            filmChannel = film.arr[FilmXml.FILM_CHANNEL];
            filmTheme = film.arr[FilmXml.FILM_THEME];
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
            themePerChannel[i] = tree[i].toArray(new String[tree[i].size()]);
            tree[i].clear();
            hashSet[i].clear();
        }

        PDuration.counterStop("Themen in Filmliste suchen");
    }

}
