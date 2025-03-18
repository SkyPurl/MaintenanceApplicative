package Calendar;

import Calendar.User.*;
import Calendar.vo.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Supplier;

class AuthenticationServiceTest {

    private TestUserService testUserService;
    private AuthenticationService authService;
    private ByteArrayOutputStream outputStreamCaptor;
    private Scanner testScanner;

    private final PrintStream standardOut = System.out;
    private final InputStream standardIn = System.in;

    @BeforeEach
    void setUp() {
        // Capturer la sortie standard pour vérifier les messages affichés
        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        // Simuler une entrée utilisateur vide par défaut
        setTestInput("");

        // Création d'une implémentation de test de UserService
        testUserService = new TestUserService();
    }

    @AfterEach
    void tearDown() {
        // Restaurer System.out et System.in après chaque test
        System.setOut(standardOut);
        System.setIn(standardIn);
    }

    private void setTestInput(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);
        testScanner = new Scanner(System.in);
        authService = new AuthenticationService(testUserService, testScanner);
    }

    @Test
    void testDisplayAuthMenu() {
        // Exécuter la méthode à tester
        authService.displayAuthMenu();

        // Vérifier le contenu affiché
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("=== Système d'Authentification ==="));
        assertTrue(output.contains("1: S'inscrire"));
        assertTrue(output.contains("2: Se connecter"));
        assertTrue(output.contains("Votre choix:"));
    }

    @Test
    void testGetAuthOption() {
        // Vérifier que les options d'authentification sont correctement enregistrées
        Supplier<Optional<User>> inscriptionOption = authService.getAuthOption("1");
        Supplier<Optional<User>> connexionOption = authService.getAuthOption("2");

        assertNotNull(inscriptionOption, "L'option d'inscription ne devrait pas être null");
        assertNotNull(connexionOption, "L'option de connexion ne devrait pas être null");
    }

    @Test
    void testAuthOptionsAvailable() {
        // Vérifier que les deux options attendues sont disponibles
        assertNotNull(authService.getAuthOption("1"), "Option d'inscription devrait être disponible");
        assertNotNull(authService.getAuthOption("2"), "Option de connexion devrait être disponible");
        assertNull(authService.getAuthOption("3"), "Option 3 ne devrait pas exister");
    }

    @Test
    void testAuthenticate_SuccessfulLogin() {
        // Simuler l'entrée utilisateur pour une connexion réussie
        setTestInput("2\ntestUser\ntestPassword\n");

        // Exécuter l'authentification
        User authenticatedUser = authService.authenticate();

        // Vérifier que l'utilisateur a bien été authentifié
        assertNotNull(authenticatedUser);
        assertEquals("testUser", authenticatedUser.username());

        // Vérifier que les messages appropriés ont été affichés
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Connexion réussie!"));
    }

    @Test
    void testAuthenticate_FailedLoginThenSuccess() {
        // Simuler une entrée utilisateur avec d'abord une connexion échouée, puis réussie
        setTestInput("2\ninvalidUser\nanyPassword\n2\ntestUser\ntestPassword\n");

        // Exécuter l'authentification
        User authenticatedUser = authService.authenticate();

        // Vérifier que l'utilisateur a été authentifié après la deuxième tentative
        assertNotNull(authenticatedUser);
        assertEquals("testUser", authenticatedUser.username());

        // Vérifier que les messages appropriés ont été affichés
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Erreur: Identifiant inexistant."));
        assertTrue(output.contains("Connexion réussie!"));
    }

    @Test
    void testAuthenticate_RegisterThenLogin() {
        // Simuler une entrée utilisateur avec inscription puis connexion
        setTestInput("1\nnewUser\nnewPassword\n2\nnewUser\nnewPassword\n");

        // Exécuter l'authentification
        User authenticatedUser = authService.authenticate();

        // Vérifier que l'utilisateur a été authentifié
        assertNotNull(authenticatedUser);
        assertEquals("newUser", authenticatedUser.username());

        // Vérifier que les messages appropriés ont été affichés
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Inscription réussie!"));
        assertTrue(output.contains("Veuillez maintenant vous connecter."));
        assertTrue(output.contains("Connexion réussie!"));
    }

    @Test
    void testAuthenticate_InvalidOption() {
        // Simuler une entrée utilisateur avec d'abord une option invalide, puis une connexion valide
        setTestInput("9\n2\ntestUser\ntestPassword\n");

        // Exécuter l'authentification
        User authenticatedUser = authService.authenticate();

        // Vérifier que l'utilisateur a été authentifié après la correction
        assertNotNull(authenticatedUser);
        assertEquals("testUser", authenticatedUser.username());

        // Vérifier que les messages appropriés ont été affichés
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Option invalide, veuillez réessayer."));
        assertTrue(output.contains("Connexion réussie!"));
    }

    /**
     * Implémentation personnalisée de UserService pour les tests
     */
    private static class TestUserService extends UserService {

        private final Map<String, User> testUsers = new HashMap<>();

        public TestUserService() {
            super(new NoOpPasswordEncoder());

            // Pré-remplir avec un utilisateur de test
            testUsers.put("testUser", new User("testUser", "testPassword"));
        }

        @Override
        public User inscrire(String username, String rawPassword) {
            if (testUsers.containsKey(username)) {
                throw new IllegalArgumentException("Identifiant déjà utilisé.");
            }
            User user = new User(username, rawPassword);
            testUsers.put(username, user);
            return user;
        }

        @Override
        public User seConnecter(String username, String rawPassword) {
            User user = testUsers.get(username);
            if (user == null) {
                throw new IllegalArgumentException("Identifiant inexistant.");
            }
            // Dans les tests on vérifie simplement que le mot de passe correspond
            if (!user.hashedPassword().equals(rawPassword)) {
                throw new IllegalArgumentException("Mot de passe incorrect.");
            }
            return user;
        }

        @Override
        public User trouverParUsername(String username) {
            return testUsers.get(username);
        }
    }

    /**
     * Encodeur de mot de passe qui ne fait rien, pour les tests
     */
    private static class NoOpPasswordEncoder implements PasswordEncoder {
        @Override
        public String encode(CharSequence rawPassword) {
            return rawPassword.toString();
        }

        @Override
        public boolean matches(CharSequence rawPassword, String encodedPassword) {
            return rawPassword.toString().equals(encodedPassword);
        }
    }
}