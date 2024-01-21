module mtplayer {
    opens de.p2tools.mtplayer;
    exports de.p2tools.mtplayer;

    opens de.p2tools.mtplayer.controller.data.setdata;
    opens de.p2tools.mtplayer.controller.data.download;
    opens de.p2tools.mtplayer.controller.data.cleaningdata;
    opens de.p2tools.mtplayer.controller.data.utdata;
    opens de.p2tools.mtplayer.controller.history;
    opens de.p2tools.mtplayer.controller.mediadb;
    opens de.p2tools.mtplayer.controller.data.abo;
    opens de.p2tools.mtplayer.controller.data.blackdata;
    exports de.p2tools.mtplayer.controller.config;
    opens de.p2tools.mtplayer.controller.config;

    requires de.p2tools.p2lib;
    requires javafx.controls;
    requires org.controlsfx.controls;

    requires java.logging;
    requires java.desktop;

    requires commons.cli;
    requires com.fasterxml.jackson.core;
    requires org.tukaani.xz;

    requires okhttp3;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
}

