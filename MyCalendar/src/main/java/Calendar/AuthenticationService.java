package Calendar;

import Calendar.User.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.Optional;
import Calendar.vo.User;

/**
 * Classe qui gère l'authentification des utilisateurs
 */
public class AuthenticationService {
    private final UserService userService;
    private final Map<String, Supplier<Optional<User>>> authOptions = new HashMap<>();
    private final Scanner scanner;

    public AuthenticationService(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;

        // Enregistrement des options d'authentification
        authOptions.put("1", this::sInscrire);
        authOptions.put("2", this::seConnecter);
    }

    /**
     * Affiche le menu d'authentification
     */
    public void displayAuthMenu() {
        System.out.println("=== Système d'Authentification ===");
        System.out.println("1: S'inscrire");
        System.out.println("2: Se connecter");
        System.out.print("Votre choix: ");
    }

    /**
     * Obtient la fonction d'authentification associée à une clé
     */
    public Supplier<Optional<User>> getAuthOption(String key) {
        return authOptions.get(key);
    }

    /**
     * Gère le processus d'authentification complet
     */
    public User authenticate() {
        while (true) {
            displayAuthMenu();
            String choice = scanner.nextLine();

            Supplier<Optional<User>> authAction = getAuthOption(choice);
            if (authAction == null) {
                System.out.println("Option invalide, veuillez réessayer.");
                continue;
            }

            Optional<User> userResult = authAction.get();
            if (userResult.isPresent()) {
                return userResult.get();
            }
            // Si nous arrivons ici, l'action d'authentification a renvoyé Optional.empty()
            // ce qui signifie revenir au menu principal
        }
    }

    private Optional<User> sInscrire() {
        System.out.println("\n--- Inscription ---");
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();

        try {
            User user = userService.inscrire(username, password);
            System.out.println("Inscription réussie!");
            System.out.println("Veuillez maintenant vous connecter.");
            return Optional.empty(); // Retourne empty pour revenir au menu principal
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur: " + e.getMessage());
            return Optional.empty(); // Retourne empty pour revenir au menu principal
        }
    }

    private Optional<User> seConnecter() {
        System.out.println("\n--- Connexion ---");
        System.out.print("Nom d'utilisateur: ");
        String username = scanner.nextLine();
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();

        try {
            User user = userService.seConnecter(username, password);
            System.out.println("Connexion réussie!");
            return Optional.of(user); // Retourne l'utilisateur connecté
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur: " + e.getMessage());
            return Optional.empty(); // Retourne empty pour revenir au menu principal
        }
    }
}