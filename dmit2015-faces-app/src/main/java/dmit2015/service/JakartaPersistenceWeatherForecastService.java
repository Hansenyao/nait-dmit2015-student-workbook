package dmit2015.service;

import dmit2015.model.WeatherForecast;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import java.util.random.RandomGenerator;

@Named("jakartaPersistenceWeatherForecastService")
@ApplicationScoped
public class JakartaPersistenceWeatherForecastService implements WeatherForecastService {

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext //(unitName="pu-name-in-persistence.xml")
    private EntityManager entityManager;

    @Override
    @Transactional
    public WeatherForecast createWeatherForecast(WeatherForecast weatherForecast) {
        // If the primary key is not an identity column then write code below here to
        // 1) Generate a new primary key value
        // 2) Set the primary key value for the new entity

        entityManager.persist(weatherForecast);
        return weatherForecast;
    }

    @Override
    public Optional<WeatherForecast> getWeatherForecastById(String id) {
        try {
            WeatherForecast querySingleResult = entityManager.find(WeatherForecast.class, id);
            if (querySingleResult != null) {
                return Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            // id value not found
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    @Override
    public List<WeatherForecast> getAllWeatherForecasts() {
        return entityManager.createQuery("SELECT o FROM WeatherForecast o ", WeatherForecast.class)
                .getResultList();
    }

    @Override
    @Transactional
    public WeatherForecast updateWeatherForecast(WeatherForecast weatherForecast) {

        Optional<WeatherForecast> optionalWeatherForecast = getWeatherForecastById(weatherForecast.getId());
        if (optionalWeatherForecast.isEmpty()) {
            String errorMessage = String.format("The id %s does not exists in the system.", weatherForecast.getId());
            throw new RuntimeException(errorMessage);
        } else {
            var existingWeatherForecast = optionalWeatherForecast.orElseThrow();
            // Update only properties that is editable by the end user
            // TODO: Copy each edit property from weatherForecast to existingWeatherForecast
            // existingWeatherForecast.setPropertyName(weatherForecast.getPropertyName());

            weatherForecast = entityManager.merge(existingWeatherForecast);
        }
        return weatherForecast;
    }

    @Override
    @Transactional
    public void deleteWeatherForecastById(String id) {
        Optional<WeatherForecast> optionalWeatherForecast = getWeatherForecastById(id);
        if (optionalWeatherForecast.isPresent()) {
            WeatherForecast weatherForecast = optionalWeatherForecast.orElseThrow();
            // Write code to throw a RuntimeException if this entity contains child records
            entityManager.remove(weatherForecast);
        } else {
            throw new RuntimeException("Could not find WeatherForecast with id: " + id);
        }
    }

}