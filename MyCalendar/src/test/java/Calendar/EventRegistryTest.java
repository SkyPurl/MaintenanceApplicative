package Calendar;

import Calendar.Events.*;
import Calendar.vo.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.function.BiFunction;

class EventRegistryTest {

    private EventRegistry eventRegistry;
    private User testUser;

    @BeforeEach
    void setUp() {
        eventRegistry = new EventRegistry();
        // Création d'un utilisateur réel au lieu d'un mock
        testUser = new User("testUser", "hashedPassword123");
    }

    @Test
    void testRegisterEventType() {
        // Préparation : Compter le nombre initial d'événements enregistrés
        String initialDisplayKey = eventRegistry.getDisplayEventsKey();
        int initialKeyValue = Integer.parseInt(initialDisplayKey);

        // Exécution : Enregistrer un nouveau type d'événement
        eventRegistry.registerEventType("Test Event", (scanner, user) -> null);

        // Vérification : Le nombre d'événements doit avoir augmenté
        String newDisplayKey = eventRegistry.getDisplayEventsKey();
        int newKeyValue = Integer.parseInt(newDisplayKey);

        assertEquals(initialKeyValue + 1, newKeyValue);
    }

    @Test
    void testGetCreator() {
        // Préparation : Enregistrer un type d'événement test avec une fonction connue
        BiFunction<Scanner, User, Event> testFunction = (scanner, user) -> new RendezVous(
                new TitreEvenement("Test"),
                new DateEvenement(LocalDateTime.now()),
                new HeureDebut(10, 0),
                new DureeEvenement(30),
                new ProprietaireEvenement(user.username())
        );
        eventRegistry.registerEventType("Test Event", testFunction);

        // Récupérer la clé (qui doit être le prochain nombre après les événements par défaut)
        String testKey = String.valueOf(Integer.parseInt(eventRegistry.getDisplayEventsKey()) - 1);

        // Vérification : GetCreator doit retourner la fonction que nous avons enregistrée
        BiFunction<Scanner, User, Event> retrievedFunction = eventRegistry.getCreator(testKey);
        assertNotNull(retrievedFunction, "La fonction récupérée ne devrait pas être null");

        // Avec un scanner simulé, la fonction devrait créer un événement
        String simulatedInput = "Test\n2025-03-18T10:00\n10\n0\n30\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        Event createdEvent = retrievedFunction.apply(mockScanner, testUser);
        assertNotNull(createdEvent, "L'événement créé ne devrait pas être null");
        assertTrue(createdEvent instanceof RendezVous, "L'événement devrait être un RendezVous");
    }

    @Test
    void testGetDisplayEventsKey() {
        // Le nombre de types d'événements par défaut est de 4 (comme initialisé dans le constructeur)
        // Donc la clé d'affichage devrait être 5
        assertEquals("5", eventRegistry.getDisplayEventsKey());
    }

    @Test
    void testGetQuitKey() {
        // La clé de sortie devrait être une de plus que la clé d'affichage
        String displayKey = eventRegistry.getDisplayEventsKey();
        String quitKey = eventRegistry.getQuitKey();

        assertEquals(Integer.parseInt(displayKey) + 1, Integer.parseInt(quitKey));
    }

    @Test
    void testCreateRendezVous() {
        // Simuler l'entrée utilisateur pour créer un rendez-vous
        String simulatedInput = "Réunion de projet\n2025-03-20T14:30\n14\n30\n60\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Obtenir la fonction de création de rendez-vous (clé 1)
        BiFunction<Scanner, User, Event> rendezvousCreator = eventRegistry.getCreator("1");

        // Créer un rendez-vous
        Event event = rendezvousCreator.apply(mockScanner, testUser);

        // Vérifier que le rendez-vous a été créé correctement
        assertNotNull(event);
        assertTrue(event instanceof RendezVous);
        assertEquals("Réunion de projet", ((RendezVous)event).getTitre().valeur());
        assertEquals("testUser", ((RendezVous)event).getProprietaire().valeur());
        assertEquals(60, ((RendezVous)event).getDuree().valeur());
    }

    @Test
    void testCreateReunion() {
        // Simuler l'entrée utilisateur pour créer une réunion
        String simulatedInput = "Réunion équipe\n2025-03-21T10:00\n10\n0\n120\nSalle A\nJohn,Alice,Bob\n";
        Scanner mockScanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));

        // Obtenir la fonction de création de réunion (clé 2)
        BiFunction<Scanner, User, Event> reunionCreator = eventRegistry.getCreator("2");

        // Créer une réunion
        Event event = reunionCreator.apply(mockScanner, testUser);

        // Vérifier que la réunion a été créée correctement
        assertNotNull(event);
        assertTrue(event instanceof Reunion);
        assertEquals("Réunion équipe", ((Reunion)event).getTitre().valeur());
        assertEquals("Salle A", ((Reunion)event).getLieu().valeur());
        assertEquals(3, ((Reunion)event).getParticipants().noms().length);
    }
}