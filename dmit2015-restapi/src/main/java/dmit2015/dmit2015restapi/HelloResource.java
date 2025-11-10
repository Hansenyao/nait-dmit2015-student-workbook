package dmit2015.dmit2015restapi;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/hello-world")
public class HelloResource {
    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello, World!";
    }

    @GET
    @Produces("text/html")
    public String helloHtml() {
        return "<h3>Hello, World in HTML</h3>";
    }

    @GET
    @Produces("application/json")
    public String helloJson() {
        return "{greeting: \"Hello from JSON World\"}";
    }
}