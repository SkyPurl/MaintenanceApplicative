package Calendar;

import Calendar.vo.EventId;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EventIdTest {

    @Test
    void shouldCreateValidEventIdWithValue() {
        // Given
        String idValue = "event-123";

        // When
        EventId id = new EventId(idValue);

        // Then
        assertEquals(idValue, id.valeur());
    }

    @Test
    void shouldGenerateUniqueEventIds() {
        // When
        EventId id1 = EventId.generate();
        EventId id2 = EventId.generate();

        // Then
        assertNotNull(id1.valeur());
        assertNotNull(id2.valeur());
        assertNotEquals(id1.valeur(), id2.valeur());
    }

    @Test
    void shouldRejectNullOrEmptyValue() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> new EventId(null));
        assertThrows(IllegalArgumentException.class, () -> new EventId(""));
        assertThrows(IllegalArgumentException.class, () -> new EventId("   "));
    }

    @Test
    void shouldProperlyImplementEqualsAndHashCode() {
        // Given
        EventId id1 = new EventId("event-123");
        EventId id2 = new EventId("event-123");
        EventId id3 = new EventId("event-456");

        // Then
        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}