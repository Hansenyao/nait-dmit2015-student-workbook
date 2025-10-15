package dmit2015.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.random.RandomGenerator;

/**
 * This Jakarta Persistence class is mapped to a relational database table with the same name.
 * If Java class name does not match database table name, you can use @Table annotation to specify the table name.
 * <p>
 * Each field in this class is mapped to a column with the same name in the mapped database table.
 * If the field name does not match database table column name, you can use the @Column annotation to specify the database table column name.
 * The @Transient annotation can be used on field that is not mapped to a database table column.
 */
@Entity
//@Table(schema = "CustomSchemaName", name="CustomTableName")
@Getter
@Setter
public class WeatherForecast implements Serializable {

    private static final Logger logger = Logger.getLogger(WeatherForecast.class.getName());

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forecastid", nullable = false)
    private String id;

    private String city;
    private LocalDate date =  LocalDate.now().plusDays(1);
    private int temperatureCelsius;

    public int getTemperatureFahrenheit() {
        return (int) (32 + temperatureCelsius / 0.5556);
    }

    public WeatherForecast() {

    }

    @Version
    private Integer version;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @PrePersist
    private void beforePersist() {
        createTime = LocalDateTime.now();
    }

    @PreUpdate
    private void beforeUpdate() {
        updateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        return (
                (obj instanceof WeatherForecast other) &&
                        Objects.equals(id, other.id)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Factory method to create a new WeatherForecast instance
    public static WeatherForecast of(Faker faker) {
        var newWeatherForecast = new WeatherForecast();
        newWeatherForecast.setId(UUID.randomUUID().toString());
        newWeatherForecast.setDate( LocalDate.now().plusDays(faker.number().numberBetween(1, 7)));
        newWeatherForecast.setCity(faker.address().city());
        newWeatherForecast.setTemperatureCelsius(faker.number().numberBetween(-35, 35));
        return newWeatherForecast;
    }

}