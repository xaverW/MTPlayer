package de.p2tools.mtplayer.controller.infotools;

public class MemoryData {
    final int xValue;
    final long usedMem;
    final long freeMemory;
    final long totalMemory;

    public MemoryData(int xValue, long usedMem, long freeMemory, long totalMemory) {
        this.xValue = xValue;
        this.usedMem = usedMem;
        this.freeMemory = freeMemory;
        this.totalMemory = totalMemory;
    }

    public int getxValue() {
        return xValue;
    }

    public long getUsedMem() {
        return usedMem;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }
}
