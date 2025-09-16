package dmit2015.faces;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
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

    public void onSubmit() {
        Messages.addGlobalInfo("Hello {0} to Faces world!", name);
    }

    public void onClear() {
        name = "";
    }
}
