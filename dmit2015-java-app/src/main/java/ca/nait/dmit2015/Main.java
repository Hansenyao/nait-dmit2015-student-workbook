package ca.nait.dmit2015;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Create a circle with radius of 5
        var circle = new Circle(5);

        // Print the radius and area to the Console
        String message = String.format(
                "Radius = %s, Area = %s, Diameter = %.2f, Circumference = %.2f",
                circle.getRadius(),
                circle.area(),
                circle.diameter(),
                circle.circumference());
        System.out.println(message);

        // Change the radius to 10
        circle.setRadius(10);
        message = String.format(
                "Radius = %s, Area = %s, Diameter = %.2f, Circumference = %.2f",
                circle.getRadius(),
                circle.area(),
                circle.diameter(),
                circle.circumference());
        System.out.println(message);
    }
}