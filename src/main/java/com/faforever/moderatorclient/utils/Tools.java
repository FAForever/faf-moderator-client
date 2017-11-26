package com.faforever.moderatorclient.utils;

public class Tools {
    public static long calculateRating(double mean, double deviation) {
        return Math.round(mean - 3 * deviation);
    }

    public static long calculateRoundedRating(double mean, double deviation) {
        return Math.round(calculateRating(mean, deviation) / 100d) * 100;
    }
}
