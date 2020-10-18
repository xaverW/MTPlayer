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

package de.p2tools.mtplayer.tools;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * User: Christian F.
 * Date: 15.06.16
 * Time: 14:03
 */
public class ProgressMonitorInputStream extends FilterInputStream {
    /**
     * Creates a <code>FilterInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     *
     * @param in the underlying input stream, or <code>null</code> if
     *           this instance is to be created without an underlying stream.
     */
    public ProgressMonitorInputStream(InputStream in, long maxSize, InputStreamProgressMonitor mon) throws IOException {
        super(in);
        monitor = mon;
        this.size = maxSize;
        if (size == 0)
            throw new IOException("Size must be greater than zero!");
    }


    @Override
    public int read() throws IOException {
        final int read = super.read();
        if (read != -1) {
            bytesRead++;
            if (monitor != null)
                monitor.progress(bytesRead, size);
        }
        return read;
    }

    @Override
    public int read(byte[] b) throws IOException {
        final int read = super.read(b);
        if (read != -1) {
            bytesRead += read;
            if (monitor != null)
                monitor.progress(bytesRead, size);
        }
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        final int read = super.read(b, off, len);
        if (read != -1) {
            bytesRead += read;
            if (monitor != null)
                monitor.progress(bytesRead, size);
        }
        return read;
    }

    private InputStreamProgressMonitor monitor = null;
    /**
     * The number of bytes that can be read from the InputStream.
     */
    private long size = 0;
    /**
     * The number of bytes that have been read from the InputStream.
     */
    private long bytesRead = 0;
}
