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

package de.p2tools.mtplayer.controller.data.download;

import de.p2tools.mtplayer.tools.SizeTools;
import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.ObjectPropertyBase;

public class DownloadSize extends ObjectPropertyBase<DownloadSizeData> implements Comparable<DownloadSize> {

    private long actFileSize = -1L;
    private Long fileSize = 0L;
    private DownloadSizeData downloadSizeData = new DownloadSizeData(0, "");

    public DownloadSize() {
    }

    public DownloadSize(long filmSize, long actFileSize) {
        this.fileSize = filmSize;
        this.actFileSize = actFileSize;
    }

    @Override
    public void setValue(DownloadSizeData v) {
        super.setValue(v);
        fileSize = v.l;
    }

    @Override
    public DownloadSizeData getValue() {
        return super.getValue();
    }

    @Override
    public final DownloadSizeData get() {
        return new DownloadSizeData(fileSize, getString());
    }

    @Override
    public Object getBean() {
        return DownloadSize.this;
    }

    @Override
    public String getName() {
        return "DownloadSize";
    }

    @Override
    public int compareTo(DownloadSize ll) {
        return (fileSize.compareTo(ll.fileSize));
    }

    @Override
    public String toString() {
        return getString();
    }

    public void setSize(String size) {
        // im Film ist die Größe in "MB" !!
        if (size.isEmpty()) {
            actFileSize = -1L;
            fileSize = 0L;
        } else {
            try {
                fileSize = Long.valueOf(size);
                fileSize = fileSize * 1000 * 1000;
            } catch (final Exception ex) {
                PLog.errorLog(978745320, ex, "String: " + size);
                fileSize = 0L;
            }
        }
        fireValueChangedEvent();
    }

    public void setSize(long l) {
        fileSize = l;
        fireValueChangedEvent();
    }

    public void reset() {
        actFileSize = -1L;
        fireValueChangedEvent();
    }

    public long getFilmSize() {
        return fileSize;
    }

    public void setActFileSize(long l) {
        actFileSize = l;
        if (fileSize < actFileSize) {
            //kann bei m3u8-URL passieren
            fileSize = 0L;
        }
        fireValueChangedEvent();
    }

    public void addAktFileSize(long l) {
        actFileSize += l;
        if (fileSize < actFileSize) {
            //kann bei m3u8-URL passieren
            fileSize = 0L;
        }
        fireValueChangedEvent();
    }

    public long getActFileSize() {
        return actFileSize;
    }

    private String getString() {
        String sizeStr;
        if (actFileSize <= 0) {
            if (fileSize > 0) {
                sizeStr = SizeTools.getSize(fileSize);
            } else {
                sizeStr = "";
            }
        } else if (fileSize > 0) {
            sizeStr = SizeTools.getSize(actFileSize) + " von " + SizeTools.getSize(fileSize);
        } else {
            sizeStr = SizeTools.getSize(actFileSize);
        }
        downloadSizeData = new DownloadSizeData(fileSize, sizeStr);
        return sizeStr;
    }
}
