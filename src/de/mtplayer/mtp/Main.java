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
package de.mtplayer.mtp;

import de.mtplayer.mLib.tools.SystemInfo;
import de.mtplayer.mtp.controller.ProgStart;
import de.mtplayer.mtp.controller.config.Const;
import de.mtplayer.mtp.controller.config.Daten;
import de.p2tools.p2Lib.tools.Log;
import de.p2tools.p2Lib.tools.SysMsg;
import javafx.application.Application;
import javafx.application.Platform;

import java.awt.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class Main {

    private final class ProgramArguments {
        private static final String STARTUPMODE_DEBUG = "-d";
        private static final String STARTUPMODE_VERBOSE = "-v";
    }

    private static final String JAVAFX_CLASSNAME_APPLICATION_PLATFORM = "javafx.application.Platform";
    private static final String HTTP_PROXY_USER = "http.proxyUser";
    private static final String HTTP_PROXY_PW = "http.proxyPassword";
    private static final String LOG_TEXT_PROXY_AUTHENTICATION_SUCESSFUL = "Proxy Authentication: (%s)";
    private static final String LOG_TEXT_PROXY_AUTHENTICATION_NOT_CONFIGURED = "Proxy Authentication: not configured";
    private static final String LOG_TEXT_PROXY_PASSWORD_NOT_SET = "Proxy Authentication: Password is not set";
    private static final String LOG_TEXT_PROXY_AUTHENTICATION_CANNOT_ACCESS_PROXY_USER_PROXY_PW = "Proxy Authentication: cannot access proxyUser / proxyPassword";
    private static final String X11_AWT_APP_CLASS_NAME = "awtAppClassName";
    private static final String ERROR_NO_JAVAFX_INSTALLED = "JavaFX wurde nicht im Klassenpfad gefunden. %n Stellen Sie sicher, dass Sie "
            + "ein Java JRE ab Version 8 benutzen. %n Falls Sie Linux nutzen, installieren Sie das openjfx-Paket ihres "
            + "Package-Managers,%n oder nutzen Sie eine eigene JRE-Installation.%n";
    public static final String TEXT_LINE = "===========================================";

    /**
     * Tests if javafx is in the classpath by loading a well known class.
     */
    private static boolean hasJavaFx() {
        try {
            Class.forName(JAVAFX_CLASSNAME_APPLICATION_PLATFORM);
            return true;

        } catch (final ClassNotFoundException e) {
            System.out.println(TEXT_LINE);
            System.out.printf(ERROR_NO_JAVAFX_INSTALLED);
            System.out.println(TEXT_LINE);

            return false;
        }
    }

    /*
     * Aufruf: java -jar mtplayer [Pfad zur Konfigdatei, sonst homeverzeichnis] [Schalter]
     *
     * Programmschalter:
     *
     * -d debug
     * -v Programmversion
     *
     */

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        new Main().start(args);
    }

    private void start(String... args) {
        if (hasJavaFx()) {

            proxyAuthentication();

            if (args != null) {
                processArgs(args);
            }
            startGuiFxMode(args);
        }
    }


    private void startGuiFxMode(final String[] args) {
        // JavaFX stuff
        Platform.setImplicitExit(false);

        if (SystemInfo.isUnix()) {
            setupX11WindowManagerClassName();
        }

        Application.launch(MTFx.class, args);
    }

    /**
     * Setup the X11 window manager WM_CLASS hint. Enables e.g. GNOME to determine application name
     * and to enable app specific functionality.
     */
    private void setupX11WindowManagerClassName() {
        try {
            final Toolkit xToolkit = Toolkit.getDefaultToolkit();
            final java.lang.reflect.Field awtAppClassNameField = xToolkit.getClass().getDeclaredField(X11_AWT_APP_CLASS_NAME);
            awtAppClassNameField.setAccessible(true);
            awtAppClassNameField.set(xToolkit, Const.PROGRAMMNAME);
        } catch (final Exception ignored) {
            System.err.println("Couldn't set awtAppClassName");
        }
    }

    private void processArgs(final String... aArguments) {
        for (String argument : aArguments) {
            argument = argument.toLowerCase();
            switch (argument) {
                case ProgramArguments.STARTUPMODE_VERBOSE:
                    EventQueue.invokeLater(() -> {
                        ProgStart.startMeldungen();
                        Log.endMsg();
                        System.exit(0);
                    });
                    break;

                case ProgramArguments.STARTUPMODE_DEBUG:
                    Daten.debug = true;
                    break;

            }
        }
    }

    private void proxyAuthentication() {
        try {
            final String prxUser = System.getProperty(HTTP_PROXY_USER, null);
            final String prxPassword = System.getProperty(HTTP_PROXY_PW, null);
            if (prxUser != null && prxPassword != null) {
                final PasswordAuthentication authenticator = new PasswordAuthentication(prxUser, prxPassword.toCharArray());
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return authenticator;
                    }
                });
                SysMsg.sysMsg(String.format(LOG_TEXT_PROXY_AUTHENTICATION_SUCESSFUL, prxUser));
            } else if (prxUser != null && prxPassword == null) {
                SysMsg.sysMsg(LOG_TEXT_PROXY_PASSWORD_NOT_SET);
            } else {
                SysMsg.sysMsg(LOG_TEXT_PROXY_AUTHENTICATION_NOT_CONFIGURED);
            }

        } catch (final SecurityException se) {
            SysMsg.sysMsg(LOG_TEXT_PROXY_AUTHENTICATION_CANNOT_ACCESS_PROXY_USER_PROXY_PW + se.toString());
        }
    }
}
