package Calendar;

import Calendar.vo.*;
import Calendar.Events.*;
import Calendar.User.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.BiFunction;

/**
 * Classe qui centralise l'enregistrement et la création des différents types d'événements
 */
public class EventRegistry {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Map<String, BiFunction<Scanner, User, Event>> eventCreators = new TreeMap<>();
    private final Map<String, String> eventDescriptions = new TreeMap<>();
    private int nextKey = 1;

    public EventRegistry() {
        // Enregistrement des créateurs d'événements
        registerEventType("Créer un rendez-vous", this::creerRendezVous);
        registerEventType("Créer une réunion", this::creerReunion);
        registerEventType("Créer un événement périodique", this::creerEvenementPeriodique);
        registerEventType("Créer un anniversaire", this::creerAnniversaire);
    }

    /**
     * Enregistre un nouveau type d'événement avec une clé automatique
     */
    public void registerEventType(String description, BiFunction<Scanner, User, Event> creator) {
        String key = String.valueOf(nextKey++);
        eventCreators.put(key, creator);
        eventDescriptions.put(key, description);
    }

    /**
     * Obtient la fonction de création associée à une clé
     */
    public BiFunction<Scanner, User, Event> getCreator(String key) {
        return eventCreators.get(key);
    }

    /**
     * Obtient les descriptions des événements
     */
    public Map<String, String> getEventDescriptions() {
        return Collections.unmodifiableMap(eventDescriptions);
    }

    /**
     * Obtient la clé pour l'option "Afficher les événements"
     */
    public String getDisplayEventsKey() {
        return String.valueOf(nextKey);
    }

    /**
     * Obtient la clé pour l'option "Supprimer un événement"
     */
    public String getDeleteKey() {
        return String.valueOf(nextKey + 1);
    }

    /**
     * Obtient la clé pour l'option "Sauvegarder le calendrier"
     */
    public String getSaveKey() {
        return String.valueOf(nextKey + 2);
    }

    /**
     * Obtient la clé pour l'option "Charger le calendrier"
     */
    public String getLoadKey() {
        return String.valueOf(nextKey + 3);
    }

    /**
     * Obtient la clé pour l'option "Quitter"
     */
    public String getQuitKey() {
        return String.valueOf(nextKey + 4);
    }

    /**
     * Affiche le menu des types d'événements disponibles
     */
    public void displayEventMenu() {
        System.out.println("\n=== Gestionnaire d'Événements ===");
        eventDescriptions.forEach((key, description) ->
                System.out.println(key + ": " + description));
        System.out.println(getDisplayEventsKey() + ": Afficher les événements");
        System.out.println(getDeleteKey() + ": Supprimer un événement");
        System.out.println(getSaveKey() + ": Sauvegarder le calendrier");
        System.out.println(getLoadKey() + ": Charger le calendrier");
        System.out.println(getQuitKey() + ": Quitter");
    }

    /**
     * Recueille les informations de base communes à tous les événements
     */
    private EventBasicInfo collectBasicEventInfo(Scanner scanner) {
        System.out.print("Titre : ");
        TitreEvenement titre = new TitreEvenement(scanner.nextLine());

        LocalDateTime ldt;
        try {
            // On demande tout en une seule fois, ex: 2025-06-01 14:30
            System.out.print("Date et heure (YYYY-MM-DD HH:MM) : ");
            String dateTimeStr = scanner.nextLine();

            // On parse en LocalDateTime
            ldt = LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Format de date invalide. Utilisez le format YYYY-MM-DD HH:MM");
        }

        // On crée DateEvenement et HeureDebut en séparant les champs de ldt
        DateEvenement dateEvt = new DateEvenement(ldt);
        HeureDebut heureEvt = new HeureDebut(ldt.getHour(), ldt.getMinute());

        int d;
        try {
            System.out.print("Durée (minutes) : ");
            d = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La durée doit être un nombre entier");
        }

        return new EventBasicInfo(
                titre,
                dateEvt,
                heureEvt,
                new DureeEvenement(d)
        );
    }

    private RendezVous creerRendezVous(Scanner scanner, User user) {
        System.out.println("\n--- Création d'un rendez-vous ---");
        EventBasicInfo info = collectBasicEventInfo(scanner);

        return new RendezVous(
                info.titre(),
                info.date(),
                info.heureDebut(),
                info.duree(),
                new ProprietaireEvenement(user.username())
        );
    }

    private Reunion creerReunion(Scanner scanner, User user) {
        System.out.println("\n--- Création d'une réunion ---");
        EventBasicInfo info = collectBasicEventInfo(scanner);

        System.out.print("Lieu: ");
        String lieu = scanner.nextLine();

        System.out.print("Participants (séparés par des virgules): ");
        String participantsStr = scanner.nextLine();
        String[] participantsArray = participantsStr.trim().split("\\s*,\\s*");

        return new Reunion(
                info.titre(),
                info.date(),
                info.heureDebut(),
                info.duree(),
                new LieuEvenement(lieu),
                new ProprietaireEvenement(user.username()),
                new Participants(participantsArray)
        );
    }

    private EvenementPeriodique creerEvenementPeriodique(Scanner scanner, User user) {
        System.out.println("\n--- Création d'un événement périodique ---");
        EventBasicInfo info = collectBasicEventInfo(scanner);

        int periode;
        try {
            System.out.print("Période (jours): ");
            periode = Integer.parseInt(scanner.nextLine());
            if (periode <= 0) {
                throw new IllegalArgumentException("La période doit être positive");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La période doit être un nombre entier");
        }

        return new EvenementPeriodique(
                info.titre(),
                info.date(),
                info.heureDebut(),
                info.duree(),
                periode,
                new ProprietaireEvenement(user.username())
        );
    }

    private Anniversaire creerAnniversaire(Scanner scanner, User user) {
        System.out.println("\n--- Création d'un anniversaire ---");
        EventBasicInfo info = collectBasicEventInfo(scanner);

        System.out.print("Personne fêtée: ");
        String personneFetee = scanner.nextLine();

        return new Anniversaire(
                info.titre(),
                info.date(),
                info.heureDebut(),
                info.duree(),
                new ProprietaireEvenement(user.username()),
                personneFetee
        );
    }


    public record EventBasicInfo(
            TitreEvenement titre,
            DateEvenement date,
            HeureDebut heureDebut,
            DureeEvenement duree
    ) {}
}