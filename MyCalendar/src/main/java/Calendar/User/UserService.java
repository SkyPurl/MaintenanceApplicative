package Calendar.User;

import java.util.HashMap;
import java.util.Map;
import Calendar.vo.User;

public class UserService {

    private final Map<String, User> users = new HashMap<>();
    private final PasswordEncoder encoder;

    public UserService(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public User inscrire(String username, String rawPassword) {
        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Identifiant déjà utilisé.");
        }
        String hashed = encoder.encode(rawPassword);
        User user = new User(username, hashed);
        users.put(username, user);
        return user;
    }

    public User seConnecter(String username, String rawPassword) {
        User user = users.get(username);
        if (user == null) {
            throw new IllegalArgumentException("Identifiant inexistant.");
        }
        if (!encoder.matches(rawPassword, user.hashedPassword())) {
            throw new IllegalArgumentException("Mot de passe incorrect.");
        }
        return user;
    }

    public User trouverParUsername(String username) {
        return users.get(username);
    }
}

