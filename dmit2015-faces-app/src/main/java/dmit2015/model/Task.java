package dmit2015.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import net.datafaker.Faker;

import java.util.UUID;
import java.util.random.RandomGenerator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Task {
    private String id;

    @NotBlank(message = "Description is required.")
    private String description;

    @NotBlank(message = "Priority is required.")
    private String priority;    // Low, Medium, High
    private boolean done;

    public Task(Task other) {
        this.id = other.id;
        this.description = other.description;
        this.priority = other.priority;
        this.done = other.done;
    }

    public static Task copyOf(Task other) {
        return new Task(other);
    }

    public static Task of(Faker faker) {
        var currentTask = new Task();
        var randomGenerator = RandomGenerator.getDefault();
        String[] priorities = {"Low", "Medium", "High"};
        currentTask.setId(UUID.randomUUID().toString());
        currentTask.setDescription(faker.babylon5().quote());
        currentTask.setPriority(priorities[randomGenerator.nextInt(priorities.length)]);
        return currentTask;
    }
}
