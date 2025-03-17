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
    private ProprietaireEvenement proprietaire;

    @BeforeEach
    void setUp() {
        calendar = new CalendarManager();
        titre = new TitreEvenement("Réunion projet");
        date = new DateEvenement(LocalDateTime.of(2025, 3, 17, 10, 0));
        heureDebut = new HeureDebut(10, 0);
        duree = new DureeEvenement(60);

        // On choisit un propriétaire par défaut pour tous les tests
        proprietaire = new ProprietaireEvenement("Michel");
    }

    @Test
    void testAjouterRendezVous() {
        Event rdv = new RendezVous(
                titre,
                date,
                heureDebut,
                duree,
                proprietaire
        );
        calendar.ajouterEvenement(rdv);

        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur(), date.valeur().plusHours(2))
        );
        assertEquals(1, events.size());
        assertEquals(
                "RDV : Réunion projet (propriétaire : Michel)  le 2025-03-17T10:00 à 10h0",
                events.get(0).description()
        );
    }

    @Test
    void testAjouterReunion() {
        // Nouvelle signature de Reunion :
        // (TitreEvenement, DateEvenement, HeureDebut, DureeEvenement, LieuEvenement, ProprietaireEvenement, Participants)
        Event reunion = new Reunion(
                titre,
                date,
                heureDebut,
                duree,
                new LieuEvenement("Salle A"),
                proprietaire,
                new Participants(new String[]{"Alice", "Bob"})
        );
        calendar.ajouterEvenement(reunion);

        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur(), date.valeur().plusHours(2))
        );
        assertEquals(1, events.size());
        assertTrue(
                events.get(0).description()
                        .contains("Réunion : Réunion projet (propriétaire : Michel)  à Salle A avec Alice, Bob")
        );
    }

    @Test
    void testAjouterEvenementPeriodique() {
        // EvenementPeriodique :
        // (TitreEvenement, DateEvenement, HeureDebut, DureeEvenement, int frequenceJours, ProprietaireEvenement)
        Event event = new EvenementPeriodique(
                titre,
                date,
                heureDebut,
                duree,
                7,
                proprietaire
        );
        calendar.ajouterEvenement(event);

        // Sur 14 jours => 2 occurrences (Jour 0 et Jour 7)
        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur(), date.valeur().plusDays(14))
        );
        assertEquals(2, events.size());
    }

    @Test
    void testAucunEvenementDansPeriode() {
        // Aucune occurrence dans la période => liste vide
        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur().plusDays(1), date.valeur().plusDays(2))
        );
        assertEquals(0, events.size());
    }

    @Test
    void testDescriptionEvenementPeriodique() {
        Event event = new EvenementPeriodique(
                titre,
                date,
                heureDebut,
                duree,
                7,
                proprietaire
        );
        assertEquals(
                "Événement périodique : Réunion projet (propriétaire : Michel)  tous les 7 jours",
                event.description()
        );
    }

    @Test
    void testFrequenceEvenementPeriodique() {
        Event event = new EvenementPeriodique(
                titre,
                date,
                heureDebut,
                duree,
                5,
                proprietaire
        );
        assertEquals(5, ((EvenementPeriodique) event).getFrequenceJours());
    }

    @Test
    void testAjoutPlusieursTypesEvenements() {
        Event rdv = new RendezVous(
                titre,
                date,
                heureDebut,
                duree,
                proprietaire
        );
        Event reunion = new Reunion(
                titre,
                date,
                heureDebut,
                duree,
                new LieuEvenement("Salle A"),
                proprietaire,
                new Participants(new String[]{"Alice", "Bob"})
        );
        Event periodique = new EvenementPeriodique(
                titre,
                date,
                heureDebut,
                duree,
                7,
                proprietaire
        );

        calendar.ajouterEvenement(rdv);
        calendar.ajouterEvenement(reunion);
        calendar.ajouterEvenement(periodique);

        // Sur 7 jours => Jour 0 => total 3 événements
        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur(), date.valeur().plusDays(7))
        );
        assertEquals(3, events.size());
    }

    // ----------------------------------------------------------------
    // Tests de validation (Value Objects, etc.)
    // ----------------------------------------------------------------

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
        // Si la logique autorise la fréquence 0
        assertDoesNotThrow(() -> {
            new EvenementPeriodique(
                    titre,
                    date,
                    heureDebut,
                    duree,
                    0,
                    proprietaire
            );
        });
    }

    @Test
    void testEvenementPeriodiquePasDansPeriode() {
        DateEvenement futureDate = new DateEvenement(LocalDateTime.of(2025, 3, 19, 10, 0));
        Event event = new EvenementPeriodique(
                titre,
                futureDate,
                heureDebut,
                duree,
                7,
                proprietaire
        );
        calendar.ajouterEvenement(event);

        List<Event> events = calendar.eventsDansPeriode(
                new Periode(date.valeur(), date.valeur().plusDays(1))
        );
        assertEquals(0, events.size());
    }

    @Test
    void testAjouterAnniversaire() {
        Event anniv = new Anniversaire(
                new TitreEvenement("Anniv. de Marie"),
                new DateEvenement(LocalDateTime.of(2025, 4, 12, 14, 0)),
                new HeureDebut(14, 0),
                new DureeEvenement(180),
                proprietaire,
                "Marie"
        );
        calendar.ajouterEvenement(anniv);
        List<Event> results = calendar.eventsDansPeriode(
                new Periode(
                        LocalDateTime.of(2025, 4, 12, 0, 0),
                        LocalDateTime.of(2025, 4, 12, 23, 59)
                )
        );
        assertEquals(1, results.size());
        assertTrue(results.get(0) instanceof Anniversaire);
    }

    @Test
    void testDescriptionAnniversaire() {
        Event anniv = new Anniversaire(
                new TitreEvenement("Anniv. de Paul"),
                new DateEvenement(LocalDateTime.of(2025, 6, 1, 18, 0)),
                new HeureDebut(18, 0),
                new DureeEvenement(120),
                proprietaire,
                "Paul"
        );
        assertEquals(
                "Anniversaire : Anniv. de Paul (propriétaire : Michel)  pour Paul",
                anniv.description()
        );
    }
}
