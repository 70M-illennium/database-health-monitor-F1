package com.fares.demo1.common;

/**
 * Byte -> human-unit conversions for API responses. Snapshots are stored in raw bytes
 * (the monitoring norm); this converts to MB / GB for display only, rounded to 2 decimals.
 *
 * <p>Public (not package-private, its original access when every DTO lived in one {@code
 * dto} package): the domain-based reorganization spread its callers across {@code host},
 * {@code database}, and {@code activity}, so package-private visibility no longer covers
 * every caller.
 */
public final class Units {

    private static final double MB = 1024.0 * 1024.0;
    private static final double GB = MB * 1024.0;

    private Units() {
    }

    public static double toMb(double bytes) {
        return round2(bytes / MB);
    }

    public static double toGb(double bytes) {
        return round2(bytes / GB);
    }

    /** Round any value to 2 decimals (percentages, load averages), not just byte conversions. */
    public static double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
