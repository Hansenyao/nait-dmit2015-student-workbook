package dmit2015.service;

import dmit2015.model.WeatherForecast;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import net.datafaker.Faker;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.random.RandomGenerator;

@Named("memoryWeatherForecastService")
@ApplicationScoped
public class MemoryWeatherForecastService implements WeatherForecastService {

    private final List<WeatherForecast> weatherForecasts = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {

        var faker = new Faker();
        var randomGenerator = RandomGenerator.getDefault();
        for (int counter = 1; counter <= 5; counter++) {
            var currentWeatherForecast = new WeatherForecast();
            currentWeatherForecast.setId(UUID.randomUUID().toString());
            // TODO: Generate fake data for applicable properties
            // currentWeatherForecast.setProperty1(faker.providerName.methodName());
            // currentWeatherForecast.setProperty2(faker.providerName.methodName());
            // currentWeatherForecast.setProperty3(faker.providerName.methodName());

            weatherForecasts.add(currentWeatherForecast);
        }

    }

    @Override
    public WeatherForecast createWeatherForecast(WeatherForecast weatherForecast) {
        Objects.requireNonNull(weatherForecast, "WeatherForecast to create must not be null");

        // Assign a fresh id on create to ensure uniqueness (ignore any incoming id)
        WeatherForecast stored = WeatherForecast.copyOf(weatherForecast);
        stored.setId(UUID.randomUUID().toString());
        weatherForecasts.add(stored);

        // Return a defensive copy
        return WeatherForecast.copyOf(stored);
    }

    @Override
    public Optional<WeatherForecast> getWeatherForecastById(String id) {
        Objects.requireNonNull(id, "id must not be null");

        return weatherForecasts.stream()
                .filter(currentWeatherForecast -> currentWeatherForecast.getId().equals(id))
                .findFirst()
                .map(WeatherForecast::copyOf); // return a copy to avoid external mutation

    }

    @Override
    public List<WeatherForecast> getAllWeatherForecasts() {
        // Unmodifiable snapshot of copies
        return weatherForecasts.stream().map(WeatherForecast::copyOf).toList();
    }

    @Override
    public WeatherForecast updateWeatherForecast(WeatherForecast weatherForecast) {
        Objects.requireNonNull(weatherForecast, "WeatherForecast to update must not be null");
        Objects.requireNonNull(weatherForecast.getId(), "WeatherForecast id must not be null");

        // Find index of existing task by id
        int index = -1;
        for (int i = 0; i < weatherForecasts.size(); i++) {
            if (weatherForecasts.get(i).getId().equals(weatherForecast.getId())) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new NoSuchElementException("Could not find Task with id: " + weatherForecast.getId());
        }

        // Replace stored item with a copy (preserve id)
        WeatherForecast stored = WeatherForecast.copyOf(weatherForecast);
        weatherForecasts.set(index, stored);

        return WeatherForecast.copyOf(stored);
    }

    @Override
    public void deleteWeatherForecastById(String id) {
        Objects.requireNonNull(id, "id must not be null");

        boolean removed = weatherForecasts.removeIf(currentWeatherForecast -> id.equals(currentWeatherForecast.getId()));
        if (!removed) {
            throw new NoSuchElementException("Could not find Task with id: " + id);
        }
    }
}
