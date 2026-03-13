package weather;

public interface WeatherInfoProvider {
    public RainForecast provideRainforecast(double longitude, double latitude);
}
