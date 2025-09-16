package dmit2015.faces;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("iphone17")
@RequestScoped
public class HelloBean {

    private String userInput;

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public String getMessage() {
        return "Hello, " + userInput;
    }

    public void submit() {
        var message = String.format("Hello %s and welcome to Jakarta Faces world!", userInput);
        var facesMessage = new FacesMessage(message);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

}
