package Calendar;

import Calendar.Events.*;
import Calendar.Util.EventConflitDetector;
import Calendar.vo.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ConflitDetectionTests {

    @Test
    void shouldDetectConflictingEvents() {
        // Given
        EventConflitDetector detector = new EventConflitDetector();
        LocalDateTime now = LocalDateTime.of(2025, 3, 24, 10, 0);

        RendezVous event1 = new RendezVous(
                new TitreEvenement("RDV 1"),
                new DateEvenement(now),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                new ProprietaireEvenement("user1")
        );

        RendezVous event2 = new RendezVous(
                new TitreEvenement("RDV 2"),
                new DateEvenement(now),
                new HeureDebut(10, 30),
                new DureeEvenement(60),
                new ProprietaireEvenement("user1")
        );

        // When & Then
        assertTrue(detector.detectConflict(event1, event2));
    }

    @Test
    void shouldNotDetectConflictForNonOverlappingEvents() {
        // Given
        EventConflitDetector detector = new EventConflitDetector();
        LocalDateTime now = LocalDateTime.of(2025, 3, 24, 10, 0);

        RendezVous event1 = new RendezVous(
                new TitreEvenement("RDV 1"),
                new DateEvenement(now),
                new HeureDebut(10, 0),
                new DureeEvenement(30),
                new ProprietaireEvenement("user1")
        );

        RendezVous event2 = new RendezVous(
                new TitreEvenement("RDV 2"),
                new DateEvenement(now),
                new HeureDebut(10, 30),
                new DureeEvenement(30),
                new ProprietaireEvenement("user1")
        );

        // When & Then
        assertFalse(detector.detectConflict(event1, event2));
    }

    @Test
    void shouldNotDetectConflictForDifferentDays() {
        // Given
        EventConflitDetector detector = new EventConflitDetector();

        RendezVous event1 = new RendezVous(
                new TitreEvenement("RDV 1"),
                new DateEvenement(LocalDateTime.of(2025, 3, 24, 10, 0)),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                new ProprietaireEvenement("user1")
        );

        RendezVous event2 = new RendezVous(
                new TitreEvenement("RDV 2"),
                new DateEvenement(LocalDateTime.of(2025, 3, 25, 10, 0)),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                new ProprietaireEvenement("user1")
        );

        // When & Then
        assertFalse(detector.detectConflict(event1, event2));
    }
}