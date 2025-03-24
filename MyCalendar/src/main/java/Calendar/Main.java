package Calendar;

import Calendar.vo.*;
import Calendar.Events.*;
import Calendar.User.*;
import Calendar.Serialization.JsonCalendarSerializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class Main {
    private static final String DEFAULT_CALENDAR_FILE = "calendar.json";

    public static void main(String[] args) {
        CalendarManager calendar = new CalendarManager();
        Scanner scanner = new Scanner(System.in);

        // Initialisation des services
        PasswordEncoder encoder = new BasicPasswordEncoder();
        UserService userService = new UserService(encoder);
        AuthenticationService authService = new AuthenticationService(userService, scanner);
        EventRegistry eventRegistry = new EventRegistry();

        // Authentification
        User authenticatedUser = authService.authenticate();

        System.out.println("\nBienvenue " + authenticatedUser.username() + "!");

        try {
            // Tentative de chargement du calendrier existant
            try {
                calendar.loadFromJson(DEFAULT_CALENDAR_FILE);
                System.out.println("Calendrier chargé depuis " + DEFAULT_CALENDAR_FILE);
            } catch (IOException e) {
                System.out.println("Aucun calendrier existant trouvé, création d'un nouveau calendrier.");
            }

            // Boucle principale
            displayMainMenu(eventRegistry);
            String displayKey = eventRegistry.getDisplayEventsKey();
            String quitKey = eventRegistry.getQuitKey();
            String saveKey = eventRegistry.getSaveKey();
            String loadKey = eventRegistry.getLoadKey();
            String deleteKey = eventRegistry.getDeleteKey();

            Stream.generate(() -> {
                        System.out.print("\nVotre choix: ");
                        return scanner.nextLine();
                    })
                    .takeWhile(choice -> !choice.equals(quitKey))
                    .forEach(choice -> {
                        try {
                            if (choice.equals(displayKey)) {
                                calendar.afficherEvenements();
                            } else if (choice.equals(saveKey)) {
                                saveCalendar(scanner, calendar);
                            } else if (choice.equals(loadKey)) {
                                loadCalendar(scanner, calendar);
                            } else if (choice.equals(deleteKey)) {
                                deleteEvent(scanner, calendar);
                            } else {
                                BiFunction<Scanner, User, Event> creator = eventRegistry.getCreator(choice);
                                if (creator != null) {
                                    Event event = creator.apply(scanner, authenticatedUser);
                                    calendar.ajouterEvenement(event);
                                    System.out.println("Événement ajouté avec succès!");
                                } else {
                                    System.out.println("Option invalide, veuillez réessayer.");
                                }
                            }

                            displayMainMenu(eventRegistry);  // Réaffiche le menu après chaque action

                        } catch (Exception e) {
                            System.out.println("Erreur: " + e.getMessage());
                            if (e instanceof CalendarManager.ConflitEvenementException) {
                                CalendarManager.ConflitEvenementException conflit =
                                        (CalendarManager.ConflitEvenementException)e;
                                System.out.println("Événements en conflit:");
                                for (Event evt : conflit.getEvenementsEnConflit()) {
                                    System.out.println(" - " + evt.description());
                                }
                            }
                        }
                    });

            // Sauvegarde automatique avant de quitter
            try {
                calendar.saveToJson(DEFAULT_CALENDAR_FILE);
                System.out.println("Calendrier sauvegardé dans " + DEFAULT_CALENDAR_FILE);
            } catch (IOException e) {
                System.out.println("Erreur lors de la sauvegarde: " + e.getMessage());
            }

            System.out.println("Au revoir!");

        } catch (Exception e) {
            System.out.println("Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayMainMenu(EventRegistry eventRegistry) {
        System.out.println("\n=== Gestionnaire d'Événements ===");
        eventRegistry.getEventDescriptions().forEach((key, description) ->
                System.out.println(key + ": " + description));
        System.out.println(eventRegistry.getDisplayEventsKey() + ": Afficher les événements");
        System.out.println(eventRegistry.getDeleteKey() + ": Supprimer un événement");
        System.out.println(eventRegistry.getSaveKey() + ": Sauvegarder le calendrier");
        System.out.println(eventRegistry.getLoadKey() + ": Charger le calendrier");
        System.out.println(eventRegistry.getQuitKey() + ": Quitter");

        // Afficher la date et l'heure actuelles
        System.out.println("\nDate et heure actuelles: " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    private static void saveCalendar(Scanner scanner, CalendarManager calendar) {
        System.out.print("Nom du fichier (ou laissez vide pour 'calendar.json'): ");
        String fileName = scanner.nextLine().trim();

        if (fileName.isEmpty()) {
            fileName = DEFAULT_CALENDAR_FILE;
        }

        try {
            calendar.saveToJson(fileName);
            System.out.println("Calendrier sauvegardé avec succès dans " + fileName);
        } catch (IOException e) {
            System.out.println("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }

    private static void loadCalendar(Scanner scanner, CalendarManager calendar) {
        System.out.print("Nom du fichier à charger (ou laissez vide pour 'calendar.json'): ");
        String fileName = scanner.nextLine().trim();

        if (fileName.isEmpty()) {
            fileName = DEFAULT_CALENDAR_FILE;
        }

        try {
            calendar.loadFromJson(fileName);
            System.out.println("Calendrier chargé avec succès depuis " + fileName);
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement: " + e.getMessage());
        }
    }

    private static void deleteEvent(Scanner scanner, CalendarManager calendar) {
        System.out.println("\n--- Supprimer un événement ---");
        calendar.afficherEvenements();

        System.out.print("Entrez l'ID de l'événement à supprimer (ou 'q' pour annuler): ");
        String idStr = scanner.nextLine();

        if ("q".equalsIgnoreCase(idStr) || idStr.isBlank()) {
            System.out.println("Opération annulée.");
            return;
        }

        EventId id = new EventId(idStr);
        boolean supprime = calendar.supprimerEvenement(id);

        if (supprime) {
            System.out.println("Événement supprimé avec succès!");
        } else {
            System.out.println("Aucun événement trouvé avec cet ID.");
        }
    }
}