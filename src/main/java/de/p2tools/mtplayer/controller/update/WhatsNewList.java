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
        WhatsNewInfo whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 11),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_01.png",

                "Doppelte Filme",
                "In den Einstellungen kann zum Suchen von doppelten Filmen jetzt die Suchreihenfolge " +
                        "vorgegeben werden. Es ist jetzt auch möglich, dass doppelte Filme beim Laden der Filmliste " +
                        "gleich ausgeschlossen werden. Es ist jetzt auch möglich, doppelte Filme mit der Blacklist " +
                        "auszuschließen.", 100);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 11),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_02.png",

                "Filme mit Untertitel",
                "Es gibt Filme, die den Untertitel im \"Film\" anzeigen. " +
                        "In den Einstellungen kann angegeben werden " +
                        "ob diese Filme auch als \"Film mit Untertitel\" geführt werden sollen. " +
                        "Für welche Filme das dann zutrifft, kann man hier vorgegeben." +
                        "\n\n" +
                        "Im Kontextmenü in der Tabelle mit den Filmen, gibt es jetzt einen weiteren Menüpunkt. " +
                        "Damit können die Untertitel-Dateien direkt heruntergeladen werden.", 150);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 11),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_03.png",

                "Filmfilter \"Thema exact\"",
                "Es ist jetzt möglich, durch Texteingabe die angezeigte Liste der Themen " +
                        "zu filtern\n" +
                        "Mit ENTER wird das selektierte Theme gewählt. Um ein Thema auszuwählen, können die " +
                        "Courser-Tasten und die Tab-Tasten benutzt werden.", 100);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 11), "/de/p2tools/mtplayer/res/whatsnew/whatsNew_04.png",
                "Infotab Downloadfehler",
                "Im Tab Download gibts bei den Infos einen neuen Reiter: \"Downloadfehler\". Dort " +
                        "werden die Fehlermeldungen von fehlerhaften Downloads angezeigt.", 80);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 11),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_05.png",

                "Proxy-Server",
                "Es ist jetzt möglich, einen Proxy-Server zu verwenden. In den \"Programmeinstellungen->Proxy\" " +
                        "kann er angegeben und eingeschaltet werden. Die Downloads laufen dann über " +
                        "den Proxy-Server.", 70);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 2, 21),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_06.png",

                "Infos der markierten Tabellen-Zeile anzeigen",
                "Die Anzeige der Infos einer markierten Zeile in den Tabellen Filme/Downloads/Abos " +
                        "kann ein- und ausgeschaltet werden. Das ist im Kontextmenü der Tabelle möglich.", 70);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 3, 3),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_07.png",

                "Filmfilter",
                "Die verwendeten Filmfilter werden jetzt gespeichert und sind bei " +
                        "einem Programmneustart wieder vorhanden. " +
                        "Eine Auswahl listet sie auf und können so auch ausgewählt werden.", 70);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 4, 1),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_08.png",

                "Live-Suche",
                "MTPlayer hat jetzt auch eine Live-Suche in den Mediatheken von ARD und ZDF. " +
                        "Damit können Filme mit einem Suchbegriff live gesucht werden. Es ist auch möglich " +
                        "einen Film mit der URL einer Filmseite aus der Mediathek anzulegen. Die Live-Suche kann " +
                        "mit dem Menübutton (oder über das Programmmenü) ein- und ausgeblendet werden.", 110);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 4, 6),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_09.png",

                "Abo mit eigenem Pfad/Dateinamen",
                "In Abos können jetzt ein eigener Pfad/Dateiname zum Speichern vorgegeben " +
                        "werden. Wird das gesetzt, werden die Vorgaben aus dem Set überschrieben. " +
                        "Filme landen im Abo vorgegebenem Pfad mit dem im Abo vorgegebenem " +
                        "Dateinamen.", 100);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 5, 4),
                "/de/p2tools/mtplayer/res/whatsnew/whatsNew_10.png",

                "Rechte Menüleiste",
                "Die rechte Menüleiste kann ein- und ausgeblendet werden, das ist über das Programm-Menü " +
                        "möglich. Ein Klick mit der rechten Maustaste " +
                        "in der rechten Menüleiste blendet sie auch aus.", 70);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 3, 3), "",
                "Was sich sonst noch geändert hat",
                "* Die Filme des Senders \"Radio Bremen TV\" wurden mit dem Sendernamen \"Radio Bremen TV\", " +
                        "\"rbtv\" und \"RBTV\" gelistet. Die werden jetzt zusammengefasst zu dem " +
                        "Sendernamen: \"RBTV\"." +
                        "\n\n" +
                        "* Es gibt ein neues ShortCut zum Anzeigen der Blacklist-Einstellungen: ALT+B" +
                        "\n\n" +
                        "* In der Ersetzungstabelle für Download-Namen (Einstellungen->Download) " +
                        "können jetzt auch RegEx verwendet werden.",
                180);
        add(whatsNewInfo);

        whatsNewInfo = new WhatsNewInfo(LocalDate.of(2024, 4, 6), "",
                "Was sich sonst noch geändert hat",
                "* Bei der Startzeit von Downloads kann jetzt die Zeit von 00:00 bis 23:45 Uhr " +
                        "angegeben werden. Liegt die Zeit in der Vergangenheit wird der Download morgen um " +
                        "diese Zeit gestartet. Z.B. es ist 20:00 Uhr und der Download wird mit Startzeit: 05:00 Uhr " +
                        "gestartet, dann startet der Download morgen um 05:00 Uhr.",
                100);
        add(whatsNewInfo);
    }
}
