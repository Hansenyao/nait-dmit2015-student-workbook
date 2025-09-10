package ca.nait.dmit2015;

public class Circle {
    private double radius;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Circle() {
        this.radius = 1;
    }
    public Circle(double radius) {
        setRadius(radius);
    }
    public double area() {
        return Math.PI * radius * radius;
    }
    public double diameter() {
        return 2 * radius;
    }
    public double circumference() {
        return 2 * Math.PI * radius;
    }
}
