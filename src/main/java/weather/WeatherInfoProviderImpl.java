package weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;

public class WeatherInfoProviderImpl implements WeatherInfoProvider {

    // private String WEATHER_DATA_PROVIDER_URL_EXAMPLE =  "https://api.weather.gov/gridpoints/TOP/32,81";
    // Longitude Latitude weather data provider URL
    private String WEATHER_DATA_PROVIDER_FORMAT = "https://api.weather.gov/points/%s,%s";

    private HttpClient client;
    public WeatherInfoProviderImpl() {
        this.client = HttpClient.newHttpClient();
    }

    private String provideForecast(double longitude, double latitude) {
        String gridDataUrl = parseGridDataProviderURL(longitude, latitude);
        HttpRequest httpRequest = buildRequest(gridDataUrl);
        try {
            HttpResponse<String> httpResponse = executeHttpRequest(httpRequest);
            return httpResponse.body();
        } catch (IOException|InterruptedException e) {
            System.out.print("Exeception while executing get weather call " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private HttpRequest buildRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
    }

    private HttpResponse<String> executeHttpRequest(HttpRequest request) throws IOException, InterruptedException {
        return this.client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Override
    public RainForecast provideRainforecast(double longitude, double latitude) {
        // Here Json parsing will take place.
        String forecast = provideForecast(longitude, latitude);
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = null;
        try {
            root = mapper.readTree(forecast);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode valuesNode = root
                .path("properties")
                .path("quantitativePrecipitation")
                .path("values");

        RainForecast rainForecast = new RainForecast();
        if (valuesNode.isArray()) {
            for (JsonNode valueNode : valuesNode) {
                String time = valueNode.path("validTime").toString();
                long[] startEndTime = parseStartEndTime(time);
                double precipitaton = valueNode.path("value").asDouble();
                RainTimeWindow rainTimeWindow = new RainTimeWindow(startEndTime[0], startEndTime[1], precipitaton);
                rainForecast.addEntry(rainTimeWindow);
            }
        }

        return rainForecast;
    }

    public String parseGridDataProviderURL(double longitude, double latitude)  {
        String weatherDataProviderURL = String.format(WEATHER_DATA_PROVIDER_FORMAT,longitude, latitude);
        HttpRequest httpRequest = buildRequest(weatherDataProviderURL);
        HttpResponse<String> httpResponse = null;
        try {
            httpResponse = executeHttpRequest(httpRequest);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String responseBody = httpResponse.body();

        ObjectMapper mapper = new ObjectMapper();

        JsonNode root = null;
        try {
            root = mapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode valuesNode = root
                .path("properties")
                .path("forecastGridData");

        if (root.isNull() || root.isEmpty()) {
            throw new RuntimeException("Forecast grid data empty. Check the longitude and latitude provided.");
        }
        String forecastGridDataProviderURL = valuesNode.asText();

        System.out.println("Rain forecast grid data provider endpoint is : " + forecastGridDataProviderURL);
        return forecastGridDataProviderURL;

    }

    /**
     * ISO-8601 time interval format
     *
     * @return
     */
    private long[] parseStartEndTime(String iso8601FormatTimeDuration) {
        iso8601FormatTimeDuration = iso8601FormatTimeDuration.replace("\"", "");
        String[] parts = iso8601FormatTimeDuration.split("/");

        Instant start = Instant.parse(parts[0]);
        Duration duration = Duration.parse(parts[1]);

        Instant end = start.plus(duration);

        long startEpoch = start.getEpochSecond();
        long endEpoch = end.getEpochSecond();

        return new long[]{startEpoch, endEpoch};
    }

    public static void main(String[] args) {
        WeatherInfoProvider weatherInfoProvider = new WeatherInfoProviderImpl();

        // Improve it by accepting longitude, latitude pair from the user.
        double longitude = 39.7456, latitude = -97.0892;
        RainForecast rain = weatherInfoProvider.provideRainforecast(longitude, latitude);
        System.out.println("Rain forecast is " + rain);
    }
}


