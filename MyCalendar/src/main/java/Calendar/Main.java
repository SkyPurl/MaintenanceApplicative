package Calendar;

import Calendar.vo.*;
import Calendar.Events.*;
import Calendar.User.*;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class Main {
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
            // Boucle principale
            eventRegistry.displayEventMenu();
            String displayKey = eventRegistry.getDisplayEventsKey();
            String quitKey = eventRegistry.getQuitKey();

            Stream.generate(() -> {
                        System.out.print("\nVotre choix: ");
                        return scanner.nextLine();
                    })
                    .takeWhile(choice -> !choice.equals(quitKey))
                    .forEach(choice -> {
                        try {
                            if (choice.equals(displayKey)) {
                                calendar.afficherEvenements();
                            } else {
                                BiFunction<Scanner, User, Event> creator = eventRegistry.getCreator(choice);
                                if (creator != null) {
                                    Event event = creator.apply(scanner, authenticatedUser);
                                    calendar.ajouterEvenement(event);
                                    System.out.println("Événement ajouté avec succès!");
                                    eventRegistry.displayEventMenu(); // Réaffiche le menu après chaque action
                                } else {
                                    System.out.println("Option invalide, veuillez réessayer.");
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("Erreur: " + e.getMessage());
                        }
                    });

            System.out.println("Au revoir!");

        } catch (Exception e) {
            System.out.println("Erreur inattendue: " + e.getMessage());
        }
    }
}