package util;

public class Time {
    public static float timeStarted = System.nanoTime();
    public static final double NANOSEC_2_SEC = 1E-9;

    public static float getTime() {
        return (float) ((System.nanoTime() - timeStarted) * NANOSEC_2_SEC);
    }

}
