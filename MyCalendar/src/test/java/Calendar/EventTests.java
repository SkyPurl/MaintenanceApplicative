package Calendar;

import Calendar.Events.*;
import Calendar.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    // ----------------------------------------------------------------
    // Nouveaux tests pour EventId et détection de conflits
    // ----------------------------------------------------------------

    @Test
    void testCreationEventId() {
        // When
        EventId id1 = EventId.generate();
        EventId id2 = EventId.generate();

        // Then
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1.valeur(), id2.valeur());
    }

    @Test
    void testEventIdEquals() {
        // Given
        String idValue = "event-123";

        // When
        EventId id1 = new EventId(idValue);
        EventId id2 = new EventId(idValue);
        EventId id3 = new EventId("event-456");

        // Then
        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    void testInvalidEventId() {
        // Then
        assertThrows(IllegalArgumentException.class, () -> new EventId(null));
        assertThrows(IllegalArgumentException.class, () -> new EventId(""));
        assertThrows(IllegalArgumentException.class, () -> new EventId("   "));
    }

    @Test
    void testDetectionConflit() {
        // Given
        RendezVous rdv1 = new RendezVous(
                new TitreEvenement("RDV 1"),
                new DateEvenement(LocalDateTime.of(2025, 3, 24, 10, 0)),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                proprietaire
        );

        RendezVous rdv2 = new RendezVous(
                new TitreEvenement("RDV 2"),
                new DateEvenement(LocalDateTime.of(2025, 3, 24, 10, 30)),
                new HeureDebut(10, 30),
                new DureeEvenement(60),
                proprietaire
        );

        // When
        calendar.ajouterEvenement(rdv1);

        // Then
        EventConflitDetector detector = new EventConflitDetector();
        assertTrue(detector.detectConflict(rdv1, rdv2));

        // Test de l'ajout d'un événement en conflit
        try {
            calendar.ajouterEvenement(rdv2);
            fail("Une exception de conflit aurait dû être levée");
        } catch (Exception e) {
            assertTrue(e instanceof CalendarManager.ConflitEvenementException);
        }
    }

    @Test
    void testPasDeConflit() {
        // Given
        RendezVous rdv1 = new RendezVous(
                new TitreEvenement("RDV 1"),
                new DateEvenement(LocalDateTime.of(2025, 3, 24, 10, 0)),
                new HeureDebut(10, 0),
                new DureeEvenement(30),
                proprietaire
        );

        RendezVous rdv2 = new RendezVous(
                new TitreEvenement("RDV 2"),
                new DateEvenement(LocalDateTime.of(2025, 3, 24, 10, 30)),
                new HeureDebut(10, 30),
                new DureeEvenement(30),
                proprietaire
        );

        // Then
        EventConflitDetector detector = new EventConflitDetector();
        assertFalse(detector.detectConflict(rdv1, rdv2));

        // Test de l'ajout d'événements qui ne sont pas en conflit
        calendar.ajouterEvenement(rdv1);
        calendar.ajouterEvenement(rdv2);

        // Vérifier que les deux ont été ajoutés
        List<Event> events = calendar.eventsDansPeriode(
                new Periode(
                        LocalDateTime.of(2025, 3, 24, 0, 0),
                        LocalDateTime.of(2025, 3, 24, 23, 59)
                )
        );
        assertEquals(2, events.size());
    }

    @Test
    void testSuppressionEvenementParId() {
        // Given
        RendezVous rdv = new RendezVous(
                new TitreEvenement("RDV à supprimer"),
                new DateEvenement(LocalDateTime.of(2025, 3, 24, 10, 0)),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                proprietaire
        );
        calendar.ajouterEvenement(rdv);
        EventId idToDelete = rdv.getId();

        // When
        boolean deleted = calendar.supprimerEvenement(idToDelete);

        // Then
        assertTrue(deleted);
        List<Event> events = calendar.eventsDansPeriode(
                new Periode(
                        LocalDateTime.of(2025, 3, 24, 0, 0),
                        LocalDateTime.of(2025, 3, 24, 23, 59)
                )
        );
        assertEquals(0, events.size());
    }

    @Test
    void testSuppressionEvenementInexistant() {
        // Given
        EventId nonExistentId = EventId.generate();

        // When
        boolean deleted = calendar.supprimerEvenement(nonExistentId);

        // Then
        assertFalse(deleted);
    }

    @Test
    void testTrouverEvenementParId() {
        // Given
        RendezVous rdv = new RendezVous(
                new TitreEvenement("RDV à trouver"),
                new DateEvenement(LocalDateTime.of(2025, 3, 24, 10, 0)),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                proprietaire
        );
        calendar.ajouterEvenement(rdv);
        EventId idToFind = rdv.getId();

        // When
        Optional<Event> foundEvent = calendar.trouverParId(idToFind);

        // Then
        assertTrue(foundEvent.isPresent());
        assertEquals("RDV à trouver", foundEvent.get().getTitre().valeur());
    }

    @Test
    void testTrouverEvenementInexistant() {
        // Given
        EventId nonExistentId = EventId.generate();

        // When
        Optional<Event> foundEvent = calendar.trouverParId(nonExistentId);

        // Then
        assertFalse(foundEvent.isPresent());
    }

    // Classe de test pour l'itération d'événements dans Evenements
    @Test
    void testEvenementsIteration() {
        // Given
        Evenements evenements = new Evenements();

        RendezVous rdv1 = new RendezVous(
                new TitreEvenement("RDV Test"),
                new DateEvenement(LocalDateTime.of(2025, 3, 24, 10, 0)),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                proprietaire
        );

        RendezVous rdv2 = new RendezVous(
                new TitreEvenement("RDV Test 2"),
                new DateEvenement(LocalDateTime.of(2025, 3, 25, 10, 0)),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                proprietaire
        );

        // When
        evenements.ajouter(rdv1);
        evenements.ajouter(rdv2);

        // Then - Check iterator
        Iterator<Event> it = evenements.iterator();
        assertTrue(it.hasNext());
        Event first = it.next();
        assertTrue(it.hasNext());
        Event second = it.next();
        assertFalse(it.hasNext());
        assertThrows(NoSuchElementException.class, it::next);

        // Verify events retrieved
        assertTrue(
                (first.getTitre().valeur().equals("RDV Test") && second.getTitre().valeur().equals("RDV Test 2")) ||
                        (first.getTitre().valeur().equals("RDV Test 2") && second.getTitre().valeur().equals("RDV Test"))
        );
    }
}