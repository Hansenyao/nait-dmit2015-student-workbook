package dmit2015;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class RectangleTest {

    @Test
    void area_whenCreatedValidValues_shouldReturnCorrectArea() {
        Rectangle rectangle = new Rectangle(2, 5);

        // JUnit
        assertEquals(2*5, rectangle.area(), 0.0);

        //AssertJ
        assertThat(rectangle.area()).isEqualTo(2*5);
    }

    @Test
    void perimeter_whenCreatedWiethValidValues_shouldReturnCorrectPerimeter() {
        Rectangle rectangle = new Rectangle(2, 5);

        // JUnit
        assertEquals(2*(2+5), rectangle.perimeter(), 0.0);

        // AssertJ
        assertThat(rectangle.perimeter())
                .isEqualTo(2 * (2 + 5));
    }

    @Test
    void rectangle_whenCreatedWithInvalidValues_shouldThrowException() {
        // JUnit
        assertThrows(RuntimeException.class, () -> new Rectangle(0, 10));
        assertThrows(RuntimeException.class, () -> new Rectangle(-1, 10));
        assertThrows(RuntimeException.class, () -> new Rectangle(1, 0));
        assertThrows(RuntimeException.class, () -> new Rectangle(1, -20));

        // AssertJ
        assertThatThrownBy(() -> new Rectangle(0, 10))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> new Rectangle(-1, 10))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> new Rectangle(1, 0))
                .isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> new Rectangle(1, -20))
                .isInstanceOf(RuntimeException.class);
    }
}