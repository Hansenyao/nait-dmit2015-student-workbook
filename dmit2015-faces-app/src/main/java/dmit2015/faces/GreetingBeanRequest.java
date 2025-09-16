package dmit2015.faces;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;
import org.omnifaces.util.Messages;

/**
 * Request-scoped backing bean: new instance per HTTP request.
 * Use for simple actions/data that don't need to persist after the response.
 */
@Named("greetingBeanRequest")
@RequestScoped // New instance per HTTP request; no Serializable required
public class GreetingBeanRequest {

    private static final Logger LOG = Logger.getLogger(GreetingBeanRequest.class.getName());

    @NotBlank(message = "Please enter a name.")
    @Getter @Setter
    private String name;

    public void onGenerateName() {
        var faker = new Faker();
        name = faker.name().name();
    }

    public void onSubmit() {
        var faker = new Faker(Locale.SIMPLIFIED_CHINESE);
        String message = String.format("Hello %s, your Chinese name is %s, you Pokemon name is %s",
                name,
                faker.name().fullName(),
                faker.pokemon().name());
        Messages.addGlobalInfo(message);
        //Messages.addGlobalInfo("Hello {0} to Faces world!", name);
    }

    public void onClear() {
        name = "";
    }
}
