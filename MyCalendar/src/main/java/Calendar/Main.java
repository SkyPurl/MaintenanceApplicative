package Calendar;

import Calendar.vo.*;
import Calendar.Events.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        CalendarManager calendar = new CalendarManager();
        Scanner scanner = new Scanner(System.in);

        Map<String, Function<Scanner, Event>> eventConstructors = Map.of(
                "1", Main::creerRendezVous,
                "2", Main::creerReunion,
                "3", Main::creerEvenementPeriodique,
                "4", s -> {
                    calendar.afficherEvenements();
                    return null;
                },
                "5", s -> {
                    throw new RuntimeException("Quitter");
                }
        );

        System.out.println("=== Gestionnaire d'Événements ===");
        Stream.generate(scanner::nextLine)
                .map(eventConstructors::get)
                .map(constructor -> {
                    try {
                        return constructor.apply(scanner);
                    } catch (NullPointerException e) {
                        System.out.println("Option invalide, veuillez réessayer.");
                        return null;
                    }
                })
                .forEach(event -> {
                    try {
                        calendar.ajouterEvenement(event);
                    } catch (NullPointerException ignored) {
                    }
                });
    }

    private static RendezVous creerRendezVous(Scanner scanner) {
        return new RendezVous(
                new TitreEvenement(scanner.nextLine()),
                new DateEvenement(LocalDateTime.parse(scanner.nextLine())),
                new HeureDebut(scanner.nextInt(), scanner.nextInt()),
                new DureeEvenement(scanner.nextInt())
        );
    }

    private static Reunion creerReunion(Scanner scanner) {
        scanner.nextLine(); // Consommer la ligne restante
        return new Reunion(
                new TitreEvenement(scanner.nextLine()),
                new DateEvenement(LocalDateTime.parse(scanner.nextLine())),
                new HeureDebut(scanner.nextInt(), scanner.nextInt()),
                new DureeEvenement(scanner.nextInt()),
                new LieuEvenement(scanner.nextLine()),
                new Participants(scanner.nextLine().split(","))
        );
    }

    private static EvenementPeriodique creerEvenementPeriodique(Scanner scanner) {
        return new EvenementPeriodique(
                new TitreEvenement(scanner.nextLine()),
                new DateEvenement(LocalDateTime.parse(scanner.nextLine())),
                new HeureDebut(scanner.nextInt(), scanner.nextInt()),
                new DureeEvenement(scanner.nextInt()),
                scanner.nextInt()
        );
    }
}