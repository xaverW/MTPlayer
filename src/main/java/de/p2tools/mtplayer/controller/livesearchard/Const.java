/*
angepasste Version aus:
https://github.com/mediathekview/MLib
*/

package de.p2tools.mtplayer.controller.livesearchard;

public class Const {
    /**
     * @deprecated
     */
    @Deprecated
    public static final String VERSION = "13";
    public static final String VERSION_FILMLISTE = "3";
    public static final String PROGRAMMNAME = "MSearch";
    //    public static final String USER_AGENT_DEFAULT = "MSearch" + Functions.getProgVersionString();
    public static final String ADRESSE_FILMLISTEN_SERVER_DIFF = "http://res.mediathekview.de/diff.xml";
    public static final String ADRESSE_FILMLISTEN_SERVER_AKT = "http://res.mediathekview.de/akt.xml";
    public static final int STRING_BUFFER_START_BUFFER = 65536;
    public static final String FORMAT_ZIP = ".zip";
    public static final String FORMAT_XZ = ".xz";
    public static final String RTMP_PRTOKOLL = "rtmp";
    public static final String RTMP_FLVSTREAMER = "-r ";
    public static final int ALTER_FILMLISTE_SEKUNDEN_FUER_AUTOUPDATE = 10800;
    public static final String TIME_MAX_AGE_FOR_DIFF = "09";
    public static final int MAX_BESCHREIBUNG = 400;
    public static final String DREISAT = "3Sat";
    public static final String ARD = "ARD";
    public static final String ARTE_DE = "ARTE.DE";
    public static final String ARTE_FR = "ARTE.FR";
    public static final String BR = "BR";
    public static final String DW = "DW";
    public static final String HR = "HR";
    public static final String KIKA = "KiKA";
    public static final String MDR = "MDR";
    public static final String NDR = "NDR";
    public static final String ORF = "ORF";
    public static final String PHOENIX = "PHOENIX";
    public static final String RBB = "RBB";
    public static final String SR = "SR";
    public static final String SRF = "SRF";
    public static final String SRF_PODCAST = "SRF.Podcast";
    public static final String SWR = "SWR";
    public static final String WDR = "WDR";
    public static final String ZDF = "ZDF";
    public static final String ZDF_TIVI = "ZDF-tivi";
    public static final String[] SENDER = new String[]{"3Sat", "ARD", "ARTE.DE", "ARTE.FR", "BR", "DW", "HR", "KiKA", "MDR", "NDR", "ORF", "PHOENIX", "RBB", "SR", "SRF", "SRF.Podcast", "SWR", "WDR", "ZDF", "ZDF-tivi"};

    public Const() {
    }
}
