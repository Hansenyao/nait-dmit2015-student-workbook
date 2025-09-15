package dmit2015.faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named
@RequestScoped
public class HelloBean {
    private String userInput;
    public String getMessage() {
        return "Hello, " + userInput;
    }
    public void setMessage(String userInput) {
        this.userInput = userInput;
    }
    public String getUserInput() {
        return userInput;
    }
    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public void submit() {
        //userInput += " submitted";
        var message = String.format("Hello, %s!", userInput);
        var facesMessage = new FacesMessage(getMessage());
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }
}
