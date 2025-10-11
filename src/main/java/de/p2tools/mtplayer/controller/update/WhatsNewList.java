package de.p2tools.mtplayer.controller.update;

import de.p2tools.mtplayer.controller.config.ProgConfig;
import de.p2tools.p2lib.dialogs.WhatsNewInfo;
import de.p2tools.p2lib.tools.date.P2LDateFactory;

import java.time.LocalDate;
import java.util.ArrayList;

public class WhatsNewList extends ArrayList<WhatsNewInfo> {
    private LocalDate maxDate = LocalDate.MIN;

    public WhatsNewList() {
        addWhatsNew();
        this.forEach(whatsNewInfo -> {
            if (whatsNewInfo.getDate().isAfter(maxDate)) {
                maxDate = whatsNewInfo.getDate();
            }
        });
    }

    public LocalDate getMaxDate() {
        return maxDate;
    }

    public void setLastShown() {
        ProgConfig.SYSTEM_WHATS_NEW_DATE_LAST_SHOWN.setValue(P2LDateFactory.toStringR(maxDate));
    }

    public ArrayList<WhatsNewInfo> getOnlyNews() {
        final ArrayList<WhatsNewInfo> list = new ArrayList<>();
        final LocalDate lastDate = P2LDateFactory.fromStringR(ProgConfig.SYSTEM_WHATS_NEW_DATE_LAST_SHOWN.getValueSafe());
        this.forEach(whatsNewInfo -> {
            if (lastDate.isBefore(whatsNewInfo.getDate())) {
                list.add(whatsNewInfo);
            }
        });
        return list;
    }

    private void addWhatsNew() {
        WhatsNewInfo whatsNewInfo = new WhatsNewInfo(LocalDate.of(2025, 4, 28),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_bookmark.png",

                "Bookmarks",
                "Die Bookmarks können in einem eigenen Dialog " +
                        "angezeigt werden. Dort können die Filme auch " +
                        "angesehen und gespeichert werden. Es können auch Kommentare " +
                        "zu den Bookmarks hinzugefügt werden.", 100);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2025, 7, 29),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_small-filter.png",

                "Einfacher Filter",
                "Im Tab Filme kann ein einfacher Filter " +
                        "angezeigt werden. Er enthält nur das nötigste und ist " +
                        "übersichtlicher. Umschalten lässt sich das " +
                        "im Film-Menü->Einfacher Filter.", 100);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2025, 8, 25),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_audiolist.png",

                "Audiothek",
                "Jetzt werden auch die Beiträge der ARD-Audiothek " +
                        "angezeigt. Die Beiträge können genauso wie die Filme " +
                        "abgespielt und aufgezeichnet werden. Auch Abos lassen sich " +
                        "anlegen.\n" +
                        "Wer die Audiothek nicht verwenden will, kann sie in den Einstellungen " +
                        "abschalten.\n" +
                        "Wenns Vorschläge dazu gibt, gerne per Mail oder im Forum.", 140);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2025, 10, 11),
                "",
                "Filtervorschläge",
                "In den Textfiltern (Filme/Audios) kann mit einem Doppelklick " +
                        "ein Filtervorschlag eingefügt werden. Die Vorschläge kann man in " +
                        "den \"Einstellungen->Filter\" vorgeben. Die Funktion kann dort auch " +
                        "abgeschaltet werden.", 100);
        add(whatsNewInfo);
    }
}
