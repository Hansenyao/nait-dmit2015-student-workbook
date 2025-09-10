package dmit2015;
public class Rectangle {
    private double length, width;

    public Rectangle(double l, double w) {
        if (l <= 0 || w <= 0) throw new RuntimeException("Invalid dimensions");
        this.length = l; this.width = w;
    }

    public double area() { return length * width; }
    public double perimeter() { return 2 * (length + width); }
}
