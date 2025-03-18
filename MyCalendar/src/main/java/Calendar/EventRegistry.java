package Calendar;

import Calendar.vo.*;
import Calendar.Events.*;
import Calendar.User.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.TreeMap;

/**
 * Classe qui centralise l'enregistrement et la création des différents types d'événements
 */
public class EventRegistry {
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
     * Obtient la clé pour l'option "Afficher les événements"
     */
    public String getDisplayEventsKey() {
        return String.valueOf(nextKey);
    }

    /**
     * Obtient la clé pour l'option "Quitter"
     */
    public String getQuitKey() {
        return String.valueOf(nextKey + 1);
    }

    /**
     * Affiche le menu des types d'événements disponibles
     */
    public void displayEventMenu() {
        System.out.println("\n=== Gestionnaire d'Événements ===");
        eventDescriptions.forEach((key, description) ->
                System.out.println(key + ": " + description));
        System.out.println(getDisplayEventsKey() + ": Afficher les événements");
        System.out.println(getQuitKey() + ": Quitter");
    }

    /**
     * Recueille les informations de base communes à tous les événements
     */
    private EventBasicInfo collectBasicEventInfo(Scanner scanner) {
        System.out.print("Titre : ");
        TitreEvenement titre = new TitreEvenement(scanner.nextLine());

        // On demande tout en une seule fois, ex: 2025-06-01 14:30
        System.out.print("Date et heure (YYYY-MM-DD HH:MM) : ");
        String dateTimeStr = scanner.nextLine(); // ex: "2025-06-01 14:30"

        // On parse en LocalDateTime
        LocalDateTime ldt = LocalDateTime.parse(dateTimeStr.replace(" ", "T"));
        // Exemple: "2025-06-01 14:30" -> "2025-06-01T14:30"

        // On crée DateEvenement et HeureDebut en séparant les champs de ldt
        DateEvenement dateEvt = new DateEvenement(ldt);
        HeureDebut heureEvt = new HeureDebut(ldt.getHour(), ldt.getMinute());

        System.out.print("Durée (minutes) : ");
        int d = scanner.nextInt();
        scanner.nextLine(); // Consommer fin de ligne

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

        return new Reunion(
                info.titre(),
                info.date(),
                info.heureDebut(),
                info.duree(),
                new LieuEvenement(lieu),
                new ProprietaireEvenement(user.username()),
                new Participants(participantsStr.split(","))
        );
    }

    private EvenementPeriodique creerEvenementPeriodique(Scanner scanner, User user) {
        System.out.println("\n--- Création d'un événement périodique ---");
        EventBasicInfo info = collectBasicEventInfo(scanner);

        System.out.print("Période (jours): ");
        int periode = Integer.parseInt(scanner.nextLine());

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

    /**
     * Classe pour stocker les informations de base communes à tous les événements
     */
    public record EventBasicInfo(
            TitreEvenement titre,
            DateEvenement date,
            HeureDebut heureDebut,
            DureeEvenement duree
    ) {}
}