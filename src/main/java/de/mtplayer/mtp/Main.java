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

import de.p2tools.p2Lib.guiTools.LinuxJavaFx;
import de.p2tools.p2Lib.tools.net.Proxy;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;

import java.awt.*;

public class Main {

    /*
     * Aufruf: java -jar mtplayer [Pfad zur Konfigdatei, sonst homeverzeichnis] [Schalter]
     *
     * Programmschalter:
     * -d,  --debug         show debug info
     * -h,  --help          show help
     * -t,  --time          show timekeeping info
     * -p,  --path <arg>    path of configuration file
     * -v,  --version       show version
     *
     */

    /**
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        new Main().start(args);
    }

    private void start(String... args) {
        if (LinuxJavaFx.hasJavaFx()) {

            Proxy.proxyAuthentication();
            new AppParameter().processArgs(args);
            startGuiFxMode(args);

        }
    }


    private void startGuiFxMode(final String[] args) {
        // JavaFX stuff
        Platform.setImplicitExit(false);

//        todo wirft einen Fehler und funktioniert nicht mit FX
//        if (SystemInfo.isUnix()) {
//            LinuxJavaFx.setupX11WindowManagerClassName(ProgConst.PROGRAMNAME);
//        }

        checkMemory(args);

        MTPlayer.main(args);
//        Application.launch(MTPlayer.class, args);
    }

    private void checkMemory(final String[] args) {
        final long maxMem = Runtime.getRuntime().maxMemory();

        // todo muss abgestuft werden -> laden der Filmliste einschränken
        if (maxMem < 500 * FileUtils.ONE_MB) {
            if (GraphicsEnvironment.isHeadless()) {
                System.err.println("Die VM hat nicht genügend Arbeitsspeicher zugewiesen bekommen.");
                System.err.println("Nutzen Sie den Startparameter -Xmx512M für Minimumspeicher");
            } else {
                MTStartErrorWindow.main(args);
            }
        }
    }

}