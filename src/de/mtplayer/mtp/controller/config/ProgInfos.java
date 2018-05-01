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

package de.mtplayer.mtp.controller.config;

import de.mtplayer.mtp.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;

public class ProgInfos {

    private static final String ERROR_CANT_CREATE_FOLDER = "Der Ordner konnte nicht angelegt werden.\n" +
            "Bitte prüfen Sie die Dateirechte.";

    public static String getUserAgent() {
        return ProgConfig.SYSTEM_PARAMETER_USERAGENT.get();
    }


    /**
     * Retrieve the path to the program jar file.
     *
     * @return The program jar file path with a separator added.
     */
    public static String getPathJar() {
        // macht Probleme bei Win und Netzwerkpfaden, liefert dann Absolute Pfade zB. \\VBOXSVR\share\Mediathek\...
        final String pFilePath = "pFile";
        File propFile = new File(pFilePath);
        if (!propFile.exists()) {
            try {
                final CodeSource cS = Main.class.getProtectionDomain().getCodeSource();
                final File jarFile = new File(cS.getLocation().toURI().getPath());
                final String jarDir = jarFile.getParentFile().getPath();
                propFile = new File(jarDir + File.separator + pFilePath);
            } catch (final Exception ignored) {
            }
        }
        String s = propFile.getAbsolutePath().replace(pFilePath, "");
        if (!s.endsWith(File.separator)) {
            s = s + File.separator;
        }
        return s;
    }

    public static String pathProgramIcons() {
        return getPathJar() + ProgConst.VERZEICHNIS_PROGRAMM_ICONS;
    }

    public static String pathSenderIcons() {
        return getPathJar() + ProgConst.VERZEICHNIS_SENDER_ICONS;
    }


    /**
     * Return the path to "mtplayer.xml"
     *
     * @return Path object to mtplayer.xml file
     */
    public Path getXmlFilePath() {
        return ProgInfos.getSettingsDirectory().resolve(ProgConst.CONFIG_FILE);
    }

    /**
     * Liefert den Pfad zur Filmliste
     *
     * @return Den Pfad als String
     */
    public static String getFilmListFile() {
        String strFile;
        strFile = ProgInfos.getSettingsDirectory_String() + File.separator + ProgConst.JSON_DATEI_FILME;

        return strFile;
    }

    /**
     * Return the location of the settings directory. If it does not exist, create one.
     *
     * @return Path to the settings directory
     * @throws IllegalStateException Will be thrown if settings directory don't exist and if there is
     *                               an error on creating it.
     */
    public static Path getSettingsDirectory() throws IllegalStateException {
        final Path baseDirectoryPath;
        if (ProgData.configDir == null || ProgData.configDir.isEmpty()) {
            baseDirectoryPath = Paths.get(System.getProperty("user.home"), ProgConst.VERZEICHNIS_EINSTELLUNGEN);
        } else {
            baseDirectoryPath = Paths.get(ProgData.configDir);
        }

        if (Files.notExists(baseDirectoryPath)) {
            try {
                Files.createDirectories(baseDirectoryPath);
            } catch (final IOException ioException) {
                System.out.println(ERROR_CANT_CREATE_FOLDER + " -> " + baseDirectoryPath.toString());
                throw new IllegalStateException(
                        ERROR_CANT_CREATE_FOLDER + " -> " + baseDirectoryPath.toString() + "\n" + ioException);
            }
        }

        return baseDirectoryPath;
    }

    public static String getSettingsDirectory_String() {
        return getSettingsDirectory().toString();
    }

    public static String getLogDirectory_String() {
        final String logDir;
        if (ProgConfig.SYSTEM_LOG_DIR.get().isEmpty()) {
            logDir = getStandardLogDirectory_String();
        } else {
            logDir = ProgConfig.SYSTEM_LOG_DIR.get();
        }
        return logDir;
    }

    public static String getStandardLogDirectory_String() {
        return Paths.get(getSettingsDirectory_String(), ProgConst.LOG_DIR).toString();
    }

    /**
     * Return the path to "mtplayer.xml_copy_" first copy exists
     *
     * @param xmlFilePath Path to file.
     */
    public void getMTPlayerXmlCopyFilePath(ArrayList<Path> xmlFilePath) {
        for (int i = 1; i <= ProgConst.MAX_COPY_OF_BACKUPFILE; ++i) {
            final Path path = ProgInfos.getSettingsDirectory().resolve(ProgConst.CONFIG_FILE_COPY + i);
            if (Files.exists(path)) {
                xmlFilePath.add(path);
            }
        }
    }
}
