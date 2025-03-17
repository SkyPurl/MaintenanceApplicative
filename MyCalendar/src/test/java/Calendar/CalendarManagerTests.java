package Calendar;

import Calendar.Events.*;
import Calendar.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalendarManagerTests {

    private CalendarManager manager;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        manager = new CalendarManager();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testCalendarVide() {
        List<Event> events = manager.eventsDansPeriode(
                new Periode(LocalDateTime.now(), LocalDateTime.now().plusDays(1))
        );
        assertTrue(events.isEmpty());
    }

    @Test
    void testAjoutUnSeulEvenement() {
        LocalDateTime d = LocalDateTime.of(2025, 1, 1, 12, 0);
        Event rdv = new RendezVous(
                new TitreEvenement("Dentiste"),
                new DateEvenement(d),
                new HeureDebut(12, 0),
                new DureeEvenement(30),
                new ProprietaireEvenement("Alice")
        );
        manager.ajouterEvenement(rdv);
        List<Event> results = manager.eventsDansPeriode(
                new Periode(d.minusHours(1), d.plusHours(1))
        );
        assertEquals(1, results.size());
        assertEquals(rdv, results.get(0));
    }

    @Test
    void testAjoutMultipleEvenements() {
        LocalDateTime d = LocalDateTime.of(2025, 1, 1, 10, 0);
        Event rdv = new RendezVous(
                new TitreEvenement("RDV test"),
                new DateEvenement(d),
                new HeureDebut(10, 0),
                new DureeEvenement(30),
                new ProprietaireEvenement("Alice")
        );
        LocalDateTime d2 = LocalDateTime.of(2025, 1, 2, 9, 0);
        Event reunion = new Reunion(
                new TitreEvenement("Réunion test"),
                new DateEvenement(d2),
                new HeureDebut(9, 0),
                new DureeEvenement(60),
                new LieuEvenement("Salle 101"),
                new ProprietaireEvenement("Bob"),
                new Participants(new String[]{"Alice", "Bob"})
        );
        LocalDateTime d3 = LocalDateTime.of(2025, 1, 1, 10, 0);
        Event periodique = new EvenementPeriodique(
                new TitreEvenement("Tennis"),
                new DateEvenement(d3),
                new HeureDebut(10, 0),
                new DureeEvenement(120),
                1,
                new ProprietaireEvenement("Alice")
        );
        manager.ajouterEvenement(rdv);
        manager.ajouterEvenement(reunion);
        manager.ajouterEvenement(periodique);
        List<Event> results = manager.eventsDansPeriode(
                new Periode(d.minusHours(1), d.plusDays(2))
        );
        assertEquals(4, results.size());
    }

    @Test
    void testAfficherEvenements() {
        manager.ajouterEvenement(new RendezVous(
                new TitreEvenement("TestConsole"),
                new DateEvenement(LocalDateTime.of(2025, 1, 1, 10, 0)),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                new ProprietaireEvenement("Alice")
        ));
        manager.afficherEvenements();
        String output = outContent.toString();
        assertTrue(output.contains("TestConsole"));
    }

    @Test
    void testPeriodeSansOccurrencePeriodique() {
        LocalDateTime now = LocalDateTime.now();
        Event periodique = new EvenementPeriodique(
                new TitreEvenement("Yoga"),
                new DateEvenement(now.plusDays(3)),
                new HeureDebut(9, 0),
                new DureeEvenement(90),
                7,
                new ProprietaireEvenement("Alice")
        );
        manager.ajouterEvenement(periodique);
        List<Event> results = manager.eventsDansPeriode(new Periode(now, now.plusDays(2)));
        assertTrue(results.isEmpty());
    }

    @Test
    void testEvenementPeriodiqueDoubleOccurrenceDansIntervalle() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        Event periodique = new EvenementPeriodique(
                new TitreEvenement("Marche"),
                new DateEvenement(start),
                new HeureDebut(0, 0),
                new DureeEvenement(30),
                2,
                new ProprietaireEvenement("Alice")
        );
        manager.ajouterEvenement(periodique);
        List<Event> results = manager.eventsDansPeriode(
                new Periode(start, start.plusDays(5))
        );
        assertEquals(3, results.size());
    }
}
