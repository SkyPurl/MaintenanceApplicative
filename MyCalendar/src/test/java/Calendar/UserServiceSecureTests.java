package Calendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import Calendar.User.*;
import Calendar.vo.User;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceSecureTests {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(new BasicPasswordEncoder());
    }

    @Test
    void testInscriptionSecure() {
        userService.inscrire("Bob", "secret1");
        User user = userService.trouverParUsername("Bob");
        assertNotNull(user);
        // Vérifie qu’on ne stocke pas le mdp en clair
        assertNotEquals("secret1", user.hashedPassword());
    }

    @Test
    void testConnexionOk() {
        userService.inscrire("Alice", "pwd123");
        User user = userService.seConnecter("Alice", "pwd123");
        assertNotNull(user);
        assertEquals("Alice", user.username());
    }

    @Test
    void testConnexionKo() {
        userService.inscrire("Alice", "pwd123");
        assertThrows(IllegalArgumentException.class,
                () -> userService.seConnecter("Alice", "wrongpwd"));
    }
}
