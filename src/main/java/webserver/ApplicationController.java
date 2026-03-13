package webserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import weather.ForecastLocation;
import weather.RainForecast;
import weather.WeatherInfoProvider;
import weather.WeatherInfoProviderImpl;

@RestController
public class ApplicationController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/status")
    public String status() {
        return "Server is running!";
    }

    @PostMapping("/user")
    // Example curl :
    // curl -X POST http://localhost:8080/user \
    //     -H "Content-Type: application/json" \
    //     -d '{"username":"Ashok","value":123}'
    public String user(@RequestBody User user) {
        System.out.println("New user create request received for user: " + user.getUsername());
        // Create an entry in the DB and return the unique userid to the caller
        return "abx";
    }

    @PostMapping("/rainforecast")
    // Example curl
    //    curl -X POST http://localhost:8080/rainforecast \
    //            -H "Content-Type: application/json" \
    //            -d '{"longitude":39.7456,"latitude":-97.0892}'
    public String rainforecast(@RequestBody ForecastLocation location) {
        System.out.println(String.format("Rainforecast request received for location <%s, %s>: ",
                location.getLongitude(), location.getLatitude()));

        // TODO: Ideally this should be injected by auto dependency injection
        WeatherInfoProvider weatherInfoProvider = new WeatherInfoProviderImpl();
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        RainForecast rainForecast = weatherInfoProvider.provideRainforecast(longitude, latitude);
        return rainForecast.toString();
    }
}


