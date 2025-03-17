package Calendar;

import Calendar.Events.*;
import Calendar.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventTests {

    private CalendarManager calendar;
    private TitreEvenement titre;
    private DateEvenement date;
    private HeureDebut heureDebut;
    private DureeEvenement duree;

    @BeforeEach
    void setUp() {
        calendar = new CalendarManager();
        titre = new TitreEvenement("Réunion projet");
        date = new DateEvenement(LocalDateTime.of(2025, 3, 17, 10, 0));
        heureDebut = new HeureDebut(10, 0);
        duree = new DureeEvenement(60);
    }

    @Test
    void testAjouterRendezVous() {
        Event rdv = new RendezVous(titre, date, heureDebut, duree);
        calendar.ajouterEvenement(rdv);

        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur(), date.valeur().plusHours(2))
        );
        assertEquals(1, events.size());
        assertEquals("RDV : Réunion projet le 2025-03-17T10:00 à 10h0", events.get(0).description());
    }

    @Test
    void testAjouterReunion() {
        Event reunion = new Reunion(
                titre,
                date,
                heureDebut,
                duree,
                new LieuEvenement("Salle A"),
                new Participants(new String[]{"Alice", "Bob"})
        );
        calendar.ajouterEvenement(reunion);

        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur(), date.valeur().plusHours(2))
        );
        assertEquals(1, events.size());
        assertTrue(events.get(0).description().contains("Réunion : Réunion projet à Salle A avec Alice, Bob"));
    }

    @Test
    void testAjouterEvenementPeriodique() {
        Event event = new EvenementPeriodique(titre, date, heureDebut, duree, 7);
        calendar.ajouterEvenement(event);

        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur(), date.valeur().plusDays(14))
        );
        assertEquals(2, events.size());
    }

    @Test
    void testAucunEvenementDansPeriode() {
        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur().plusDays(1), date.valeur().plusDays(2))
        );
        assertEquals(0, events.size());
    }

    @Test
    void testDescriptionEvenementPeriodique() {
        Event event = new EvenementPeriodique(titre, date, heureDebut, duree, 7);
        assertEquals("Événement périodique : Réunion projet tous les 7 jours", event.description());
    }

    @Test
    void testFrequenceEvenementPeriodique() {
        Event event = new EvenementPeriodique(titre, date, heureDebut, duree, 5);
        assertEquals(5, ((EvenementPeriodique) event).getFrequenceJours());
    }

    @Test
    void testAjoutPlusieursTypesEvenements() {
        Event rdv = new RendezVous(titre, date, heureDebut, duree);
        Event reunion = new Reunion(
                titre,
                date,
                heureDebut,
                duree,
                new LieuEvenement("Salle A"),
                new Participants(new String[]{"Alice", "Bob"})
        );
        Event periodique = new EvenementPeriodique(titre, date, heureDebut, duree, 7);

        calendar.ajouterEvenement(rdv);
        calendar.ajouterEvenement(reunion);
        calendar.ajouterEvenement(periodique);

        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur(), date.valeur().plusDays(7))
        );
        assertEquals(3, events.size());
    }

    @Test
    void testTitreEvenementInvalide() {
        assertThrows(IllegalArgumentException.class, () -> new TitreEvenement(""));
        assertThrows(IllegalArgumentException.class, () -> new TitreEvenement(null));
    }

    @Test
    void testDateEvenementInvalide() {
        assertThrows(IllegalArgumentException.class, () -> new DateEvenement(null));
    }

    @Test
    void testHeureDebutInvalide() {
        assertThrows(IllegalArgumentException.class, () -> new HeureDebut(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new HeureDebut(24, 0));
        assertThrows(IllegalArgumentException.class, () -> new HeureDebut(10, -10));
        assertThrows(IllegalArgumentException.class, () -> new HeureDebut(10, 60));
    }

    @Test
    void testDureeEvenementInvalide() {
        assertThrows(IllegalArgumentException.class, () -> new DureeEvenement(-1));
        assertThrows(IllegalArgumentException.class, () -> new DureeEvenement(0));
    }

    @Test
    void testLieuEvenementInvalide() {
        assertThrows(IllegalArgumentException.class, () -> new LieuEvenement(""));
        assertThrows(IllegalArgumentException.class, () -> new LieuEvenement(null));
    }

    @Test
    void testParticipantsInvalide() {
        assertThrows(IllegalArgumentException.class, () -> new Participants(new String[]{}));
        assertThrows(IllegalArgumentException.class, () -> new Participants(null));
    }

    @Test
    void testPeriodeInvalide() {
        LocalDateTime start = LocalDateTime.of(2025, 3, 17, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 3, 16, 10, 0);
        assertThrows(IllegalArgumentException.class, () -> new Periode(start, end));
    }

    @Test
    void testEvenementPeriodiqueZeroFrequency() {
        assertDoesNotThrow(() -> {
            new EvenementPeriodique(titre, date, heureDebut, duree, 0);
        });
    }

    @Test
    void testEvenementPeriodiquePasDansPeriode() {
        DateEvenement futureDate = new DateEvenement(LocalDateTime.of(2025, 3, 19, 10, 0));
        Event event = new EvenementPeriodique(titre, futureDate, heureDebut, duree, 7);
        calendar.ajouterEvenement(event);

        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur(), date.valeur().plusDays(1))
        );
        assertEquals(0, events.size());
    }
}
