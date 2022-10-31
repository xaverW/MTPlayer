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

package de.p2tools.mtplayer.controller.tools;

import de.p2tools.p2Lib.tools.log.PLog;
import javafx.beans.property.IntegerProperty;

import java.util.concurrent.Semaphore;

/**
 * This singleton class provides the necessary tokens for direct file downloads. It ensures that
 * selected bandwidth limit will not be exceeded for all concurrent direct downloads. Bandwidth
 * throttling based on http://en.wikipedia.org/wiki/Token_bucket
 */
public class MLBandwidthTokenBucket {

    public static final int DEFAULT_BUFFER_SIZE = 4 * 1024; // default byte buffer size
    private final Semaphore bucketSize = new Semaphore(0, false);

    public static final int BANDWIDTH_MAX_RED_KBYTE = 500; // 500 kByte/s
    public static final int BANDWIDTH_MAX_BYTE = 1_000_000; // 1.000 kByte/s
    public static final int BANDWIDTH_MAX_KBYTE = 1_000; // 1.000 kByte/s

    private volatile int bucketCapacity = BANDWIDTH_MAX_RED_KBYTE * 1_000; // 500kByte/s
    private MVBandwidthTokenBucketFillerThread fillerThread = null;
    IntegerProperty bandwidthValue;

    public MLBandwidthTokenBucket(IntegerProperty bandwidthValue) {
        this.bandwidthValue = bandwidthValue;

        setBucketCapacity(getBandwidth());
        this.bandwidthValue.addListener(l -> {
            PLog.sysLog("change bucketCapacity: " + getBandwidth() + " bytesPerSecond");
            setBucketCapacity(getBandwidth());
        });
    }

    /**
     * Ensure that bucket filler thread is running. If it running, nothing will happen.
     */
    public synchronized void ensureBucketThreadIsRunning() {
        if (fillerThread == null) {
            fillerThread = new MVBandwidthTokenBucketFillerThread();
            fillerThread.start();
        }
    }

    /**
     * Take number of byte tickets from bucket.
     *
     * @param howMany The number of bytes to acquire.
     */
    public void takeBlocking(final int howMany) {
        // if bucket size equals BANDWIDTH_MAX_BYTE then unlimited speed...
        if (getBucketCapacity() < BANDWIDTH_MAX_BYTE) {
            try {
                bucketSize.acquire(howMany);
            } catch (final Exception ignored) {
            }
        }
    }

    /**
     * Acquire one byte ticket from bucket.
     */
    public void takeBlocking() {
        takeBlocking(1);
    }

    /**
     * Get the capacity of the Token Bucket.
     *
     * @return Maximum number of tokens in the bucket.
     */
    public synchronized int getBucketCapacity() {
        return bucketCapacity;
    }

    /**
     * Kill the semaphore filling thread.
     */
    private void terminateFillerThread() {
        if (fillerThread != null) {
            fillerThread.interrupt();
            fillerThread = null;
        }
    }

    public synchronized void setBucketCapacity(int bucketCapacity) {
        this.bucketCapacity = bucketCapacity;
        if (bucketCapacity == BANDWIDTH_MAX_BYTE) {
            terminateFillerThread();

            // if we have waiting callers, release them by releasing buckets in the semaphore...
            while (bucketSize.hasQueuedThreads()) {
                bucketSize.release();
            }

            // reset semaphore
            bucketSize.drainPermits();
        } else {
            terminateFillerThread();
            bucketSize.drainPermits();

            // restart filler thread with new settings...
            ensureBucketThreadIsRunning();
        }
    }

    /**
     * Read bandwidth settings from config.
     *
     * @return The maximum bandwidth in bytes set or zero for unlimited speed.
     */
    private int getBandwidth() {
        int bytesPerSecond;

        try {
            final int maxKBytePerSec = bandwidthValue.get();
            bytesPerSecond = maxKBytePerSec * 1_000;
        } catch (final Exception ex) {
            PLog.errorLog(612547803, ex, "reset Bandwidth");
            bytesPerSecond = BANDWIDTH_MAX_KBYTE * 1_000;
            bandwidthValue.set(BANDWIDTH_MAX_KBYTE);
        }
        return bytesPerSecond;
    }

    /**
     * Fills the bucket semaphore with available download buckets for speed management.
     */
    private class MVBandwidthTokenBucketFillerThread extends Thread {

        public MVBandwidthTokenBucketFillerThread() {
            setName("MLBandwidthTokenBucket Filler Thread");
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    // run 2times per second, its more regular
                    final int bucketCapacity = getBucketCapacity();
                    // for unlimited speed we dont need the thread
                    if (bucketCapacity == MLBandwidthTokenBucket.BANDWIDTH_MAX_BYTE) {
                        break;
                    }

                    final int releaseCount = bucketCapacity / 2 - bucketSize.availablePermits();
                    if (releaseCount > 0) {
                        bucketSize.release(releaseCount);
                    }

                    sleep(500);
                }
            } catch (final Exception ignored) {
            }
        }
    }
}
