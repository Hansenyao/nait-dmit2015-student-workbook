package dmit2015.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;

import java.io.Serializable;
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
public class Task implements Serializable {

    private static final Logger logger = Logger.getLogger(Task.class.getName());

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taskid", nullable = false)
    private String id;

    @NotBlank(message = "Description is required.")
    @Column(length = 1024)
    private String description;

    private String priority;    // Low, Medium, High
    private boolean done;

    public Task() {

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
                (obj instanceof Task other) &&
                        Objects.equals(id, other.id)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Factory method to create a new Task instance
    public static Task of(Faker faker) {
        var currentTask = new Task();
        var randomGenerator = RandomGenerator.getDefault();
        String[] priorities = {"Low","Medium","High"};
        currentTask.setId(UUID.randomUUID().toString());
        currentTask.setDescription(faker.breakingBad().episode());
        currentTask.setPriority(priorities[randomGenerator.nextInt(priorities.length)]);
        return currentTask;
    }

}