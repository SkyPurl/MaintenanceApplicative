package Calendar;

import Calendar.Events.*;
import Calendar.Serialization.JsonCalendarSerializer;
import Calendar.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests complets pour la sérialisation/désérialisation des événements du calendrier
 */
class JsonCalendarSerializerTest {

    private JsonCalendarSerializer serializer;
    private List<Event> testEvents;
    private LocalDateTime baseDateTime;
    private CalendarManager calendar;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        serializer = new JsonCalendarSerializer();
        calendar = new CalendarManager();
        testEvents = new ArrayList<>();
        baseDateTime = LocalDateTime.of(2025, 3, 24, 10, 0);

        createTestEvents();

        // Ajouter tous les événements au calendrier
        testEvents.forEach(event -> {
            try {
                calendar.ajouterEvenement(event);
            } catch (Exception e) {
                // Ignorer les conflits pour les tests
                System.out.println("Conflit d'événement ignoré pour les tests: " + e.getMessage());
            }
        });
    }

    private void createTestEvents() {
        // Un rendez-vous simple
        RendezVous rdv = new RendezVous(
                new TitreEvenement("RDV Test"),
                new DateEvenement(baseDateTime),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                new ProprietaireEvenement("user1")
        );
        testEvents.add(rdv);

        // Une réunion avec participants et lieu
        Reunion reunion = new Reunion(
                new TitreEvenement("Réunion Test"),
                new DateEvenement(baseDateTime.plusHours(2)),
                new HeureDebut(12, 0),
                new DureeEvenement(90),
                new LieuEvenement("Salle A"),
                new ProprietaireEvenement("user2"),
                new Participants(new String[]{"Alice", "Bob", "Charlie"})
        );
        testEvents.add(reunion);

        // Un événement périodique
        EvenementPeriodique periodique = new EvenementPeriodique(
                new TitreEvenement("Événement Périodique"),
                new DateEvenement(baseDateTime.plusDays(1)),
                new HeureDebut(14, 30),
                new DureeEvenement(45),
                7, // Tous les 7 jours
                new ProprietaireEvenement("user1")
        );
        testEvents.add(periodique);

        // Un anniversaire
        Anniversaire anniversaire = new Anniversaire(
                new TitreEvenement("Anniversaire Test"),
                new DateEvenement(baseDateTime.plusDays(3)),
                new HeureDebut(18, 0),
                new DureeEvenement(180),
                new ProprietaireEvenement("user3"),
                "Sophie"
        );
        testEvents.add(anniversaire);
    }

    @Test
    @DisplayName("Test de sérialisation de base pour tous les types d'événements")
    void testBasicSerialization() throws Exception {
        // Quand: on sérialise tous les événements
        String json = serializer.serializeEvents(testEvents);

        // Alors: le JSON ne devrait pas être vide
        assertNotNull(json);
        assertTrue(json.length() > 100);

        // Et: le JSON devrait contenir des informations sur tous les types d'événements
        assertTrue(json.contains("RendezVous"), "Le JSON ne contient pas le type RendezVous");
        assertTrue(json.contains("Reunion"), "Le JSON ne contient pas le type Reunion");
        assertTrue(json.contains("EvenementPeriodique"), "Le JSON ne contient pas le type EvenementPeriodique");
        assertTrue(json.contains("Anniversaire"), "Le JSON ne contient pas le type Anniversaire");

        // Et: le JSON devrait contenir les titres des événements
        assertTrue(json.contains("RDV Test"), "Le JSON ne contient pas le titre RDV Test");
        assertTrue(json.contains("Réunion Test"), "Le JSON ne contient pas le titre Réunion Test");
        assertTrue(json.contains("Événement Périodique"), "Le JSON ne contient pas le titre Événement Périodique");
        assertTrue(json.contains("Anniversaire Test"), "Le JSON ne contient pas le titre Anniversaire Test");
    }

    @Test
    @DisplayName("Test de désérialisation de tous les types d'événements")
    void testDeserialization() throws Exception {
        // Étant donné: un JSON contenant tous les types d'événements
        String json = serializer.serializeEvents(testEvents);

        // Quand: on désérialise ce JSON
        List<Event> deserializedEvents = serializer.deserializeEvents(json);

        // Alors: le nombre d'événements devrait être le même
        assertEquals(testEvents.size(), deserializedEvents.size(),
                "Le nombre d'événements désérialisés ne correspond pas");

        // Et: chaque type d'événement devrait être présent
        List<Class<?>> eventTypes = deserializedEvents.stream()
                .map(Event::getClass)
                .collect(Collectors.toList());

        assertTrue(eventTypes.contains(RendezVous.class), "RendezVous manquant après désérialisation");
        assertTrue(eventTypes.contains(Reunion.class), "Reunion manquante après désérialisation");
        assertTrue(eventTypes.contains(EvenementPeriodique.class), "EvenementPeriodique manquant après désérialisation");
        assertTrue(eventTypes.contains(Anniversaire.class), "Anniversaire manquant après désérialisation");

        // Et: vérifier que les propriétés spécifiques sont conservées
        for (Event event : deserializedEvents) {
            if (event instanceof Reunion) {
                Reunion reunion = (Reunion) event;
                assertNotNull(reunion.getLieu(), "Le lieu de la réunion est null après désérialisation");
                assertNotNull(reunion.getParticipants(), "Les participants de la réunion sont null après désérialisation");
                assertTrue(reunion.getParticipants().noms().length > 0,
                        "La liste des participants est vide après désérialisation");
            } else if (event instanceof EvenementPeriodique) {
                EvenementPeriodique periodique = (EvenementPeriodique) event;
                assertEquals(7, periodique.getFrequenceJours(),
                        "La fréquence de l'événement périodique n'est pas conservée");
            } else if (event instanceof Anniversaire) {
                Anniversaire anniversaire = (Anniversaire) event;
                assertEquals("Sophie", anniversaire.getPersonneFetee(),
                        "Le nom de la personne fêtée n'est pas conservé");
            }
        }
    }

    @Test
    @DisplayName("Test de sauvegarde dans un fichier JSON")
    void testSaveToFile() throws IOException {
        // Quand: on sauvegarde le calendrier dans un fichier
        File tempFile = tempDir.resolve("test-calendar.json").toFile();
        serializer.saveCalendarToFile(calendar, tempFile.getPath());

        // Alors: le fichier devrait exister et ne pas être vide
        assertTrue(tempFile.exists(), "Le fichier de sauvegarde n'a pas été créé");
        assertTrue(tempFile.length() > 0, "Le fichier de sauvegarde est vide");

        // Et: le contenu du fichier devrait être du JSON valide
        String fileContent = Files.readString(tempFile.toPath());
        assertTrue(fileContent.startsWith("["), "Le contenu ne commence pas par un crochet ouvrant");
        assertTrue(fileContent.endsWith("]"), "Le contenu ne se termine pas par un crochet fermant");
    }

    @Test
    @DisplayName("Test de chargement depuis un fichier JSON")
    void testLoadFromFile() throws IOException {
        // Étant donné: un calendrier sauvegardé dans un fichier
        File tempFile = tempDir.resolve("test-calendar-load.json").toFile();
        serializer.saveCalendarToFile(calendar, tempFile.getPath());

        // Quand: on crée un nouveau calendrier et on le charge depuis le fichier
        CalendarManager newCalendar = new CalendarManager();
        serializer.loadCalendarFromFile(newCalendar, tempFile.getPath());

        // Alors: le nouveau calendrier devrait contenir tous les types d'événements originaux
        List<Event> loadedEvents = newCalendar.eventsDansPeriode(
                new Periode(baseDateTime.minusDays(1), baseDateTime.plusDays(10)));

        // Vérifier que tous les types d'événements sont présents
        boolean hasRendezVous = false, hasReunion = false, hasEvenementPeriodique = false, hasAnniversaire = false;
        for (Event e : loadedEvents) {
            if (e instanceof RendezVous && e.getTitre().valeur().equals("RDV Test")) hasRendezVous = true;
            if (e instanceof Reunion && e.getTitre().valeur().equals("Réunion Test")) hasReunion = true;
            if (e instanceof EvenementPeriodique && e.getTitre().valeur().equals("Événement Périodique")) hasEvenementPeriodique = true;
            if (e instanceof Anniversaire && e.getTitre().valeur().equals("Anniversaire Test")) hasAnniversaire = true;
        }

        assertTrue(hasRendezVous, "RendezVous manquant");
        assertTrue(hasReunion, "Reunion manquante");
        assertTrue(hasEvenementPeriodique, "EvenementPeriodique manquant");
        assertTrue(hasAnniversaire, "Anniversaire manquant");
    }

    @Test
    @DisplayName("Test de gestion des erreurs - fichier inexistant")
    void testFileNotFoundError() {
        // Quand: on essaie de charger un fichier qui n'existe pas
        File nonExistentFile = tempDir.resolve("non-existent.json").toFile();

        // Alors: une IOException devrait être levée
        assertThrows(IOException.class, () ->
                        serializer.loadCalendarFromFile(calendar, nonExistentFile.getPath()),
                "Une exception aurait dû être levée pour un fichier inexistant");
    }

    @Test
    @DisplayName("Test de sérialisation avec des caractères spéciaux")
    void testSpecialCharactersSerialization() throws Exception {
        // Étant donné: un événement avec des caractères spéciaux dans le titre
        RendezVous rdvWithSpecialChars = new RendezVous(
                new TitreEvenement("RDV avec caractères spéciaux: é, è, à, ç, ù, €, @, #"),
                new DateEvenement(baseDateTime.plusDays(5)),
                new HeureDebut(15, 30),
                new DureeEvenement(45),
                new ProprietaireEvenement("user-special")
        );

        // Quand: on sérialise et désérialise cet événement
        String json = serializer.serializeEvents(List.of(rdvWithSpecialChars));
        List<Event> deserializedEvents = serializer.deserializeEvents(json);

        // Alors: le titre devrait être conservé avec ses caractères spéciaux
        assertEquals(1, deserializedEvents.size(), "Un seul événement aurait dû être désérialisé");
        assertEquals("RDV avec caractères spéciaux: é, è, à, ç, ù, €, @, #",
                deserializedEvents.get(0).getTitre().valeur(),
                "Le titre avec caractères spéciaux n'est pas conservé correctement");
    }

    @Test
    @DisplayName("Test de désérialisation d'une liste vide")
    void testEmptyListDeserialization() throws Exception {
        // Étant donné: un JSON représentant une liste vide
        String emptyJson = "[]";

        // Quand: on désérialise ce JSON
        List<Event> deserializedEvents = serializer.deserializeEvents(emptyJson);

        // Alors: la liste résultante devrait être vide mais pas null
        assertNotNull(deserializedEvents, "La liste désérialisée ne devrait pas être null");
        assertTrue(deserializedEvents.isEmpty(), "La liste désérialisée devrait être vide");
    }

    @Test
    @DisplayName("Test de cycle complet: sérialisation, désérialisation, resérialisation")
    void testFullCycle() throws Exception {
        // Étant donné: une liste d'événements
        List<Event> originalEvents = testEvents;

        // Quand: on sérialise, désérialise, puis resérialise
        String json1 = serializer.serializeEvents(originalEvents);
        List<Event> deserializedEvents = serializer.deserializeEvents(json1);
        String json2 = serializer.serializeEvents(deserializedEvents);

        // Alors: les deux JSON devraient contenir les mêmes informations essentielles
        // Note: Une comparaison exacte de chaînes peut échouer en raison de l'ordre des champs
        // donc nous vérifions plutôt la présence d'informations clés

        List<String> essentialStrings = Arrays.asList(
                "RDV Test", "Réunion Test", "Événement Périodique", "Anniversaire Test",
                "RendezVous", "Reunion", "EvenementPeriodique", "Anniversaire",
                "user1", "user2", "user3", "Salle A", "Sophie"
        );

        for (String str : essentialStrings) {
            assertTrue(json1.contains(str), "Premier JSON manquant: " + str);
            assertTrue(json2.contains(str), "Second JSON manquant: " + str);
        }

        // Vérifier que les deux JSON ont approximativement la même longueur
        // (permet une petite variation due à l'ordre des champs)
        int lengthDiff = Math.abs(json1.length() - json2.length());
        assertTrue(lengthDiff < 100, "Différence de taille trop importante entre les deux JSON");
    }

    @Test
    @DisplayName("Test de robustesse avec des ID d'événements")
    void testEventIdPreservation() throws Exception {
        // Étant donné: des événements avec des ID spécifiques
        List<EventId> originalIds = testEvents.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        // Quand: on sérialise puis désérialise
        String json = serializer.serializeEvents(testEvents);
        List<Event> deserializedEvents = serializer.deserializeEvents(json);

        // Alors: les ID des événements devraient être préservés
        List<EventId> deserializedIds = deserializedEvents.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        // Vérifier que chaque ID d'origine est présent dans les ID désérialisés
        for (EventId originalId : originalIds) {
            boolean found = false;
            for (EventId deserializedId : deserializedIds) {
                if (deserializedId.valeur().equals(originalId.valeur())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "ID d'événement non préservé après désérialisation: " + originalId.valeur());
        }
    }

    @Test
    @DisplayName("Test de sérialisation indépendante de chaque type d'événement")
    void testSerializeEventTypes() throws Exception {
        // Pour chaque type d'événement, tester la sérialisation individuellement

        // RendezVous
        RendezVous rdv = (RendezVous) testEvents.stream()
                .filter(e -> e instanceof RendezVous)
                .findFirst()
                .orElseThrow();

        String rdvJson = serializer.serializeEvents(List.of(rdv));
        assertTrue(rdvJson.contains("RendezVous"), "JSON de RendezVous incorrect");

        // Reunion
        Reunion reunion = (Reunion) testEvents.stream()
                .filter(e -> e instanceof Reunion)
                .findFirst()
                .orElseThrow();

        String reunionJson = serializer.serializeEvents(List.of(reunion));
        assertTrue(reunionJson.contains("Reunion"), "JSON de Reunion incorrect");

        // EvenementPeriodique
        EvenementPeriodique periodique = (EvenementPeriodique) testEvents.stream()
                .filter(e -> e instanceof EvenementPeriodique)
                .findFirst()
                .orElseThrow();

        String periodiqueJson = serializer.serializeEvents(List.of(periodique));
        assertTrue(periodiqueJson.contains("EvenementPeriodique"), "JSON d'EvenementPeriodique incorrect");

        // Anniversaire
        Anniversaire anniversaire = (Anniversaire) testEvents.stream()
                .filter(e -> e instanceof Anniversaire)
                .findFirst()
                .orElseThrow();

        String anniversaireJson = serializer.serializeEvents(List.of(anniversaire));
        assertTrue(anniversaireJson.contains("Anniversaire"), "JSON d'Anniversaire incorrect");
    }

    @Test
    @DisplayName("Test du format des dates")
    void testDateTimeFormat() throws Exception {
        // Étant donné: un événement à une date/heure précise
        LocalDateTime specificDateTime = LocalDateTime.of(2025, 12, 31, 23, 59);
        RendezVous rdvWithSpecificDate = new RendezVous(
                new TitreEvenement("RDV Fin d'année"),
                new DateEvenement(specificDateTime),
                new HeureDebut(23, 59),
                new DureeEvenement(60),
                new ProprietaireEvenement("user-special")
        );

        // Quand: on sérialise et désérialise cet événement
        String json = serializer.serializeEvents(List.of(rdvWithSpecificDate));
        List<Event> deserializedEvents = serializer.deserializeEvents(json);

        // Alors: la date et l'heure devraient être préservées
        assertEquals(1, deserializedEvents.size(), "Un seul événement aurait dû être désérialisé");
        Event deserializedEvent = deserializedEvents.get(0);

        assertEquals(2025, deserializedEvent.getDateDebut().valeur().getYear(),
                "L'année n'est pas préservée");
        assertEquals(12, deserializedEvent.getDateDebut().valeur().getMonthValue(),
                "Le mois n'est pas préservé");
        assertEquals(31, deserializedEvent.getDateDebut().valeur().getDayOfMonth(),
                "Le jour n'est pas préservé");
        assertEquals(23, deserializedEvent.getHeureDebut().heure(),
                "L'heure n'est pas préservée");
        assertEquals(59, deserializedEvent.getHeureDebut().minute(),
                "Les minutes ne sont pas préservées");
    }

    @Test
    @DisplayName("Test de la méthode clear du calendrier")
    void testClearCalendar() throws IOException {
        // Étant donné: un calendrier avec des événements
        List<Event> eventsInCalendar = calendar.eventsDansPeriode(
                new Periode(baseDateTime.minusDays(1), baseDateTime.plusDays(10)));

        // Vérifier que tous les types d'événements sont présents
        boolean hasRendezVous = false, hasReunion = false, hasEvenementPeriodique = false, hasAnniversaire = false;
        for (Event e : eventsInCalendar) {
            if (e instanceof RendezVous && e.getTitre().valeur().equals("RDV Test")) hasRendezVous = true;
            if (e instanceof Reunion && e.getTitre().valeur().equals("Réunion Test")) hasReunion = true;
            if (e instanceof EvenementPeriodique && e.getTitre().valeur().equals("Événement Périodique")) hasEvenementPeriodique = true;
            if (e instanceof Anniversaire && e.getTitre().valeur().equals("Anniversaire Test")) hasAnniversaire = true;
        }

        assertTrue(hasRendezVous, "RendezVous manquant");
        assertTrue(hasReunion, "Reunion manquante");
        assertTrue(hasEvenementPeriodique, "EvenementPeriodique manquant");
        assertTrue(hasAnniversaire, "Anniversaire manquant");

        // Quand: on vide le calendrier
        calendar.clearEvents();

        // Alors: le calendrier devrait être vide
        List<Event> emptyEvents = calendar.eventsDansPeriode(
                new Periode(baseDateTime.minusDays(1), baseDateTime.plusDays(10)));
        assertTrue(emptyEvents.isEmpty(), "Le calendrier devrait être vide après clear()");
    }

    @Test
    @DisplayName("Test de reconstitution complète du calendrier")
    void testCalendarReconstruction() throws IOException {
        // Étant donné: un calendrier sauvegardé
        File tempFile = tempDir.resolve("calendar-reconstruct.json").toFile();
        serializer.saveCalendarToFile(calendar, tempFile.getPath());

        // Quand: on vide le calendrier puis on le recharge
        calendar.clearEvents();
        serializer.loadCalendarFromFile(calendar, tempFile.getPath());

        // Alors: le calendrier devrait contenir tous les types d'événements originaux
        List<Event> reloadedEvents = calendar.eventsDansPeriode(
                new Periode(baseDateTime.minusDays(1), baseDateTime.plusDays(10)));

        // Vérifier que tous les types d'événements sont présents
        boolean hasRendezVous = false, hasReunion = false, hasEvenementPeriodique = false, hasAnniversaire = false;
        for (Event e : reloadedEvents) {
            if (e instanceof RendezVous && e.getTitre().valeur().equals("RDV Test")) hasRendezVous = true;
            if (e instanceof Reunion && e.getTitre().valeur().equals("Réunion Test")) hasReunion = true;
            if (e instanceof EvenementPeriodique && e.getTitre().valeur().equals("Événement Périodique")) hasEvenementPeriodique = true;
            if (e instanceof Anniversaire && e.getTitre().valeur().equals("Anniversaire Test")) hasAnniversaire = true;
        }

        assertTrue(hasRendezVous, "RendezVous manquant");
        assertTrue(hasReunion, "Reunion manquante");
        assertTrue(hasEvenementPeriodique, "EvenementPeriodique manquant");
        assertTrue(hasAnniversaire, "Anniversaire manquant");
    }

    @Test
    @DisplayName("Test de sérialisation des attributs spécifiques de tous les types d'événements")
    void testSpecificAttributesAllEventTypes() throws Exception {
        // 1. Test de Reunion
        testReunionSpecificAttributes();

        // 2. Test d'EvenementPeriodique
        testEvenementPeriodiqueSpecificAttributes();

        // 3. Test d'Anniversaire
        testAnniversaireSpecificAttributes();
    }

    @Test
    @DisplayName("Test de sérialisation des attributs spécifiques de Reunion")
    void testReunionSpecificAttributes() throws Exception {
        // Étant donné: une réunion avec des participants et un lieu spécifiques
        String[] participantsAttendus = {"Alice", "Bob", "Charlie"};
        String lieuAttendu = "Salle Spéciale";

        Reunion reunion = new Reunion(
                new TitreEvenement("Réunion Spéciale"),
                new DateEvenement(baseDateTime),
                new HeureDebut(14, 0),
                new DureeEvenement(120),
                new LieuEvenement(lieuAttendu),
                new ProprietaireEvenement("user-test"),
                new Participants(participantsAttendus)
        );

        // Quand: on sérialise puis désérialise cette réunion
        String json = serializer.serializeEvents(List.of(reunion));
        List<Event> deserializedEvents = serializer.deserializeEvents(json);

        // Alors: la réunion désérialisée devrait contenir les mêmes participants et lieu
        assertEquals(1, deserializedEvents.size(), "Un seul événement devrait être désérialisé");
        assertTrue(deserializedEvents.get(0) instanceof Reunion, "L'événement désérialisé devrait être une Reunion");

        Reunion deserializedReunion = (Reunion) deserializedEvents.get(0);
        assertEquals(lieuAttendu, deserializedReunion.getLieu().valeur(),
                "Le lieu de la réunion n'est pas préservé correctement");

        String[] participantsDeserialized = deserializedReunion.getParticipants().noms();
        assertEquals(participantsAttendus.length, participantsDeserialized.length,
                "Le nombre de participants ne correspond pas");

        // Vérifier chaque participant individuellement
        for (String expectedParticipant : participantsAttendus) {
            boolean found = false;
            for (String actualParticipant : participantsDeserialized) {
                if (expectedParticipant.equals(actualParticipant)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Participant non trouvé après désérialisation: " + expectedParticipant);
        }

        // Afficher le JSON pour inspection visuelle
        System.out.println("JSON de la réunion:");
        System.out.println(json);
    }

    @Test
    @DisplayName("Test de sérialisation des attributs spécifiques d'EvenementPeriodique")
    void testEvenementPeriodiqueSpecificAttributes() throws Exception {
        // Étant donné: un événement périodique avec une fréquence spécifique
        int frequenceAttendue = 14; // tous les 14 jours

        EvenementPeriodique evenementPeriodique = new EvenementPeriodique(
                new TitreEvenement("Événement Récurrent"),
                new DateEvenement(baseDateTime),
                new HeureDebut(9, 30),
                new DureeEvenement(45),
                frequenceAttendue,
                new ProprietaireEvenement("user-test")
        );

        // Quand: on sérialise puis désérialise cet événement
        String json = serializer.serializeEvents(List.of(evenementPeriodique));
        List<Event> deserializedEvents = serializer.deserializeEvents(json);

        // Alors: l'événement désérialisé devrait avoir la même fréquence
        assertEquals(1, deserializedEvents.size(), "Un seul événement devrait être désérialisé");
        assertTrue(deserializedEvents.get(0) instanceof EvenementPeriodique,
                "L'événement désérialisé devrait être un EvenementPeriodique");

        EvenementPeriodique deserializedEP = (EvenementPeriodique) deserializedEvents.get(0);
        assertEquals(frequenceAttendue, deserializedEP.getFrequenceJours(),
                "La fréquence n'est pas préservée correctement");

        // Vérifier aussi qu'une occurrence future générée est correcte
        LocalDateTime expectedNextOccurrence = baseDateTime.plusDays(frequenceAttendue);
        Iterator<Event> occurrences = deserializedEP.occurrences(
                new Periode(baseDateTime.plusDays(frequenceAttendue - 1),
                        baseDateTime.plusDays(frequenceAttendue + 1)));

        assertTrue(occurrences.hasNext(), "L'événement périodique devrait générer une occurrence");
        Event nextOccurrence = occurrences.next();
        assertEquals(expectedNextOccurrence.getDayOfMonth(),
                nextOccurrence.getStartDateTime().getDayOfMonth(),
                "Le jour de la prochaine occurrence n'est pas correct");

        // Afficher le JSON pour inspection visuelle
        System.out.println("JSON de l'événement périodique:");
        System.out.println(json);
    }

    @Test
    @DisplayName("Test de sérialisation des attributs spécifiques d'Anniversaire")
    void testAnniversaireSpecificAttributes() throws Exception {
        // Étant donné: un anniversaire avec une personne fêtée spécifique
        String personneFeteeAttendue = "Marcel Dupont";

        Anniversaire anniversaire = new Anniversaire(
                new TitreEvenement("Anniversaire Spécial"),
                new DateEvenement(baseDateTime),
                new HeureDebut(19, 0),
                new DureeEvenement(180),
                new ProprietaireEvenement("user-test"),
                personneFeteeAttendue
        );

        // Quand: on sérialise puis désérialise cet anniversaire
        String json = serializer.serializeEvents(List.of(anniversaire));
        List<Event> deserializedEvents = serializer.deserializeEvents(json);

        // Alors: l'anniversaire désérialisé devrait avoir la même personne fêtée
        assertEquals(1, deserializedEvents.size(), "Un seul événement devrait être désérialisé");
        assertTrue(deserializedEvents.get(0) instanceof Anniversaire,
                "L'événement désérialisé devrait être un Anniversaire");

        Anniversaire deserializedAnniv = (Anniversaire) deserializedEvents.get(0);
        assertEquals(personneFeteeAttendue, deserializedAnniv.getPersonneFetee(),
                "La personne fêtée n'est pas préservée correctement");

        // Afficher le JSON pour inspection visuelle
        System.out.println("JSON de l'anniversaire:");
        System.out.println(json);
    }

    @Test
    @DisplayName("Test de sérialisation des attributs communs à tous les événements")
    void testCommonAttributes() throws Exception {
        // Étant donné: un événement simple (RDV)
        TitreEvenement titreAttendu = new TitreEvenement("RDV Important");
        DateEvenement dateAttendue = new DateEvenement(baseDateTime);
        HeureDebut heureAttendue = new HeureDebut(15, 45);
        DureeEvenement dureeAttendue = new DureeEvenement(30);
        ProprietaireEvenement proprietaireAttendu = new ProprietaireEvenement("SkyPurl");

        RendezVous rdv = new RendezVous(
                titreAttendu,
                dateAttendue,
                heureAttendue,
                dureeAttendue,
                proprietaireAttendu
        );

        // Quand: on sérialise puis désérialise cet événement
        String json = serializer.serializeEvents(List.of(rdv));
        List<Event> deserializedEvents = serializer.deserializeEvents(json);

        // Alors: l'événement désérialisé devrait avoir les mêmes attributs communs
        assertEquals(1, deserializedEvents.size(), "Un seul événement devrait être désérialisé");
        Event deserializedEvent = deserializedEvents.get(0);

        assertEquals(titreAttendu.valeur(), deserializedEvent.getTitre().valeur(),
                "Le titre n'est pas préservé correctement");

        assertEquals(dateAttendue.valeur().getYear(), deserializedEvent.getDateDebut().valeur().getYear(),
                "L'année de la date n'est pas préservée");
        assertEquals(dateAttendue.valeur().getMonthValue(), deserializedEvent.getDateDebut().valeur().getMonthValue(),
                "Le mois de la date n'est pas préservé");
        assertEquals(dateAttendue.valeur().getDayOfMonth(), deserializedEvent.getDateDebut().valeur().getDayOfMonth(),
                "Le jour de la date n'est pas préservé");

        assertEquals(heureAttendue.heure(), deserializedEvent.getHeureDebut().heure(),
                "L'heure n'est pas préservée");
        assertEquals(heureAttendue.minute(), deserializedEvent.getHeureDebut().minute(),
                "Les minutes ne sont pas préservées");

        assertEquals(dureeAttendue.valeur(), deserializedEvent.getDuree().valeur(),
                "La durée n'est pas préservée");

        assertEquals(proprietaireAttendu.valeur(), deserializedEvent.getProprietaire().valeur(),
                "Le propriétaire n'est pas préservé");

        // Vérifier que l'ID est préservé
        assertNotNull(rdv.getId(), "L'ID de l'événement original ne devrait pas être null");
        assertNotNull(deserializedEvent.getId(), "L'ID de l'événement désérialisé ne devrait pas être null");
        assertEquals(rdv.getId().valeur(), deserializedEvent.getId().valeur(),
                "L'ID n'est pas préservé correctement");

        // Afficher le JSON pour inspection visuelle
        System.out.println("JSON du rendez-vous avec tous les attributs communs:");
        System.out.println(json);
    }

    @Test
    @DisplayName("Test de sérialisation et désérialisation de tous les types avec une seule opération")
    void testAllEventTypesSerialization() throws Exception {
        // Étant donné: un exemplaire de chaque type d'événement
        List<Event> allTypeEvents = new ArrayList<>();

        // RendezVous
        allTypeEvents.add(new RendezVous(
                new TitreEvenement("RDV Test"),
                new DateEvenement(baseDateTime),
                new HeureDebut(10, 0),
                new DureeEvenement(60),
                new ProprietaireEvenement("SkyPurl")
        ));

        // Reunion
        allTypeEvents.add(new Reunion(
                new TitreEvenement("Réunion Test"),
                new DateEvenement(baseDateTime.plusHours(2)),
                new HeureDebut(12, 0),
                new DureeEvenement(90),
                new LieuEvenement("Salle A"),
                new ProprietaireEvenement("SkyPurl"),
                new Participants(new String[]{"Alice", "Bob"})
        ));

        // EvenementPeriodique
        allTypeEvents.add(new EvenementPeriodique(
                new TitreEvenement("Événement Périodique"),
                new DateEvenement(baseDateTime.plusDays(1)),
                new HeureDebut(14, 30),
                new DureeEvenement(45),
                7, // Tous les 7 jours
                new ProprietaireEvenement("SkyPurl")
        ));

        // Anniversaire
        allTypeEvents.add(new Anniversaire(
                new TitreEvenement("Anniversaire Test"),
                new DateEvenement(baseDateTime.plusDays(3)),
                new HeureDebut(18, 0),
                new DureeEvenement(180),
                new ProprietaireEvenement("SkyPurl"),
                "Sophie"
        ));

        // Quand: on sérialise puis désérialise tous ces événements
        String json = serializer.serializeEvents(allTypeEvents);
        List<Event> deserializedEvents = serializer.deserializeEvents(json);

        // Alors: tous les événements devraient être correctement désérialisés
        assertEquals(allTypeEvents.size(), deserializedEvents.size(),
                "Le nombre d'événements devrait être préservé");

        // Vérifier chaque type
        int rdvCount = 0, reunionCount = 0, periodiqueCount = 0, anniversaireCount = 0;

        for (Event e : deserializedEvents) {
            if (e instanceof RendezVous) rdvCount++;
            else if (e instanceof Reunion) reunionCount++;
            else if (e instanceof EvenementPeriodique) periodiqueCount++;
            else if (e instanceof Anniversaire) anniversaireCount++;
        }

        assertEquals(1, rdvCount, "Il devrait y avoir exactement 1 RendezVous");
        assertEquals(1, reunionCount, "Il devrait y avoir exactement 1 Reunion");
        assertEquals(1, periodiqueCount, "Il devrait y avoir exactement 1 EvenementPeriodique");
        assertEquals(1, anniversaireCount, "Il devrait y avoir exactement 1 Anniversaire");

        // Afficher le JSON pour inspection visuelle
        System.out.println("JSON avec tous les types d'événements:");
        System.out.println(json);
    }


}