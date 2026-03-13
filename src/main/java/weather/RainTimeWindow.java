package weather;

public class RainTimeWindow {
    private long startTimeEpoch;
    private long endTimeEpoch;
    private double rainForecast;

    public RainTimeWindow(long startTimeEpoch, long endTimeEpoch, double rainForecast) {
        this.startTimeEpoch = startTimeEpoch;
        this.endTimeEpoch = endTimeEpoch;
        this.rainForecast = rainForecast;
    }

    public String toString() {
        return String.format("%s %s %s", startTimeEpoch, endTimeEpoch, rainForecast);
    }
}


