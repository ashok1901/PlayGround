package weather;


import java.util.ArrayList;
import java.util.List;

public class RainForecast {
    List<RainTimeWindow> rainForecast = new ArrayList<>();

    public void addEntry(RainTimeWindow rainTimeWindow) {
        this.rainForecast.add(rainTimeWindow);
    }

    @Override
    public String toString() {
        return rainForecast.toString();
    }
}


